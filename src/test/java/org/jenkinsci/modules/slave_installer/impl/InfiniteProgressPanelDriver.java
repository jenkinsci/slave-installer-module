package org.jenkinsci.modules.slave_installer.impl;

import hudson.remoting.jnlp.GUI;
import hudson.remoting.jnlp.MainDialog;
import hudson.remoting.jnlp.MainMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Test driver code to visually see the effect of {@link InfiniteProgressPanel}.
 *
 * @author Kohsuke Kawaguchi
 */
public class InfiniteProgressPanelDriver {
    public static void main(String[] args) {
        final InfiniteProgressPanel glass = new InfiniteProgressPanel();

        GUI.setUILookAndFeel();
        MainDialog d = new MainDialog();
        d.setGlassPane(glass);
        d.setVisible(true);

        MainMenu mainMenu = d.getMainMenu();
        JMenu m = mainMenu.getFileMenu();
        JMenuItem menu = new JMenuItem("Test");

        menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                glass.start();
                new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        Thread.sleep(5000);
                        return null;
                    }

                    @Override
                    protected void done() {
                        glass.stop();
                    }
                }.execute();
            }
        });
        m.add(menu);
        mainMenu.commit();
    }
}
