package org.jenkinsci.modules.slave_installer.impl;

import org.jenkinsci.modules.slave_installer.Prompter;

import javax.swing.*;

/**
 * @author Kohsuke Kawaguchi
 */
public class SwingPrompter extends Prompter {
    @Override
    public String prompt(String question, String defaultValue) {
        return JOptionPane.showInputDialog(null,question,defaultValue);
    }

    @Override
    public String promptPassword(String question) {
        // Password in JOptionPane?
        return JOptionPane.showInputDialog(null,question);
    }
}
