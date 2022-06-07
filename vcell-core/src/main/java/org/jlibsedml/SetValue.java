package org.jlibsedml;

import org.jmathml.ASTNode;
import org.jmathml.FormulaFormatter;

public class SetValue extends ComputeChange {

/*
<listOfChanges>
    <setValue target="/s:sbml/s:model/s:listOfParameters/s:parameter[@id='w']" range="current" modelReference="model1">
        <math xmlns="http://www.w3.org/1998/Math/MathML">
            <ci> current </ci>
        </math>
    </setValue>
</listOfChanges>

<listOfChanges>
    <setValue target="/sbml/model/listOfParameters/parameter[@id='w']" modelReference="model1" >
        <math>
            <ci> current </ci>
        </math>
    </setValue>
</listOfChanges>
*/
    private String modelReference = null;
    // as for functionalRange, variable references always retrieve the current value of the
    // model variable or range at the current iteration of the enclosing repeatedTask. For a model not being
    // simulated by any subTask, the initial state of the model is used.
    private String rangeReference = null;
	private FormulaFormatter formulaFormatter=new FormulaFormatter();

    // Remember to set the math separately
    public SetValue(XPathTarget target, String rangeReference, String modelReference) {
        super(target);
        this.rangeReference = rangeReference;
        this.setModelReference(modelReference);
    };
    public SetValue(XPathTarget target, ASTNode math, String rangeReference, String modelReference) {
        super(target, math);
        this.rangeReference = rangeReference;
        this.setModelReference(modelReference);
    };
    
    public void setRangeReference(String rangeReference) {
        this.rangeReference = rangeReference;
    }
    public String getRangeReference() {
        return rangeReference;
    }
    public void setModelReference(String modelReference) {
        this.modelReference = modelReference;
    }
    public String getModelReference() {
        return modelReference;
    }

    @Override
    public String toString() {
        return "SetValue [getTargetXPath()=" + getTargetXPath()
        + ", getRangeReference()=" + getRangeReference()
        + ", getModelReference()=" + getModelReference()
        + ", getListOfVariables().size()=" + getListOfVariables().size()
        + ", getListOfParameters().size()=" + getListOfParameters().size()
                 + "]";
    }

	public String getMathAsString(){
	    return formulaFormatter.formulaToString(getMath());
	}

    @Override
    public String getChangeKind() {
        return SEDMLTags.SET_VALUE_KIND;
    }

    @Override
    public String getElementName() {
        return SEDMLTags.SET_VALUE;
    }

    @Override
    public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }
}
