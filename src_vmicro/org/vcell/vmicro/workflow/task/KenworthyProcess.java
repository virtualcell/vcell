package org.vcell.vmicro.workflow.task;

import org.vcell.optimization.ProfileData;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.vmicro.workflow.data.ModelType;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataHolderOutputPort;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;
import org.vcell.workflow.Workflow;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.RowColumnResultSet;

public class KenworthyProcess extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawTimeSeriesImages;
	public final DataInput<Double> bleachThreshold; // for automatic thresholding
	public final DataInput<Double> cellThreshold;   // for automatic thresholding
	
	//
	// outputs
	//
//	public final DataHolderOutputPort<ROI[]> imageDataROIs;
	public final DataHolderOutputPort<ROI> cellROI_2D;
	public final DataHolderOutputPort<ImageTimeSeries> normalizedTimeSeries;
	public final DataHolderOutputPort<RowColumnResultSet> reducedTimeSeries;
//	public final DataHolderOutputPort<ProfileData[]> profileData;

	
	private Workflow workflow;
	private final GenerateCellROIsFromRawTimeSeries generateCellROIs = new GenerateCellROIsFromRawTimeSeries("generateCellROIs");
	private final GenerateNormalizedFrapData generateNormalizedFrapData = new GenerateNormalizedFrapData("generateNormalizedFrapData");
	private final GenerateBleachROI generateROIs = new GenerateBleachROI("generateROIs");
//	private final GenerateDependentImageROIs generateDependentROIs = new GenerateDependentImageROIs("generateDependentImageROIs");
	private final FitBleachSpot fitBleachSpot = new FitBleachSpot("fitBleachSpot");
	private final GenerateReducedRefData generateReducedNormalizedData = new GenerateReducedRefData("generateReducedNormalizedData");
	private final ComputeMeasurementError computeMeasurementError = new ComputeMeasurementError("computeMeasurementError");

	private final GenerateKenworthyOptModel generateUniformDiskModel2 = new GenerateKenworthyOptModel("generateUniformDiskModel2");
	private final GenerateKenworthyOptModel generateUniformDiskModel3 = new GenerateKenworthyOptModel("generateUniformDiskModel3");
	private final GenerateKenworthyOptModel generateUniformDiskModel4 = new GenerateKenworthyOptModel("generateUniformDiskModel4");
	private final Generate2DOptContext generate2DOptContextUniformDisk2 = new Generate2DOptContext("generate2DOptContextUniformDisk2");
	private final Generate2DOptContext generate2DOptContextUniformDisk3 = new Generate2DOptContext("generate2DOptContextUniformDisk3");
	private final Generate2DOptContext generate2DOptContextUniformDisk4 = new Generate2DOptContext("generate2DOptContextUniformDisk4");
	private final DisplayInteractiveModel displayInteractiveModel2 = new DisplayInteractiveModel("displayInteractiveModel2");
	private final DisplayInteractiveModel displayInteractiveModel3 = new DisplayInteractiveModel("displayInteractiveModel3");
	private final DisplayInteractiveModel displayInteractiveModel4 = new DisplayInteractiveModel("displayInteractiveModel4");
	
	public KenworthyProcess(String name) {
		super(name);
		rawTimeSeriesImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		bleachThreshold = new DataInput<Double>(Double.class,"bleachThreshold",this); // 0.80
		cellThreshold = new DataInput<Double>(Double.class,"cellThreshold",this); // 0.5
		addInput(rawTimeSeriesImages);
		addInput(bleachThreshold);
		addInput(cellThreshold);

//		imageDataROIs = new DataHolderOutputPort<ROI[]>(ROI[].class, "imageDataROIs", this, generateDependentROIs.imageDataROIs);
//		profileData = new DataHolderOutputPort<ProfileData[]>(ProfileData[].class, "profileDataOne", this, runProfileLikelihoodUniformDisk.profileData);		
//		addOutput(imageDataROIs);
//		addOutput(profileData);
		cellROI_2D = new DataHolderOutputPort<ROI>(ROI.class,"cellROI_2D",this, generateCellROIs.cellROI_2D);
		normalizedTimeSeries = new DataHolderOutputPort<ImageTimeSeries>(ImageTimeSeries.class, "normalizedTimeSeries", this, generateNormalizedFrapData.normalizedFrapData);
		reducedTimeSeries = new DataHolderOutputPort<RowColumnResultSet>(RowColumnResultSet.class, "reducedTimeSeries", this, generateReducedNormalizedData.reducedROIData);
		addOutput(cellROI_2D);
		addOutput(normalizedTimeSeries);
		addOutput(reducedTimeSeries);
		
	}

	@Override
	protected void compute0(ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		getWorkflow().compute(clientTaskStatusSupport);
	}
	
	
	public Workflow getWorkflow(){
		if (workflow == null){
			//
			// construct the dataflow graph
			//
			workflow = new Workflow(getLocalWorkspace());
			
			generateCellROIs.cellThreshold.setSource(cellThreshold);
			generateCellROIs.rawTimeSeriesImages.setSource(rawTimeSeriesImages);	
			workflow.addTask(generateCellROIs);
			
			generateNormalizedFrapData.backgroundROI_2D.setSource(generateCellROIs.backgroundROI_2D);
			generateNormalizedFrapData.indexOfFirstPostbleach.setSource(generateCellROIs.indexOfFirstPostbleach);
			generateNormalizedFrapData.rawImageTimeSeries.setSource(rawTimeSeriesImages);	
			workflow.addTask(generateNormalizedFrapData);
			
			generateROIs.bleachThreshold.setSource(bleachThreshold);
			generateROIs.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
			generateROIs.normalizedTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);	
			workflow.addTask(generateROIs);
			
			generateReducedNormalizedData.imageTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
			generateReducedNormalizedData.imageDataROIs.setSource(generateROIs.bleachedROI_2D_array);		
			workflow.addTask(generateReducedNormalizedData);
			
			computeMeasurementError.imageDataROIs.setSource(generateROIs.bleachedROI_2D_array);
			computeMeasurementError.indexFirstPostbleach.setSource(generateCellROIs.indexOfFirstPostbleach);
			computeMeasurementError.prebleachAverage.setSource(generateNormalizedFrapData.prebleachAverage);
			computeMeasurementError.rawImageTimeSeries.setSource(rawTimeSeriesImages);		
			workflow.addTask(computeMeasurementError);
			
			
			fitBleachSpot.normalizedImages.setSource(generateNormalizedFrapData.normalizedFrapData);
			fitBleachSpot.bleachROI.setSource(generateROIs.bleachedROI_2D);
			workflow.addTask(fitBleachSpot);
			
			generateUniformDiskModel2.bleachRadius.setSource(fitBleachSpot.bleachRadius);
			generateUniformDiskModel2.modelType.setSource(workflow.addParameter(String.class,"uniformDisk",ModelType.KenworthyUniformDisk2Param.toString()));		
			workflow.addTask(generateUniformDiskModel2);
			generate2DOptContextUniformDisk2.optModel.setSource(generateUniformDiskModel2.optModel);
			generate2DOptContextUniformDisk2.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
			generate2DOptContextUniformDisk2.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
			workflow.addTask(generate2DOptContextUniformDisk2);
			displayInteractiveModel2.optContext.setSource(generate2DOptContextUniformDisk2.optContext);
			displayInteractiveModel2.rois.setSource(generateROIs.bleachedROI_2D_array);
			displayInteractiveModel2.title.setSource(workflow.addParameter(String.class, "displayTitle", "Uniform Disk Model - 2 parameters"));
			workflow.addTask(displayInteractiveModel2);

			generateUniformDiskModel3.bleachRadius.setSource(fitBleachSpot.bleachRadius);
			generateUniformDiskModel3.modelType.setSource(workflow.addParameter(String.class,"uniformDisk",ModelType.KenworthyUniformDisk3Param.toString()));		
			workflow.addTask(generateUniformDiskModel3);
			generate2DOptContextUniformDisk3.optModel.setSource(generateUniformDiskModel3.optModel);
			generate2DOptContextUniformDisk3.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
			generate2DOptContextUniformDisk3.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
			workflow.addTask(generate2DOptContextUniformDisk3);
			displayInteractiveModel3.optContext.setSource(generate2DOptContextUniformDisk3.optContext);
			displayInteractiveModel3.rois.setSource(generateROIs.bleachedROI_2D_array);
			displayInteractiveModel2.title.setSource(workflow.addParameter(String.class, "displayTitle", "Uniform Disk Model - 3 parameters"));
			workflow.addTask(displayInteractiveModel3);

			generateUniformDiskModel4.bleachRadius.setSource(fitBleachSpot.bleachRadius);
			generateUniformDiskModel4.modelType.setSource(workflow.addParameter(String.class,"uniformDisk",ModelType.KenworthyUniformDisk4Param.toString()));		
			workflow.addTask(generateUniformDiskModel4);
			generate2DOptContextUniformDisk4.optModel.setSource(generateUniformDiskModel4.optModel);
			generate2DOptContextUniformDisk4.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
			generate2DOptContextUniformDisk4.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
			workflow.addTask(generate2DOptContextUniformDisk4);
			displayInteractiveModel4.optContext.setSource(generate2DOptContextUniformDisk4.optContext);
			displayInteractiveModel4.rois.setSource(generateROIs.bleachedROI_2D_array);
			displayInteractiveModel2.title.setSource(workflow.addParameter(String.class, "displayTitle", "Uniform Disk Model - 4 parameters"));
			workflow.addTask(displayInteractiveModel4);

		}			
		return workflow;
	}
}
