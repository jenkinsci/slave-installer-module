package org.jenkinsci.modules.slave_installer.impl;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.ComputerListener;
import hudson.slaves.SlaveComputer;
import org.jenkinsci.modules.slave_installer.SlaveInstaller;
import org.jenkinsci.modules.slave_installer.SlaveInstallerFactory;

import java.io.IOException;

/**
 * Inserts {@link InstallerGui} to a slave that has JNLP GUI.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class ComputerListenerImpl extends ComputerListener {
//    @Inject
//    InstanceIdentity id;

    @Override
    public void onOnline(Computer c, TaskListener listener) throws IOException, InterruptedException {
//        RSAPublicKey key = id.getPublic();
//        String instanceId = Util.getDigestOf(new String(Base64.encodeBase64(key.getEncoded()))).substring(0,8);

        if (c instanceof SlaveComputer) {
            SlaveComputer sc = (SlaveComputer) c;
            SlaveInstaller si = SlaveInstallerFactory.createFor(sc);
            if (si!=null)
                c.getChannel().call(new InstallerGui(si, sc));
        }
    }
}
