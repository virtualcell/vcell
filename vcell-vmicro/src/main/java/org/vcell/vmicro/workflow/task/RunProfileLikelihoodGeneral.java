package org.vcell.vmicro.workflow.task;

import org.vcell.optimization.ProfileData;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.RunProfileLikelihoodGeneralOp;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

public class RunProfileLikelihoodGeneral extends Task {
	
	//
	// inputs
	//
	public final DataInput<OptContext> optContext;
	//
	// outputs
	//
	public final DataOutput<ProfileData[]> profileData;
	
	private double leastError = Double.MAX_VALUE;
	

	public RunProfileLikelihoodGeneral(String id){
		super(id);
		optContext = new DataInput<OptContext>(OptContext.class,"optContext",this);
		profileData = new DataOutput<ProfileData[]>(ProfileData[].class,"profileData",this);
		addInput(optContext);
		addOutput(profileData);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		OptContext optContext = context.getData(this.optContext);
		
		// do op
		RunProfileLikelihoodGeneralOp op = new RunProfileLikelihoodGeneralOp();
		ProfileData[] profileData = op.runProfileLikihood(optContext, clientTaskStatusSupport);
		
		// set output
		context.setData(this.profileData,profileData);
	}
	
}
