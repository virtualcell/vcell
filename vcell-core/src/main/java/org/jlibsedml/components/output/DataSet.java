package org.jlibsedml.components.output;

import org.jlibsedml.*;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;
import org.jlibsedml.components.dataGenerator.DataGenerator;
import org.jlibsedml.components.model.Change;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the representation of a data set output from a task. * @author
 * anu/radams
 * 
 */
public final class DataSet extends SedBase {

    private String label;
    private SId dataReference; // DataGenerator.id

    /**
     *
     * @param id
     *            An identifier that is unique in this document.
     * @param name
     *            An optional name.
     * @param label
     *            to identify the data set in a report.
     * @param dataRef
     *            A <code>String</code> reference to the {@link DataGenerator}
     *            for this data set.
     * @throws IllegalArgumentException
     *             if any argument except <code>name</code> is null or empty.
     */
    public DataSet(SId id, String name, String label, SId dataRef) {
        super(id, name);
        if (SedMLElementFactory.getInstance().isStrictCreation()) {
            SedGeneralClass.checkNoNullArgs(id, label, dataRef);
            SedGeneralClass.stringsNotEmpty(label);
        }
        this.dataReference = dataRef;
        this.label = label == null ? "" : label;

    }

    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>();
        if (this.label != null) params.add(String.format("label=%s", this.label));
        params.add(String.format("dataReference=%s", this.dataReference.string()));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    /**
     * @return the label for this element.
     */
    public String getLabel() {
        return this.label;
    }

	/**
	 * Sets the label used to identify this DataSet.
	 * @param label A <code>String</code>.
	 * @since 1.2.0
	 */
	public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the reference to the {@link DataGenerator} used to create this
     *         data set.
     */
    public SId getDataReference() {
        return this.dataReference;
    }

	/**
	 * Sets the dataReference. This should be a DataGenerator reference.
	 * @param dataReference A non-null<code>String</code>.
	 * @since 1.2.0 
	 */
    public void setDataReference(SId dataReference) {
        this.dataReference = dataReference;
    }

	@Override
	public String getElementName() {
		return SedMLTags.OUTPUT_DATASET;
	}

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }
}
