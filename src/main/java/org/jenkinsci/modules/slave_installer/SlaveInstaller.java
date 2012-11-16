package org.jenkinsci.modules.slave_installer;

import org.jenkinsci.modules.slave_installer.impl.Messages;
import org.jvnet.localizer.Localizable;

import java.io.IOException;
import java.io.Serializable;

/**
 * Represents platform-specific installation process of a slave agent to the service wrapper.
 *
 * @author Kohsuke Kawaguchi
 * @see SlaveInstallerFactory
 */
public abstract class SlaveInstaller implements Serializable {
    /**
     * Short one-line human readable name of what this slave installer does.
     * This can be used as a GUI menu item, text in the button, etc, to
     * have the user initiate the {@link #install(LaunchConfiguration,Prompter)} call.
     */
    public String getDisplayName() {
        return Messages.InstallerGui_DisplayName();
    }

    /**
     * Possibly multi-line text that explains in more details what {@link #install(LaunchConfiguration,Prompter)}
     * would do. This message should be used by the caller to confirm the user's intention
     * before actually executing {@link #install(LaunchConfiguration,Prompter)}.
     *
     * The text needs to be wrapped around 72 chars. No HTML is allowed.
     */
    public abstract Localizable getConfirmationText();

    /**
     * Installs the slave as a service, quit the current JVM, and let the newly installed service
     * spawns and connects as a slave.
     *
     * The JVM termination is tied to the installation as some service wrappers (such as launchd)
     * cannot distinguish the service installation and launch.
     */
    public abstract void install(LaunchConfiguration params, Prompter prompter) throws InstallationException, IOException, InterruptedException;
}
