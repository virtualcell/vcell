package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.output.DataSet;

public class ListOfDataSets extends ListOf<DataSet>{
    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.OUTPUT_DATASETS_LIST;
    }

    public ListOfDataSets clone() throws CloneNotSupportedException {
        return (ListOfDataSets) super.clone();
    }
}
