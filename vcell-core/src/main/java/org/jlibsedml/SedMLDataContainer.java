package org.jlibsedml;

import java.text.MessageFormat;
import java.util.*;

import org.jdom2.Namespace;
import org.jlibsedml.components.*;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.components.output.*;
import org.jlibsedml.components.simulation.OneStep;
import org.jlibsedml.components.simulation.Simulation;
import org.jlibsedml.components.simulation.UniformTimeCourse;
import org.jlibsedml.components.task.AbstractTask;
import org.jlibsedml.components.task.RepeatedTask;
import org.jlibsedml.components.task.SubTask;
import org.jlibsedml.components.task.Task;

/**
 * This class serves a bridge between the implementations of SedML classes / components, and all the utility work
 * and surrounding XML needed to make a reproducible description.
 */
public final class SedMLDataContainer {

    // added for import
    private String pathForURI;
    private String fileName;
    private final Map<String, Namespace> xmlPrefixToNamespaceMap;
    private final SedML sedml;

    private static List<Namespace> getDefaultNamespaces(int sedmlLevel, int sedmlVersion){
        String sedmlURI = String.format("http://sed-ml.org/sed-ml/level%d/version%d", sedmlLevel, sedmlVersion);
        return Arrays.asList(
                Namespace.getNamespace(sedmlURI),
                Namespace.getNamespace("math", "http://www.w3.org/1998/Math/MathML"),
                Namespace.getNamespace("sbml", "http://www.sbml.org/sbml/level3/version2/core")
        );
    }

    SedMLDataContainer(SedML sedml) {
        if (sedml.getLevel() != 1) {
            String message = MessageFormat.format("Invalid level {0}, valid level is {1}", sedml.getLevel(), "1");
            throw new IllegalArgumentException(message);
        }
        if (sedml.getVersion() < 1 || sedml.getVersion() > 5) {
            String message = MessageFormat.format("Invalid version {0}, valid versions are {1}", sedml.getVersion(), "1,2,3,4,5");
            throw new IllegalArgumentException(message);
        }
        this.sedml = sedml;
        this.xmlPrefixToNamespaceMap = new HashMap<>();
    }

    public SedMLDataContainer(SedMLDataContainer containerToCopy, boolean deepCopySedml) throws CloneNotSupportedException {
        this.sedml = deepCopySedml ? containerToCopy.sedml.clone() : containerToCopy.sedml;
        this.pathForURI = containerToCopy.pathForURI;
        this.fileName = containerToCopy.fileName;
        this.xmlPrefixToNamespaceMap = new HashMap<>(containerToCopy.xmlPrefixToNamespaceMap);
    }

    public SedMLDataContainer(SedMLDataContainer containerToCopy, SedML sedMLToReplaceWith){
        this.sedml = sedMLToReplaceWith;
        this.pathForURI = containerToCopy.pathForURI;
        this.fileName = containerToCopy.fileName;
        this.xmlPrefixToNamespaceMap = new HashMap<>(containerToCopy.xmlPrefixToNamespaceMap);
    }

    public SedML getSedML() {
        return this.sedml;
    }

    /**
     * Removes any dangling references from the internal SedML
     */
    public void pruneSedML(){
        this.pruneSedMLTasks();
        this.pruneSedMLDataGenerators();
        this.pruneSedMLOutputs();
    }

    private void pruneSedMLTasks(){
        // Step 0; separate into base and repeated tasks
        List<Task> baseTasks = new ArrayList<>();
        List<RepeatedTask> repeatedTasks = new ArrayList<>();
        for (AbstractTask abTask: this.sedml.getTasks()){
            if (abTask instanceof RepeatedTask repTask) repeatedTasks.add(repTask);
            if (abTask instanceof Task task) baseTasks.add(task);
        }
        // Step 1: prune base tasks
        for (Task task: baseTasks){
            Model possibleModel = this.findModelById(task.getModelReference());
            if (null == possibleModel) this.sedml.getListOfTasks().removeContent(task);
            Simulation possibleSim = this.findSimulationById(task.getSimulationReference());
            if (null == possibleSim) this.sedml.getListOfTasks().removeContent(task);
        }

        // Step 2: prune repeated tasks
        int currentNumTask;
        do {
            currentNumTask = this.sedml.getListOfTasks().size();
            for (RepeatedTask repTask: repeatedTasks){
                if (!this.sedml.getListOfTasks().containsContent(repTask.getId())) continue;
                for (SubTask subTask: repTask.getSubTasks()){
                    AbstractTask possibleReferredToTask = this.findAbstractTaskById(subTask.getTask());
                    if (null == possibleReferredToTask) this.sedml.getListOfTasks().removeContent(repTask);
                }
            }
        } while (currentNumTask != this.sedml.getListOfTasks().size()); // There may be nested repeated tasks to prune
    }

    private void pruneSedMLDataGenerators(){
        for (DataGenerator generator: new ArrayList<>(this.sedml.getDataGenerators())) {
            for (Variable var: generator.getVariables()) {
                Model possibleModel = this.findModelById(var.getModelReference());
                AbstractTask possibleTask =  this.findAbstractTaskById(var.getTaskReference());
                if (null == possibleModel && null == possibleTask) this.sedml.getListOfDataGenerators().removeContent(generator);
            }
        }
    }

    private void pruneSedMLOutputs(){
        for (Output output: new ArrayList<>(this.sedml.getOutputs())) {
            if (output instanceof Report report) {
                for (DataSet dataSet: report.getDataSets()) {
                    SedBase dataGen = this.sedml.searchInDataGeneratorsFor(dataSet.getDataReference());
                    if (dataGen instanceof DataGenerator) continue;
                    this.sedml.getListOfOutputs().removeContent(output);
                    break;
                }
            } else if (output instanceof Plot2D plot) {
                for (AbstractCurve abCurve: plot.getCurves()) {
                    SedBase dataGenX = this.sedml.searchInDataGeneratorsFor(abCurve.getXDataReference());
                    if (!(dataGenX instanceof DataGenerator)){ this.sedml.getListOfOutputs().removeContent(output); break; }
                    if (!(abCurve instanceof Curve curve)) continue;
                    SedBase dataGenY = this.sedml.searchInDataGeneratorsFor(curve.getYDataReference());
                    if (dataGenY instanceof DataGenerator) continue;
                    this.sedml.getListOfOutputs().removeContent(output);
                    break;
                }
            } else if (output instanceof Plot3D plot) {
                for (Surface surface: plot.getSurfaces()) {
                    SedBase dataGenX = this.sedml.searchInDataGeneratorsFor(surface.getXDataReference());
                    if (!(dataGenX instanceof DataGenerator)){ this.sedml.getListOfOutputs().removeContent(output); break; }
                    SedBase dataGenY = this.sedml.searchInDataGeneratorsFor(surface.getYDataReference());
                    if (!(dataGenY instanceof DataGenerator)){ this.sedml.getListOfOutputs().removeContent(output); break; }
                    SedBase dataGenZ = this.sedml.searchInDataGeneratorsFor(surface.getZDataReference());
                    if (dataGenZ instanceof DataGenerator) continue;
                    this.sedml.getListOfOutputs().removeContent(output);
                    break;
                }
            }
        }
    }

    /**
     * Sets additional namespaces on SedDocument
     */
    public void addAllAdditionalNamespaces(List<Namespace> additionalNamespaces) {
        for (Namespace namespace : additionalNamespaces) {
            if (namespace.getPrefix() == null || namespace.getPrefix().isEmpty()) continue;
            this.xmlPrefixToNamespaceMap.put(namespace.getPrefix(), namespace);
        }
    }

    /**
     * Fetches the namespaces associated with this <code>SedML</code> object
     * @return an unmodifiable {@link List} of {@link Namespace}
     */
    public List<Namespace> getAllNamespaces() {
        return this.xmlPrefixToNamespaceMap.keySet().stream().map(this.xmlPrefixToNamespaceMap::get).toList();
    }

    /**
     * Fetches the namespaces associated with this <code>SedML</code> object
     * @return an unmodifiable {@link List} of {@link Namespace}
     */
    public List<Namespace> getExtraNamespaces() {
        Set<String> extraNamespaces = new HashSet<>(this.xmlPrefixToNamespaceMap.keySet());
        extraNamespaces.remove("");
        return extraNamespaces.stream().map(this.xmlPrefixToNamespaceMap::get).toList();
    }

    public void addExtraNamespace(Namespace namespace) {
        String prefix = namespace.getPrefix();
        if (prefix == null) prefix = "";
        if (this.xmlPrefixToNamespaceMap.containsKey(prefix))
            throw new IllegalStateException(String.format("Namespace already exists for prefix %s", prefix.isEmpty() ? "<no_prefix>": "\"" + prefix + "\"" ));
        this.xmlPrefixToNamespaceMap.put(prefix, namespace);
    }

    public void removeExtraNamespace(Namespace namespace) {
        String prefix = namespace.getPrefix();
        if (prefix == null) prefix = "";
        if (!this.xmlPrefixToNamespaceMap.containsKey(prefix)) return;
        this.xmlPrefixToNamespaceMap.remove(prefix);
    }

    /**
     * Boolean test for whether this object is L1V1 of the specification
     *
     * @return <code>true</code> if is Level-1 Version-1, <code>false</code>
     * otherwise
     * @since 2.2.3
     */
    public boolean isL1V1() {
        return this.sedml.getLevel() == 1 && this.sedml.getVersion() == 1;
    }

    /**
     * Boolean test for whether this object is L1V2 of the specification
     *
     * @return <code>true</code> if is Level-1 Version-2, <code>false</code>
     * otherwise
     * @since 2.2.3
     */
    public boolean isL1V2() {
        return this.sedml.getLevel() == 1 && this.sedml.getVersion() == 2;
    }

    /**
     * Boolean test for whether this object is L1V2 of the specification
     *
     * @return <code>true</code> if is Level-1 Version-2, <code>false</code>
     * otherwise
     * @since 2.2.3
     */
    public boolean isL1V3() {
        return this.sedml.getLevel() == 1 && this.sedml.getVersion() == 3;
    }

    /**
     * Boolean test for whether this object is L1V2 of the specification
     *
     * @return <code>true</code> if is Level-1 Version-2, <code>false</code>
     * otherwise
     * @since 2.2.3
     */
    public boolean isL1V4() {
        return this.sedml.getLevel() == 1 && this.sedml.getVersion() == 4;
    }

    /**
     * Boolean test for whether this object is L1V2 of the specification
     *
     * @return <code>true</code> if is Level-1 Version-2, <code>false</code>
     * otherwise
     * @since 2.2.3
     */
    public boolean isL1V5() {
        return this.sedml.getLevel() == 1 && this.sedml.getVersion() == 5;
    }

    public Model getBaseModel(SId startingModelID) {
        SedBase foundBase = this.sedml.searchInModelsFor(startingModelID);
        if (!(foundBase instanceof Model modelFound)) return null;
        String modelSource = modelFound.getSourceAsString().strip();
        if (!modelSource.startsWith("#")) return modelFound;
        SId nextLevelDownId;
        try {
            nextLevelDownId = new SId(modelSource.substring(1));
        } catch (IllegalArgumentException e) {
            return null;
        }
        return this.getBaseModel(nextLevelDownId);
    }

    /**
     * Attempts to find a base task
     * @param startingTaskID
     * @return
     */
    public Task getBaseTask(SId startingTaskID) {
        SedBase foundBase = this.sedml.searchInTasksFor(startingTaskID);
        if (foundBase instanceof Task task) return task;
        if (foundBase instanceof RepeatedTask rTask){
            if (rTask.getSubTasks().size() != 1) return null;
            return this.getBaseTask(rTask.getSubTasks().get(0).getTask());
        }
        return null;
    }

    public Set<Task> getBaseTasks(SId startingTaskID) {
        SedBase foundBase = this.sedml.searchInTasksFor(startingTaskID);
        if (foundBase instanceof Task task) return new HashSet<>(List.of(task));
        if (foundBase instanceof RepeatedTask rTask){
            Set<Task> tasks = new HashSet<>();
            for (SubTask subTask : rTask.getSubTasks()) {
                tasks.addAll(this.getBaseTasks(subTask.getTask()));
            }
            return tasks;
        }
        throw new IllegalArgumentException(String.format("provided identifier `%s` corresponds to an unknown type of Task (%s)", startingTaskID.string(), foundBase.getClass().getSimpleName()));
    }

    /**
     * Returns the next "submodel" the model referenced by <code>startingModelID</code> uses. If the model does
     * not contain any "sub-models" (i.e. the source is not another sedml model), then null is returned.
     * @param startingModelID id of the model to start with
     * @return null if there is no sub-model or the ID is invalid, otherwise the model object itself
     */
    public Model getSubModel(SId startingModelID) {
        SedBase foundBase = this.sedml.searchInModelsFor(startingModelID);
        if (!(foundBase instanceof Model startingModel)) return null;
        String modelSource = startingModel.getSourceAsString().strip();
        if (!modelSource.startsWith("#")) return null; // no next level to return!
        SId nextLevelDownId;
        try {
            nextLevelDownId = new SId(modelSource.substring(1));
        } catch (IllegalArgumentException e) {
            return null;
        }
        foundBase = this.sedml.searchInModelsFor(nextLevelDownId);
        if (!(foundBase instanceof Model subModel)) return null;
        return subModel;
    }

    /**
     * Attempts to find a subtask task
     * @param startingTaskID
     * @return
     */
    public AbstractTask getActualSubTask(SId startingTaskID) {
        SedBase foundBase = this.sedml.searchInTasksFor(startingTaskID);
        if (!(foundBase instanceof RepeatedTask rTask)) throw new IllegalArgumentException("provided id does not correspond to a repeated Task");
        return this.getActualSubTask(rTask);
    }

    public AbstractTask getActualSubTask(RepeatedTask repeatedTask){
        if (repeatedTask.getSubTasks().size() != 1) return null;
        SedBase foundSubTask = this.sedml.searchInModelsFor(repeatedTask.getSubTasks().get(0).getTask());
        if (foundSubTask instanceof AbstractTask desiredTask) return desiredTask;
        throw new IllegalArgumentException(String.format("provided repeated task `%s` corresponds to an unknown type of Task (%s) as a subtask", repeatedTask.getId().string(), foundSubTask.getClass().getSimpleName()));
    }

    public Set<AbstractTask> getActualSubTasks(SId startingTaskID) {
        SedBase foundBase = this.sedml.searchInTasksFor(startingTaskID);
        if (!(foundBase instanceof RepeatedTask rTask)) throw new IllegalArgumentException("provided id does not correspond to a repeated Task");
        return this.getActualSubTasks(rTask);
    }

    public Set<AbstractTask> getActualSubTasks(RepeatedTask repeatedTask) {
        List<SedBase> subTaskBases = repeatedTask.getSubTasks().stream().map(SubTask::getTask).map(this.sedml::searchInTasksFor).toList();
        List<AbstractTask> tasks = subTaskBases.stream().filter(AbstractTask.class::isInstance).map(AbstractTask.class::cast).toList();
        if (tasks.size() != subTaskBases.size()) throw new RuntimeException("Non-tasks were filtered out of list of subtasks!");
        return new HashSet<>(tasks);
    }


    /**
     * Convenience method to add a Variable and DataGenerator together.<br/>
     * This method should be useful when only the raw form of an output is
     * required, and no post-processing of the data is required.<br/>
     * <p>
     * For example, for a parameter in an SBML model with id <b>k1</b>:
     *
     * <pre>
     * String xpathAsString = new SBMLSupport().getXPathForGlobalParameter(&quot;k1&quot;);
     * XPathTarget xpath = new XPathTarget(xpathAsString);
     * DataGenerator dg = sed.addSimpleSpeciesAsOutput(xpath, &quot;k1&quot;, &quot;A param name&quot;,
     *         t1, true);
     * </pre>
     * <p>
     * will produce the following SED-ML:
     *
     * <pre>
     *  &lt;dataGenerator id="k1_dg" name="A param name"&gt;
     *  &lt;listOfVariables&gt;
     *     &lt;variable id="k1" name=" A param name" target="/path/to/param/"/&gt;
     *  &lt;/listOfVariables&gt;
     *   &lt;math:math&gt;
     *       &lt;math:ci&gt;k1&lt;/math:ci&gt;
     *      &lt;/math:math&gt;
     *  &lt;/dataGenerator&gt;
     * </pre>
     *
     * </p>
     *
     * @param variableToAdd The {@link Variable} to create a data generator for.
     * @return `null` if a sufficient {@link DataGenerator} already exists, else a new {@link DataGenerator} is returned.
     * @throws IllegalArgumentException if the variable has a <code>null</code> id, <code>null</code> task reference, or refers to a task not in this SedML
     * @since 2.1
     */
    public DataGenerator createIdentityDataGeneratorForSpecies(final Variable variableToAdd) {
        SedGeneralClass.checkNoNullArgs(variableToAdd);
        // First, check the variable
        if (null == variableToAdd.getId())
            throw new IllegalArgumentException("Provided variable has a null id.");
        SId taskReference = variableToAdd.getTaskReference();
        if (null == taskReference)
            throw new IllegalArgumentException("Variable must have task reference for use with this method.");
        if (!(this.sedml.searchInTasksFor(taskReference) instanceof AbstractTask task))
            throw new IllegalArgumentException(String.format("Unable to find task `%s` in this SedML.", taskReference));

        // Now, check if we need to add an identity Data Generator
        if (this.sedml.appropriateIdentityDataGeneratorAlreadyExistsFor(variableToAdd))
            return null;
        DataGenerator dg = new DataGenerator(new SId(variableToAdd.getId().string() + "_dg"),
                variableToAdd.getName() != null ? String.format("Data Generator for %s", variableToAdd.getName()) : "");
        dg.setMath(Libsedml.parseFormulaString(dg.getId().string()));
        dg.addVariable(variableToAdd);
        return dg;
    }

    public String getPathForURI() {
        return this.pathForURI;
    }

    public void setPathForURI(String pathForURI) {
        this.pathForURI = pathForURI;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Model findModelById(SId id) {
        SedBase foundBase = this.sedml.searchInModelsFor(id);
        if (!(foundBase instanceof Model model)) return null;
        return model;
    }

    public org.jlibsedml.components.simulation.Simulation findSimulationById(SId id) {
        SedBase foundBase = this.sedml.searchInSimulationsFor(id);
        if (!(foundBase instanceof org.jlibsedml.components.simulation.Simulation sim)) return null;
        return sim;
    }

    public UniformTimeCourse findUniformTimeCourseById(SId id) {
        SedBase foundBase = this.sedml.searchInSimulationsFor(id);
        if (!(foundBase instanceof UniformTimeCourse uniformTimeCourse)) return null;
        return uniformTimeCourse;
    }

    public OneStep findOneStepById(SId id) {
        SedBase foundBase = this.sedml.searchInSimulationsFor(id);
        if (!(foundBase instanceof OneStep oneStep)) return null;
        return oneStep;
    }

    public AbstractTask findAbstractTaskById(SId id) {
        SedBase foundBase = this.sedml.searchInTasksFor(id);
        if (!(foundBase instanceof AbstractTask abstractTask)) return null;
        return abstractTask;
    }

    public Task findTaskById(SId id) {
        SedBase foundBase = this.sedml.searchInTasksFor(id);
        if (!(foundBase instanceof Task task)) return null;
        return task;
    }

    public Task findBaseTaskByAbstractTaskId(SId id) {
        SedBase foundBase = this.sedml.searchInTasksFor(id);
        if (!(foundBase instanceof AbstractTask)) throw new RuntimeException("The element `" + id + "` is not an abstract task.");
        return this.getBaseTask(id);
    }

    public RepeatedTask findRepeatedTaskById(SId id) {
        SedBase foundBase = this.sedml.searchInTasksFor(id);
        if (!(foundBase instanceof RepeatedTask repeatedTask)) return null;
        return repeatedTask;
    }

    public DataGenerator findDataGeneratorById(SId id) {
        SedBase foundBase = this.sedml.searchInDataGeneratorsFor(id);
        if (!(foundBase instanceof DataGenerator dataGenerator)) return null;
        return dataGenerator;
    }

    public Output findOutputById(SId id) {
        SedBase foundBase = this.sedml.searchInOutputsFor(id);
        if (!(foundBase instanceof Output output)) return null;
        return output;
    }

    public Plot findPlotById(SId id) {
        SedBase foundBase = this.sedml.searchInOutputsFor(id);
        if (!(foundBase instanceof Plot plot)) return null;
        return plot;
    }

    public Plot2D findPlot2DById(SId id) {
        SedBase foundBase = this.sedml.searchInOutputsFor(id);
        if (!(foundBase instanceof Plot2D plot)) return null;
        return plot;
    }

    public Plot3D findPlot3DById(SId id) {
        SedBase foundBase = this.sedml.searchInOutputsFor(id);
        if (!(foundBase instanceof Plot3D plot)) return null;
        return plot;
    }

    public Report findReportById(SId id) {
        SedBase foundBase = this.sedml.searchInOutputsFor(id);
        if (!(foundBase instanceof Report report)) return null;
        return report;
    }

    public Axis findAxisById(SId id) {
        SedBase foundBase = this.sedml.searchInOutputsFor(id);
        if (!(foundBase instanceof Axis axis)) return null;
        return axis;
    }
}
