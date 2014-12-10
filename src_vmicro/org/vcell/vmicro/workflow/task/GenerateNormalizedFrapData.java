package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateNormalizedFrapData extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawImageTimeSeries;
	public final DataInput<ROI> backgroundROI_2D;
	public final DataInput<Integer> indexOfFirstPostbleach;
	
	//
	// outputs
	//
	public final DataHolder<ImageTimeSeries> normalizedFrapData;
	public final DataHolder<FloatImage> prebleachAverage;
	

	public GenerateNormalizedFrapData(String id){
		super(id);
		rawImageTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawImageTimeSeries",this);
		backgroundROI_2D = new DataInput<ROI>(ROI.class,"backgroundROI_2D",this);
		indexOfFirstPostbleach = new DataInput<Integer>(Integer.class,"indexOfFirstPostbleach",this);
		normalizedFrapData = new DataHolder<ImageTimeSeries>(ImageTimeSeries.class,"normalizedFrapData",this);
		prebleachAverage = new DataHolder<FloatImage>(FloatImage.class,"prebleachAverage",this);
		addInput(rawImageTimeSeries);
		addInput(backgroundROI_2D);
		addInput(indexOfFirstPostbleach);
		addOutput(normalizedFrapData);
		addOutput(prebleachAverage);
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

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		ImageTimeSeries<UShortImage> rawTimeSeries = rawImageTimeSeries.getData();
		UShortImage firstImage = rawTimeSeries.getAllImages()[0];
		ISize isize = firstImage.getISize();
		int nX = isize.getX();
		int nY = isize.getY();
		int nZ = isize.getZ();
		int numTimes = rawTimeSeries.getSizeT();
		Extent extent = firstImage.getExtent();
		org.vcell.util.Origin origin = firstImage.getOrigin();
		
		Integer indexPostbleach = indexOfFirstPostbleach.getData();
		if (indexPostbleach == 0){
			throw new RuntimeException("no prebleach images found - indexOfFirstPostbleach is 0");
		}
		
		//
		// find average "dark count" in background of pre-bleach images (for background subtraction)
		//
		float avgBackground = 0.0f;
		int numPrebleach = indexPostbleach;
		for (int i=0;i<numPrebleach;i++){
			avgBackground += getAverage(rawTimeSeries.getAllImages()[i], backgroundROI_2D.getData());
		}
		avgBackground /= numPrebleach;
		
		//
		// find averaged prebleach image (corrected for background). 
		//
		int numPostbleachImages = numTimes-numPrebleach;
		float[] prebleachAveragePixels = new float[nX*nY*nZ]; // holds new averaged image pixels
		for (int i=0;i<numPrebleach;i++){
			short[] currPrebleachImage = rawTimeSeries.getAllImages()[i].getPixels();
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
			double[] origTimeStamps = rawImageTimeSeries.getData().getImageTimeStamps();
			postbleachTimeStamps[i] = origTimeStamps[indexPostbleach + i] - origTimeStamps[indexPostbleach]; 
			float[] normalizedPixels = new float[isize.getXYZ()];
			normalizedImages[i] = new FloatImage(normalizedPixels, origin, extent, nX, nY, nZ);
			short[] uncorrectedPixels = rawTimeSeries.getAllImages()[i+indexPostbleach].getPixels();
			for (int j=0;j<isize.getXYZ();j++){
				int intPixel = 0x0000ffff & ((int)uncorrectedPixels[j]);
				normalizedPixels[j] = (intPixel - avgBackground)/(Math.max(1, prebleachAveragePixels[j] - avgBackground));
			}
			normalizedImages[i] = new FloatImage(normalizedPixels,origin,extent,nX,nY,nZ);
		}
		ImageTimeSeries<FloatImage> normalizedData = new ImageTimeSeries<FloatImage>(FloatImage.class, normalizedImages, postbleachTimeStamps, nZ);
		
		normalizedFrapData.setData(normalizedData);
		this.prebleachAverage.setData(prebleachAverageImage);
		
	}

}
