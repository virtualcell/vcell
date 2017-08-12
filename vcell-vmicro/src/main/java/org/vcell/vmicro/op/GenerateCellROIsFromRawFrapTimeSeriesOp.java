package org.vcell.vmicro.op;

import java.util.Arrays;

import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.Image.ImageStatistics;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateCellROIsFromRawFrapTimeSeriesOp {
	
	public static class GeometryRoisAndBleachTiming {
		public ROI cellROI_2D;
		public ROI backgroundROI_2D;
		public int indexOfFirstPostbleach;
	}

	public GeometryRoisAndBleachTiming generate(ImageTimeSeries rawTimeSeriesImages, double cellThreshold) throws Exception {
		
		Image[] allImages = rawTimeSeriesImages.getAllImages();
		int numPixels = allImages[0].getNumXYZ();
		int numTimes = allImages.length;

		ImageStatistics[] imageStats = new ImageStatistics[numTimes];
		for (int i=0;i<numTimes;i++){
			imageStats[i] = allImages[i].getImageStatistics();
		}
		
		//
		// find largest change in fluorescence (ratio of total[n]/total[n+1]
		//
		int indexPostbleach = -1;
		double largestRatio = 0.0;
		for (int tIndex = 0; tIndex < numTimes-1; tIndex++){
			double currentRatio = imageStats[tIndex].meanValue/imageStats[tIndex+1].meanValue;
			if (currentRatio > largestRatio){
				largestRatio = currentRatio;
				indexPostbleach = tIndex+1;
			}
		}
		
		double[] firstImagePixels = allImages[0].getDoublePixels();
		short[] scaledCellDataShort = new short[numPixels];
		short[] scaledBackgoundDataShort = new short[numPixels];
		short[] wholeDomainDataShort = new short[numPixels];
		
		//
		// find cell and background by thresholding the first image
		//
		double cellThresholdValue = cellThreshold*imageStats[0].maxValue;
		for (int j = 0; j < numPixels; j++) {
			boolean isCell = firstImagePixels[j] > cellThresholdValue;
			if(isCell) {
				scaledCellDataShort[j]= 1;
			}else{
				scaledBackgoundDataShort[j]= 1;
			}
		}
		
		UShortImage cellImage =
			new UShortImage(
				scaledCellDataShort,
				allImages[0].getOrigin(),
				allImages[0].getExtent(),
				allImages[0].getNumX(),allImages[0].getNumY(),allImages[0].getNumZ());
		UShortImage backgroundImage =
				new UShortImage(
						scaledBackgoundDataShort,
						allImages[0].getOrigin(),
						allImages[0].getExtent(),
						allImages[0].getNumX(),allImages[0].getNumY(),allImages[0].getNumZ());
		
		
		Arrays.fill(wholeDomainDataShort, (short)1);
		UShortImage wholeDomainImage = 
				new UShortImage(
					wholeDomainDataShort,
					allImages[0].getOrigin(),
					allImages[0].getExtent(),
					allImages[0].getNumX(),allImages[0].getNumY(),allImages[0].getNumZ());
		
		UShortImage reverseCell = UShortImage.fastDilate(cellImage, 15, wholeDomainImage);
		reverseCell.reverse();
		reverseCell.and(backgroundImage);
		backgroundImage = reverseCell;
		
		ROI cellROI = new ROI(cellImage,"cellROI");
		ROI backgroundROI = new ROI(backgroundImage,"backgroundROI");
		
		GeometryRoisAndBleachTiming results = new GeometryRoisAndBleachTiming();
		results.cellROI_2D = cellROI;
		results.backgroundROI_2D = backgroundROI;
		results.indexOfFirstPostbleach = indexPostbleach;
		
		return results;
	}

}
