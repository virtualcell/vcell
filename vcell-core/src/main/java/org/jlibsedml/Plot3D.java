package org.jlibsedml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Encapsulates the information required for a 3d plot in SED-ML.
 * 
 * @author anu/radams
 * 
 */
public class Plot3D extends Output {

    @Override
    public String getElementName() {
        return SEDMLTags.OUTPUT_P3D;
    }

    @Override
    public String toString() {
        return "Plot3D [listOfSurfaces=" + listOfSurfaces + "]";
    }

    private ArrayList<Surface> listOfSurfaces;

    /**
     * 
     * @param id
     *            A unique <code>String</code> identifier for this element.
     * @param name
     *            An optional name.
     */
    public Plot3D(String id, String name) {
        super(id, name);
        listOfSurfaces = new ArrayList<Surface>();
    }

    /**
     * Getter for a read-only list of Surfaces of this object.
     * @return list of {@link Surface}
     */
    public List<Surface> getListOfSurfaces() {
        return Collections.unmodifiableList(listOfSurfaces);
    }

    /**
     * Adds a {@link Surface} to this object's list of Surfaces, if not already
     * present.
     * 
     * @param surface
     *            A non-null {@link Surface} element
     * @return <code>true</code> if surface added, <code>false </code>
     *         otherwise.
     */
    public boolean addSurface(Surface surface) {
        if (!listOfSurfaces.contains(surface))
            return listOfSurfaces.add(surface);
        return false;
    }

    /**
     * REmoves a {@link Surface} from this object's list of Surfaces, if not
     * already present.
     * 
     * @param surface
     *            A non-null {@link Surface} element
     * @return <code>true</code> if surface removed, <code>false </code>
     *         otherwise.
     */
    public boolean removeSurface(Surface surface) {

        return listOfSurfaces.remove(surface);

    }

    /**
     * Gets the type of this output.
     * 
     * @return SEDMLTags.PLOT3D_KIND
     */
    public String getKind() {
        return SEDMLTags.PLOT3D_KIND;
    }

    @Override
    public List<String> getAllDataGeneratorReferences() {

        Set<String> rc = new TreeSet<String>();
        for (Surface c : listOfSurfaces) {
            rc.add(c.getXDataReference());
            rc.add(c.getYDataReference());
            rc.add(c.getZDataReference());
        }
        List<String> rc2 = new ArrayList<String>();
        for (String id : rc) {
            rc2.add(id);
        }
        return rc2;

    }

    @Override
    public List<String> getAllIndependentDataGeneratorReferences() {
        Set<String> rc = new TreeSet<String>();
        for (Surface c : listOfSurfaces) {
            rc.add(c.getXDataReference());
        }
        List<String> rc2 = new ArrayList<String>();
        for (String id : rc) {
            rc2.add(id);
        }
        return rc2;
    }

}
