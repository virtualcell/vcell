package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.vcell.VirtualMicroscopy.ROI;

public class FitBleachSpot extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> normalizedImages;
	public final DataInput<ROI> bleachROI;
	//
	// outputs
	//
	public final DataOutput<Double> bleachRadius;
	public final DataOutput<Double> centerX;
	public final DataOutput<Double> centerY;
	

	public FitBleachSpot(String id){
		super(id);
		normalizedImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"normalizedImages", this);
		bleachROI = new DataInput<ROI>(ROI.class,"bleachROI", this);
		addInput(normalizedImages);
		addInput(bleachROI);

		bleachRadius = new DataOutput<Double>(Double.class,"bleachRadius",this);
		centerX = new DataOutput<Double>(Double.class,"centerX",this);
		centerY = new DataOutput<Double>(Double.class,"centerY",this);
		addOutput(bleachRadius);
		addOutput(centerX);
		addOutput(centerY);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		ROI roi = context.getData(bleachROI);
		
		// initialize guess by centroid of bleach ROI and total area of bleach ROI (assuming a circle of radius R)
		int nonzeroPixelsCount = roi.getNonzeroPixelsCount();
		ISize size = roi.getISize();
		if (size.getZ()>1){
			throw new RuntimeException("expecting 2D bleach region ROI");
		}
		
		short[] pixels = roi.getBinaryPixelsXYZ(1);
		long numPixels = 0;
		Extent extent = roi.getRoiImages()[0].getExtent();
		double totalX = 0.0;
		double totalY = 0.0;
		int pixelIndex = 0;
		for (int i=0;i<size.getX();i++){
			double x = extent.getX()*(i+0.5)/(size.getX()-1);
			for (int j=0;j<size.getY();j++){
				if (pixels[pixelIndex] != 0){
					double y = extent.getY()*(j+0.5)/(size.getY()-1);
					totalX += x;
					totalY += y;
					numPixels++;
				}
				pixelIndex++;
			}
		}
		Origin origin = roi.getRoiImages()[0].getOrigin();
		double roiCenterX = origin.getX() + totalX/numPixels;
		double roiCenterY = origin.getY() + totalY/numPixels;
		
		// Area = PI * R^2
		// R = sqrt(Area/PI)
		double roiBleachSpotArea = (extent.getX()*extent.getY()*numPixels)/(size.getX()*size.getY());
		double roiBleachRadiusValue = Math.sqrt(roiBleachSpotArea/Math.PI);
		
		//
		// do some optimization on the image (fitting to a Gaussian)
		//
		double fittedRadius = roiBleachRadiusValue;
		double fittedCenterX = roiCenterX;
		double fittedCenterY = roiCenterY;
		
		
		
		context.setData(bleachRadius,fittedRadius);
		context.setData(centerX,fittedCenterX);
		context.setData(centerY,fittedCenterY);
		
	}

}
