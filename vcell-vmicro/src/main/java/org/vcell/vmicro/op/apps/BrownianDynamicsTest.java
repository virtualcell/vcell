package org.vcell.vmicro.op.apps;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.vmicro.op.BrownianDynamics2DSolver;
import org.vcell.vmicro.op.ExportRawTimeSeriesToVFrapOp;
import org.vcell.vmicro.op.GenerateReducedDataOp;
import org.vcell.vmicro.op.display.DisplayPlotOp;
import org.vcell.vmicro.op.display.DisplayTimeSeriesOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.CSV;
import cbit.vcell.math.RowColumnResultSet;

public class BrownianDynamicsTest {

	private static final String OPTION_DIFFUSION = "diffusion";
	private static final String OPTION_NUM_PARTICLES = "numParticles";
	private static final String OPTION_IMAGE_SIZE = "imageSize";
	private static final String OPTION_EXTENT = "extent";
	private static final String OPTION_DISPLAY_PLOT = "displayPlot";
	private static final String OPTION_DISPLAY_IMAGE = "displayImage";
	private static final String OPTION_IMAGEFILE = "imageFile";
	private static final String OPTION_PLOTFILE = "plotFile";
	private static final String OPTION_PSF = "psf";
	private static final String OPTION_NOISE = "noise";
	private static final String OPTION_BLEACH_RADIUS = "bleachRadius";
	private static final String OPTION_BLEACH_DURATION = "bleachDuration";
	private static final String OPTION_PSF_RADIUS = "psfRadius";
	private static final String OPTION_ENDTIME = "endTime";
	
	private static final double DEFAULT_DIFFUSION = 2;
	private static final double DEFAULT_BLEACH_RADIUS = 2;
	private static final double DEFAULT_BLEACH_DURATION = 0.1;
	private static final double DEFAULT_EXTENT_SCALE = 50;
	private static final int DEFAULT_IMAGE_SIZE = 50;
	private static final double DEFAULT_PSF_RADIUS = DEFAULT_EXTENT_SCALE/DEFAULT_IMAGE_SIZE*1.2;
	private static final int DEFAULT_NUM_PARTICLES = 40000;
	private static final double DEFAULT_ENDTIME = 5;
	

	public static void main(String[] args) {
		try {
			Options commandOptions = new Options();
			
			Option noiseOption = new Option(OPTION_NOISE,false,"sampled images use Poisson statistics for photons, default is to count particles");
			commandOptions.addOption(noiseOption);
			
			Option psfOption = new Option(OPTION_PSF,false,"sampled images are convolve with microscope psf, default is to bin");
			commandOptions.addOption(psfOption);
			
			Option imageFileOption = new Option(OPTION_IMAGEFILE,true,"file to store image time series");
			imageFileOption.setArgName("filename");
			imageFileOption.setValueSeparator('=');
			commandOptions.addOption(imageFileOption);
			
			Option plotFileOption = new Option(OPTION_PLOTFILE,true,"file to store CSV time series (reduced data for ROIs)");
			plotFileOption.setArgName("filename");
			plotFileOption.setValueSeparator('=');
			commandOptions.addOption(plotFileOption);
			
			Option displayImageOption = new Option(OPTION_DISPLAY_IMAGE,false,"display image time series");
			commandOptions.addOption(displayImageOption);
			
			Option displayPlotOption = new Option(OPTION_DISPLAY_PLOT,false,"display plot of bleach roi");
			commandOptions.addOption(displayPlotOption);
			
			Option extentOption = new Option(OPTION_EXTENT,true,"extent of entire domain (default "+DEFAULT_EXTENT_SCALE+")");
			extentOption.setArgName("extent");
			extentOption.setValueSeparator('=');
			commandOptions.addOption(extentOption);
			
			Option imageSizeOption = new Option(OPTION_IMAGE_SIZE,true,"num pixels in x and y (default "+DEFAULT_IMAGE_SIZE+")");
			imageSizeOption.setArgName("numPixels");
			imageSizeOption.setValueSeparator('=');
			commandOptions.addOption(imageSizeOption);
			
			Option numParticlesOption = new Option(OPTION_NUM_PARTICLES,true,"num particles (default "+DEFAULT_NUM_PARTICLES+")");
			numParticlesOption.setArgName("num");
			numParticlesOption.setValueSeparator('=');
			commandOptions.addOption(numParticlesOption);
			
			Option diffusionOption = new Option(OPTION_DIFFUSION,true,"diffusion rate (default "+DEFAULT_DIFFUSION+")");
			diffusionOption.setArgName("rate");
			diffusionOption.setValueSeparator('=');
			commandOptions.addOption(diffusionOption);
			
			Option bleachRadiusOption = new Option(OPTION_BLEACH_RADIUS,true,"bleach radius (default "+DEFAULT_BLEACH_RADIUS+")");
			bleachRadiusOption.setArgName("radius");
			bleachRadiusOption.setValueSeparator('=');
			commandOptions.addOption(bleachRadiusOption);
			
			Option psfRadiusOption = new Option(OPTION_PSF_RADIUS,true,"psf radius (default "+DEFAULT_PSF_RADIUS+")");
			psfRadiusOption.setArgName("radius");
			psfRadiusOption.setValueSeparator('=');
			commandOptions.addOption(psfRadiusOption);
			
			Option bleachDurationOption = new Option(OPTION_BLEACH_DURATION,true,"psf radius (default "+DEFAULT_BLEACH_DURATION+")");
			bleachDurationOption.setArgName("duration");
			bleachDurationOption.setValueSeparator('=');
			commandOptions.addOption(bleachDurationOption);
			
			Option endTimeOption = new Option(OPTION_ENDTIME,true,"end time (default "+DEFAULT_ENDTIME+")");
			endTimeOption.setArgName("time");
			endTimeOption.setValueSeparator('=');
			commandOptions.addOption(endTimeOption);
			
			CommandLine cmdLine = null;
			try {
				Parser parser = new BasicParser( );
				cmdLine = parser.parse(commandOptions, args);
			} catch (ParseException e1) {
				e1.printStackTrace();
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("BrownianDynamicsTest", commandOptions);
				System.exit(2);
			}
			boolean bNoise = cmdLine.hasOption(OPTION_NOISE);
			boolean bConvolve = cmdLine.hasOption(OPTION_PSF);
			File imageFile = null;
			if (cmdLine.hasOption(OPTION_IMAGEFILE)){
				imageFile = new File(cmdLine.getOptionValue(OPTION_IMAGEFILE));
			}
			File plotFile = null;
			if (cmdLine.hasOption(OPTION_PLOTFILE)){
				plotFile = new File(cmdLine.getOptionValue(OPTION_PLOTFILE));
			}
			boolean bDisplayImage = cmdLine.hasOption(OPTION_DISPLAY_IMAGE);
			boolean bDisplayPlot = cmdLine.hasOption(OPTION_DISPLAY_PLOT);
			double extentScale = DEFAULT_EXTENT_SCALE;
			if (cmdLine.hasOption(OPTION_EXTENT)){
				extentScale = Double.parseDouble(cmdLine.getOptionValue(OPTION_EXTENT));
			}
			int imageSize = DEFAULT_IMAGE_SIZE;
			if (cmdLine.hasOption(OPTION_IMAGE_SIZE)){
				imageSize = Integer.parseInt(cmdLine.getOptionValue(OPTION_IMAGE_SIZE));
			}
			int numParticles = DEFAULT_NUM_PARTICLES;
			if (cmdLine.hasOption(OPTION_NUM_PARTICLES)){
				numParticles = Integer.parseInt(cmdLine.getOptionValue(OPTION_NUM_PARTICLES));
			}
			double diffusionRate = DEFAULT_DIFFUSION;
			if (cmdLine.hasOption(OPTION_DIFFUSION)){
				diffusionRate = Double.parseDouble(cmdLine.getOptionValue(OPTION_DIFFUSION));
			}
			double bleachRadius = DEFAULT_BLEACH_RADIUS;
			if (cmdLine.hasOption(OPTION_BLEACH_RADIUS)){
				bleachRadius = Double.parseDouble(cmdLine.getOptionValue(OPTION_BLEACH_RADIUS));
			}
			double psfRadius = DEFAULT_PSF_RADIUS;
			if (cmdLine.hasOption(OPTION_PSF_RADIUS)){
				psfRadius = Double.parseDouble(cmdLine.getOptionValue(OPTION_PSF_RADIUS));
			}
			double bleachDuration = DEFAULT_BLEACH_DURATION;
			if (cmdLine.hasOption(OPTION_BLEACH_DURATION)){
				bleachDuration = Double.parseDouble(cmdLine.getOptionValue(OPTION_BLEACH_DURATION));
			}
			double endTime = DEFAULT_ENDTIME;
			if (cmdLine.hasOption(OPTION_ENDTIME)){
				endTime = Double.parseDouble(cmdLine.getOptionValue(OPTION_BLEACH_DURATION));
			}
			
			//
			// hard coded parameters
			//
			Origin origin = new Origin(0,0,0);
			Extent extent = new Extent(extentScale,extentScale,1);
			int numX = imageSize;
			int numY = imageSize;
			double psfVar = psfRadius*psfRadius;

			BrownianDynamicsTest test = new BrownianDynamicsTest();
			ImageTimeSeries<UShortImage> rawTimeSeries = test.generateTestData(origin, extent, numX, numY, numParticles, diffusionRate, psfVar, bleachRadius*bleachRadius, bNoise, bConvolve, bleachDuration, endTime);
			
			//
			// optionally write images to file
			//
			if (imageFile!=null){
				new ExportRawTimeSeriesToVFrapOp().exportToVFRAP(imageFile, rawTimeSeries, null);
			}
			
			//
			// optionally display time series
			//
			if (bDisplayImage){
				new DisplayTimeSeriesOp().displayImageTimeSeries(rawTimeSeries, "time series", null);
			}
			
			//
			// compute reduced data if needed for plotting or saving.
			//
			RowColumnResultSet reducedData = null;
			if (bDisplayPlot || plotFile!=null){
				double muX = origin.getX()+0.5*extent.getX();
				double muY = origin.getY()+0.5*extent.getY();
				double sigma = Math.sqrt(psfVar);
				NormalizedSampleFunction gaussian = NormalizedSampleFunction.fromGaussian("psf", origin, extent, new ISize(numX,numY,1), muX, muY, sigma);
				reducedData = new GenerateReducedDataOp().generateReducedData(rawTimeSeries, new NormalizedSampleFunction[] { gaussian });
			}
			
			//
			// optionally write plot to file
			//
			if (plotFile!=null){
				FileOutputStream fos = new FileOutputStream(plotFile);
				new CSV().exportTo(fos, reducedData);
			}
			
			if (bDisplayPlot) {
				new DisplayPlotOp().displayPlot(reducedData, "bleached roi", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ImageTimeSeries<UShortImage> generateTestData(Origin origin, Extent extent, int numX, int numY, int numParticles, double diffusionRate, double psfVar, double bleachRadius, boolean bNoise, boolean bConvolve, double bleachDuration, double endTime) throws ImageException{
		double detectorGainAndQuantumYeild = 100;
		double totalSampleTime = 0.2;
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
		
		
		int numBleachSteps = 1;
		double bleachCenterX = origin.getX() + 0.5*extent.getX();
		double bleachCenterY = origin.getY() + 0.5*extent.getY();
		double bleachPsfVariance = bleachRadius;
		double bleachFactor_K = 10/bleachDuration;
		solver.bleachGuassian(diffusionRate, bleachDuration, numBleachSteps, bleachCenterX, bleachCenterY, bleachPsfVariance, bleachFactor_K);

		// wait
		double postbleachDelay = 0.1;
		solver.step(diffusionRate, postbleachDelay);
		
		// sample postbleach for 10 seconds

		while (solver.currentTime()<endTime){
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
	
}
