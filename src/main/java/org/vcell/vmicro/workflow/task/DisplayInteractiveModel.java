package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.display.DisplayInteractiveModelOp;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

public class DisplayInteractiveModel extends Task {
	
	//
	// inputs
	//
	public final DataInput<OptContext> optContext;
	public final DataInput<NormalizedSampleFunction[]> rois;
	public final DataInput<String> title;
	
	//
	// outputs
	//
	public final DataOutput<Boolean> displayed;
	

	public DisplayInteractiveModel(String id){
		super(id);
		optContext = new DataInput<OptContext>(OptContext.class,"optContext", this);
		rois = new DataInput<NormalizedSampleFunction[]>(NormalizedSampleFunction[].class,"rois", this);
		title = new DataInput<String>(String.class,"title",this,true);
		displayed = new DataOutput<Boolean>(Boolean.class,"displayed",this);
		addInput(optContext);
		addInput(rois);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		String titleString = context.getDataWithDefault(title, "no title - not connected");
		OptContext optcontext = context.getData(optContext);
		NormalizedSampleFunction[] roiArray = context.getData(rois);
		
		DisplayInteractiveModelOp op = new DisplayInteractiveModelOp();
		op.displayOptModel(optcontext, roiArray, context.getLocalWorkspace(), titleString, null);
		
		// set output
		context.setData(displayed,true);
	}
}
