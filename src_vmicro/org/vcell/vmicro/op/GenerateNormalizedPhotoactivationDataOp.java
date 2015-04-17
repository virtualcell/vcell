package org.vcell.vmicro.op;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateNormalizedPhotoactivationDataOp {

	public static class NormalizedPhotoactivationDataResults {
		public ImageTimeSeries<FloatImage> normalizedPhotoactivationData;
		public FloatImage preactivationAverageImage;
		public FloatImage normalizedPostactivationImage;
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

	public NormalizedPhotoactivationDataResults generate(ImageTimeSeries<UShortImage> rawImageTimeSeries, ROI backgroundROI_2D, Integer indexPostactivation, boolean backgroundSubtract, boolean normalizedByPreactivation) throws Exception {
		
		UShortImage preactivationImage = rawImageTimeSeries.getAllImages()[0];
		ISize isize = preactivationImage.getISize();
		int nX = isize.getX();
		int nY = isize.getY();
		int nZ = isize.getZ();
		int numTimes = rawImageTimeSeries.getSizeT();
		Extent extent = preactivationImage.getExtent();
		org.vcell.util.Origin origin = preactivationImage.getOrigin();
		
		if (indexPostactivation == 0){
			throw new RuntimeException("no preactivation images found - indexOfFirstPostactivation is 0");
		}
		
		//
		// find average "dark count" in background of pre-activation images (for background subtraction)
		//
		float avgBackground = 0.0f;
		int numPreactivation = indexPostactivation;
		for (int i=0;i<numPreactivation;i++){
			avgBackground += getAverage(rawImageTimeSeries.getAllImages()[i], backgroundROI_2D);
		}
		avgBackground /= numPreactivation;
		
		//
		// find averaged preactivation image (corrected for background). 
		//
		int numPostactivationImages = numTimes-numPreactivation;
		float[] preactivationAveragePixels = new float[nX*nY*nZ]; // holds new averaged image pixels
		for (int i=0;i<numPreactivation;i++){
			short[] currPreactivationImage = rawImageTimeSeries.getAllImages()[i].getPixels();
			for (int j=0;j<isize.getXYZ();j++){
				float intPixel = 0x0000ffff & ((int)currPreactivationImage[j]);
				preactivationAveragePixels[j] += intPixel/numPreactivation;
			}
		}
		FloatImage preactivationAverageImage = new FloatImage(preactivationAveragePixels,origin,extent,nX,nY,nZ);
		UShortImage firstPostactivationImage = rawImageTimeSeries.getAllImages()[indexPostactivation];
		
		//
		// create normalized dataset 
		//
		// normalized postbleach = (origPostbleach - background)/(prebleach - background)
		//
		FloatImage[] normalizedImages = new FloatImage[numPostactivationImages];
		double[] postactivationTimeStamps = new double[numPostactivationImages];
		for (int i=0;i<numPostactivationImages;i++){
			double[] origTimeStamps = rawImageTimeSeries.getImageTimeStamps();
			postactivationTimeStamps[i] = origTimeStamps[indexPostactivation + i] - origTimeStamps[indexPostactivation]; 
			float[] normalizedPixels = new float[isize.getXYZ()];
			normalizedImages[i] = new FloatImage(normalizedPixels, origin, extent, nX, nY, nZ);
			short[] uncorrectedPixels = rawImageTimeSeries.getAllImages()[i+indexPostactivation].getPixels();
			for (int j=0;j<isize.getXYZ();j++){
				int intUncorrectedPixel = 0x0000ffff & ((int)uncorrectedPixels[j]);
		
				float background = 0;
				if (backgroundSubtract){
					background = avgBackground;
				}
				if (normalizedByPreactivation){
					int intPreactivationAvgPixel = 0x0000ffff & ((int)preactivationAveragePixels[j]);
					normalizedPixels[j] = (intUncorrectedPixel - background)/(Math.max(1, intPreactivationAvgPixel - background));
				}else{
					normalizedPixels[j] = (intUncorrectedPixel - background);
				}
			}
			normalizedImages[i] = new FloatImage(normalizedPixels,origin,extent,nX,nY,nZ);
		}
		ImageTimeSeries<FloatImage> normalizedData = new ImageTimeSeries<FloatImage>(FloatImage.class, normalizedImages, postactivationTimeStamps, nZ);
		
		NormalizedPhotoactivationDataResults results = new NormalizedPhotoactivationDataResults();
		
		results.normalizedPhotoactivationData = normalizedData;
		results.preactivationAverageImage = preactivationAverageImage;
		results.normalizedPostactivationImage = normalizedData.getAllImages()[0];
		
		return results;
	}

}
