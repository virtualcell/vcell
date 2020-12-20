package org.jlibsedml.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jlibsedml.AbstractTask;
import org.jlibsedml.DataGenerator;
import org.jlibsedml.Model;
import org.jlibsedml.Output;
import org.jlibsedml.SedML;
import org.jlibsedml.Simulation;
import org.jlibsedml.Task;
import org.jlibsedml.UniformTimeCourse;
import org.jlibsedml.Variable;
import org.jlibsedml.execution.ExecutionStatusElement.ExecutionStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis of framework for executing SED-ML. If the SED-ML document has errors,
 * it is possible, but unlikely that the execution of a SED-ML task will occur
 * successfully. Therefore clients should call doc.validate() and check for
 * errors before using this class.
 * <p>
 * To use this class clients should subclass and implement the abstract methods:
 * <ul>
 * <li>IRawSedmlSimulationResults executeSimulation(String model,
 * UniformTimeCourse simConfig);
 * <li>protected abstract boolean canExecuteSimulation(final Simulation sim);
 * <li>protected abstract boolean supportsLanguage(String language);
 * </ul>
 * Subclasses should accept a SedML object and an Output object as arguments.
 * <p>
 * To run a simulation, call: <code>
 *      public final Map<AbstractTask, IRawSedmlSimulationResults> runSimulations ();
 *    </code> The returned <code>Map</code> can be used within a
 * {@link SedMLResultsProcesser2} object to obtain processed results.
 * 
 * @author radams
 *
 */
public abstract class AbstractSedmlExecutor {
    protected final SedML sedml;
    private final Output output;

    private List<ExecutionStatusElement> failureMessages = new ArrayList<ExecutionStatusElement>();
    private ModelResolver modelResolver;
    private AbstractTask task = null;

    final static String NO_SIMULATABLE_TASK_ERROR1 = "Cannot simulate task [";
    final static String NO_SIMULATABLE_TASK_ERROR2 = "]. Either the simulation reference is corrupt or the simulation algorithm is not available.";
    final static String LANGUAGE_NOT_SUPPORTED_ERROR = "The modelling language is not supported: ";

    private static final String NO_TASKS_ERROR = "No Tasks could be resolved from the required output.";

    private static Logger log = LoggerFactory
            .getLogger(AbstractSedmlExecutor.class);

    /**
     * 
     * @param model
     *            A non-null {@link SedML} model
     * @param output
     *            An {@link Output} which we want to reproduce.
     * @throws IllegalArgumentException
     *             if <code>model == null</code> or <code>output == null</code>.
     */
    public AbstractSedmlExecutor(SedML model, Output output) {
        if (model == null || output == null) {
            throw new IllegalArgumentException();
        }
        this.sedml = model;
        this.output = output;
        this.modelResolver = new ModelResolver(sedml);
        modelResolver.add(new FileModelResolver());
    }

    /**
     * Alternative constructor when no outputs are specified and we want to run
     * a single time-course and return all variables.
     * 
     * @param model
     *            A non-null {@link SedML} model
     * @param task
     *            An {@link Task} which we want to run.
     * @throws IllegalArgumentException
     *             if <code>model == null</code> or <code>output == null</code>.
     */
    public AbstractSedmlExecutor(SedML model, AbstractTask task) {
        if (model == null || task == null) {
            throw new IllegalArgumentException();
        }
        this.sedml = model;
        this.task = task;
        this.output = null;
        this.modelResolver = new ModelResolver(sedml);
        modelResolver.add(new FileModelResolver());
    }

    /**
     * Adds an {@link IModelResolver}. Subclasses or clients can add new mode
     * resolvers to acquire models from their URIs
     * 
     * @param resolver
     *            A non-null {@link IModelResolver}.
     */
    public final void addModelResolver(IModelResolver resolver) {
        modelResolver.add(resolver);
    }

    /**
     * Subclasses can add a failure item if any sub-classed methods do not work(
     * e.g, a simulation failed).
     * 
     * @param statusItem
     *            A non-null {@link ExecutionStatusElement}.
     */
    protected final void addStatus(ExecutionStatusElement statusItem) {
        failureMessages.add(statusItem);
    }

    /**
     * Main method for running simulations
     * 
     * @return A non-null Map<AbstractTask, IRawSedmlSimulationResults>
     */
    public final Map<AbstractTask, IRawSedmlSimulationResults> runSimulations() {
        failureMessages.clear();
        log.debug("Running simulations");
        Map<AbstractTask, IRawSedmlSimulationResults> res = new HashMap<AbstractTask, IRawSedmlSimulationResults>();
        Set<AbstractTask> tasksToExecute = new HashSet<AbstractTask>();
        if (task != null) {
            tasksToExecute.add(task);
        } else {
            tasksToExecute = findTasks(output);
        }
        if (tasksToExecute.isEmpty()) {
            addStatus(new ExecutionStatusElement(null, NO_TASKS_ERROR,
                    ExecutionStatusType.ERROR));
            return res;
        }
        log.debug("Got a task to execute");
        // handle no tasks

        for (AbstractTask t : tasksToExecute) {
            if (!getSimulatableTasks().contains(t)) {
                addStatus(new ExecutionStatusElement(null,
                        NO_SIMULATABLE_TASK_ERROR1 + t.getId()
                                + NO_SIMULATABLE_TASK_ERROR2,
                        ExecutionStatusType.ERROR));
                return res;
            }
        }

        for (AbstractTask task : tasksToExecute) {
            Model m = sedml.getModelWithId(task.getModelReference());
            if (!supportsLanguage(m.getLanguage())) {
                addStatus(new ExecutionStatusElement(null,
                        LANGUAGE_NOT_SUPPORTED_ERROR + m.getLanguage(),
                        ExecutionStatusType.ERROR));
                return res;

            }
            log.debug("language {} is OK", m.getLanguage());
            String changedModel = modelResolver.getModelString(m);
            log.debug("Changed modell is {}", changedModel);
            if (changedModel == null) {
                addStatus(new ExecutionStatusElement(null,
                        modelResolver.getMessage(), ExecutionStatusType.ERROR));
            }

            log.debug("Ready to execute");
            IRawSedmlSimulationResults results = executeSimulation(
                    changedModel, (UniformTimeCourse) sedml.getSimulation(task
                            .getSimulationReference()));
            if (results == null) {
                addStatus(new ExecutionStatusElement(null,
                        "Simulation failed during execution: "
                                + task.getSimulationReference() + " with model: "
                                + task.getModelReference(),
                        ExecutionStatusType.ERROR));
                // return res;
            }
            log.debug("Results are {}", results);
            res.put(task, results);
        }
        return res;
    }

    /**
     * Boolean test for the successful execution of a simulation.
     * 
     * @return <code>true</code> if the simulation was successfully executed,
     *         <code>false</code> otherwise.
     */
    public final boolean isExecuted() {
        for (ExecutionStatusElement el : failureMessages) {
            if (el.getType().equals(ExecutionStatusType.ERROR)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets an unmodifiable <code>List</code> of failure messages
     * 
     * @return A possibly empty but non-null <code>List</code> of
     *         {@link ExecutionStatusElement} objects.
     */
    public List<ExecutionStatusElement> getFailureMessages() {
        return Collections.unmodifiableList(failureMessages);
    }

    /**
     * Implementors should indicate whether or not they support the value of the
     * model's language attribute, in terms of the ability to simulate such a
     * model.
     * 
     * @param language
     *            A SED-ML supported modelling language.
     * @return <code>true</code> if subclass can support the specified language
     */
    protected abstract boolean supportsLanguage(String language);

    /**
     * Subclasses should implement this method to indicate whether they can
     * support a particular simulation type.
     * 
     * @param sim
     * @return <code>true</code> if can execute, <code>false</code> otherwise
     */
    protected abstract boolean canExecuteSimulation(final Simulation sim);

    /**
     * Implementing subclasses should run the simulation in this method. If the
     * simulation fails then this should be logged by using the method:
     * 
     * <pre>
     *  protected final  void addStatus(ExecutionStatusElement statusItem)
     * </pre>
     * 
     * and returning <code>null</code>. If execution is successful, then a
     * non-null {@link IRawSedmlSimulationResults} object should be returned.
     * 
     * @param model
     *            A String of the XML of the model to simulate.
     * @param simConfig
     *            The simulation configuration
     * @return An object which implements {@link IRawSedmlSimulationResults}
     *         which will then be processed.
     */
    protected abstract IRawSedmlSimulationResults executeSimulation(
            String model, UniformTimeCourse simConfig);

    /**
     * Gets a subset of the list of Tasks contained in the supplied model, based
     * on whether the platform can simulate them or not.
     * 
     * @return A non-null but possibly empty <code>List</code> of tasks which
     *         can be simulated. This decision is made on the basis of Kisao
     *         IDs.
     */
    public List<AbstractTask> getSimulatableTasks() {
        List<AbstractTask> rc = new ArrayList<AbstractTask>();
        for (AbstractTask task : sedml.getTasks()) {
            Simulation s = sedml.getSimulation(task.getSimulationReference());
            if (s != null && canExecuteSimulation(s)) {
                rc.add(task);
            }
        }
        return rc;
    }

    private Set<AbstractTask> findTasks(Output output) {
        Set<AbstractTask> tasksToExecute = new TreeSet<AbstractTask>();
        Set<DataGenerator> dgs = new TreeSet<DataGenerator>();
        for (String dgid : output.getAllDataGeneratorReferences()) {
            dgs.add(sedml.getDataGeneratorWithId(dgid));
        }

        for (DataGenerator dg : dgs) {
            for (Variable v : dg.getListOfVariables()) {
                tasksToExecute.add(sedml.getTaskWithId(v.getReference()));
            }
        }
        return tasksToExecute;
    }
}
