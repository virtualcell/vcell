package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.Parameter;

public class ListOfParameters extends ListOf<Parameter> {
    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.PARAMETERS;
    }

    public ListOfParameters clone () throws CloneNotSupportedException {
        return (ListOfParameters) super.clone();
    }
}
