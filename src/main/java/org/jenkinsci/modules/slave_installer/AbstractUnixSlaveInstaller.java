package org.jenkinsci.modules.slave_installer;

import hudson.util.jna.GNUCLibrary;
import org.jvnet.libpam.impl.CLibrary.passwd;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractUnixSlaveInstaller extends SlaveInstaller {

    protected void reportError(String msg) {
        System.err.println("Error: "+msg);
    }

    /**
     * Returns the user name of the effective UID that the current process is running.
     */
    protected String getCurrentUnixUserName() {
        passwd pwd = GNUCLibrary.LIBC.getpwuid(GNUCLibrary.LIBC.geteuid());
        return pwd.pw_name;
    }
}
