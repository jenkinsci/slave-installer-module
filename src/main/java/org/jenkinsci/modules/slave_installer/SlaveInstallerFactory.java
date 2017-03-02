package org.jenkinsci.modules.slave_installer;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.remoting.Channel;
import hudson.slaves.SlaveComputer;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extension point for installer to put a Java program under a service manager
 * (such as launchd, Windows Service Control Manager, or upstart.)
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class SlaveInstallerFactory implements ExtensionPoint {
    /**
     * If this factory is capable of creating a {@link SlaveInstaller} for the platform
     * of the given {@link SlaveComputer}, create one and return its {@link SlaveInstaller}.
     *
     * @param c
     *      Agent that's online.
     */
    // this method was the original abstraction in 1.0, which can be now implemented on top of createIfApplicable(Channel)
    public SlaveInstaller createIfApplicable(SlaveComputer c) throws IOException, InterruptedException {
        return createIfApplicable(c.getChannel());
    }

    /**
     * If this factory is capable of creating a {@link SlaveInstaller} for the system
     * that the other end of the given channel runs in, create one and return a {@link SlaveInstaller}.
     *
     * @since 1.1
     */
    public /*abstract*/ SlaveInstaller createIfApplicable(Channel c) throws IOException, InterruptedException {
        // to be implemented by subtypes
        return null;
    }

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

    public static SlaveInstaller createFor(Channel c) throws InterruptedException {
        for (SlaveInstallerFactory f : all()) {
            try {
                SlaveInstaller si = f.createIfApplicable(c);
                if (si!=null)   return si;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, f+" has failed on "+c.getName(),e);
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