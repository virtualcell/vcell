package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

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
	public final DataHolder<OptContext> optContext;
	
	public Generate2DOptContext(String id){
		super(id);
		optModel = new DataInput<OptModel>(OptModel.class,"optModel",this);
		normExpData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"normExpData",this);
		normalizedMeasurementErrors = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"normalizedMeasurementErrors",this);
		optContext = new DataHolder<OptContext>(OptContext.class,"optContext",this);
		addInput(optModel);
		addInput(normExpData);
		addInput(normalizedMeasurementErrors);
		addOutput(optContext);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		
		RowColumnResultSet normExpDataset = normExpData.getData();
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
		
		RowColumnResultSet measurementErrorDataset = normalizedMeasurementErrors.getData();
		double[][] measurementErrors = new double[numRois][numNormExpTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = measurementErrorDataset.extractColumn(roi+1);
			for (int t=0; t<numNormExpTimes; t++){
				measurementErrors[roi][t] = roiData[t];
			}
		}
		
		optContext.setData(new OptContext(optModel.getData(),normExpData,normExpTimePoints,measurementErrors));
	}
	
}
