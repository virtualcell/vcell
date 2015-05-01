package org.vcell.vmicro.workflow.task;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.Workflow;
import org.vcell.workflow.WorkflowParameter;
import org.vcell.workflow.WorkflowTask;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.RowColumnResultSet;

public class KenworthyProcess extends WorkflowTask {
	
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
	public final DataOutput<ROI> cellROI_2D;
	public final DataOutput<ImageTimeSeries> normalizedTimeSeries;
	public final DataOutput<RowColumnResultSet> reducedTimeSeries;
//	public final DataHolderOutputPort<ProfileData[]> profileData;

	
	private final GenerateCellROIsFromRawTimeSeries generateCellROIs = new GenerateCellROIsFromRawTimeSeries("k_generateCellROIs");
	private final GenerateNormalizedFrapData generateNormalizedFrapData = new GenerateNormalizedFrapData("k_generateNormalizedFrapData");
	private final GenerateBleachROI generateROIs = new GenerateBleachROI("k_generateROIs");
//	private final GenerateDependentImageROIs generateDependentROIs = new GenerateDependentImageROIs("generateDependentImageROIs");
	private final FitBleachSpot fitBleachSpot = new FitBleachSpot("k_fitBleachSpot");
	private final GenerateReducedData generateReducedNormalizedData = new GenerateReducedData("k_generateReducedNormalizedData");
	private final ComputeMeasurementError computeMeasurementError = new ComputeMeasurementError("k_computeMeasurementError");

	private final GenerateKenworthyOptModel generateUniformDiskModel2 = new GenerateKenworthyOptModel("k_generateUniformDiskModel2");
	private final GenerateKenworthyOptModel generateUniformDiskModel3 = new GenerateKenworthyOptModel("k_generateUniformDiskModel3");
	private final Generate2DOptContext generate2DOptContextUniformDisk2 = new Generate2DOptContext("k_generate2DOptContextUniformDisk2");
	private final Generate2DOptContext generate2DOptContextUniformDisk3 = new Generate2DOptContext("k_generate2DOptContextUniformDisk3");
	private final DisplayInteractiveModel displayInteractiveModel2 = new DisplayInteractiveModel("k_displayInteractiveModel2");
	private final DisplayInteractiveModel displayInteractiveModel3 = new DisplayInteractiveModel("k_displayInteractiveModel3");
	
	//
	// internal parameters
	//
	private WorkflowParameter<String> uniformDisk2ModelName;
	private WorkflowParameter<String> uniformDisk3ModelName;
	private WorkflowParameter<String> uniformDisk2PanelTitle;
	private WorkflowParameter<String> uniformDisk3PanelTitle;
	
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
		cellROI_2D = new DataOutput<ROI>(ROI.class,"cellROI_2D",this);
		normalizedTimeSeries = new DataOutput<ImageTimeSeries>(ImageTimeSeries.class, "normalizedTimeSeries", this);
		reducedTimeSeries = new DataOutput<RowColumnResultSet>(RowColumnResultSet.class, "reducedTimeSeries", this);
		addOutput(cellROI_2D);
		addOutput(normalizedTimeSeries);
		addOutput(reducedTimeSeries);
		
	}
	
	@Override
	protected void compute0(TaskContext context, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		//
		// set parameter values for sub-tasks.
		//
		context.setParameterValue(uniformDisk2ModelName, GenerateKenworthyOptModel.ModelType.KenworthyUniformDisk2Param.toString());
		context.setParameterValue(uniformDisk3ModelName, GenerateKenworthyOptModel.ModelType.KenworthyUniformDisk3Param.toString());
		context.setParameterValue(uniformDisk2PanelTitle, "Uniform Disk Model - 2 parameters");
		context.setParameterValue(uniformDisk3PanelTitle, "Uniform Disk Model - 3 parameters");
	}

	@Override
	public void addWorkflowComponents(Workflow workflow){
//		uniformDisk2ModelName = workflow.addParameter(String.class,"uniformDisk2ModelName");
//		uniformDisk3ModelName = workflow.addParameter(String.class,"uniformDisk3ModelName");
//		uniformDisk2PanelTitle = workflow.addParameter(String.class, "uniformDisk2PanelTitle");
//		uniformDisk3PanelTitle = workflow.addParameter(String.class, "uniformDisk3PanelTitle");
//
//		//
//		// construct the dataflow graph
//		//
//		workflow.joinInputs(this.cellThreshold, generateCellROIs.cellThreshold);
//		workflow.joinInputs(this.rawTimeSeriesImages, generateCellROIs.rawTimeSeriesImages);	
//
//		workflow.joinOutputs(generateCellROIs.cellROI_2D, this.cellROI_2D);
//		workflow.joinOutputs(generateNormalizedFrapData.normalizedFrapData, this.normalizedTimeSeries);
//		workflow.joinOutputs(generateReducedNormalizedData.reducedROIData, this.reducedTimeSeries);
//		
//		workflow.addTask(generateCellROIs);
//		
//		workflow.connect2(generateCellROIs.backgroundROI_2D, generateNormalizedFrapData.backgroundROI_2D);
//		workflow.connect2(generateCellROIs.indexOfFirstPostbleach, generateNormalizedFrapData.indexOfFirstPostbleach);
//		workflow.joinInputs(this.rawTimeSeriesImages, generateNormalizedFrapData.rawImageTimeSeries);	
//		workflow.addTask(generateNormalizedFrapData);
//		
//		workflow.joinInputs(this.bleachThreshold, generateROIs.bleachThreshold);
//		workflow.connect2(generateCellROIs.cellROI_2D, generateROIs.cellROI_2D);
//		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, generateROIs.normalizedTimeSeries);	
//		workflow.addTask(generateROIs);
//		
//		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, generateReducedNormalizedData.imageTimeSeries);
//		workflow.connect2(generateROIs.bleachedROI_2D_array, generateReducedNormalizedData.imageDataROIs);		
//		workflow.addTask(generateReducedNormalizedData);
//		
//		workflow.connect2(generateROIs.bleachedROI_2D_array, computeMeasurementError.imageDataROIs);
//		workflow.connect2(generateCellROIs.indexOfFirstPostbleach, computeMeasurementError.indexFirstPostbleach);
//		workflow.connect2(generateNormalizedFrapData.prebleachAverage, computeMeasurementError.prebleachAverage);
//		workflow.joinInputs(this.rawTimeSeriesImages, computeMeasurementError.rawImageTimeSeries);		
//		workflow.addTask(computeMeasurementError);
//		
//		
//		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, fitBleachSpot.normalizedImages);
//		workflow.connect2(generateROIs.bleachedROI_2D, fitBleachSpot.bleachROI);
//		workflow.addTask(fitBleachSpot);
//		
//		workflow.connect2(fitBleachSpot.bleachRadius_GaussianFit, generateUniformDiskModel2.bleachRadius);
//		workflow.connectParameter(uniformDisk2ModelName, generateUniformDiskModel2.modelType);		
//		workflow.addTask(generateUniformDiskModel2);
//		workflow.connect2(generateUniformDiskModel2.optModel, generate2DOptContextUniformDisk2.optModel);
//		workflow.connect2(computeMeasurementError.normalizedMeasurementError, generate2DOptContextUniformDisk2.normalizedMeasurementErrors);
//		workflow.connect2(generateReducedNormalizedData.reducedROIData, generate2DOptContextUniformDisk2.normExpData);
//		workflow.addTask(generate2DOptContextUniformDisk2);
//		workflow.connect2(generate2DOptContextUniformDisk2.optContext, displayInteractiveModel2.optContext);
//		workflow.connect2(generateROIs.bleachedROI_2D_array, displayInteractiveModel2.rois);
//		workflow.connectParameter(uniformDisk2PanelTitle, displayInteractiveModel2.title);
//		workflow.addTask(displayInteractiveModel2);
//
//		workflow.connect2(fitBleachSpot.bleachRadius_GaussianFit, generateUniformDiskModel3.bleachRadius);
//		workflow.connectParameter(uniformDisk3ModelName, generateUniformDiskModel3.modelType);		
//		workflow.addTask(generateUniformDiskModel3);
//		workflow.connect2(generateUniformDiskModel3.optModel, generate2DOptContextUniformDisk3.optModel);
//		workflow.connect2(computeMeasurementError.normalizedMeasurementError, generate2DOptContextUniformDisk3.normalizedMeasurementErrors);
//		workflow.connect2(generateReducedNormalizedData.reducedROIData, generate2DOptContextUniformDisk3.normExpData);
//		workflow.addTask(generate2DOptContextUniformDisk3);
//		workflow.connect2(generate2DOptContextUniformDisk3.optContext, displayInteractiveModel3.optContext);
//		workflow.connect2(generateROIs.bleachedROI_2D_array, displayInteractiveModel3.rois);
//		workflow.connectParameter(uniformDisk3PanelTitle, displayInteractiveModel3.title);
//		workflow.addTask(displayInteractiveModel3);
//
	}
}
