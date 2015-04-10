package org.vcell.vmicro.op;

import org.vcell.util.ClientTaskStatusSupport;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;

public class ComputeMeasurementErrorOp {
	
	public RowColumnResultSet computeNormalizedMeasurementError(ROI[] rois, int indexPostbleach, UShortImage[] rawImages, double[] timeStamps, FloatImage prebleachAvgImage, final ClientTaskStatusSupport clientTaskStatusSupport) {
		
		double[][] sigma = refreshNormalizedMeasurementError(rawImages, timeStamps, prebleachAvgImage, rois, indexPostbleach);
		String[] columnNames = new String[rois.length+1];
		columnNames[0] = "t";
		for (int i=0; i<rois.length; i++){
			columnNames[i+1] = rois[i].getROIName();
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
	double[][] refreshNormalizedMeasurementError(UShortImage[] rawImages, double[] timeStamps, FloatImage prebleachAverage, ROI[] rois, int indexFirstPostbleach) {
		int startIndexRecovery = indexFirstPostbleach;
		int roiLen = rois.length;
		double[][] sigma = new double[roiLen][timeStamps.length - startIndexRecovery];
		double[] prebleachAvg = prebleachAverage.getDoublePixels();
		
		for(int roiIdx=0; roiIdx<roiLen; roiIdx++)
		{
			ROI roi = rois[roiIdx];
			short[] roiData = roi.getPixelsXYZ();
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
		//for debug purpose
//		for(int timeIdx = startIndexRecovery; timeIdx < timeStamp.length; timeIdx++)
//		{
//			String value = sigma[FRAPData.VFRAP_ROI_ENUM.ROI_CELL.ordinal()][timeIdx-startIndexRecovery]+"\t"+
//			sigma[FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.ordinal()][timeIdx-startIndexRecovery];
//			System.out.println(value);
//		}
		return sigma;
	}
	

}
