package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.dataGenerator.DataGenerator;

public class ListOfDataGenerators extends ListOf<DataGenerator> {
    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.DATA_GENERATORS;
    }
}
