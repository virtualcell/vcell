package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.Generate2DOptContextOp;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.parser.ExpressionException;

public class Generate2DOptContext extends Task {
	
	//
	// inputs
	//
	public final DataInput<OptModel> optModel;
	public final DataInput<RowColumnResultSet> normExpData;
	public final DataInput<RowColumnResultSet> normalizedMeasurementErrors;
	//
	// outputs
	//
	public final DataOutput<OptContext> optContext;
	
	public Generate2DOptContext(String id){
		super(id);
		optModel = new DataInput<OptModel>(OptModel.class,"optModel",this);
		normExpData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"normExpData",this);
		normalizedMeasurementErrors = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"normalizedMeasurementErrors",this);
		optContext = new DataOutput<OptContext>(OptContext.class,"optContext",this);
		addInput(optModel);
		addInput(normExpData);
		addInput(normalizedMeasurementErrors);
		addOutput(optContext);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws ExpressionException {
		// get inputs
		RowColumnResultSet normExpDataset = context.getData(normExpData);
		RowColumnResultSet measurementErrorDataset = context.getData(normalizedMeasurementErrors);
		OptModel optmodel = context.getData(optModel);
		
		// do op
		Generate2DOptContextOp op = new Generate2DOptContextOp();
		OptContext optcontext = op.generate2DOptContext(optmodel, normExpDataset, measurementErrorDataset);
		
		// set output
		context.setData(optContext,optcontext);
	}
	
}
