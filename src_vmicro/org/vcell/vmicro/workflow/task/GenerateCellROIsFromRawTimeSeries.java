package org.vcell.vmicro.workflow.task;

import java.util.Arrays;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.Image.ImageStatistics;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateCellROIsFromRawTimeSeries extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawTimeSeriesImages;
	public final DataInput<Double> cellThreshold;
	
	//
	// outputs
	//
	public final DataHolder<ROI> cellROI_2D;
	public final DataHolder<ROI> backgroundROI_2D;
	public final DataHolder<Integer> indexOfFirstPostbleach;
	

	public GenerateCellROIsFromRawTimeSeries(String id){
		super(id);
		rawTimeSeriesImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		cellThreshold = new DataInput<Double>(Double.class,"cellThreshold",this);
		cellROI_2D = new DataHolder<ROI>(ROI.class,"cellROI_2D",this);
		backgroundROI_2D = new DataHolder<ROI>(ROI.class,"backgroundROI_2D",this);
		indexOfFirstPostbleach = new DataHolder<Integer>(Integer.class,"indexOfFirstPostbleach",this);
		addInput(rawTimeSeriesImages);
		addInput(cellThreshold);
		addOutput(cellROI_2D);
		addOutput(backgroundROI_2D);
		addOutput(indexOfFirstPostbleach);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		
		Image[] allImages = rawTimeSeriesImages.getData().getAllImages();
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
		double cellThresholdValue = cellThreshold.getData()*imageStats[0].maxValue;
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
		
		if (clientTaskStatusSupport != null){
			clientTaskStatusSupport.setProgress(100);
		}
		
		ROI cellROI = new ROI(cellImage,"cellROI");
		ROI backgroundROI = new ROI(backgroundImage,"backgroundROI");
		
		cellROI_2D.setData(cellROI);
		backgroundROI_2D.setData(backgroundROI);
		
		indexOfFirstPostbleach.setData(indexPostbleach);
	}

}
