package org.jenkinsci.modules.slave_installer;

import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Encapsulates what to launch from the service wrapper.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class LaunchConfiguration {
    /**
     * To install a slave on a system, sometimes the installer needs to place some files (including
     * configuration files, binaries, etc.)
     *
     * <p>
     * Sometimes, the platform has directories designed for services by convention (such as /System/Library/LaunchDaemon
     * for launchd or /etc/default/jenkins-slave on Linux), but in other OSes there's no such conventional location
     * (for example on Windows.)
     *
     * <p>
     * Because of this, the caller will provide a directory that allows installer to place some files. The installer
     * is encourage to use platform dependent conventional locations over this storage area. Typically,
     * this is the remote file system root of the slave.
     */
    public abstract File getStorage() throws IOException;

    /**
     * Decides the jar file to be launched from the service wrapper.
     *
     * The file will be copied into another safe location before getting registered..
     */
    public abstract File getJarFile() throws IOException;

    /**
     * Decides the arguments to the jar file to be started by the service wrapper.
     */
    public abstract ArgumentListBuilder buildRunnerArguments();
}
