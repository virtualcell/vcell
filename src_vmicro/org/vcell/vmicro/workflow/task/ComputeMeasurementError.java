package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.ComputeMeasurementErrorOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;

public class ComputeMeasurementError extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawImageTimeSeries;
	public final DataInput<FloatImage> prebleachAverage;
	public final DataInput<ROI[]> imageDataROIs;
	public final DataInput<Integer> indexFirstPostbleach;
	//
	// outputs
	//
	public final DataOutput<RowColumnResultSet> normalizedMeasurementError;
	
	public ComputeMeasurementError(String id){
		super(id);
		rawImageTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawImageTimeSeries",this);
		prebleachAverage = new DataInput<FloatImage>(FloatImage.class,"prebleachAverage",this);
		imageDataROIs = new DataInput<ROI[]>(ROI[].class,"imageDataROIs",this);
		indexFirstPostbleach = new DataInput<Integer>(Integer.class,"indexFirstPostbleach",this);
		normalizedMeasurementError = new DataOutput<RowColumnResultSet>(RowColumnResultSet.class,"normalizedMeasurmentError",this);
		addInput(rawImageTimeSeries);
		addInput(prebleachAverage);
		addInput(imageDataROIs);
		addInput(indexFirstPostbleach);
		addOutput(normalizedMeasurementError);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		
		ROI[] rois = context.getData(imageDataROIs);
		int indexPostbleach = context.getData(indexFirstPostbleach);
		ImageTimeSeries<UShortImage> rawImageDataset = context.getData(rawImageTimeSeries);
		FloatImage prebleachAvgImage = context.getData(prebleachAverage);
		
		// do op
		ComputeMeasurementErrorOp op = new ComputeMeasurementErrorOp();
		RowColumnResultSet rowColumnResultSet = op.computeNormalizedMeasurementError(rois, indexPostbleach, rawImageDataset, prebleachAvgImage, clientTaskStatusSupport);
		
		// set output
		context.setData(normalizedMeasurementError,rowColumnResultSet);
	}
}
