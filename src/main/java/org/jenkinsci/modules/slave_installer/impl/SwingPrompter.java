package org.jenkinsci.modules.slave_installer.impl;

import org.jenkinsci.modules.slave_installer.Prompter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
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
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    JLabel label = new JLabel(question);
                    JPasswordField pass = new JPasswordField(10);
                    panel.add(label);
                    panel.add(pass);
                    pass.addKeyListener(new KeyAdapter() {
                        // accept ENTER as a window closer
                        @Override
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                                Container c;
                                for (c=panel; !(c instanceof JDialog); c=c.getParent()) ;
                                JDialog dialog = (JDialog) c;
                                dialog.dispatchEvent(new WindowEvent( dialog, WindowEvent.WINDOW_CLOSING ));
                            }
                        }
                    });
                    String[] options = new String[]{"OK", "Cancel"};
                    int option = JOptionPane.showOptionDialog(null, panel, null,
                                             JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                                             null, options, null);
                    if(option == 0)
                        value[0] = new String(pass.getPassword());
                }
            });
            return value[0];
        } catch (InvocationTargetException e) {
            throw new Error(e);
        }
    }
}
