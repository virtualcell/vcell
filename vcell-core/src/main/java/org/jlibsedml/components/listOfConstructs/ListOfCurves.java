package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.output.AbstractCurve;

public class ListOfCurves extends ListOf<AbstractCurve> {

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.OUTPUT_CURVES_LIST;
    }

//    @Override
//    public void addContent(AbstractCurve content) {
//        if (null == content) return;
//
//        super.addContent(content);
//    }
}
