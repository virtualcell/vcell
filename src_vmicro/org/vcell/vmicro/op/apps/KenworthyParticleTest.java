package org.vcell.vmicro.op.apps;

import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileReader;

import org.vcell.optimization.ProfileData;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ISize;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vmicro.op.ComputeMeasurementErrorOp;
import org.vcell.vmicro.op.FitBleachSpotOp;
import org.vcell.vmicro.op.FitBleachSpotOp.FitBleachSpotOpResults;
import org.vcell.vmicro.op.Generate2DExpModelOpAbstract.Context;
import org.vcell.vmicro.op.Generate2DExpModelOpAbstract.GeneratedModelResults;
import org.vcell.vmicro.op.Generate2DExpModel_GaussianBleachOp;
import org.vcell.vmicro.op.Generate2DOptContextOp;
import org.vcell.vmicro.op.GenerateBleachRoiOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawFrapTimeSeriesOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawFrapTimeSeriesOp.GeometryRoisAndBleachTiming;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp.NormalizedFrapDataResults;
import org.vcell.vmicro.op.GenerateReducedDataOp;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromVFrapOp;
import org.vcell.vmicro.op.RunFakeSimOp;
import org.vcell.vmicro.op.RunProfileLikelihoodGeneralOp;
import org.vcell.vmicro.op.display.DisplayImageOp;
import org.vcell.vmicro.op.display.DisplayInteractiveModelOp;
import org.vcell.vmicro.op.display.DisplayPlotOp;
import org.vcell.vmicro.op.display.DisplayProfileLikelihoodPlotsOp;
import org.vcell.vmicro.op.display.DisplayTimeSeriesOp;
import org.vcell.vmicro.workflow.data.ErrorFunction;
import org.vcell.vmicro.workflow.data.ErrorFunctionKenworthy;
import org.vcell.vmicro.workflow.data.ErrorFunctionMeanSquared;
import org.vcell.vmicro.workflow.data.ErrorFunctionNoiseWeightedL2;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.vmicro.workflow.data.OptModelKenworthyGaussian;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk2P;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk3P;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.CSV;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.solver.Simulation;

public class KenworthyParticleTest {

	public static void main(String[] args) {
		try {
			File baseDir = new File(".");
			//File baseDir = new File("/Users/schaff/Documents/workspace/VCell_5.4");
			
			// initialize computing environment
			//
			File workingDirectory = new File(baseDir, "workingDir");
			LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
			
			//
			// analyze raw data (from file?) using Keyworthy method.
			//
			//File vfrapFile = new File(baseDir, "vfrapPaper/rawData/sim3/workflow.txt.save");
			File vfrapFile = new File(baseDir, "tryit.vfrap");
			ImageTimeSeries<UShortImage> fluorTimeSeriesImages = new ImportRawTimeSeriesFromVFrapOp().importRawTimeSeriesFromVFrap(vfrapFile);
			RowColumnResultSet reducedData = new CSV().importFrom(new FileReader(new File(baseDir, "tryit.csv")));
			analyzeKeyworthy(fluorTimeSeriesImages, reducedData, localWorkspace);
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}
	
	/**
	 * Fits raw image time series data to uniform disk models (with Guassian or Uniform fluorescence).
	 * 
	 * @param rawTimeSeriesImages
	 * @param localWorkspace
	 * @throws Exception
	 */
	private static void analyzeKeyworthy(ImageTimeSeries<UShortImage> rawTimeSeriesImages, RowColumnResultSet reducedData, LocalWorkspace localWorkspace) throws Exception {

		new DisplayTimeSeriesOp().displayImageTimeSeries(rawTimeSeriesImages, "raw images", (WindowListener)null);
		
//		double cellThreshold = 0.5;
//		GeometryRoisAndBleachTiming cellROIresults = new GenerateCellROIsFromRawFrapTimeSeriesOp().generate(rawTimeSeriesImages, cellThreshold);
//		ROI backgroundROI = cellROIresults.backgroundROI_2D;
//		ROI cellROI = cellROIresults.cellROI_2D;
//		int indexOfFirstPostbleach = cellROIresults.indexOfFirstPostbleach;
//		
//		new DisplayImageOp().displayImage(backgroundROI.getRoiImages()[0], "background ROI", null);
//		new DisplayImageOp().displayImage(cellROI.getRoiImages()[0], "cell ROI", null);
//		
//		NormalizedFrapDataResults normResults = new GenerateNormalizedFrapDataOp().generate(rawTimeSeriesImages, backgroundROI, indexOfFirstPostbleach);
//		ImageTimeSeries<FloatImage> normalizedTimeSeries = normResults.normalizedFrapData;
//		FloatImage prebleachAvg = normResults.prebleachAverage;
//		FloatImage normalizedPostbleach = normalizedTimeSeries.getAllImages()[0];
//
//		
//		new DisplayTimeSeriesOp().displayImageTimeSeries(normalizedTimeSeries, "normalized images", (WindowListener)null);
//		//
//		// create a single bleach ROI by thresholding
//		//
//		double bleachThreshold = 0.80;
//		ROI bleachROI = new GenerateBleachRoiOp().generateBleachRoi(normalizedPostbleach, cellROI, bleachThreshold);
//					
//		//
//		// only use bleach ROI for fitting etc.
//		//
////		ROI[] dataROIs = new ROI[] { bleachROI };
//		
//		//
//		// fit 2D Gaussian to normalized data to determine center, radius and K factor of bleach (assuming exp(-exp
//		//
//		FitBleachSpotOpResults fitSpotResults = new FitBleachSpotOp().fit(NormalizedSampleFunction.fromROI(bleachROI), normalizedTimeSeries.getAllImages()[0]);
//		double bleachFactorK_GaussianFit = fitSpotResults.bleachFactorK_GaussianFit;
//		double bleachRadius_GaussianFit = fitSpotResults.bleachRadius_GaussianFit;
//		double bleachRadius_ROI = fitSpotResults.bleachRadius_ROI;
//		double centerX_GaussianFit = fitSpotResults.centerX_GaussianFit;
//		double centerX_ROI = fitSpotResults.centerX_ROI;
//		double centerY_GaussianFit = fitSpotResults.centerY_GaussianFit;
//		double centerY_ROI = fitSpotResults.centerY_ROI;
//		
//		NormalizedSampleFunction[] sampleFunctions = new NormalizedSampleFunction[] {
//				NormalizedSampleFunction.fromROI(bleachROI)
//		};

		//
		// get reduced data and errors for each ROI
		//
//		RowColumnResultSet reducedData = new GenerateReducedDataOp().generateReducedData(normalizedTimeSeries, sampleFunctions);
		RowColumnResultSet measurementErrors = new RowColumnResultSet(new String[] { reducedData.getColumnDescriptions()[0].getName(), reducedData.getColumnDescriptions()[1].getName() });
		for (int i=0;i<reducedData.getRowCount();i++){
			double[] row = new double[] { reducedData.getRow(i)[0], Math.sqrt(reducedData.getRow(i)[1]) };
			measurementErrors.addRow(row);
		}
		new DisplayPlotOp().displayPlot(reducedData, "raw reduced data", null);
		new DisplayPlotOp().displayPlot(measurementErrors, "raw reduced noise", null);
		
		//
		// find large drop and determine first post-bleach timepoint.
		//
		double largestDrop = -1000000;
		int indexLargestDrop = -1;
		for (int i=1;i<reducedData.getRowCount();i++){
			double drop = reducedData.getRow(i-1)[1] - reducedData.getRow(i)[1];
			if (drop > largestDrop){
				indexLargestDrop = i;
				largestDrop = drop;
			}
		}
		System.out.println("index of first postbleach is "+indexLargestDrop);
		//
		// normalize data and noise and start with first post-bleach index;
		//
		RowColumnResultSet normalizedReducedData = new RowColumnResultSet(new String[] { reducedData.getColumnDescriptions()[0].getName(), reducedData.getColumnDescriptions()[1].getName() });
		RowColumnResultSet normalizedNoiseData = new RowColumnResultSet(new String[] { reducedData.getColumnDescriptions()[0].getName(), reducedData.getColumnDescriptions()[1].getName() });
		for (int i=indexLargestDrop;i<reducedData.getRowCount();i++){
			double timeAfterBleach = reducedData.getRow(i)[0]-reducedData.getRow(indexLargestDrop)[0];
			double normalizedData = reducedData.getRow(i)[1]/reducedData.getRow(0)[1];
			double[] newDataRow = new double[] { timeAfterBleach, normalizedData }; 
			normalizedReducedData.addRow(newDataRow);
			double normalizedNoise = measurementErrors.getRow(i)[1]/reducedData.getRow(0)[1];
			double[] newNoiseRow = new double[] { timeAfterBleach, normalizedNoise };
			normalizedNoiseData.addRow(newNoiseRow);
		}
		new DisplayPlotOp().displayPlot(normalizedReducedData, "normalized reduced data", null);
		new DisplayPlotOp().displayPlot(normalizedNoiseData, "normalized reduced noise", null);
		
		NormalizedSampleFunction[] sampleFunctions = new NormalizedSampleFunction[] { NormalizedSampleFunction.createUniform("psf", null, null, new ISize(10,10,1)) };
		ErrorFunction errorFunction = new ErrorFunctionKenworthy(normalizedReducedData);
//		ErrorFunction errorFunction = new ErrorFunctionMeanSquared();
		
		double bleachRadius_ROI = 2;
		
		//
		// 2 parameter uniform disk model
		//
		OptModel uniformDisk2OptModel = new OptModelKenworthyUniformDisk2P(bleachRadius_ROI);
		String title_u2 = "Uniform Disk Model - 2 parameters, (Rn="+bleachRadius_ROI+")";
		OptContext uniformDisk2Context = new Generate2DOptContextOp().generate2DOptContext(uniformDisk2OptModel, normalizedReducedData, normalizedNoiseData, errorFunction);
		new DisplayInteractiveModelOp().displayOptModel(uniformDisk2Context, sampleFunctions, localWorkspace, title_u2, null);

		//
		// 3 parameter uniform disk model
		//
		OptModel uniformDisk3OptModel = new OptModelKenworthyUniformDisk3P(bleachRadius_ROI);		
		OptContext uniformDisk3Context = new Generate2DOptContextOp().generate2DOptContext(uniformDisk3OptModel, normalizedReducedData, normalizedNoiseData, errorFunction);
		String title_u3 = "Uniform Disk Model - 3 parameters, (Rn="+bleachRadius_ROI+")";
		new DisplayInteractiveModelOp().displayOptModel(uniformDisk3Context, sampleFunctions, localWorkspace, title_u3, null);

		ProfileData[] profileData = new RunProfileLikelihoodGeneralOp().runProfileLikihood(uniformDisk3Context, null);
		new DisplayProfileLikelihoodPlotsOp().displayProfileLikelihoodPlots(profileData, "3param model", null);
		//
		// GaussianFit parameter uniform disk model
		//
//		FloatImage prebleachBleachAreaImage = new FloatImage(prebleachAvg);
//		prebleachBleachAreaImage.and(bleachROI.getRoiImages()[0]); // mask-out all but the bleach area
//		double prebleachAvgInROI = prebleachBleachAreaImage.getImageStatistics().meanValue;
//		OptModel gaussian2OptModel = new OptModelKenworthyGaussian(prebleachAvgInROI, bleachFactorK_GaussianFit, bleachRadius_GaussianFit, bleachRadius_ROI);		
//		OptContext gaussianDisk2Context = new Generate2DOptContextOp().generate2DOptContext(gaussian2OptModel, reducedData, measurementErrors, errorFunction);
//		String title_g2 = "Gaussian Disk Model - 2 parameters (prebleach="+prebleachAvgInROI+",K="+bleachFactorK_GaussianFit+",Re="+bleachRadius_GaussianFit+",Rnom="+bleachRadius_ROI+")";
//		new DisplayInteractiveModelOp().displayOptModel(gaussianDisk2Context, sampleFunctions, localWorkspace, title_g2, null);
	}
	
	private static ImageTimeSeries<UShortImage>  generateFakeData(LocalWorkspace localWorkspace, ClientTaskStatusSupport progressListener) throws Exception{

		//
		// technical simulation parameters
		//
		double deltaX = 0.3;
		double outputTimeStep = 0.3;

		//
		// circular 2D cell geometry
		//
		double cellRadius= 50.0;
		String extracellularName = "ec";
		String cytosolName = "cytosol";

		//
		// bleaching experimental protocol
		//
		double bleachRadius = 1.5;
		double bleachDuration = 0.003;
		double bleachRate = 500.0;
		double postbleachDelay = 0.001;
		double postbleachDuration = 25.0;
		double psfSigma = 0.01;

		//
		// underlying physiological model
		//
		double primaryDiffusionRate = 2.0;
		double primaryFraction = 1.0;
		double bleachMonitorRate = 0.0000005;
		double secondaryDiffusionRate = 0.0;
		double secondaryFraction =  0.0;

		//
		// generate corresponding BioModel with Application and Simulation ready for simulation.
		//
		Context context = new Context() {
			
			@Override
			public User getDefaultOwner() {
				return LocalWorkspace.getDefaultOwner();
			}
			
			@Override
			public KeyValue createNewKeyValue() {
				return LocalWorkspace.createNewKeyValue();
			}
		};
		GeneratedModelResults results = new Generate2DExpModel_GaussianBleachOp().generateModel(deltaX, bleachRadius, cellRadius, bleachDuration, bleachRate, postbleachDelay, postbleachDuration, psfSigma, outputTimeStep, primaryDiffusionRate, primaryFraction, bleachMonitorRate, secondaryDiffusionRate, secondaryFraction, extracellularName, cytosolName, context);
		Simulation simulation = results.simulation_2D;
		double bleachBlackoutStartTime = results.bleachBlackoutBeginTime;
		double bleachBlackoutStopTime = results.bleachBlackoutEndTime;

		
		//
		// run simulation to get simulated fluorescence (with noise)
		//
		boolean hasNoise = true;
		double maxIntensity =  60000.0;
		ImageTimeSeries<UShortImage> simulatedFluorescence = new RunFakeSimOp().runRefSimulation(localWorkspace, simulation, maxIntensity, bleachBlackoutStartTime, bleachBlackoutStopTime, hasNoise, progressListener);
					
		return simulatedFluorescence;
	}
	
}
