package org.vcell.vmicro.op;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateNormalizedFrapDataOp {

	public static class NormalizedFrapDataResults {
		public ImageTimeSeries<FloatImage> normalizedFrapData;
		public FloatImage prebleachAverage;
	}	

	private double getAverage(UShortImage image, ROI roi){
		double intensityVal = 0.0;
		long numPixelsInMask = 0;

		double[] doublePixels = image.getDoublePixels();
		short[] roiPixels = roi.getBinaryPixelsXYZ(1);
		for (int i = 0; i < doublePixels.length; i++) {
			if (roiPixels[i] != 0){
				intensityVal += doublePixels[i];
				numPixelsInMask++;
			}
		}
		if (numPixelsInMask==0){
			throw new RuntimeException("ROI was empty");
		}
		return intensityVal/numPixelsInMask;
	}

	public NormalizedFrapDataResults generate(ImageTimeSeries<UShortImage> rawImageTimeSeries, ROI backgroundROI_2D, Integer indexPostbleach) throws Exception {
		UShortImage firstImage = rawImageTimeSeries.getAllImages()[0];
		ISize isize = firstImage.getISize();
		int nX = isize.getX();
		int nY = isize.getY();
		int nZ = isize.getZ();
		int numTimes = rawImageTimeSeries.getSizeT();
		Extent extent = firstImage.getExtent();
		org.vcell.util.Origin origin = firstImage.getOrigin();
		
		if (indexPostbleach == 0){
			throw new RuntimeException("no prebleach images found - indexOfFirstPostbleach is 0");
		}
		
		//
		// find average "dark count" in background of pre-bleach images (for background subtraction)
		//
		float avgBackground = 0.0f;
		int numPrebleach = indexPostbleach;
		for (int i=0;i<numPrebleach;i++){
			avgBackground += getAverage(rawImageTimeSeries.getAllImages()[i], backgroundROI_2D);
		}
		avgBackground /= numPrebleach;
		
		//
		// find averaged prebleach image (corrected for background). 
		//
		int numPostbleachImages = numTimes-numPrebleach;
		float[] prebleachAveragePixels = new float[nX*nY*nZ]; // holds new averaged image pixels
		for (int i=0;i<numPrebleach;i++){
			short[] currPrebleachImage = rawImageTimeSeries.getAllImages()[i].getPixels();
			for (int j=0;j<isize.getXYZ();j++){
				float intPixel = 0x0000ffff & ((int)currPrebleachImage[j]);
				prebleachAveragePixels[j] += intPixel/numPrebleach;
			}
		}
		FloatImage prebleachAverageImage = new FloatImage(prebleachAveragePixels,origin,extent,nX,nY,nZ);
		
		//
		// create normalized dataset 
		//
		// normalized postbleach = (origPostbleach - background)/(prebleach - background)
		//
		FloatImage[] normalizedImages = new FloatImage[numPostbleachImages];
		double[] postbleachTimeStamps = new double[numPostbleachImages];
		for (int i=0;i<numPostbleachImages;i++){
			double[] origTimeStamps = rawImageTimeSeries.getImageTimeStamps();
			postbleachTimeStamps[i] = origTimeStamps[indexPostbleach + i] - origTimeStamps[indexPostbleach]; 
			float[] normalizedPixels = new float[isize.getXYZ()];
			normalizedImages[i] = new FloatImage(normalizedPixels, origin, extent, nX, nY, nZ);
			short[] uncorrectedPixels = rawImageTimeSeries.getAllImages()[i+indexPostbleach].getPixels();
			for (int j=0;j<isize.getXYZ();j++){
				int intPixel = 0x0000ffff & ((int)uncorrectedPixels[j]);
				normalizedPixels[j] = (intPixel - avgBackground)/(Math.max(1, prebleachAveragePixels[j] - avgBackground));
			}
			normalizedImages[i] = new FloatImage(normalizedPixels,origin,extent,nX,nY,nZ);
		}
		ImageTimeSeries<FloatImage> normalizedData = new ImageTimeSeries<FloatImage>(FloatImage.class, normalizedImages, postbleachTimeStamps, nZ);
		
		NormalizedFrapDataResults results = new NormalizedFrapDataResults();
		
		results.normalizedFrapData = normalizedData;
		results.prebleachAverage = prebleachAverageImage;
		
		return results;
	}

}
