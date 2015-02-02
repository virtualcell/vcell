package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk2P;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk3P;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

public class GenerateKenworthyOptModel extends Task {
	
	public enum ModelType {
		KenworthyUniformDisk2Param("Uniform Disk bleach area: D,amp"),
		KenworthyUniformDisk3Param("Uniform Disk bleach area: D,amp,bwm"), 
		KenworthyEffectiveRadius("Gaussian bleach Rn and detection Re radii");
		
		public final String description;
		
		private ModelType(String desc){
			this.description = desc;
		}
		
		public static ModelType fromDescription(String desc){
			for (ModelType id : values()){
				if (id.description.equals(desc)){
					return id;
				}
			}
			return null;
		}
	}
	
	//
	// inputs
	//
	public final DataInput<Double> bleachRadius;
	public final DataInput<String> modelType;
	//
	// outputs
	//
	public final DataOutput<OptModel> optModel;
	
	public GenerateKenworthyOptModel(String id){
		super(id);
		bleachRadius = new DataInput<Double>(Double.class,"bleachRadius",this);
		modelType = new DataInput<String>(String.class,"modelType",this);
		optModel = new DataOutput<OptModel>(OptModel.class,"optModel",this);
		addInput(bleachRadius);
		addInput(modelType);
		addOutput(optModel);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		double bleachRadiusValue = context.getData(bleachRadius);
		
		ModelType modelType = ModelType.valueOf(context.getData(this.modelType));
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
		case KenworthyEffectiveRadius: {
			throw new RuntimeException("not yet implemented");
//			optModel = new OptModelKenworthyEffectiveRadius(bleachRadiusValue, detectionRadiusValue);
//			break;
		}
		default:{
			throw new RuntimeException("model type "+modelType+" not supported");
		}
		}
		
		context.setData(this.optModel,optModel);
	}
	
}
