package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.display.DisplayImageOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.Image;

public class DisplayImage extends Task {
	
	//
	// inputs
	//
	public final DataInput<Image> image;
	public final DataInput<String> title;
		
	//
	// outputs
	//
	public final DataOutput<Boolean> displayed;

	
	public DisplayImage(String id){
		super(id);
		image = new DataInput<Image>(Image.class,"image",this);
		title = new DataInput<String>(String.class,"title",this);
		displayed = new DataOutput<Boolean>(Boolean.class,"displayed",this);
		addInput(image);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		DisplayImageOp op = new DisplayImageOp();
		op.displayImage(context.getData(image), context.getDataWithDefault(title,"no title"), null);
		context.setData(displayed,true);
	}
		
}
