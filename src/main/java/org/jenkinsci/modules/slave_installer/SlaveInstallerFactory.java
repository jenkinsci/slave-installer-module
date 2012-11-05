package org.jenkinsci.modules.slave_installer;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.slaves.SlaveComputer;
import jenkins.model.Jenkins;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class SlaveInstallerFactory implements ExtensionPoint {
    /**
     * If this factory is capable of creating a {@link SlaveInstaller} for the platform
     * of the given {@link SlaveComputer}, create one and return its {@link SlaveInstaller}.
     */
    public abstract SlaveInstaller createIfApplicable(SlaveComputer c);

    public static SlaveInstaller createFor(SlaveComputer c) {
        for (SlaveInstallerFactory f : all()) {
            SlaveInstaller si = f.createFor(c);
            if (si!=null)   return si;
        }
        return null;
    }

    /**
     * All the registered {@link SlaveInstallerFactory}s.
     */
    public static ExtensionList<SlaveInstallerFactory> all() {
        return Jenkins.getInstance().getExtensionList(SlaveInstallerFactory.class);
    }
}

/*
    On JNLPLauncher launched from "start" button, we'll use the JNLP LaunchConfiguration.


*/