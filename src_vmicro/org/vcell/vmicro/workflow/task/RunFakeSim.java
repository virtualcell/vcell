package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.RunFakeSimOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.solver.Simulation;

public class RunFakeSim extends Task {
	
	//
	// inputs
	//
	public final DataInput<Simulation> simulation_2D;
	public final DataInput<Double> maxIntensity;
	public final DataInput<Boolean> bNoise;
	public final DataInput<Double> bleachBlackoutBeginTime;
	public final DataInput<Double> bleachBlackoutEndTime;

	
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> simTimeSeries;
	
	
	public RunFakeSim(String id){
		super(id);
		simulation_2D = new DataInput<Simulation>(Simulation.class,"simulation_2D",this);
		maxIntensity = new DataInput<Double>(Double.class,"maxIntensity",this);
		bNoise = new DataInput<Boolean>(Boolean.class,"bNoise",this);
		bleachBlackoutBeginTime = new DataInput<Double>(Double.class,"bleachBlackoutBeginTime",this);
		bleachBlackoutEndTime = new DataInput<Double>(Double.class,"bleachBlackoutEndTime",this);
		addInput(simulation_2D);
		addInput(maxIntensity);
		addInput(bNoise);
		addInput(bleachBlackoutBeginTime);
		addInput(bleachBlackoutEndTime);

		simTimeSeries = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"simTimeSeries",this);
		addOutput(simTimeSeries);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// set input
		Simulation sim = context.getData(simulation_2D);
		Double max_intensity = context.getData(maxIntensity);
		Double bleachLockingBegin = context.getData(bleachBlackoutBeginTime);
		Double bleachLockoutEnd = context.getData(bleachBlackoutEndTime);
		Boolean hasNoise = context.getData(bNoise);

		// do op
		RunFakeSimOp op = new RunFakeSimOp();
		ImageTimeSeries<UShortImage> solution = op.runRefSimulation(context.getLocalWorkspace(), sim, max_intensity, bleachLockingBegin, bleachLockoutEnd, hasNoise, clientTaskStatusSupport);
		
		// set output
		context.setData(simTimeSeries,solution);
	}
}
