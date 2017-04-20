package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.GenerateCellROIsFromRawFrapTimeSeriesOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawFrapTimeSeriesOp.GeometryRoisAndBleachTiming;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;

public class GenerateCellROIsFromRawTimeSeries extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawTimeSeriesImages;
	public final DataInput<Double> cellThreshold;
	
	//
	// outputs
	//
	public final DataOutput<ROI> cellROI_2D;
	public final DataOutput<ROI> backgroundROI_2D;
	public final DataOutput<Integer> indexOfFirstPostbleach;
	

	public GenerateCellROIsFromRawTimeSeries(String id){
		super(id);
		rawTimeSeriesImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		cellThreshold = new DataInput<Double>(Double.class,"cellThreshold",this);
		cellROI_2D = new DataOutput<ROI>(ROI.class,"cellROI_2D",this);
		backgroundROI_2D = new DataOutput<ROI>(ROI.class,"backgroundROI_2D",this);
		indexOfFirstPostbleach = new DataOutput<Integer>(Integer.class,"indexOfFirstPostbleach",this);
		addInput(rawTimeSeriesImages);
		addInput(cellThreshold);
		addOutput(cellROI_2D);
		addOutput(backgroundROI_2D);
		addOutput(indexOfFirstPostbleach);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get inputs
		ImageTimeSeries<Image> imageTimeSeries = context.getData(rawTimeSeriesImages);
		double cellThresholdValue = context.getData(cellThreshold);
		
		// do op
		GenerateCellROIsFromRawFrapTimeSeriesOp op = new GenerateCellROIsFromRawFrapTimeSeriesOp();
		GeometryRoisAndBleachTiming results = op.generate(imageTimeSeries, cellThresholdValue);
		
		// set output
		context.setData(cellROI_2D,results.cellROI_2D);
		context.setData(backgroundROI_2D,results.backgroundROI_2D);
		context.setData(indexOfFirstPostbleach,results.indexOfFirstPostbleach);
	}

}
