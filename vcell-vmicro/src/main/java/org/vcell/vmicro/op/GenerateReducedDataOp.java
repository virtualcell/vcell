package org.vcell.vmicro.op;

import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction.SampleStatistics;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.RowColumnResultSet;

public class GenerateReducedDataOp {
	
	public RowColumnResultSet generateReducedData(ImageTimeSeries<? extends Image> imageTimeSeries, NormalizedSampleFunction[] rois) throws Exception {
		int numROIs = rois.length;
		
		int numTimes = imageTimeSeries.getSizeT();
		
		String[] roiNames = new String[numROIs+1];
		roiNames[0] = "t";
		for (int i=0; i<numROIs; i++){
			roiNames[i+1] = rois[i].getName();
		}
		RowColumnResultSet reducedData = new RowColumnResultSet(roiNames);
		
		for (int t=0;t<numTimes;t++){
			double[] row = new double[numROIs+1];
			row[0] = imageTimeSeries.getImageTimeStamps()[t];
			for (int r=0; r<numROIs; r++){
				SampleStatistics result = rois[r].sample(imageTimeSeries.getAllImages()[t]);
				row[r+1] = result.weightedMean;
			}
			reducedData.addRow(row);
		}
		return reducedData;
	}
	
	public RowColumnResultSet generateReducedData(ImageTimeSeries<? extends Image> imageTimeSeries, ROI[] rois) throws Exception {
		int numROIs = rois.length;
		
		int numTimes = imageTimeSeries.getSizeT();
		int numPixels = imageTimeSeries.getISize().getXYZ();
		
		String[] roiNames = new String[numROIs+1];
		roiNames[0] = "t";
		for (int i=0; i<numROIs; i++){
			roiNames[i+1] = rois[i].getROIName();
		}
		RowColumnResultSet reducedData = new RowColumnResultSet(roiNames);
		
		for (int t=0;t<numTimes;t++){
			double[] row = new double[numROIs+1];
			row[0] = imageTimeSeries.getImageTimeStamps()[t];
			double[] simDataPixels = imageTimeSeries.getAllImages()[t].getDoublePixels();
			for (int r=0; r<numROIs; r++){
				double average = 0.0;
				int count = 0;
				short[] roiPixels = rois[r].getBinaryPixelsXYZ(1);
				for (int p=0; p<numPixels; p++){
					if (roiPixels[p] != 0){
						count++;
						average += simDataPixels[p];
					}
				}
				if (count==0){
					throw new RuntimeException("roi \""+rois[r].getROIName()+"\" has zero pixels");
				}
				row[r+1] = average/count;
			}
			reducedData.addRow(row);
		}
		return reducedData;
	}

}
