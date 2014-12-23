package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

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
	public final DataOutput<Boolean> displayed;
	

	public DisplayBioModel(String id){
		super(id);
		bioModel = new DataInput<BioModel>(BioModel.class,"bioModel", this);
		displayed = new DataOutput<Boolean>(Boolean.class,"displayed",this);
		addInput(bioModel);
		addOutput(displayed);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		displayBioModel(context.getData(bioModel));
		context.setData(displayed,true);
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
