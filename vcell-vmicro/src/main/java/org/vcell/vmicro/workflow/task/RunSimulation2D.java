package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.RunSimulation2DOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.solver.Simulation;

public class RunSimulation2D extends Task {
	
	//
	// inputs
	//
	public final DataInput<Simulation> simulation_2D;
	public final DataInput<String> dataVarName;
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> simTimeSeries;
	

	public RunSimulation2D(String id){
		super(id);
		simulation_2D = new DataInput<Simulation>(Simulation.class,"simulation_2D",this);
		dataVarName = new DataInput<String>(String.class,"dataVarName",this);
		simTimeSeries = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"simTimeSeries",this);
		addInput(simulation_2D);
		addInput(dataVarName);
		addOutput(simTimeSeries);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		LocalWorkspace localWorkspace = context.getLocalWorkspace();
		Simulation sim = context.getData(simulation_2D);
		String dataVar = context.getData(dataVarName);
		
		// do op
		RunSimulation2DOp op = new RunSimulation2DOp();
		ImageTimeSeries<FloatImage> solution = op.runRefSimulation(localWorkspace, sim, dataVar, clientTaskStatusSupport);

		// set output
		context.setData(simTimeSeries,solution);
	}
	
}
