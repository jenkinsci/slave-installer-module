package org.jenkinsci.modules.slave_installer.impl;

import org.jenkinsci.modules.slave_installer.Prompter;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Kohsuke Kawaguchi
 */
public class SwingPrompter extends Prompter {
    @Override
    public String prompt(final String question, final String defaultValue) throws InterruptedException {
        try {
            final String[] value= new String[1];
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    value[0] = JOptionPane.showInputDialog(null,question,defaultValue);
                }
            });
            return value[0];
        } catch (InvocationTargetException e) {
            throw new Error(e);
        }
    }

    @Override
    public String promptPassword(final String question) throws InterruptedException {
        try {
            final String[] value= new String[1];
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
        // Password in JOptionPane?
                    value[0] = JOptionPane.showInputDialog(null,question);
                }
            });
            return value[0];
        } catch (InvocationTargetException e) {
            throw new Error(e);
        }
    }
}
