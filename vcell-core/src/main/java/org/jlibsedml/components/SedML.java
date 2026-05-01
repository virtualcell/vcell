package org.jlibsedml.components;

import org.jdom2.Namespace;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.listOfConstructs.*;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.components.output.Output;
import org.jlibsedml.components.simulation.Simulation;
import org.jlibsedml.components.task.AbstractTask;

import java.util.*;


/**
 * The top level object in a SED-ML document.
 * <p/>
 * To create a SedML object, create a SED-ML document first.
 * <p/>
 *
 * <p/>
 * This class is basically a container element for the 5 different parts of the
 * SED-ML - the model, simulation, task description, data generator, and output.
 * </p>
 *
 * Elements can be added in two ways:
 * <ul>
 * <li>Either pass in a previously created list, e.g.,
 *
 * <pre>
 * List&lt;Simulation&gt; simulations = createListOfSimulationsSomewhereElse();
 * sedml.setSimulations(simulations);
 * </pre>
 *
 * or
 * <li>Add simulations one at a time:
 *
 * <pre>
 * Simulation sim = createASimulation();
 * sedml.addSimulation(sim);
 * </pre>
 *
 * </ul>
 *
 * Elements can be added to a list that has previously been set, however setting
 * in a list will overwrite any collections generated from either of the two
 * approaches above. </p>
 *
 * All getListOfXXX() methods will return read-only lists. Attempting to modify
 * these lists will generate runtime exceptions. To manipulate the lists, then,
 * use the add/remove/set methods.
 * <p/>
 *
 * <p>
 * Elements can be searched for by the getXXXWithId() methods. E.g.,
 * </p>
 *
 * <pre>
 * Model model = sedml.getModelWithId(&quot;myModel&quot;);
 * if (model != null) {
 *     // do something.
 * }
 * </pre>
 * <p>
 * It should be noted that all collections held in this object, and elsewhere in
 * SEDML, are mutable from outside the object. So, if you pass in a collection
 * of Simulations into this class, then modify the collection outside this
 * object, then this collection will be modified.
 * </p>
 *
 * @author anu/radams
 *
 */
public class SedML extends SedBase {
    private final static int DEFAULT_LEVEL = 1;
    private final static int DEFAULT_VERSION = 5;

    private int level;
    private int version;
    private ListOfModels models;
    private ListOfSimulations simulations;
    private ListOfTasks tasks;
    private ListOfDataGenerators dataGenerators;
    private ListOfOutputs outputs;

    public SedML() {
        this(SedML.DEFAULT_LEVEL, SedML.DEFAULT_VERSION);
    }

    public SedML(int level, int version) {
        this(null, null, level, version);
    }

    public SedML(SId id, String name) {
        this(id, name, SedML.DEFAULT_LEVEL, SedML.DEFAULT_VERSION);
    }

    public SedML(SId id, String name, int level, int version) {
        this(id, name, level, version, new ListOfModels(), new ListOfSimulations(), new ListOfTasks(), new ListOfDataGenerators(), new ListOfOutputs());
    }

    public SedML(SId id, String name, int level, int version, ListOfModels models, ListOfSimulations simulations,
                 ListOfTasks tasks, ListOfDataGenerators dataGenerators, ListOfOutputs outputs) {
        super(id, name);
        this.level = level;
        this.version = version;
        this.models = models;
        this.simulations = simulations;
        this.tasks = tasks;
        this.dataGenerators = dataGenerators;
        this.outputs = outputs;
    }

    public SedML clone() throws CloneNotSupportedException {
        SedML clone = (SedML) super.clone();
        clone.level = this.level;
        clone.version = this.version;
        clone.models = this.models.clone();
        clone.simulations = this.simulations.clone();
        clone.tasks = this.tasks.clone();
        clone.dataGenerators = this.dataGenerators.clone();
        clone.outputs = this.outputs.clone();
        return clone;
    }

    public int getLevel() {
        return this.level;
    }

    public int getVersion() {
        return this.version;
    }

    public Namespace getSedMLNamespace(){
        return Namespace.getNamespace("", this.getSedMLNamespaceURI());
    }

    public String getSedMLNamespaceURI(){
        return String.format("http://sed-ml.org/sed-ml/level%d/version%d", this.level, this.version);
    }

    public ListOfModels getListOfModels() {
        return this.models;

    }

    /**
     * Returns a read-only list of models in SedML
     *
     * @return unmodifiable list of {@link Model}s
     */
    public List<Model> getModels() {
        return this.models.getContents();
    }

    /**
     * Adds a {@link Model} to this object's {@link ListOfModels}, if not already present.
     *
     * @param model A non-null {@link Model} element
     */
    public void addModel(Model model) {
        this.models.addContent(model);
    }

    /**
     * Removes a {@link Model} from this object's {@link ListOfModels}, if it is present.
     *
     * @param model A non-null {@link Model} element
     */
    public void removeModel(Model model) {
        this.models.removeContent(model);
    }

    public ListOfSimulations getListOfSimulations() {
        return this.simulations;

    }

    /**
     * Returns a read-only list of simulations in SedML
     *
     * @return unmodifiable list of {@link Simulation}s
     */
    public List<Simulation> getSimulations() {
        return this.simulations.getContents();
    }

    /**
     * Adds a {@link Simulation} to this object's {@link ListOfSimulations}, if not already present.
     *
     * @param sim A non-null {@link Simulation} element
     */
    public void addSimulation(Simulation sim) {
        this.simulations.addContent(sim);
    }

    /**
     * Removes a {@link Simulation} from this object's {@link ListOfSimulations}, if it is present.
     *
     * @param sim A non-null {@link Simulation} element
     */
    public void removeSimulation(Simulation sim) {
        this.simulations.removeContent(sim);
    }

    public ListOfTasks getListOfTasks() {
        return this.tasks;
    }

    /**
     * Returns a read-only list of tasks in SedMl
     *
     * @return unmodifiable list of {@link AbstractTask}s
     */
    public List<AbstractTask> getTasks() {
        return this.tasks.getContents();
    }

    /**
     * Adds a {@link AbstractTask} to this object's {@link ListOfTasks}, if not already present.
     *
     * @param task A non-null {@link AbstractTask} element
     */
    public void addTask(AbstractTask task) {
        this.tasks.addContent(task);
    }

    /**
     * Removes an {@link AbstractTask} from this object's {@link ListOfTasks}, if it is present.
     *
     * @param task A non-null {@link AbstractTask} element
     */
    public void removeTask(AbstractTask task) {
        this.tasks.removeContent(task);
    }

    public ListOfDataGenerators getListOfDataGenerators() {
        return this.dataGenerators;
    }

    /**
     * Returns a read-only list of data generators in SedML
     *
     * @return unmodifiable list of {@link DataGenerator}s
     */
    public List<DataGenerator> getDataGenerators() {
        return this.dataGenerators.getContents();
    }

    /**
     * Adds a {@link DataGenerator} to this object's {@link ListOfDataGenerators}, if not already present.
     *
     * @param dataGenerator A non-null {@link DataGenerator} element
     */
    public void addDataGenerator(DataGenerator dataGenerator) {
        this.dataGenerators.addContent(dataGenerator);
    }

    /**
     * Removes a {@link DataGenerator} from this object's {@link ListOfDataGenerators}, if it is present.
     *
     * @param dataGenerator A non-null {@link DataGenerator} element
     */
    public void removeDataGenerator(DataGenerator dataGenerator) {
        this.dataGenerators.removeContent(dataGenerator);
    }

    /**
     * Ease-of-use function to help when auto-creating "identity" DataGenerators
     * @param var a variable to potentially be referenced by a new data generator
     * @return true, if an identity data-generator already exists, else false
     */
    public boolean appropriateIdentityDataGeneratorAlreadyExistsFor(Variable var){
        return this.dataGenerators.appropriateIdentityDataGeneratorAlreadyExistsFor(var);
    }

    public ListOfOutputs getListOfOutputs() {
        return this.outputs;
    }

    /**
     * Returns a read-only list of outputs in SedDocument. This method does not
     * return the list in the order by which Outputs were added. Instead, it
     * orders the outputs by types to agree with the schema.
     * I.e.: Plot2D, Plot3D, Reports.
     * <br/>
     *
     * @return A possibly empty but non-null <code>List</code> of Outputs.
     */
    public List<Output> getOutputs() {
        return this.outputs.getContents();
    }

    /**
     * Adds a {@link Output} to this object's {@link ListOfOutputs}, if not already present.
     *
     * @param output A non-null {@link Output} element
     */
    public void addOutput(Output output) {
        this.outputs.addContent(output);
    }

    /**
     * Removes a {@link Output} from this object's {@link ListOfOutputs}, if it is present.
     *
     * @param output A non-null {@link Output} element
     */
    public void removeOutput(Output output) {
        this.outputs.removeContent(output);
    }

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.SED_ML_ROOT;
    }

    @Override
    public String parametersToString(){
        List<String> params = new ArrayList<>();
        params.add(String.format("level=%d", this.level));
        params.add(String.format("version=%d", this.version));
        if (this.models != null) params.add(String.format("models=%s", this.models.getId() != null ? this.models.getIdAsString() : '{' + this.models.parametersToString() +'}'));
        if (this.simulations != null) params.add(String.format("simulations=%s", this.simulations.getId() != null ? this.simulations.getIdAsString() : '{' + this.simulations.parametersToString() +'}'));
        if (this.tasks != null) params.add(String.format("tasks=%s", this.tasks.getId() != null ? this.tasks.getIdAsString() : '{' + this.tasks.parametersToString() +'}'));
        if (this.dataGenerators != null) params.add(String.format("dataGenerators=%s", this.dataGenerators.getId() != null ? this.dataGenerators.getIdAsString() : '{' + this.dataGenerators.parametersToString() +'}'));
        if (this.outputs != null) params.add(String.format("outputs=%s", this.outputs.getId() != null ? this.outputs.getIdAsString() : '{' + this.outputs.parametersToString() +'}'));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        elementFound = this.models.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        elementFound = this.simulations.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        elementFound = this.tasks.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        elementFound = this.dataGenerators.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        return this.outputs.searchFor(idOfElement);
    }

    public SedBase searchInModelsFor(SId idOfElement){
        return this.models.searchFor(idOfElement);
    }

    public SedBase searchInSimulationsFor(SId idOfElement){
        return this.simulations.searchFor(idOfElement);
    }

    public SedBase searchInTasksFor(SId idOfElement){
        return this.tasks.searchFor(idOfElement);
    }

    public SedBase searchInDataGeneratorsFor(SId idOfElement){
        return this.dataGenerators.searchFor(idOfElement);
    }

    public SedBase searchInOutputsFor(SId idOfElement){
        return this.outputs.searchFor(idOfElement);
    }
}
