package org.jlibsedml.components.task;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.components.SId;

import java.util.ArrayList;
import java.util.List;

public class VectorRange extends Range {

    private final List<Double> values;
    
    public VectorRange(SId id, List<Double> values) {
        this(id);
        if (values == null) return;
        this.values.addAll(values);
    }
    public VectorRange(SId id) {
        super(id);
        this.values = new ArrayList<>();
    }
    
    public void addValue(Double value) {
        this.values.add(value);
    }
    
    @Override
    public int getNumElements() {
        return this.values.size();
    }

    @Override
    public double getElementAt(int index) {
        return this.values.get(index);
    }

    @Override
    public String parametersToString(){
        List<String> valueStrings = this.values.stream().map(Object::toString).toList();
        return super.parametersToString() + ", " + String.format("values=[%s]", String.join(", ", valueStrings));
    }

    @Override
    public String getElementName() {
        return SedMLTags.VECTOR_RANGE_TAG;
    }

    @Override
    public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }

}
