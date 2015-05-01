package org.vcell.vmicro.op;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;

public class ComputeMeasurementErrorOp {
	
	public RowColumnResultSet computeNormalizedMeasurementError(NormalizedSampleFunction[] rois, int indexPostbleach, ImageTimeSeries<UShortImage> rawImageDataset, FloatImage prebleachAvgImage, final ClientTaskStatusSupport clientTaskStatusSupport) throws ImageException {
		
		UShortImage[] rawImages = rawImageDataset.getAllImages();
		double[] timeStamps = rawImageDataset.getImageTimeStamps();
		
		double[][] sigma = refreshNormalizedMeasurementError(rawImages, timeStamps, prebleachAvgImage, rois, indexPostbleach);
		String[] columnNames = new String[rois.length+1];
		columnNames[0] = "t";
		for (int i=0; i<rois.length; i++){
			columnNames[i+1] = rois[i].getName();
		}
		RowColumnResultSet rowColumnResultSet = new RowColumnResultSet(columnNames);
		double[] allTimePoints = timeStamps;
		for (int time=0; time<(allTimePoints.length-indexPostbleach); time++){
			double[] rowValues = new double[rois.length+1];
			rowValues[0] = allTimePoints[time+indexPostbleach];
			for (int col=0; col<rois.length; col++){
				rowValues[col+1] = sigma[col][time]; 
			}
			rowColumnResultSet.addRow(rowValues);
		}
		
		return rowColumnResultSet;
	}

	/*
	 * Calculate Measurement error for data that is normalized 
	 * and averaged at each ROI ring.
	 * The first dimension is ROI rings(according to the Enum in FRAPData)
	 * The second dimension is time points (from starting index to the end) 
	 */
	double[][] refreshNormalizedMeasurementError(UShortImage[] rawImages, double[] timeStamps, FloatImage prebleachAverage, NormalizedSampleFunction[] rois, int indexFirstPostbleach) throws ImageException {
		int startIndexRecovery = indexFirstPostbleach;
		int roiLen = rois.length;
		double[][] sigma = new double[roiLen][timeStamps.length - startIndexRecovery];
		double[] prebleachAvg = prebleachAverage.getDoublePixels();
		
		for(int roiIdx=0; roiIdx<roiLen; roiIdx++)
		{
			NormalizedSampleFunction roi = rois[roiIdx];
			short[] roiData = roi.toROI(1e-5).getPixelsXYZ();
			for(int timeIdx = startIndexRecovery; timeIdx < timeStamps.length; timeIdx++)
			{
				double[] rawTimeData = rawImages[timeIdx].getDoublePixels();
				if(roiData.length != rawTimeData.length || roiData.length != prebleachAvg.length || rawTimeData.length != prebleachAvg.length)
				{
					throw new RuntimeException("ROI data and image data are not in the same length.");
				}
				else
				{
					//loop through ROI
					int roiPixelCounter = 0;
					double sigmaVal = 0;
					for(int i = 0 ; i<roiData.length; i++)
					{
						if(roiData[i] != 0)
						{
							sigmaVal = sigmaVal + rawTimeData[i]/(prebleachAvg[i]*prebleachAvg[i]);
							roiPixelCounter ++;
						}
					}
					if(roiPixelCounter == 0)
					{
						sigmaVal = 0;
					}
					else
					{
						sigmaVal = Math.sqrt(sigmaVal)/roiPixelCounter;
					}
					sigma[roiIdx][timeIdx-startIndexRecovery] = sigmaVal;
				}
			}
		}
		return sigma;
	}
	

}
