package org.vcell.vmicro.workflow.data;

import java.util.Arrays;

import org.apache.commons.math3.util.FastMath;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.VirtualMicroscopy.Image.ImageStatistics;
import cbit.vcell.VirtualMicroscopy.ROI;

public class NormalizedSampleFunction {
	private final FloatImage sampleImage;
	private final ImageStatistics sampleImageStatistics;
	private final String name;
	
	private NormalizedSampleFunction(String name, FloatImage sampleImage){
		this.name = name;
		this.sampleImage = sampleImage;
		ImageStatistics stats = sampleImage.getImageStatistics();
		final double epsilon = 1e-6;
		if (Math.abs(stats.maxValue) < (1-epsilon) || Math.abs(stats.maxValue) > (1+epsilon)){
			throw new RuntimeException("NormalizedSampleFunction should have maximum value of 1.0, max is "+stats.maxValue);
		}
		this.sampleImageStatistics = stats;
	}
	
	public String getName(){
		return name;
	}
	public ISize getISize(){
		return sampleImage.getISize();
	}
	public Extent getExtent(){
		return sampleImage.getExtent();
	}
	public Origin getOrigin(){
		return sampleImage.getOrigin();
	}
	public double getFunctionIntegral(){
		return sampleImageStatistics.sum;
	}
	
	private float[] getFloatPixels(Image image) {
		if (!image.getISize().compareEqual(sampleImage.getISize())){
			throw new RuntimeException("sample function "+name+" size ("+sampleImage.getISize()+") doesn't match image ("+image.getISize()+")");
		}
		if (!image.getOrigin().compareEqual(sampleImage.getOrigin())){
			throw new RuntimeException("sample function "+name+" origin ("+sampleImage.getOrigin()+") doesn't match image ("+image.getOrigin()+")");
		}
		if (!image.getExtent().compareEqual(sampleImage.getExtent())){
			throw new RuntimeException("sample function "+name+" extent ("+sampleImage.getExtent()+") doesn't match image ("+image.getExtent()+")");
		}
		float[] floatPixels = null;
		if (image instanceof FloatImage){
			floatPixels = ((FloatImage)image).getPixels();
		}else{
			floatPixels = image.getFloatPixels();
		}
		return floatPixels;
	}
	
	public static class SampleStatistics {
		public final double weightedMean;
		public final double weightedVariance;
		public final double sumOfWeights;
		public final double sumOfWeightsSquared;

		private SampleStatistics(double weightedMean, double weightedVariance, double sumOfWeights, double sumOfWeightsSquared) {
			this.weightedMean = weightedMean;
			this.weightedVariance = weightedVariance;
			this.sumOfWeights = sumOfWeights;
			this.sumOfWeightsSquared = sumOfWeightsSquared;
		}
		
		public String toString(){
			return "stats(mu_weighted="+this.weightedMean+", var_weighted="+this.weightedVariance+", V1="+this.sumOfWeights+", V2="+this.sumOfWeightsSquared+")";
		}
	}

	public SampleStatistics sample(Image image){
		float[] imagePixels = getFloatPixels(image);
		float[] functionSamples = sampleImage.getPixels();
		//
		// weighted sample mean (see http://en.wikipedia.org/wiki/Weighted_arithmetic_mean)
		//
		double weightedMean = 0.0;
		for (int i=0; i<imagePixels.length; i++){
			double weight = functionSamples[i];
			double pixelIntensity = imagePixels[i];
			double unweightedPixelVariable = pixelIntensity; // due to poisson statistics of photons, variance equals mean
			weightedMean += pixelIntensity*weight; // weighted mean
		}
		//
		// biased weighted sample variance
		//
		double sumOfWeights = 0.0;
		double sumOfWeightsSquared = 0.0;
		for (int i=0; i<imagePixels.length; i++){
			double weight = functionSamples[i];
			sumOfWeightsSquared += weight*weight;
			sumOfWeights += weight;
		}
		
		double weightedVariance = weightedMean; // assume Poisson statistics, variance is mean
		// normalize 1
		weightedVariance = weightedVariance / (sumOfWeights-sumOfWeightsSquared/sumOfWeights) * (weightedMean/sumOfWeights);
		// normalize 2
//		weightedVariance = weightedVariance * (weightedMean/sumOfWeights);
		return new SampleStatistics(weightedMean, weightedVariance, sumOfWeights, sumOfWeightsSquared);
	}
	
	public ROI toROI(double threshold) {
		short[] pixels = sampleImage.getBinaryPixels((float)threshold);
		UShortImage roiImage;
		try {
			roiImage = new UShortImage(pixels,getOrigin(),getExtent(),getISize().getX(),getISize().getY(),1);
		} catch (ImageException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create image: "+e.getMessage(),e);
		}
		ROI roi = new ROI(roiImage, getName());
		return roi;
	}
	
	public static NormalizedSampleFunction fromROI(ROI roi) throws ImageException{
		if (roi.getISize().getZ()>1){
			throw new RuntimeException("ROISampleFunction doesn't yet support 3D ROIs");
		}
		float[] normalizedPixels = new float[roi.getISize().getXYZ()];
		int nonzeroPixelCount = roi.getNonzeroPixelsCount();
		if (nonzeroPixelCount == 0){
			throw new RuntimeException("empty ROI \""+roi.getROIName()+"\"");
		}
		float pixelValue = 1.0f;
		for (int i=0;i<normalizedPixels.length;i++){
			if (roi.getRoiImages()[0].getPixels()[i] > 0){
				normalizedPixels[i] = pixelValue;
			}
		}
		UShortImage roiImage = roi.getRoiImages()[0];
		FloatImage sample = new FloatImage(normalizedPixels,roiImage.getOrigin(),roiImage.getExtent(),roi.getISize().getX(),roi.getISize().getY(),roi.getISize().getZ());
		return new NormalizedSampleFunction(roi.getROIName(), sample);
	}
	
	public static NormalizedSampleFunction fromGaussian(String name, Origin origin, Extent extent, ISize size, double muX, double muY, double sigma) throws ImageException{
		if (size.getZ()>1){
			throw new RuntimeException("NormalizedSampleFunction doesn't yet support 3D functions");
		}
		double var = sigma*sigma;
		int numX = size.getX();
		int numY = size.getY();
		double oX = origin.getX();
		double oY = origin.getY();
		double eX = extent.getX();
		double eY = extent.getY();
		float[] pixels = new float[size.getXYZ()];
		
		if (sigma< eX/numX ){
			throw new RuntimeException("psf not properly sampled, sigma="+sigma+", pixelSize="+(eX/numX));
		}
		
		//
		// set unnormalized Gaussian (don't bother dividing by 2*pi*sigma, need the sampled version to sum to 1.0 anyway).
		// record sum for normalization
		//
		int index = 0;
		double sum = 0.0;
		double max = Double.NEGATIVE_INFINITY;
		for (int j=0;j<numY;j++){
			double y = (j+0.5)*eY/(numY-1);
			double rY_squared = (y-muY)*(y-muY);
			for (int i=0;i<numX;i++){
				double x = (i+0.5)*eX/(numX-1);
				double rX_squared = (x-muX)*(x-muX);
				double distanceSquared = rX_squared + rY_squared;
				double unnormalizedGaussian = FastMath.exp(-distanceSquared/var);
				sum += unnormalizedGaussian;
				max = Math.max(unnormalizedGaussian,max);
				pixels[index++] = (float)unnormalizedGaussian;
			}
		}
		//
		// normalize so that sampled image sums to one
		//
		if (sum <= 0.0){
			throw new RuntimeException("Gaussian sample summed to "+sum+", unexpected - cannot normalize");
		}
		for (index=0; index<pixels.length; index++){
			pixels[index] /= max;
		}
		FloatImage sample = new FloatImage(pixels, origin, extent, size.getX(), size.getY(), size.getZ());
		return new NormalizedSampleFunction(name, sample);
	}
	
	public static NormalizedSampleFunction createUniform(String name, Origin origin, Extent extent, ISize size) throws ImageException{
		if (size.getZ()>1){
			throw new RuntimeException("NormalizedSampleFunction doesn't yet support 3D functions");
		}
		float[] pixels = new float[size.getXYZ()];
		Arrays.fill(pixels, 1.0f);
//		double sum = size.getXYZ(); // each element is initially 1.0f
//		//
//		// normalize so that sampled image sums to one
//		//
//		for (int index=0; index<pixels.length; index++){
//			pixels[index] /= sum;
//		}
		FloatImage sample = new FloatImage(pixels, origin, extent, size.getX(), size.getY(), size.getZ());
		return new NormalizedSampleFunction(name, sample);
	}
}
