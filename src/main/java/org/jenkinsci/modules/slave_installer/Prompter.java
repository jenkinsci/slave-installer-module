package org.jenkinsci.modules.slave_installer;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class Prompter {

    /**
     * Asks a question to the user and asks them to provide a value.
     *
     * @param question
     *      The string to be shown to the user. Don't add ':', '?' and so on in the end.
     */
    public abstract String prompt(String question, String defaultValue) throws InterruptedException;

    public final String prompt(String question) {
        return prompt(question,null);
    }

    public abstract String promptPassword(String question) throws InterruptedException;
}
