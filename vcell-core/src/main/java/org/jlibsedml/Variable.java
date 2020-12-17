package org.jlibsedml;

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
public final class Variable extends AbstractIdentifiableElement {

    private String targetXPathStr = null;
    private String reference = null; // task OR model ID
    private VariableSymbol symbol;

    /**
     * Standard constructor.
     * 
     * @param id
     *            A non-null ID
     * @param name
     *            An optional name ( can be null or empty string)
     * @param reference
     *            A non-null, non-empty<code>String</code> reference, to either
     *            a taskId or a model Id ( see class documentation )
     * @param targetXPath
     *            An XPath expression identifying the variable in the model
     *            referenced by the {@link Task} element.
     * @throws IllegalArgumentException
     *             id <code>id</code>,<code>reference</code>, or
     *             <code>targetXPath</code> is null.
     */
    public Variable(String id, String name, String reference, String targetXPath) {
        super(id, name);
        if (SEDMLElementFactory.getInstance().isStrictCreation()) {
            Assert.checkNoNullArgs(reference, targetXPath);
        }
        this.targetXPathStr = targetXPath;
        this.reference = reference;
    }

    /**
     * Alternative constructor for use when this object refers to an implicit
     * variable such as time.
     * 
     * @param id
     * @param name
     * @param reference
     * @param symbol
     *            A non-null {@link VariableSymbol} object.
     * @throws IllegalArgumentException
     *             id <code>id</code>,<code>reference</code>, or
     *             <code>symbol</code> is null.
     */
    public Variable(String id, String name, String reference,
            VariableSymbol symbol) {
        super(id, name);
        Assert.checkNoNullArgs(reference, symbol);
        this.symbol = symbol;
        this.reference = reference;
    }

    /**
     * Gets the symbol for this variable, if isSymbol() ==true.
     * 
     * @return Gets a {@link VariableSymbol}, or <code>null</code> if
     *         <code>isVariable()==true</code>.
     */
    public VariableSymbol getSymbol() {
        return symbol;
    }

    /**
     * Boolean query for whether or not this object represents a SEDML implicit
     * variable, such as time.
     * 
     * @return <code>true</code> if this object represents a SEDML implicit
     *         variable, <code>false</code> otherwise.
     */
    public boolean isSymbol() {
        return symbol != null;
    }

    /**
     * Sets the target XPath for this object. This method will have the
     * side-effect of setting any symbol reference to <code>null</code>, since a
     * Variable can only refer to a symbol or a model variable, but not both.
     * 
     * @param targetXPathStr
     *            A non-null <code></code>representing an XPath statement.
     * @since 1.2.0
     */
    public void setTargetXPathStr(String targetXPathStr) {
        this.targetXPathStr = targetXPathStr;
        this.symbol = null;
    }

    /**
     * Sets the task reference for this variable.
     * 
     * @param reference
     *            A <code>non-null</code>String.
     * @since 1.2.0
     */
    public void setReference(String reference) {
        this.reference = reference;

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

    @Override
    public String toString() {
        return "Variable [name=" + getName() + ", reference=" + reference
                + ", symbol=" + symbol + ", targetXPathStr=" + targetXPathStr
                + ", getId()=" + getId() + "]";
    }

    /**
     * Boolean test for whether or not this object represents a SEDML variable.
     * @return <code>true</code> if this object represents a SEDML variable,
     *         <code>false</code> otherwise.
     */
    public boolean isVariable() {
        return targetXPathStr != null;
    }

    /**
     * Boolean test for whether or not this object represents the SEDML implicit
     * variable for <em>urn:sedml:symbol:time</em>. 
     * @return <code>true</code> if this object represents the SEDML implicit
     *         variable <em>urn:sedml:symbol:time</em>, <code>false</code>
     *         otherwise.
     */
    public boolean isTime() {
        return isSymbol() && symbol.equals(VariableSymbol.TIME);
    }

    /**
     * Getter for the XPath string which identifies this variable in the model.
     * If isSymbol() == true, this will return null.
     * 
     * @return An XPath string
     */
    public final String getTarget() {
        return targetXPathStr;
    }

    /**
     * Gets the cross-reference for this variable. If this variable is defined
     * inside a DataGenerator element, this cross-reference will refer to a
     * <code>task</code> id. If it occurs in a <code>ComputeChange</code>
     * element, it will refer to a <code>Model</code> id.
     * 
     * @return the reference ID.
     */
    public final String getReference() {
        return reference;
    }

    @Override
    public String getElementName() {
        return SEDMLTags.DATAGEN_ATTR_VARIABLE;
    }

    public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }
}
