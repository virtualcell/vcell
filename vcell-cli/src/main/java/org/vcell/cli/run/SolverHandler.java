package org.vcell.cli.run;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionMathMLParser;
import cbit.vcell.solver.*;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.ode.AbstractJavaSolver;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultsSetReturnable;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.xml.ExternalDocInfo;

import org.jlibsedml.*;
import org.jlibsedml.modelsupport.SBMLSupport;

import org.jmathml.ASTNode;
import org.vcell.cli.messaging.CLIRecordable;
import org.vcell.cli.run.results.solver.SolverExecutionRequest;
import org.vcell.cli.run.results.solver.SolverNonSpatialExecutionRequest;
import org.vcell.cli.run.results.solver.SolverSpatialExecutionRequest;
import org.vcell.sbml.vcell.*;
import org.vcell.sedml.SEDMLImportException;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.trace.Span;
import org.vcell.trace.Tracer;
import org.vcell.sedml.SEDMLImporter;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Pair;
import org.vcell.util.Triplet;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.*;

public class SolverHandler {

	private final static Logger logger = LogManager.getLogger(SolverHandler.class);

	public int countBioModels = 0;        // number of biomodels in this sedml file
	public int countSuccessfulSimulationRuns = 0;    // number of simulations that we ran successfully for this sedml file

    Map<TaskJob, NonSpatialSBMLSimResults> nonSpatialResults = new LinkedHashMap<>();
    Map<TaskJob, SpatialSBMLSimResults> spatialResults = new LinkedHashMap<>();

    Map<TempSimulation, AbstractTask> tempSimulationToTaskMap = new LinkedHashMap<> ();    // key = vcell simulation, value = sedml topmost task (the imported task id)
	Map<AbstractTask, TempSimulation> taskToTempSimulationMap = new LinkedHashMap<> ();    // the opposite
	Map<Simulation, TempSimulation> origSimulationToTempSimulationMap = new LinkedHashMap<> ();    // the opposite
    Map<AbstractTask, List<AbstractTask>> taskToListOfSubTasksMap = new LinkedHashMap<AbstractTask, List<AbstractTask>> ();    // key = topmost AbstractTask, value = recursive list of subtasks
    Map<AbstractTask, List<Variable>> taskToVariableMap = new LinkedHashMap<> ();    // key = AbstractTask, value = list of variables calculated by this task
    Map<RepeatedTask, Set<String>> taskToChangeTargetMap = new LinkedHashMap<> ();    // key = RepeatedTask, value = list of the parameters that are being changed
    Map<Task, Set<RepeatedTask>> taskToChildRepeatedTasks = new LinkedHashMap<> ();    // key = Task, value = list of RepeatedTasks ending with this task
    Map<String, Task> topTaskToBaseTask = new LinkedHashMap<> ();                // key = TopmostTaskId, value = Tasks at the bottom of the SubTasks chain OR the topmost task itself if instanceof Task

    private static void sanityCheck(BioModel bioModel) throws SEDMLImportException {
        if (bioModel == null) throw new SEDMLImportException("Imported BioModel is null.");

        String docName = bioModel.getName();
        if (docName == null || docName.isEmpty()) throw new SEDMLImportException("The name of the imported BioModel is null or empty.");

		// VCell (vcml) has a different Paradigm to SedML.
		// > SedML Tasks contain SedML Simulations.
		// > VCell Applications (SimContexts) contain VCell Simulations.
		// BUT
		// > SedML Tasks are actually analogous to VCell Simulations
		if (bioModel.getNumSimulationContexts() == 0 || bioModel.getSimulationContext(0) == null) {
			throw new SEDMLImportException("VCell did not generate any VCell-Applications from `" + docName + "`; Does the SedML contain tasks?");
		}
        if (bioModel.getNumSimulations() == 0 || bioModel.getSimulation(0) == null) {
			throw new SEDMLImportException("VCell did not generate any VCell-Simulations from `" + docName + "`; No tasks in the SedML are able to run by VCell.");
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
				TempSimulation tempSimulation = new TempSimulation(sim,false);
				String importedTaskId = tempSimulation.getImportedTaskID();
				AbstractTask at = sedml.getTaskWithId(importedTaskId);
                this.tempSimulationToTaskMap.put(tempSimulation, at);
                this.taskToTempSimulationMap.put(at,  tempSimulation);
                this.origSimulationToTempSimulationMap.put(sim, tempSimulation);
				topmostTasks.add(at);    // all the tasks referred by an importedTaskId are supposed to be topmost
			}
        }
        
        {
    	// we first make a list of all the sub tasks (sub tasks themselves may be instanceof Task or another RepeatedTask)
        Set <AbstractTask> subTasks = new LinkedHashSet<> ();
        for(AbstractTask at : sedml.getTasks()) {
        	if(!(at instanceof RepeatedTask rt)) continue;
            Map<String, SubTask> subTasksOfRepeatedTask = rt.getSubTasks();
			for (Map.Entry<String, SubTask> entry : subTasksOfRepeatedTask.entrySet()) {
				String subTaskId = entry.getKey();
				AbstractTask subTask = sedml.getTaskWithId(subTaskId);
				subTasks.add(subTask);
			}
        }
        // then we make a list of all topmost tasks (Task or RepeatedTask that are not a subtask)
    	// the topmost task is the "actual" task at the end of a chain  of subtasks
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
        for (AbstractTask task : topmostTasks) {        // we have higher confidence that topmostTask is correct
			List<AbstractTask> subTasksList = new ArrayList<> ();
			AbstractTask referredTask;
			RepeatedTask rt;
			Task actualTask;
			// find the actual Task and extract the simulation
			if(task instanceof RepeatedTask repeatedTask) {
				rt = repeatedTask;
				do {
					SubTask st = rt.getSubTasks().entrySet().iterator().next().getValue(); // single subtask
					String taskId = st.getTaskId();
					referredTask = sedml.getTaskWithId(taskId);
					if (referredTask instanceof RepeatedTask repeatedReferredTask) rt = repeatedReferredTask;
					subTasksList.add(referredTask);                // last entry added will be a instanceof Task
				} while (referredTask instanceof RepeatedTask);
				actualTask = (Task)referredTask;
			} else {
				actualTask = (Task)task;
			}
            this.taskToListOfSubTasksMap.put(task, subTasksList);    // subTasksList may be empty if task instanceof Task
            this.topTaskToBaseTask.put(task.getId(), actualTask);

			Set<RepeatedTask> childRepeatedTasks = new LinkedHashSet<> ();
            this.taskToChildRepeatedTasks.put(actualTask, childRepeatedTasks);    // list of all Tasks, the set is only initialized here
        }
        for(Map.Entry<AbstractTask, List<AbstractTask>> entry : this.taskToListOfSubTasksMap.entrySet()) {    // populate the taskToChildRepeatedTasks map
			AbstractTask topmostTask = entry.getKey();
			List<AbstractTask> dependingTasks = entry.getValue();
			if(topmostTask instanceof Task) {
				// nothing to do except some sanity checks maybe
				// the taskToChildRepeatedTasks contains this key and the associated set should be empty
//        		assert dependingTasks.isEmpty() == true;							// the dependingTasks list should be empty
//        		assert taskToChildRepeatedTasks.containsKey(topmostTask) == true;	// the Task should be a key in the map
//        		assert taskToChildRepeatedTasks.get(topmostTask).isEmpty() == true;	// the set of repeated tasks associated to this task should be empty
			} else {    // this is a RepeatedTask
				// or use Task actualTask = topTaskToBaseTask.get(topmostTask.getId());
				Task actualTask = null;
				for(AbstractTask dependingTask : dependingTasks) {
					if(dependingTask instanceof Task) {        // should always be one Task at the end of the list
						actualTask = (Task)dependingTask;
						break;        // we found the only Task
					}
				}
//        		assert rootTask != null;
				Set<RepeatedTask> childRepeatedTasks = this.taskToChildRepeatedTasks.get(actualTask);
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
        Map<Variable, AbstractTask> variableToTaskMap = new LinkedHashMap<> ();        // temporary use
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
			TempSimulation tempSimulation = taskToTempSimulationMap.get(topTask);
			int scanCount = tempSimulation.getScanCount();

			if(scanCount > 1) {        // we know that topTask is a RepeatedTask
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
						Enumeration<String> overridesHashKeys = tempSimulation.getMathOverrides().getOverridesHashKeys();
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
		if (logger.isDebugEnabled()){
			logger.info("Initialization Statistics:\n\t> taskToSimulationMap: {}\n\t> taskToListOfSubTasksMap: {}\n\t> taskToVariableMap: {}\n\t> topTaskToBaseTask: {}\n",
					this.taskToTempSimulationMap.size(), this.taskToListOfSubTasksMap.size(), this.taskToVariableMap.size(), this.topTaskToBaseTask.size());
		}
    }

	private static class TempSimulationJob extends SimulationJob {

		/**
		 * Insert the method's description here.
		 * Creation date: (10/7/2005 4:50:05 PM)
		 *
		 * @param argSim
		 * @param jobIndex int
		 * @param argFDIS
		 */
		public TempSimulationJob(TempSimulation argSim, int jobIndex, FieldDataIdentifierSpec[] argFDIS) {
			super(argSim, jobIndex, argFDIS);
		}

		@Override
		public TempSimulation getSimulation() {
			return (TempSimulation)super.getSimulation();
		}

		public Simulation getOrigSimulation() {
			return getSimulation().getOriginalSimulation();
		}

		public TempSimulation getTempSimulation() {
			return getSimulation();
		}
	}

    public Map<AbstractTask, Triplet<BiosimulationLog.Status, Double, Exception>> simulateAllTasks(ExternalDocInfo externalDocInfo, SedML sedmlRequested, CLIRecordable cliLogger,
                                 File outputDirForSedml, String outDir, String sedmlLocation, boolean exactMatchOnly, boolean bSmallMeshOverride)
			throws XMLException, IOException, SEDMLImportException, ExpressionException, PropertyVetoException {
        // create the VCDocument(s) (bioModel(s) + application(s) + simulation(s)), do sanity checks
		Map<AbstractTask, Triplet<BiosimulationLog.Status, Double, Exception>> biosimStatusMap = new LinkedHashMap<>();
        cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
        String bioModelBaseName = org.vcell.util.FileUtils.getBaseName(externalDocInfo.getFile().getAbsolutePath());

        //String outDirRoot = outputDirForSedml.toString().substring(0, outputDirForSedml.toString().lastIndexOf(System.getProperty("file.separator")));
        SEDMLImporter sedmlImporter = new SEDMLImporter(sedmlImportLogger, externalDocInfo.getFile(), sedmlRequested, exactMatchOnly);
		List<BioModel> bioModelList;
		try {
			bioModelList = sedmlImporter.getBioModels();
        } catch (Exception e) {
            logger.error("Unable to Parse SED-ML into Bio-Model, failed with err: {}", e.getMessage(), e);
            throw e;
        }
		for (BioModel generatedBioModel : bioModelList) SolverHandler.sanityCheck(generatedBioModel);

        this.countBioModels = bioModelList.size();

        this.initialize(bioModelList, sedmlRequested);
        int simulationJobCount = 0;
        for (BioModel bioModel : bioModelList) {
            try (Span biomodel_span = Tracer.startSpan(Span.ContextType.BioModel, bioModel.getName(), Map.of("bioModelName", bioModel.getName()))) {
                Map<TempSimulation, BiosimulationLog.Status> vCellTempSimStatusMap = new LinkedHashMap<>();

                List<TempSimulationJob> simJobsList = this.preProcessTempSimulations(sedmlLocation, bSmallMeshOverride, bioModel, vCellTempSimStatusMap);
                for (TempSimulationJob tempSimulationJob : simJobsList) {

                    AbstractTask task = this.tempSimulationToTaskMap.get(tempSimulationJob.getTempSimulation());
                    biosimStatusMap.put(task, new Triplet<>(BiosimulationLog.Status.QUEUED, 0.0, null));
					Task baseTask = this.topTaskToBaseTask.get(task.getId());
					if (!(sedmlRequested.getSimulation(baseTask.getSimulationReference()) instanceof UniformTimeCourse utcSim)) throw new IllegalArgumentException("Sim found was not UTC.");
                    String paramScanIndex = task instanceof RepeatedTask ? ":" + tempSimulationJob.getJobIndex() : "";
                    String tempSimJobLabel = tempSimulationJob.getSimulationJobID() + tempSimulationJob.getJobIndex();
                    String logTaskMessage = String.format("Initializing SedML task %s%s (%s)...", task.getId(), paramScanIndex, tempSimJobLabel);
                    RunUtils.drawBreakLine("-  -", 25); logger.info(logTaskMessage);
                    String logTaskError = "";
                    long startTimeTask_ms = System.currentTimeMillis();

                    SimulationTask simTask;
                    String kisao;

                    Simulation tempSimulationJobSim = tempSimulationJob.getSimulation();
                    simTask = new SimulationTask(tempSimulationJob, 0);
                    SimulationOwner so = tempSimulationJobSim.getSimulationOwner();
                    tempSimulationJobSim = new TempSimulation(tempSimulationJobSim, false);
                    tempSimulationJobSim.setSimulationOwner(so);
					try (Span sim_span = Tracer.startSpan(Span.ContextType.SIMULATION_RUN, tempSimulationJobSim.getName(), Map.of("simName", tempSimulationJobSim.getName()))){
						SolverTaskDescription std = tempSimulationJobSim.getSolverTaskDescription();
						SolverDescription sd = std.getSolverDescription();
						kisao = sd.getKisao();
						if (kisao == null) throw new RuntimeException("KISAO is null.");

						// Build Solver
						SolverExecutionRequest executionRequest;
						try {
							executionRequest = SolverExecutionRequest.createNewExecutionRequest(tempSimulationJob, sd, simTask, utcSim, outputDirForSedml, sedmlLocation);
						} catch (IllegalArgumentException iae) {
							biosimStatusMap.put(task, new Triplet<>(BiosimulationLog.Status.SKIPPED, Double.NaN, iae));
							logger.warn("VCell does not support requested simulation:", iae);
							continue;
						} catch (Exception e) {
							biosimStatusMap.put(task, new Triplet<>(BiosimulationLog.Status.FAILED, Double.NaN, e));
							Tracer.failure(e, "\"Unable to build solvers\"");
							throw new RuntimeException("Unable to build solvers", e);
						}
						String solverLabel = SolverHandler.getSolverDescriptionLabel(sd, kisao);

						// Run Solver
						biosimStatusMap.put(task, new Triplet<>(BiosimulationLog.Status.RUNNING, 0.0, null));
						BiosimulationLog.instance().updateTaskStatusYml(sedmlLocation, task.getId(), BiosimulationLog.Status.RUNNING, 0, kisao);
						for (AbstractTask subTask : this.taskToListOfSubTasksMap.get(task)) {
							BiosimulationLog.instance().updateTaskStatusYml(sedmlLocation, subTask.getId(), BiosimulationLog.Status.RUNNING, 0, kisao);
						}
						Triplet<Double, Integer, Exception> solverResults = this.performSolverExecution(executionRequest, solverLabel);

						// Parse How Execution Went
						Triplet<BiosimulationLog.Status, Double, Exception> exec_result = this.determineSimulationOutcome(executionRequest, solverResults, startTimeTask_ms);
						logger.info("Results of `{}`(Model '{}' Task '{}'): {}({}s): .", tempSimulationJobSim.getDescription(), bioModel.getName(), simTask.getSimulation().getName() , exec_result.one.name(), exec_result.two);

						if (exec_result.one == BiosimulationLog.Status.SUCCEEDED) cliLogger.writeDetailedResultList(bioModelBaseName + ",  solver: " + solverLabel);
						else cliLogger.writeDetailedErrorList(solverResults.three, bioModelBaseName + ",  solver: " + solverLabel + ": " + (solverResults.three == null ? "<No Exception>" : solverResults.three.getClass().getSimpleName()) + ": " + logTaskError);
						biosimStatusMap.put(task, exec_result);
						BiosimulationLog.instance().updateTaskStatusYml(sedmlLocation, task.getId(), exec_result.one, exec_result.two, kisao);
						for (AbstractTask subTask : this.taskToListOfSubTasksMap.get(task)) {
							BiosimulationLog.instance().updateTaskStatusYml(sedmlLocation, subTask.getId(), exec_result.one, exec_result.two, kisao);
						}

						if (exec_result.one != BiosimulationLog.Status.SUCCEEDED) continue;
						// Get Solver Results
						MathSymbolMapping mathMapping = (MathSymbolMapping) simTask.getSimulation().getMathDescription().getSourceSymbolMapping();
						SBMLSymbolMapping sbmlMapping = sedmlImporter.getSBMLSymbolMapping(bioModel);
						TaskJob taskJob = new TaskJob(task.getId(), tempSimulationJob.getJobIndex());
						if (executionRequest instanceof SolverSpatialExecutionRequest sser) {
							logger.info("Collecting spatial results of execution...");
							SpatialSBMLSimResults spatialResults = sser.getResults(mathMapping, sbmlMapping);
							this.spatialResults.put(taskJob, spatialResults);
						} else if (executionRequest instanceof SolverNonSpatialExecutionRequest snser) {
							logger.info("Collecting non-spatial results of execution...");
							NonSpatialSBMLSimResults nonSpatialSimResults = snser.getResults(mathMapping, sbmlMapping);
							this.nonSpatialResults.put(taskJob, nonSpatialSimResults);
						} else {
							logger.warn("Unexpectedly reached code-block that shouldn't be possible; results may be missing!");
						}
						simulationJobCount++;
					} catch (MathException | DataAccessException e){
						Tracer.failure(e, "Unable to collect Spatial Data");
					}

                }
                RunUtils.drawBreakLine("-  -", 25);
            }
        }
        logger.info("Ran " + simulationJobCount + " simulation jobs for " + bioModelList.size() + " biomodels.");
		RunUtils.drawBreakLine("-", 100);
		return biosimStatusMap;
    }

	private Triplet<Double, Integer, Exception> performSolverExecution(SolverExecutionRequest ser, String solverDescriptionLabel) {
		try (Span sim_span = Tracer.startSpan(Span.ContextType.SIMULATION_RUN, ser.toDisplayString(), Map.of("solverName", solverDescriptionLabel))){
			Triplet<Double, Integer, Exception> solverResult;
			ser.progressGeneralLog.append("Beginning simulation...");
			CLISolverListener solverListener = new CLISolverListener(solverDescriptionLabel);
			ser.solver.addSolverListener(solverListener);
			new Thread(ser.solver::runSolver).start(); // We want to retain control on this thread.
			try {
				solverResult = solverListener.solverResult.get();
			} catch (Exception e){
				ser.progressGeneralLog.append("Failed!");
				Tracer.failure(e, "Solver \"" + solverDescriptionLabel + "\" failed!");
				return new Triplet<>(Double.NaN, SolverEvent.SOLVER_ABORTED, e);
			}
			if (!(ser.solver instanceof ODESolverResultsSetReturnable)) logger.info("Solver results will not be compatible with CSV format.");
			ser.progressGeneralLog.append(solverResult.two == SolverEvent.SOLVER_FINISHED ? "Done!" : "Failed!").append("\n");
			return solverResult;
		}
	}

	// Returns the duration in seconds, and the BioSim-style status of the solver
	private Triplet<BiosimulationLog.Status, Double, Exception> determineSimulationOutcome(SolverExecutionRequest ser, Triplet<Double, Integer, Exception> solverResult, long startTimeTask_ms){
		BiosimulationLog.Status solverStatusType;

		String displayMessage = ser.solver.getSolverStatus().getSimulationMessage().getDisplayMessage();
		String message = String.format("Solver (%s) status: `%s`(%s)", ser.solver.getClass().getSimpleName(), SolverStatus.SOLVER_STATUS[solverResult.two], displayMessage);

		int solverStatus = ser.solver.getSolverStatus().getStatus();
		long elapsedTime_ms = System.currentTimeMillis() - startTimeTask_ms;
		if (SolverStatus.SOLVER_FINISHED == solverStatus) {
			ser.progressGeneralLog.append("Simulation processed successfully ");
			this.countSuccessfulSimulationRuns++;    // we only count the number of simulations (tasks) that succeeded
			solverStatusType = BiosimulationLog.Status.SUCCEEDED;
		} else {
			ser.progressGeneralLog.append("Simulation processed unsuccessfully ");
			solverStatusType = solverStatus == SolverStatus.SOLVER_ABORTED ?
					BiosimulationLog.Status.ABORTED:
					BiosimulationLog.Status.FAILED;
		}
		ser.progressGeneralLog.append("(").append(message).append(")\n");
		ser.bioSimLogSetOutputMessage(ser.progressGeneralLog.toString());
		ser.bioSimLogSetOutputMessage(ser.progressErrLog.toString(), solverResult.three);
		return new Triplet<>(solverStatusType, ((double) elapsedTime_ms) / 1000.0, solverResult.three);
	}

	private List<TempSimulationJob> preProcessTempSimulations(String sedmlLocation, boolean bSmallMeshOverride, BioModel bioModel, Map<TempSimulation, BiosimulationLog.Status> vCellTempSimStatusMap) throws PropertyVetoException {
		List<TempSimulationJob> simJobsList = new ArrayList<>();
		for (TempSimulation tempSimulation : Arrays.stream(bioModel.getSimulations()).map(s -> this.origSimulationToTempSimulationMap.get(s)).toList()) {
			if (tempSimulation.getImportedTaskID() == null) {
				continue;    // this is a simulation not matching the imported task, so we skip it
			}

			AbstractTask task = this.tempSimulationToTaskMap.get(tempSimulation);
			vCellTempSimStatusMap.put(tempSimulation, BiosimulationLog.Status.RUNNING);
			BiosimulationLog.instance().updateTaskStatusYml(sedmlLocation, task.getId(), BiosimulationLog.Status.RUNNING,
					0.0, tempSimulation.getSolverTaskDescription().getSolverDescription().getKisao());

			if (bSmallMeshOverride && tempSimulation.getMeshSpecification() != null) {
				int maxSize = 11;
				ISize currSize = tempSimulation.getMeshSpecification().getSamplingSize();
				ISize newSize = new ISize(
						Math.min(maxSize, currSize.getX()),
						Math.min(maxSize, currSize.getY()),
						Math.min(maxSize, currSize.getZ()));
				tempSimulation.getMeshSpecification().setSamplingSize(newSize);
			}

			int scanCount = tempSimulation.getScanCount();
			for (int i = 0; i < scanCount; i++) {
				TempSimulationJob simJob = new TempSimulationJob(tempSimulation, i, null);
				simJobsList.add(simJob);
			}
		}
		return simJobsList;
	}

	/**
     * this function is unmaintained, do not run it
     */
    @Deprecated
    public HashMap<String, ODESolverResultSet> simulateAllVcmlTasks(File vcmlPath, File outputDir) throws Exception {
		throw new NotImplementedException("Not Implemented");
   }


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

	private static String getSolverDescriptionLabel(SolverDescription solverDescription, String kisao) {
		if (solverDescription == null) return kisao;
		String sdl = solverDescription.getShortDisplayLabel();
		if (sdl == null) return kisao;
		if (sdl.isEmpty()) return kisao;
        return sdl;
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

