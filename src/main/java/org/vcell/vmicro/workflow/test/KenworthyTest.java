package org.vcell.vmicro.workflow.test;

import java.io.File;
import java.util.ArrayList;

import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.task.Generate2DExpModel_UniformBleach;
import org.vcell.vmicro.workflow.task.KenworthyProcess;
import org.vcell.vmicro.workflow.task.RunFakeSim;
import org.vcell.workflow.MemoryRepository;
import org.vcell.workflow.Repository;
import org.vcell.workflow.TaskContext;
import org.vcell.workflow.Workflow;
import org.vcell.workflow.WorkflowParameter;

import cbit.vcell.mongodb.VCMongoMessage;

public class KenworthyTest {
	
	public static void main(String[] args){
		if (args.length!=2){
			System.out.println("expecting 2 arguments");
			System.out.println("usage: java "+Workflow.class.getSimpleName()+" workingdir workflowInputFile");
			System.out.println("workingdir example: "+"D:\\developer\\eclipse\\workspace\\VCell_5.4_vmicro\\datadir");
			System.out.println("workflowInputFile example: "+"D:\\developer\\eclipse\\workspace\\VCell_5.4_vmicro\\workflow1.txt");
			System.exit(1);
		}
		try {
			VCMongoMessage.enabled=false;
			//PropertyLoader.loadProperties();
			// workflowInputFile "C:\\developer\\eclipse\\workspace\\VCell_5.3_vmicro\\workflow1.txt"
			File workingDirectory = new File(args[0]);
			LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
			Repository repository = new MemoryRepository();

//			String workflowLanguageText = BeanUtils.readBytesFromFile(new File(args[1]), null);
//			Workflow workflow = Workflow.parse(localWorkspace, workflowLanguageText);
System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> using hard-coded example instead <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
File vfrapFile = new File("D:\\Developer\\eclipse\\workspace_refactor\\VCell_5.4_vmicro\\3D_FRAP_2_ZProjection_Simulation1.vfrap");
//Workflow workflow = getVFrapSimpleExample(workingDirectory, vfrapFile);
//Workflow workflow = getFakeDataExample(workingDirectory);
Workflow workflow = getInteractiveModelWorkflow(repository, workingDirectory);
			
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


	public static Workflow getInteractiveModelWorkflow(Repository repository, File workingDirectory){
			
			//
			// construct the dataflow graph
			//
			LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
			Workflow workflow = new Workflow("main");
	
			//
			// workflow parameters
			//
			WorkflowParameter<Double> bleachThreshold = workflow.addParameter(Double.class,"bleachThreshold", repository,0.80);
			WorkflowParameter<Double> cellThreshold = workflow.addParameter(Double.class,"cellThreshold",repository,0.5);
	
			WorkflowParameter<Double> cellRadius = workflow.addParameter(Double.class, 			"cellRadius", 					repository, 10.0);
			WorkflowParameter<String> cytosolName = workflow.addParameter(String.class, 			"cytosolName", 					repository, "cytosol");
			WorkflowParameter<String> extracellularName = workflow.addParameter(String.class, 		"extracellularName", 			repository, "ec");
			WorkflowParameter<Double> deltaX = workflow.addParameter(Double.class, 				"deltaX", 						repository, 0.3);
			WorkflowParameter<Double> outputTimeStep = workflow.addParameter(Double.class, 		"outputTimeStep", 				repository, 0.3);
	
			WorkflowParameter<Double> psfSigma = workflow.addParameter(Double.class, 				"psfSigma",			 			repository, 0.01);	// 0.3
	
			WorkflowParameter<Double> bleachRadius = workflow.addParameter(Double.class, 			"bleachRadius", 				repository, 4.0); // circular disk (no K
			WorkflowParameter<Double> bleachRate = workflow.addParameter(Double.class, 			"bleachRate", 					repository, 500.0); // 5.0
			WorkflowParameter<Double> bleachDuration = workflow.addParameter(Double.class, 		"bleachDuraction", 				repository, 0.003); // 0.3
	
			WorkflowParameter<Double> postbleachDelay = workflow.addParameter(Double.class, 		"postbleachDelay", 				repository, 0.001); // 1.0
			WorkflowParameter<Double> postbleachDuration = workflow.addParameter(Double.class, 	"postbleachDuraction", 			repository, 25.0);
			WorkflowParameter<Double> bleachMonitorRate = workflow.addParameter(Double.class, 		"bleachMonitorRate", 			repository, 0.0000005); // no bleach while monitoring
	
			WorkflowParameter<Double> primaryDiffusionRate = workflow.addParameter(Double.class, 	"primaryDiffusionRate", 		repository, 2.0);
			WorkflowParameter<Double> primaryFraction = workflow.addParameter(Double.class, 		"primaryFraction", 				repository, 1.0); // primary fraction 100%
			WorkflowParameter<Double> secondaryDiffusionRate = workflow.addParameter(Double.class, "secondaryDiffusionRate", 		repository, 0.0); // 20.0
			WorkflowParameter<Double> secondaryFraction = workflow.addParameter(Double.class, 		"secondaryFraction",			repository, 0.0); // 0.2
	
			WorkflowParameter<Boolean> bNoise = workflow.addParameter(Boolean.class, 				"bNoise", 						repository, true); // no noise to start
			WorkflowParameter<Double> maxIntensity = workflow.addParameter(Double.class,			"maxIntensity",					repository, 60000.0);

			Generate2DExpModel_UniformBleach generateExpModel = new Generate2DExpModel_UniformBleach("generateExpModel");
			workflow.connectParameter(bleachDuration, generateExpModel.bleachDuration);
			workflow.connectParameter(bleachMonitorRate, generateExpModel.bleachMonitorRate);
			workflow.connectParameter(bleachRadius, generateExpModel.bleachRadius);
			workflow.connectParameter(bleachRate, generateExpModel.bleachRate);
			workflow.connectParameter(cellRadius, generateExpModel.cellRadius);
			workflow.connectParameter(cytosolName, generateExpModel.cytosolName);
			workflow.connectParameter(deltaX, generateExpModel.deltaX);
			workflow.connectParameter(extracellularName, generateExpModel.extracellularName);
			workflow.connectParameter(outputTimeStep, generateExpModel.outputTimeStep);
			workflow.connectParameter(postbleachDelay, generateExpModel.postbleachDelay);
			workflow.connectParameter(postbleachDuration, generateExpModel.postbleachDuration);
			workflow.connectParameter(primaryDiffusionRate, generateExpModel.primaryDiffusionRate);
			workflow.connectParameter(primaryFraction, generateExpModel.primaryFraction);
			workflow.connectParameter(psfSigma, generateExpModel.psfSigma);
			workflow.connectParameter(secondaryDiffusionRate, generateExpModel.secondaryDiffusionRate);
			workflow.connectParameter(secondaryFraction, generateExpModel.secondaryFraction);
			workflow.addTask(generateExpModel);
			
//			DisplayBioModel displayBioModel = new DisplayBioModel("displayBioModel");
//			displayBioModel.bioModel.setSource(generateExpModel.bioModel_2D);
//			workflow.addTask(displayBioModel);
			
			RunFakeSim runFakeSim = new RunFakeSim("runFakeSim");
			workflow.connectParameter(bNoise, runFakeSim.bNoise);
			workflow.connectParameter(maxIntensity, runFakeSim.maxIntensity);
			workflow.connect2(generateExpModel.simulation_2D, runFakeSim.simulation_2D);
			workflow.connect2(generateExpModel.bleachBlackoutBeginTime, runFakeSim.bleachBlackoutBeginTime);
			workflow.connect2(generateExpModel.bleachBlackoutEndTime, runFakeSim.bleachBlackoutEndTime);
			workflow.addTask(runFakeSim);
			
//			DisplayTimeSeries displayRawImages = new DisplayTimeSeries("displayRawImages");
//			displayRawImages.imageTimeSeries.setSource(runFakeSim.simTimeSeries);
//			displayRawImages.title.setSource(workflow.addParameter(String.class, "displayRawImagesTitle", "raw Images from "+runFakeSim.getClass().getSimpleName()));
//			workflow.addTask(displayRawImages);
			
			KenworthyProcess kenworthyProcess = new KenworthyProcess("kenworthyProcess");
			workflow.connectParameter(bleachThreshold, kenworthyProcess.bleachThreshold);
			workflow.connectParameter(cellThreshold, kenworthyProcess.cellThreshold);
			workflow.connect2(runFakeSim.simTimeSeries, kenworthyProcess.rawTimeSeriesImages);
			workflow.addTask(kenworthyProcess);
			
//			Workflow kenworthyInternalWorkflow = kenworthyProcess.getWorkflow();
//			WorkflowJGraphProxy kenworthyInternalWorkflowJGraphProxy = new WorkflowJGraphProxy(kenworthyInternalWorkflow);
//			displayWorkflowGraphJGraphX(kenworthyInternalWorkflowJGraphProxy);
//			displayWorkflowTable(kenworthyInternalWorkflow);
//	
//			DisplayProfileLikelihoodPlots displayProfilesUniformDisk = new DisplayProfileLikelihoodPlots("displayProfilesUniformDisk");
//			displayProfilesUniformDisk.profileData.setSource(kenworthyProcess.profileData);
//			displayProfilesUniformDisk.title.setSource(workflow.addParameter(String.class, "title", "uniform disk"));
//			workflow.addTask(displayProfilesUniformDisk);
			
			return workflow;
	
		}

}
