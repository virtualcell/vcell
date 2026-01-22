package org.jlibsedml.components.output;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;

public class XAxis extends Axis{
    public XAxis(SId id, String name, Type type) {
        super(id, name, type);
    }

    public XAxis(SId id, String name, Type type, Double min, Double max, Boolean grid, SId styleId, Boolean reverse) {
        super(id, name, type, min, max, grid, styleId, reverse);
    }

    @Override
    public String getAxisTagName() {
        return SedMLTags.AXIS_X;
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
