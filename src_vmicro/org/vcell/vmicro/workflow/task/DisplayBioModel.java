package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.display.DisplayBioModelOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.biomodel.BioModel;

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
		// get input
		BioModel biomodel = context.getData(bioModel);
		
		// do op
		DisplayBioModelOp op = new DisplayBioModelOp();
		op.displayBioModel(biomodel);
		
		// set output
		context.setData(displayed,true);
	}
}
