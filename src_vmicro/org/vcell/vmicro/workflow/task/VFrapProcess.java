package org.vcell.vmicro.workflow.task;

import org.vcell.optimization.ProfileData;
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

public class VFrapProcess extends WorkflowTask {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawTimeSeriesImages;
	public final DataInput<Double> bleachThreshold; // for automatic thresholding
	public final DataInput<Double> cellThreshold;   // for automatic thresholding
	
	//
	// outputs
	//
	public final DataOutput<ROI[]> imageDataROIs;
	public final DataOutput<ROI> cellROI_2D;
	public final DataOutput<ImageTimeSeries> normalizedTimeSeries;
	public final DataOutput<RowColumnResultSet> reducedTimeSeries;
	public final DataOutput<ProfileData[]> profileDataOne;
//	public final DataOutput<ProfileData[]> profileDataTwoWithoutPenalty;
	public final DataOutput<ProfileData[]> profileDataTwoWithPenalty;
	
	//
	// parameters
	//
	private WorkflowParameter<String> modelType1;
	private WorkflowParameter<String> modelType2WithPenalty;
	//private WorkflowParameter<String> modelType2WithoutPenalty;

	private Workflow workflow;
	private final GenerateCellROIsFromRawTimeSeries generateCellROIs = new GenerateCellROIsFromRawTimeSeries("v_generateCellROIs");
	private final GenerateNormalizedFrapData generateNormalizedFrapData = new GenerateNormalizedFrapData("v_generateNormalizedFrapData");
	private final GenerateBleachROI generateROIs = new GenerateBleachROI("v_generateROIs");
	private final GenerateDependentImageROIs generateDependentROIs = new GenerateDependentImageROIs("v_generateDependentImageROIs");
	private final GenerateReducedData generateReducedNormalizedData = new GenerateReducedData("v_generateReducedNormalizedData");
	private final ComputeMeasurementError computeMeasurementError = new ComputeMeasurementError("v_computeMeasurementError");
	private final GenerateTrivial2DPsf psf_2D = new GenerateTrivial2DPsf("v_generateTrivial2DPsf");

//	private final RunRefSimulation runRefSimulationFull = new RunRefSimulation("runRefSimulationFull");
//	private final GenerateReducedRefData generateReducedRefSimData = new GenerateReducedRefData("generateReducedRefSimData");
	
	private final RunRefSimulationFast runRefSimulationFast = new RunRefSimulationFast("v_runRefSimulationFast");
	private final GenerateRefSimOptModel generateRefSimOptModelOne = new GenerateRefSimOptModel("v_generateRefSimOptModelOne");
	private final Generate2DOptContext generate2DOptContextOne = new Generate2DOptContext("v_generate2DOptContextOne");
	private final RunProfileLikelihoodGeneral runProfileLikelihoodOne = new RunProfileLikelihoodGeneral("v_runProfileLikelihoodOne");
//	private final GenerateRefSimOptModel generateRefSimOptModelTwoWithoutPenalty = new GenerateRefSimOptModel("generateRefSimOptModelTwoWithoutPenalty");
//	private final Generate2DOptContext generate2DOptContextTwoWithoutPenalty = new Generate2DOptContext("generate2DOptContextTwoWithoutPenalty");
//	private final RunProfileLikelihoodGeneral runProfileLikelihoodTwoWithoutPenalty = new RunProfileLikelihoodGeneral("runProfileLikelihoodTwoWithoutPenalty");
	private final GenerateRefSimOptModel generateRefSimOptModelTwoWithPenalty = new GenerateRefSimOptModel("v_generateRefSimOptModelTwoWithPenalty");
	private final Generate2DOptContext generate2DOptContextTwoWithPenalty = new Generate2DOptContext("v_generate2DOptContextTwoWithPenalty");
	private final RunProfileLikelihoodGeneral runProfileLikelihoodTwoWithPenalty = new RunProfileLikelihoodGeneral("v_runProfileLikelihoodTwoWithPenalty");
	
	
	public VFrapProcess(String name) {
		super(name);
		rawTimeSeriesImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		bleachThreshold = new DataInput<Double>(Double.class,"bleachThreshold",this); // 0.80
		cellThreshold = new DataInput<Double>(Double.class,"cellThreshold",this); // 0.5
		addInput(rawTimeSeriesImages);
		addInput(bleachThreshold);
		addInput(cellThreshold);

		imageDataROIs = new DataOutput<ROI[]>(ROI[].class, "imageDataROIs", this);
		profileDataOne = new DataOutput<ProfileData[]>(ProfileData[].class, "profileDataOne", this);
//		profileDataTwoWithoutPenalty = new DataOutput<ProfileData[]>(ProfileData[].class, "profileDataTwoWithoutPenalty", this);
		profileDataTwoWithPenalty = new DataOutput<ProfileData[]>(ProfileData[].class, "profileDataTwoWithoutPenalty", this);
		
		addOutput(imageDataROIs);
		addOutput(profileDataOne);
		addOutput(profileDataTwoWithPenalty);
//		addOutput(profileDataTwoWithoutPenalty);
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
		context.setParameterValue(modelType1,GenerateRefSimOptModel.ModelType.DiffOne.toString());
		context.setParameterValue(modelType2WithPenalty, GenerateRefSimOptModel.ModelType.DiffTwoWithPenalty.toString());
		//context.setParameterValue(modelType2WithoutPenalty, ModelType.DiffTwoWithoutPenalty.toString());
	}

	@Override
	public void addWorkflowComponents(Workflow workflow){
		//
		// workflow parameters (hard coded)
		//
		modelType1 = workflow.addParameter(String.class,"modelType1");
		modelType2WithPenalty = workflow.addParameter(String.class,"modelType2WithPenalty");
		//modelType2WithoutPenalty = workflow.addParameter(String.class,"modelType2WithoutPenalty");

		workflow.joinOutputs(generateDependentROIs.imageDataROIs, this.imageDataROIs);
		workflow.joinOutputs(runProfileLikelihoodOne.profileData, this.profileDataOne);
		//workflow.joinOutputs(runProfileLikelihoodTwoWithoutPenalty.profileData, this.profileDataTwoWithoutPenalty);
		workflow.joinOutputs(runProfileLikelihoodTwoWithPenalty.profileData, this.profileDataTwoWithPenalty);
		
		workflow.joinOutputs(generateCellROIs.cellROI_2D, this.cellROI_2D);
		workflow.joinOutputs(generateNormalizedFrapData.normalizedFrapData, this.normalizedTimeSeries);
		workflow.joinOutputs(generateReducedNormalizedData.reducedROIData, reducedTimeSeries);
		
		workflow.joinInputs(this.cellThreshold, generateCellROIs.cellThreshold);
		workflow.joinInputs(this.rawTimeSeriesImages, generateCellROIs.rawTimeSeriesImages);	
		workflow.addTask(generateCellROIs);
		
		workflow.connect2(generateCellROIs.backgroundROI_2D, generateNormalizedFrapData.backgroundROI_2D);
		workflow.connect2(generateCellROIs.indexOfFirstPostbleach, generateNormalizedFrapData.indexOfFirstPostbleach);
		workflow.joinInputs(this.rawTimeSeriesImages, generateNormalizedFrapData.rawImageTimeSeries);	
		workflow.addTask(generateNormalizedFrapData);
		
		workflow.joinInputs(this.bleachThreshold, generateROIs.bleachThreshold);
		workflow.connect2(generateCellROIs.cellROI_2D, generateROIs.cellROI_2D);
		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, generateROIs.normalizedTimeSeries);	
		workflow.addTask(generateROIs);
		
		workflow.connect2(generateCellROIs.cellROI_2D, generateDependentROIs.cellROI_2D);
		workflow.connect2(generateROIs.bleachedROI_2D, generateDependentROIs.bleachedROI_2D);	
		workflow.addTask(generateDependentROIs);
		
		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, generateReducedNormalizedData.imageTimeSeries);
		workflow.connect2(generateDependentROIs.imageDataROIs, generateReducedNormalizedData.imageDataROIs);		
		workflow.addTask(generateReducedNormalizedData);
		
		workflow.connect2(generateDependentROIs.imageDataROIs, computeMeasurementError.imageDataROIs);
		workflow.connect2(generateCellROIs.indexOfFirstPostbleach, computeMeasurementError.indexFirstPostbleach);
		workflow.connect2(generateNormalizedFrapData.prebleachAverage, computeMeasurementError.prebleachAverage);
		workflow.joinInputs(this.rawTimeSeriesImages, computeMeasurementError.rawImageTimeSeries);		
		workflow.addTask(computeMeasurementError);
		
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
		
		//
		// FAST WAY
		//
		workflow.addTask(psf_2D);
		workflow.connect2(generateCellROIs.cellROI_2D, runRefSimulationFast.cellROI_2D);
		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, runRefSimulationFast.normalizedTimeSeries);
		workflow.connect2(generateDependentROIs.imageDataROIs, runRefSimulationFast.imageDataROIs);
		workflow.connect2(psf_2D.psf_2D, runRefSimulationFast.psf);
		workflow.addTask(runRefSimulationFast);			
		DataOutput<RowColumnResultSet> reducedROIData = runRefSimulationFast.reducedROIData;
		DataOutput<Double> refSimDiffusionRate = runRefSimulationFast.refSimDiffusionRate;
		
		//
		// model with One mobile fraction
		//
		{
		workflow.connectParameter(modelType1, generateRefSimOptModelOne.modelType);
		workflow.connect2(reducedROIData, generateRefSimOptModelOne.refSimData);
		workflow.connect2(refSimDiffusionRate, generateRefSimOptModelOne.refSimDiffusionRate);
		workflow.addTask(generateRefSimOptModelOne);
		workflow.connect2(generateRefSimOptModelOne.optModel, generate2DOptContextOne.optModel);
		workflow.connect2(computeMeasurementError.normalizedMeasurementError, generate2DOptContextOne.normalizedMeasurementErrors);
		workflow.connect2(generateReducedNormalizedData.reducedROIData, generate2DOptContextOne.normExpData);
		workflow.addTask(generate2DOptContextOne);
		workflow.connect2(generate2DOptContextOne.optContext, runProfileLikelihoodOne.optContext);
		workflow.addTask(runProfileLikelihoodOne);
		}
		//
		// model with Two mobile fractions (without penalty)
		//
//			{
//			generateRefSimOptModelTwoWithoutPenalty.modelType,modelType2WithoutPenalty);
//			generateRefSimOptModelTwoWithoutPenalty.refSimData,reducedROIData);
//			generateRefSimOptModelTwoWithoutPenalty.refSimDiffusionRate,refSimDiffusionRate);
//			workflow.addTask(generateRefSimOptModelTwoWithoutPenalty);
//			generate2DOptContextTwoWithoutPenalty.optModel,generateRefSimOptModelTwoWithoutPenalty.optModel);
//			generate2DOptContextTwoWithoutPenalty.normalizedMeasurementErrors,computeMeasurementError.normalizedMeasurementError);
//			generate2DOptContextTwoWithoutPenalty.normExpData,generateReducedNormalizedData.reducedROIData);
//			workflow.addTask(generate2DOptContextTwoWithoutPenalty);
//			runProfileLikelihoodTwoWithoutPenalty.optContext,generate2DOptContextTwoWithoutPenalty.optContext);
//			workflow.addTask(runProfileLikelihoodTwoWithoutPenalty);
//			}
		//
		// model with Two mobile fractions (with penalty)
		//
		{
		workflow.connectParameter(modelType2WithPenalty, generateRefSimOptModelTwoWithPenalty.modelType);
		workflow.connect2(reducedROIData, generateRefSimOptModelTwoWithPenalty.refSimData);
		workflow.connect2(refSimDiffusionRate, generateRefSimOptModelTwoWithPenalty.refSimDiffusionRate);
		workflow.addTask(generateRefSimOptModelTwoWithPenalty);
		workflow.connect2(generateRefSimOptModelTwoWithPenalty.optModel, generate2DOptContextTwoWithPenalty.optModel);
		workflow.connect2(computeMeasurementError.normalizedMeasurementError, generate2DOptContextTwoWithPenalty.normalizedMeasurementErrors);
		workflow.connect2(generateReducedNormalizedData.reducedROIData, generate2DOptContextTwoWithPenalty.normExpData);
		workflow.addTask(generate2DOptContextTwoWithPenalty);
		workflow.connect2(generate2DOptContextTwoWithPenalty.optContext, runProfileLikelihoodTwoWithPenalty.optContext);
		workflow.addTask(runProfileLikelihoodTwoWithPenalty);
		}		
	}
}
