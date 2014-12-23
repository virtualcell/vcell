package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.RowColumnResultSet;

public class GenerateReducedRefData extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> imageTimeSeries;
	public final DataInput<ROI[]> imageDataROIs;
	//
	// outputs
	//
	public final DataOutput<RowColumnResultSet> reducedROIData;
	

	public GenerateReducedRefData(String id){
		super(id);
		imageTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"imageTimeSeries", this);
		imageDataROIs = new DataInput<ROI[]>(ROI[].class,"imageDataROIs", this);
		reducedROIData = new DataOutput<RowColumnResultSet>(RowColumnResultSet.class,"reducedROIData",this);
		addInput(imageTimeSeries);
		addInput(imageDataROIs);
		addOutput(reducedROIData);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		ROI[] rois = context.getData(imageDataROIs);
		int numROIs = rois.length;
		
		ImageTimeSeries<FloatImage> simData = context.getData(imageTimeSeries);
		int numTimes = simData.getSizeT();
		int numPixels = simData.getISize().getXYZ();
		
		String[] roiNames = new String[numROIs+1];
		roiNames[0] = "t";
		for (int i=0; i<numROIs; i++){
			roiNames[i+1] = rois[i].getROIName();
		}
		RowColumnResultSet reducedData = new RowColumnResultSet(roiNames);
		
		for (int t=0;t<numTimes;t++){
			double[] row = new double[numROIs+1];
			row[0] = simData.getImageTimeStamps()[t];
			double[] simDataPixels = simData.getAllImages()[t].getDoublePixels();
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
		context.setData(reducedROIData,reducedData);
	}

}
