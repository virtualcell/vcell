package org.vcell.vmicro.workflow;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.server.ClientServerInfo;

public class DisplayBioModel extends Task {
	
	//
	// inputs
	//
	public final DataInput<BioModel> bioModel;
	
	//
	// outputs
	//
	public final DataHolder<Boolean> displayed;
	

	public DisplayBioModel(String id){
		super(id);
		bioModel = new DataInput<BioModel>(BioModel.class,"bioModel", this);
		displayed = new DataHolder<Boolean>(Boolean.class,"displayed",this);
		addInput(bioModel);
		addOutput(displayed);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		displayBioModel(bioModel.getData());
		displayed.setData(true);
	}
	
	public static void displayBioModel(final BioModel bioModel){
		new Thread() {
			public void run(){
				ClientServerInfo clientServerInfo = ClientServerInfo.createLocalServerInfo("schaff", new DigestedPassword("abc"));
				VCellClient vcellClient = VCellClient.startClient(bioModel, clientServerInfo);
			}
		}.start();
	}
	
}
