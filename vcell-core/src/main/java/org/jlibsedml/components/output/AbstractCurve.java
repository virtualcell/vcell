package org.jlibsedml.components.output;

import org.jlibsedml.SedMLElementFactory;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCurve extends SedBase {

    public enum YAxisAlignment {
        NOT_APPLICABLE,
        LEFT,
        RIGHT
    }

    protected SId xDataReference;
    @Deprecated protected Boolean logScaleXAxis;
    protected Integer order;
    protected SId style;
    protected YAxisAlignment yAxis;


    public AbstractCurve(SId id, String name, SId xDataReference) {
        this(id, name, xDataReference, null, null, null, YAxisAlignment.NOT_APPLICABLE);
    }

    public AbstractCurve(SId id, String name, SId xDataReference, Boolean logScaleXAxis, Integer order, SId style, YAxisAlignment yAxis) {
        super(id, name);
        if (order != null && order < 0) throw new IllegalArgumentException("order must be >= 0");
        this.xDataReference = xDataReference;
        this.logScaleXAxis = logScaleXAxis;
        this.order = order;
        this.style = style;
        this.yAxis = yAxis;
    }

    public SId getXDataReference() {
        return this.xDataReference;
    }

    public void setXDataReference(SId xDataReference) {
        if(SedMLElementFactory.getInstance().isStrictCreation()){
            SedGeneralClass.checkNoNullArgs( xDataReference);
        }
        this.xDataReference = xDataReference;
    }

    public Boolean getLogScaleXAxis() {
        return this.logScaleXAxis;
    }

    public void setLogScaleXAxis(Boolean logScaleXAxis) {
        this.logScaleXAxis = logScaleXAxis;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        if (order != null && order < 0) throw new IllegalArgumentException("order must be >= 0");
        this.order = order;
    }

    public SId getStyle() {
        return this.style;
    }

    public void setStyle(SId style) {
        this.style = style;
    }

    public YAxisAlignment getYAxis() {
        return this.yAxis;
    }

    public void setYAxis(YAxisAlignment yAxis) {
        this.yAxis = yAxis;
    }
}
