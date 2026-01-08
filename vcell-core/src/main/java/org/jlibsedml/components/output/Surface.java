package org.jlibsedml.components.output;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.SedMLElementFactory;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;
import org.jlibsedml.components.dataGenerator.DataGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the {@link Surface} element in SED-ML for representing an element of a 3-dimensional plot.
 *
 */
public final class Surface extends SedBase {
    public enum Type {
        PARAMETRIC_CURVE,
        SURFACE_MESH,
        SURFACE_CONTOUR,
        CONTOUR,
        HEATMAP,
        BAR
    }

    private SId xDataReference;
    @Deprecated private Boolean logScaleXAxis;
    private SId yDataReference;
    @Deprecated private Boolean logScaleYAxis;
    private SId zDataReference;
    @Deprecated private Boolean logScaleZAxis;
    private Integer order;
    private SId style;
    private Type type;



    /**
     *
     * @param id A <code>String</code> identifier that is unique in this document.
     * @param name An optional <code>String</code> name
     * @param xDataReference A {@link SId} reference to the {@link DataGenerator} for the x-axis.
     * @param yDataReference  A {@link SId} reference to the {@link DataGenerator} for the y-axis.
     * @param zDataReference  A {@link SId} reference to the {@link DataGenerator} for the z-axis.
     * @param logScaleXAxis  <code>boolean</code> as to whether x-axis is a log scale.
     * @param logScaleYAxis	<code>boolean</code> as to whether y-axis is a log scale.
     * @param logScaleZAxis	<code>boolean</code> as to whether z-axis is a log scale.
     *
     * @throws IllegalArgumentException if any argument except <code>name</code> is null or empty.
     */
    public Surface(SId id, String name, SId xDataReference, SId yDataReference, SId zDataReference,
                   Boolean logScaleXAxis, Boolean logScaleYAxis, Boolean logScaleZAxis) {
        super(id, name);
        if(SedMLElementFactory.getInstance().isStrictCreation()){
            SedGeneralClass.checkNoNullArgs(xDataReference, yDataReference, zDataReference);
        }
        this.xDataReference = xDataReference;
        this.logScaleXAxis = logScaleXAxis;
        this.yDataReference = yDataReference;
        this.logScaleYAxis = logScaleYAxis;
        this.zDataReference = zDataReference;
        this.logScaleZAxis = logScaleZAxis;
    }

    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>();
        params.add(String.format("xDataReference=%s", this.xDataReference.string()));
        params.add(String.format("yDataReference=%s", this.yDataReference.string()));
        params.add(String.format("zDataReference=%s", this.zDataReference.string()));
        if (null != this.logScaleXAxis) params.add(String.format("logX=%s", this.logScaleXAxis));
        if (null != this.logScaleYAxis) params.add(String.format("logY=%s", this.logScaleYAxis));
        if (null != this.logScaleYAxis) params.add(String.format("logZ=%s", this.logScaleZAxis));
        if (null != this.order) params.add(String.format("order=%s", this.order));
        if (null != this.style) params.add(String.format("style=%s", this.style.string()));
        return super.parametersToString() + ", " + String.join(", ", params);
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

    /**
     * @return the reference to the {@link DataGenerator} for the y-axis
     */
    public SId getYDataReference() {
        return this.yDataReference;
    }

    /**
     * Setter for the y-axis  data generator.
     * @param yDataReference A non-null <code>String</code> that is an identifier of a {@link DataGenerator}
     *  element.
     * @since 1.2.0
     */
    public void setyDataReference(SId yDataReference) {
        this.yDataReference = yDataReference;
    }

    public SId getZDataReference() {
        return this.zDataReference;
    }

    /**
     * Sets the z Data Reference for this object.
     * @param zDataReference A non-null, non empty <code>String</code> that should
     *  refer to a {@link DataGenerator} identifier.
     *  @since 1.2.0
     */
    public void setzDataReference(SId zDataReference) {
        this.zDataReference = zDataReference;
    }

    public Boolean getLogScaleXAxis() {
        return this.logScaleXAxis;
    }

    public void setLogScaleXAxis(Boolean logScaleXAxis) {
        this.logScaleXAxis = logScaleXAxis;
    }

    /**
     * @return <code>true</code> if the y-axis is a log scale, <code>false</code> otherwise.
     */
    public Boolean getLogScaleYAxis() {
        return this.logScaleYAxis;
    }

    /**
     * Setter for whether the y-axis of this curve is on a log scale.
     * @param logScaleYAxis A <code>boolean</code>.
     * @since 1.2.0
     */
    public void setLogScaleYAxis(Boolean logScaleYAxis) {
        this.logScaleYAxis = logScaleYAxis;
    }

    /**
     * @return <code>true</code> if the y-axis is a log scale, <code>false</code> otherwise.
     */
    public Boolean getLogScaleZAxis() {
        return this.logScaleYAxis;
    }

    /**
     * Setter for whether the y-axis of this curve is on a log scale.
     * @param logScaleYAxis A <code>boolean</code>.
     * @since 1.2.0
     */
    public void setLogScaleZAxis(Boolean logScaleYAxis) {
        this.logScaleYAxis = logScaleYAxis;
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

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    
    @Override
	public String getElementName() {
		return SedMLTags.OUTPUT_SURFACE;
	}


	/**
	 * @return the reference to the {@link DataGenerator} for the z-axis
	 */
    public SId getzDataReference() {
	   return this.zDataReference;
	}

	
	public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }
}

