package org.jlibsedml;

import java.util.ArrayList;
import java.util.List;

public class VectorRange extends Range {

    private List<Double> values = new ArrayList<Double> ();
    
    public VectorRange(String id, List<Double> values) {
        super(id);
        if(values != null) {
            this.values = values;
        }
    }
    public VectorRange(String id) {
        super(id);
    }
    
    public void addValue(Double value) {
        values.add(value);
    }
    
    @Override
    public int getNumElements() {
        return values.size();
    }

    @Override
    public double getElementAt(int index) {
        return values.get(index);
    }

    @Override
    public String toString() {
        return "Vector Range ["
        + "getId()=" + getId()
        + ", values.size()=" + values.size()
        + "]";
    }

    @Override
    public String getElementName() {
        return SEDMLTags.VECTOR_RANGE_TAG;
    }

    @Override
    public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }

}
