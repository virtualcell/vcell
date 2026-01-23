package org.jlibsedml.components.output;

import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.listOfConstructs.ListOfCurves;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Encapsulates the Plot2d Sed-ML element.
 *
 * @author anu/radams
 *
 */
public class Plot2D extends Plot {
    private RightYAxis rightYAxis;
    private ListOfCurves listOfCurves;

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name) {
        this(id, name, null, new ListOfCurves());
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name, Boolean useLegend, Double plotHeight, Double plotWidth) {
        this(id, name, null, new ListOfCurves(), useLegend, plotHeight, plotWidth);

    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name, Boolean useLegend, Double plotHeight, Double plotWidth, XAxis xAxis, YAxis yAxis){
        this(id, name, null, new ListOfCurves(), useLegend, plotHeight, plotWidth, xAxis, yAxis);
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name, RightYAxis rightYAxis, ListOfCurves listOfCurves) {
        super(id, name);
        this.rightYAxis = rightYAxis;
        this.listOfCurves = listOfCurves;
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name, RightYAxis rightYAxis, ListOfCurves listOfCurves, Boolean useLegend, Double plotHeight, Double plotWidth) {
        super(id, name, useLegend, plotHeight, plotWidth);
        this.rightYAxis = rightYAxis;
        this.listOfCurves = listOfCurves;
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name, RightYAxis rightYAxis, ListOfCurves listOfCurves, Boolean useLegend, Double plotHeight, Double plotWidth, XAxis xAxis, YAxis yAxis) {
        super(id, name, useLegend, plotHeight, plotWidth, xAxis, yAxis);
        this.rightYAxis = rightYAxis;
        this.listOfCurves = listOfCurves;
    }

    public Plot2D clone() throws CloneNotSupportedException {
        Plot2D clone = (Plot2D) super.clone();
        clone.rightYAxis = this.rightYAxis == null ? null : this.rightYAxis.clone();
        clone.listOfCurves = this.listOfCurves.clone();
        return clone;
    }

    public RightYAxis getRightYAxis() {
        return this.rightYAxis;
    }

    public void setRightYAxis(RightYAxis rightYAxis) {
        this.rightYAxis = rightYAxis;
    }

    /**
     * Gets a read-only list of Curves contained in this element.
     *
     * @return A possibly empty but non-null {@link List} of {@link Curve} elements.
     */
    public ListOfCurves getListOfCurves() {
        return this.listOfCurves;
    }

    /**
     * Gets a read-only list of Curves contained in this element.
     *
     * @return A possibly empty but non-null {@link List} of {@link Curve} elements.
     */
    public List<AbstractCurve> getCurves() {
        return this.listOfCurves.getContents();
    }

    /**
     * Gets the type of this output.
     *
     * @return SEDMLTags.PLOT2D_KIND
     */
    public String getKind() {
        return SedMLTags.PLOT2D_KIND;
    }

    /**
     * Gets a {@link List} of all {@link DataGenerator} identifiers used in this output.<br/>
     * This list will contain only unique entries; the same {@link DataGenerator} id will not appear
     * twice in this output.
     *
     * @return A possibly empty but non-null {@link List} of {@link DataGenerator} id values.
     */
    @Override
    public Set<SId> getAllDataGeneratorReferences() {
        Set<SId> refs = new LinkedHashSet<>();
        for (AbstractCurve absCurve : this.getCurves()) {
            refs.add(absCurve.getXDataReference());
            if (absCurve instanceof Curve curve) {
                refs.add(curve.getYDataReference());
                refs.add(curve.getXErrorUpper());
                refs.add(curve.getXErrorLower());
                refs.add(curve.getYErrorUpper());
                refs.add(curve.getYErrorLower());
            }
            // TODO: Add code here if other type of Curve
        }
        return refs;
    }

    /**
     * Adds a {@link AbstractCurve} to this object's list of Curves, if not already present.
     *
     * @param curve A non-null {@link AbstractCurve} element
     */
    public void addCurve(AbstractCurve curve) {
        this.listOfCurves.addContent(curve);
    }

    /**
     * Removes a {@link AbstractCurve} from this object's list of Curves, if not already present.
     *
     * @param curve A non-null {@link AbstractCurve} element
     */
    public void removeCurve(AbstractCurve curve) {
        this.listOfCurves.removeContent(curve);
    }

    @OverridingMethodsMustInvokeSuper
    public Boolean xAxisShouldBeLogarithmic(){
        Boolean superResult = super.xAxisShouldBeLogarithmic();
        if (superResult != null) return superResult;
        Set<Boolean> logRequestInCurves = this.listOfCurves.getContents().stream().map(AbstractCurve::getLogScaleXAxis).collect(Collectors.toSet());
        if (logRequestInCurves.size() != 1) throw new IllegalArgumentException("Inconsistent curve requests");
        Boolean request = logRequestInCurves.stream().toList().get(0);
        return request != null && request;
    }

    @OverridingMethodsMustInvokeSuper
    public Boolean yAxisShouldBeLogarithmic(){
        Boolean superResult = super.yAxisShouldBeLogarithmic();
        if (superResult != null) return superResult;
        Set<Boolean> logRequestInCurves = this.listOfCurves.getContents().stream().filter(Curve.class::isInstance).map(Curve.class::cast).map(Curve::getLogScaleYAxis).collect(Collectors.toSet());
        if (logRequestInCurves.size() != 1) throw new IllegalArgumentException("Inconsistent curve requests");
        Boolean request = logRequestInCurves.stream().toList().get(0);
        return request != null && request;
    }

    @Override
    public String getElementName() {
        return SedMLTags.OUTPUT_P2D;
    }

    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>(), curveParams =  new ArrayList<>();
        if (this.rightYAxis != null) params.add(String.format("rightYAxis=%s", this.rightYAxis.getId() != null ? this.rightYAxis.getId().string() : '{' + this.rightYAxis.parametersToString() + '}'));
        for (AbstractCurve curve: this.getCurves())
            curveParams.add(String.format("%s", curve.getId() != null ? curve.getId().string() : '{' + curve.parametersToString() + '}'));
        params.add(String.format("curves=[%s]", String.join(", ", curveParams)));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        for (AbstractCurve var : this.getCurves()) {
            elementFound = var.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        return elementFound;
    }

}
