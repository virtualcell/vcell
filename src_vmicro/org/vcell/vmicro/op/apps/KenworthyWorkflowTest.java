package org.vcell.vmicro.op.apps;

import java.io.File;

import org.vcell.vmicro.op.GenerateBleachRoiOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawTimeSeriesOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawTimeSeriesOp.GeneratedCellRoiResults;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp.NormalizedFrapDataResults;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromVFrapOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;

public class KenworthyWorkflowTest {

	public static void main(String[] args) {
		try {
			doit();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}
	
	private static void doit() throws Exception{
		double bleachThreshold = 0.80;
		double cellThreshold = 0.5;
		double prebleachFluor = 10;
		
		File vfrapFile = new File("d:\\Developer\\eclipse\\workspace_refactor\\VCell_5.4_clean\\vfrapPaper\\rawData\\sim3\\workflow.txt.save");
		
		String uniformDisk2ModelName = "KenworthyUniformDisk2Param";
		String uniformDisk2PanelTitle = "Uniform Disk Model - 2 parameters";
		String uniformDisk3ModelName = "KenworthyUniformDisk3Param";
		String uniformDisk3PanelTitle = "Uniform Disk Model - 3 parameters";

		ImageTimeSeries<UShortImage> rawTimeSeriesImages = new ImportRawTimeSeriesFromVFrapOp().importRawTimeSeriesFromVFrap(vfrapFile);
					
		GeneratedCellRoiResults cellROIresults = new GenerateCellROIsFromRawTimeSeriesOp().generate(rawTimeSeriesImages, cellThreshold);
		ROI backgroundROI = cellROIresults.backgroundROI_2D;
		ROI cellROI = cellROIresults.cellROI_2D;
		int indexOfFirstPostbleach = cellROIresults.indexOfFirstPostbleach;
		
		NormalizedFrapDataResults normResults = new GenerateNormalizedFrapDataOp().generate(rawTimeSeriesImages, backgroundROI, indexOfFirstPostbleach);
		ImageTimeSeries<FloatImage> normalizedTimeSeries = normResults.normalizedFrapData;
		FloatImage prebleachAvg = normResults.prebleachAverage;
		FloatImage normalizedPostbleach = normalizedTimeSeries.getAllImages()[0];

		ROI bleachROI = new GenerateBleachRoiOp().generateBleachRoi(normalizedPostbleach, cellROI, bleachThreshold);
					
//		new GenerateReducedROIDataOp().generateReducedData(normalizedTimeSeries, bleachROI);
//					generateReducedNormalizedData.imageTimeSeries = generateNormalizedFrapData.normalizedFrapData
//					generateReducedNormalizedData.imageDataROIs = generateROIs.bleachedROI_2D_array
//						
//				computeMeasurementError = ComputeMeasurementError()
//					computeMeasurementError.imageDataROIs = generateROIs.bleachedROI_2D_array
//					computeMeasurementError.indexFirstPostbleach = generateCellROIs.indexOfFirstPostbleach
//					computeMeasurementError.prebleachAverage = generateNormalizedFrapData.prebleachAverage
//					computeMeasurementError.rawImageTimeSeries = importVFrap.rawTimeSeriesImages
//
//				fitBleachSpot = FitBleachSpot()
//					fitBleachSpot.normalizedImages = generateNormalizedFrapData.normalizedFrapData
//					fitBleachSpot.bleachROI = generateROIs.bleachedROI_2D
//
//
//				generateUniformDiskModel2 = GenerateKenworthyOptModel()
//					generateUniformDiskModel2.bleachRadius = fitBleachSpot.bleachRadius_GaussianFit
//					generateUniformDiskModel2.modelType = uniformDisk2ModelName
//
//				generate2DOptContextUniformDisk2 = Generate2DOptContext()
//					generate2DOptContextUniformDisk2.optModel = generateUniformDiskModel2.optModel
//					generate2DOptContextUniformDisk2.normalizedMeasurementErrors = computeMeasurementError.normalizedMeasurementError
//					generate2DOptContextUniformDisk2.normExpData = generateReducedNormalizedData.reducedROIData
//					
//				displayInteractiveModel2 = DisplayInteractiveModel()
//					displayInteractiveModel2.optContext = generate2DOptContextUniformDisk2.optContext
//					displayInteractiveModel2.rois = generateROIs.bleachedROI_2D_array
//					displayInteractiveModel2.title = uniformDisk2PanelTitle
//
//
//				generateUniformDiskModel3 = GenerateKenworthyOptModel()
//					generateUniformDiskModel3.bleachRadius = fitBleachSpot.bleachRadius_GaussianFit
//					generateUniformDiskModel3.modelType = uniformDisk3ModelName	
//					generateUniformDiskModel3.prebleachFluor = prebleachFluor
//
//				generate2DOptContextUniformDisk3 = Generate2DOptContext()
//					generate2DOptContextUniformDisk3.optModel = generateUniformDiskModel3.optModel
//					generate2DOptContextUniformDisk3.normalizedMeasurementErrors = computeMeasurementError.normalizedMeasurementError
//					generate2DOptContextUniformDisk3.normExpData = generateReducedNormalizedData.reducedROIData
//
//				displayInteractiveModel3 = DisplayInteractiveModel()
//					displayInteractiveModel3.optContext = generate2DOptContextUniformDisk3.optContext
//					displayInteractiveModel3.rois = generateROIs.bleachedROI_2D_array
//					displayInteractiveModel3.title = uniformDisk3PanelTitle
//

	}

}
