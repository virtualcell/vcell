package org.jlibsedml;

/**
 * Encapsulates the representation of a data set output from a task. * @author
 * anu/radams
 * 
 */
public final class DataSet extends AbstractIdentifiableElement {

	@Override
	public String toString() {
		return "DataSet [dataReference=" + dataReference + ", id=" + getId()
				+ ", label=" + label + ", name=" + getName() + "]";
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
	 * Sets the dataReference. This should be a DataGenerator reference.
	 * @param dataReference A non-null<code>String</code>.
	 * @since 1.2.0 
	 */
    public void setDataReference(String dataReference) {
        this.dataReference = dataReference;
    }

    private String label = null;
	private String dataReference = null; // DataGenerator.id

	/**
	 * 
	 * @param argId
	 *            An identifier that is unique in this document.
	 * @param argName
	 *            An optional name.
	 * @param label
	 *            to identify the data set in a report.
	 * @param dataRef
	 *            A <code>String</code> reference to the {@link DataGenerator}
	 *            for this data set.
	 * @throws IllegalArgumentException
	 *             if any argument except <code>name</code> is null or empty.
	 */
	public DataSet(String argId, String argName, String label, String dataRef) {
		super(argId, argName);
		if (SEDMLElementFactory.getInstance().isStrictCreation()) {
			Assert.checkNoNullArgs(argId, label, dataRef);
			Assert.stringsNotEmpty(argId, label, dataRef);
		}
		this.dataReference = dataRef;

		this.label = label;

	}

	/**
	 * @return the label for this element.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the reference to the {@link DataGenerator} used to create this
	 *         data set.
	 */
	public final String getDataReference() {
		return dataReference;
	}

	@Override
	public String getElementName() {
		return SEDMLTags.OUTPUT_DATASET;
	}
	
	public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }
	
	

}
