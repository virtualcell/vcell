package org.jlibsedml;

/**
 * Supports the SED-ML 'Curve' element representing a trace on a 2D Plot.
 */
public   class Curve extends AbstractIdentifiableElement{
	
	@Override
	public String toString() {
		return "Curve [id=" + getId() + ", logX=" + logX + ", logY=" + logY
				+ ", name=" + getName() + ", xDataReference=" + xDataReference
				+ ", yDataReference=" + yDataReference + "]";
	}


	/**
	 * Setter for whether the x-axis of this curve is on a log scale.
	 * @param logX A <code>boolean</code>.
	 * @since 1.2.0
	 */
	public void setLogX(boolean logX) {
        this.logX = logX;
    }

	/**
     * Setter for whether the y-axis of this curve is on a log scale.
     * @param logY A <code>boolean</code>.
     * @since 1.2.0
     */
    public void setLogY(boolean logY) {
        this.logY = logY;
    }

    /**
     * Setter for the x-axis  data generator.
     * @param xDataReference A non-null <code>String</code> that is an identifier of a {@link DataGenerator}
     *  element.
     * @since 1.2.0
     */
    public void setxDataReference(String xDataReference) {
        if(SEDMLElementFactory.getInstance().isStrictCreation()){
            Assert.checkNoNullArgs( xDataReference);
            Assert.stringsNotEmpty( xDataReference);
            }
        this.xDataReference = xDataReference;
    }

    /**
     * Setter for the y-axis  data generator.
     * @param yDataReference A non-null <code>String</code> that is an identifier of a {@link DataGenerator}
     *  element.
     * @since 1.2.0
     */
    public void setyDataReference(String yDataReference) {
        this.yDataReference = yDataReference;
    }



    private boolean logX = false;
	private boolean logY = false;

	private String xDataReference = null; // DataGenerator.id
	private String yDataReference = null; // DataGenerator.id

	
	/**
	 * 
	 * @param argId An identifier that is unique in this document.
	 * @param argName An optional name
	 * @param logX  <code>boolean</code> as to whether x-axis is a log scale.
	 * @param logY	<code>boolean</code> as to whether y-axis is a log scale.
	 * @param xDataReference A <code>String</code> reference to the {@link DataGenerator} for the x-axis.
	 * @param yDataReference  A <code>String</code> reference to the {@link DataGenerator} for the y-axis.
	 * @throws IllegalArgumentException if any argument except <code>name</code> is null or empty.
	 */
	public Curve(String argId, String argName, boolean logX, boolean logY, String xDataReference, String yDataReference) {
		super(argId,argName);
		if(SEDMLElementFactory.getInstance().isStrictCreation()){
		Assert.checkNoNullArgs(argId, logX, logY, xDataReference, yDataReference);
		Assert.stringsNotEmpty(argId,  xDataReference, yDataReference);
		}
		this.logX = logX;
		this.logY = logY;
		this.xDataReference = xDataReference;
		this.yDataReference = yDataReference;
	}

	/**
	 * @return the reference to the {@link DataGenerator} for the x-axis
	 */
	public String getXDataReference() {
		return xDataReference;
	}

	/**
	 * @return the reference to the {@link DataGenerator} for the y-axis
	 */
	public String getYDataReference() {
		return yDataReference;
	}

	/**
	 * @return <code>true</code> if the x-axis is a log scale, <code>false</code> otherwise.
	 */
	public boolean getLogX() {
		return logX;
	}

	/**
	 * @return <code>true</code> if the y-axis is a log scale, <code>false</code> otherwise.
	 */
	public boolean getLogY() {
		return logY;
	}

	@Override
	public String getElementName() {
		return SEDMLTags.OUTPUT_CURVE;
	}
	
	public boolean accept(SEDMLVisitor visitor) {
	    return visitor.visit(this);
	}
}
