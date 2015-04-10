package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.GenerateDependentImageROIsOp;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.ROI;

public class GenerateDependentImageROIs extends Task {
	
	private enum VFRAP_ROI_ENUM {
		ROI_BLEACHED_RING1,
		ROI_BLEACHED_RING2,
		ROI_BLEACHED_RING3,
		ROI_BLEACHED_RING4,
		ROI_BLEACHED_RING5,
		ROI_BLEACHED_RING6,
		ROI_BLEACHED_RING7,
		ROI_BLEACHED_RING8
	};
	
	//
	// inputs
	//
	public final DataInput<ROI> cellROI_2D;
	public final DataInput<ROI> bleachedROI_2D;
	
	//
	// outputs
	//
	public final DataOutput<ROI[]> imageDataROIs;
	

	public GenerateDependentImageROIs(String id){
		super(id);
		cellROI_2D = new DataInput<ROI>(ROI.class,"cellROI_2D",this);
		bleachedROI_2D = new DataInput<ROI>(ROI.class,"bleachedROI_2D",this);
		imageDataROIs = new DataOutput<ROI[]>(ROI[].class,"ImageDataROIs",this);
		addInput(cellROI_2D);
		addInput(bleachedROI_2D);
		addOutput(imageDataROIs);
	}
	
	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// get input
		ROI cellROI_2D = context.getData(this.cellROI_2D);
		ROI bleachedROI_2D = context.getData(this.bleachedROI_2D);

		// do op
		GenerateDependentImageROIsOp op = new GenerateDependentImageROIsOp();
		ROI[] rois = op.generate(cellROI_2D, bleachedROI_2D);
		
		// set output
		context.setData(imageDataROIs,rois);
	}

}
