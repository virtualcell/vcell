package org.vcell.vmicro.op.apps;

import java.awt.event.WindowListener;
import java.io.File;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vmicro.op.ComputeMeasurementErrorOp;
import org.vcell.vmicro.op.Generate2DExpModelOpAbstract.Context;
import org.vcell.vmicro.op.Generate2DExpModelOpAbstract.GeneratedModelResults;
import org.vcell.vmicro.op.Generate2DExpModel_GaussianBleachOp;
import org.vcell.vmicro.op.Generate2DOptContextOp;
import org.vcell.vmicro.op.GenerateActivationRoiOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawPhotoactivationTimeSeriesOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawPhotoactivationTimeSeriesOp.GeometryRoisAndActivationTiming;
import org.vcell.vmicro.op.GenerateNormalizedPhotoactivationDataOp;
import org.vcell.vmicro.op.GenerateNormalizedPhotoactivationDataOp.NormalizedPhotoactivationDataResults;
import org.vcell.vmicro.op.GenerateReducedDataOp;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromVFrapOp;
import org.vcell.vmicro.op.RunFakeSimOp;
import org.vcell.vmicro.op.display.DisplayImageOp;
import org.vcell.vmicro.op.display.DisplayInteractiveModelOp;
import org.vcell.vmicro.op.display.DisplayPlotOp;
import org.vcell.vmicro.op.display.DisplayTimeSeriesOp;
import org.vcell.vmicro.workflow.data.ErrorFunction;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.ErrorFunctionNoiseWeightedL2;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModel;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.opt.Parameter;
import cbit.vcell.solver.Simulation;

public class PhotoactivationExperimentTest {

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
			analyzePhotoactivation(fluorTimeSeriesImages, localWorkspace);
			
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
	private static void analyzePhotoactivation(ImageTimeSeries<UShortImage> rawTimeSeriesImages, LocalWorkspace localWorkspace) throws Exception {

		//
		// correct the timestamps (1 per second).
		//
		double[] timeStamps = rawTimeSeriesImages.getImageTimeStamps();
		for (int i=0;i<timeStamps.length;i++){
			timeStamps[i] = i;
		}
		
		
		new DisplayTimeSeriesOp().displayImageTimeSeries(rawTimeSeriesImages, "raw images", (WindowListener)null);
		
		ImageTimeSeries<UShortImage> blurredRaw = blurTimeSeries(rawTimeSeriesImages);
		new DisplayTimeSeriesOp().displayImageTimeSeries(blurredRaw, "blurred raw images", (WindowListener)null);
		
		
		double cellThreshold = 0.4;
		GeometryRoisAndActivationTiming cellROIresults = new GenerateCellROIsFromRawPhotoactivationTimeSeriesOp().generate(blurredRaw, cellThreshold);
		ROI backgroundROI = cellROIresults.backgroundROI_2D;
		ROI cellROI = cellROIresults.cellROI_2D;
		int indexOfFirstPostactivation = cellROIresults.indexOfFirstPostactivation;
		
		boolean backgroundSubtract = false;
		boolean normalizeByPreActivation = false;
		NormalizedPhotoactivationDataResults normResults = new GenerateNormalizedPhotoactivationDataOp().generate(rawTimeSeriesImages, backgroundROI, indexOfFirstPostactivation, backgroundSubtract, normalizeByPreActivation);
		ImageTimeSeries<FloatImage> normalizedTimeSeries = normResults.normalizedPhotoactivationData;
		FloatImage preactivationAvg = normResults.preactivationAverageImage;
		FloatImage normalizedPostactivation = normalizedTimeSeries.getAllImages()[0];
		
		
		new DisplayTimeSeriesOp().displayImageTimeSeries(normalizedTimeSeries, "normalized images", (WindowListener)null);
		//
		// create a single bleach ROI by thresholding
		//
		double activatedThreshold = 1000;
		ROI activatedROI = new GenerateActivationRoiOp().generateActivatedRoi(blurredRaw.getAllImages()[indexOfFirstPostactivation], cellROI, activatedThreshold);
					
		new DisplayImageOp().displayImage(activatedROI.getRoiImages()[0], "activated roi", null);
		
		new DisplayImageOp().displayImage(cellROI.getRoiImages()[0], "cell roi", null);
		
		new DisplayImageOp().displayImage(backgroundROI.getRoiImages()[0], "background roi", null);
		
		{
		//
		// only use bleach ROI for fitting etc.
		//
		NormalizedSampleFunction[] dataROIs = new NormalizedSampleFunction[] { NormalizedSampleFunction.fromROI(activatedROI) };
		
		//
		// get reduced data and errors for each ROI
		//
		RowColumnResultSet reducedData = new GenerateReducedDataOp().generateReducedData(normalizedTimeSeries, dataROIs);
		RowColumnResultSet measurementErrors = new ComputeMeasurementErrorOp().computeNormalizedMeasurementError(
				dataROIs, indexOfFirstPostactivation, rawTimeSeriesImages, preactivationAvg, null);

		
		DisplayPlotOp displayReducedData = new DisplayPlotOp();
		displayReducedData.displayPlot(reducedData, "reduced data", null);
		DisplayPlotOp displayMeasurementError = new DisplayPlotOp();
		displayMeasurementError.displayPlot(measurementErrors, "measurement error", null);
		
		//
		// 2 parameter uniform disk model
		//
		Parameter tau = new Parameter("tau",0.001,200.0,1.0,0.1);
		Parameter f_init = new Parameter("f_init",0.5,4000,1.0,1.0);
		Parameter f_final = new Parameter("f_final",0.01,4000,1.0,0.5);
		Parameter[] parameters = new Parameter[] {	tau, f_init, f_final };
		
		OptModel optModel = new OptModel("photoactivation (activated roi)", parameters) {
			
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
		ErrorFunction errorFunction = new ErrorFunctionNoiseWeightedL2();
		OptContext uniformDisk2Context = new Generate2DOptContextOp().generate2DOptContext(optModel, reducedData, measurementErrors, errorFunction);
		new DisplayInteractiveModelOp().displayOptModel(uniformDisk2Context, dataROIs, localWorkspace, "nonspatial photoactivation - activated ROI only", null);
		}
		
		{
		//
		// only activation ROI for chemistry, cellROI for bleaching
		//
		NormalizedSampleFunction[] dataROIs = new NormalizedSampleFunction[] { 
				NormalizedSampleFunction.fromROI(activatedROI), 
				NormalizedSampleFunction.fromROI(cellROI)
		};
		
		//
		// get reduced data and errors for each ROI
		//
		RowColumnResultSet reducedData = new GenerateReducedDataOp().generateReducedData(normalizedTimeSeries, dataROIs);
		RowColumnResultSet measurementErrors = new ComputeMeasurementErrorOp().computeNormalizedMeasurementError(
				dataROIs, indexOfFirstPostactivation, rawTimeSeriesImages, preactivationAvg, null);

		DisplayPlotOp displayReducedData = new DisplayPlotOp();
		displayReducedData.displayPlot(reducedData, "reduced data (2)", null);
		DisplayPlotOp displayMeasurementError = new DisplayPlotOp();
		displayMeasurementError.displayPlot(measurementErrors, "measurement error (2)", null);
		//
		// 2 parameter uniform disk model
		//
		Parameter tau_active = new Parameter("tau_active",0.001,200.0,1.0,0.1);
		Parameter f_active_init = new Parameter("f_active_init",0.5,4000,1.0,1.0);
		Parameter f_active_amplitude = new Parameter("f_active_amplitude",0.01,4000,1.0,0.5);
		Parameter f_cell_init = new Parameter("f_cell_init",0.01,4000,1.0,0.1);
		Parameter f_cell_amplitude = new Parameter("f_cell_amplitude",0.01,4000,1.0,0.1);
		Parameter tau_cell = new Parameter("tau_cell",0.00001,200,1.0,1);
		Parameter[] parameters = new Parameter[] {	tau_active, f_active_init, f_active_amplitude, tau_cell, f_cell_init, f_cell_amplitude  };
		
		OptModel optModel = new OptModel("photoactivation (activated and cell rois)", parameters) {
			
			@Override
			public double[][] getSolution0(double[] newParams, double[] solutionTimePoints) {
				double tau_active = newParams[0];
				double max_active = newParams[1];
				double amplitude_active = newParams[2];
				double tau_cell = newParams[3];
				double max_cell = newParams[4];
				double amplitude_cell = newParams[5];
				
				final int ACTIVE_ROI = 0;
				final int CELL_ROI = 1;
				final int NUM_ROIS = 2;

				double[][] solution = new double[NUM_ROIS][solutionTimePoints.length];
				for (int i=0;i<solution[0].length;i++){
					double t = solutionTimePoints[i];
					solution[ACTIVE_ROI][i] = (max_active-amplitude_active) + (amplitude_active)*Math.exp(-t/tau_active)*Math.exp(-t/tau_cell);
					solution[CELL_ROI][i] = (max_cell-amplitude_cell) + (amplitude_cell)*Math.exp(-t/tau_cell);
				}
				
				return solution;
			}
			
			@Override
			public double getPenalty(double[] parameters2) {
				return 0;
			}
		};
				
		ErrorFunctionNoiseWeightedL2 errorFunction = new ErrorFunctionNoiseWeightedL2();
		OptContext uniformDisk2Context = new Generate2DOptContextOp().generate2DOptContext(optModel, reducedData, measurementErrors, errorFunction);
		new DisplayInteractiveModelOp().displayOptModel(uniformDisk2Context, dataROIs, localWorkspace, "nonspatial photoactivation - activated and cell ROIs", null);
		}
		
		
	}
	
	private static ImageTimeSeries<UShortImage> blurTimeSeries(ImageTimeSeries<UShortImage> rawTimeSeriesImages) throws ImageException {
		UShortImage[] blurredImages = new UShortImage[rawTimeSeriesImages.getAllImages().length];
		for (int i=0;i<blurredImages.length;i++){
			blurredImages[i] = new UShortImage(rawTimeSeriesImages.getAllImages()[i]);
			blurredImages[i].blur();
			blurredImages[i].blur();
		}
		ImageTimeSeries<UShortImage> blurredRaw = new ImageTimeSeries<UShortImage>(UShortImage.class,blurredImages,rawTimeSeriesImages.getImageTimeStamps(),rawTimeSeriesImages.getSizeZ());
		return blurredRaw;
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
