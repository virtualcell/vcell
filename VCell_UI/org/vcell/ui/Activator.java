package org.vcell.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import cbit.util.VCDocument;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.test.VCellClientTest;

public class Activator implements BundleActivator {

	public Activator() {
		// TODO Auto-generated constructor stub
	}

	public void start(BundleContext context) throws Exception {
        System.out.println("Starting org.vcell.ui.Activator");
		VCDocument initialDocument = null;
		ClientServerInfo csInfo = ClientServerInfo.createLocalServerInfo("schaff", "me&jan");
		try {
			VCellClient.startClient(initialDocument, csInfo);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of VCellApplication");
			exception.printStackTrace(System.out);
		}
	}

	public void stop(BundleContext context) throws Exception {
	}

}
