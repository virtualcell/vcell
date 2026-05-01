package org.jlibsedml.components.task;

import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.model.ComputeChange;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.XPathTarget;
import org.jmathml.ASTNode;

import java.util.ArrayList;
import java.util.List;

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

public class SetValue extends ComputeChange {

    private SId modelReference;
    // as for functionalRange, variable references always retrieve the current value of the
    // model variable or range at the current iteration of the enclosing repeatedTask. For a model not being
    // simulated by any subTask, the initial state of the model is used.
    private SId rangeReference;

    // Remember to set the math separately
    public SetValue(XPathTarget target, SId rangeReference, SId modelReference) {
        this(null, null, target, rangeReference, modelReference);

    }

    public SetValue(XPathTarget target, ASTNode math, SId rangeReference, SId modelReference) {
        this(null, null, target, math, rangeReference, modelReference);
    }

    // Remember to set the math separately
    public SetValue(SId id, String name, XPathTarget target, SId rangeReference, SId modelReference) {
        super(id, name, target);
        this.modelReference = modelReference;
        this.rangeReference = rangeReference;
    }

    public SetValue(SId id, String name, XPathTarget target, ASTNode math, SId rangeReference, SId modelReference) {
        super(id, name, target, math);
        this.rangeReference = rangeReference;
        this.modelReference = modelReference;
    }

    public SetValue clone() throws CloneNotSupportedException {
        SetValue clone = (SetValue) super.clone();
        clone.modelReference = new SId(this.modelReference.string());
        clone.rangeReference = new SId(this.rangeReference.string());
        return clone;
    }
    
    public void setRangeReference(SId rangeReference) {
        this.rangeReference = rangeReference;
    }
    public SId getRangeReference() {
        return this.rangeReference;
    }
    public void setModelReference(SId modelReference) {
        this.modelReference = modelReference;
    }
    public SId getModelReference() {
        return this.modelReference;
    }

    @Override
    public String getChangeKind() {
        return SedMLTags.SET_VALUE_KIND;
    }

    @Override
    public String getElementName() {
        return SedMLTags.SET_VALUE;
    }

    @Override
    public String parametersToString(){
        List<String> params = new ArrayList<>();
        params.add(String.format("rangeId=%s", this.rangeReference.string()));
        params.add(String.format("modelId=%s", this.modelReference.string()));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement){
        return super.searchFor(idOfElement);
    }
}
