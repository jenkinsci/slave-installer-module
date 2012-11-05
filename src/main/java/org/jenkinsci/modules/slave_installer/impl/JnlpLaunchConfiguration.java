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
public class JnlpLaunchConfiguration extends LaunchConfiguration {
    private final URL jnlpUrl;

    public JnlpLaunchConfiguration(URL jnlpUrl) {
        this.jnlpUrl = jnlpUrl;
    }

    public File getJarFile() throws IOException {
        return Which.jarFile(Channel.class);
    }

    @Override
    public ArgumentListBuilder buildRunnerArguments() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("-jnlpUrl").add(jnlpUrl);
        return args;
    }
}
