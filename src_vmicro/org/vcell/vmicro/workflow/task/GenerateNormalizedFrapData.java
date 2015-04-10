package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp.NormalizedFrapDataResults;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateNormalizedFrapData extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawImageTimeSeries;
	public final DataInput<ROI> backgroundROI_2D;
	public final DataInput<Integer> indexOfFirstPostbleach;
	
	//
	// outputs
	//
	public final DataOutput<ImageTimeSeries> normalizedFrapData;
	public final DataOutput<FloatImage> prebleachAverage;
	

	public GenerateNormalizedFrapData(String id){
		super(id);
		rawImageTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawImageTimeSeries",this);
		backgroundROI_2D = new DataInput<ROI>(ROI.class,"backgroundROI_2D",this);
		indexOfFirstPostbleach = new DataInput<Integer>(Integer.class,"indexOfFirstPostbleach",this);
		normalizedFrapData = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class,"normalizedFrapData",this);
		prebleachAverage = new DataOutput<FloatImage>(FloatImage.class,"prebleachAverage",this);
		addInput(rawImageTimeSeries);
		addInput(backgroundROI_2D);
		addInput(indexOfFirstPostbleach);
		addOutput(normalizedFrapData);
		addOutput(prebleachAverage);
	}
	
	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		ImageTimeSeries<UShortImage> rawTimeSeries = context.getData(rawImageTimeSeries);
		ROI backgroundROI = context.getData(backgroundROI_2D);
		Integer indexPostbleach = context.getData(indexOfFirstPostbleach);
		
		// do op
		GenerateNormalizedFrapDataOp op = new GenerateNormalizedFrapDataOp();
		NormalizedFrapDataResults results = op.generate(rawTimeSeries, backgroundROI, indexPostbleach);
		
		// set output
		context.setData(normalizedFrapData,results.normalizedFrapData);
		context.setData(this.prebleachAverage,results.prebleachAverage);	
	}

}
