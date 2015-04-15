package org.vcell.vmicro.op.apps;

import java.io.File;

import org.vcell.vmicro.op.ComputeMeasurementErrorOp;
import org.vcell.vmicro.op.FitBleachSpotOp;
import org.vcell.vmicro.op.FitBleachSpotOp.FitBleachSpotOpResults;
import org.vcell.vmicro.op.Generate2DOptContextOp;
import org.vcell.vmicro.op.GenerateBleachRoiOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawTimeSeriesOp;
import org.vcell.vmicro.op.GenerateCellROIsFromRawTimeSeriesOp.GeneratedCellRoiResults;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp;
import org.vcell.vmicro.op.GenerateNormalizedFrapDataOp.NormalizedFrapDataResults;
import org.vcell.vmicro.op.GenerateReducedROIDataOp;
import org.vcell.vmicro.op.ImportRawTimeSeriesFromVFrapOp;
import org.vcell.vmicro.op.display.DisplayInteractiveModelOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk2P;
import org.vcell.vmicro.workflow.data.OptModelKenworthyUniformDisk3P;

import cbit.vcell.VirtualMicroscopy.FloatImage;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.math.RowColumnResultSet;

public class KenworthyWorkflowTest {

	public static void main(String[] args) {
		try {
			File workingDirectory = new File("/Users/schaff/Documents/workspace/VCell_5.4/workingDir");
			LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
			doit(localWorkspace);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}
	
	private static void doit(LocalWorkspace localWorkspace) throws Exception{
		double bleachThreshold = 0.80;
		double cellThreshold = 0.5;
		double prebleachFluor = 10;
		
		File vfrapFile = new File("/Users/schaff/Documents/workspace/VCell_5.4/vfrapPaper/rawData/sim3/workflow.txt.save");
		

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
					
		//
		// only use bleach ROI for fitting etc.
		//
		ROI[] dataROIs = new ROI[] { bleachROI };
		
		//
		// get reduced data and errors for each ROI
		//
		RowColumnResultSet reducedData = new GenerateReducedROIDataOp().generateReducedData(normalizedTimeSeries, dataROIs);
		RowColumnResultSet measurementErrors = new ComputeMeasurementErrorOp().computeNormalizedMeasurementError(
				dataROIs, indexOfFirstPostbleach, rawTimeSeriesImages, prebleachAvg, null);

		//
		// fit 2D Gaussian to normalized data to determine center, radius and K factor of bleach (assuming exp(-exp
		//
		FitBleachSpotOpResults fitSpotResults = new FitBleachSpotOp().fit(bleachROI, normalizedTimeSeries.getAllImages()[0]);
		double bleachFactorK_GaussianFit = fitSpotResults.bleachFactorK_GaussianFit;
		double bleachRadius_GaussianFit = fitSpotResults.bleachRadius_GaussianFit;
		double bleachRadius_ROI = fitSpotResults.bleachRadius_ROI;
		double centerX_GaussianFit = fitSpotResults.centerX_GaussianFit;
		double centerX_ROI = fitSpotResults.centerX_ROI;
		double centerY_GaussianFit = fitSpotResults.centerY_GaussianFit;
		double centerY_ROI = fitSpotResults.centerY_ROI;

		//
		// 2 parameter uniform disk model
		//
		OptModel uniformDisk2OptModel = new OptModelKenworthyUniformDisk2P(bleachRadius_GaussianFit);
		OptContext uniformDisk2Context = new Generate2DOptContextOp().generate2DOptContext(uniformDisk2OptModel, reducedData, measurementErrors);
		new DisplayInteractiveModelOp().displayOptModel(uniformDisk2Context, dataROIs, localWorkspace, "Uniform Disk Model - 2 parameters", null);

		//
		// 3 parameter uniform disk model
		//
		OptModel uniformDisk3OptModel = new OptModelKenworthyUniformDisk3P(bleachRadius_GaussianFit);
		OptContext uniformDisk3Context = new Generate2DOptContextOp().generate2DOptContext(uniformDisk3OptModel, reducedData, measurementErrors);
		new DisplayInteractiveModelOp().displayOptModel(uniformDisk3Context, dataROIs, localWorkspace, "Uniform Disk Model - 3 parameters", null);


	}

}
