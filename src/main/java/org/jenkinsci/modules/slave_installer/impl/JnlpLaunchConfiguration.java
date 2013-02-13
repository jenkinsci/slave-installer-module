package org.jenkinsci.modules.slave_installer.impl;

import hudson.remoting.Channel;
import hudson.remoting.Which;
import hudson.slaves.JNLPLauncher;
import hudson.util.ArgumentListBuilder;
import org.jenkinsci.modules.slave_installer.LaunchConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * {@link LaunchConfiguration} for {@link JNLPLauncher}.
 *
 * @author Kohsuke Kawaguchi
 */
class JnlpLaunchConfiguration extends LaunchConfiguration {
    private final URL jnlpUrl;
    private final File storage;
    private final String jnlpMac;

    JnlpLaunchConfiguration(URL jnlpUrl, File storage, String jnlpMac) {
        this.jnlpUrl = jnlpUrl;
        this.storage = storage;
        this.jnlpMac = jnlpMac;
    }

    @Override
    public File getStorage() throws IOException {
        return storage;
    }

    public File getJarFile() throws IOException {
        return Which.jarFile(Channel.class);
    }

    @Override
    public ArgumentListBuilder buildRunnerArguments() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("-jnlpUrl").add(jnlpUrl);
        args.add("-secret").add(jnlpMac);
        return args;
    }
}
