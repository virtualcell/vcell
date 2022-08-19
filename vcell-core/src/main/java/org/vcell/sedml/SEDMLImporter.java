package org.vcell.sedml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.Algorithm;
import org.jlibsedml.AlgorithmParameter;
import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.Change;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.Libsedml;
import org.jlibsedml.Model;
import org.jlibsedml.Output;
import org.jlibsedml.Range;
import org.jlibsedml.RepeatedTask;
import org.jlibsedml.SedML;
import org.jlibsedml.SetValue;
import org.jlibsedml.SubTask;
import org.jlibsedml.Task;
import org.jlibsedml.UniformRange;
import org.jlibsedml.UniformRange.UniformType;
import org.jlibsedml.UniformTimeCourse;
import org.jlibsedml.VectorRange;
import org.jlibsedml.XMLException;
import org.jlibsedml.execution.ArchiveModelResolver;
import org.jlibsedml.execution.FileModelResolver;
import org.jlibsedml.execution.ModelResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.jlibsedml.modelsupport.SUPPORTED_LANGUAGE;
import org.vcell.util.FileUtils;
import org.vcell.util.document.VCDocument;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ErrorTolerance.ErrorToleranceDescription;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.NonspatialStochHybridOptions;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverDescription.AlgorithmParameterDescription;
import cbit.vcell.solver.SolverDescription.SolverFeature;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.TimeStep.TimeStepDescription;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class SEDMLImporter {

	private final static Logger logger = LogManager.getLogger(SEDMLImporter.class);
	private SedML sedml;
	private ExternalDocInfo externalDocInfo;
	private boolean exactMatchOnly;
	private VCLogger transLogger;
	private List<BioModel> docs;
	private String bioModelBaseName;
	private ArchiveComponents ac;
	private ModelResolver resolver;
	
	public  SEDMLImporter(VCLogger transLogger, ExternalDocInfo externalDocInfo, SedML sedml, boolean exactMatchOnly) throws FileNotFoundException, XMLException {
		this.transLogger = transLogger;
		this.externalDocInfo = externalDocInfo;
		this.sedml = sedml;
		this.exactMatchOnly = exactMatchOnly;
		
		initialize();
	}
	
	private void initialize() throws FileNotFoundException, XMLException {
		bioModelBaseName = FileUtils.getBaseName(externalDocInfo.getFile().getAbsolutePath());		// extract bioModel name from sedx (or sedml) file
		if(externalDocInfo.getFile().getPath().toLowerCase().endsWith("sedx") || externalDocInfo.getFile().getPath().toLowerCase().endsWith("omex")) {
			ac = Libsedml.readSEDMLArchive(new FileInputStream(externalDocInfo.getFile().getPath()));
		}
		resolver = new ModelResolver(sedml);
		if(ac != null) {
			resolver.add(new ArchiveModelResolver(ac));
		} else {
			resolver.add(new FileModelResolver()); // assumes absolute paths
			String sedmlRelativePrefix = externalDocInfo.getFile().getParent() + File.separator;
			resolver.add(new RelativeFileModelResolver(sedmlRelativePrefix)); // in case model URIs are relative paths
		}
	}

	public  List<BioModel> getBioModels() throws Exception {
		docs = new ArrayList<BioModel>();
		try {
	        // iterate through all the elements and show them at the console
	        List<org.jlibsedml.Model> mmm = sedml.getModels();
	        for(Model mm : mmm) {
	            logger.trace("sedml model: "+mm.toString());
	            List<Change> listOfChanges = mm.getListOfChanges();
	            logger.debug("There are " + listOfChanges.size() + " changes in model "+mm.getId());
	        }
	        List<org.jlibsedml.Simulation> sss = sedml.getSimulations();
	        for(org.jlibsedml.Simulation ss : sss) {
	            logger.trace("sedml simulaton: "+ss.toString());
	        }
	        List<AbstractTask> ttt = sedml.getTasks();
	        for(AbstractTask tt : ttt) {
	            logger.trace("sedml task: "+tt.toString());
	        }
	        List<DataGenerator> ddd = sedml.getDataGenerators();
	        for(DataGenerator dd : ddd) {
	            logger.trace("sedml dataGenerator: "+dd.toString());
	        }
	        List<Output> ooo = sedml.getOutputs();
	        for(Output oo : ooo) {
	            logger.trace("sedml output: "+oo.toString());
	        }


			// We need to make a separate BioModel for each SED-ML model
			// We will parse all tasks and create Simulations for each in the BioModel(s) corresponding to the model referenced by the tasks
	    	String kisaoID = null;
	    	org.jlibsedml.Simulation sedmlSimulation = null;	// this will become the vCell simulation
	    	org.jlibsedml.Model sedmlOriginalModel = null;		// the "original" model referred to by the task
	    	String sedmlOriginalModelName = null;				// this will be used in the BioModel name
	    	String sedmlOriginalModelLanguage = null;			// can be sbml or vcml
			
			// Creating one VCell Simulation per SED-ML Task
			// VCell Biomodel(s) and Application(s) are also created as necessary
			HashMap<String, Simulation> vcSimulations = new HashMap<String, Simulation>();
			for (AbstractTask selectedTask : ttt) {
				if(selectedTask instanceof Task) {
					sedmlOriginalModel = sedml.getModelWithId(selectedTask.getModelReference());
					sedmlSimulation = sedml.getSimulation(selectedTask.getSimulationReference());
				} else if(selectedTask instanceof RepeatedTask) {
					// Repeated tasks refer to regular tasks
					// We need simulations to be created for all regular tasks before we can process repeated tasks
					logger.warn("RepeatedTask not supported yet, task "+SEDMLUtil.getName(selectedTask)+" is being skipped");
					continue;
				} else {
					throw new RuntimeException("Unexpected task " + selectedTask);
				}
				sedmlOriginalModelName = sedmlOriginalModel.getId();
				sedmlOriginalModelLanguage = sedmlOriginalModel.getLanguage();
				List<Change> listOfChanges = sedmlOriginalModel.getListOfChanges();		// get the list of changes now!

				// at this point we assume that the sedml simulation, algorithm and kisaoID are all valid
				Algorithm algorithm = sedmlSimulation.getAlgorithm();
				kisaoID = algorithm.getKisaoID();

				// identify the vCell solvers that would match best the sedml solver kisao id

				// try to find a match in the ontology tree
				SolverDescription solverDescription = SolverUtilities.matchSolverWithKisaoId(kisaoID, exactMatchOnly);
				if (solverDescription != null) {
					logger.info("Task (id='"+selectedTask.getId()+"') is compatible, solver match found in ontology: '" + kisaoID + "' matched to " + solverDescription);
				} else {
					// give it a try anyway with our deterministic default solver
					solverDescription = SolverDescription.CombinedSundials;
					logger.error("Task (id='"+selectedTask.getId()+")' is not compatible, no equivalent solver found in ontology for requested algorithm '"+kisaoID + "'; trying with deterministic default solver "+solverDescription);
				}
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

				// we make a biomodel for each task; if there are many simulations, probably 
				// only one will match the selected task id, the others are parasites and must not be run
				
				BioModel bioModel = null;
				boolean justMade = false;
				String newMdl = resolver.getModelString(sedmlOriginalModel);
				// no need for this, the resolver applies all changes
//				if(listOfChanges != null && listOfChanges.size() > 0) {
//					newMdl = updateXML(newMdl, listOfChanges);
//				}

				String bioModelName = bioModelBaseName + "_" + sedml.getFileName() + "_" + sedmlOriginalModelName;
				// get it if we made it already
				for (VCDocument existingDoc : docs) {
					if (!docs.isEmpty()) {
						if (existingDoc.getName().equals(bioModelName)) {
							bioModel = (BioModel)existingDoc;
							break;
						}
					}
				}	
				// make it if we didn't and mark it as fresh
				if (bioModel == null) {
					if(sedmlOriginalModelLanguage.contentEquals(SUPPORTED_LANGUAGE.VCELL_GENERIC.getURN())) {	// vcml
						XMLSource vcmlSource = new XMLSource(newMdl);
						bioModel = (BioModel)XmlHelper.XMLToBioModel(vcmlSource);
						bioModel.setName(bioModelName);
						docs.add(bioModel);
						justMade = true;
						try {
							bioModel.getVCMetaData().createBioPaxObjects(bioModel);
						} catch (Exception e) {
							logger.error("failed to make BioPax objects", e);
						}
					} else {				// we assume it's sbml, if it's neither import will fail
						XMLSource sbmlSource = new XMLSource(newMdl);		// sbmlSource with all the changes applied
						bioModel = (BioModel)XmlHelper.importSBML(transLogger, sbmlSource, bSpatial);
						bioModel.setName(bioModelName);
						docs.add(bioModel);
						justMade = true;
					}
				}
				
				if(sedmlOriginalModelLanguage.contentEquals(SUPPORTED_LANGUAGE.VCELL_GENERIC.getURN())) {
					// we don't need to make a simulation from sedml if we're coming from vcml, we already got all we need
					// we basically ignore the sedml simulation altogether
					Simulation theSimulation = null;
					for (Simulation sim : bioModel.getSimulations()) {
						if (sim.getName().equals(selectedTask.getName())) {
							logger.trace(" --- selected task - name: " + selectedTask.getName() + ", id: " + selectedTask.getId());
							sim.setImportedTaskID(selectedTask.getId());
							theSimulation = sim;
							break;	// found the one, no point to continue the for loop
						}
					}if(theSimulation == null) {
						logger.error("Couldn't match sedml task '" + selectedTask.getName() + "' with any biomodel simulation");
						// TODO: should we throw an exception?
						continue;	// should never happen
					}
					if(!(sedmlSimulation instanceof UniformTimeCourse)) {
						continue;
					}
					
					SolverTaskDescription simTaskDesc = theSimulation.getSolverTaskDescription();
					TimeBounds timeBounds = new TimeBounds();
					TimeStep timeStep = new TimeStep();
					double outputTimeStep = 0.1;
					int outputNumberOfPoints = 1;
					// we translate initial time to zero, we provide output for the duration of the simulation
					// because we can't select just an interval the way the SEDML simulation can
					double initialTime = ((UniformTimeCourse) sedmlSimulation).getInitialTime();
					double outputStartTime = ((UniformTimeCourse) sedmlSimulation).getOutputStartTime();
					double outputEndTime = ((UniformTimeCourse) sedmlSimulation).getOutputEndTime();
					outputNumberOfPoints = ((UniformTimeCourse) sedmlSimulation).getNumberOfPoints();
					outputTimeStep = (outputEndTime - outputStartTime) / outputNumberOfPoints;
					timeBounds = new TimeBounds(0, outputEndTime - initialTime);

					OutputTimeSpec outputTimeSpec = new UniformOutputTimeSpec(outputTimeStep);
					simTaskDesc.setTimeBounds(timeBounds);
					simTaskDesc.setTimeStep(timeStep);
					if (simTaskDesc.getSolverDescription().supports(outputTimeSpec)) {
						simTaskDesc.setOutputTimeSpec(outputTimeSpec);
					} else {
						simTaskDesc.setOutputTimeSpec(new DefaultOutputTimeSpec(1,Integer.max(DefaultOutputTimeSpec.DEFAULT_KEEP_AT_MOST, outputNumberOfPoints)));
					}
					//theSimulation.setSolverTaskDescription(simTaskDesc);
					//theSimulation.refreshDependencies();
					continue;
				}
				
				// even if we just created the biomodel from the sbml file we have at least one application with initial conditions and stuff
				// see if there is a suitable application type for the sedml kisao
				// if not, we add one by doing a "copy as" to the right type
				SimulationContext[] existingSimulationContexts = bioModel.getSimulationContexts();
				SimulationContext matchingSimulationContext = null;
				for (SimulationContext simContext : existingSimulationContexts) {
					if (simContext.getApplicationType().equals(appType) && ((simContext.getGeometry().getDimension() > 0) == bSpatial)) {
						matchingSimulationContext = simContext;
						break;
					}
				}
				if (matchingSimulationContext == null) {
					if (justMade) {
						matchingSimulationContext = SimulationContext.copySimulationContext(existingSimulationContexts[0], sedmlOriginalModelName, bSpatial, appType);
						bioModel.removeSimulationContext(existingSimulationContexts[0]);
					} else {
						matchingSimulationContext = SimulationContext.copySimulationContext(existingSimulationContexts[0], sedmlOriginalModelName+"_"+existingSimulationContexts.length, bSpatial, appType);
					}
					bioModel.addSimulationContext(matchingSimulationContext);
				}
				matchingSimulationContext.refreshDependencies();
				MathMappingCallback callback = new MathMappingCallbackTaskAdapter(null);
				matchingSimulationContext.refreshMathDescription(callback, NetworkGenerationRequirements.ComputeFullStandardTimeout);

				// making the new vCell simulation based on the sedml simulation
				if(!(sedmlSimulation instanceof UniformTimeCourse)) {
					// we don't even bother if it's an unsupported type
					continue;
				}
				Simulation newSimulation = new Simulation(matchingSimulationContext.getMathDescription());
				newSimulation.setSimulationOwner(matchingSimulationContext);
				if (selectedTask instanceof Task) {
					String newSimName = selectedTask.getId();
					if(SEDMLUtil.getName(selectedTask) != null) {
						newSimName += "_" + SEDMLUtil.getName(selectedTask);
					}
					newSimulation.setName(newSimName);
					newSimulation.setImportedTaskID(selectedTask.getId());
					vcSimulations.put(selectedTask.getId(), newSimulation);
				} else {
					newSimulation.setName(SEDMLUtil.getName(sedmlSimulation)+"_"+SEDMLUtil.getName(selectedTask));
				}
				
				// we identify the type of sedml simulation (uniform time course, etc)
				// and set the vCell simulation parameters accordingly
				SolverTaskDescription simTaskDesc = newSimulation.getSolverTaskDescription();
				if(solverDescription != null) {
					simTaskDesc.setSolverDescription(solverDescription);
				}

				TimeBounds timeBounds = new TimeBounds();
				TimeStep timeStep = new TimeStep();
				double outputTimeStep = 0.1;
				int outputNumberOfPoints = 1;
				if(sedmlSimulation instanceof UniformTimeCourse) {
					// we translate initial time to zero, we provide output for the duration of the simulation
					// because we can't select just an interval the way the SEDML simulation can
					double initialTime = ((UniformTimeCourse) sedmlSimulation).getInitialTime();
					double outputStartTime = ((UniformTimeCourse) sedmlSimulation).getOutputStartTime();
					double outputEndTime = ((UniformTimeCourse) sedmlSimulation).getOutputEndTime();
					outputNumberOfPoints = ((UniformTimeCourse) sedmlSimulation).getNumberOfPoints();
					outputTimeStep = (outputEndTime - outputStartTime) / outputNumberOfPoints;
					timeBounds = new TimeBounds(0, outputEndTime - initialTime);
//				} else if(sedmlSimulation instanceof OneStep) {		// for anything other than UniformTimeCourse we just ignore
//					System.err.println("OneStep Simulation not supported");
//				} else if(sedmlSimulation instanceof SteadyState) {
//					System.err.println("SteadyState Simulation not supported");
				}
				simTaskDesc.setTimeBounds(timeBounds);
				simTaskDesc.setTimeStep(timeStep);

				// we look for explicit algorithm parameters
				ErrorTolerance errorTolerance = new ErrorTolerance();
				List<AlgorithmParameter> sedmlAlgorithmParameters = algorithm.getListOfAlgorithmParameters();
				for(AlgorithmParameter sedmlAlgorithmParameter : sedmlAlgorithmParameters) {

					String apKisaoID = sedmlAlgorithmParameter.getKisaoID();
					String apValue = sedmlAlgorithmParameter.getValue();
					if(apKisaoID == null || apKisaoID.isEmpty()) {
						logger.error("Undefined KisaoID algorithm parameter for algorithm '" + kisaoID + "'");
					}

					// we don't check if the recognized algorithm parameters are valid for our algorithm
					// we just use any parameter we find for the solver task description we have, assuming the exporting code did all the checks
					// WARNING: if our algorithm is the result of a guess, this may be wrong
					// TODO: use the proper ontology for the algorithm parameters kisao id
					if(apKisaoID.contentEquals(ErrorTolerance.ErrorToleranceDescription.Absolute.getKisao())) {
						double value = Double.parseDouble(apValue);
						errorTolerance.setAbsoluteErrorTolerance(value);
					} else if(apKisaoID.contentEquals(ErrorTolerance.ErrorToleranceDescription.Relative.getKisao())) {
						double value = Double.parseDouble(apValue);
						errorTolerance.setRelativeErrorTolerance(value);
					} else if(apKisaoID.contentEquals(TimeStep.TimeStepDescription.Default.getKisao())) {
						double value = Double.parseDouble(apValue);
						timeStep.setDefaultTimeStep(value);
					} else if(apKisaoID.contentEquals(TimeStep.TimeStepDescription.Maximum.getKisao())) {
						double value = Double.parseDouble(apValue);
						timeStep.setMaximumTimeStep(value);
					} else if(apKisaoID.contentEquals(TimeStep.TimeStepDescription.Minimum.getKisao())) {
						double value = Double.parseDouble(apValue);
						timeStep.setMinimumTimeStep(value);
					} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Seed.getKisao())) {		// custom seed
						if(simTaskDesc.getSimulation().getMathDescription().isNonSpatialStoch()) {
							NonspatialStochSimOptions nssso = simTaskDesc.getStochOpt();
							int value = Integer.parseInt(apValue);
							nssso.setCustomSeed(value);
						} else {
							logger.error("Algorithm parameter '" + AlgorithmParameterDescription.Seed.getDescription() +"' is only supported for nonspatial stochastic simulations");
						}
						// some arguments used only for non-spatial hybrid solvers
					} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Epsilon.getKisao())) {
						NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();
						nssho.setEpsilon(Double.parseDouble(apValue));
					} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.Lambda.getKisao())) {
						NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();
						nssho.setLambda(Double.parseDouble(apValue));
					} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.MSRTolerance.getKisao())) {
						NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();
						nssho.setMSRTolerance(Double.parseDouble(apValue));
					} else if(apKisaoID.contentEquals(AlgorithmParameterDescription.SDETolerance.getKisao())) {
						NonspatialStochHybridOptions nssho = simTaskDesc.getStochHybridOpt();
						nssho.setSDETolerance(Double.parseDouble(apValue));
					} else {
						logger.error("Algorithm parameter with kisao id '" + apKisaoID + "' not supported at this time, skipping.");
					}
				}
				simTaskDesc.setErrorTolerance(errorTolerance);

				OutputTimeSpec outputTimeSpec = new UniformOutputTimeSpec(outputTimeStep);
				if (simTaskDesc.getSolverDescription().supports(outputTimeSpec)) {
					simTaskDesc.setOutputTimeSpec(outputTimeSpec);
				} else {
					simTaskDesc.setOutputTimeSpec(new DefaultOutputTimeSpec(1,Integer.max(DefaultOutputTimeSpec.DEFAULT_KEEP_AT_MOST, outputNumberOfPoints)));
				}
				newSimulation.setSolverTaskDescription(simTaskDesc);
				newSimulation.setDescription(SEDMLUtil.getName(selectedTask));
				bioModel.addSimulation(newSimulation);
				newSimulation.refreshDependencies();
			}
			// now processing repeated tasks, if any
			for (AbstractTask selectedTask : ttt) {
				if (selectedTask instanceof RepeatedTask) {
					RepeatedTask rt = (RepeatedTask)selectedTask;
					if (!rt.getResetModel() || rt.getSubTasks().size() != 1) {
						logger.error("sequential RepeatedTask not yet supported, task "+SEDMLUtil.getName(selectedTask)+" is being skipped");
						continue;
					}
					if (rt.getChanges().size() != 1) {
						logger.error("lockstep multiple parameter scans are not yet supported, task "+SEDMLUtil.getName(selectedTask)+" is being skipped");
						continue;
					}
					AbstractTask referredTask;
					// find the actual Task, which can be directly referred or indirectly through a chain of RepeatedTasks (in case of multiple parameter scans)
					do {
						SubTask st = rt.getSubTasks().entrySet().iterator().next().getValue(); // single subtask
						String taskId = st.getTaskId();
						referredTask = sedml.getTaskWithId(taskId);
						if (referredTask instanceof RepeatedTask) rt = (RepeatedTask)referredTask;
					} while (referredTask instanceof RepeatedTask);
					Task actualTask = (Task)referredTask;
					Simulation simulation = vcSimulations.get(actualTask.getId());
					// now add a parameter scan to the simulation referred by the actual task
					ConstantArraySpec scanSpec = null;
					rt = (RepeatedTask)selectedTask;
					SetValue change = rt.getChanges().get(0); // single param scan
					SBMLSupport sbmlSupport = new SBMLSupport();
					String targetID = sbmlSupport.getIdFromXPathIdentifer(change.getTargetXPath().getTargetAsString());
					Range range = rt.getRange(change.getRangeReference());
					// TODO start
					// need to map the SBML target to VCell constant name
					String constant = targetID; // placeholder
					// TODO end
					if (range instanceof UniformRange) {
						UniformRange ur = (UniformRange)range;
						scanSpec = ConstantArraySpec.createIntervalSpec(constant, Math.min(ur.getStart(), ur.getEnd()), Math.max(ur.getStart(), ur.getEnd()), ur.getNumberOfPoints(), ur.getType().equals(UniformType.LOG));
					} else if (range instanceof VectorRange) {
						VectorRange vr = (VectorRange)range;
						String[] values = new String[vr.getNumElements()];
						for (int i = 0; i < values.length; i++) {
							values[i] = Double.toString(vr.getElementAt(i));
						}
						scanSpec = ConstantArraySpec.createListSpec(constant, values);
					} else {
						logger.error("unsupported Range class found, task "+SEDMLUtil.getName(selectedTask)+" is being skipped");
						continue;						
					}
					MathOverrides mo = simulation.getMathOverrides();
					mo.putConstantArraySpec(scanSpec);
				}
			}
			return docs;
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize bioModel for the given selection\n"+e.getMessage(), e);
		}
	}

}
