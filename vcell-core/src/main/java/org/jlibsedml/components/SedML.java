package org.jlibsedml.components;

import org.jlibsedml.SEDMLVisitor;
import org.jdom2.Namespace;
import org.jlibsedml.SedMLDataContainer;
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
    private final int level;
    private final int version;
    private final Map<String, Namespace> xmlPrefixToNamespaceMap = new HashMap<>();

    private final static int DEFAULT_LEVEL = 1;
    private final static int DEFAULT_VERSION = 5;
    private static List<Namespace> getDefaultNamespaces(int sedmlLevel, int sedmlVersion){
        String sedmlURI = String.format("http://sed-ml.org/sed-ml/level%d/version%d", sedmlLevel, sedmlVersion);
        return Arrays.asList(
                Namespace.getNamespace(sedmlURI),
                Namespace.getNamespace("math", "http://www.w3.org/1998/Math/MathML"),
                Namespace.getNamespace("sbml", "http://www.sbml.org/sbml/level3/version2/core")
        );
    }

    private final ListOfModels models;
    private final ListOfSimulations simulations;
    private final ListOfTasks tasks;
    private final ListOfDataGenerators dataGenerators;
    private final ListOfOutputs outputs;

    public SedML() {
        this(SedML.DEFAULT_LEVEL, SedML.DEFAULT_VERSION);
    }

    public SedML(int level, int version) {
        this(level, version, SedML.getDefaultNamespaces(level, version));
    }

    public SedML(int level, int version, List<Namespace> xmlNameSpaces) {
        this(null, null,  level, version, xmlNameSpaces);
    }

    public SedML(SId id, String name) {
        this(id, name, SedML.DEFAULT_LEVEL, SedML.DEFAULT_VERSION);
    }

    public SedML(SId id, String name, int level, int version) {
        this(id, name, level, version,  SedML.getDefaultNamespaces(level, version));
    }

    public SedML(SId id, String name, int level, int version, List<Namespace> xmlNameSpaces) {
        super(id, name);
        this.level = level;
        this.version = version;
        for (Namespace namespace : xmlNameSpaces) this.addNamespace(namespace);
        this.models = new ListOfModels();
        this.simulations = new ListOfSimulations();
        this.tasks = new ListOfTasks();
        this.dataGenerators = new ListOfDataGenerators();
        this.outputs = new ListOfOutputs();
    }

    public int getLevel() {
        return this.level;
    }

    public int getVersion() {
        return this.version;
    }

    /**
     * Fetches the namespaces associated with this <code>SedML</code> object
     * @return an unmodifiable {@link List} of {@link Namespace}
     */
    public List<Namespace> getNamespaces() {
        return this.xmlPrefixToNamespaceMap.keySet().stream().map(this.xmlPrefixToNamespaceMap::get).toList();
    }

    public void addNamespace(Namespace namespace) {
        String prefix = namespace.getPrefix();
        if (prefix == null) prefix = "";
        if (this.xmlPrefixToNamespaceMap.containsKey(prefix))
            throw new IllegalStateException(String.format("Namespace already exists for prefix %s", prefix.isEmpty() ? "<no_prefix>": "\"" + prefix + "\"" ));
        this.xmlPrefixToNamespaceMap.put(prefix, namespace);
    }

    public void removeNamespace(Namespace namespace) {
        String prefix = namespace.getPrefix();
        if (prefix == null) prefix = "";
        if (!this.xmlPrefixToNamespaceMap.containsKey(prefix)) return;
        this.xmlPrefixToNamespaceMap.remove(prefix);
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

    /**
     * Returns a read-only list of simulations in SedML
     *
     * @return unmodifiable list of {@link Simulation}s
     */
    public List<Simulation> getSimulations() {
        return this.simulations.getContents();
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
     * Returns a read-only list of data generators in SedML
     *
     * @return unmodifiable list of {@link DataGenerator}s
     */
    public List<DataGenerator> getDataGenerators() {
        return this.dataGenerators.getContents();
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

    @Override
    public boolean accept(SEDMLVisitor visitor) {
        return true; // keep searching
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
}
