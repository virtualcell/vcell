package org.jlibsedml;

import java.util.HashMap;
import java.util.Map;

import org.jmathml.ASTNode;

public class FunctionalRange extends Range {

    private String range = "";
    private Map<String, AbstractIdentifiableElement> variables = new HashMap<String, AbstractIdentifiableElement> ();
    private Map<String, AbstractIdentifiableElement> parameters = new HashMap<String, AbstractIdentifiableElement> ();
    private ASTNode math = null;


    public FunctionalRange(String id, String range) {
        super(id);
        if(range != null) {
            this.range = range;
        }
    }
    public FunctionalRange(String id, String index, Map<String, AbstractIdentifiableElement> variables, Map<String, AbstractIdentifiableElement> parameters, ASTNode mathAsNode) {
        super(id);
        if(index != null) {
            this.range = index;
        }
        if(variables != null) {
            this.variables = variables;
        }
        if(parameters != null) {
            this.parameters = parameters;
        }
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
    public String getRange() {
        return range;
    }
    public void addVariable(AbstractIdentifiableElement var) {
        if(!variables.containsKey(var.getId())) {
            variables.put(var.getId(), var);
        }
    }
    // can be Variable or Parameter
    public Map<String, AbstractIdentifiableElement> getVariables() {
        return variables;
    }
    public void addParameter(AbstractIdentifiableElement var) {
        if(!parameters.containsKey(var.getId())) {
            parameters.put(var.getId(), var);
        }
    }
    // can be Variable or Parameter
    public Map<String, AbstractIdentifiableElement> getParameters() {
        return parameters;
    }
    public void setMath(ASTNode math) {
        this.math = math;
    }
    public ASTNode getMath() {
        return math;
    }

    @Override
    public String toString() {
        return "Functional Range ["
        + "getId()=" + getId()
        + ", getIndex()=" + getRange()
        + ", getMath()=" + getMath()
        + ", variables.size()=" + variables.size()
        + ", parameters.size()=" + parameters.size()
        + "]";
    }
    /**
     * This method is not supported yet.
     * @throws UnsupportedOperationException
     */
    @Override
    public int getNumElements() {
        throw new UnsupportedOperationException("Unsupported method getNumElements() for " + getElementName());
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
        return SEDMLTags.FUNCTIONAL_RANGE_TAG;
    }

    @Override
    public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }
}
