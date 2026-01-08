package org.jlibsedml.components.model;

import java.util.ArrayList;
import java.util.List;


import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.XPathTarget;
import org.jlibsedml.components.Calculation;
import org.jlibsedml.components.Parameter;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.Variable;
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
    private final ListOfVariables listOfVariables;
    private final ListOfParameters listOfParameters;

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

    public ComputeChange(SId id, String name, XPathTarget target) {
        this(id, name, target, null);
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

    /**
     * Returns a possible empty but non-null list of {@link Parameter} objects
     *
     * @return a list of {@link Parameter}
     */
    public ListOfParameters getListOfParameters() {
        return this.listOfParameters;
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

    /**
     * Returns a possible empty but non-null list of {@link Variable} objects
     *
     * @return a list of {@link Variable}
     */
    public ListOfVariables getListOfVariables() {
        return this.listOfVariables;
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

    public boolean accept(SEDMLVisitor visitor) {
        if (!visitor.visit(this)) return false;
        for (Variable var : this.getListOfVariables().getContents()) {
            if (!var.accept(visitor)) return false;
        }
        for (Parameter p : this.getListOfParameters().getContents()) {
            if (!p.accept(visitor)) return false;
        }
        return true;
    }

    /**
     * Convenience function to return the maths expression as a C-style string.
     * @return A <code>String</code> representation of the maths of this DataGenerator.
     */
    public String getMathAsString(){
        return ComputeChange.formulaFormatter.formulaToString(this.math);
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
