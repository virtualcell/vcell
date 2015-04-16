package org.vcell.vmicro.op.apps;

import java.awt.event.WindowListener;
import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vmicro.op.ComputeMeasurementErrorOp;
import org.vcell.vmicro.op.FitBleachSpotOp;
import org.vcell.vmicro.op.Generate2DExpModelOpAbstract.Context;
import org.vcell.vmicro.op.Generate2DExpModelOpAbstract.GeneratedModelResults;
import org.vcell.vmicro.op.Generate2DExpModel_GaussianBleachOp;
import org.vcell.vmicro.op.Generate2DOptContextOp;
import org.vcell.vmicro.op.GenerateActivationRoiOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawFlipTimeSeriesOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawFlipTimeSeriesOp.GeometryRoisAndBleachTiming;
import org.vcell.vmicro.op.GenerateNormalizedFlipDataOp;
import org.vcell.vmicro.op.GenerateNormalizedFlipDataOp.NormalizedFlipDataResults;
import org.vcell.vmicro.op.GenerateReducedROIDataOp;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromVFrapOp;
import org.vcell.vmicro.op.RunFakeSimOp;
import org.vcell.vmicro.op.RunProfileLikelihoodGeneralOp;
import org.vcell.vmicro.op.display.DisplayImageOp;
import org.vcell.vmicro.op.display.DisplayInteractiveModelOp;
import org.vcell.vmicro.op.display.DisplayTimeSeriesOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModel;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.opt.Parameter;
import cbit.vcell.solver.Simulation;

public class FlipExperimentTest {

	public static void main(String[] args) {
		try {
			File baseDir = new File(".");
			//File baseDir = new File("/Users/schaff/Documents/workspace/VCell_5.4");
			
			// initialize computing environment
			//
			File workingDirectory = new File(baseDir, "workingDir");
			LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
			
			//
			// import raw image time series data from VFRAP file format (can have noise, background, etc ... can be actual microscopy data)
			//
			
			ClientTaskStatusSupport progressListener = new ClientTaskStatusSupport() {
				String message = "";
				int progress = 0;
				@Override
				public void setProgress(int progress) {
					this.progress = progress;
				}
				
				@Override
				public void setMessage(String message) {
					this.message = message;
				}
				
				@Override
				public boolean isInterrupted() {
					return false;
				}
				
				@Override
				public int getProgress() {
					return progress;
				}
				
				@Override
				public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
				}
			};
			
			//
			// generate fake data (and save?)
			//
//			ImageTimeSeries<UShortImage> simulatedFluorescence = generateFakeData(localWorkspace, progressListener);
//			new ExportRawTimeSeriesToVFrapOp().exportToVFRAP(vfrapFile, simulatedFluorescence, null);
			

			//
			// analyze raw data (from file?) using Keyworthy method.
			//
			//File vfrapFile = new File(baseDir, "vfrapPaper/photoactivation/Actin.vfrap");
			File vfrapFile = new File(baseDir, "vfrapPaper/photoactivation/Actin_binding_protein.vfrap");
			ImageTimeSeries<UShortImage> fluorTimeSeriesImages = new ImportRawTimeSeriesFromVFrapOp().importRawTimeSeriesFromVFrap(vfrapFile);
			analyzeFlip(fluorTimeSeriesImages, localWorkspace);
			
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
	private static void analyzeFlip(ImageTimeSeries<UShortImage> rawTimeSeriesImages, LocalWorkspace localWorkspace) throws Exception {

		//
		// correct the timestamps (1 per second).
		//
		double[] timeStamps = rawTimeSeriesImages.getImageTimeStamps();
		for (int i=0;i<timeStamps.length;i++){
			timeStamps[i] = i;
		}
		
		
		new DisplayTimeSeriesOp().displayImageTimeSeries(rawTimeSeriesImages, "raw images", (WindowListener)null);
		
		double cellThreshold = 0.5;
		GeometryRoisAndBleachTiming cellROIresults = new GenerateCellROIsFromRawFlipTimeSeriesOp().generate(rawTimeSeriesImages, cellThreshold);
		ROI backgroundROI = cellROIresults.backgroundROI_2D;
		ROI cellROI = cellROIresults.cellROI_2D;
		int indexOfFirstPostactivation = cellROIresults.indexOfFirstPostactivation;
		
		NormalizedFlipDataResults normResults = new GenerateNormalizedFlipDataOp().generate(rawTimeSeriesImages, backgroundROI, indexOfFirstPostactivation);
		ImageTimeSeries<FloatImage> normalizedTimeSeries = normResults.normalizedFlipData;
		FloatImage preactivationAvg = normResults.preactivationAverageImage;
		FloatImage normalizedPostactivation = normalizedTimeSeries.getAllImages()[0];
		
		new DisplayTimeSeriesOp().displayImageTimeSeries(normalizedTimeSeries, "normalized images", (WindowListener)null);
		//
		// create a single bleach ROI by thresholding
		//
		double activatedThreshold = 1.20;
		ROI activatedROI = new GenerateActivationRoiOp().generateActivatedRoi(normalizedPostactivation, cellROI, activatedThreshold);
					
		DisplayImageOp displayActivatedROI = new DisplayImageOp();
		displayActivatedROI.displayImage(activatedROI.getRoiImages()[0], "activated roi", null);
		//
		// only use bleach ROI for fitting etc.
		//
		ROI[] dataROIs = new ROI[] { activatedROI };
		
		//
		// get reduced data and errors for each ROI
		//
		RowColumnResultSet reducedData = new GenerateReducedROIDataOp().generateReducedData(normalizedTimeSeries, dataROIs);
		RowColumnResultSet measurementErrors = new ComputeMeasurementErrorOp().computeNormalizedMeasurementError(
				dataROIs, indexOfFirstPostactivation, rawTimeSeriesImages, preactivationAvg, null);

		//
		// 2 parameter uniform disk model
		//
		Parameter tau = new Parameter("tau",0.001,30.0,1.0,0.1);
		Parameter f_init = new Parameter("f_init",0.5,15,1.0,1.0);
		Parameter f_final = new Parameter("f_final",0.01,15.0,1.0,0.5);
		Parameter[] parameters = new Parameter[] {	tau, f_init, f_final };
		
		OptModel optModel = new OptModel("simpleFlip", parameters) {
			
			@Override
			public double[][] getSolution0(double[] newParams, double[] solutionTimePoints) {
				double tau = newParams[0];
				double max = newParams[1];
				double offset = newParams[2];
				
				double[][] solution = new double[1][solutionTimePoints.length];
				for (int i=0;i<solution[0].length;i++){
					double t = solutionTimePoints[i];
					solution[0][i] = offset + (max-offset)*Math.exp(-t/tau);
				}
				return solution;
			}
			
			@Override
			public double getPenalty(double[] parameters2) {
				return 0;
			}
		};
		OptContext uniformDisk2Context = new Generate2DOptContextOp().generate2DOptContext(optModel, reducedData, measurementErrors);
		new DisplayInteractiveModelOp().displayOptModel(uniformDisk2Context, dataROIs, localWorkspace, "nonspatial flip", null);
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
