package org.vcell.vmicro.workflow.data;

import org.apache.commons.math3.util.FastMath;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.Image;

public class Gaussian2DSampleFunction implements SampleFunction {

	private final String name;
	private final double muX;
	private final double muY;
	private final double var;
	
	private Gaussian2DSampleFunction(String name, double muX, double muY, double var) {
		this.name = name;
		this.muX = muX;
		this.muY = muY;
		this.var = var;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double average(Image image) {
		float[] floatPixels = getFloatPixels(image);
		return integrate0(floatPixels,image.getISize(),image.getOrigin(),image.getExtent(), true);
	}

	@Override
	public double integrate(Image image) {
		float[] floatPixels = getFloatPixels(image);
		return integrate0(floatPixels,image.getISize(),image.getOrigin(),image.getExtent(), false);
	}
	
	private float[] getFloatPixels(Image image) {
		if (image.getISize().getZ() > 1){
			throw new RuntimeException("expecting a 2D image ("+image.getISize()+")");
		}
		float[] floatPixels = null;
		if (image instanceof FloatImage){
			floatPixels = ((FloatImage)image).getPixels();
		}else{
			floatPixels = image.getFloatPixels();
		}
		return floatPixels;
	}

	private double integrate0(float[] simDataPixels, ISize size, Origin origin, Extent extent, boolean bAverage) {
//		int numX = size.getX();
//		int numY = size.getY();
//		double oX = origin.getX();
//		double oY = origin.getY();
//		double eX = extent.getX();
//		double eY = extent.getY();
//		double dX = eX/numX;
//		double dY = eY/numY;
//		
//		int count = 0;
//		double total = 0.0;
//		double integral = 0.0;
//		double pX = eX;
//		int 
//		for (int j=0;j<numY;j++){
//			for (int i=0;i<numX;i++){
//				double rX = (muX-pX);
//				double rY = (muY-pY);
//				double distanceSquared = rX*rX + rY*rY;
//				double g = FastMath.exp(-distanceSquared/var);
//				total += g*
//			}
//			pX += dX;
//		}
//		for (int p=0; p<simDataPixels.length; p++){
//			if (roiBinaryPixels[p] != 0){
//				count++;
//				total += simDataPixels[p];
//			}
//		}
//		if (count==0){
//			throw new RuntimeException("roi \""+roi.getROIName()+"\" has zero pixels");
//		}
//		if (bAverage){
//			return total/count;
//		}else{
//			return total;
//		}
		return 0;
	}

}
