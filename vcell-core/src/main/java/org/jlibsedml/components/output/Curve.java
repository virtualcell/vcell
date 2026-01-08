package org.jlibsedml.components.output;

import org.jlibsedml.*;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;
import org.jlibsedml.components.dataGenerator.DataGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Supports the SED-ML 'Curve' element representing a trace on a 2D Plot.
 */
public class Curve extends AbstractCurve {
    public enum Type {
        POINTS,
        BAR,
        BAR_STACKED,
        HORIZONTAL_BAR,
        HORIZONTAL_BAR_STACKED,
    }

    @Deprecated protected Boolean logScaleYAxis; // now a capital-B Boolean because deprecated
    protected SId yDataReference; // DataGenerator.id
    protected SId xErrorUpper, xErrorLower, yErrorUpper, yErrorLower;
    protected Type type;


    /**
     * @param id An identifier that is unique in this document.
     * @param name An optional name
     * @param xDataReference An {@link SId} reference to the {@link DataGenerator} for the x-axis.
     * @param yDataReference  An {@link SId} reference to the {@link DataGenerator} for the y-axis.
     * @throws IllegalArgumentException if any required argument is `null`
     */
    public Curve(SId id, String name, SId xDataReference, SId yDataReference){
        this(id, name, xDataReference, yDataReference, null, null, Type.POINTS, null, null, YAxisAlignment.NOT_APPLICABLE, null, null, null, null);
    }

    /**
     * @param id An identifier that is unique in this document.
     * @param name An optional name
     * @param logScaleXAxis  <code>boolean</code> as to whether x-axis is a log scale.
     * @param logScaleYAxis	<code>boolean</code> as to whether y-axis is a log scale.
     * @param xDataReference An {@link SId} reference to the {@link DataGenerator} for the x-axis.
     * @param yDataReference  An {@link SId} reference to the {@link DataGenerator} for the y-axis.
     * @param xErrorUpper An {@link SId} reference to the {@link DataGenerator} to be used as an upper-bounds line for the x-axis.
     * @param xErrorLower An {@link SId} reference to the {@link DataGenerator} to be used as a lower-bounds line for the x-axis.
     * @param yErrorUpper An {@link SId} reference to the {@link DataGenerator} to be used as an upper-bounds line for the y-axis.
     * @param yErrorLower An {@link SId} reference to the {@link DataGenerator} to be used as a lower-bounds line for the y-axis.
     * @throws IllegalArgumentException if any required argument is `null`, or <code>order</code> is a negative integer
     */
    public Curve(SId id, String name, SId xDataReference, SId yDataReference, Boolean logScaleXAxis, Boolean logScaleYAxis, Type type, Integer order, SId style, YAxisAlignment yAxis, SId xErrorUpper, SId xErrorLower, SId yErrorUpper, SId yErrorLower) {
        super(id, name, xDataReference, logScaleXAxis, order, style, yAxis);
        if (SedMLElementFactory.getInstance().isStrictCreation()) SedGeneralClass.checkNoNullArgs(xDataReference, yDataReference);
        this.logScaleYAxis = logScaleYAxis;
        this.yDataReference = yDataReference;
        this.type = type;
        this.xErrorUpper = xErrorUpper;
        this.xErrorLower = xErrorLower;
        this.yErrorUpper = yErrorUpper;
        this.yErrorLower = yErrorLower;
    }
    
    /**
     * @return <code>true</code> if the y-axis is a log scale, <code>false</code> otherwise.
     */
    public Boolean getLogY() {
        return this.logScaleYAxis;
    }
    
	/**
     * Setter for whether the y-axis of this curve is on a log scale.
     * @param logScaleYAxis A <code>boolean</code>.
     * @since 1.2.0
     */
    public void setLogY(Boolean logScaleYAxis) {
        this.logScaleYAxis = logScaleYAxis;
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

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public SId getxErrorUpper() {
        return this.xErrorUpper;
    }

    public void setxErrorUpper(SId xErrorUpper) {
        this.xErrorUpper = xErrorUpper;
    }

    public SId getxErrorLower() {
        return this.xErrorLower;
    }

    public void setxErrorLower(SId xErrorLower) {
        this.xErrorLower = xErrorLower;
    }

    public SId getyErrorUpper() {
        return this.yErrorUpper;
    }

    public void setyErrorUpper(SId yErrorUpper) {
        this.yErrorUpper = yErrorUpper;
    }

    public SId getyErrorLower() {
        return this.yErrorLower;
    }

    public void setyErrorLower(SId yErrorLower) {
        this.yErrorLower = yErrorLower;
    }

	@Override
	public String getElementName() {
		return SedMLTags.OUTPUT_CURVE;
	}
	
	public boolean accept(SEDMLVisitor visitor) {
	    return visitor.visit(this);
	}
    
    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>();
        params.add(String.format("xDataReference=%s", this.xDataReference.string()));
        params.add(String.format("yDataReference=%s", this.yDataReference.string()));
        if (null != this.logScaleXAxis) params.add(String.format("logX=%s", this.logScaleXAxis));
        if (null != this.logScaleYAxis) params.add(String.format("logY=%s", this.logScaleYAxis));
        if (null != this.order) params.add(String.format("order=%s", this.order));
        if (null != this.style) params.add(String.format("style=%s", this.style.string()));
        if (null != this.yAxis) params.add(String.format("yAxis=%s", this.yAxis));
        if (null != this.xErrorUpper) params.add(String.format("xErrorUpper=%s", this.xErrorUpper.string()));
        if (null != this.xErrorLower) params.add(String.format("xErrorLower=%s", this.xErrorLower.string()));
        if (null != this.yErrorUpper) params.add(String.format("yErrorUpper=%s", this.yErrorUpper.string()));
        if (null != this.yErrorLower) params.add(String.format("yErrorLower=%s", this.yErrorLower.string()));
        return super.parametersToString() + ", " + String.join(", ", params);
    }
}
