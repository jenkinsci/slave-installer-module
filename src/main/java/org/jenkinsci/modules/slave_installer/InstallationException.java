package org.jenkinsci.modules.slave_installer;

/**
 * Represents an anticipated failure during {@link SlaveInstaller#install(org.jenkinsci.modules.slave_installer.LaunchConfiguration, org.jenkinsci.modules.slave_installer.Prompter)}
 * that does not require stack trace printing.
 *
 * <p>
 * The calling code shouldn't report the stack trace to the user.
 *
 * @author Kohsuke Kawaguchi
 */
public class InstallationException extends Exception {
    public InstallationException(String message) {
        super(message);
    }

    public InstallationException(String message, Throwable cause) {
        super(message, cause);
    }
}
