package org.jlibsedml;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Namespace;
import org.jlibsedml.execution.IModelResolver;
import org.jlibsedml.execution.ModelResolver;
import org.jlibsedml.extensions.ElementSearchVisitor;
import org.jmathml.ASTNode;

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
public final class SedML extends SEDBase {

    @Override
    public String toString() {
        return "SedML [level=" + level + "]";
    }

    private int level = 1;
    private int version = 2;
    private Namespace xmlns = null;

    private List additionalNamespaces = new ArrayList();

    private List<Model> models = new ArrayList<Model>();
    private List<Simulation> simulations = new ArrayList<Simulation>();
    private List<AbstractTask> tasks = new ArrayList<AbstractTask>();
    private List<DataGenerator> dataGenerators = new ArrayList<DataGenerator>();
    private List<Output> outputs = new ArrayList<Output>();

    /**
     * Sorts a list of Outputs into the correct order specified in the schema.
     * 
     * @author radams
     *
     */
    static class OutputComparator implements Comparator<Output> {
        static Map<String, Integer> changeKindOrder;
        static {
            changeKindOrder = new HashMap<String, Integer>();
            changeKindOrder.put(SEDMLTags.PLOT2D_KIND, 1);
            changeKindOrder.put(SEDMLTags.PLOT3D_KIND, 2);
            changeKindOrder.put(SEDMLTags.REPORT_KIND, 3);

        }

        public int compare(Output o1, Output o2) {
            return changeKindOrder.get(o1.getKind()).compareTo(
                    changeKindOrder.get(o2.getKind()));
        }

    }

    SedML(int aLevel, int aVersion, Namespace aNameSpace) {
        if (aLevel != 1) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Invalid level {0}, valid level is {1}", aLevel, "1"));
        }
        if (aVersion < 1 || aVersion > 2) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Invalid version {0}, valid versions are {1}", aVersion,
                    "1,2"));
        }
        this.level = aLevel;
        this.version = aVersion;
        this.xmlns = aNameSpace;
    }

    SedML(Namespace aNamespace) {
        this.xmlns = aNamespace;
        this.level = 1;
        this.version = 1;
    }

    /**
     * Returns a read-only list of models in SedDocument
     * 
     * @return list of {@link Model}s
     */
    public List<Model> getModels() {
        return Collections.unmodifiableList(models);
    }

    /**
     * Returns a read-only list of simulations in SedML
     * 
     * @return list of simulations
     */
    public List<Simulation> getSimulations() {
        return Collections.unmodifiableList(simulations);
    }

    /**
     * Returns a read-only list of tasks in SedMl
     * 
     * @return list of tasks
     */
    public List<AbstractTask> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    @Override
    public String getElementName() {
        return SEDMLTags.ROOT_NODE_TAG;
    }

    /**
     * Returns a read-only list of data generators in SedML
     * 
     * @return list of datagenerators
     */
    public List<DataGenerator> getDataGenerators() {
        return Collections.unmodifiableList(dataGenerators);
    }

    /**
     * Returns a read-only list of outputs in SedDocument. This method does not
     * return the list in the order by whic Outputs were added. Instead, it
     * orders the outputs by types to agree with the schema. I.e., Plot2D
     * ,Plot3d, Reports. <br
     * /
     * 
     * @return A possibly empty but non-null <code>List</code> of Outputs.
     */
    public List<Output> getOutputs() {
        Collections.sort(outputs, new OutputComparator());
        return Collections.unmodifiableList(outputs);
    }

    /**
     * Sets additional namespaces on SedDocument
     */
    public void setAdditionalNamespaces(List additionalNamespaces) {
        this.additionalNamespaces = additionalNamespaces;
    }

    /**
     * Sets list of models on SedDocument
     * 
     * @param models
     *            list of Model objects
     */
    public void setModels(List<Model> models) {
        this.models = models;
    }

    /**
     * Adds a {@link Model} to this object's list of Models, if not already
     * present.
     * 
     * @param model
     *            A non-null {@link Model} element
     * @return <code>true</code> if model added, <code>false </code> otherwise.
     */
    public boolean addModel(Model model) {
        if (!models.contains(model))
            return models.add(model);
        return false;
    }

    /**
     * Removes a {@link Model} from this object's list of Models.
     * 
     * @param model
     *            A non-null {@link Model} element
     * @return <code>true</code> if model removed, <code>false </code>
     *         otherwise.
     */
    public boolean removeModel(Model model) {
        return models.remove(model);
    }

    /**
     * Sets list of simulations on SedDocument
     * 
     * @param simulations
     *            list of simulations
     */
    public void setSimulations(List<Simulation> simulations) {
        this.simulations = simulations;
    }

    /**
     * Adds a {@link Simulation} to this object's list of Simulations, if not
     * already present.
     * 
     * @param simulation
     *            A non-null {@link Simulation} element
     * @return <code>true</code> if simulation added, <code>false </code>
     *         otherwise.
     */
    public boolean addSimulation(Simulation simulation) {
        if (!simulations.contains(simulation))
            return simulations.add(simulation);
        return false;
    }

    /**
     * Removes a {@link Simulation} from this object's list of Simulations.
     * 
     * @param simulation
     *            A non-null {@link Simulation} element
     * @return <code>true</code> if simulation removed, <code>false </code>
     *         otherwise.
     */
    public boolean removeSimulation(Simulation simulation) {
        return simulations.remove(simulation);
    }

    /**
     * Sets list of tasks on SedDocument
     * 
     * @param tasks
     *            list of Tasks
     */
    public void setTasks(List<AbstractTask> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a {@link Task} to this object's list of Tasks, if not already
     * present.
     * 
     * @param task
     *            A non-null {@link Task} element
     * @return <code>true</code> if task added, <code>false </code> otherwise.
     */
    public boolean addTask(AbstractTask task) {
        if (!tasks.contains(task))
            return tasks.add(task);
        return false;
    }

    /**
     * Removes a {@link Task} from this object's list of Tasks.
     * 
     * @param task
     *            A non-null {@link Task} element
     * @return <code>true</code> if task removed, <code>false </code> otherwise.
     */
    public boolean removeTask(AbstractTask task) {
        return tasks.remove(task);
    }

    /**
     * Sets list of data generators on SedDocument
     * 
     * @param dataGenerators
     *            list of DataGenerators
     */
    public void setDataGenerators(List<DataGenerator> dataGenerators) {
        this.dataGenerators = dataGenerators;
    }

    /**
     * Adds a {@link DataGenerator} to this object's list of DataGenerators, if
     * not already present.
     * 
     * @param dataGenerator
     *            A non-null {@link DataGenerator} element
     * @return <code>true</code> if dataGenerator added, <code>false </code>
     *         otherwise.
     */
    public boolean addDataGenerator(DataGenerator dataGenerator) {
        if (!dataGenerators.contains(dataGenerator))
            return dataGenerators.add(dataGenerator);
        return false;
    }

    /**
     * Removes a {@link DataGenerator} from this object's list of
     * DataGenerators.
     * 
     * @param dg
     *            A non-null {@link DataGenerator} element
     * @return <code>true</code> if removed, <code>false </code> otherwise.
     */
    public boolean removeDataGenerator(DataGenerator dg) {
        return dataGenerators.remove(dg);
    }

    /**
     * Sets list of outputs on SedDocument
     */
    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    /**
     * Adds a {@link Output} to this object's list of Outputs, if not already
     * present.
     * 
     * @param output
     *            A non-null {@link Output} element
     * @return <code>true</code> if dataGenerator added, <code>false </code>
     *         otherwise.
     */
    public boolean addOutput(Output output) {
        if (!outputs.contains(output))
            return outputs.add(output);
        return false;
    }

    /**
     * Removes a {@link Output} from this object's list of Outputs.
     * 
     * @param output
     *            A non-null {@link Output} element
     * @return <code>true</code> if removed, <code>false </code> otherwise.
     */
    public boolean removeOutput(Output output) {
        return outputs.remove(output);
    }

    /**
     * Returns the SED-ML {@link Model} with id 'modelRef', if present. <br/>
     * This method does not return the content of the model; use implementations
     * of {@link IModelResolver} together with the {@link ModelResolver} class
     * to get the model content.
     * 
     * @param modelRef
     *            A non-null model Identifier
     * @return The model element with the specified ID, or <code>null</code> if
     *         not found.
     */
    public Model getModelWithId(String modelRef) {
        for (int i = 0; i < models.size(); i++) {
            Model m = models.get(i);
            if (m.getId().equals(modelRef)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Boolean test for whether the <code> modelRef</code> argument corresponds
     * to a model ID defined in the list of models.
     * 
     * @param modelRef
     * @return <code>true</code> if a model is defined with
     *         <code> modelRef</code> argument, <code>false</code> otherwise.
     */
    public boolean isModel(String modelRef) {
        for (int i = 0; i < models.size(); i++) {
            Model m = models.get(i);
            if (m.getId().equals(modelRef)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns simulation with id 'simRef', if present.
     * 
     * @param simRef
     * @return A {@link Simulation} object, or <code>null</code> if not found.
     */
    public Simulation getSimulation(String simRef) {
        for (int i = 0; i < simulations.size(); i++) {
            Simulation s = simulations.get(i);
            if (s.getId().equals(simRef)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Returns task with id 'taskRef', if present
     * 
     * @param taskRef
     * @return A {@link Task} object, or <code>null</code> if not found.
     */
    public AbstractTask getTaskWithId(String taskRef) {
        for (int i = 0; i < tasks.size(); i++) {
            AbstractTask t = tasks.get(i);
            if (t.getId().equals(taskRef)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Returns a {@link DataGenerator} with id 'dataGenRef', if present.
     * 
     * @param dataGenRef
     * @return A {@link DataGenerator} object, or <code>null</code> if not
     *         found.
     */
    public DataGenerator getDataGeneratorWithId(String dataGenRef) {
        for (int i = 0; i < dataGenerators.size(); i++) {
            DataGenerator d = dataGenerators.get(i);
            if (d.getId().equals(dataGenRef)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Returns the {@link Output} with id 'outputID', if present.
     * 
     * @param outputID
     * @return A {@link Output} object, or <code>null</code> if not found.
     */
    public Output getOutputWithId(String outputID) {
        for (int i = 0; i < outputs.size(); i++) {
            Output d = outputs.get(i);
            if (d.getId().equals(outputID)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Returns list of additional namespaces in SedDocument
     * 
     * @return list of namespaces
     */
    public List getAdditionalNamespaces() {
        return additionalNamespaces;
    }

    /**
     * Returns version of SedDocument
     * 
     * @return the version of this document's SEDML level
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns level of SedDocument
     * 
     * @return the SEDML level of this document
     */
    public int getLevel() {
        return level;
    }

    /**
     * Boolean test for whether this object is L1V1 of the specification
     * 
     * @return <code>true</code> if is Level-1 Version-1, <code>false</code>
     *         otherwise
     * @since 2.2.3
     */
    public boolean isL1V1() {
        return level == 1 && version == 1;
    }

    /**
     * Boolean test for whether this object is L1V2 of the specification
     * 
     * @return <code>true</code> if is Level-1 Version-2, <code>false</code>
     *         otherwise
     * @since 2.2.3
     */
    public boolean isL1V2() {
        return level == 1 && version == 2;
    }

    /**
     * Returns default namespace of SedDocument
     * 
     * @return a {@link Namespace}
     */
    public Namespace getNamespace() {
        return xmlns;
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
     * 
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
     * @param xpath
     *            The {@link XPathTarget} identifying the element.
     * @param id
     *            A suggested ID for the Variable element. If this ID is not
     *            currently used in the model, a suffix will be appended to make
     *            the ID unique. <br/>
     *            The suffix will be '__x' where x is an integer. <br/>
     *            The dataGenerator ID will be the variableID + '_dg'.
     * @param name
     *            An optional name. This will be used for the Variable and
     *            DataGenerator name.
     * @param task
     *            A {@link Task} object.
     * @param force
     *            A <code>boolean</code>. If <code>true</code>, will force a new
     *            identifier if need be, if <code>false</code>, will not force a
     *            new identifier if there is already a Variable in this SED-ML
     *            object with the given id.
     * @return the created {@link DataGenerator} if was successfully added,
     *         <code>null</code> otherwise ( for example if no unique identifier
     *         can be generated in a fixed number of attempts ).
     * @throws IllegalArgumentException
     *             if any argument except <code>name</code> is null.
     * @since 2.1
     */
    public final DataGenerator addSimpleSpeciesAsOutput(
            final XPathTarget xpath, final String id, final String name,
            final AbstractTask task, boolean force) {
        Assert.checkNoNullArgs(xpath, id, task);
        ElementSearchVisitor vis1 = new ElementSearchVisitor(id);
        String dgID = id + "_dg";

        ElementSearchVisitor vis2 = new ElementSearchVisitor(dgID);
        final int MAX_ATTEMPTS = 10;
        int attempts = 0;
        accept(vis1);
        accept(vis2);
        int suffix = 0;
        String newID = id;
        while (vis1.getFoundElement() != null && vis2.getFoundElement() != null
                && attempts < MAX_ATTEMPTS) {
            if (!force) {
                return null;
            }
            // try a new id to see if it's unique
            attempts++;
            newID = id + "__" + ++suffix;
            dgID = newID + "_dg";
            vis1 = new ElementSearchVisitor(newID);
            vis2 = new ElementSearchVisitor(dgID);
            accept(vis1);
            accept(vis2);

        }
        if (attempts < MAX_ATTEMPTS) {
            // create the SEDML objects
            DataGenerator dg = new DataGenerator(dgID, name);
            ASTNode node = Libsedml.parseFormulaString(newID);
            dg.setMathML(node);
            Variable var = new Variable(newID, name, task.getId(),
                    xpath.getTargetAsString());
            dg.addVariable(var);
            addDataGenerator(dg);
            return dg;
        } else {
            return null;
        }

    }

    /**
     * This is a convenience method to add many data generators using just the
     * IDs of model elements. <br/>
     * There are several pre-conditions for this method to succeed:
     * <ul>
     * <li>The identifiers supplied in the var-args list of {@link IdName}
     * objects should uniquely identify a model element. If the id is not
     * unique, this method will generate an XPath expression to locate the first
     * match found.
     * <li>The model content should be retrievable and should be a valid,
     * well-formed XML document.
     * <li>Each {@link DataGenerator} will be created using the conventions of
     * {@link #addIdentifiersAsDataGenerators(AbstractTask, String, boolean, IModelResolver, IdName...)}.
     * <li>The list of {@link IdName} objects should contain no duplicate
     * identifiers.
     * </ul>
     * Post-conditions are that:
     * <ul>
     * <li>The model source will be unaffected.
     * <li>Only the list of {@link DataGenerator} objects will be affected by
     * this method; the other parts of the SED-ML object remain unaltered.
     * </ul>
     * 
     * @param task
     *            The {@link Task} to which the DataGenerators will refer. This
     *            object MUST belong to this SEDML object
     * @param attributeIdentifierName
     *            The name of the attribute used to uniquely identify the
     *            elements to which DataGenerators will refer. For example, in
     *            an SBML file, this parameter would be "id" or "meta_id"
     * @param allOrNothing
     *            A <code>boolean</code>. If <code>true</code>, either all the
     *            requested identifiers will be converted into DataGenerators,
     *            or none if at least one identifier could not be resolved in
     *            the model. If <code>false</code>, a best effort will be made
     *            to resolve the identifiers and as many datagenerators added
     *            for each match.
     * @param modelResolver
     *            An {@link IModelResolver} capable of obtaining the model
     *            content from the Task's model reference.
     * @param idNameList
     *            A var-args list of id-name pairs. The ID part should uniquely
     *            identify an element in the model.
     * @return <code>true</code> if at least some datagenerators were added, or
     *         <code>false</code> if none were ( for example, if the model
     *         content could not be obtained, or the model was not well-formed,
     *         or the Task did not belong to this SedML object
     * @throws IllegalArgumentException
     *             if any argument is <code>null</code>.
     * @since 2.1
     */
    public final boolean addIdentifiersAsDataGenerators(AbstractTask task,
            final String attributeIdentifierName, boolean allOrNothing,
            IModelResolver modelResolver, IdName... idNameList) {
        Assert.checkNoNullArgs(task, attributeIdentifierName, modelResolver,
                idNameList);
        boolean foundTask = false;
        for (AbstractTask belonging : getTasks()) {
            if (task == belonging) {
                foundTask = true;
            }
        }
        if (!foundTask) {
            return false;
        }
        XpathGeneratorHelper gen = new XpathGeneratorHelper(this);
        return gen.addIdentifiersAsDataGenerators(task,
                attributeIdentifierName, allOrNothing, modelResolver,
                idNameList);
    }

    /**
     * This method returns a non-redundant <code>List</code> of models which
     * refer to external sources and do not refer to other models in this
     * document.
     * 
     * @return Returns a possibly empty but non-null read-only list of base
     *         models in this document.
     */
    public List<Model> getBaseModels() {
        Set<Model> set = new HashSet<Model>();
        for (Model m : getModels()) {
            if (getModelWithId(m.getSource()) == null) {
                set.add(m);
            }
        }
        List<Model> rc = new ArrayList<Model>();
        for (Model m : set) {
            rc.add(m);
        }
        return Collections.unmodifiableList(rc);
    }

    @Override
    public boolean accept(SEDMLVisitor visitor) {
        if (!visitor.visit(this)) {
            return false;
        }
        for (Simulation sim : getSimulations()) {
            if (!sim.accept(visitor)) {
                return false;
            }
        }
        for (AbstractTask t : getTasks()) {
            if (!t.accept(visitor)) {
                return false;
            }
        }
        for (Model m : getModels()) {
            if (!m.accept(visitor)) {
                return false;
            }
        }
        for (DataGenerator dg : getDataGenerators()) {
            if (!dg.accept(visitor)) {
                return false;
            }
        }
        for (Output o : getOutputs()) {
            if (!o.accept(visitor)) {
                return false;
            }
        }
        return true;
    }
}
