package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.NormalizeRawBleachDataOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.math.RowColumnResultSet;

public class NormalizeRawBleachData extends Task {
	
	//
	// inputs
	//
	public final DataInput<RowColumnResultSet> rawExpData;
	//
	// outputs
	//
	public final DataOutput<RowColumnResultSet> normExpData;
	
	

	public NormalizeRawBleachData(String id){
		super(id);
		rawExpData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"rawExpData",this);
		normExpData = new DataOutput<RowColumnResultSet>(RowColumnResultSet.class,"normExpData",this);
		addInput(rawExpData);
		addOutput(normExpData);
	}
	
	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		RowColumnResultSet rawExpDataset = context.getData(rawExpData);
		
		// do op
		NormalizeRawBleachDataOp op = new NormalizeRawBleachDataOp();
		RowColumnResultSet normExpDataset = op.normalizeRawBleachData(rawExpDataset);

		// set output
		context.setData(normExpData,normExpDataset);
	}
	
}
