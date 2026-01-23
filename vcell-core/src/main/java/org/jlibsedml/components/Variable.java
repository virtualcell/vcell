package org.jlibsedml.components;

import org.jlibsedml.SedMLElementFactory;
import org.jlibsedml.SedMLTags;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a SED-ML Variable element. A variable can have EITHER a
 * <code>taskRef</code> or a <code>modelRef</code> depending on whether it
 * belongs to a ComputeChange or DataGenerator element.
 * <p>
 * The 'taskRef' and 'modelRef' attributes have different meanings. The
 * 'modelRef', used for instance in a computeChange structure, means that the
 * variable refers to the value in the model. This is currently a scalar in
 * SBML, but may be something else in the future (for instance a list or a
 * function). The important concept is that it is the value BEFORE simulation.
 * </p>
 * <p>
 * The 'taskRef' means the variable refers to the value**S** as output by the
 * task. For instance, in COPASI, a variable with 'modelRef' would correspond to
 * the "initial concentration" while a variable with 'taskRef' would correspond
 * to the "transient concentration".
 * </p>
 *
 */
public final class Variable extends SedBase {

    private String targetXPathStr; // maybe make: `private XPathTarget targetXPathStr;` ?
    private SId modelReference;
    private SId taskReference;
    private VariableSymbol symbol;
//    private XPathTarget target2; // Not used yet
//    private VariableSymbol symbol2; // Not used yet
//    private String anyURI; // Not used yet
//    private String dimensionTerm; // Not used yet

    /**
     * Case 1: Variable references a target-only value (ex: DataSource [not currently implemented])
     *
     * @param id
     *            A non-null ID
     * @param name
     *            An optional name ( can be null or empty string)
     * @param targetXPath
     *            An XPath expression identifying the variable in the data
     * @throws IllegalArgumentException
     *            <code>id</code> or <code>targetXPath</code> is null.
     */
    public Variable(SId id, String name, String targetXPath) {
        super(id, name);
        if (SedMLElementFactory.getInstance().isStrictCreation()) {
            SedGeneralClass.checkNoNullArgs(id, targetXPath);
        }
        this.targetXPathStr = targetXPath;
        this.modelReference = null;
        this.taskReference = null;
        this.symbol = null;
    }

    /**
     * Case 2a: Variable references a model by target; note that argument order matters!!
     * 
     * @param id
     *            A non-null ID
     * @param name
     *            An optional name ( can be null or empty string)
     * @param targetXPath
     *            An XPath expression identifying the variable in the model referenced.
     * @param modelReference
     *            A non-null {@link SId} to a model Id ( see class documentation )
     * @throws IllegalArgumentException
     *             <code>id</code>,<code>modelReference</code>, or
     *             <code>targetXPath</code> is null.
     */
    public Variable(SId id, String name, String targetXPath, SId modelReference) {
        super(id, name);
        if (SedMLElementFactory.getInstance().isStrictCreation()) {
           SedGeneralClass.checkNoNullArgs(id, modelReference, targetXPath);
        }
        this.targetXPathStr = targetXPath;
        this.modelReference = null;
        this.taskReference = null;
        this.symbol = null;
    }

    /**
     * Case 2b: Variable references a model by symbol; note that argument order matters!!
     *
     * @param id
     *            A non-null ID
     * @param name
     *            An optional name ( can be null or empty string)
     * @param symbol
     *            An {@link VariableSymbol} identifying the variable in the model referenced
     * @param modelReference
     *            A non-null {@link SId} to a model Id ( see class documentation )
     * @throws IllegalArgumentException
     *             <code>id</code>,<code>modelReference</code>, or
     *             <code>targetXPath</code> is null.
     */
    public Variable(SId id, String name, VariableSymbol symbol, SId modelReference) {
        super(id, name);
        SedGeneralClass.checkNoNullArgs(id, modelReference, symbol);
        this.targetXPathStr = null;
        this.modelReference = modelReference;
        this.taskReference = null;
        this.symbol = symbol;
    }

    /**
     * Case 3a: Variable references a task by target; note that argument order matters!!
     *
     * @param id
     *            A non-null ID
     * @param name
     *            An optional name ( can be null or empty string)
     * @param targetXPath
     *            An XPath expression identifying the variable in the task referenced.
     * @param taskReference
     *            A non-null {@link SId} to a taskId
     * @throws IllegalArgumentException
     *             <code>id</code>,<code>taskReference</code>, or
     *             <code>targetXPath</code> is null.
     */
    public Variable(SId id, String name, SId taskReference, String targetXPath) {
        super(id, name);
        if (SedMLElementFactory.getInstance().isStrictCreation()) {
            SedGeneralClass.checkNoNullArgs(id, taskReference, targetXPath);
        }
        this.targetXPathStr = targetXPath;
        this.modelReference = null;
        this.taskReference = taskReference;
        this.symbol = null;
    }

    /**
     * Case 3b: Variable references a task by symbol; note that argument order matters!!
     *
     * @param id
     *            A non-null ID
     * @param name
     *            An optional name ( can be null or empty string)
     * @param symbol
     *            An {@link VariableSymbol} identifying the variable in the task referenced.
     * @param taskReference
     *            A non-null {@link SId} to a taskId ( see class documentation )
     * @throws IllegalArgumentException
     *             <code>id</code>,<code>taskReference</code>, or
     *             <code>targetXPath</code> is null.
     */
    public Variable(SId id, String name, SId taskReference, VariableSymbol symbol) {
        super(id, name);
        SedGeneralClass.checkNoNullArgs(id, taskReference, symbol);
        this.targetXPathStr = null;
        this.modelReference = null;
        this.taskReference = taskReference;
        this.symbol = symbol;
    }

    /**
     * Case 4a: Variable references a task AND model by symbol; note that argument order matters!!
     *
     * @param id
     *            A non-null ID
     * @param name
     *            An optional name ( can be null or empty string)
     * @param targetXPath
     *            An XPath expression identifying the variable in the task + model referenced.
     * @param modelReference
     *            A non-null {@link SId} to a modelId ( see class documentation )
     * @param taskReference
     *            A non-null {@link SId} to a taskId ( see class documentation )
     * @throws IllegalArgumentException
     *             <code>id</code>,<code>taskReference</code>, or
     *             <code>targetXPath</code> is null.
     */
    public Variable(SId id, String name, SId modelReference, SId taskReference, String targetXPath) {
        super(id, name);
        SId oneRefNotNull = (modelReference != null) ? modelReference : taskReference;
        SedGeneralClass.checkNoNullArgs(id, oneRefNotNull, targetXPath);
        this.targetXPathStr = targetXPath;
        this.modelReference = modelReference;
        this.taskReference = taskReference;
        this.symbol = null;
    }

    /**
     * Case 4b: Variable references a task AND model by symbol; note that argument order matters!!
     *
     * @param id
     *            A non-null ID
     * @param name
     *            An optional name ( can be null or empty string)
     * @param symbol
     *            An {@link VariableSymbol} identifying the variable in the task + model referenced.
     * @param modelReference
     *            A non-null {@link SId} to a modelId ( see class documentation )
     * @param taskReference
     *            A non-null {@link SId} to a taskId ( see class documentation )
     * @throws IllegalArgumentException
     *             <code>id</code>,<code>taskReference</code>, or
     *             <code>targetXPath</code> is null.
     */
    public Variable(SId id, String name, SId modelReference, SId taskReference, VariableSymbol symbol) {
        super(id, name);
        SId oneRefNotNull = (modelReference != null) ? modelReference : taskReference;
        SedGeneralClass.checkNoNullArgs(id, oneRefNotNull, symbol);
        this.targetXPathStr = null;
        this.modelReference = modelReference;
        this.taskReference = taskReference;
        this.symbol = symbol;
    }

    public Variable clone() throws CloneNotSupportedException {
        Variable clone = (Variable) super.clone();
        clone.targetXPathStr = this.targetXPathStr;
        clone.modelReference = new SId(this.modelReference.string());
        clone.taskReference = new SId(this.taskReference.string());
        clone.symbol = this.symbol;
        return clone;
    }

    /**
     * Getter for the XPath string which identifies this variable in the model.
     * If isSymbol() == true, this will return null.
     *
     * @return An XPath string
     */
    public String getTarget() {
        return this.targetXPathStr;
    }

    public void setTarget(String target) {
        this.targetXPathStr = target;
    }

    /**
     * Gets the cross-reference for this variable.
     *
     * @return the reference ID.
     */
    public SId getModelReference() {
        return this.modelReference;
    }

    /**
     * Sets the task reference for this variable.
     * 
     * @param reference
     *            A <code>non-null</code>String.
     * @since 1.2.0
     */
    public void setModelReference(SId reference) {
        this.modelReference = reference;
    }

    /**
     * Gets the cross-reference for this variable.
     *
     * @return the reference ID.
     */
    public SId getTaskReference() {
        return this.taskReference;
    }

    /**
     * Sets the task reference for this variable.
     *
     * @param reference
     *            A <code>non-null</code>String.
     * @since 1.2.0
     */
    public void setTaskReference(SId reference) {
        this.taskReference = reference;
    }

    /**
     * Gets the symbol for this variable, if isSymbol() ==true.
     *
     * @return Gets a {@link VariableSymbol}, or <code>null</code> if
     *         <code>isVariable()==true</code>.
     */
    public VariableSymbol getSymbol() {
        return this.symbol;
    }

    /**
     * Sets the symbol reference for this variable. This method will have the
     * side-effect of setting any task reference to <code>null</code>, since a
     * Variable can only refer to a symbol or a task reference, but not both.
     * 
     * @param symbol
     *            A <code>non-null</code> {@link VariableSymbol}.
     * @since 1.2.0
     */
    public void setSymbol(VariableSymbol symbol) {
        this.symbol = symbol;
        this.targetXPathStr = null;
    }

    /**
     * Boolean query for whether this object represents a SEDML implicit
     * variable, such as time.
     *
     * @return <code>true</code> if this object represents a SEDML implicit
     *         variable, <code>false</code> otherwise.
     */
    public boolean isSymbol() {
        return this.symbol != null;
    }

    /**
     * Boolean test for whether this object represents a SEDML variable.
     * @return <code>true</code> if this object represents a SEDML variable,
     *         <code>false</code> otherwise.
     */
    public boolean referencesXPath() {
        return this.targetXPathStr != null;
    }

    /**
     * Boolean test for whether this object represents the SEDML implicit
     * variable for <em>urn:sedml:symbol:time</em>. 
     * @return <code>true</code> if this object represents the SEDML implicit
     *         variable <em>urn:sedml:symbol:time</em>, <code>false</code>
     *         otherwise.
     */
    public boolean isTime() {
        return this.isSymbol() && this.symbol.equals(VariableSymbol.TIME);
    }


    @Override
    public String getElementName() {
        return SedMLTags.DATAGEN_ATTR_VARIABLE;
    }

    @Override
    public String parametersToString(){
        List<String> params = new ArrayList<>();
        if (this.modelReference != null) params.add(String.format("modelReference=%s", this.modelReference.string()));
        if (this.modelReference != null)params.add(String.format("taskReference=%s", this.taskReference.string()));
        if (this.symbol != null) params.add(String.format("symbol=%s", this.symbol));
        if (this.targetXPathStr != null) params.add(String.format("targetXPathStr=%s", this.targetXPathStr));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }
}
