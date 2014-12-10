package org.vcell.vmicro.workflow.test;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.ProgressDialogListener;
import org.vcell.vmicro.workflow.ComputeMeasurementError;
import org.vcell.vmicro.workflow.DisplayBioModel;
import org.vcell.vmicro.workflow.DisplayDependentROIs;
import org.vcell.vmicro.workflow.DisplayPlot;
import org.vcell.vmicro.workflow.DisplayProfileLikelihoodPlots;
import org.vcell.vmicro.workflow.DisplayTimeSeries;
import org.vcell.vmicro.workflow.Generate2DExpModel;
import org.vcell.vmicro.workflow.Generate2DOptContext;
import org.vcell.vmicro.workflow.GenerateBleachROI;
import org.vcell.vmicro.workflow.GenerateCellROIsFromRawTimeSeries;
import org.vcell.vmicro.workflow.GenerateDependentImageROIs;
import org.vcell.vmicro.workflow.GenerateNormalizedFrapData;
import org.vcell.vmicro.workflow.GenerateReducedRefData;
import org.vcell.vmicro.workflow.GenerateRefSimOptModel;
import org.vcell.vmicro.workflow.GenerateTrivial2DPsf;
import org.vcell.vmicro.workflow.ImportRawTimeSeriesFromHdf5Fluor;
import org.vcell.vmicro.workflow.ImportRawTimeSeriesFromVFrap;
import org.vcell.vmicro.workflow.RunFakeSim;
import org.vcell.vmicro.workflow.RunProfileLikelihoodGeneral;
import org.vcell.vmicro.workflow.RunRefSimulation;
import org.vcell.vmicro.workflow.RunRefSimulationFast;
import org.vcell.vmicro.workflow.VFrapProcess;
import org.vcell.vmicro.workflow.data.LocalWorkspace;
import org.vcell.vmicro.workflow.data.ModelType;
import org.vcell.vmicro.workflow.gui.WorkflowModelPanel;
import org.vcell.vmicro.workflow.gui.WorkflowObjectsPanel;
import org.vcell.vmicro.workflow.jgraphx.WorkflowEditorPanel;
import org.vcell.vmicro.workflow.jgraphx.WorkflowJGraphProxy;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.Workflow;

import cbit.vcell.mongodb.VCMongoMessage;

public class WorkflowTest {
	
	public static class Progress implements ClientTaskStatusSupport {
		
		@Override
		public void setProgress(int progress) {
			System.out.println("status: progress : "+progress);
		}
		
		@Override
		public void setMessage(String message) {
			System.out.println("status: message : "+message);
		}
		
		@Override
		public boolean isInterrupted() {
			return false;
		}
		
		@Override
		public int getProgress() {
			return 0;
		}
		
		@Override
		public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
		}
	};
	
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

//			String workflowLanguageText = BeanUtils.readBytesFromFile(new File(args[1]), null);
//			Workflow workflow = Workflow.parse(localWorkspace, workflowLanguageText);
System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> using hard-coded example instead <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
File vfrapFile = new File("D:\\Developer\\eclipse\\workspace_refactor\\VCell_5.4_vmicro\\3D_FRAP_2_ZProjection_Simulation1.vfrap");
//Workflow workflow = getVFrapSimpleExample(workingDirectory, vfrapFile);
Workflow workflow = getFakeDataExample(workingDirectory);
			
			ArrayList<Issue> issues = new ArrayList<Issue>();
			IssueContext issueContext = new IssueContext();
			workflow.gatherIssues(issueContext, issues);
			
			WorkflowJGraphProxy workflowJGraphProxy = new WorkflowJGraphProxy(workflow);
			displayWorkflowGraphJGraphX(workflowJGraphProxy);
			
			displayWorkflowGraph(workflow);
			
			displayWorkflowTable(workflow);
			
			workflow.reportIssues(issues, Issue.SEVERITY_INFO, true);
			
			//
			// execute the workflow
			//
			workflow.compute(new Progress());
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public static void displayWorkflowGraph(Workflow workflow){
		JFrame frame = new javax.swing.JFrame();
		WorkflowModelPanel aWorkflowModelPanel;
		aWorkflowModelPanel = new WorkflowModelPanel();
		frame.setContentPane(aWorkflowModelPanel);
		frame.setSize(aWorkflowModelPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		
		aWorkflowModelPanel.setWorkflow(workflow);
		frame.setTitle("workflow graph - old");
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	}
	
	
	public static void displayWorkflowTable(Workflow workflow){
		JFrame frame = new javax.swing.JFrame();
		WorkflowObjectsPanel aWorkflowObjectsPanel;
		aWorkflowObjectsPanel = new WorkflowObjectsPanel();
		frame.setContentPane(aWorkflowObjectsPanel);
		frame.setSize(aWorkflowObjectsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		
		frame.setLocation(0,1000);
		frame.setSize(1000,600);
		frame.setVisible(true);
		frame.setTitle("workflow table");

		aWorkflowObjectsPanel.setWorkflow(workflow);
	}
	
	
	public static void displayWorkflowGraphJGraphX(WorkflowJGraphProxy workflowJGraphProxy){
		WorkflowEditorPanel aWorkflowEditorPanel;
		aWorkflowEditorPanel = new WorkflowEditorPanel();
		
		aWorkflowEditorPanel.setWorkflowGraphProxy(workflowJGraphProxy);
		JFrame frame = aWorkflowEditorPanel.createFrame(null);
		frame.setLocation(0, 700);
		frame.setSize(2300, 800);
		frame.setVisible(true);
	}
	
	
	

	public static Workflow getExample(File workingDirectory){
		
		
		
		//
		// construct the dataflow graph
		//
		LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
		Workflow workflow = new Workflow(localWorkspace);

//		DataHolder<File> simLogFile = workflow.addParameter("simLogFile",new File("C:\\temp\\SimID_39393_0_0_.log"));
//		DataHolder<String> simFluorFunctionName = workflow.addParameter("simFluorFunctionName","fluor");
		DataHolder<Double> maxIntensity = workflow.addParameter(Double.class,"maxIntensity",10000.0);
		DataHolder<Boolean> bNoise = workflow.addParameter(Boolean.class,"bNoise",true);
		
		DataHolder<String> hdf5File = workflow.addParameter(String.class,"hdf5File","D:\\Developer\\eclipse\\workspace_refactor\\VCell_5.4_vmicro\\datadir\\VirtualMicroscopy\\SimulationData\\SimID_421351917_0_.hdf5");
		DataHolder<String> hdf5FluorName = workflow.addParameter(String.class,"hdf5FluorName","fluor");
		DataHolder<Integer> hdf5ZSliceIndex = workflow.addParameter(Integer.class,"hdf5ZsliceIndex",9);

//		DataHolder<Double> primaryDiffusionRate = workflow.addParameter("primaryDiffusionRate",1.0);
//		DataHolder<Double> primaryFraction = workflow.addParameter("primaryFraction",1.0);
//		DataHolder<Double> bleachMonitorRate = workflow.addParameter("bleachMonitorRate",0.0);
//		DataHolder<Double> secondaryDiffusionRate = workflow.addParameter("secondaryDiffusionRate",1.0);	
//		DataHolder<Double> secondaryFraction = workflow.addParameter("secondaryFraction",1.0);
//		DataHolder<Double> bindingSiteConcentration = workflow.addParameter("bindingSiteConcentration",1.0);
//		DataHolder<Double> bindingOnRate = workflow.addParameter("bindingOnRate",1.0);
//		DataHolder<Double> bindingOffRate = workflow.addParameter("bindingOffRate",1.0);
//		DataHolder<String> extracellularName = workflow.addParameter("extracellularName","ec");
//		DataHolder<String> cytosolName = workflow.addParameter("cytosolName","cyt");
//		DataHolder<User> owner = workflow.addParameter("owner",new User("owner",new KeyValue("1234")));
//		DataHolder<KeyValue> simKey = workflow.addParameter("simKey",new KeyValue("12341234"));
					
		DataHolder<Double> bleachThreshold = workflow.addParameter(Double.class,"bleachThreshold",0.80);
		DataHolder<Double> cellThreshold = workflow.addParameter(Double.class,"cellThreshold",0.5);
		
		DataHolder<String> modelType = workflow.addParameter(String.class,"modelType",ModelType.DiffOne.toString());
		DataHolder<String> modelType2 = workflow.addParameter(String.class,"modelType2",ModelType.DiffTwoWithPenalty.toString());
				
		// input data from 2D simulated concentrations (assumes fluorescence == concentration)
//		ImportRawTimeSeriesFrom2DVCellConcentrations timeSeriesFromVCell = new ImportRawTimeSeriesFrom2DVCellConcentrations();
//		timeSeriesFromVCell.bNoise.setSource(bNoise);
//		timeSeriesFromVCell.maxIntensity.setSource(maxIntensity);
//		timeSeriesFromVCell.fluorFunctionName.setSource(simFluorFunctionName);
//		timeSeriesFromVCell.vcellSimLogFile.setSource(simLogFile);

		// input data from HDF5 postprocessing (includes PSF)
		ImportRawTimeSeriesFromHdf5Fluor timeSeriesFromVCell = new ImportRawTimeSeriesFromHdf5Fluor("t1");
		timeSeriesFromVCell.bNoise.setSource(bNoise);
		timeSeriesFromVCell.maxIntensity.setSource(maxIntensity);
		timeSeriesFromVCell.fluorDataName.setSource(hdf5FluorName);
		timeSeriesFromVCell.vcellHdf5File.setSource(hdf5File);
		timeSeriesFromVCell.zSliceIndex.setSource(hdf5ZSliceIndex);

		workflow.addTask(timeSeriesFromVCell);
		
		GenerateCellROIsFromRawTimeSeries generateCellROIs = new GenerateCellROIsFromRawTimeSeries("t111");
		generateCellROIs.cellThreshold.setSource(cellThreshold);
		generateCellROIs.rawTimeSeriesImages.setSource(timeSeriesFromVCell.rawTimeSeriesImages);
		
		workflow.addTask(generateCellROIs);
		
		GenerateNormalizedFrapData generateNormalizedFrapData = new GenerateNormalizedFrapData("t3");
		generateNormalizedFrapData.backgroundROI_2D.setSource(generateCellROIs.backgroundROI_2D);
		generateNormalizedFrapData.indexOfFirstPostbleach.setSource(generateCellROIs.indexOfFirstPostbleach);
		generateNormalizedFrapData.rawImageTimeSeries.setSource(timeSeriesFromVCell.rawTimeSeriesImages);
		
		workflow.addTask(generateNormalizedFrapData);
		
		GenerateBleachROI generateROIs = new GenerateBleachROI("t2");
		generateROIs.bleachThreshold.setSource(bleachThreshold);
		generateROIs.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
		generateROIs.normalizedTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
		
		workflow.addTask(generateROIs);
		
//		Generate2DSimBioModel generate2DSimBioModel = new Generate2DSimBioModel();
//		generate2DSimBioModel.extent.setSource(timeSeriesFromVCell.extent);
//		generate2DSimBioModel.cellROI_2D.setSource(generateROIs.cellROI_2D);
//		generate2DSimBioModel.timeStamps.setSource(timeSeriesFromVCell.timeStamps);
//		generate2DSimBioModel.indexFirstPostbleach.setSource(generateROIs.indexOfFirstPostbleach);
//		generate2DSimBioModel.primaryDiffusionRate.setSource(primaryDiffusionRate);
//		generate2DSimBioModel.primaryFraction.setSource(primaryFraction);
//		generate2DSimBioModel.bleachMonitorRate.setSource(bleachMonitorRate);
//		generate2DSimBioModel.extracellularName.setSource(extracellularName);
//		generate2DSimBioModel.cytosolName.setSource(cytosolName);
//		generate2DSimBioModel.owner.setSource(owner);
//		generate2DSimBioModel.simKey.setSource(simKey);
//		
//		workflow.addTask(generate2DSimBioModel);
		
		RunRefSimulation runRefSimulation = new RunRefSimulation("t4");
		runRefSimulation.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
		runRefSimulation.normalizedTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
		
		workflow.addTask(runRefSimulation);
		
		GenerateDependentImageROIs generateDependentROIs = new GenerateDependentImageROIs("t5");
		generateDependentROIs.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
		generateDependentROIs.bleachedROI_2D.setSource(generateROIs.bleachedROI_2D);
		
		workflow.addTask(generateDependentROIs);
		
		DisplayDependentROIs displayDependentROIs = new DisplayDependentROIs("displayDependentROIs");
				displayDependentROIs.imageROIs.setSource(generateDependentROIs.imageDataROIs);
				displayDependentROIs.cellROI.setSource(generateCellROIs.cellROI_2D);
	DataHolder<String> displayROITitle = workflow.addParameter(String.class,"displayROITitle","rois");
				displayDependentROIs.title.setSource(displayROITitle);
		
		workflow.addTask(displayDependentROIs);
				
		GenerateReducedRefData generateReducedNormalizedData = new GenerateReducedRefData("t6");
		generateReducedNormalizedData.imageTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
		generateReducedNormalizedData.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
		
		workflow.addTask(generateReducedNormalizedData);
		
		GenerateReducedRefData generateReducedRefSimData = new GenerateReducedRefData("t7");
		generateReducedRefSimData.imageTimeSeries.setSource(runRefSimulation.refSimTimeSeries);
		generateReducedRefSimData.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
		
		workflow.addTask(generateReducedRefSimData);
		
		ComputeMeasurementError computeMeasurementError = new ComputeMeasurementError("t8");
		computeMeasurementError.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
		computeMeasurementError.indexFirstPostbleach.setSource(generateCellROIs.indexOfFirstPostbleach);
		computeMeasurementError.prebleachAverage.setSource(generateNormalizedFrapData.prebleachAverage);
		computeMeasurementError.rawImageTimeSeries.setSource(timeSeriesFromVCell.rawTimeSeriesImages);
		
		workflow.addTask(computeMeasurementError);
		
		GenerateRefSimOptModel generateRefSimOptModel = new GenerateRefSimOptModel("t11");
		generateRefSimOptModel.modelType.setSource(modelType);
		generateRefSimOptModel.refSimData.setSource(generateReducedRefSimData.reducedROIData);
		generateRefSimOptModel.refSimDiffusionRate.setSource(runRefSimulation.refSimDiffusionRate);

		
		Generate2DOptContext generate2DOptContext = new Generate2DOptContext("t9");
		generate2DOptContext.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
		generate2DOptContext.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
		generate2DOptContext.optModel.setSource(generateRefSimOptModel.optModel);
		
		workflow.addTask(generate2DOptContext);
		
		return workflow;

	}
	
	public static Workflow getVFrapExample(File workingDirectory, File vfrapFile){
		
		
		
		//
		// construct the dataflow graph
		//
		LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
		Workflow workflow = new Workflow(localWorkspace);

		//
		// workflow parameters
		//
		DataHolder<Double> maxIntensity = workflow.addParameter(Double.class,"maxIntensity",10000.0);
		DataHolder<Boolean> bNoise = workflow.addParameter(Boolean.class,"bNoise",true);
		DataHolder<File> vfrapFileParam = workflow.addParameter(File.class,"vfrapFile",vfrapFile);
		DataHolder<Double> bleachThreshold = workflow.addParameter(Double.class,"bleachThreshold",0.80);
		DataHolder<Double> cellThreshold = workflow.addParameter(Double.class,"cellThreshold",0.5);
		DataHolder<String> modelTypeOne = workflow.addParameter(String.class,"modelType",ModelType.DiffOne.toString());
		DataHolder<String> modelTypeTwo = workflow.addParameter(String.class,"modelType2",ModelType.DiffTwoWithPenalty.toString());
		DataHolder<String> displayROITitle = workflow.addParameter(String.class,"displayROITitle","rois");
				
		// input data from VFrap file
		ImportRawTimeSeriesFromVFrap timeSeriesFromVCell = new ImportRawTimeSeriesFromVFrap("frapImport");
		timeSeriesFromVCell.vfrapFile.setSource(vfrapFileParam);
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
		generateCellROIs.cellThreshold.setSource(cellThreshold);
		generateCellROIs.rawTimeSeriesImages.setSource(timeSeriesFromVCell.rawTimeSeriesImages);	
		workflow.addTask(generateCellROIs);
		
		GenerateNormalizedFrapData generateNormalizedFrapData = new GenerateNormalizedFrapData("generateNormalizedFrapData");
		generateNormalizedFrapData.backgroundROI_2D.setSource(generateCellROIs.backgroundROI_2D);
		generateNormalizedFrapData.indexOfFirstPostbleach.setSource(generateCellROIs.indexOfFirstPostbleach);
		generateNormalizedFrapData.rawImageTimeSeries.setSource(timeSeriesFromVCell.rawTimeSeriesImages);	
		workflow.addTask(generateNormalizedFrapData);
		
		GenerateBleachROI generateROIs = new GenerateBleachROI("generateROIs");
		generateROIs.bleachThreshold.setSource(bleachThreshold);
		generateROIs.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
		generateROIs.normalizedTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);	
		workflow.addTask(generateROIs);
		
		GenerateDependentImageROIs generateDependentROIs = new GenerateDependentImageROIs("generateDependentROIs");
		generateDependentROIs.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
		generateDependentROIs.bleachedROI_2D.setSource(generateROIs.bleachedROI_2D);	
		workflow.addTask(generateDependentROIs);
		
		DisplayDependentROIs displayDependentROIs = new DisplayDependentROIs("displayDependentROIs");
		displayDependentROIs.imageROIs.setSource(generateDependentROIs.imageDataROIs);
		displayDependentROIs.cellROI.setSource(generateCellROIs.cellROI_2D);
		displayDependentROIs.title.setSource(displayROITitle);
		workflow.addTask(displayDependentROIs);
				
		GenerateReducedRefData generateReducedNormalizedData = new GenerateReducedRefData("generateReducedNormalizedData");
		generateReducedNormalizedData.imageTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
		generateReducedNormalizedData.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);		
		workflow.addTask(generateReducedNormalizedData);
		
		ComputeMeasurementError computeMeasurementError = new ComputeMeasurementError("computeMeasurementError");
		computeMeasurementError.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
		computeMeasurementError.indexFirstPostbleach.setSource(generateCellROIs.indexOfFirstPostbleach);
		computeMeasurementError.prebleachAverage.setSource(generateNormalizedFrapData.prebleachAverage);
		computeMeasurementError.rawImageTimeSeries.setSource(timeSeriesFromVCell.rawTimeSeriesImages);		
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
		runRefSimulationFast.cellROI_2D.setSource(generateCellROIs.cellROI_2D);
		runRefSimulationFast.normalizedTimeSeries.setSource(generateNormalizedFrapData.normalizedFrapData);
		runRefSimulationFast.imageDataROIs.setSource(generateDependentROIs.imageDataROIs);
		runRefSimulationFast.psf.setSource(psf_2D.psf_2D);
		workflow.addTask(runRefSimulationFast);
		
		//
		// model with One mobile fraction
		//
		{
		GenerateRefSimOptModel generateRefSimOptModelOne = new GenerateRefSimOptModel("generateRefSimOptModel");
		generateRefSimOptModelOne.modelType.setSource(modelTypeOne);
		generateRefSimOptModelOne.refSimData.setSource(runRefSimulationFast.reducedROIData);
		generateRefSimOptModelOne.refSimDiffusionRate.setSource(runRefSimulationFast.refSimDiffusionRate);
		workflow.addTask(generateRefSimOptModelOne);
		Generate2DOptContext generate2DOptContextOne = new Generate2DOptContext("generate2DOptContextOne");
		generate2DOptContextOne.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
		generate2DOptContextOne.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
		generate2DOptContextOne.optModel.setSource(generateRefSimOptModelOne.optModel);
		workflow.addTask(generate2DOptContextOne);
		RunProfileLikelihoodGeneral runProfileLikelihoodOne = new RunProfileLikelihoodGeneral("runProfileLikelihoodOne");
		runProfileLikelihoodOne.optContext.setSource(generate2DOptContextOne.optContext);
		workflow.addTask(runProfileLikelihoodOne);
		}
		//
		// model with One mobile fraction
		//
		GenerateRefSimOptModel generateRefSimOptModelTwo = new GenerateRefSimOptModel("generateRefSimOptModelTwo");
		generateRefSimOptModelTwo.modelType.setSource(modelTypeTwo);
		generateRefSimOptModelTwo.refSimData.setSource(runRefSimulationFast.reducedROIData);
		generateRefSimOptModelTwo.refSimDiffusionRate.setSource(runRefSimulationFast.refSimDiffusionRate);
		workflow.addTask(generateRefSimOptModelTwo);
		Generate2DOptContext generate2DOptContextTwo = new Generate2DOptContext("generate2DOptContextTwo");
		generate2DOptContextTwo.normalizedMeasurementErrors.setSource(computeMeasurementError.normalizedMeasurementError);
		generate2DOptContextTwo.optModel.setSource(generateRefSimOptModelTwo.optModel);
		generate2DOptContextTwo.normExpData.setSource(generateReducedNormalizedData.reducedROIData);
		workflow.addTask(generate2DOptContextTwo);
		RunProfileLikelihoodGeneral runProfileLikelihoodTwo = new RunProfileLikelihoodGeneral("runProfileLikelihoodTwo");
		runProfileLikelihoodTwo.optContext.setSource(generate2DOptContextTwo.optContext);
		workflow.addTask(runProfileLikelihoodTwo);
		
		return workflow;

	}

	public static Workflow getVFrapSimpleExample(File workingDirectory, File vfrapFile){
		
		//
		// construct the dataflow graph
		//
		LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
		Workflow workflow = new Workflow(localWorkspace);

		//
		// workflow parameters
		//
		DataHolder<File> vfrapFileParam = workflow.addParameter(File.class,"vfrapFile",vfrapFile);
		DataHolder<Double> bleachThreshold = workflow.addParameter(Double.class,"bleachThreshold",0.80);
		DataHolder<Double> cellThreshold = workflow.addParameter(Double.class,"cellThreshold",0.5);

		DataHolder<String> displayROITitle = workflow.addParameter(String.class,"displayROITitle","rois");
		DataHolder<String> displayProfileOneTitle = workflow.addParameter(String.class,"displayProfileOneTitle","1 Diffusing");
		DataHolder<String> displayProfileTwoWithoutPenaltyTitle = workflow.addParameter(String.class,"displayProfileTwoTitle","2 Diffusing - no penalty");
		DataHolder<String> displayProfileTwoWithPenaltyTitle = workflow.addParameter(String.class,"displayProfileTwoTitle","2 Diffusing - with penalty");
				
		ImportRawTimeSeriesFromVFrap importFromVFrap = new ImportRawTimeSeriesFromVFrap("importFromVFrap");
		importFromVFrap.vfrapFile.setSource(vfrapFileParam);
		workflow.addTask(importFromVFrap);
		
		DisplayTimeSeries displayRawImages = new DisplayTimeSeries("displayRawImages");
		displayRawImages.imageTimeSeries.setSource(importFromVFrap.rawTimeSeriesImages);
		displayRawImages.title.setSource(workflow.addParameter(String.class, "displayRawImagesTitle", "raw Images from "+importFromVFrap.getClass().getSimpleName()));
		workflow.addTask(displayRawImages);
		
		VFrapProcess vfrapProcess = new VFrapProcess("vfrapProcess");
		vfrapProcess.bleachThreshold.setSource(bleachThreshold);
		vfrapProcess.cellThreshold.setSource(cellThreshold);
		vfrapProcess.rawTimeSeriesImages.setSource(importFromVFrap.rawTimeSeriesImages);
		workflow.addTask(vfrapProcess);

		DisplayDependentROIs displayROIs = new DisplayDependentROIs("displayROis");
		displayROIs.cellROI.setSource(vfrapProcess.cellROI_2D);
		displayROIs.imageROIs.setSource(vfrapProcess.imageDataROIs);
		displayROIs.title.setSource(displayROITitle);
		workflow.addTask(displayROIs);
		
		DisplayProfileLikelihoodPlots displayProfilesOne = new DisplayProfileLikelihoodPlots("displayProfilesOne");
		displayProfilesOne.profileData.setSource(vfrapProcess.profileDataOne);
		displayProfilesOne.title.setSource(displayProfileOneTitle);
		workflow.addTask(displayProfilesOne);
		
		DisplayProfileLikelihoodPlots displayProfilesTwoWithPenalty = new DisplayProfileLikelihoodPlots("displayProfilesTwoWithPenalty");
		displayProfilesTwoWithPenalty.profileData.setSource(vfrapProcess.profileDataTwoWithPenalty);
		displayProfilesTwoWithPenalty.title.setSource(displayProfileTwoWithPenaltyTitle);
		workflow.addTask(displayProfilesTwoWithPenalty);
		
//		DisplayProfileLikelihoodPlots displayProfilesTwoWithoutPenalty = new DisplayProfileLikelihoodPlots("displayProfilesTwoWithoutPenalty");
//		displayProfilesTwoWithoutPenalty.profileData.setSource(vfrapProcess.profileDataTwoWithoutPenalty);
//		displayProfilesTwoWithoutPenalty.title.setSource(displayProfileTwoWithoutPenaltyTitle);
//		workflow.addTask(displayProfilesTwoWithoutPenalty);
		
		
		return workflow;

	}

	public static Workflow getFakeDataExample(File workingDirectory){
		
		//
		// construct the dataflow graph
		//
		LocalWorkspace localWorkspace = new LocalWorkspace(workingDirectory);
		Workflow workflow = new Workflow(localWorkspace);

		//
		// workflow parameters
		//
		DataHolder<Double> bleachThreshold = workflow.addParameter(Double.class,"bleachThreshold",0.80);
		DataHolder<Double> cellThreshold = workflow.addParameter(Double.class,"cellThreshold",0.5);

		DataHolder<String> displayROITitle = workflow.addParameter(String.class,"displayROITitle","rois");
		DataHolder<String> displayProfileOneTitle = workflow.addParameter(String.class,"displayProfileOneTitle","1 Diffusing");
		DataHolder<String> displayProfileTwoWithoutPenaltyTitle = workflow.addParameter(String.class,"displayProfileTwoTitle","2 Diffusing - no penalty");
		DataHolder<String> displayProfileTwoWithPenaltyTitle = workflow.addParameter(String.class,"displayProfileTwoTitle","2 Diffusing - with penalty");
		
		DataHolder<Double> cellRadius = workflow.addParameter(Double.class, 			"cellRadius", 					20.0);
		DataHolder<String> cytosolName = workflow.addParameter(String.class, 			"cytosolName", 					"cytosol");
		DataHolder<String> extracellularName = workflow.addParameter(String.class, 		"extracellularName", 			"ec");
		DataHolder<Double> deltaX = workflow.addParameter(Double.class, 				"deltaX", 						0.3);
		DataHolder<Double> outputTimeStep = workflow.addParameter(Double.class, 		"outputTimeStep", 				0.3);

		DataHolder<Double> psfSigma = workflow.addParameter(Double.class, 				"psfSigma",			 			0.3);

		DataHolder<Double> bleachRadius = workflow.addParameter(Double.class, 			"bleachRadius", 				4.0); // circular disk (no K
		DataHolder<Double> bleachRate = workflow.addParameter(Double.class, 			"bleachRate", 					5.0);
		DataHolder<Double> bleachDuration = workflow.addParameter(Double.class, 		"bleachDuraction", 				0.3);

		DataHolder<Double> postbleachDelay = workflow.addParameter(Double.class, 		"postbleachDelay", 				1.0);
		DataHolder<Double> postbleachDuration = workflow.addParameter(Double.class, 	"postbleachDuraction", 			25.0);
		DataHolder<Double> bleachMonitorRate = workflow.addParameter(Double.class, 		"bleachMonitorRate", 			0.005); // no bleach while monitoring

		DataHolder<Double> primaryDiffusionRate = workflow.addParameter(Double.class, 	"primaryDiffusionRate", 		2.0);
		DataHolder<Double> primaryFraction = workflow.addParameter(Double.class, 		"primaryFraction", 				0.8); // primary fraction 100%
		DataHolder<Double> secondaryDiffusionRate = workflow.addParameter(Double.class, "secondaryDiffusionRate", 		20.0);
		DataHolder<Double> secondaryFraction = workflow.addParameter(Double.class, 		"secondaryFraction",			0.2);

		DataHolder<Boolean> bNoise = workflow.addParameter(Boolean.class, 				"bNoise", 						true); // no noise to start
		DataHolder<Double> maxIntensity = workflow.addParameter(Double.class,			"maxIntensity",					60000.0);
		
		Generate2DExpModel generateExpModel = new Generate2DExpModel("generateExpModel");
		generateExpModel.bleachDuration.setSource(bleachDuration);
		generateExpModel.bleachMonitorRate.setSource(bleachMonitorRate);
		generateExpModel.bleachRadius.setSource(bleachRadius);
		generateExpModel.bleachRate.setSource(bleachRate);
		generateExpModel.cellRadius.setSource(cellRadius);
		generateExpModel.cytosolName.setSource(cytosolName);
		generateExpModel.deltaX.setSource(deltaX);
		generateExpModel.extracellularName.setSource(extracellularName);
		generateExpModel.outputTimeStep.setSource(outputTimeStep);
		generateExpModel.postbleachDelay.setSource(postbleachDelay);
		generateExpModel.postbleachDuration.setSource(postbleachDuration);
		generateExpModel.primaryDiffusionRate.setSource(primaryDiffusionRate);
		generateExpModel.primaryFraction.setSource(primaryFraction);
		generateExpModel.psfSigma.setSource(psfSigma);
		generateExpModel.secondaryDiffusionRate.setSource(secondaryDiffusionRate);
		generateExpModel.secondaryFraction.setSource(secondaryFraction);
		workflow.addTask(generateExpModel);
		
		DisplayBioModel displayBioModel = new DisplayBioModel("displayBioModel");
		displayBioModel.bioModel.setSource(generateExpModel.bioModel_2D);
		workflow.addTask(displayBioModel);
		
		RunFakeSim runFakeSim = new RunFakeSim("runFakeSim");
		runFakeSim.bNoise.setSource(bNoise);
		runFakeSim.maxIntensity.setSource(maxIntensity);
		runFakeSim.simulation_2D.setSource(generateExpModel.simulation_2D);
		runFakeSim.bleachBlackoutBeginTime.setSource(generateExpModel.bleachBlackoutBeginTime);
		runFakeSim.bleachBlackoutEndTime.setSource(generateExpModel.bleachBlackoutEndTime);
		workflow.addTask(runFakeSim);
		
		DisplayTimeSeries displayRawImages = new DisplayTimeSeries("displayRawImages");
		displayRawImages.imageTimeSeries.setSource(runFakeSim.simTimeSeries);
		displayRawImages.title.setSource(workflow.addParameter(String.class, "displayRawImagesTitle", "raw Images from "+runFakeSim.getClass().getSimpleName()));
		workflow.addTask(displayRawImages);
		
		VFrapProcess vfrapProcess = new VFrapProcess("vfrapProcess");
		vfrapProcess.bleachThreshold.setSource(bleachThreshold);
		vfrapProcess.cellThreshold.setSource(cellThreshold);
		vfrapProcess.rawTimeSeriesImages.setSource(runFakeSim.simTimeSeries);
		workflow.addTask(vfrapProcess);
		
		Workflow vfrapInternalWorkflow = vfrapProcess.getWorkflow();
		WorkflowJGraphProxy internalWorkflowJGraphProxy = new WorkflowJGraphProxy(vfrapInternalWorkflow);
		displayWorkflowGraphJGraphX(internalWorkflowJGraphProxy);
		displayWorkflowTable(vfrapInternalWorkflow);

		
		DisplayPlot displayROIData = new DisplayPlot("displayROIData");
		displayROIData.plotData.setSource(vfrapProcess.reducedTimeSeries);
		displayROIData.title.setSource(workflow.addParameter(String.class, "displayROIDataTitle", "reduced data from "+vfrapProcess.getClass().getSimpleName()));
		workflow.addTask(displayROIData);
		
		DisplayTimeSeries displayNormalizedImages = new DisplayTimeSeries("displayNormalizedImages");
		displayNormalizedImages.imageTimeSeries.setSource(vfrapProcess.normalizedTimeSeries);
		displayNormalizedImages.title.setSource(workflow.addParameter(String.class, "displayNormalizedImagesTitle", "normalized images from "+vfrapProcess.getClass().getSimpleName()));
		workflow.addTask(displayNormalizedImages);
		
		DisplayDependentROIs displayROIs = new DisplayDependentROIs("displayROis");
		displayROIs.cellROI.setSource(vfrapProcess.cellROI_2D);
		displayROIs.imageROIs.setSource(vfrapProcess.imageDataROIs);
		displayROIs.title.setSource(displayROITitle);
		workflow.addTask(displayROIs);
		
		DisplayProfileLikelihoodPlots displayProfilesOne = new DisplayProfileLikelihoodPlots("displayProfilesOne");
		displayProfilesOne.profileData.setSource(vfrapProcess.profileDataOne);
		displayProfilesOne.title.setSource(displayProfileOneTitle);
		workflow.addTask(displayProfilesOne);
		
		DisplayProfileLikelihoodPlots displayProfilesTwoWithPenalty = new DisplayProfileLikelihoodPlots("displayProfilesTwoWithPenalty");
		displayProfilesTwoWithPenalty.profileData.setSource(vfrapProcess.profileDataTwoWithPenalty);
		displayProfilesTwoWithPenalty.title.setSource(displayProfileTwoWithPenaltyTitle);
		workflow.addTask(displayProfilesTwoWithPenalty);
		
//		DisplayProfileLikelihoodPlots displayProfilesTwoWithoutPenalty = new DisplayProfileLikelihoodPlots("displayProfilesTwoWithoutPenalty");
//		displayProfilesTwoWithoutPenalty.profileData.setSource(vfrapProcess.profileDataTwoWithoutPenalty);
//		displayProfilesTwoWithoutPenalty.title.setSource(displayProfileTwoWithoutPenaltyTitle);
//		workflow.addTask(displayProfilesTwoWithoutPenalty);
		
		
		return workflow;

	}

}
