package org.vcell.vmicro.workflow;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;

public class ComputeMeasurementError extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawImageTimeSeries;
	public final DataInput<FloatImage> prebleachAverage;
	public final DataInput<ROI[]> imageDataROIs;
	public final DataInput<Integer> indexFirstPostbleach;
	//
	// outputs
	//
	public final DataHolder<RowColumnResultSet> normalizedMeasurementError;
	
	public ComputeMeasurementError(String id){
		super(id);
		rawImageTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawImageTimeSeries",this);
		prebleachAverage = new DataInput<FloatImage>(FloatImage.class,"prebleachAverage",this);
		imageDataROIs = new DataInput<ROI[]>(ROI[].class,"imageDataROIs",this);
		indexFirstPostbleach = new DataInput<Integer>(Integer.class,"indexFirstPostbleach",this);
		normalizedMeasurementError = new DataHolder<RowColumnResultSet>(RowColumnResultSet.class,"normalizedMeasurmentError",this);
		addInput(rawImageTimeSeries);
		addInput(prebleachAverage);
		addInput(imageDataROIs);
		addInput(indexFirstPostbleach);
		addOutput(normalizedMeasurementError);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		
		ROI[] rois = imageDataROIs.getData();
		int indexPostbleach = indexFirstPostbleach.getData();
		ImageTimeSeries<UShortImage> rawImageDataset = rawImageTimeSeries.getData();
		FloatImage prebleachAvgImage = prebleachAverage.getData();
		
		double[][] sigma = refreshNormalizedMeasurementError(rawImageDataset, prebleachAvgImage, rois, indexPostbleach);
		String[] columnNames = new String[rois.length+1];
		columnNames[0] = "t";
		for (int i=0; i<rois.length; i++){
			columnNames[i+1] = rois[i].getROIName();
		}
		RowColumnResultSet rowColumnResultSet = new RowColumnResultSet(columnNames);
		double[] allTimePoints = rawImageDataset.getImageTimeStamps();
		for (int time=0; time<(allTimePoints.length-indexPostbleach); time++){
			double[] rowValues = new double[rois.length+1];
			rowValues[0] = allTimePoints[time+indexPostbleach];
			for (int col=0; col<rois.length; col++){
				rowValues[col+1] = sigma[col][time]; 
			}
			rowColumnResultSet.addRow(rowValues);
		}
		
		normalizedMeasurementError.setData(rowColumnResultSet);
	}

	/*
	 * Calculate Measurement error for data that is normalized 
	 * and averaged at each ROI ring.
	 * The first dimension is ROI rings(according to the Enum in FRAPData)
	 * The second dimension is time points (from starting index to the end) 
	 */
	public double[][] refreshNormalizedMeasurementError(ImageTimeSeries<UShortImage> rawImageTimeSeries, FloatImage prebleachAverage, ROI[] rois, int indexFirstPostbleach) throws Exception
	{
		double[] timeStamp = rawImageTimeSeries.getImageTimeStamps();
		int startIndexRecovery = indexFirstPostbleach;
		int roiLen = rois.length;
		double[][] sigma = new double[roiLen][timeStamp.length - startIndexRecovery];
		double[] prebleachAvg = prebleachAverage.getDoublePixels();
		
		for(int roiIdx=0; roiIdx<roiLen; roiIdx++)
		{
			ROI roi = rois[roiIdx];
			short[] roiData = roi.getPixelsXYZ();
			for(int timeIdx = startIndexRecovery; timeIdx < timeStamp.length; timeIdx++)
			{
				double[] rawTimeData = rawImageTimeSeries.getAllImages()[timeIdx].getDoublePixels();
				if(roiData.length != rawTimeData.length || roiData.length != prebleachAvg.length || rawTimeData.length != prebleachAvg.length)
				{
					throw new Exception("ROI data and image data are not in the same length.");
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
