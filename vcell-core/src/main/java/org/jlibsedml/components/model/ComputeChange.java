package org.jlibsedml.components.model;

import java.util.List;


import org.jlibsedml.SedMLTags;
import org.jlibsedml.XPathTarget;
import org.jlibsedml.components.*;
import org.jlibsedml.components.listOfConstructs.ListOfParameters;
import org.jlibsedml.components.listOfConstructs.ListOfVariables;
import org.jmathml.ASTNode;
import org.jmathml.ASTRootNode;
import org.jmathml.FormulaFormatter;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Encapsulates the ComputeChange element of SEDML.
 * <p>
 * * @author anu/radams
 *
 */
public class ComputeChange extends Change implements Calculation {
    private final static FormulaFormatter formulaFormatter = new FormulaFormatter();

    private ASTNode math = null;
    private ListOfVariables listOfVariables;
    private ListOfParameters listOfParameters;

    /**
     *
     * @param target A non-null XPathTarget to which the change should be
     *               applied.
     */
    public ComputeChange(XPathTarget target) {
        this(null, null, target);
    }

    /**
     *
     * @param target A non-null XPathTarget to which the change should be
     *               applied.
     * @param math   An {@link ASTRootNode} used  to compute the new value of the target element.
     */
    public ComputeChange(XPathTarget target, ASTNode math) {
        this(null, null, target, math);
    }

    /**
     *
     * @param id id of the element
     * @param name name of the element
     * @param target A non-null XPathTarget to which the change should be applied.
     */
    public ComputeChange(SId id, String name, XPathTarget target) {
        this(id, name, target, null);
    }

    /**
     *
     * @param target A non-null XPathTarget to which the change should be
     *               applied.
     * @param math   An {@link ASTRootNode} used  to compute the new value of the target element.
     */
    public ComputeChange(SId id, String name, XPathTarget target, ASTNode math) {
        super(id, name, target);
        this.setMath(math);
        this.listOfVariables = new ListOfVariables();
        this.listOfParameters = new ListOfParameters();
    }

    public ComputeChange clone() throws CloneNotSupportedException {
        ComputeChange clone = (ComputeChange) super.clone();
        clone.math = this.math;
        clone.listOfVariables = this.listOfVariables;
        clone.listOfParameters = this.listOfParameters;
        return clone;
    }

    public ASTNode getMath() {
        return this.math;
    }
    public void setMath(ASTNode math) {
        this.math = math;
    }



    /**
     * Getter for the change kind.
     *
     * @return SEDMLTags.COMPUTE_CHANGE_KIND;
     */
    @Override
    public String getChangeKind() {
        return SedMLTags.COMPUTE_CHANGE_KIND;
    }


    public void setListOfVariables(List<Variable> listOfVariables) {
        for (Variable variable : listOfVariables) this.listOfVariables.addContent(variable);
    }

    public void setListOfParameters(List<Parameter> listOfParameters) {
         for (Parameter parameter : listOfParameters) this.listOfParameters.addContent(parameter);
    }

    @Override
    public ListOfParameters getListOfParameters() {
        return this.listOfParameters;
    }

    /**
     * Returns a possible empty but non-null list of {@link Parameter} objects
     *
     * @return a list of {@link Parameter}
     */
    public List<Parameter> getParameters() {
        return this.listOfParameters.getContents();
    }

    /**
     * Adds a parameter to this element
     *
     * @param param parameter to add
     */
    public void addParameter(Parameter param) {
        this.listOfParameters.addContent(param);
    }

    @Override
    public void removeParameter(Parameter parameter) {

    }

    @Override
    public ListOfVariables getListOfVariables() {
        return this.listOfVariables;
    }

    /**
     * Returns a possible empty but non-null list of {@link Variable} objects
     *
     * @return a list of {@link Variable}
     */
    public List<Variable> getVariables() {
        return this.listOfVariables.getContents();
    }

    /**
     * Adds a variable to this element
     *
     * @param var a {@link Variable}
     */
    public void addVariable(Variable var) {
        this.listOfVariables.addContent(var);
    }

    @Override
    public void removeVariable(Variable variable) {

    }

    @Override
    public String getElementName() {
        return SedMLTags.COMPUTE_CHANGE;
    }

    /**
     * Convenience function to return the maths expression as a C-style string.
     * @return A <code>String</code> representation of the maths of this DataGenerator.
     */
    public String getMathAsString(){
        return ComputeChange.formulaFormatter.formulaToString(this.math);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        for (Variable var : this.getVariables()) {
            elementFound = var.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        for (Parameter p : this.getParameters()) {
            elementFound = p.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        return elementFound;
    }

    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @OverridingMethodsMustInvokeSuper
    public String parametersToString(){
        return super.parametersToString() + ", " + String.join(", ", this.getMathParamsAndVarsAsStringParams());
    }
}
