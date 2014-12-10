package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.Image.ImageStatistics;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class GenerateBleachROI extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> normalizedTimeSeries;
	public final DataInput<ROI> cellROI_2D;
	public final DataInput<Double> bleachThreshold;
	
	//
	// outputs
	//
	public final DataHolder<ROI> bleachedROI_2D;
	

	public GenerateBleachROI(String id){
		super(id);
		normalizedTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		bleachThreshold = new DataInput<Double>(Double.class,"bleachThreshold",this);
		cellROI_2D = new DataInput<ROI>(ROI.class,"cellROI_2D",this);
		bleachedROI_2D = new DataHolder<ROI>(ROI.class,"bleachedROI_2D",this);
		addInput(normalizedTimeSeries);
		addInput(cellROI_2D);
		addInput(bleachThreshold);
		addOutput(bleachedROI_2D);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		
		Image[] allImages = normalizedTimeSeries.getData().getAllImages();
		int numPixels = allImages[0].getNumXYZ();
		int numTimes = allImages.length;

		ImageStatistics[] imageStats = new ImageStatistics[numTimes];
		for (int i=0;i<numTimes;i++){
			imageStats[i] = allImages[i].getImageStatistics();
		}
		
		short[] scaledBleachedDataShort = new short[numPixels];
				
		short[] erodedCellUShort = cellROI_2D.getData().getRoiImages()[0].getBinaryPixels(1);
		double bleachThresholdValue = bleachThreshold.getData(); // input is already normalized to 1.0 ... if relative to max, then crazy values from outside cell can interfere.
		double[] firstPostbleachImage = allImages[0].getDoublePixels();
		for (int j = 0; j < numPixels; j++) {
			boolean isCell = (erodedCellUShort[j] == 1);
			boolean isBleach = firstPostbleachImage[j] < bleachThresholdValue;
			if (isCell && isBleach) {
				scaledBleachedDataShort[j] = 1;
			}
		}


		UShortImage bleachedImage =
			new UShortImage(
					scaledBleachedDataShort,
					allImages[0].getOrigin(),
					allImages[0].getExtent(),
					allImages[0].getNumX(),allImages[0].getNumY(),allImages[0].getNumZ());

		if (clientTaskStatusSupport != null){
			clientTaskStatusSupport.setProgress(100);
		}
		
		ROI bleachedROI = new ROI(bleachedImage,"bleachedROI");
		
		bleachedROI_2D.setData(bleachedROI);
	}

}
