package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateTrivial2DPsf extends Task {
	
	//
	// inputs
	//
		// none
	
	//
	// outputs
	//
	public final DataOutput<UShortImage> psf_2D;
	

	public GenerateTrivial2DPsf(String id){
		super(id);
		psf_2D = new DataOutput<UShortImage>(UShortImage.class,"psf_2D",this);
		addOutput(psf_2D);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		
		//	psf image is now expecting a 3x3 image that are all zeros with a 1 in the middle (Kronecker delta)
		short[] pixels = new short[] { 0, 0, 0, 0, 1, 0, 0, 0, 0 };
		Origin origin = new Origin(0,0,0);
		Extent extent =new Extent(1, 1, 1);
		ISize isize = new ISize(3, 3, 1);
		
		UShortImage psfImage = new UShortImage(pixels,origin,extent,isize.getX(),isize.getY(),isize.getZ());
		context.setData(psf_2D,psfImage);
	}

}
