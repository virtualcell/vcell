package org.vcell.vmicro.workflow.data;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.ROI;

public class ROISampleFunction implements SampleFunction {
	
	private final ROI roi;
	private final short[] roiBinaryPixels;
	
	public ROISampleFunction(ROI roi){
		if (roi.getISize().getZ()>1){
			throw new RuntimeException("ROISampleFunction doesn't yet support 3D ROIs");
		}
		this.roi = roi;
		this.roiBinaryPixels = roi.getBinaryPixelsXYZ(1);
	}

	@Override
	public String getName() {
		return roi.getROIName();
	}

	@Override
	public double average(Image image) {
		float[] floatPixels = getFloatPixels(image);
		return integrate0(floatPixels,true);
	}

	@Override
	public double integrate(Image image) {
		float[] floatPixels = getFloatPixels(image);
		return integrate0(floatPixels,false);
	}

	private float[] getFloatPixels(Image image) {
		if (!image.getISize().compareEqual(roi.getISize())){
			throw new RuntimeException("expecting ROI ("+roi.getISize().toString()+") to be same size as image ("+image.getISize()+")");
		}
		float[] floatPixels = null;
		if (image instanceof FloatImage){
			floatPixels = ((FloatImage)image).getPixels();
		}else{
			floatPixels = image.getFloatPixels();
		}
		return floatPixels;
	}

	private double integrate0(float[] simDataPixels, boolean bAverage) {
		double total = 0.0;
		int count = 0;
		for (int p=0; p<simDataPixels.length; p++){
			if (roiBinaryPixels[p] != 0){
				count++;
				total += simDataPixels[p];
			}
		}
		if (count==0){
			throw new RuntimeException("roi \""+roi.getROIName()+"\" has zero pixels");
		}
		if (bAverage){
			return total/count;
		}else{
			return total;
		}
	}

}
