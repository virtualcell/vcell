package org.jlibsedml.components.output;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.listOfConstructs.ListOfSurfaces;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Encapsulates the information required for a 3d plot in SED-ML.
 * 
 * @author anu/radams
 * 
 */
public class Plot3D extends Plot {

    private ListOfSurfaces listOfSurfaces;
    private ZAxis zAxis;

    /**
     * 
     * @param id
     *            A unique <code>String</code> identifier for this element.
     * @param name
     *            An optional name.
     */
    public Plot3D(SId id, String name) {
        super(id, name);
        this.listOfSurfaces = new ListOfSurfaces();
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot3D(SId id, String name, Boolean useLegend, Double plotHeight, Double plotWidth) {
        this(id, name, new ListOfSurfaces(), useLegend, plotHeight, plotWidth);

    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot3D(SId id, String name, Boolean useLegend, Double plotHeight, Double plotWidth, XAxis xAxis, YAxis yAxis, ZAxis zAxis){
        this(id, name, new ListOfSurfaces(), useLegend, plotHeight, plotWidth, xAxis, yAxis, zAxis);
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot3D(SId id, String name, ListOfSurfaces listOfSurfaces) {
        this(id, name, listOfSurfaces, null, null, null);
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot3D(SId id, String name, ListOfSurfaces listOfSurfaces, Boolean useLegend, Double plotHeight, Double plotWidth) {
        this(id, name, listOfSurfaces, useLegend, plotHeight, plotWidth, null, null, null);
    }

    /**
     *
     * @param id   A unique id for this element in the document.
     * @param name An optional name for this element.
     */
    public Plot3D(SId id, String name, ListOfSurfaces listOfSurfaces, Boolean useLegend, Double plotHeight, Double plotWidth, XAxis xAxis, YAxis yAxis, ZAxis zAxis) {
        super(id, name, useLegend, plotHeight, plotWidth, xAxis, yAxis);
        this.zAxis = zAxis;
        this.listOfSurfaces = listOfSurfaces;
    }

    public Plot3D clone() throws CloneNotSupportedException {
        Plot3D clone = (Plot3D) super.clone();
        clone.listOfSurfaces = this.listOfSurfaces;
        clone.zAxis = this.zAxis.clone();
        return clone;
    }

    public ZAxis getZAxis() {
        return this.zAxis;
    }

    public void setZAxis(ZAxis zAxis) {
        this.zAxis = zAxis;
    }

    /**
     * Getter for a read-only list of Surfaces of this object.
     * @return list of {@link Surface}
     */
    public ListOfSurfaces getListOfSurfaces() {
        return this.listOfSurfaces;
    }

    /**
     * Getter for a read-only list of Surfaces of this object.
     * @return list of {@link Surface}
     */
    public List<Surface> getSurfaces() {
        return this.listOfSurfaces.getContents();
    }

    /**
     * Adds a {@link Surface} to this object's list of Surfaces, if not already
     * present.
     * 
     * @param surface
     *            A non-null {@link Surface} element
     */
    public void addSurface(Surface surface) {
        this.listOfSurfaces.addContent(surface);
    }

    /**
     * REmoves a {@link Surface} from this object's list of Surfaces, if not
     * already present.
     * 
     * @param surface
     *            A non-null {@link Surface} element
     */
    public void removeSurface(Surface surface) {
        this.listOfSurfaces.removeContent(surface);
    }

    /**
     * Gets the type of this output.
     * 
     * @return SEDMLTags.PLOT3D_KIND
     */
    public String getKind() {
        return SedMLTags.PLOT3D_KIND;
    }

    @Override
    public Set<SId> getAllDataGeneratorReferences() {
        Set<SId> rc = new LinkedHashSet<>();
        for (Surface c : this.listOfSurfaces.getContents()) {
            rc.add(c.getXDataReference());
            rc.add(c.getYDataReference());
            rc.add(c.getZDataReference());
        }
        return rc;
    }

    @OverridingMethodsMustInvokeSuper
    public Boolean xAxisShouldBeLogarithmic(){
        Boolean superResult = super.xAxisShouldBeLogarithmic();
        if (superResult != null) return superResult;
        return this.checkIfSurfacesRequestLogarithmic(Surface::getLogScaleXAxis);
    }

    @OverridingMethodsMustInvokeSuper
    public Boolean yAxisShouldBeLogarithmic(){
        Boolean superResult = super.yAxisShouldBeLogarithmic();
        if (superResult != null) return superResult;
        return this.checkIfSurfacesRequestLogarithmic(Surface::getLogScaleYAxis);
    }

    public Boolean zAxisShouldBeLogarithmic(){
        if (this.yAxis != null) return this.yAxis.getType() == Axis.Type.LOG10;
        return this.checkIfSurfacesRequestLogarithmic(Surface::getLogScaleZAxis);
    }

    private Boolean checkIfSurfacesRequestLogarithmic(java.util.function.Function<Surface, Boolean> mappingFunc){
        Set<Boolean> logRequestInCurves = this.listOfSurfaces.getContents().stream().map(mappingFunc).collect(Collectors.toSet());
        if (logRequestInCurves.size() != 1) throw new IllegalArgumentException("Inconsistent surface requests");
        Boolean request = logRequestInCurves.stream().toList().get(0);
        return request != null && request;
    }

    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>(), surfaceParams = new ArrayList<>();
        for (Surface surface : this.getSurfaces())
            surfaceParams.add(String.format("%s", surface.getId() != null ? surface.getId().string() : '{' + surface.parametersToString() + '}'));
        params.add(String.format("surfaces=[%s]", String.join(", ", surfaceParams)));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        for (Surface var : this.getSurfaces()) {
            elementFound = var.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        return elementFound;
    }

    @Override
    public String getElementName() {
        return SedMLTags.OUTPUT_P3D;
    }

}
