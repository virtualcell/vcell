package org.vcell.cli.run.plotting;

import java.util.List;

/**
 * "Record" class for storing label and data combos
 * <br/>
 * NOTE: This class can not realistically use `double[]`, because there is no direct immutable array type in java
 * (except direct initializer; not useful at runtime). So we instead use UnmodifiableCollections.
 * But that means extra-legwork making this happen.
 */
public class SingleAxisSeries {
    final String immutableLabel;
    final List<Double> immutableData;
    final String STRING_REPRESENTATION;

    /**
     * Record class for storing label and data combos
     * <br/>
     * NOTE: This class can not realistically use `double[]`, because there is no direct immutable array type in java
     * (except direct initializer; not useful at runtime). So we instead use UnmodifiableCollections.
     * But that means extra-legwork making this happen.
     * @param label the label to assign to the data in a plot
     * @param data the ***IMMUTABLE COLLECTION*** of data to be represented.
     */
    public SingleAxisSeries(String label, List<Double> data) {
        if (label == null) throw new IllegalArgumentException("Argument for `label` can not be null!");
        if (data == null) throw new IllegalArgumentException("Argument for `data` can not be null!");
        // We need to verify the data provided was provided as immutable!
        try {
            data.add(0.0);
            throw new IllegalArgumentException("Provided data list can *not* be mutable. Use `Collections.unmodifiableList(List.copyOf())` on the data!");
        } catch (UnsupportedOperationException ignored) {
            // Perfect! this is what we want!
        }
        this.immutableLabel = label; // Strings are one of the few objects classified as pass by value
        this.immutableData = data;
        StringBuilder stringRep = new StringBuilder();
        stringRep.append(label).append("(length:").append(data.size()).append(")::[");
        for (Double d : data) stringRep.append(d).append(",");
        this.STRING_REPRESENTATION = stringRep.deleteCharAt(stringRep.length() - 1).append(']').toString();
    }

    public String label(){
        return this.immutableLabel;
    }

    public List<Double> data(){
        return this.immutableData;
    }

    @Override
    public String toString(){
        return this.STRING_REPRESENTATION;
    }

    @Override
    public int hashCode(){
        // By adding the length of the data in the StringRepresentation, no "label" could emulate the data, and thus this is safe.
        return this.STRING_REPRESENTATION.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if (!(other instanceof SingleAxisSeries sas)) return false;
        return this.STRING_REPRESENTATION.equals(sas.STRING_REPRESENTATION);
    }
}
