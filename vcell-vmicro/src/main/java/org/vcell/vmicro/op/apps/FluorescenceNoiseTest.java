package org.vcell.vmicro.op.apps;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.vmicro.op.BrownianDynamics2DSolver;
import org.vcell.vmicro.op.display.DisplayImageOp;
import org.vcell.vmicro.op.display.DisplayTimeSeriesOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction.SampleStatistics;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class FluorescenceNoiseTest {

	private static final int NUM_TRIALS = 1000;
	private static final double MEAN_INTENSITY = 100;

	public static void main(String[] args) {
		try {
			FluorescenceNoiseTest test = new FluorescenceNoiseTest();
//			test.testBrownianDynamics();
			if (args.length != 2){
				System.out.println("usage: FluorescenceNoiseTest noise convolve\n"
						+ "   where:\n"
						+ "     noise (true|false) 'true' samples poisson distribution for each particle, 'false' counts particles\n"
						+ "     convolve (true|false) 'true' convolves with PSF, 'false' bins particles");
				System.exit(1);
			}
			Boolean bNoise = Boolean.parseBoolean(args[0]);
			Boolean bConvolve = Boolean.parseBoolean(args[1]);
			test.testImageTimeSeries(bNoise, bConvolve);
//			test.doit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testImageTimeSeries(boolean bNoise, boolean bConvolve) throws ImageException, IOException{
		Origin origin = new Origin(0,0,0);
		Extent extent = new Extent(10,10,1);
		int numX = 30;
		int numY = 30;
		int numParticles = 40000; // typically 10000 or 100000;
		double diffusionRate = 1;
		double bleachRadius = 5*5;
		double psfVar = 0.6*0.6;
		ImageTimeSeries<UShortImage> rawTimeSeries = generateTestData(origin, extent, numX, numY, numParticles, diffusionRate, psfVar, bleachRadius, bNoise, bConvolve);
		new DisplayTimeSeriesOp().displayImageTimeSeries(rawTimeSeries, "time series", null);
	}
	
	private ImageTimeSeries<UShortImage> generateTestData(Origin origin, Extent extent, int numX, int numY, int numParticles, double diffusionRate, double psfVar, double bleachRadius, boolean bNoise, boolean bConvolve) throws ImageException{
		double detectorGainAndQuantumYeild = 100;
		double totalSampleTime = 0.5;
		int numSampleSteps = 1;
		int numPrebleach = 2;
		
		ArrayList<UShortImage> images = new ArrayList<UShortImage>();
		ArrayList<Double> times = new ArrayList<Double>();
		
		final BrownianDynamics2DSolver solver = new BrownianDynamics2DSolver(origin, extent, numParticles);
		boolean initiallyFluorescent = true;
		solver.initializeUniform(initiallyFluorescent);
		
		// simulate and write prebleach images
		for (int i=0;i<numPrebleach;i++){
			images.add(solver.sampleImage(numX, numY, origin, extent, diffusionRate, totalSampleTime, numSampleSteps, psfVar, detectorGainAndQuantumYeild, bConvolve, bNoise));
			times.add(solver.currentTime());
		}
		
		
		// bleach
		double totalBleachTime = 0.1;
		int numBleachSteps = 5;
		double bleachCenterX = origin.getX() + 0.5*extent.getX();
		double bleachCenterY = origin.getY() + 0.5*extent.getY();
		double bleachPsfVariance = bleachRadius;
		double bleachFactor_K = 10;
		solver.bleachGuassian(diffusionRate, totalBleachTime, numBleachSteps, bleachCenterX, bleachCenterY, bleachPsfVariance, bleachFactor_K);

		// wait
		double postbleachDelay = 0.1;
		solver.step(diffusionRate, postbleachDelay);
		
		// sample postbleach for 10 seconds

		while (solver.currentTime()<5){
			double samplePsfVariance = psfVar;
			images.add(solver.sampleImage(numX, numY, origin, extent, diffusionRate, totalSampleTime, numSampleSteps, psfVar, detectorGainAndQuantumYeild, bConvolve, bNoise));
			times.add(solver.currentTime());
//			solver.step(diffusionRate, 1-totalSampleTime);
		}
		
		double[] timeArray = new double[times.size()];
		for (int i=0; i<timeArray.length; i++){
			timeArray[i] = times.get(i);
		}
		UShortImage[] imageArray = images.toArray(new UShortImage[images.size()]);
		
		return new ImageTimeSeries<UShortImage>(UShortImage.class, imageArray, timeArray, 1);
	}
	
	private void testBrownianDynamics() throws ImageException{
		Origin origin = new Origin(0,0,0);
		Extent extent = new Extent(10,10,1);
		double psfVar = 0.25*0.25;
		int numParticles = 10000; // typically 10000 or 100000;
		double diffusionRate = 1;
		double bleachRadius = 0.25;
		final double bleachAmplitude = 0.5;
		final double bleachRadius2 = bleachRadius*bleachRadius;
		double endTime = 10;
		int numTimes = 1000;
		
		
		final BrownianDynamics2DSolver solver = new BrownianDynamics2DSolver(origin, extent, numParticles);
		boolean initiallyFluorescent = true;
		solver.initializeUniform(initiallyFluorescent);
		double totalBleachTime = 0.00001;
		int numBleachSteps = 1;
		double bleachCenterX = origin.getX() + 0.5*extent.getX();
		double bleachCenterY = origin.getY() + 0.5*extent.getY();
		double bleachPsfVariance = psfVar;
		double bleachFactor_K = 1000000;
		solver.bleachGuassian(diffusionRate, totalBleachTime, numBleachSteps, bleachCenterX, bleachCenterY, bleachPsfVariance, bleachFactor_K);
//		ParticleVisitor bleachGaussian = new ParticleVisitor() {
//			UniformRealDistribution uniform = new UniformRealDistribution();
//			
//			@Override
//			public void visit(double x, double y, boolean bFluorescent, int index) {
//				// TODO Auto-generated method stub
//				double radius2 = x*x + y*y;
//				double bleachProb = bleachAmplitude * FastMath.exp(-radius2/bleachRadius2);
//				if (uniform.sample() < bleachProb){
//					solver.setFluorescence(index,false);
//				}else{
//					solver.setFluorescence(index,true);
//				}
//			}
//		};
//		solver.visit(bleachGaussian);
		double totalSampleTime = 0.1;
		int numSampleSteps = 1;
		double samplePsfVariance = psfVar;
		double detectorGainAndQuantumYeild = 100;
		boolean bNoise = true;
		boolean bConvolve = true;
		UShortImage sampledImage = solver.sampleImage(100, 100, origin, extent, diffusionRate, totalSampleTime, numSampleSteps, samplePsfVariance, detectorGainAndQuantumYeild, bConvolve, bNoise);
		new DisplayImageOp().displayImage(sampledImage, "initialBleach", null);
	}
	
	private void doit() throws ImageException{
		int[] sizes = new int[] { 1,2,4,8,16,32,64,128,256 };
		for (int imageSize : sizes){
			ISize size = new ISize(imageSize,imageSize,1);
			Extent extent = new Extent(1,1,1);
			Origin origin = new Origin(0,0,0);
//			NormalizedSampleFunction sampleFunction = NormalizedSampleFunction.createUniform("uniformROI", origin, extent, size);
			NormalizedSampleFunction sampleFunction = NormalizedSampleFunction.fromGaussian("testGaussian", origin, extent, size, 0.5, 0.2, 0.1);
			SampleStatistics[] samples = new SampleStatistics[NUM_TRIALS];
			for (int i=0;i<NUM_TRIALS;i++){
				UShortImage rawImage = getUniformFluorescenceImage(size, extent, origin, MEAN_INTENSITY);
				samples[i] = sampleFunction.sample(rawImage);
			}
			Mean mean = new Mean();
			Variance var = new Variance();
			double[] weightedMeans = getWeightedMeans(samples);
			double[] weightedVariances = getWeightedVariances(samples);
			double weightedMeansVariance = var.evaluate(weightedMeans);
			double weightedMeansMean = mean.evaluate(weightedMeans);
			double weightedVarVariance = var.evaluate(weightedVariances);
			double weightedVarMean = mean.evaluate(weightedVariances);
			double V1 = samples[0].sumOfWeights;
			double V2 = samples[0].sumOfWeightsSquared;
			System.out.println("image is "+imageSize+"x"+imageSize+", V1="+V1+", V2="+V2+", numTrials="+NUM_TRIALS+", sample means (mu="+weightedMeansMean+",s="+weightedMeansVariance+"), sample variances (mu="+weightedVarMean+",s="+weightedVarVariance);
		}
	}
	
	private double[] getWeightedMeans(SampleStatistics[] sampleStats){
		double[] values = new double[sampleStats.length];
		for (int i=0;i<sampleStats.length;i++){
			values[i] = sampleStats[i].weightedMean/sampleStats[i].sumOfWeights;
		}
		return values;
	}

	private double[] getWeightedVariances(SampleStatistics[] sampleStats){
		double[] variances = new double[sampleStats.length];
		for (int i=0;i<sampleStats.length;i++){
			variances[i] = sampleStats[i].weightedVariance/sampleStats[i].sumOfWeightsSquared;
		}
		return variances;
	}

	private UShortImage getUniformFluorescenceImage(ISize size, Extent extent, Origin origin, double mean) throws ImageException {
		ExponentialDistribution expDistribution = new ExponentialDistribution(mean);
		short[] shortPixels = new short[size.getXYZ()];
		for (int i=0;i<shortPixels.length;i++){
			// consider that we have unsigned short (must use 32 bit integer first)
			double sample = expDistribution.sample();
			if (sample > 65535){
				sample = 65535;
				System.err.println("sample "+sample+" overflows 16 bit unsigned");
			}
			shortPixels[i] = (short)(0xffff&((int)Math.round(sample)));
		}
		UShortImage rawImage = new UShortImage(shortPixels, origin, extent, size.getX(), size.getY(), size.getZ());
		return rawImage;
	}

}
