package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.GenerateReducedDataOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.RowColumnResultSet;

public class GenerateReducedData extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> imageTimeSeries;
	public final DataInput<ROI[]> imageDataROIs;
	//
	// outputs
	//
	public final DataOutput<RowColumnResultSet> reducedROIData;
	

	public GenerateReducedData(String id){
		super(id);
		imageTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"imageTimeSeries", this);
		imageDataROIs = new DataInput<ROI[]>(ROI[].class,"imageDataROIs", this);
		reducedROIData = new DataOutput<RowColumnResultSet>(RowColumnResultSet.class,"reducedROIData",this);
		addInput(imageTimeSeries);
		addInput(imageDataROIs);
		addOutput(reducedROIData);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		ROI[] rois = context.getData(imageDataROIs);
		ImageTimeSeries<? extends Image> simData = (ImageTimeSeries<? extends Image>)context.getData(imageTimeSeries);
		
		// do op
		GenerateReducedDataOp op = new GenerateReducedDataOp();
		RowColumnResultSet reducedData = op.generateReducedData(simData, rois);
		
		// set output
		context.setData(reducedROIData,reducedData);
	}

}
