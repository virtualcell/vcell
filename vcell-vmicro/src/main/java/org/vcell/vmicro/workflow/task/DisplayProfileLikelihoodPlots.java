package org.vcell.vmicro.workflow.task;

import java.awt.event.WindowListener;

import org.vcell.optimization.ProfileData;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.display.DisplayProfileLikelihoodPlotsOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

public class DisplayProfileLikelihoodPlots extends Task {
	
	//
	// inputs
	//
	public final DataInput<ProfileData[]> profileData;
	public final DataInput<String> title;
		
	//
	// outputs
	//
	public final DataOutput<Boolean> displayed;

	
	public DisplayProfileLikelihoodPlots(String id){
		super(id);
		profileData = new DataInput<ProfileData[]>(ProfileData[].class,"profileData", this);
		title = new DataInput<String>(String.class,"title",this);
		displayed = new DataOutput<Boolean>(Boolean.class,"displayed",this);
		addInput(profileData);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		ProfileData[] profiledata = context.getData(profileData);
		String titleString = context.getDataWithDefault(title,"no title");
		WindowListener listener = null;
		
		// do op
		DisplayProfileLikelihoodPlotsOp op = new DisplayProfileLikelihoodPlotsOp();
		op.displayProfileLikelihoodPlots(profiledata, titleString, listener);
		
		
		// set output
		context.setData(displayed,true);
	}
}
