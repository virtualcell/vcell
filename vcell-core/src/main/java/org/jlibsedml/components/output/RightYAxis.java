package org.jlibsedml.components.output;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;

public class RightYAxis extends Axis {
    public RightYAxis(SId id, String name, Axis.Type type) {
        super(id, name, type);
    }

    public RightYAxis(SId id, String name, Axis.Type type, Double min, Double max, Boolean grid, SId styleId, Boolean reverse) {
        super(id, name, type, min, max, grid, styleId, reverse);
    }

    public RightYAxis clone() throws CloneNotSupportedException {
        return (RightYAxis) super.clone();
    }

    @Override
    public String getAxisTagName() {
        return SedMLTags.AXIS_RIGHT_Y;
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }

    @Override
    public String parametersToString() {
        return super.parametersToString();
    }
}
