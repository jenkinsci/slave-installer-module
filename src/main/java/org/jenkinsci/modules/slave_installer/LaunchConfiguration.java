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
