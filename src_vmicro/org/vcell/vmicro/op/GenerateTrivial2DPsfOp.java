package org.vcell.vmicro.op;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateTrivial2DPsfOp {
	
	public UShortImage generateTrivial2D_Psf() {
		
		//	psf image is now expecting a 3x3 image that are all zeros with a 1 in the middle (Kronecker delta)
		short[] pixels = new short[] { 0, 0, 0, 0, 1, 0, 0, 0, 0 };
		Origin origin = new Origin(0,0,0);
		Extent extent =new Extent(1, 1, 1);
		ISize isize = new ISize(3, 3, 1);
		
		UShortImage psfImage;
		try {
			psfImage = new UShortImage(pixels,origin,extent,isize.getX(),isize.getY(),isize.getZ());
		} catch (ImageException e) {
			e.printStackTrace();
			throw new RuntimeException("unexpected image exception: "+e.getMessage(), e);
		}
		
		return psfImage;
	}

}
