package org.jenkinsci.modules.slave_installer;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.slaves.SlaveComputer;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class SlaveInstallerFactory implements ExtensionPoint {
    /**
     * If this factory is capable of creating a {@link SlaveInstaller} for the platform
     * of the given {@link SlaveComputer}, create one and return its {@link SlaveInstaller}.
     *
     * @param c
     *      Slave that's online.
     */
    public abstract SlaveInstaller createIfApplicable(SlaveComputer c) throws IOException, InterruptedException;

    public static SlaveInstaller createFor(SlaveComputer c) throws InterruptedException {
        for (SlaveInstallerFactory f : all()) {
            try {
                SlaveInstaller si = f.createIfApplicable(c);
                if (si!=null)   return si;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, f+" has failed on "+c,e);
                // try the next one
            }
        }
        return null;
    }

    /**
     * All the registered {@link SlaveInstallerFactory}s.
     */
    public static ExtensionList<SlaveInstallerFactory> all() {
        return Jenkins.getInstance().getExtensionList(SlaveInstallerFactory.class);
    }

    private static final Logger LOGGER = Logger.getLogger(SlaveInstallerFactory.class.getName());
}