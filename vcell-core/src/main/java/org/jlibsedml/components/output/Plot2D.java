package org.jlibsedml.components.output;

import org.jlibsedml.components.SId;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.listOfConstructs.ListOfCurves;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the Plot2d Sed-ML element.
 *
 * @author anu/radams
 *
 */
public class Plot2D extends Plot {
    private Axis rightYAxis;
    private final ListOfCurves listOfCurves;

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
    public Plot2D(SId id, String name, Boolean useLegend, Double plotHeight, Double plotWidth, Axis xAxis, Axis yAxis){
        this(id, name, null, new ListOfCurves(), useLegend, plotHeight, plotWidth, xAxis, yAxis);
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name, Axis rightYAxis, ListOfCurves listOfCurves) {
        super(id, name);
        this.rightYAxis = rightYAxis;
        this.listOfCurves = listOfCurves;
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name, Axis rightYAxis, ListOfCurves listOfCurves, Boolean useLegend, Double plotHeight, Double plotWidth) {
        super(id, name, useLegend, plotHeight, plotWidth);
        this.rightYAxis = rightYAxis;
        this.listOfCurves = listOfCurves;
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot2D(SId id, String name, Axis rightYAxis, ListOfCurves listOfCurves, Boolean useLegend, Double plotHeight, Double plotWidth, Axis xAxis, Axis yAxis) {
        super(id, name, useLegend, plotHeight, plotWidth, xAxis, yAxis);
        this.rightYAxis = rightYAxis;
        this.listOfCurves = listOfCurves;
    }

    /**
     * Gets a read-only list of Curves contained in this element.
     *
     * @return A possibly empty but non-null {@link List} of {@link Curve} elements.
     */
    public List<AbstractCurve> getListOfCurves() {
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
        for (AbstractCurve absCurve : this.getListOfCurves()) {
            refs.add(absCurve.getXDataReference());
            if (absCurve instanceof Curve curve) {
                refs.add(curve.getYDataReference());
                refs.add(curve.getxErrorUpper());
                refs.add(curve.getxErrorLower());
                refs.add(curve.getyErrorUpper());
                refs.add(curve.getyErrorLower());
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

    @Override
    public String getElementName() {
        return SedMLTags.OUTPUT_P2D;
    }

}
