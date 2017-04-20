package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.RunRefSimulationFastOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;

public class RunRefSimulationFast extends Task {
	
	//
	// inputs
	//
	public final DataInput<ROI> cellROI_2D;
	public final DataInput<ImageTimeSeries> normalizedTimeSeries;
	public final DataInput<ROI[]> imageDataROIs;
	public final DataInput<UShortImage> psf;

	//
	// outputs
	//
	public final DataOutput<Double> refSimDiffusionRate;
	public final DataOutput<RowColumnResultSet> reducedROIData;
	

	public RunRefSimulationFast(String id){
		super(id);
		cellROI_2D = new DataInput<ROI>(ROI.class,"cellROI_2D",this);
		normalizedTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"normalizedTimeSeries",this);
		psf = new DataInput<UShortImage>(UShortImage.class,"psf",this);
		imageDataROIs = new DataInput<ROI[]>(ROI[].class,"imageDataROIs", this);
		refSimDiffusionRate = new DataOutput<Double>(Double.class,"refSimDiffusionRate",this);
		reducedROIData = new DataOutput<RowColumnResultSet>(RowColumnResultSet.class,"reducedROIData",this);
		addInput(cellROI_2D);
		addInput(normalizedTimeSeries);
		addInput(psf);
		addInput(imageDataROIs);
		addOutput(reducedROIData);
		addOutput(refSimDiffusionRate);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		ImageTimeSeries<FloatImage> normTimeSeries = context.getData(normalizedTimeSeries);
		ROI cellROI = context.getData(cellROI_2D);
		ROI[] imageDataRois = context.getData(imageDataROIs);
		UShortImage psf = context.getData(this.psf);
		
		// do op
		RunRefSimulationFastOp op = new RunRefSimulationFastOp();
		RowColumnResultSet reducedData = op.runRefSimFast(cellROI, normTimeSeries, imageDataRois, psf, context.getLocalWorkspace(), clientTaskStatusSupport);

		// set output
		context.setData(reducedROIData,reducedData);
		context.setData(refSimDiffusionRate,op.getReferenceDiffusionCoef()); // always D = 1, 
	}
	
}
