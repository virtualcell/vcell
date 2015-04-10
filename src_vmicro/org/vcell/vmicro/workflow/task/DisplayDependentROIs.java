package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.display.DisplayDependentROIsOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.ROI;

public class DisplayDependentROIs extends Task {
	
	//
	// inputs
	//
	public final DataInput<ROI[]> imageROIs;
	public final DataInput<ROI> cellROI;
	public final DataInput<String> title;
		
	//
	// outputs
	//
	public final DataOutput<Boolean> displayed;

	
	public DisplayDependentROIs(String id){
		super(id);
		imageROIs = new DataInput<ROI[]>(ROI[].class,"imageROIs", this);
		cellROI = new DataInput<ROI>(ROI.class,"cellROI", this);
		title = new DataInput<String>(String.class,"title",this);
		displayed = new DataOutput<Boolean>(Boolean.class,"displayed",this);
		addInput(imageROIs);
		addInput(cellROI);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get inputs
		ROI[] imagerois = context.getData(imageROIs);
		ROI cellroi = context.getData(cellROI);
		String titleString = context.getDataWithDefault(title,"no title");
		
		// do op
		DisplayDependentROIsOp op = new DisplayDependentROIsOp();
		op.displayDependentROIs(imagerois, cellroi, titleString, null);
		
		// set output
		context.setData(displayed,true);
	}
}
