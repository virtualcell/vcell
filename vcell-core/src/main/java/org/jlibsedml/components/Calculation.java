package org.jlibsedml.components;

import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.components.listOfConstructs.ListOfParameters;
import org.jlibsedml.components.listOfConstructs.ListOfVariables;
import org.jmathml.ASTNode;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;

// Java does not allow for multiple inheritance, this is the most appropriate work around, since `Calculation` is abstract.
public interface Calculation extends SedGeneralClass {
    SId getId(); // This should end up being implemented through `SedBase`

    ListOfParameters getListOfParameters();
    void addParameter(Parameter parameter);
    void removeParameter(Parameter parameter);

    ListOfVariables getListOfVariables();
    void addVariable(Variable variable);
    void removeVariable(Variable variable);

    /**
     * Convenience function to return the maths expression as a C-style string.
     * @return A <code>String</code> representation of the maths of this DataGenerator.
     */
    String getMathAsString();
    ASTNode getMath();
    void setMath(ASTNode math);

    /**
     * Proxy for shared code used by `parametersToString()` method children should implement
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @OverridingMethodsMustInvokeSuper
     default List<String> getMathParamsAndVarsAsStringParams(){
        List<String> params = new ArrayList<>(), paramParams = new ArrayList<>(), varParams = new ArrayList<>();
        if (this.getMath() != null) params.add(String.format("math=%s", this.getMathAsString()));
        for (Parameter p : this.getListOfParameters().getContents()) paramParams.add(p.getId() != null ? p.getIdAsString() : '[' + p.parametersToString() + ']');
        for (Variable var : this.getListOfVariables().getContents()) varParams.add(var.getId() != null ? var.getIdAsString() : '[' + var.parametersToString() + ']');
        params.add(String.format("parameters={%s}", String.join(",", paramParams)));
        params.add(String.format("variables={%s}", String.join(",", varParams)));
        return params;
    }
}
