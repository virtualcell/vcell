package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vmicro.op.Generate2DExpModelOpAbstract.Context;
import org.vcell.vmicro.op.Generate2DExpModelOpAbstract.GeneratedModelResults;
import org.vcell.vmicro.op.Generate2DExpModel_GaussianBleachOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.solver.Simulation;

public class Generate2DExpModel_GaussianBleach extends Task {
	
	//
	// inputs
	//
	public final DataInput<Double> deltaX;
	public final DataInput<Double> bleachRadius;
	public final DataInput<Double> cellRadius;
	public final DataInput<Double> bleachDuration;
	public final DataInput<Double> bleachRate;
	public final DataInput<Double> postbleachDelay;
	public final DataInput<Double> postbleachDuration;
	public final DataInput<Double> psfSigma;
	public final DataInput<Double> outputTimeStep;
	public final DataInput<Double> primaryDiffusionRate;
	public final DataInput<Double> primaryFraction;
	public final DataInput<Double> bleachMonitorRate;
	public final DataInput<Double> secondaryDiffusionRate;
	public final DataInput<Double> secondaryFraction;
	public final DataInput<String> extracellularName;
	public final DataInput<String> cytosolName;
	//
	// outputs
	//
	public final DataOutput<BioModel> bioModel_2D;
	public final DataOutput<Simulation> simulation_2D;
	public final DataOutput<Double> bleachBlackoutBeginTime;
	public final DataOutput<Double> bleachBlackoutEndTime;
	

	public Generate2DExpModel_GaussianBleach(String id){
		super(id);
		deltaX = new DataInput<Double>(Double.class,"deltaX", this);
		bleachRadius = new DataInput<Double>(Double.class,"bleachRadius",this);
		cellRadius = new DataInput<Double>(Double.class,"cellRadius",this);
		bleachDuration = new DataInput<Double>(Double.class,"bleachDuration",this);
		bleachRate = new DataInput<Double>(Double.class,"bleachRate",this);
		postbleachDelay = new DataInput<Double>(Double.class,"postbleachDelay",this);
		postbleachDuration = new DataInput<Double>(Double.class,"postbleachDuration",this);
		psfSigma = new DataInput<Double>(Double.class,"psfSigma",this);
		outputTimeStep = new DataInput<Double>(Double.class,"outputTimeStep",this);
		primaryDiffusionRate = new DataInput<Double>(Double.class,"primaryDiffusionRate",this);
		primaryFraction = new DataInput<Double>(Double.class,"primaryFraction", this);
		bleachMonitorRate = new DataInput<Double>(Double.class,"bleachMonitorRate", this);
		secondaryDiffusionRate = new DataInput<Double>(Double.class,"secondaryDiffusionRate", this, true);	
		secondaryFraction = new DataInput<Double>(Double.class,"secondaryFraction", this, true);
		extracellularName = new DataInput<String>(String.class,"extracellularName", this);
		cytosolName = new DataInput<String>(String.class,"cytosolName", this);
		addInput(deltaX);
		addInput(bleachRadius);
		addInput(cellRadius);
		addInput(bleachDuration);
		addInput(bleachRate);
		addInput(postbleachDelay);
		addInput(postbleachDuration);
		addInput(psfSigma);
		addInput(outputTimeStep);
		addInput(primaryDiffusionRate);
		addInput(primaryFraction);
		addInput(bleachMonitorRate);
		addInput(secondaryDiffusionRate);
		addInput(secondaryFraction);
		addInput(extracellularName);
		addInput(cytosolName);
		
		bioModel_2D = new DataOutput<BioModel>(BioModel.class,"bioModel_2D",this);
		simulation_2D = new DataOutput<Simulation>(Simulation.class,"simulation_2D",this);
		bleachBlackoutBeginTime = new DataOutput<Double>(Double.class,"bleachBlackoutBeginTime",this);
		bleachBlackoutEndTime = new DataOutput<Double>(Double.class,"bleachBlackoutEndTime",this);
		addOutput(bioModel_2D);
		addOutput(simulation_2D);
		addOutput(bleachBlackoutBeginTime);
		addOutput(bleachBlackoutEndTime);
	}

	@Override
	protected void compute0(final TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		double deltaX = context.getData(this.deltaX);
		double bleachRadius = context.getData(this.bleachRadius);
		double cellRadius = context.getData(this.cellRadius);
		double bleachDuration = context.getData(this.bleachDuration);
		double bleachRate = context.getData(this.bleachRate);
		double postbleachDelay = context.getData(this.postbleachDelay);
		double postbleachDuration = context.getData(this.postbleachDuration);
		double psfSigma = context.getData(this.psfSigma);
		double outputTimeStep = context.getData(this.outputTimeStep);
		double primaryDiffusionRate = context.getData(this.primaryDiffusionRate);
		double primaryFraction = context.getData(this.primaryFraction);
		double bleachMonitorRate = context.getData(this.bleachMonitorRate);
		double secondaryDiffusionRate = context.getData(this.secondaryDiffusionRate);
		double secondaryFraction = context.getData(this.secondaryFraction);
		String extracellularName = context.getData(this.extracellularName);
		String cytosolName = context.getData(this.cytosolName);
		
		// isolate from Workflow
		Context modelGenerationContext = new Context(){
			@Override
			public User getDefaultOwner() {
				return context.getDefaultOwner();
			}
			
			@Override
			public KeyValue createNewKeyValue() {
				return context.createNewKeyValue();
			}
		};

		// do op
		Generate2DExpModel_GaussianBleachOp op = new Generate2DExpModel_GaussianBleachOp();
		GeneratedModelResults results = op.generateModel(
				deltaX, 
				bleachRadius, 
				cellRadius, 
				bleachDuration, 
				bleachRate, 
				postbleachDelay, 
				postbleachDuration, 
				psfSigma, 
				outputTimeStep, 
				primaryDiffusionRate, 
				primaryFraction, 
				bleachMonitorRate, 
				secondaryDiffusionRate, 
				secondaryFraction, 
				extracellularName, 
				cytosolName, 
				modelGenerationContext);

		// set output
		context.setData(this.bioModel_2D,results.bioModel_2D);
		context.setData(this.simulation_2D,results.simulation_2D);
		context.setData(this.bleachBlackoutBeginTime,results.bleachBlackoutBeginTime);
		context.setData(this.bleachBlackoutEndTime,results.bleachBlackoutEndTime);
	}

}
