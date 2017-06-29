package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.GenerateBleachRoiOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;

public class GenerateBleachROI extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> normalizedTimeSeries;
	public final DataInput<ROI> cellROI_2D;
	public final DataInput<Double> bleachThreshold;
	
	//
	// outputs
	//
	public final DataOutput<ROI> bleachedROI_2D;
	public final DataOutput<ROI[]> bleachedROI_2D_array;
	

	public GenerateBleachROI(String id){
		super(id);
		normalizedTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		bleachThreshold = new DataInput<Double>(Double.class,"bleachThreshold",this);
		cellROI_2D = new DataInput<ROI>(ROI.class,"cellROI_2D",this);
		bleachedROI_2D = new DataOutput<ROI>(ROI.class,"bleachedROI_2D",this);
		bleachedROI_2D_array = new DataOutput<ROI[]>(ROI[].class,"bleachedROI_2D_array",this);
		addInput(normalizedTimeSeries);
		addInput(cellROI_2D);
		addInput(bleachThreshold);
		addOutput(bleachedROI_2D);
		addOutput(bleachedROI_2D_array);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		
		// get input
		Image firstPostbleachImage = context.getData(normalizedTimeSeries).getAllImages()[0];
		ROI cellROI = context.getData(cellROI_2D);
		double bleachThresholdValue = context.getData(bleachThreshold);
		
		// do op
		GenerateBleachRoiOp op = new GenerateBleachRoiOp();
		ROI bleachedROI = op.generateBleachRoi(firstPostbleachImage, cellROI, bleachThresholdValue);
		
		// set output
		context.setData(bleachedROI_2D,bleachedROI);
		context.setData(bleachedROI_2D_array,new ROI[] { bleachedROI });
	}

}
