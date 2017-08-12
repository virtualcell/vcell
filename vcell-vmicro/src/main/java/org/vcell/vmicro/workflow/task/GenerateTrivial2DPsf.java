package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.op.GenerateTrivial2DPsfOp;
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
		
		// do op
		GenerateTrivial2DPsfOp op = new GenerateTrivial2DPsfOp();
		UShortImage psf = op.generateTrivial2D_Psf();
		
		// set output
		context.setData(psf_2D,psf);
	}

}
