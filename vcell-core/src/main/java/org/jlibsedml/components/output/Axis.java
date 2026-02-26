package org.jlibsedml.components.output;

import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;

import java.util.ArrayList;
import java.util.List;

public abstract class Axis extends SedBase {

    public enum Type {
        LINEAR("linear"),
        LOG10("log10");

        private final String tagStr;
        Type(String tagStr){
            this.tagStr = tagStr;
        }

        public String getTag() {
            return this.tagStr;
        }

        public static Type fromTag(String tagStr) {
            if ("linear".equals(tagStr)) return LINEAR;
            if ("log10".equals(tagStr)) return LOG10;
            throw new IllegalArgumentException("invalid tag: " + (tagStr == null ? "null" : tagStr));
        }
    }

    private Type type;
    private Double min, max;
    private Boolean grid;
    private SId styleId;
    private Boolean reverse;


    public Axis(SId id, String name, Type type) {
        this(id, name, type, null, null, null, null, null);
    }

    public Axis(SId id, String name, Type type, Double min, Double max, Boolean grid, SId styleId, Boolean reverse) {
        super(id, name);
        this.type = type;
        this.min = min;
        this.max = max;
        this.grid = grid;
        this.styleId = styleId;
        this.reverse = reverse;
    }

    public Axis clone() throws CloneNotSupportedException {
        Axis newAxis = (Axis) super.clone();
        newAxis.type = this.type;
        newAxis.min = this.min;
        newAxis.max = this.max;
        newAxis.grid = this.grid;
        newAxis.styleId = this.styleId;
        newAxis.reverse = this.reverse;
        return newAxis;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Double getMin() {
        return this.min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return this.max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Boolean getGrid() {
        return this.grid;
    }

    public void setGrid(Boolean grid) {
        this.grid = grid;
    }

    public SId getStyleId() {
        return this.styleId;
    }

    public void setStyleId(SId styleId) {
        this.styleId = styleId;
    }

    public Boolean getReverse() {
        return this.reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>();
        params.add(String.format("type=%s", this.type));
        if (this.min != null) params.add(String.format("min=%s", this.min));
        if (this.max != null) params.add(String.format("max=%s", this.max));
        if (this.grid != null) params.add(String.format("grid=%s", this.grid));
        if (this.styleId != null) params.add(String.format("styleId=%s", this.styleId.string()));
        if (this.reverse != null) params.add(String.format("reverse=%s", this.reverse));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    public abstract String getAxisTagName();

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return this.getAxisTagName();
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }
}
