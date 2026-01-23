package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.output.Surface;

public class ListOfSurfaces extends ListOf<Surface> {

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.OUTPUT_SURFACES_LIST;
    }

    public ListOfSurfaces clone() throws CloneNotSupportedException {
        return (ListOfSurfaces) super.clone();
    }
}
