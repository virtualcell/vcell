package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.display.DisplayPlotOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.math.RowColumnResultSet;

public class DisplayPlot extends Task {
	
	//
	// inputs
	//
	public final DataInput<RowColumnResultSet> plotData;
	public final DataInput<String> title;
	
	//
	// outputs
	//
	public final DataOutput<Boolean> displayed;
	

	public DisplayPlot(String id){
		super(id);
		plotData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"plotData", this);
		title = new DataInput<String>(String.class,"title",this,true);
		displayed = new DataOutput<Boolean>(Boolean.class,"displayed",this);
		addInput(plotData);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		String titleString = context.getDataWithDefault(title,"no title");
		RowColumnResultSet plotdata = context.getData(plotData);
		
		// do op
		DisplayPlotOp op = new DisplayPlotOp();
		op.displayPlot(plotdata, titleString, null);
		
		// set output
		context.setData(displayed,true);
	}
}
