package org.vcell.vmicro.op;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateBleachRoiOp {
	
	public ROI generateBleachRoi(Image firstPostbleach, ROI cellROI_2D, Double bleachThreshold) throws ImageException {
		int numPixels = firstPostbleach.getNumXYZ();
		
		short[] scaledBleachedDataShort = new short[numPixels];
				
		short[] erodedCellUShort = cellROI_2D.getRoiImages()[0].getBinaryPixels(1);
		double bleachThresholdValue = bleachThreshold; // input is already normalized to 1.0 ... if relative to max, then crazy values from outside cell can interfere.
		double[] firstPostbleachImage = firstPostbleach.getDoublePixels();
		for (int j = 0; j < numPixels; j++) {
			boolean isCell = (erodedCellUShort[j] == 1);
			boolean isBleach = firstPostbleachImage[j] < bleachThresholdValue;
			if (isCell && isBleach) {
				scaledBleachedDataShort[j] = 1;
			}
		}


		UShortImage bleachedImage =
			new UShortImage(
					scaledBleachedDataShort,
					firstPostbleach.getOrigin(),
					firstPostbleach.getExtent(),
					firstPostbleach.getNumX(),firstPostbleach.getNumY(),firstPostbleach.getNumZ());
		
		ROI bleachedROI = new ROI(bleachedImage,"bleachedROI");
		
		return bleachedROI;
	}

}
