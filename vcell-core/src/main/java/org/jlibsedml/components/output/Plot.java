package org.jlibsedml.components.output;

import org.jlibsedml.components.SId;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class Plot extends Output {
    protected Boolean useLegend;
    protected Double plotHeight;
    protected Double plotWidth;

    protected XAxis xAxis;
    protected YAxis yAxis;

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

    public Plot(SId id, String name, Boolean useLegend, Double plotHeight, Double plotWidth, XAxis xAxis, YAxis yAxis) {
        super(id, name);
        this.useLegend = useLegend;
        this.plotHeight = plotHeight;
        this.plotWidth = plotWidth;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public Boolean getUseLegend() {
        return this.useLegend;
    }

    public void setUseLegend(Boolean useLegend) {
        this.useLegend = useLegend;
    }

    public Double getPlotHeight() {
        return this.plotHeight;
    }

    public void setPlotHeight(Double plotHeight) {
        this.plotHeight = plotHeight;
    }

    public Double getPlotWidth() {
        return this.plotWidth;
    }

    public void setPlotWidth(Double plotWidth) {
        this.plotWidth = plotWidth;
    }

    public XAxis getXAxis() {
        return this.xAxis;
    }

    public void setXAxis(XAxis xAxis) {
        this.xAxis = xAxis;
    }

    public YAxis getYAxis() {
        return this.yAxis;
    }

    public void setYAxis(YAxis yAxis) {
        this.yAxis = yAxis;
    }

    @OverridingMethodsMustInvokeSuper
    public Boolean xAxisShouldBeLogarithmic(){
        if (this.xAxis != null) return this.xAxis.getType() == Axis.Type.LOG10;
        return null; // Note that the subclasses should handle the deprecated way to check for this...but should still call this!!!
    }

    @OverridingMethodsMustInvokeSuper
    public Boolean yAxisShouldBeLogarithmic(){
        if (this.yAxis != null) return this.yAxis.getType() == Axis.Type.LOG10;
        return null; // Note that the subclasses should handle the deprecated way to check for this...but should still call this!!!
    }
}
