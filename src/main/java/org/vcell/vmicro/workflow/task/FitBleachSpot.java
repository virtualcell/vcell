package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.FitBleachSpotOp;
import org.vcell.vmicro.op.FitBleachSpotOp.FitBleachSpotOpResults;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.FloatImage;

public class FitBleachSpot extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> normalizedImages;
	public final DataInput<NormalizedSampleFunction> bleachROI;
	//
	// outputs
	//
	public final DataOutput<Double> bleachRadius_ROI;
	public final DataOutput<Double> centerX_ROI;
	public final DataOutput<Double> centerY_ROI;
	public final DataOutput<Double> bleachRadius_GaussianFit;
	public final DataOutput<Double> bleachFactorK_GaussianFit;
	public final DataOutput<Double> centerX_GaussianFit;
	public final DataOutput<Double> centerY_GaussianFit;
	

	public FitBleachSpot(String id){
		super(id);
		normalizedImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"normalizedImages", this);
		bleachROI = new DataInput<NormalizedSampleFunction>(NormalizedSampleFunction.class,"bleachROI", this);
		addInput(normalizedImages);
		addInput(bleachROI);

		bleachRadius_ROI = new DataOutput<Double>(Double.class,"bleachRadiusROI_ROI",this);
		centerX_ROI = new DataOutput<Double>(Double.class,"centerX_ROI",this);
		centerY_ROI = new DataOutput<Double>(Double.class,"centerY_ROI",this);
		bleachRadius_GaussianFit = new DataOutput<Double>(Double.class,"bleachRadius_GaussianFit",this);
		bleachFactorK_GaussianFit = new DataOutput<Double>(Double.class,"bleachFactorK_GaussianFit",this);
		centerX_GaussianFit = new DataOutput<Double>(Double.class,"centerX_GaussianFit",this);
		centerY_GaussianFit = new DataOutput<Double>(Double.class,"centerY_GaussianFit",this);
		addOutput(bleachRadius_ROI);
		addOutput(centerX_ROI);
		addOutput(centerY_ROI);
		addOutput(bleachRadius_GaussianFit);
		addOutput(bleachFactorK_GaussianFit);
		addOutput(centerX_GaussianFit);
		addOutput(centerY_GaussianFit);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get inputs
		NormalizedSampleFunction bleach_roi = context.getData(bleachROI);
		FloatImage normImage = (FloatImage)context.getData(normalizedImages).getAllImages()[0];
		
		// do operation
		FitBleachSpotOp fitBleachSpotOp = new FitBleachSpotOp();
		FitBleachSpotOpResults opResults = fitBleachSpotOp.fit(bleach_roi, normImage);
		
		// set outputs
		context.setData(bleachRadius_ROI,opResults.bleachRadius_ROI);
		context.setData(centerX_ROI,opResults.centerX_ROI);
		context.setData(centerY_ROI,opResults.centerY_ROI);
		context.setData(bleachFactorK_GaussianFit,opResults.bleachFactorK_GaussianFit);
		context.setData(bleachRadius_GaussianFit, opResults.bleachRadius_ROI);
		context.setData(centerX_GaussianFit, opResults.centerX_GaussianFit);
		context.setData(centerY_GaussianFit, opResults.centerY_GaussianFit);
	}
}
