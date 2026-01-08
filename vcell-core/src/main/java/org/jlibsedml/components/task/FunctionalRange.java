package org.jlibsedml.components.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.components.*;
import org.jlibsedml.components.listOfConstructs.ListOfParameters;
import org.jlibsedml.components.listOfConstructs.ListOfVariables;
import org.jmathml.ASTNode;
import org.jmathml.FormulaFormatter;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class FunctionalRange extends Range implements Calculation {
    private final static FormulaFormatter formulaFormatter = new FormulaFormatter();

    private final SId range;
    private final ListOfVariables variables;
    private final ListOfParameters parameters;
    private ASTNode math;


    public FunctionalRange(SId id, SId range) {
        super(id);
        this.range = range;
        this.variables = new ListOfVariables();
        this.parameters = new ListOfParameters();
        this.math = null;
    }
    public FunctionalRange(SId id, SId range, Map<SId, Variable> variables, Map<SId, Parameter> parameters, ASTNode mathAsNode) {
        this(id, range);
        if(variables != null) for (SId varKey : variables.keySet()) this.variables.addContent(variables.get(varKey));
        if(parameters != null) for (SId paramKey : parameters.keySet()) this.parameters.addContent(parameters.get(paramKey));
        this.math = mathAsNode;
    }

/*
<functionalRange id="current" index="index">
    <listOfVariables>
        <variable id="w" name="current parameter value" target="/sbml/model/listOfParameters/parameter[@id='w']" />
    </listOfVariables>
    <function>
        <math>
            <apply>
            <times/>
            <ci> w </ci>
            <ci> index </ci>
            </apply>
        </math>
    </function>
</functionalRange>
*/
    public SId getRange() {
        return this.range;
    }

    public void setMath(ASTNode math) {
        this.math = math;
    }

    @Override
    public ListOfParameters getListOfParameters() {
        return this.parameters;
    }

    @Override
    public void addParameter(Parameter parameter) {
        this.parameters.addContent(parameter);
    }

    @Override
    public void removeParameter(Parameter parameter) {
        this.parameters.removeContent(parameter);
    }

    @Override
    public ListOfVariables getListOfVariables() {
        return this.variables;
    }

    @Override
    public void addVariable(Variable variable) {
        this.variables.addContent(variable);
    }

    @Override
    public void removeVariable(Variable variable) {
        this.variables.removeContent(variable);
    }

    /**
     * Convenience function to return the maths expression as a C-style string.
     *
     * @return A <code>String</code> representation of the maths of this DataGenerator.
     */
    @Override
    public String getMathAsString() {
        return FunctionalRange.formulaFormatter.formulaToString(this.math);
    }

    public ASTNode getMath() {
        return this.math;
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
        List<String> params = new ArrayList<>();
        params.add(String.format("range=%s", this.getRange()));
        params.addAll(this.getMathParamsAndVarsAsStringParams());
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    /**
     * This method is not supported yet.
     * @throws UnsupportedOperationException
     */
    @Override
    public int getNumElements() {
        throw new UnsupportedOperationException("Unsupported method getNumElements() for " + this.getElementName());
    }

    /**
     * This method is not supported yet.
     * @throws UnsupportedOperationException 
     */
    @Override
    public double getElementAt(int index) {
        throw new UnsupportedOperationException("Unsupported method getElementAt() for " + getElementName());
    }

    @Override
    public String getElementName() {
        return SedMLTags.FUNCTIONAL_RANGE_TAG;
    }

    @Override
    public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }
}
