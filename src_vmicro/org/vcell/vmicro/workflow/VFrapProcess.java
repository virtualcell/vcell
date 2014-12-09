package org.vcell.vmicro.workflow;

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

public class VFrapProcess extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> rawTimeSeriesImages;
	public final DataInput<Double> bleachThreshold; // for automatic thresholding
	public final DataInput<Double> cellThreshold;   // for automatic thresholding
	
	//
	// outputs
	//
	public final DataHolderOutputPort<ROI[]> imageDataROIs;
	public final DataHolderOutputPort<ROI> cellROI_2D;
	public final DataHolderOutputPort<ImageTimeSeries> normalizedTimeSeries;
	public final DataHolderOutputPort<RowColumnResultSet> reducedTimeSeries;
	public final DataHolderOutputPort<ProfileData[]> profileDataOne;
//	public final DataHolderOutputPort<ProfileData[]> profileDataTwoWithoutPenalty;
	public final DataHolderOutputPort<ProfileData[]> profileDataTwoWithPenalty;

	private Workflow workflow;
	private final GenerateCellROIsFromRawTimeSeries generateCellROIs = new GenerateCellROIsFromRawTimeSeries("generateCellROIs");
	private final GenerateNormalizedFrapData generateNormalizedFrapData = new GenerateNormalizedFrapData("generateNormalizedFrapData");
	private final GenerateBleachROI generateROIs = new GenerateBleachROI("generateROIs");
	private final GenerateDependentImageROIs generateDependentROIs = new GenerateDependentImageROIs("generateDependentImageROIs");
	private final GenerateReducedRefData generateReducedNormalizedData = new GenerateReducedRefData("generateReducedNormalizedData");
	private final ComputeMeasurementError computeMeasurementError = new ComputeMeasurementError("computeMeasurementError");
	private final GenerateTrivial2DPsf psf_2D = new GenerateTrivial2DPsf("generateTrivial2DPsf");

//	private final RunRefSimulation runRefSimulationFull = new RunRefSimulation("runRefSimulationFull");
//	private final GenerateReducedRefData generateReducedRefSimData = new GenerateReducedRefData("generateReducedRefSimData");
	
	private final RunRefSimulationFast runRefSimulationFast = new RunRefSimulationFast("runRefSimulationFast");
	private final GenerateRefSimOptModel generateRefSimOptModelOne = new GenerateRefSimOptModel("generateRefSimOptModelOne");
	private final Generate2DOptContext generate2DOptContextOne = new Generate2DOptContext("generate2DOptContextOne");
	private final RunProfileLikelihoodGeneral runProfileLikelihoodOne = new RunProfileLikelihoodGeneral("runProfileLikelihoodOne");
//	private final GenerateRefSimOptModel generateRefSimOptModelTwoWithoutPenalty = new GenerateRefSimOptModel("generateRefSimOptModelTwoWithoutPenalty");
//	private final Generate2DOptContext generate2DOptContextTwoWithoutPenalty = new Generate2DOptContext("generate2DOptContextTwoWithoutPenalty");
//	private final RunProfileLikelihoodGeneral runProfileLikelihoodTwoWithoutPenalty = new RunProfileLikelihoodGeneral("runProfileLikelihoodTwoWithoutPenalty");
	private final GenerateRefSimOptModel generateRefSimOptModelTwoWithPenalty = new GenerateRefSimOptModel("generateRefSimOptModelTwoWithPenalty");
	private final Generate2DOptContext generate2DOptContextTwoWithPenalty = new Generate2DOptContext("generate2DOptContextTwoWithPenalty");
	private final RunProfileLikelihoodGeneral runProfileLikelihoodTwoWithPenalty = new RunProfileLikelihoodGeneral("runProfileLikelihoodTwoWithPenalty");
	
	
	public VFrapProcess(String name) {
		super(name);
		rawTimeSeriesImages = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"rawTimeSeriesImages",this);
		bleachThreshold = new DataInput<Double>(Double.class,"bleachThreshold",this); // 0.80
		cellThreshold = new DataInput<Double>(Double.class,"cellThreshold",this); // 0.5
		addInput(rawTimeSeriesImages);
		addInput(bleachThreshold);
		addInput(cellThreshold);

		imageDataROIs = new DataHolderOutputPort<ROI[]>(ROI[].class, "imageDataROIs", this, generateDependentROIs.imageDataROIs);
		profileDataOne = new DataHolderOutputPort<ProfileData[]>(ProfileData[].class, "profileDataOne", this, runProfileLikelihoodOne.profileData);
//		profileDataTwoWithoutPenalty = new DataHolderOutputPort<ProfileData[]>(ProfileData[].class, "profileDataTwoWithoutPenalty", this, runProfileLikelihoodTwoWithoutPenalty.profileData);
		profileDataTwoWithPenalty = new DataHolderOutputPort<ProfileData[]>(ProfileData[].class, "profileDataTwoWithoutPenalty", this, runProfileLikelihoodTwoWithPenalty.profileData);
		
		addOutput(imageDataROIs);
		addOutput(profileDataOne);
		addOutput(profileDataTwoWithPenalty);
//		addOutput(profileDataTwoWithoutPenalty);
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
	
			//
			// workflow parameters (hard coded)
			//
			DataHolder<String> modelTypeOne = workflow.addParameter(String.class,"modelType",ModelType.DiffOne.toString());
			DataHolder<String> modelTypeTwoWithPenalty = workflow.addParameter(String.class,"modelType2",ModelType.DiffTwoWithPenalty.toString());
			DataHolder<String> modelTypeTwoWithoutPenalty = workflow.addParameter(String.class,"modelType3",ModelType.DiffTwoWithoutPenalty.toString());
	
			
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
			
			generateDependentROIs.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
			generateDependentROIs.bleachedROI_2D.setSource(generateROIs.bleachedROI_2D);	
			workflow.addTask(generateDependentROIs);
			
			generateReducedNormalizedData.imageTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
			generateReducedNormalizedData.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);		
			workflow.addTask(generateReducedNormalizedData);
			
			computeMeasurementError.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
			computeMeasurementError.indexFirstPostbleach.setSource(generateCellROIs.indexOfFirstPostbleach);
			computeMeasurementError.prebleachAverage.setSource(generateNormalizedFrapData.prebleachAverage);
			computeMeasurementError.rawImageTimeSeries.setSource(rawTimeSeriesImages);		
			workflow.addTask(computeMeasurementError);
			
			//
			// SLOW WAY
			//
//			runRefSimulationFull.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
//			runRefSimulationFull.normalizedTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
//			workflow.addTask(runRefSimulationFull);
//			generateReducedRefSimData.imageTimeSeries.setSource(runRefSimulationFull.refSimTimeSeries);
//			generateReducedRefSimData.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
//			workflow.addTask(generateReducedRefSimData);
//			DataHolder<RowColumnResultSet> reducedROIData = generateReducedRefSimData.reducedROIData;
//			DataHolder<Double> refSimDiffusionRate = runRefSimulationFull.refSimDiffusionRate;
			
			//
			// FAST WAY
			//
			workflow.addTask(psf_2D);
			runRefSimulationFast.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
			runRefSimulationFast.normalizedTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
			runRefSimulationFast.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
			runRefSimulationFast.psf.setSource(psf_2D.psf_2D);
			workflow.addTask(runRefSimulationFast);			
			DataHolder<RowColumnResultSet> reducedROIData = runRefSimulationFast.reducedROIData;
			DataHolder<Double> refSimDiffusionRate = runRefSimulationFast.refSimDiffusionRate;
			
			//
			// model with One mobile fraction
			//
			{
			generateRefSimOptModelOne.modelType.setSource(modelTypeOne);
			generateRefSimOptModelOne.refSimData.setSource(reducedROIData);
			generateRefSimOptModelOne.refSimDiffusionRate.setSource(refSimDiffusionRate);
			workflow.addTask(generateRefSimOptModelOne);
			generate2DOptContextOne.optModel.setSource(generateRefSimOptModelOne.optModel);
			generate2DOptContextOne.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
			generate2DOptContextOne.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
			workflow.addTask(generate2DOptContextOne);
			runProfileLikelihoodOne.optContext.setSource(generate2DOptContextOne.optContext);
			workflow.addTask(runProfileLikelihoodOne);
			}
			//
			// model with Two mobile fractions (without penalty)
			//
//			{
//			generateRefSimOptModelTwoWithoutPenalty.modelType.setSource(modelTypeTwoWithoutPenalty);
//			generateRefSimOptModelTwoWithoutPenalty.refSimData.setSource(reducedROIData);
//			generateRefSimOptModelTwoWithoutPenalty.refSimDiffusionRate.setSource(refSimDiffusionRate);
//			workflow.addTask(generateRefSimOptModelTwoWithoutPenalty);
//			generate2DOptContextTwoWithoutPenalty.optModel.setSource(generateRefSimOptModelTwoWithoutPenalty.optModel);
//			generate2DOptContextTwoWithoutPenalty.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
//			generate2DOptContextTwoWithoutPenalty.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
//			workflow.addTask(generate2DOptContextTwoWithoutPenalty);
//			runProfileLikelihoodTwoWithoutPenalty.optContext.setSource(generate2DOptContextTwoWithoutPenalty.optContext);
//			workflow.addTask(runProfileLikelihoodTwoWithoutPenalty);
//			}
			//
			// model with Two mobile fractions (with penalty)
			//
			{
			generateRefSimOptModelTwoWithPenalty.modelType.setSource(modelTypeTwoWithPenalty);
			generateRefSimOptModelTwoWithPenalty.refSimData.setSource(reducedROIData);
			generateRefSimOptModelTwoWithPenalty.refSimDiffusionRate.setSource(refSimDiffusionRate);
			workflow.addTask(generateRefSimOptModelTwoWithPenalty);
			generate2DOptContextTwoWithPenalty.optModel.setSource(generateRefSimOptModelTwoWithPenalty.optModel);
			generate2DOptContextTwoWithPenalty.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
			generate2DOptContextTwoWithPenalty.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
			workflow.addTask(generate2DOptContextTwoWithPenalty);
			runProfileLikelihoodTwoWithPenalty.optContext.setSource(generate2DOptContextTwoWithPenalty.optContext);
			workflow.addTask(runProfileLikelihoodTwoWithPenalty);
			}
		}		
		return workflow;
	}
}
