package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.math.RowColumnResultSet;

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
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		
		RowColumnResultSet normExpDataset = context.getData(normExpData);
		double[] normExpTimePoints = normExpDataset.extractColumn(0);

		int numRois = normExpDataset.getDataColumnCount()-1;
		int numNormExpTimes = normExpDataset.getRowCount();
		double[][] normExpData = new double[numRois][numNormExpTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = normExpDataset.extractColumn(roi+1);
			for (int t=0; t<numNormExpTimes; t++){
				normExpData[roi][t] = roiData[t];
			}
		}
		
		RowColumnResultSet measurementErrorDataset = context.getData(normalizedMeasurementErrors);
		double[][] measurementErrors = new double[numRois][numNormExpTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = measurementErrorDataset.extractColumn(roi+1);
			for (int t=0; t<numNormExpTimes; t++){
				measurementErrors[roi][t] = roiData[t];
			}
		}
		
		context.setData(optContext,new OptContext(context.getData(optModel),normExpData,normExpTimePoints,measurementErrors));
	}
	
}
