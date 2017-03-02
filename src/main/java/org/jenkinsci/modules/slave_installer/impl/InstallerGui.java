package org.jenkinsci.modules.slave_installer.impl;

import hudson.FilePath;
import hudson.Util;
import hudson.remoting.Callable;
import hudson.remoting.Channel;
import hudson.remoting.Engine;
import hudson.remoting.jnlp.MainDialog;
import hudson.remoting.jnlp.MainMenu;
import hudson.slaves.SlaveComputer;
import org.jenkinsci.modules.slave_installer.InstallationException;
import org.jenkinsci.modules.slave_installer.LaunchConfiguration;
import org.jenkinsci.modules.slave_installer.SlaveInstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import static javax.swing.JOptionPane.*;
import jenkins.security.MasterToSlaveCallable;

/**
 * When executed via {@link Channel#call(Callable)} on an agent,
 * adds a GUI menu to install the agent as a platform-specific service.
 *
 * @author Kohsuke Kawaguchi
 */
public class InstallerGui extends MasterToSlaveCallable<Void,IOException> {
    private final SlaveInstaller installer;
    private final FilePath slaveRoot;
    private final String jnlpMac;

    protected transient Engine engine;
    protected transient MainDialog dialog;

    public InstallerGui(SlaveInstaller installer, SlaveComputer sc) {
        this.installer = installer;
        this.slaveRoot = sc.getNode().getRootPath();
        jnlpMac = sc.getJnlpMac();
    }

    public InstallerGui(SlaveInstaller installer, FilePath slaveRoot, String jnlpMac) {
        this.installer = installer;
        this.slaveRoot = slaveRoot;
        this.jnlpMac = jnlpMac;
    }

    /**
     * To be executed on each agent JVM.
     */
    public Void call() throws IOException {
        dialog = MainDialog.get();
        if(dialog==null)     return null;    // can't find the main window. Maybe not running with GUI

        // capture the engine
        engine = Engine.current();
        if(engine==null)     return null;    // Ditto

        final URL jarUrl = new URL(engine.getHudsonUrl(),"jnlpJars/slave.jar");
        final URL jnlpUrl = getJnlpUrl();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainMenu mainMenu = dialog.getMainMenu();
                JMenu m = mainMenu.getFileMenu();
                JMenuItem menu = new JMenuItem(installer.getDisplayName());

                final InfiniteProgressPanel glassPane = new InfiniteProgressPanel();
                dialog.setGlassPane(glassPane);

                menu.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // final confirmation before taking an action
                        int r = JOptionPane.showConfirmDialog(dialog,
                                installer.getConfirmationText(),
                                installer.getDisplayName(), OK_CANCEL_OPTION);
                        if (r != JOptionPane.OK_OPTION) return;

                        glassPane.start();

                        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                        Thread t = new Thread("installer") {
                            @Override
                            public void run() {
                                try {
                                    LaunchConfiguration config = LAUNCH_CONFIG;
                                    if (config == null) {
                                        assert !slaveRoot.isRemote();
                                        config = new JnlpLaunchConfiguration(jarUrl, jnlpUrl, new File(slaveRoot.getRemote()), jnlpMac);
                                    }
                                    installer.install(config, new SwingPrompter());
                                } catch (final InstallationException t) {
                                    error(t.getMessage());
                                } catch (final Throwable t) {// this runs as a JNLP app, so if we let an exception go, we'll never find out why it failed
                                    StringWriter sw = new StringWriter();
                                    t.printStackTrace(new PrintWriter(sw));
                                    error(sw.toString());
                                } finally {
                                    // disengage the busy dialog
                                    glassPane.stop();
                                    dialog.setCursor(null);
                                }
                            }

                            private void error(final String msg) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        JOptionPane.showMessageDialog(dialog, msg, "Error", ERROR_MESSAGE);
                                    }
                                });
                            }
                        };
                        t.start();
                    }
                });
                m.add(menu);
                mainMenu.commit();
            }
        });

        return null;
    }

    protected URL getJnlpUrl() throws MalformedURLException {
        return new URL(engine.getHudsonUrl(),"computer/"+ Util.rawEncode(engine.slaveName)+"/slave-agent.jnlp");
    }

    private static final long serialVersionUID = 1L;

    /**
     * {@link LaunchConfiguration} that controls what process will be run under the service wrapper
     * when the agent installation happens through GUI.
     *
     * Conceptually, this can be thought of as a recovered memory of how this agent JVM has been started.
     * This is "recovered", because we can't really reliably tell from within the agent itself, but
     * nonetheless it's a piece of information scoped to the agent JVM. Hence singleton.
     */
    // XXX what is this for? no one ever writes to it
    public static LaunchConfiguration LAUNCH_CONFIG;
}
