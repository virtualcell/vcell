package org.vcell.vmicro.workflow.test;

import java.io.File;
import java.util.ArrayList;

import org.vcell.util.BeanUtils;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.task.ComputeMeasurementError;
import org.vcell.vmicro.workflow.task.DisplayDependentROIs;
import org.vcell.vmicro.workflow.task.DisplayProfileLikelihoodPlots;
import org.vcell.vmicro.workflow.task.DisplayTimeSeries;
import org.vcell.vmicro.workflow.task.Generate2DOptContext;
import org.vcell.vmicro.workflow.task.GenerateBleachROI;
import org.vcell.vmicro.workflow.task.GenerateCellROIsFromRawTimeSeries;
import org.vcell.vmicro.workflow.task.GenerateDependentImageROIs;
import org.vcell.vmicro.workflow.task.GenerateNormalizedFrapData;
import org.vcell.vmicro.workflow.task.GenerateReducedData;
import org.vcell.vmicro.workflow.task.GenerateRefSimOptModel;
import org.vcell.vmicro.workflow.task.GenerateTrivial2DPsf;
import org.vcell.vmicro.workflow.task.ImportRawTimeSeriesFromVFrap;
import org.vcell.vmicro.workflow.task.RunProfileLikelihoodGeneral;
import org.vcell.vmicro.workflow.task.RunRefSimulationFast;
import org.vcell.vmicro.workflow.task.VFrapProcess;
import org.vcell.workflow.MemoryRepository;
import org.vcell.workflow.Repository;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.Workflow;
import org.vcell.workflow.WorkflowParameter;

public class WorkflowTest {
	
	public static void main(String[] args){
		if (args.length!=2){
			System.out.println("expecting 2 arguments");
			System.out.println("usage: java "+Workflow.class.getSimpleName()+" workingdir workflowInputFile");
			System.out.println("workingdir example: "+"D:\\developer\\eclipse\\workspace\\VCell_5.4_vmicro\\datadir");
			System.out.println("workflowInputFile example: "+"D:\\developer\\eclipse\\workspace\\VCell_5.4_vmicro\\workflow1.txt");
			System.exit(1);
		}
		try {
			//PropertyLoader.loadProperties();
			// workflowInputFile "C:\\developer\\eclipse\\workspace\\VCell_5.3_vmicro\\workflow1.txt"
			File workingDirectory = new File(args[0]);
			LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
			Repository repository = new MemoryRepository();

			String workflowLanguageText = BeanUtils.readBytesFromFile(new File(args[1]), null);
			Workflow workflow = Workflow.parse(repository, localWorkspace, workflowLanguageText);

			System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> using hard-coded example instead <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//			File vfrapFile = new File("D:\\Developer\\eclipse\\workspace_refactor\\VCell_5.4_vmicro\\3D_FRAP_2_ZProjection_Simulation1.vfrap");
//			Workflow workflow = getVFrapSimpleExample(workingDirectory, vfrapFile);
			
			TaskContext taskContext = new TaskContext(workflow,repository,localWorkspace);

			ArrayList<Issue> issues = new ArrayList<Issue>();
			IssueContext issueContext = new IssueContext();
			workflow.gatherIssues(issueContext, issues);
			
//			WorkflowJGraphProxy workflowJGraphProxy = new WorkflowJGraphProxy(workflow);
//			displayWorkflowGraphJGraphX(workflowJGraphProxy);
			
			WorkflowUtilities.displayWorkflowGraph(workflow);
			
			WorkflowUtilities.displayWorkflowTable(taskContext);
			
			workflow.reportIssues(issues, Issue.SEVERITY_INFO, true);
			
			//
			// execute the workflow
			//
			workflow.compute(taskContext, new WorkflowUtilities.Progress());
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}


	
	public static Workflow getVFrapExample(Repository repository, File workingDirectory, File vfrapFile){
		
		
		
		//
		// construct the dataflow graph
		//
		LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
		Workflow workflow = new Workflow("main");
		TaskContext context = new TaskContext(workflow,repository,localWorkspace);

		//
		// workflow parameters
		//
		WorkflowParameter<Double> maxIntensity = workflow.addParameter(Double.class,"maxIntensity");
		context.setParameterValue(maxIntensity,10000.0);
		
		WorkflowParameter<Boolean> bNoise = workflow.addParameter(Boolean.class,"bNoise");
		context.setParameterValue(bNoise, true);
		
		WorkflowParameter<File> vfrapFileParam = workflow.addParameter(File.class,"vfrapFile");
		context.setParameterValue(vfrapFileParam,vfrapFile);
		
		WorkflowParameter<Double> bleachThreshold = workflow.addParameter(Double.class,"bleachThreshold");
		context.setParameterValue(bleachThreshold, 0.80);
		
		WorkflowParameter<Double> cellThreshold = workflow.addParameter(Double.class,"cellThreshold");
		context.setParameterValue(cellThreshold,0.5);

		WorkflowParameter<String> modelTypeOne = workflow.addParameter(String.class,"modelType");
		context.setParameterValue(modelTypeOne, GenerateRefSimOptModel.ModelType.DiffOne.toString());

		WorkflowParameter<String> modelTypeTwo = workflow.addParameter(String.class,"modelType2");
		context.setParameterValue(modelTypeTwo, GenerateRefSimOptModel.ModelType.DiffTwoWithPenalty.toString());

		WorkflowParameter<String> displayROITitle = workflow.addParameter(String.class,"displayROITitle");
		context.setParameterValue(displayROITitle, "rois");
				
		// input data from VFrap file
		ImportRawTimeSeriesFromVFrap timeSeriesFromVCell = new ImportRawTimeSeriesFromVFrap("frapImport");
		workflow.connectParameter(vfrapFileParam,timeSeriesFromVCell.vfrapFile);
		workflow.addTask(timeSeriesFromVCell);

//		// input data from HDF5 postprocessing (includes PSF)
//		ImportRawTimeSeriesFromHdf5Fluor timeSeriesFromVCell = new ImportRawTimeSeriesFromHdf5Fluor("t1");
//		timeSeriesFromVCell.bNoise.setSource(bNoise);
//		timeSeriesFromVCell.maxIntensity.setSource(maxIntensity);
//		timeSeriesFromVCell.fluorDataName.setSource(hdf5FluorName);
//		timeSeriesFromVCell.vcellHdf5File.setSource(hdf5File);
//		timeSeriesFromVCell.zSliceIndex.setSource(hdf5ZSliceIndex);
//		workflow.addTask(timeSeriesFromVCell);
		
		GenerateCellROIsFromRawTimeSeries generateCellROIs = new GenerateCellROIsFromRawTimeSeries("generateCellROIs");
		workflow.connectParameter(cellThreshold, generateCellROIs.cellThreshold);
		workflow.connect2(timeSeriesFromVCell.rawTimeSeriesImages, generateCellROIs.rawTimeSeriesImages);
		workflow.addTask(generateCellROIs);
		
		GenerateNormalizedFrapData generateNormalizedFrapData = new GenerateNormalizedFrapData("generateNormalizedFrapData");
		workflow.connect2(generateCellROIs.backgroundROI_2D, generateNormalizedFrapData.backgroundROI_2D);
		workflow.connect2(generateCellROIs.indexOfFirstPostbleach, generateNormalizedFrapData.indexOfFirstPostbleach);
		workflow.connect2(timeSeriesFromVCell.rawTimeSeriesImages, generateNormalizedFrapData.rawImageTimeSeries);	
		workflow.addTask(generateNormalizedFrapData);
		
		GenerateBleachROI generateROIs = new GenerateBleachROI("generateROIs");
		workflow.connectParameter(bleachThreshold, generateROIs.bleachThreshold);
		workflow.connect2(generateCellROIs.cellROI_2D, generateROIs.cellROI_2D);
		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, generateROIs.normalizedTimeSeries);	
		workflow.addTask(generateROIs);
		
		GenerateDependentImageROIs generateDependentROIs = new GenerateDependentImageROIs("generateDependentROIs");
		workflow.connect2(generateCellROIs.cellROI_2D, generateDependentROIs.cellROI_2D);
		workflow.connect2(generateROIs.bleachedROI_2D, generateDependentROIs.bleachedROI_2D);	
		workflow.addTask(generateDependentROIs);
		
		DisplayDependentROIs displayDependentROIs = new DisplayDependentROIs("displayDependentROIs");
		workflow.connect2(generateDependentROIs.imageDataROIs, displayDependentROIs.imageROIs);
		workflow.connect2(generateCellROIs.cellROI_2D, displayDependentROIs.cellROI);
		workflow.connectParameter(displayROITitle, displayDependentROIs.title);
		workflow.addTask(displayDependentROIs);
				
		GenerateReducedData generateReducedNormalizedData = new GenerateReducedData("generateReducedNormalizedData");
		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, generateReducedNormalizedData.imageTimeSeries);
		workflow.connect2(generateDependentROIs.imageDataROIs, generateReducedNormalizedData.imageDataROIs);		
		workflow.addTask(generateReducedNormalizedData);
		
		ComputeMeasurementError computeMeasurementError = new ComputeMeasurementError("computeMeasurementError");
		workflow.connect2(generateDependentROIs.imageDataROIs, computeMeasurementError.imageDataROIs);
		workflow.connect2(generateCellROIs.indexOfFirstPostbleach, computeMeasurementError.indexFirstPostbleach);
		workflow.connect2(generateNormalizedFrapData.prebleachAverage, computeMeasurementError.prebleachAverage);
		workflow.connect2(timeSeriesFromVCell.rawTimeSeriesImages, computeMeasurementError.rawImageTimeSeries);		
		workflow.addTask(computeMeasurementError);
		
		//
		// SLOW WAY
		//
//		RunRefSimulation runRefSimulationFull = new RunRefSimulation("runRefSimulationFull");
//		runRefSimulationFull.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
//		runRefSimulationFull.normalizedTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
//		workflow.addTask(runRefSimulationFull);
//		GenerateReducedRefData generateReducedRefSimData = new GenerateReducedRefData("generateReducedRefSimData");
//		generateReducedRefSimData.imageTimeSeries.setSource(runRefSimulationFull.refSimTimeSeries);
//		generateReducedRefSimData.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
//		workflow.addTask(generateReducedRefSimData);
//		Generate2DOptContext generate2DOptContextFull = new Generate2DOptContext("generate2DOptContextFull");
//		generate2DOptContextFull.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
//		generate2DOptContextFull.modelType.setSource(modelType);
//		generate2DOptContextFull.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
//		generate2DOptContextFull.refSimData.setSource(generateReducedRefSimData.reducedROIData);
//		generate2DOptContextFull.refSimDiffusionRate.setSource(runRefSimulationFull.refSimDiffusionRate);
//		workflow.addTask(generate2DOptContextFull);
		
		//
		// FAST WAY
		//
		GenerateTrivial2DPsf psf_2D = new GenerateTrivial2DPsf("psf_2D");
		workflow.addTask(psf_2D);
		RunRefSimulationFast runRefSimulationFast = new RunRefSimulationFast("runRefSimulationFast");
		workflow.connect2(generateCellROIs.cellROI_2D, runRefSimulationFast.cellROI_2D);
		workflow.connect2(generateNormalizedFrapData.normalizedFrapData, runRefSimulationFast.normalizedTimeSeries);
		workflow.connect2(generateDependentROIs.imageDataROIs, runRefSimulationFast.imageDataROIs);
		workflow.connect2(psf_2D.psf_2D, runRefSimulationFast.psf);
		workflow.addTask(runRefSimulationFast);
		
		//
		// model with One mobile fraction
		//
		{
		GenerateRefSimOptModel generateRefSimOptModelOne = new GenerateRefSimOptModel("generateRefSimOptModel");
		workflow.connectParameter(modelTypeOne, generateRefSimOptModelOne.modelType);
		workflow.connect2(runRefSimulationFast.reducedROIData, generateRefSimOptModelOne.refSimData);
		workflow.connect2(runRefSimulationFast.refSimDiffusionRate, generateRefSimOptModelOne.refSimDiffusionRate);
		workflow.addTask(generateRefSimOptModelOne);
		Generate2DOptContext generate2DOptContextOne = new Generate2DOptContext("generate2DOptContextOne");
		workflow.connect2(computeMeasurementError.normalizedMeasurementError, generate2DOptContextOne.normalizedMeasurementErrors);
		workflow.connect2(generateReducedNormalizedData.reducedROIData, generate2DOptContextOne.normExpData);
		workflow.connect2(generateRefSimOptModelOne.optModel, generate2DOptContextOne.optModel);
		workflow.addTask(generate2DOptContextOne);
		RunProfileLikelihoodGeneral runProfileLikelihoodOne = new RunProfileLikelihoodGeneral("runProfileLikelihoodOne");
		workflow.connect2(generate2DOptContextOne.optContext, runProfileLikelihoodOne.optContext);
		workflow.addTask(runProfileLikelihoodOne);
		}
		//
		// model with One mobile fraction
		//
		GenerateRefSimOptModel generateRefSimOptModelTwo = new GenerateRefSimOptModel("generateRefSimOptModelTwo");
		workflow.connectParameter(modelTypeTwo, generateRefSimOptModelTwo.modelType);
		workflow.connect2(runRefSimulationFast.reducedROIData, generateRefSimOptModelTwo.refSimData);
		workflow.connect2(runRefSimulationFast.refSimDiffusionRate, generateRefSimOptModelTwo.refSimDiffusionRate);
		workflow.addTask(generateRefSimOptModelTwo);
		Generate2DOptContext generate2DOptContextTwo = new Generate2DOptContext("generate2DOptContextTwo");
		workflow.connect2(computeMeasurementError.normalizedMeasurementError, generate2DOptContextTwo.normalizedMeasurementErrors);
		workflow.connect2(generateRefSimOptModelTwo.optModel, generate2DOptContextTwo.optModel);
		workflow.connect2(generateReducedNormalizedData.reducedROIData, generate2DOptContextTwo.normExpData);
		workflow.addTask(generate2DOptContextTwo);
		RunProfileLikelihoodGeneral runProfileLikelihoodTwo = new RunProfileLikelihoodGeneral("runProfileLikelihoodTwo");
		workflow.connect2(generate2DOptContextTwo.optContext, runProfileLikelihoodTwo.optContext);
		workflow.addTask(runProfileLikelihoodTwo);
		
		return workflow;

	}

	public static Workflow getVFrapSimpleExample(File workingDirectory, File vfrapFile){
		
		//
		// construct the dataflow graph
		//
		LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
		Workflow workflow = new Workflow("main");
		Repository repository = new MemoryRepository();
		TaskContext context = new TaskContext(workflow, repository, localWorkspace);

		//
		// workflow parameters
		//
		WorkflowParameter<File> vfrapFileParam = workflow.addParameter(File.class,"vfrapFile");
		context.setParameterValue(vfrapFileParam,vfrapFile);
		
		WorkflowParameter<Double> bleachThreshold = workflow.addParameter(Double.class,"bleachThreshold");
		context.setParameterValue(bleachThreshold, 0.80);
		
		WorkflowParameter<Double> cellThreshold = workflow.addParameter(Double.class,"cellThreshold");
		context.setParameterValue(cellThreshold,0.5);

		WorkflowParameter<String> displayROITitle = workflow.addParameter(String.class,"displayROITitle");
		context.setParameterValue(displayROITitle, "rois");
				
		WorkflowParameter<String> displayProfileOneTitle = workflow.addParameter(String.class,"displayProfileOneTitle");
		context.setParameterValue(displayProfileOneTitle, "1 Diffusing");
				
		WorkflowParameter<String> displayProfileTwoWithoutPenaltyTitle = workflow.addParameter(String.class,"displayProfileTwoTitle");
		context.setParameterValue(displayProfileTwoWithoutPenaltyTitle, "2 Diffusing - no penalty");
		
		WorkflowParameter<String> displayProfileTwoWithPenaltyTitle = workflow.addParameter(String.class,"displayProfileTwoTitle");
		context.setParameterValue(displayProfileTwoWithPenaltyTitle,"2 Diffusing - with penalty");
				
		WorkflowParameter<String> displayRawImagesTitle = workflow.addParameter(String.class, "displayRawImagesTitle");
		context.setParameterValue(displayRawImagesTitle, "raw Images from "+ImportRawTimeSeriesFromVFrap.class.getName());

		ImportRawTimeSeriesFromVFrap importFromVFrap = new ImportRawTimeSeriesFromVFrap("importFromVFrap");
		workflow.connectParameter(vfrapFileParam,  importFromVFrap.vfrapFile);
		workflow.addTask(importFromVFrap);
		
		DisplayTimeSeries displayRawImages = new DisplayTimeSeries("displayRawImages");
		workflow.connect2(importFromVFrap.rawTimeSeriesImages, displayRawImages.imageTimeSeries);
		workflow.connectParameter(displayRawImagesTitle, displayRawImages.title);
		workflow.addTask(displayRawImages);
		
		VFrapProcess vfrapProcess = new VFrapProcess("vfrapProcess");
		workflow.connectParameter(bleachThreshold, vfrapProcess.bleachThreshold);
		workflow.connectParameter(cellThreshold, vfrapProcess.cellThreshold);
		workflow.connect2(importFromVFrap.rawTimeSeriesImages, vfrapProcess.rawTimeSeriesImages);
		workflow.addTask(vfrapProcess);

		DisplayDependentROIs displayROIs = new DisplayDependentROIs("displayROis");
		workflow.connect2(vfrapProcess.cellROI_2D, displayROIs.cellROI);
		workflow.connect2(vfrapProcess.imageDataROIs, displayROIs.imageROIs);
		workflow.connectParameter(displayROITitle, displayROIs.title);
		workflow.addTask(displayROIs);
		
		DisplayProfileLikelihoodPlots displayProfilesOne = new DisplayProfileLikelihoodPlots("displayProfilesOne");
		workflow.connect2(vfrapProcess.profileDataOne, displayProfilesOne.profileData);
		workflow.connectParameter(displayProfileOneTitle, displayProfilesOne.title);
		workflow.addTask(displayProfilesOne);
		
		DisplayProfileLikelihoodPlots displayProfilesTwoWithPenalty = new DisplayProfileLikelihoodPlots("displayProfilesTwoWithPenalty");
		workflow.connect2(vfrapProcess.profileDataTwoWithPenalty, displayProfilesTwoWithPenalty.profileData);
		workflow.connectParameter(displayProfileTwoWithPenaltyTitle, displayProfilesTwoWithPenalty.title);
		workflow.addTask(displayProfilesTwoWithPenalty);
		
		return workflow;
	}


}
