package org.vcell.ui;

import org.eclipse.core.runtime.IPlatformRunnable;

import cbit.util.document.VCDocument;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.server.ClientServerInfo;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IPlatformRunnable {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	public Object run(Object args) throws Exception {
		VCDocument initialDocument = null;
		ClientServerInfo csInfo = ClientServerInfo.createLocalServerInfo("schaff", "me&jan");
		try {
			VCellClient.startClient(initialDocument, csInfo);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of VCellApplication");
			exception.printStackTrace(System.out);
		}
		// TODO: this is to wait for a long time ... is better way?
		for (int i = 0; i < 100000; i++) {
			Thread.sleep(100000);
		}
		return IPlatformRunnable.EXIT_OK;
	}
}
