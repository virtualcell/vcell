package org.jlibsedml.components.output;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.listOfConstructs.ListOfSurfaces;

import java.util.*;

/**
 * Encapsulates the information required for a 3d plot in SED-ML.
 * 
 * @author anu/radams
 * 
 */
public class Plot3D extends Plot {

    private final ListOfSurfaces listOfSurfaces;

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
     * Getter for a read-only list of Surfaces of this object.
     * @return list of {@link Surface}
     */
    public List<Surface> getListOfSurfaces() {
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
            rc.add(c.getzDataReference());
        }
        return rc;
    }

    @Override
    public String getElementName() {
        return SedMLTags.OUTPUT_P3D;
    }

}
