package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ModelType;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.vmicro.workflow.data.OptModelOneDiff;
import org.vcell.vmicro.workflow.data.OptModelTwoDiffWithPenalty;
import org.vcell.vmicro.workflow.data.OptModelTwoDiffWithoutPenalty;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.math.RowColumnResultSet;

public class GenerateRefSimOptModel extends Task {
	
	//
	// inputs
	//
	public final DataInput<RowColumnResultSet> refSimData;
	public final DataInput<Double> refSimDiffusionRate;
	public final DataInput<String> modelType;
	//
	// outputs
	//
	public final DataHolder<OptModel> optModel;
	
	public GenerateRefSimOptModel(String id){
		super(id);
		refSimData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"refSimData",this);
		refSimDiffusionRate = new DataInput<Double>(Double.class,"refSimDiffusionRate",this);
		modelType = new DataInput<String>(String.class,"modelType",this);
		optModel = new DataHolder<OptModel>(OptModel.class,"optModel",this);
		addInput(refSimData);
		addInput(refSimDiffusionRate);
		addInput(modelType);
		addOutput(optModel);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		RowColumnResultSet refSimDataset = refSimData.getData();
		double[] refSimTimePoints = refSimDataset.extractColumn(0);

		int numRois = refSimDataset.getDataColumnCount()-1;
		int numRefSimTimes = refSimDataset.getRowCount();
		double[][] refSimData = new double[numRois][numRefSimTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = refSimDataset.extractColumn(roi+1);
			for (int t=0; t<numRefSimTimes; t++){
				refSimData[roi][t] = roiData[t];
			}
		}
		
		ModelType modelType = ModelType.valueOf(this.modelType.getData());
		OptModel optModel = null;
		switch (modelType){
			case DiffOne: {
				optModel = new OptModelOneDiff(refSimData, refSimTimePoints, refSimDiffusionRate.getData());
				break;
			}
			case DiffTwoWithoutPenalty: {
				optModel = new OptModelTwoDiffWithoutPenalty(refSimData, refSimTimePoints, refSimDiffusionRate.getData());
				break;
			}
			case DiffTwoWithPenalty: {
				optModel = new OptModelTwoDiffWithPenalty(refSimData, refSimTimePoints, refSimDiffusionRate.getData());
				break;
			}
			default:{
				throw new RuntimeException("model type "+modelType+" not supported");
			}
		}
		
		this.optModel.setData(optModel);
	}
	
}
