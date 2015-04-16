package org.vcell.vmicro.op;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateActivationRoiOp {
	
	public ROI generateActivatedRoi(Image firstPostactivation, ROI cellROI_2D, Double activationThreshold) throws ImageException {
		int numPixels = firstPostactivation.getNumXYZ();
		
		short[] scaledActivatedDataShort = new short[numPixels];
				
		short[] erodedCellUShort = cellROI_2D.getRoiImages()[0].getBinaryPixels(1);
		double activationThresholdValue = activationThreshold; // input is already normalized to 1.0 ... if relative to max, then crazy values from outside cell can interfere.
		double[] firstPostactivationImage = firstPostactivation.getDoublePixels();
		for (int j = 0; j < numPixels; j++) {
			boolean isCell = (erodedCellUShort[j] == 1);
			boolean isActivation = firstPostactivationImage[j] > activationThresholdValue;
			if (isCell && isActivation) {
				scaledActivatedDataShort[j] = 1;
			}
		}


		UShortImage activatedImage =
			new UShortImage(
					scaledActivatedDataShort,
					firstPostactivation.getOrigin(),
					firstPostactivation.getExtent(),
					firstPostactivation.getNumX(),firstPostactivation.getNumY(),firstPostactivation.getNumZ());
		
		ROI bleachedROI = new ROI(activatedImage,"bleachedROI");
		
		return bleachedROI;
	}

}
