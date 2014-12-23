package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ModelType;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.vmicro.workflow.data.OptModelOneDiff;
import org.vcell.vmicro.workflow.data.OptModelTwoDiffWithPenalty;
import org.vcell.vmicro.workflow.data.OptModelTwoDiffWithoutPenalty;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

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
	public final DataOutput<OptModel> optModel;
	
	public GenerateRefSimOptModel(String id){
		super(id);
		refSimData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"refSimData",this);
		refSimDiffusionRate = new DataInput<Double>(Double.class,"refSimDiffusionRate",this);
		modelType = new DataInput<String>(String.class,"modelType",this);
		optModel = new DataOutput<OptModel>(OptModel.class,"optModel",this);
		addInput(refSimData);
		addInput(refSimDiffusionRate);
		addInput(modelType);
		addOutput(optModel);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		RowColumnResultSet refSimDataset = context.getData(refSimData);
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
		
		ModelType modelType = ModelType.valueOf(context.getData(this.modelType));
		OptModel optModel = null;
		Double refSimDiff = context.getData(refSimDiffusionRate);
		switch (modelType){
			case DiffOne: {
				optModel = new OptModelOneDiff(refSimData, refSimTimePoints, refSimDiff);
				break;
			}
			case DiffTwoWithoutPenalty: {
				optModel = new OptModelTwoDiffWithoutPenalty(refSimData, refSimTimePoints, refSimDiff);
				break;
			}
			case DiffTwoWithPenalty: {
				optModel = new OptModelTwoDiffWithPenalty(refSimData, refSimTimePoints, refSimDiff);
				break;
			}
			default:{
				throw new RuntimeException("model type "+modelType+" not supported");
			}
		}
		
		context.setData(this.optModel,optModel);
	}
	
}
