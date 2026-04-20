package org.jlibsedml.components.task;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.model.Change;

import java.util.ArrayList;
import java.util.List;

public class VectorRange extends Range {
    private List<Double> values;

    public VectorRange(SId id) {
        this(id, List.of());
    }

    public VectorRange(SId id, String name) {
        this(id, name, List.of());
    }

    public VectorRange(SId id, List<Double> values) {
        this(id, null, values);
    }

    public VectorRange(SId id, String name, List<Double> values) {
        super(id, name);
        this.values = new ArrayList<>(values);
    }

    public VectorRange clone() throws CloneNotSupportedException {
        VectorRange clone = (VectorRange) super.clone();
        clone.values = new ArrayList<>(this.values);
        return clone;
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

    public List<Double> getElements(){
        return List.copyOf(this.values);
    }

    @Override
    public String getElementName() {
        return SedMLTags.VECTOR_RANGE_TAG;
    }

    @Override
    public String parametersToString(){
        List<String> valueStrings = this.values.stream().map(Object::toString).toList();
        return super.parametersToString() + ", " + String.format("values=[%s]", String.join(", ", valueStrings));
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }
}
