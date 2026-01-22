package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.algorithm.AlgorithmParameter;

public class ListOfAlgorithmParameters extends ListOf<AlgorithmParameter> {
    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.ALGORITHM_PARAMETERS;
    }
}
