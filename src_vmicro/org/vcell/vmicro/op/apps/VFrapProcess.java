package org.vcell.vmicro.op.apps;

import java.io.File;

import org.vcell.optimization.ProfileData;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.vmicro.op.ComputeMeasurementErrorOp;
import org.vcell.vmicro.op.Generate2DOptContextOp;
import org.vcell.vmicro.op.GenerateBleachRoiOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawFrapTimeSeriesOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawFrapTimeSeriesOp.GeometryRoisAndBleachTiming;
import org.vcell.vmicro.op.GenerateDependentImageROIsOp;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp.NormalizedFrapDataResults;
import org.vcell.vmicro.op.GenerateReducedDataOp;
import org.vcell.vmicro.op.GenerateTrivial2DPsfOp;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromVFrapOp;
import org.vcell.vmicro.op.RunProfileLikelihoodGeneralOp;
import org.vcell.vmicro.op.RunRefSimulationFastOp;
import org.vcell.vmicro.op.display.DisplayDependentROIsOp;
import org.vcell.vmicro.op.display.DisplayImageOp;
import org.vcell.vmicro.op.display.DisplayProfileLikelihoodPlotsOp;
import org.vcell.vmicro.op.display.DisplayTimeSeriesOp;
import org.vcell.vmicro.workflow.data.ErrorFunction;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.ErrorFunctionNoiseWeightedL2;
import org.vcell.vmicro.workflow.data.NormalizedSampleFunction;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModelOneDiff;
import org.vcell.vmicro.workflow.data.OptModelTwoDiffWithPenalty;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;

public class VFrapProcess {
	
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
			File vfrapFile = new File(baseDir, "vfrapPaper/rawData/sim3/workflow.txt.save");
			ImageTimeSeries<UShortImage> fluorTimeSeriesImages = new ImportRawTimeSeriesFromVFrapOp().importRawTimeSeriesFromVFrap(vfrapFile);
			VFrapProcessResults results = compute(fluorTimeSeriesImages, 0.85, 0.4, localWorkspace, progressListener);
			
			new DisplayTimeSeriesOp().displayImageTimeSeries(fluorTimeSeriesImages, "raw data", null);
			new DisplayImageOp().displayImage(results.cellROI_2D.getRoiImages()[0], "cell ROI", null);
			new DisplayImageOp().displayImage(results.bleachROI_2D.getRoiImages()[0], "bleach ROI", null);
			new DisplayDependentROIsOp().displayDependentROIs(results.imageDataROIs, results.cellROI_2D, "data rois", null);
			new DisplayTimeSeriesOp().displayImageTimeSeries(results.normalizedTimeSeries, "normalized data", null);
			new DisplayProfileLikelihoodPlotsOp().displayProfileLikelihoodPlots(results.profileDataOne, "one fluorescent pool", null);
			new DisplayProfileLikelihoodPlotsOp().displayProfileLikelihoodPlots(results.profileDataTwoWithPenalty, "too pools", null);
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}
	//
	// outputs
	//
	public static class VFrapProcessResults {
		public final ROI[] imageDataROIs;
		public final ROI cellROI_2D;
		public final ROI bleachROI_2D;
		public final ImageTimeSeries normalizedTimeSeries;
		public final RowColumnResultSet reducedTimeSeries;
		public final ProfileData[] profileDataOne;
	//	public final ProfileData[] profileDataTwoWithoutPenalty;
		public final ProfileData[] profileDataTwoWithPenalty;
		
		private VFrapProcessResults(
				ROI[] imageDataROIs, 
				ROI cellROI_2D,
				ROI bleachROI_2D,
				ImageTimeSeries normalizedTimeSeries,
				RowColumnResultSet reducedTimeSeries,
				ProfileData[] profileDataOne,
				ProfileData[] profileDataTwoWithPenalty) {
	
			this.imageDataROIs = imageDataROIs;
			this.cellROI_2D = cellROI_2D;
			this.bleachROI_2D = bleachROI_2D;
			this.normalizedTimeSeries = normalizedTimeSeries;
			this.reducedTimeSeries = reducedTimeSeries;
			this.profileDataOne = profileDataOne;
			this.profileDataTwoWithPenalty = profileDataTwoWithPenalty;
		}
		
	}
	
	
	
	public static VFrapProcessResults compute(ImageTimeSeries rawTimeSeriesImages, double bleachThreshold, double cellThreshold, LocalWorkspace localWorkspace, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {

		GenerateCellROIsFromRawFrapTimeSeriesOp generateCellROIs = new GenerateCellROIsFromRawFrapTimeSeriesOp();
		GeometryRoisAndBleachTiming geometryAndTiming = generateCellROIs.generate(rawTimeSeriesImages, cellThreshold);
		
		
		GenerateNormalizedFrapDataOp generateNormalizedFrapData = new GenerateNormalizedFrapDataOp();
		NormalizedFrapDataResults normalizedFrapResults = generateNormalizedFrapData.generate(rawTimeSeriesImages, geometryAndTiming.backgroundROI_2D, geometryAndTiming.indexOfFirstPostbleach);
		
		GenerateBleachRoiOp generateROIs = new GenerateBleachRoiOp();
		ROI bleachROI = generateROIs.generateBleachRoi(normalizedFrapResults.normalizedFrapData.getAllImages()[0], geometryAndTiming.cellROI_2D, bleachThreshold);
		
		
		GenerateDependentImageROIsOp generateDependentROIs = new GenerateDependentImageROIsOp();
		ROI[] dataROIs = generateDependentROIs.generate(geometryAndTiming.cellROI_2D, bleachROI);
		
		NormalizedSampleFunction[] roiSampleFunctions = new NormalizedSampleFunction[dataROIs.length];
		for (int i=0;i<dataROIs.length;i++){
			roiSampleFunctions[i] = NormalizedSampleFunction.fromROI(dataROIs[i]);
		}
		
		
		GenerateReducedDataOp generateReducedNormalizedData = new GenerateReducedDataOp();
		RowColumnResultSet reducedData = generateReducedNormalizedData.generateReducedData(normalizedFrapResults.normalizedFrapData, roiSampleFunctions);
		
		
		ComputeMeasurementErrorOp computeMeasurementError = new ComputeMeasurementErrorOp();
		RowColumnResultSet measurementError = computeMeasurementError.computeNormalizedMeasurementError(roiSampleFunctions, geometryAndTiming.indexOfFirstPostbleach, rawTimeSeriesImages, normalizedFrapResults.prebleachAverage, clientTaskStatusSupport);
		
		
		GenerateTrivial2DPsfOp psf_2D = new GenerateTrivial2DPsfOp();
		UShortImage psf = psf_2D.generateTrivial2D_Psf();
		

		//		RunRefSimulationOp runRefSimulationFull = new RunRefSimulationOp();
//		GenerateReducedROIDataOp generateReducedRefSimData = new GenerateReducedROIDataOp();
		
		RunRefSimulationFastOp runRefSimulationFast = new RunRefSimulationFastOp();
		RowColumnResultSet refData = runRefSimulationFast.runRefSimFast(geometryAndTiming.cellROI_2D, normalizedFrapResults.normalizedFrapData, dataROIs, psf, localWorkspace, clientTaskStatusSupport);
		
		final double refDiffusionRate = 1.0;
		double[] refSimTimePoints = refData.extractColumn(0);
		int numRois = refData.getDataColumnCount()-1;
		int numRefSimTimes = refData.getRowCount();
		double[][] refSimData = new double[numRois][numRefSimTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = refData.extractColumn(roi+1);
			for (int t=0; t<numRefSimTimes; t++){
				refSimData[roi][t] = roiData[t];
			}
		}

		ErrorFunction errorFunction = new ErrorFunctionNoiseWeightedL2();
		
		OptModelOneDiff optModelOneDiff = new OptModelOneDiff(refSimData, refSimTimePoints, refDiffusionRate);
		Generate2DOptContextOp generate2DOptContextOne = new Generate2DOptContextOp();
		OptContext optContextOneDiff = generate2DOptContextOne.generate2DOptContext(optModelOneDiff, reducedData, measurementError, errorFunction);
		RunProfileLikelihoodGeneralOp runProfileLikelihoodOne = new RunProfileLikelihoodGeneralOp();
		ProfileData[] profileDataOne = runProfileLikelihoodOne.runProfileLikihood(optContextOneDiff, clientTaskStatusSupport);
		
//		OptModelTwoDiffWithoutPenalty optModelTwoDiffWithoutPenalty = new OptModelTwoDiffWithoutPenalty(refSimData, refSimTimePoints, refDiffusionRate);
//		Generate2DOptContextOp generate2DOptContextTwoWithoutPenalty = new Generate2DOptContextOp();
//		OptContext optContextTwoDiffWithoutPenalty = generate2DOptContextTwoWithoutPenalty.generate2DOptContext(optModelTwoDiffWithoutPenalty, reducedData, measurementError);
//		RunProfileLikelihoodGeneralOp runProfileLikelihoodTwoWithoutPenalty = new RunProfileLikelihoodGeneralOp();
//		ProfileData[] profileDataTwoWithoutPenalty = runProfileLikelihoodTwoWithoutPenalty.runProfileLikihood(optContextTwoDiffWithoutPenalty, clientTaskStatusSupport);
		
		OptModelTwoDiffWithPenalty optModelTwoDiffWithPenalty = new OptModelTwoDiffWithPenalty(refSimData, refSimTimePoints, refDiffusionRate);
		Generate2DOptContextOp generate2DOptContextTwoWithPenalty = new Generate2DOptContextOp();
		OptContext optContextTwoDiffWithPenalty = generate2DOptContextTwoWithPenalty.generate2DOptContext(optModelTwoDiffWithPenalty, reducedData, measurementError, errorFunction);
		RunProfileLikelihoodGeneralOp runProfileLikelihoodTwoWithPenalty = new RunProfileLikelihoodGeneralOp();
		ProfileData[] profileDataTwoWithPenalty = runProfileLikelihoodTwoWithPenalty.runProfileLikihood(optContextTwoDiffWithPenalty, clientTaskStatusSupport);

		//
		// SLOW WAY
		//
//			runRefSimulationFull.cellROI_2D,generateCellROIs.cellROI_2D);
//			runRefSimulationFull.normalizedTimeSeries,generateNormalizedFrapData.normalizedFrapData);
//			workflow.addTask(runRefSimulationFull);
//			generateReducedRefSimData.imageTimeSeries,runRefSimulationFull.refSimTimeSeries);
//			generateReducedRefSimData.imageDataROIs,generateDependentROIs.imageDataROIs);
//			workflow.addTask(generateReducedRefSimData);
//			DataHolder<RowColumnResultSet> reducedROIData = generateReducedRefSimData.reducedROIData;
//			DataHolder<Double> refSimDiffusionRate = runRefSimulationFull.refSimDiffusionRate;
		VFrapProcessResults results = new VFrapProcessResults(dataROIs, geometryAndTiming.cellROI_2D, bleachROI, normalizedFrapResults.normalizedFrapData, reducedData, profileDataOne, profileDataTwoWithPenalty);
		return results;
	}
}
