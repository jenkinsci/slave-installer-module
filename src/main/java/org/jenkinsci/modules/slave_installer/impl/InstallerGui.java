package org.jenkinsci.modules.slave_installer.impl;

import hudson.Util;
import hudson.remoting.Callable;
import hudson.remoting.Engine;
import hudson.remoting.jnlp.MainDialog;
import hudson.remoting.jnlp.MainMenu;
import org.jenkinsci.modules.slave_installer.InstallationException;
import org.jenkinsci.modules.slave_installer.LaunchConfiguration;
import org.jenkinsci.modules.slave_installer.SlaveInstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import static javax.swing.JOptionPane.*;

/**
 * @author Kohsuke Kawaguchi
 */
public class InstallerGui implements Callable<Void,IOException> {
    private final SlaveInstaller installer;

    private transient Engine engine;
    private transient MainDialog dialog;

    public InstallerGui(SlaveInstaller installer) {
        this.installer = installer;
    }

    /**
     * To be executed on each slave JVM.
     */
    public Void call() throws IOException {
        dialog = MainDialog.get();
        if(dialog==null)     return null;    // can't find the main window. Maybe not running with GUI

        // capture the engine
        engine = Engine.current();
        if(engine==null)     return null;    // Ditto

        final URL jnlpUrl = new URL(engine.getHudsonUrl(),"computer/"+ Util.rawEncode(engine.slaveName)+"/slave-agent.jnlp");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainMenu mainMenu = dialog.getMainMenu();
                JMenu m = mainMenu.getFileMenu();
                JMenuItem menu = new JMenuItem(installer.getDisplayName());
                menu.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            // final confirmation before taking an action
                            int r = JOptionPane.showConfirmDialog(dialog,
                                    installer.getConfirmationText(),
                                    installer.getDisplayName(), OK_CANCEL_OPTION);
                            if(r!=JOptionPane.OK_OPTION)    return;

                            LaunchConfiguration config = LAUNCH_CONFIG;
                            if (config==null)
                                config = new JnlpLaunchConfiguration(jnlpUrl);
                            dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            installer.install(config,new SwingPrompter());
                        } catch (InstallationException t) {
                            JOptionPane.showMessageDialog(dialog,t.getMessage(),"Error", ERROR_MESSAGE);
                        } catch (Exception t) {// this runs as a JNLP app, so if we let an exception go, we'll never find out why it failed
                            StringWriter sw = new StringWriter();
                            t.printStackTrace(new PrintWriter(sw));
                            JOptionPane.showMessageDialog(dialog,sw.toString(),"Error", ERROR_MESSAGE);
                        }
                    }
                });
                m.add(menu);
                mainMenu.commit();
            }
        });

        return null;
    }

    private static final long serialVersionUID = 1L;

    /**
     * {@link LaunchConfiguration} that controls what process will be run under the service wrapper
     * when the slave installation happens through GUI.
     *
     * Conceptually, this can be thought of as a recovered memory of how this slave JVM has been started.
     * This is "recovered", because we can't really reliably tell from within the slave itself, but
     * nonetheless it's a piece of information scoped to the slave JVM. Hence singleton.
     */
    public static LaunchConfiguration LAUNCH_CONFIG;
}
