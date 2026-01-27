package org.vcell.cli.run;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.FunctionColumnDescription;
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

import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedML;
import org.jlibsedml.components.task.AbstractTask;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.output.DataSet;
import org.jlibsedml.components.output.Output;
import org.jlibsedml.components.task.Range;
import org.jlibsedml.components.task.RepeatedTask;
import org.jlibsedml.components.output.Report;
import org.jlibsedml.SedMLDataContainer;
import org.jlibsedml.components.task.SetValue;
import org.jlibsedml.components.task.SubTask;
import org.jlibsedml.components.task.Task;
import org.jlibsedml.components.simulation.UniformTimeCourse;
import org.jlibsedml.components.Variable;
import org.jlibsedml.XPathTarget;
import org.jlibsedml.XMLException;
import org.jlibsedml.modelsupport.SBMLSupport;

import org.jmathml.ASTNode;
import org.vcell.cli.messaging.CLIRecordable;
import org.vcell.sbml.vcell.*;
import org.vcell.sedml.SEDMLImportException;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.trace.Span;
import org.vcell.trace.Tracer;
import org.vcell.sedml.SedMLImporter;
import org.vcell.util.ISize;
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Pair;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.*;

public class SolverHandler {

	private final static Logger logger = LogManager.getLogger(SolverHandler.class);

	public int countBioModels = 0;        // number of biomodels in this sedml file
	public int countSuccessfulSimulationRuns = 0;    // number of simulations that we ran successfully for this sedml file

    // Initialization Vars
    private String modelReportingName;
    private SedMLDataContainer initializedSedMLContainer;
    private Map<BioModel, SBMLSymbolMapping> bioModelToSBMLMapping;
    // // // //
    Map<TaskJob, NonSpatialSBMLSimResults> nonSpatialResults = new LinkedHashMap<>();
    Map<TaskJob, SpatialSBMLSimResults> spatialResults = new LinkedHashMap<>();

    Map<TempSimulation, AbstractTask> tempSimulationToTaskMap = new LinkedHashMap<> ();    // key = vcell simulation, value = sedml topmost task (the imported task id)
	Map<AbstractTask, TempSimulation> taskToTempSimulationMap = new LinkedHashMap<> ();    // the opposite
	Map<Simulation, TempSimulation> origSimulationToTempSimulationMap = new LinkedHashMap<> ();    // the opposite
    Map<AbstractTask, List<AbstractTask>> taskToListOfSubTasksMap = new LinkedHashMap<> ();    // key = topmost AbstractTask, value = recursive list of subtasks
    Map<AbstractTask, List<Variable>> taskToVariableMap = new LinkedHashMap<> ();    // key = AbstractTask, value = list of variables calculated by this task
    Map<RepeatedTask, Set<String>> taskToChangeTargetMap = new LinkedHashMap<> ();    // key = RepeatedTask, value = list of the parameters that are being changed
    Map<SId, Task> topTaskToBaseTask = new LinkedHashMap<> ();                // key = TopmostTaskId, value = Tasks at the bottom of the SubTasks chain OR the topmost task itself if instanceof Task

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

    public Pair<SedMLDataContainer, Map<BioModel, SBMLSymbolMapping>> initialize(ExternalDocInfo externalDocInfo, SedMLDataContainer providedSedmlContainer, boolean disallowModifiedImport)
            throws ExpressionException, SEDMLImportException {
        cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();

        //String outDirRoot = outputDirForSedml.toString().substring(0, outputDirForSedml.toString().lastIndexOf(System.getProperty("file.separator")));
        SedMLDataContainer actionableSedmlContainer;
        Map<BioModel, SBMLSymbolMapping> bioModelMapping;
        SedMLImporter sedmlImporter = new SedMLImporter(sedmlImportLogger, disallowModifiedImport);
        this.modelReportingName  = org.vcell.util.FileUtils.getBaseName(externalDocInfo.getFile().getAbsolutePath());

        try {
            actionableSedmlContainer = sedmlImporter.initialize(externalDocInfo.getFile(), providedSedmlContainer);
        } catch (Exception e) {
            String errMessage = "Unable to prepare SED-ML for conversion into BioModel(s)";
            String formattedError = String.format("%s, failed with error: %s", errMessage, e.getMessage());
            logger.error(formattedError);
            throw new SEDMLImportException(e);
        }

        try {
            bioModelMapping = sedmlImporter.getBioModels();
        } catch (Exception e) {
            logger.error("Unable to Parse SED-ML into Bio-Model, failed with err: {}", e.getMessage(), e);
            throw e;
        }
        for (BioModel generatedBioModel : bioModelMapping.keySet()) SolverHandler.sanityCheck(generatedBioModel);

        this.countBioModels = bioModelMapping.size();

        SedML sedML = providedSedmlContainer.getSedML();
        Set <AbstractTask> topmostTasks = new LinkedHashSet<> ();
        for(BioModel bioModel : bioModelMapping.keySet()) {
			Simulation[] sims = bioModel.getSimulations();
			for(Simulation sim : sims) {
				if(sim.getImportedTaskID() == null) {
					continue;
				}
				TempSimulation tempSimulation = new TempSimulation(sim,false);
				String importedTaskId = tempSimulation.getImportedTaskID();
                SedBase foundElement = sedML.searchInTasksFor(new SId(importedTaskId));
                if (!(foundElement instanceof AbstractTask abstractTask)) throw new RuntimeException("Imported task id " + importedTaskId + " is not an AbstractTask.");
				this.tempSimulationToTaskMap.put(tempSimulation, abstractTask);
				this.taskToTempSimulationMap.put(abstractTask,  tempSimulation);
				this.origSimulationToTempSimulationMap.put(sim, tempSimulation);
				topmostTasks.add(abstractTask);    // all the tasks referred by an importedTaskId are supposed to be topmost
			}
        }
        
        { // sub scope to keep names limited
            // we first make a list of all the subtasks (subtasks themselves may be instanceof Task or another RepeatedTask)
            Set <AbstractTask> subTasks = new LinkedHashSet<> ();
            for(AbstractTask at : sedML.getTasks()) {
                if(!(at instanceof RepeatedTask rt)) continue;
                for (SubTask entry : rt.getSubTasks()) {
                    SedBase foundElement = sedML.searchInTasksFor(entry.getTask());
                    if (!(foundElement instanceof AbstractTask subTaskTarget)) throw new RuntimeException("Subtask (id=" + entry.getId().string() + " ) does not reference an AbstractTask.");
                    subTasks.add(subTaskTarget);
                }
            }
            // then we make a list of all topmost tasks (Task or RepeatedTask that are not a subtask)
            // the topmost task is the "actual" task at the end of a chain  of subtasks
            Set <AbstractTask> topmostTasks2 = new LinkedHashSet<> ();	// topmost tasks, different way to calculate (they are not in the list of subtasks above)
            for (AbstractTask at : sedML.getTasks()) {
                if(!subTasks.contains(at)) {
                    topmostTasks2.add(at);
                }
            }
            if(topmostTasks.size() != topmostTasks2.size()) {
                logger.error("TopmostTasks lists sizes are different.");
    //        	throw new RuntimeException("TopmostTasks lists sizes are different.");
            }
            for (AbstractTask abstractTask : topmostTasks) {        // we have higher confidence that topmostTask is correct
                List<AbstractTask> subTasksList = new ArrayList<> ();
                Task baseTask;
                if(abstractTask instanceof RepeatedTask repeatedTask) {
                    subTasksList.addAll(providedSedmlContainer.getActualSubTasks(repeatedTask.getId()));
                    baseTask = providedSedmlContainer.getBaseTask(repeatedTask.getId());
                    if (baseTask == null) throw new RuntimeException("Unable to find base task of repeated task: " + repeatedTask.getId().string() + ".");
                } else if (abstractTask instanceof Task task) {
                    baseTask = task;
                } else {
                    throw new RuntimeException(String.format("Task (id=%s) has unknown type: %s.", abstractTask.getId().string(), abstractTask.getClass().getName()));
                }

                this.taskToListOfSubTasksMap.put(abstractTask, subTasksList);    // subTasksList may be empty if task instanceof Task
                this.topTaskToBaseTask.put(abstractTask.getId(), baseTask);
            }
        } // End of sub scope to keep names limited
        
        {
            //
            // key = tasks that are used for generating some output
            //
            Map<Variable, AbstractTask> variableToTaskMap = new LinkedHashMap<> ();        // temporary use
            for(Output oo : sedML.getOutputs()) {
                if(oo instanceof Report rep) {
                    // TODO: check if multiple reports may use different tasks for the same variable
                    // here we assume that each variable may only be the result of one task
                    // the variable id we produce in vcell is definitely correct since the
                    // variable id is constructed based on the task id
                    List<DataSet> datasets = rep.getDataSets();
                    for (DataSet dataset : datasets) {
                        SedBase foundDataGen = sedML.searchInDataGeneratorsFor(dataset.getDataReference());
                        if (!(foundDataGen instanceof DataGenerator dataGen)) throw new IllegalArgumentException("Unable to find data generator referenced in dataset: " + dataset.getDataReference());
                        for(Variable var : dataGen.getVariables()) {
                            SedBase foundAbstractTask = sedML.searchInTasksFor(var.getTaskReference());
                            if (!(foundAbstractTask instanceof AbstractTask task)) throw new IllegalArgumentException("Unable to find task referenced by variable: " + var.getTaskReference());
                            variableToTaskMap.put(var, task);
                        }
                    }
                }
            }
            for (Map.Entry<Variable, AbstractTask> entry : variableToTaskMap.entrySet()) {
                Variable var = entry.getKey();
                AbstractTask task = entry.getValue();
                List<Variable> vars = this.taskToVariableMap.containsKey(task) ? this.taskToVariableMap.get(task) : new ArrayList<>();
                vars.add(var);
                this.taskToVariableMap.put(task, vars);
            }
        }
        
        for (AbstractTask topTask : this.taskToListOfSubTasksMap.keySet()) {
            List<AbstractTask> subTasks = this.taskToListOfSubTasksMap.get(topTask);
			Task baseTask = this.topTaskToBaseTask.get(topTask.getId());
			TempSimulation tempSimulation = this.taskToTempSimulationMap.get(topTask);
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
					if (exp.infix().equals(range.getId().string())) {
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
                this.taskToChangeTargetMap.put(rt, targetIdSet);
			}
        }

		if (logger.isDebugEnabled()){
			logger.info("Initialization Statistics:\n\t> taskToSimulationMap: {}\n\t> taskToListOfSubTasksMap: {}\n\t> taskToVariableMap: {}\n\t> topTaskToBaseTask: {}\n",
					this.taskToTempSimulationMap.size(), this.taskToListOfSubTasksMap.size(), this.taskToVariableMap.size(), this.topTaskToBaseTask.size());
		}
        return new Pair<>(this.initializedSedMLContainer = actionableSedmlContainer, this.bioModelToSBMLMapping = bioModelMapping);
    }

	private static class TempSimulationJob extends SimulationJob {

		/**
		 * Insert the method's description here.
		 * Creation date: (10/7/2005 4:50:05 PM)
		 *
		 * @param sim the {@link TempSimulation} this job refers to
		 * @param jobIndex int for parameters scans, what the job index is
		 * @param fdis field data identification specifications
		 */
		public TempSimulationJob(TempSimulation sim, int jobIndex, FieldDataIdentifierSpec[] fdis) {
			super(sim, jobIndex, fdis);
		}

		@Override
		public TempSimulation getSimulation() {
			return (TempSimulation)super.getSimulation();
		}

		public Simulation getOrigSimulation() {
			return this.getSimulation().getOriginalSimulation();
		}

		public TempSimulation getTempSimulation() {
			return this.getSimulation();
		}
	}

    public Map<AbstractTask, BiosimulationLog.Status> simulateAllTasks(CLIRecordable cliLogger, File outputDirForSedml, String sedmlLocation, boolean keepTempFiles, boolean bSmallMeshOverride)
			throws IOException, PropertyVetoException {
        // Input state validation
        if (this.initializedSedMLContainer == null) throw new IllegalStateException("Importer has not yet been initialized!");
        if (this.bioModelToSBMLMapping == null) throw new IllegalStateException("Importer has not yet been initialized!");
        if (this.bioModelToSBMLMapping.isEmpty()) throw new IllegalStateException("Importer failed to create biomodels for initialized SedML!");
        // create the VCDocument(s) (bioModel(s) + application(s) + simulation(s)), do sanity checks
		Map<AbstractTask, BiosimulationLog.Status> biosimStatusMap = new LinkedHashMap<>();

        //String outDirRoot = outputDirForSedml.toString().substring(0, outputDirForSedml.toString().lastIndexOf(System.getProperty("file.separator")));

        int simulationJobCount = 0;
        int bioModelCount = 0;
        boolean hasSomeSpatial = false;
        boolean bTimeoutFound = false;
        for (BioModel bioModel : this.bioModelToSBMLMapping.keySet()) {
			Span biomodel_span = null;
			try {
				biomodel_span = Tracer.startSpan(Span.ContextType.BioModel, bioModel.getName(), Map.of("bioModelName", bioModel.getName()));

				Map<TempSimulation, BiosimulationLog.Status> vCellTempSimStatusMap = new LinkedHashMap<>();
				Map<TempSimulation, Integer> simDurationMap_ms = new LinkedHashMap<>();

				List<TempSimulationJob> simJobsList = this.preProcessTempSimulations(sedmlLocation, bSmallMeshOverride, bioModel, vCellTempSimStatusMap, simDurationMap_ms);
				for (TempSimulationJob tempSimulationJob : simJobsList) {
					AbstractTask task = this.tempSimulationToTaskMap.get(tempSimulationJob.getTempSimulation());
					biosimStatusMap.put(task, BiosimulationLog.Status.QUEUED);
					String paramScanIndex = task instanceof RepeatedTask ? ":" + tempSimulationJob.getJobIndex() : "";
					String tempSimJobLabel = tempSimulationJob.getSimulationJobID() + tempSimulationJob.getJobIndex();
					String logTaskMessage = String.format("Initializing simulation job %s (%s%s)...", tempSimJobLabel, task.getId(), paramScanIndex);
					RunUtils.drawBreakLine("-  -", 25);
					logger.info(logTaskMessage);
					String logTaskError = "";
					long startTimeTask_ms = System.currentTimeMillis();

					SimulationTask simTask;
					String kisao = "null";
					ODESolverResultSet odeSolverResultSet = null;
					SolverDescription sd = null;
					int solverStatus = SolverStatus.SOLVER_READY;

					Simulation tempSimulationJobSim = tempSimulationJob.getSimulation();
					simTask = new SimulationTask(tempSimulationJob, 0);
					Span sim_span = null;
					try {
						sim_span = Tracer.startSpan(Span.ContextType.SIMULATION_RUN, tempSimulationJobSim.getName(), Map.of("simName", tempSimulationJobSim.getName()));
						SimulationOwner so = tempSimulationJobSim.getSimulationOwner();
						tempSimulationJobSim = new TempSimulation(tempSimulationJobSim, false);
						tempSimulationJobSim.setSimulationOwner(so);

						SolverTaskDescription std = tempSimulationJobSim.getSolverTaskDescription();
						sd = std.getSolverDescription();
						kisao = sd.getKisao();
						if (kisao == null) throw new RuntimeException("KISAO is null.");


						Solver solver = SolverFactory.createSolver(outputDirForSedml, simTask, false);
						logTaskMessage += "done. Starting simulation... ";

						if (sd.isSpatial()) hasSomeSpatial = true;

						biosimStatusMap.put(task, BiosimulationLog.Status.RUNNING);
						RunUtils.drawBreakLine("-  -", 25);
						logger.info("Beginning Simulation...");
						if (solver instanceof AbstractCompiledSolver abstractCompiledSolver) {
							abstractCompiledSolver.runSolver();
							if (solver instanceof ODESolver odeSolver) {
								odeSolverResultSet = odeSolver.getODESolverResultSet();
							} else if (solver instanceof GibsonSolver gibsonSolver) {
								odeSolverResultSet = gibsonSolver.getStochSolverResultSet();
							} else if (solver instanceof HybridSolver hybridSolver) {
								odeSolverResultSet = hybridSolver.getHybridSolverResultSet();
							} else {
								String str = "Solver results will not be compatible with CSV format. ";
								logger.warn(str);
	//                            keepTempFiles = true;		// temp fix for Jasraj
	//                        	throw new RuntimeException(str);
							}
						} else if (solver instanceof AbstractJavaSolver abstractJavaSolver) {
							abstractJavaSolver.runSolver();
							if (SolverStatus.SOLVER_FINISHED == abstractJavaSolver.getSolverStatus().getStatus()){
								odeSolverResultSet = ((ODESolver) solver).getODESolverResultSet();
								// must interpolate data for uniform time course which is not supported natively by the Java solvers
                                Task baseTask = this.initializedSedMLContainer.getBaseTask(task.getId());
                                if (baseTask == null) throw new RuntimeException("Unable to find base task");
                                SedBase elementFound = this.initializedSedMLContainer.getSedML().searchInSimulationsFor(baseTask.getSimulationReference());
                                if (!(elementFound instanceof org.jlibsedml.components.simulation.Simulation sedmlSim))
                                    throw new RuntimeException("Unable to find simulation for base task");
								if (sedmlSim instanceof UniformTimeCourse utcSedmlSim) {
									odeSolverResultSet = RunUtils.interpolate(odeSolverResultSet, utcSedmlSim);
									logTaskMessage += "done. Interpolating... ";
								}
							}
						} else {
							// this should actually never happen...
							String str = "Unexpected solver: " + kisao + " " + solver + ". ";
							biosimStatusMap.put(task, BiosimulationLog.Status.SKIPPED);
							throw new RuntimeException(str);
						}

						if (odeSolverResultSet != null) {
							// add output functions, if any, to result set
							List<AnnotatedFunction> funcs = so.getOutputFunctionContext().getOutputFunctionsList();
							if (funcs != null) {
								for (AnnotatedFunction function : funcs) {
									FunctionColumnDescription fcd = null;
									String funcName = function.getName();
									Expression funcExp = function.getExpression();
									fcd = new FunctionColumnDescription(funcExp, funcName, null, function.getDisplayName(), true);
									odeSolverResultSet.checkFunctionValidity(fcd);
									odeSolverResultSet.addFunctionColumn(fcd);
								}
							}
						}

						if (solver.getSolverStatus().getStatus() == SolverStatus.SOLVER_FINISHED) {

							logTaskMessage += "done. ";
							long endTimeTask_ms = System.currentTimeMillis();
							long elapsedTime_ms = endTimeTask_ms - startTimeTask_ms;
							int duration_ms = (int) elapsedTime_ms;

							TempSimulation originalSim = tempSimulationJob.getSimulation();
							int simDuration_ms = simDurationMap_ms.get(originalSim);
							simDuration_ms += duration_ms;
							simDurationMap_ms.put(originalSim, simDuration_ms);

                            logger.info("Successful execution ({}s): Model '{}' Task '{}' ({}).",
									((double)elapsedTime_ms)/1000, bioModel.getName(), tempSimulationJobSim.getDescription(), simTask.getSimulation().getName());
                            this.countSuccessfulSimulationRuns++;    // we only count the number of simulations (tasks) that succeeded
							if (vCellTempSimStatusMap.get(originalSim) != BiosimulationLog.Status.ABORTED && vCellTempSimStatusMap.get(originalSim) != BiosimulationLog.Status.FAILED) {
								vCellTempSimStatusMap.put(originalSim, BiosimulationLog.Status.SUCCEEDED);
							}
							BiosimulationLog.instance().setOutputMessage(sedmlLocation, tempSimulationJobSim.getImportedTaskID(), "task", logTaskMessage);
							biosimStatusMap.put(task, BiosimulationLog.Status.SUCCEEDED);
						} else {
							String error = solver.getSolverStatus().getSimulationMessage().getDisplayMessage();
							solverStatus = solver.getSolverStatus().getStatus();
							String message = String.format("Solver (%s) status: `%s` (%s) ", solver.getClass().getSimpleName(), solver.getSolverStatus().getStatusAsString(), error);
							biosimStatusMap.put(task, BiosimulationLog.Status.FAILED);
							throw new RuntimeException(message);
						}
					} catch (Exception e) {
						long endTime_ms = System.currentTimeMillis();
						long elapsedTime_ms = endTime_ms - startTimeTask_ms;
						String error = String.format("Failed execution:\n\t> Elapsed Time:\t%dms\n\t> Model:\t\t%s\n\t> Task:\t\t\t%s\n\t> Cause:\t\t%s\n\t> Message:\t\t%s", elapsedTime_ms, bioModel.getName(), tempSimulationJobSim.getDescription(), e.getClass().getSimpleName(), e.getMessage());
						logger.error(error);
						Tracer.failure(e, error);

						if (tempSimulationJobSim.getImportedTaskID() == null) {
							String str = "'null' imported task id, this should never happen. ";
							logger.error(str);
							logTaskError += str;
						} else {
							TempSimulation originalSim = tempSimulationJob.getSimulation();
							if (solverStatus == SolverStatus.SOLVER_ABORTED) {
								vCellTempSimStatusMap.put(originalSim, BiosimulationLog.Status.ABORTED);
							} else {
								vCellTempSimStatusMap.put(originalSim, BiosimulationLog.Status.FAILED);
							}
						}
	//                    CLIUtils.finalStatusUpdate(CLIUtils.Status.FAILED, outDir);
						logTaskError += (e.getMessage() != null ? e.getMessage() : error) + ". ";
						String type = e.getClass().getSimpleName();
						BiosimulationLog.instance().setOutputMessage(sedmlLocation, tempSimulationJobSim.getImportedTaskID(), "task", logTaskMessage);
						BiosimulationLog.instance().setExceptionMessage(sedmlLocation, tempSimulationJobSim.getImportedTaskID(), "task", type, logTaskError);
						String sdl = "";
						if (sd != null && sd.getShortDisplayLabel() != null && !sd.getShortDisplayLabel().isEmpty()) {
							sdl = sd.getShortDisplayLabel();
						} else {
							sdl = kisao;
						}
						if (logTaskError.contains("Process timed out")) {
							if (!bTimeoutFound) {        // don't repeat this for each task
								String str = logTaskError.substring(0, logTaskError.indexOf("Process timed out"));
								str += "Process timed out";        // truncate the rest of the spam
								cliLogger.writeDetailedErrorList(e, this.modelReportingName + ",  solver: " + sdl + ": " + type + ": " + str);
								bTimeoutFound = true;
							}
						} else {
							cliLogger.writeDetailedErrorList(e,this.modelReportingName + ",  solver: " + sdl + ": " + type + ": " + logTaskError);
						}
					} finally {
						if (sim_span != null) {
							sim_span.close();
						}
					}

					MathSymbolMapping mathMapping = (MathSymbolMapping) simTask.getSimulation().getMathDescription().getSourceSymbolMapping();
					SBMLSymbolMapping sbmlMapping = this.bioModelToSBMLMapping.get(bioModel);
					TaskJob taskJob = new TaskJob(task.getId(), tempSimulationJob.getJobIndex());
					if (sd.isSpatial()) {
						logger.info("Processing spatial results of execution...");
						SpatialSBMLSimResults spatialResults = new SpatialSBMLSimResults(tempSimulationJob, outputDirForSedml, sbmlMapping, mathMapping);
						this.spatialResults.put(taskJob, spatialResults);
						keepTempFiles = true;
					} else {
						logger.info("Processing non-spatial results of execution...");
						NonSpatialSBMLSimResults nonspatialSimResults = new NonSpatialSBMLSimResults(odeSolverResultSet, sbmlMapping, mathMapping);
						this.nonSpatialResults.put(taskJob, nonspatialSimResults);
					}

					if (!keepTempFiles) {
						RunUtils.removeIntermediarySimFiles(outputDirForSedml);
					}
					simulationJobCount++;
				}
				for (Map.Entry<TempSimulation, BiosimulationLog.Status> entry : vCellTempSimStatusMap.entrySet()) {

					TempSimulation tempSimulation = entry.getKey();
					BiosimulationLog.Status status = entry.getValue();
					if (status == BiosimulationLog.Status.RUNNING) {
						continue;    // if this happens somehow, we just don't write anything
					}
					AbstractTask task = this.tempSimulationToTaskMap.get(tempSimulation);
	//	        	assert task != null;
					double duration_s = simDurationMap_ms.get(tempSimulation)/1000.0;
					SolverTaskDescription std = tempSimulation.getSolverTaskDescription();
					SolverDescription sd = std.getSolverDescription();
					String kisao = sd.getKisao();
					BiosimulationLog.instance().updateTaskStatusYml(sedmlLocation, task.getId().string(), status, duration_s, kisao);

					List<AbstractTask> children = this.taskToListOfSubTasksMap.get(task);
					for (AbstractTask rt : children) {
						BiosimulationLog.instance().updateTaskStatusYml(sedmlLocation, rt.getId().string(), status, duration_s, kisao);
					}
				}
				bioModelCount++;
				RunUtils.drawBreakLine("-  -", 25);
			} finally {
				if (biomodel_span != null){
					biomodel_span.close();
				}
			}
        }
        logger.info("Ran " + simulationJobCount + " simulation jobs for " + bioModelCount + " biomodels.");
        if(hasSomeSpatial) {
            cliLogger.writeSpatialList(this.modelReportingName);
        }
		RunUtils.drawBreakLine("-", 100);
		return biosimStatusMap;
    }

	private List<TempSimulationJob> preProcessTempSimulations(String sedmlLocation, boolean bSmallMeshOverride, BioModel bioModel, Map<TempSimulation, BiosimulationLog.Status> vCellTempSimStatusMap, Map<TempSimulation, Integer> simDurationMap_ms) throws PropertyVetoException {
		List<TempSimulationJob> simJobsList = new ArrayList<>();
		for (TempSimulation tempSimulation : Arrays.stream(bioModel.getSimulations()).map(s -> this.origSimulationToTempSimulationMap.get(s)).toList()) {
			if (tempSimulation.getImportedTaskID() == null) {
				continue;    // this is a simulation not matching the imported task, so we skip it
			}

			AbstractTask task = tempSimulationToTaskMap.get(tempSimulation);
			vCellTempSimStatusMap.put(tempSimulation, BiosimulationLog.Status.RUNNING);
			BiosimulationLog.instance().updateTaskStatusYml(sedmlLocation, task.getId().string(), BiosimulationLog.Status.RUNNING,
					0.0, tempSimulation.getSolverTaskDescription().getSolverDescription().getKisao());
			simDurationMap_ms.put(tempSimulation, 0);

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

        public void sendAllMessages() {}

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

