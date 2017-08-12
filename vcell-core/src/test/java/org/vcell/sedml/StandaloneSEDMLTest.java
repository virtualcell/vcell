package org.vcell.sedml;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jlibsedml.AbstractTask;
import org.jlibsedml.Algorithm;
import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.Libsedml;
import org.jlibsedml.Model;
import org.jlibsedml.OneStep;
import org.jlibsedml.Output;
import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.SedML;
import org.jlibsedml.SteadyState;
import org.jlibsedml.UniformTimeCourse;
import org.jlibsedml.execution.ArchiveModelResolver;
import org.jlibsedml.execution.ModelResolver;
import org.jlibsedml.modelsupport.BioModelsModelsRetriever;
import org.jlibsedml.modelsupport.KisaoOntology;
import org.jlibsedml.modelsupport.KisaoTerm;
import org.jlibsedml.modelsupport.URLResourceRetriever;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.TranslationLogger;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class StandaloneSEDMLTest {
	
	static ClientTaskStatusSupport progressListener = new ClientTaskStatusSupport() {
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

	
    static TranslationLogger transLogger = new TranslationLogger((Component)null){
    	@Override
    	public void sendAllMessages(){
    		if (!messages.isEmpty()) {
    			StringBuilder messageBuf = new StringBuilder("The translation process has encountered the following problem(s):\n ");
    			//"which can affect the quality of the translation:\n");
    			int i = 0;
    			for (Message m : messages) {
    				messageBuf.append(++i +") " + m.message + "\n");
    			}
    			System.err.println(messageBuf.toString());
    		}
    	}
    };
    	

    public static class VCellSedmlModel {
		
	}

	
	public static void doit(File archiveFile) throws Exception{
		ArchiveComponents ac = null;
		ac = Libsedml.readSEDMLArchive(new FileInputStream(archiveFile));
		
		SEDMLDocument sedmlDoc = ac.getSedmlDocument();
	
		SedML sedml = sedmlDoc.getSedMLModel();
		if(sedml == null || sedml.getModels().isEmpty()) {
			throw new RuntimeException("sedml null or empty");
		}
		
        ModelResolver resolver = new ModelResolver(sedml);
       // resolver.add(new FileModelResolver());
        resolver.add(new ArchiveModelResolver(ac));
        resolver.add(new BioModelsModelsRetriever());
        resolver.add(new URLResourceRetriever());
       // resolver.add(new RelativeFileModelResolver(FileUtils.getFullPath(archiveFile.getAbsolutePath())));
		
		//
        // iterate through all the elements and show them at the console
		//
        List<org.jlibsedml.Model> mmm = sedml.getModels();
        for(Model mm : mmm) {
            System.out.println(mm.toString());
        }
        List<org.jlibsedml.Simulation> sss = sedml.getSimulations();
        for(org.jlibsedml.Simulation ss : sss) {
            System.out.println(ss.toString());
        }
        List<AbstractTask> ttt = sedml.getTasks();
        for(AbstractTask tt : ttt) {
            System.out.println(tt.toString());
        }
        List<DataGenerator> ddd = sedml.getDataGenerators();
        for(DataGenerator dd : ddd) {
            System.out.println(dd.toString());
        }
        List<Output> ooo = sedml.getOutputs();
        for(Output oo : ooo) {
            System.out.println(oo.toString());
        }
        
        //
        // extract models referenced in tasks.
        //
        KisaoOntology kisaoInstance = KisaoOntology.getInstance();
//        HashMap<String,Model> flattenedModels = new HashMap<String, Model>();
		List<AbstractTask> taskList = sedml.getTasks();
        for (AbstractTask task : taskList){
        	String modelReference = task.getModelReference();
			org.jlibsedml.Model sedmlOriginalModel = sedml.getModelWithId(modelReference);
        	
            String sbmlModelString = resolver.getModelString(sedmlOriginalModel);
            
            XMLSource sbmlSource = new XMLSource(sbmlModelString);		// sbmlSource with all the changes applied
            
            org.jlibsedml.Simulation sedmlSimulation = sedml.getSimulation(task.getSimulationReference());
    		Algorithm algorithm = sedmlSimulation.getAlgorithm();
			KisaoTerm sedmlKisao = kisaoInstance.getTermById(algorithm.getKisaoID());

			//
			// try to find a VCell solverDescription to match the Kisao term
			//
	        // UniformTimeCourse [initialTime=0.0, numberOfPoints=1000, outputEndTime=1.0, outputStartTime=0.0, 
	        // Algorithm [kisaoID=KISAO:0000019], getId()=SimSlow]
	        
	        // identify the vCell solvers that would match best the sedml solver kisao id
	        List<SolverDescription> solverDescriptions = new ArrayList<>();
			for (SolverDescription sd : SolverDescription.values()) {
				KisaoTerm solverKisaoTerm = kisaoInstance.getTermById(sd.getKisao());
				if(solverKisaoTerm == null) {
					break;
				}
				boolean isExactlySame = solverKisaoTerm.equals(sedmlKisao);
				if (isExactlySame && !solverKisaoTerm.isObsolete()) {
					solverDescriptions.add(sd);		// we make a list with all the solvers that match the kisao
				}
			}
			if (solverDescriptions.isEmpty()){
				throw new RuntimeException("cannot find the solverDescription with exact match for Kisao term '"+sedmlKisao+"'");
			}
			SolverDescription solverDescription = solverDescriptions.get(0); // choose first one
			
			// find out everything else we need about the application we're going to use,
			// some of the info will be needed when we parse the sbml file 
			boolean bSpatial = false;
			Application appType = Application.NETWORK_DETERMINISTIC;
			Set<SolverDescription.SolverFeature> sfList = solverDescription.getSupportedFeatures();
			for(SolverDescription.SolverFeature sf : sfList) {
				switch(sf) {
				case Feature_Rulebased:
					appType = Application.RULE_BASED_STOCHASTIC;
					break;
				case Feature_Stochastic:
					appType = Application.NETWORK_STOCHASTIC;
					break;
				case Feature_Deterministic:
					appType = Application.NETWORK_DETERMINISTIC;
					break;
				case Feature_Spatial:
					bSpatial = true;
					break;
				default:
					break;
				}
			}
			
			BioModel bioModel = (BioModel)XmlHelper.importSBML(transLogger, sbmlSource, bSpatial);
			
			//
	        // we already have an application loaded from the sbml file, with initial conditions and stuff
	        // which may be not be suitable because the sedml kisao may need a different app type
	        // so we do a "copy as" to the right type and then delete the original we loaded from the sbml file
			//
	        SimulationContext newSimulationContext = null;		// the new application we're making from the old one
	        if(bioModel.getSimulationContexts().length == 1) {
	        	SimulationContext oldSimulationContext = bioModel.getSimulationContext(0);
	        	String newSCName = bioModel.getFreeSimulationContextName();
	        	newSimulationContext = SimulationContext.copySimulationContext(oldSimulationContext, newSCName, bSpatial, appType);
	        	bioModel.addSimulationContext(newSimulationContext);
	        	bioModel.removeSimulationContext(oldSimulationContext);
	        } else {
	        	newSimulationContext = bioModel.addNewSimulationContext("App1", appType);
	        }
	        
	        //
	        // making the new vCell simulation based on the sedml simulation
	        //
	        newSimulationContext.refreshDependencies();
	        MathMappingCallback callback = new MathMappingCallbackTaskAdapter(progressListener);
	        newSimulationContext.refreshMathDescription(callback, NetworkGenerationRequirements.ComputeFullStandardTimeout);
	    	Simulation newSimulation = new Simulation(newSimulationContext.getMathDescription());
	    	newSimulation.setName(sedmlSimulation.getName());	
	    	bioModel.addSimulation(newSimulation);
			
			// we identify the type of sedml simulation (uniform time course, etc) 
	    	// and set the vCell simulation parameters accordingly
			if(sedmlSimulation instanceof UniformTimeCourse) {
				
			} else if(sedmlSimulation instanceof OneStep) {
				
			} else if(sedmlSimulation instanceof SteadyState) {
				
			} else {
				
			}
			
			System.out.println(XmlHelper.bioModelToXML(bioModel));
        }
        
	}
	
	
	
//	public void runSimulation(Simulation originalSimulation, File localSimDataDir){
//		Simulation simulation = new TempSimulation(originalSimulation, false);
//		StdoutSessionLog log = new StdoutSessionLog("Quick run");
//		SimulationTask simTask = new SimulationTask(new SimulationJob(simulation, 0, null),0);
//		Solver solver = createQuickRunSolver(log, localSimDataDir, simTask);
//		if (solver == null) {
//			throw new RuntimeException("null solver");
//		}
//		// check if spatial stochastic simulation (smoldyn solver) has data processing instructions with field data - need to access server for field data, so cannot do local simulation run. 
//		if (solver instanceof SmoldynSolver) {
//			DataProcessingInstructions dpi = simulation.getDataProcessingInstructions();
//			if (dpi != null) {
//				FieldDataIdentifierSpec fdis = dpi.getSampleImageFieldData(simulation.getVersion().getOwner());	
//				if (fdis != null) {
//					throw new RuntimeException("Spatial Stochastic simulation '" + simulation.getName() + "' (Smoldyn solver) with field data (in data processing instructions) cannot be run locally at this time since field data needs to be retrieved from the VCell server.");
//				}
//			}
//		}
//		solver.addSolverListener(new SolverListener() {
//			public void solverStopped(SolverEvent event) {
//				getClientTaskStatusSupport().setMessage(event.getSimulationMessage().getDisplayMessage());
//			}
//			public void solverStarting(SolverEvent event) {
//				String displayMessage = event.getSimulationMessage().getDisplayMessage();
//				System.out.println(displayMessage);
//				getClientTaskStatusSupport().setMessage(displayMessage);
//				if(displayMessage.equals(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT.getDisplayMessage())) {
//					getClientTaskStatusSupport().setProgress(75);
//				} else if(displayMessage.equals(SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE.getDisplayMessage())) {
//					getClientTaskStatusSupport().setProgress(90);
//				}
//			}
//			public void solverProgress(SolverEvent event) {
//				getClientTaskStatusSupport().setMessage("Running...");
//				int progress = (int)(event.getProgress() * 100);
//				getClientTaskStatusSupport().setProgress(progress);
//			}
//			public void solverPrinted(SolverEvent event) {
//				getClientTaskStatusSupport().setMessage("Running...");
//			}
//			public void solverFinished(SolverEvent event) {
//				getClientTaskStatusSupport().setMessage(event.getSimulationMessage().getDisplayMessage());
//			}
//			public void solverAborted(SolverEvent event) {
//				getClientTaskStatusSupport().setMessage(event.getSimulationMessage().getDisplayMessage());
//			}
//		});
//		solver.startSolver();
//
//		while (true){
//			try { 
//				Thread.sleep(500); 
//			} catch (InterruptedException e) {
//			}
//
//			if (getClientTaskStatusSupport().isInterrupted()) {
//				solver.stopSolver();
//				throw UserCancelException.CANCEL_GENERIC;
//			}
//			SolverStatus solverStatus = solver.getSolverStatus();
//			if (solverStatus != null) {
//				if (solverStatus.getStatus() == SolverStatus.SOLVER_ABORTED) {
//					throw new RuntimeException(solverStatus.getSimulationMessage().getDisplayMessage());
//				}
//				if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
//					solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
//					solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
//					break;
//				}
//			}		
//		}
//		
//		ArrayList<AnnotatedFunction> outputFunctionsList = getSimWorkspace().getSimulationOwner().getOutputFunctionContext().getOutputFunctionsList();
//		OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
//		Simulation[] simsArray = new Simulation[] {simulation};
//		hashTable.put("outputContext", outputContext);
//		hashTable.put("simsArray", simsArray);
//
//	}
	
	
	public static void main(String[] args) {
		try {
			
			ResourceUtil.setNativeLibraryDirectory();

			doit(new File("C:\\temp\\fff\\BBasicModel.sedx"));
			
		}catch (Exception e){
			e.printStackTrace(System.out);
		}finally{
			System.exit(0);
		}

	}

}
