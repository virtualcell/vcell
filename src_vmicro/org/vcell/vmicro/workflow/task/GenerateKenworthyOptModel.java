package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ModelType;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk2P;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk3P;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk4P;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

public class GenerateKenworthyOptModel extends Task {
	
	//
	// inputs
	//
	public final DataInput<Double> bleachRadius;
	public final DataInput<String> modelType;
	//
	// outputs
	//
	public final DataHolder<OptModel> optModel;
	
	public GenerateKenworthyOptModel(String id){
		super(id);
		bleachRadius = new DataInput<Double>(Double.class,"bleachRadius",this);
		modelType = new DataInput<String>(String.class,"modelType",this);
		optModel = new DataHolder<OptModel>(OptModel.class,"optModel",this);
		addInput(bleachRadius);
		addInput(modelType);
		addOutput(optModel);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		double bleachRadiusValue = bleachRadius.getData();
		
		ModelType modelType = ModelType.valueOf(this.modelType.getData());
		OptModel optModel = null;
		switch (modelType){
		case KenworthyUniformDisk2Param: {
			optModel = new OptModelKenworthyUniformDisk2P(bleachRadiusValue);
			break;
		}
		case KenworthyUniformDisk3Param: {
			optModel = new OptModelKenworthyUniformDisk3P(bleachRadiusValue);
			break;
		}
		case KenworthyUniformDisk4Param: {
			optModel = new OptModelKenworthyUniformDisk4P(bleachRadiusValue);
			break;
		}
		default:{
			throw new RuntimeException("model type "+modelType+" not supported");
		}
		}
		
		this.optModel.setData(optModel);
	}
	
}
