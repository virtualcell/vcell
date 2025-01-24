package org.vcell.vmicro.op.display;

import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.server.ClientServerInfo;

public class DisplayBioModelOp {
	
	public static void displayBioModel(final BioModel bioModel){
		new Thread() {
			public void run(){
				ClientServerInfo clientServerInfo = ClientServerInfo.createLocalServerInfo("schaff");
				VCellClient vCellClient = new VCellClient( null, null);
				vCellClient.startClient(bioModel, clientServerInfo);
			}
		}.start();
	}
	
}
