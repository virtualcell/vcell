package org.jlibsedml.components.output;

import org.jlibsedml.components.SId;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Plot extends Output {
    protected Boolean useLegend;
    protected Double plotHeight;
    protected Double plotWidth;

    protected Axis xAxis;
    protected Axis yAxis;

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot(SId id, String name) {
        this(id, name, null, null, null);
    }

    public Plot(SId id, String name, Boolean useLegend, Double plotHeight, Double plotWidth) {
        this(id, name, useLegend, plotHeight, plotWidth, null, null);
    }

    public Plot(SId id, String name, Boolean useLegend, Double plotHeight, Double plotWidth, Axis xAxis, Axis yAxis) {
        super(id, name);
        this.useLegend = useLegend;
        this.plotHeight = plotHeight;
        this.plotWidth = plotWidth;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    @OverridingMethodsMustInvokeSuper
    public boolean xAxisShouldBeLogarithmic(){
        if (this.xAxis != null) return this.xAxis.getType() == Axis.Type.LOG10;
        return false; // Note that the subclasses should handle the deprecated way to check for this...but should still call this!!!
    }

    @OverridingMethodsMustInvokeSuper
    public boolean yAxisShouldBeLogarithmic(){
        if (this.yAxis != null) return this.yAxis.getType() == Axis.Type.LOG10;
        return false; // Note that the subclasses should handle the deprecated way to check for this...but should still call this!!!
    }
}
