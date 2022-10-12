package org.vcell.cli.run;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.AbstractJavaSolver;
import cbit.vcell.solver.ode.ODESolver;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solver.stoch.GibsonSolver;
import cbit.vcell.solver.stoch.HybridSolver;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XmlHelper;

import org.jlibsedml.AbstractTask;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.DataSet;
import org.jlibsedml.Output;
import org.jlibsedml.Range;
import org.jlibsedml.RepeatedTask;
import org.jlibsedml.Report;
import org.jlibsedml.SedML;
import org.jlibsedml.SetValue;
import org.jlibsedml.SubTask;
import org.jlibsedml.Task;
import org.jlibsedml.UniformTimeCourse;
import org.jlibsedml.Variable;
import org.jlibsedml.XPathTarget;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.jmathml.ASTNode;
import org.vcell.cli.CLIRecorder;
import org.vcell.cli.vcml.VCMLHandler;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.document.VCDocument;
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SolverHandler {
	
    private final static Logger logger = LogManager.getLogger(SolverHandler.class);

	public int countBioModels = 0;		// number of biomodels in this sedml file
	public int countSuccessfulSimulationRuns = 0;	// number of simulations that we ran successfully for this sedml file
	public Map <String, String> sim2Hdf5Map = new LinkedHashMap<> ();
	
    Map<Simulation, AbstractTask> simulationToTaskMap = new LinkedHashMap<> ();	// key = vcell simulation, value = sedml topmost task (the imported task id)
    Map<AbstractTask, Simulation> taskToSimulationMap = new LinkedHashMap<> ();	// the opposite
    Map<AbstractTask, List<AbstractTask>> taskToListOfSubTasksMap = new LinkedHashMap<> ();	// key = topmost AbstractTask, value = recursive list of subtasks
    Map<AbstractTask, List<Variable>> taskToVariableMap = new LinkedHashMap<> ();	// key = AbstractTask, value = list of variables calculated by this task
    Map<RepeatedTask, Set<String>> taskToChangeTargetMap = new LinkedHashMap<> ();	// key = RepeatedTask, value = list of the parameters that are being changed
    Map<Task, Set<RepeatedTask>> taskToChildRepeatedTasks = new LinkedHashMap<> ();	// key = Task, value = list of RepeatedTasks ending with this task
    Map<String, Task> topTaskToBaseTask = new LinkedHashMap<> ();				// key = TopmostTaskId, value = Tasks at the bottom of the SubTasks chain OR the topmost task itself if instanceof Task

    private static void sanityCheck(VCDocument doc) {
        if (doc == null) {
            throw new RuntimeException("Imported VCDocument is null.");
        }
        String docName = doc.getName();
        if (docName == null || docName.isEmpty()) {
            throw new RuntimeException("The name of the imported VCDocument is null or empty.");
        }
        if (!(doc instanceof BioModel)) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' is not a BioModel.");
        }
        BioModel bioModel = (BioModel) doc;
        if (bioModel.getSimulationContext(0) == null) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' has no Application");
        }
        if (bioModel.getSimulation(0) == null) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' has no Simulation");
        }
    }

    public void initialize(List<BioModel> bioModelList, SedML sedml) throws ExpressionException {
    	
    	Set <AbstractTask> topmostTasks = new LinkedHashSet<> ();
        for(BioModel bioModel : bioModelList) {
        	Simulation[] sims = bioModel.getSimulations();
        	for(Simulation sim : sims) {
               	if(sim.getImportedTaskID() == null) {
            		continue;
            	}
               	String importedTaskId = sim.getImportedTaskID();
               	AbstractTask at = sedml.getTaskWithId(importedTaskId);
           		simulationToTaskMap.put(sim, at);
           		taskToSimulationMap.put(at,  sim);
           		topmostTasks.add(at);	// all the tasks referred by an importedTaskId are supposed to be topmost
        	}
        }
        
        {
    	// we first make a list of all the sub tasks (may be instanceof Task or RepeatedTask)
        Set <AbstractTask> subTasks = new LinkedHashSet<> ();
        for(AbstractTask at : sedml.getTasks()) {
        	if(at instanceof RepeatedTask) {
        		RepeatedTask rt = (RepeatedTask)at;
        		Map<String, SubTask> subTasksOfRepeatedTask = rt.getSubTasks();
        		for (Map.Entry<String, SubTask> entry : subTasksOfRepeatedTask.entrySet()) {
        			String subTaskId = entry.getKey();
        			AbstractTask subTask = sedml.getTaskWithId(subTaskId);
       				subTasks.add(subTask);
        		}
        	}
        }
        // then we make a list of all topmost tasks (Task or RepeatedTask that are not a subtask)
    	// is the topmost task of a chain that ends with an actual task
        Set <AbstractTask> topmostTasks2 = new LinkedHashSet<> ();	// topmost tasks, different way to calculate (they are not in the list of subtasks above)
        for(AbstractTask at : sedml.getTasks()) {
       		if(!subTasks.contains(at)) {
       			topmostTasks2.add(at);
       		}
        }
        if(topmostTasks.size() != topmostTasks2.size()) {
            logger.error("TopmostTasks lists sizes are different.");
//        	throw new RuntimeException("TopmostTasks lists sizes are different.");
        }
        for (AbstractTask task : topmostTasks) {		// we have higher confidence that topmostTask is correct
   			List<AbstractTask> subTasksList = new ArrayList<> ();
			AbstractTask referredTask;
			RepeatedTask rt;
			Task actualTask;
			// find the actual Task and extract the simulation
			if(task instanceof RepeatedTask) {
				rt = (RepeatedTask)task;
				do {
					SubTask st = rt.getSubTasks().entrySet().iterator().next().getValue(); // single subtask
					String taskId = st.getTaskId();
					referredTask = sedml.getTaskWithId(taskId);
					if (referredTask instanceof RepeatedTask) {
						rt = (RepeatedTask)referredTask;
					}
					subTasksList.add(referredTask);				// last entry added will be a instanceof Task
				} while (referredTask instanceof RepeatedTask);
				actualTask = (Task)referredTask;
			} else {
				actualTask = (Task)task;
			}
        	taskToListOfSubTasksMap.put(task, subTasksList);	// subTasksList may be empty if task instanceof Task
        	topTaskToBaseTask.put(task.getId(), actualTask);

        	Set<RepeatedTask> childRepeatedTasks = new LinkedHashSet<> ();
			taskToChildRepeatedTasks.put(actualTask, childRepeatedTasks);	// list of all Tasks, the set is only initialized here
        }
        for(Map.Entry<AbstractTask, List<AbstractTask>> entry : taskToListOfSubTasksMap.entrySet()) {	// populate the taskToChildRepeatedTasks map
        	AbstractTask topmostTask = entry.getKey();
        	List<AbstractTask> dependingTasks = entry.getValue();
        	if(topmostTask instanceof Task) {
        		// nothing to do except some sanity checks maybe
        		// the taskToChildRepeatedTasks contains this key and the associated set should be empty
//        		assert dependingTasks.isEmpty() == true;							// the dependingTasks list should be empty
//        		assert taskToChildRepeatedTasks.containsKey(topmostTask) == true;	// the Task should be a key in the map
//        		assert taskToChildRepeatedTasks.get(topmostTask).isEmpty() == true;	// the set of repeated tasks associated to this task should be empty
        	} else {	// this is a RepeatedTask
        		// or use Task actualTask = topTaskToBaseTask.get(topmostTask.getId());
        		Task actualTask = null;
        		for(AbstractTask dependingTask : dependingTasks) {
        			if(dependingTask instanceof Task) {		// should always be one Task at the end of the list
        				actualTask = (Task)dependingTask;
        				break;		// we found the only Task
        			}
        		}
//        		assert rootTask != null;
        		Set<RepeatedTask> childRepeatedTasks = taskToChildRepeatedTasks.get(actualTask);
//        		assert childRepeatedTasks.isEmpty() == true;
        		childRepeatedTasks.add((RepeatedTask)topmostTask);
        		for(AbstractTask dependingTask : dependingTasks) {
        			if(dependingTask instanceof RepeatedTask) {
        				childRepeatedTasks.add((RepeatedTask)dependingTask);
        			}
        		}
        	}
        }
        }
        
        {
        // 
        // key = tasks that are used for generating some output
        //
        Map<Variable, AbstractTask> variableToTaskMap = new LinkedHashMap<> ();		// temporary use
        List<Output> ooo = sedml.getOutputs();
        for(Output oo : ooo) {
        	if(oo instanceof Report) {
        		// TODO: check if multiple reports may use different tasks for the same variable
        		// here we assume that each variable may only be the result of one task
        		// the variable id we produce in vcell is definitely correct since the 
        		// variable id is constructed based on the task id
                List<DataSet> datasets = ((Report) oo).getListOfDataSets();
                for (DataSet dataset : datasets) {
                    DataGenerator datagen = sedml.getDataGeneratorWithId(dataset.getDataReference());
//                    assert datagen != null;
                    List<Variable> vars = new ArrayList<>(datagen.getListOfVariables());
                    for(Variable var : vars) {
                    	AbstractTask task = sedml.getTaskWithId(var.getReference());
                    	variableToTaskMap.put(var, task);
                    }
                }
        	}
        }
        for(Map.Entry<Variable, AbstractTask> entry : variableToTaskMap.entrySet()) {
        	Variable var = entry.getKey();
        	AbstractTask task = entry.getValue();
        	if(!taskToVariableMap.containsKey(task)) {
        		List<Variable> vars = new ArrayList<> ();
        		vars.add(var);
        		taskToVariableMap.put(task, vars);
        	} else {
        		List<Variable> vars = taskToVariableMap.get(task);
        		vars.add(var);
        		taskToVariableMap.put(task, vars);
        	}
        }
        }
        
        for (Map.Entry<AbstractTask, List<AbstractTask>> entry : taskToListOfSubTasksMap.entrySet()) {
        	AbstractTask topTask = entry.getKey();
        	Task actualTask = topTaskToBaseTask.get(topTask.getId());
        	Simulation sim = taskToSimulationMap.get(topTask);
			int scanCount = sim.getScanCount();
			
			if(scanCount > 1) {		// we know that topTask is a RepeatedTask
//				assert task instanceof RepeatedTask;
//				assert !subTasksList.isEmpty();
				
				// TODO: the logic is probably bad here, we need to look atall the repeated tasks in chain
				// and identify all changes. (it's not being used, so not urgent)
				SBMLSupport sbmlSupport = new SBMLSupport();
				RepeatedTask rt = (RepeatedTask)topTask;

				List<SetValue> changes = rt.getChanges();
				Set<String> targetIdSet = new LinkedHashSet<>();
				for(SetValue change : changes) {
					XPathTarget target = change.getTargetXPath();
					String starget = target.getTargetAsString();
					Range range = rt.getRange(change.getRangeReference());
					ASTNode math = change.getMath();
					Expression exp = new ExpressionMathMLParser(null).fromMathML(math, "t");
					if (exp.infix().equals(range.getId())) {
						String targetID = sbmlSupport.getIdFromXPathIdentifer(starget);
						Enumeration<String> overridesHashKeys = sim.getMathOverrides().getOverridesHashKeys();
						boolean found = false;
						while(overridesHashKeys.hasMoreElements()) {
							String candidate = overridesHashKeys.nextElement();
							if(candidate.equals(targetID)) {
								targetIdSet.add(targetID);
								found = true;
								break;
							}
						}
//						assert found == true;
					}
				}
				taskToChangeTargetMap.put(rt, targetIdSet);
			}
        }
        System.out.println("taskToSimulationMap: " + taskToSimulationMap.size());
        System.out.println("taskToListOfSubTasksMap: " + taskToListOfSubTasksMap.size());
        System.out.println("taskToVariableMap: " + taskToVariableMap.size());
        System.out.println("topTaskToBaseTask: " + topTaskToBaseTask.size());
    }

    public Map<TaskJob, ODESolverResultSet>
            simulateAllTasks(ExternalDocInfo externalDocInfo, SedML sedml, CLIRecorder cliLogger, 
                             File outputDirForSedml, String outDir, String outputBaseDir, String sedmlLocation,
                             boolean keepTempFiles, boolean exactMatchOnly) throws Exception {
        // create the VCDocument(s) (bioModel(s) + application(s) + simulation(s)), do sanity checks
        cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
        String inputFile = externalDocInfo.getFile().getAbsolutePath();
        String bioModelBaseName = org.vcell.util.FileUtils.getBaseName(inputFile);
        
        List<BioModel> bioModelList = null;
        // Key String is SEDML Task ID
        Map<TaskJob, ODESolverResultSet> resultsHash = new LinkedHashMap<TaskJob, ODESolverResultSet>();
        String docName = null;
        Simulation[] sims = null;
        //String outDirRoot = outputDirForSedml.toString().substring(0, outputDirForSedml.toString().lastIndexOf(System.getProperty("file.separator")));
        try {
            bioModelList = XmlHelper.importSEDML(sedmlImportLogger, externalDocInfo, sedml, exactMatchOnly);
        } catch (Exception e) {
            logger.error("Unable to Parse SED-ML into Bio-Model, failed with err: " + e.getMessage(), e);
            throw e;
        }
        if(bioModelList != null) {
        	countBioModels = bioModelList.size();
        }
        
        initialize(bioModelList, sedml);

        int simulationJobCount = 0;
        int bioModelCount = 0;
        boolean hasSomeSpatial = false;
        boolean bTimeoutFound = false;
        
        for (BioModel bioModel : bioModelList) {
            try {
                SolverHandler.sanityCheck(bioModel);
            } catch (Exception e) {
                logger.error("Exception encountered: " + e.getMessage(), e);
                // continue;
            }
            docName = bioModel.getName();
            sims = bioModel.getSimulations();
            
            Map <Simulation, Status> simStatusMap = new LinkedHashMap<> ();
            Map <Simulation, Integer> simDurationMap = new LinkedHashMap<> ();
			List<SimulationJob> simJobsList = new ArrayList<>();
			for (Simulation sim : sims) {
				if(sim.getImportedTaskID() == null) {
					continue;	// this is a simulation not matching the imported task, so we skip it
				}
				
				AbstractTask task = simulationToTaskMap.get(sim);
				simStatusMap.put(sim, Status.RUNNING);
				simDurationMap.put(sim, 0);

				int scanCount = sim.getScanCount();
				for(int i=0; i < scanCount; i++) {
					SimulationJob simJob = new SimulationJob(sim, i, null);
					simJobsList.add(simJob);
				}
				
//				String[] targets = sim.getMathOverrides().getScannedConstantNames();
//				java.util.Arrays.sort(targets);
//				int[] bounds = new int[targets.length]; // bounds of scanning matrix
//				for (int i=0; i < targets.length; i++) {
//					bounds[i] = sim.getMathOverrides().getConstantArraySpec(targets[i]).getNumValues() - 1;
//				}
//				int[] coordinates = BeanUtils.indexToCoordinate(scanCount, bounds);
			}
            
			for (SimulationJob simJob : simJobsList) {
				logger.debug("Initializing simulation job... ");
				String logTaskMessage = "Initializing simulation job " + simJob.getJobIndex() + " ... ";
				String logTaskError = "";
				long startTimeTask = System.currentTimeMillis();

                SimulationTask simTask;
                String kisao = "null";
            	ODESolverResultSet odeSolverResultSet = null;
            	SolverTaskDescription std = null;
            	SolverDescription sd = null;
            	int solverStatus = SolverStatus.SOLVER_READY;
            	
            	Simulation sim = simJob.getSimulation();
            	try {
                	SimulationOwner so = sim.getSimulationOwner();
                	sim = new TempSimulation(sim, false);
                	sim.setSimulationOwner(so);
                	
                	std = sim.getSolverTaskDescription();
                	sd = std.getSolverDescription();
                	kisao = sd.getKisao();
                	if(kisao == null) {
                		throw new RuntimeException("KISAO is null.");
                	}
                	simTask = new SimulationTask(simJob, 0);
                	Solver solver = SolverFactory.createSolver(outputDirForSedml, simTask, false);
                	logTaskMessage += "done. Starting simulation... ";

                	if(sd.isSpatial()) {
                		hasSomeSpatial = true;
                	}
                	if (solver instanceof AbstractCompiledSolver) {
                        ((AbstractCompiledSolver) solver).runSolver();
                        logger.info("Solver: " + solver);
                        logger.info("Status: " + solver.getSolverStatus());
						if (solver instanceof ODESolver) {
                            odeSolverResultSet = ((ODESolver) solver).getODESolverResultSet();
                        } else if (solver instanceof GibsonSolver) {
                            odeSolverResultSet = ((GibsonSolver) solver).getStochSolverResultSet();
                        } else if (solver instanceof HybridSolver) {
                            odeSolverResultSet = ((HybridSolver) solver).getHybridSolverResultSet();
                        } else {
                        	String str = "Solver results are not compatible with CSV format. ";
                            logger.error(str);
//                            keepTempFiles = true;		// temp fix for Jasraj
//                        	throw new RuntimeException(str);
                        }
                    } else if (solver instanceof AbstractJavaSolver) {
                        ((AbstractJavaSolver) solver).runSolver();
                        odeSolverResultSet = ((ODESolver) solver).getODESolverResultSet();
                        // must interpolate data for uniform time course which is not supported natively by the Java solvers
                        Task task = (Task) sedml.getTaskWithId(sim.getImportedTaskID());
                        assert task != null;
                        org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(task.getSimulationReference());
                        if (sedmlSim instanceof UniformTimeCourse) {
                            odeSolverResultSet = RunUtils.interpolate(odeSolverResultSet, (UniformTimeCourse) sedmlSim);
                            logTaskMessage += "done. Interpolating... ";
                        }
                    } else {
                        // this should actually never happen...
                    	String str = "Unexpected solver: " + kisao + " " + solver + ". ";
                        throw new RuntimeException(str);
                    }
                   
                    if (solver.getSolverStatus().getStatus() == SolverStatus.SOLVER_FINISHED) {
                    	
                    	if(sd.isSpatial()) {
                    		// TODO: uncomment this for export to HDF5 for spatial
//                        	File hdf5Results = new File(outDir + System.getProperty("file.separator") + "reports.h5");
//                        	try {
//                        		RunUtils.exportPDE2HDF5(sim, outputDirForSedml, hdf5Results);
//                        		sim2Hdf5Map.put(sim.getImportedTaskID(), null);
//                        	} catch(Exception e) {
//                        		sim2Hdf5Map.put(sim.getImportedTaskID(), null);
//                        	}
                    	}
                    	
                    	logTaskMessage += "done. ";
                        logger.info("Succesful execution: Model '" + docName + "' Task '" + sim.getDescription() + "'.");

                        long endTimeTask = System.currentTimeMillis();
                		long elapsedTime = endTimeTask - startTimeTask;
                		int duration = (int)Math.ceil(elapsedTime /1000.0);

                		Simulation originalSim = simJob.getSimulation();
                		int simDuration = simDurationMap.get(originalSim);
                		simDuration += duration;
                		simDurationMap.put(originalSim, simDuration);

                		String msg = "Running simulation " + simTask.getSimulation().getName() + ", " + elapsedTime + " ms";
                		logger.info(msg);
                		countSuccessfulSimulationRuns++;	// we only count the number of simulations (tasks) that succeeded
//                		PythonCalls.updateTaskStatusYml(sedmlLocation, sim.getImportedTaskID(), Status.SUCCEEDED, outDir ,duration + "", kisao);
                		if(simStatusMap.get(originalSim) != Status.ABORTED && simStatusMap.get(originalSim) != Status.FAILED) {
                			simStatusMap.put(originalSim, Status.SUCCEEDED);
                		}
                		PythonCalls.setOutputMessage(sedmlLocation, sim.getImportedTaskID(), outDir, "task", logTaskMessage);
                        RunUtils.drawBreakLine("-", 100);
                    } else {
                        String error = solver.getSolverStatus().getSimulationMessage().getDisplayMessage();
                    	solverStatus = solver.getSolverStatus().getStatus();
                        logger.error("Solver status: " + solverStatus);
                        logger.error("Solver message: " + error);
                        
                        throw new RuntimeException(error + " ");
                    }
                } catch (Exception e) {
                	String error = "Failed execution: Model '" + docName + "' Task '" + sim.getDescription() + "'. ";
                    logger.error(error);
                    
                    long endTime = System.currentTimeMillis();
            		long elapsedTime = endTime - startTimeTask;
            		int duration = (int)Math.ceil(elapsedTime /1000.0);
            		String msg = "Running simulation for " + elapsedTime + " ms";
            		logger.info(msg);
                    
            		if(sim.getImportedTaskID() == null) {
            			String str = "'null' imported task id, this should never happen. ";
            			logger.error(str);
            			logTaskError += str;
            		} else {
                		Simulation originalSim = simJob.getSimulation();
            			if(solverStatus == SolverStatus.SOLVER_ABORTED) {
//            				PythonCalls.updateTaskStatusYml(sedmlLocation, sim.getImportedTaskID(), Status.ABORTED, outDir ,duration + "", kisao);
            				simStatusMap.put(originalSim, Status.ABORTED);
            			} else {
//            				PythonCalls.updateTaskStatusYml(sedmlLocation, sim.getImportedTaskID(), Status.FAILED, outDir ,duration + "", kisao);
            				simStatusMap.put(originalSim, Status.FAILED);
            			}
            		}
//                    CLIUtils.finalStatusUpdate(CLIUtils.Status.FAILED, outDir);
                    if (e.getMessage() != null) {
                        // something else than failure caught by solver instance during execution
                    	logTaskError += (e.getMessage() + ". ");
                        logger.error(e.getMessage());
                    } else {
                    	logTaskError += (error + ". ");
                    }
                    String type = e.getClass().getSimpleName();
                    PythonCalls.setOutputMessage(sedmlLocation, sim.getImportedTaskID(), outDir, "task", logTaskMessage);
                    PythonCalls.setExceptionMessage(sedmlLocation, sim.getImportedTaskID(), outDir, "task", type, logTaskError);
                    String sdl = "";
                    if(sd != null && sd.getShortDisplayLabel() != null && !sd.getShortDisplayLabel().isEmpty()) {
                    	sdl = sd.getShortDisplayLabel();
                    } else {
                    	sdl = kisao;
                    }
                    if(logTaskError.contains("Process timed out")) {
                    	if(bTimeoutFound == false) {		// don't repeat this for each task
                    		String str = logTaskError.substring(0, logTaskError.indexOf("Process timed out"));
                    		str += "Process timed out";		// truncate the rest of the spam
                            cliLogger.writeDetailedErrorList(bioModelBaseName + ",  solver: " + sdl + ": " + type + ": " + str);
                        	bTimeoutFound = true;
                    	}
                    } else {
                        cliLogger.writeDetailedErrorList(bioModelBaseName + ",  solver: " + sdl + ": " + type + ": " + logTaskError);
                    }
                    RunUtils.drawBreakLine("-", 100);
                }
                AbstractTask task = sedml.getTaskWithId(sim.getImportedTaskID());
                if(odeSolverResultSet != null) {
                    resultsHash.put(new TaskJob(task.getId(), simJob.getJobIndex()), odeSolverResultSet);
                } else {
                	resultsHash.put(new TaskJob(task.getId(), simJob.getJobIndex()), null);	// if any task fails, we still put it in the hash with a null value
                }
                if(keepTempFiles == false) {
                	RunUtils.removeIntermediarySimFiles(outputDirForSedml);
                }
                simulationJobCount++;
            }
			for(Map.Entry<Simulation, Status> entry : simStatusMap.entrySet()) {
				
				Simulation sim = entry.getKey();
	        	Status status = entry.getValue();
	        	if(status == Status.RUNNING) {
	        		continue;	// if this happens somehow, we just don't write anything
	        	}
	        	AbstractTask task = simulationToTaskMap.get(sim);
//	        	assert task != null;
	        	int duration = simDurationMap.get(sim);
            	SolverTaskDescription std = sim.getSolverTaskDescription();
            	SolverDescription sd = std.getSolverDescription();
            	String kisao = sd.getKisao();
        		PythonCalls.updateTaskStatusYml(sedmlLocation, task.getId(), status, outDir ,duration + "", kisao);

        		List<AbstractTask> children = taskToListOfSubTasksMap.get(task);
	        	for(AbstractTask rt : children) {
	        		PythonCalls.updateTaskStatusYml(sedmlLocation, rt.getId(), status, outDir ,duration + "", kisao);
	        	}
			}
            bioModelCount++;
        }
        logger.info("Ran " + simulationJobCount + " simulation jobs for " + bioModelCount + " biomodels.");
        if(hasSomeSpatial) {
            cliLogger.writeSpatialList(bioModelBaseName);
        }
        return resultsHash;
    }

    /**
     * this function is unmaintained, do not run it
     */
    @Deprecated
    public HashMap<String, ODESolverResultSet> simulateAllVcmlTasks(File vcmlPath, File outputDir) throws Exception {
    	
    	throw new NotImplementedException("Not Implemented");
    	
//        // create the VCDocument(s) (bioModel(s) + application(s) + simulation(s)), do sanity checks
//        //List<VCDocument> docs = null;
//        // Key String is SEDML Task ID
//        HashMap<String, ODESolverResultSet> resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
//        String biomodelName = null;
//        BioModel bioModel = null;
//        Simulation[] sims = null;
//        VCDocument singleDoc = null;
//        try {
//            singleDoc = VCMLHandler.convertVcmlToVcDocument(vcmlPath);
//        } catch (Exception e) {
//            logger.error("Unable to Parse SED-ML into Bio-Model, failed with err: " + e.getMessage(), e);
//            throw e;
//        }
//        try {
//            sanityCheck(singleDoc);
//        } catch (Exception e) {
//            logger.error("Exception encountered: " + e.getMessage(), e);
//        }
//        assert singleDoc != null;
//        biomodelName = singleDoc.getName();
//        bioModel = (BioModel) singleDoc;
//        sims = bioModel.getSimulations();
//        for (Simulation sim : sims) {
//            sim = new TempSimulation(sim, false);
//            SolverTaskDescription std = sim.getSolverTaskDescription();
//            SolverDescription sd = std.getSolverDescription();
//            String kisao = sd.getKisao();
//            SimulationJob simJob = new SimulationJob(sim, 0, null);
//            SimulationTask simTask = new SimulationTask(simJob, 0);
//            Solver solver = SolverFactory.createSolver(outputDir, simTask, false);
//            ODESolverResultSet odeSolverResultSet = null;
//            try {
//                if (solver instanceof AbstractCompiledSolver) {
//                    ((AbstractCompiledSolver) solver).runSolver();
//                    if (solver instanceof ODESolver) {
//                        odeSolverResultSet = ((ODESolver) solver).getODESolverResultSet();
//                    } else if (solver instanceof GibsonSolver) {
//                        odeSolverResultSet = ((GibsonSolver) solver).getStochSolverResultSet();
//                    } else if (solver instanceof HybridSolver) {
//                        odeSolverResultSet = ((HybridSolver) solver).getHybridSolverResultSet();
//                    } else {
//                        logger.error("Solver results are not compatible with CSV format");
//                    }
//                    //TODO: Add support for JAVA solvers and implement interpolation
//
////                        odeSolverResultSet = CLIUtils.interpolate(odeSolverResultSet, (UniformTimeCourse) sedmlSim);
//
//                } else {
//                    // this should actually never happen...
//                    throw new Exception("Unexpected solver: " + kisao + " " + solver);
//                }
//                if (solver.getSolverStatus().getStatus() == SolverStatus.SOLVER_FINISHED) {
//                    logger.info("Succesful execution: Model '" + biomodelName + "' Task '" + sim.getDescription() + "'.");
//                } else {
//                    logger.debug("Solver status: " + solver.getSolverStatus().getStatus());
//                    logger.debug("Solver message: " + solver.getSolverStatus().getSimulationMessage().getDisplayMessage());
//                    throw new RuntimeException();
//                }
//
//            } catch (Exception e) {
//                logger.error("Failed execution: Model '" + biomodelName + "' Task '" + sim.getDescription() + "'.");
//                
//                if (e.getMessage() != null) {
//                    // something else than failure caught by solver instance during execution
//                    logger.error(e.getMessage(), e);
//                }
//            }
//            if(odeSolverResultSet != null) {
//                resultsHash.put(sim.getName(), odeSolverResultSet);
//            }
//
//            RunUtils.removeIntermediarySimFiles(outputDir);
//
//        }
//        return resultsHash;
    }

    ;

    // TODO: Complete this logger and use it for whole CLI
    private static class LocalLogger extends VCLogger {
        @Override
        public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException {
            logger.debug("LOGGER: msgLevel=" + p + ", msgType=" + et + ", " + message);
            if (p == VCLogger.Priority.HighPriority) {
                SBMLImportException.Category cat = SBMLImportException.Category.UNSPECIFIED;
                if (message.contains(SBMLImporter.RESERVED_SPATIAL)) {
                    cat = SBMLImportException.Category.RESERVED_SPATIAL;
                }
                throw new VCLoggerException(new SBMLImportException(message, cat));
            }
        }

        public void sendAllMessages() {
        }

        public boolean hasMessages() {
            return false;
        }
    }
    
    //
    // Running python interactively from java code
    // Proof of concept 
    //
    public static void main(String[] args) throws Exception {  
    	
    	ProcessBuilder processBuilder = new ProcessBuilder("cmd");
    	processBuilder.redirectErrorStream(true);
    	processBuilder.start();

    	/*
    	 * Interprocess communication with Python - proof of concept
    	 * Possible race conditions
    	 */
    	processBuilder.command("python","-i");
    	Process pythonProcess = processBuilder.start();
    	OutputStream outputStream = pythonProcess.getOutputStream();
    	OutputStreamWriter osw = new OutputStreamWriter(outputStream);
    	InputStream inputStream = pythonProcess.getInputStream();
    	BufferedReader bufferedReader = new BufferedReader(
    	                  new InputStreamReader(inputStream));

    	String line = "";

    	osw.write("2+2\r\n");
//    	osw.write("2+2\r\nquit()\r\n");
    	osw.flush();
//    	osw.close();
//    	InputStream inputStream = pythonProcess.getInputStream();
//    	BufferedReader bufferedReader = new BufferedReader(
//    	                  new InputStreamReader(inputStream));
//    	String line;
//    	while( (line=bufferedReader.readLine())!=null) {
//    	    System.out.println(line);
//    	}
//	    System.out.println(line);
	    
    	osw.write("3+2\r\n");
    	osw.flush();
//    	InputStream inputStream = pythonProcess.getInputStream();
//    	BufferedReader bufferedReader = new BufferedReader(
//    	                  new InputStreamReader(inputStream));

    	osw.write("4+2\r\n");
    	osw.flush();
    	
    	osw.write("5+2\r\n");
    	osw.flush();

        Thread.sleep(1000);

    	osw.write("6+2\r\n");
    	osw.write("9\r\nquit()\r\n");
    	osw.flush();
    	osw.close();

    	line = "";
    	while( (line=bufferedReader.readLine())!=null) {
    	    System.out.println(line);
    	}
    	System.out.println("done");
        	
    	
    	/*
    	
    	
        ProcessBuilder pb;
                pb = new ProcessBuilder("C:/Users/Motan/AppData/Local/Programs/Python/Python39/python.exe", "-qi", "/dev/null");

                Process p = pb.start();

    	
    	
        char[] readBuffer = new char[1000];
        InputStreamReader isr = new InputStreamReader(p.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        int charCount;
        boolean written = false;
        while(true) {
            if (!br.ready() && !written) {
                // Ugly. Should be reading for '>>>' prompt then writing.
                Thread.sleep(1000);
                if (!written) {
                    written = true;
                    OutputStream os = p.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write("2+2");
                    bw.newLine();
                    //bw.write("quit()");
                    bw.newLine();
                    bw.flush();
                    bw.close();
                }
                continue;
            }
            charCount = br.read(readBuffer);
            if (charCount > 0)
                System.out.print(new String(readBuffer, 0, charCount));
            else
                break;
        }

//        br = new BufferedReader(isr);
        written = false;
        while(true) {
            if (!br.ready() && !written) {
                // Ugly. Should be reading for '>>>' prompt then writing.
                Thread.sleep(1000);
                if (!written) {
                    written = true;
                    OutputStream os2 = p.getOutputStream();
                    OutputStreamWriter osw2 = new OutputStreamWriter(os2);
                    BufferedWriter bw2 = new BufferedWriter(osw2);
                    bw2.write("3+3");
                    bw2.newLine();
                    bw2.write("quit()");
                    bw2.newLine();
                    bw2.flush();
                    bw2.close();
                }
                continue;
            }
            charCount = br.read(readBuffer);
            if (charCount > 0)
                System.out.print(new String(readBuffer, 0, charCount));
            else
                break;
        }

*/
    }
}

