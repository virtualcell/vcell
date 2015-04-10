package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.RunRefSimulationOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;

public class RunRefSimulation extends Task {
	
	//
	// inputs
	//
	public final DataInput<ROI> cellROI_2D;
	public final DataInput<ImageTimeSeries> normalizedTimeSeries;
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> refSimTimeSeries;
	public final DataOutput<Double> refSimDiffusionRate;
	

	public RunRefSimulation(String id){
		super(id);
		cellROI_2D = new DataInput<ROI>(ROI.class,"cellROI_2D",this);
		normalizedTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"normalizedTimeSeries",this);
		refSimTimeSeries = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"refSimTimeSeries",this);
		refSimDiffusionRate = new DataOutput<Double>(Double.class,"refSimDiffusionRate",this);
		addInput(cellROI_2D);
		addInput(normalizedTimeSeries);
		addOutput(refSimTimeSeries);
		addOutput(refSimDiffusionRate);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		ImageTimeSeries<FloatImage> normTimeSeries = context.getData(normalizedTimeSeries);
		ROI cellROI = context.getData(cellROI_2D);
		double referenceDiffusionRate = 1.0;
		
		// do op
		RunRefSimulationOp op = new RunRefSimulationOp();
		ImageTimeSeries<FloatImage> solution = op.compute0(cellROI, normTimeSeries, referenceDiffusionRate, context.getLocalWorkspace(), clientTaskStatusSupport);
		
		// set output
		context.setData(refSimTimeSeries,solution);
		context.setData(refSimDiffusionRate,referenceDiffusionRate);
	}
	
}
