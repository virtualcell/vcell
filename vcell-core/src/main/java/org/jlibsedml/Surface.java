package org.jlibsedml;


/**
 * Encapsulates the {@link Surface} element in SED-ML for representing an element of a 3-dimensional plot.
 *
 */
public final class Surface extends AbstractIdentifiableElement{
    @Override
	public String toString() {
		return "Surface [logZ=" + logZ + ", zDataReference=" + zDataReference
				+ ", getId()=" + getId() + ", getLogX()=" + getLogX()
				+ ", getLogY()=" + getLogY() + ", getXDataReference()="
				+ getXDataReference() + ", getYDataReference()="
				+ getYDataReference() + "]";
	}

/**
 * Sets the x and y axes for this surface, using a <code>Curve</code>object.
 * @param curve A non-null <code>Curve</code>.
 * @since 1.2.0
 */
    public void setCurve(Curve curve) {
        this.curve = curve;
    }

    /**
     * Setter for whether the z-axis of this object should be on a log scale, or not.
     * @param logZ A <code>boolean</code>.
     * @since 1.2.0
     */
    public void setLogZ(boolean logZ) {
        this.logZ = logZ;
    }

    /**
     * Sets the z Data Reference for this object.
     * @param zDataReference A non-null, non empty <code>String</code> that should 
     *  refer to a {@link DataGenerator} identifier.
     *  @since 1.2.0
     */
    public void setZDataReference(String zDataReference) {
        this.zDataReference = zDataReference;
    }

    /**
     * Getter for the reference to the {@link DataGenerator} for the y-axis.
     * @return A <code>non-null String</code>
     */
    public String getYDataReference() {
		return curve.getYDataReference();
	}
    
    /**
     * Getter for the reference to the {@link DataGenerator} for the x-axis.
     * @return A <code>non-null String</code>
     */
    public String getXDataReference() {
		return curve.getXDataReference();
	}

    /**
     * Boolean test for whether the y-axis is on a log scale.
     * @return <code>true</code> if it is in a log-scale, <code>false</code> otherwise.
     */
    public boolean getLogY() {
		return curve.getLogY();
	}

    /**
     * Boolean test for whether the x-axis is on a log scale.
     * @return <code>true</code> if it is in a log-scale, <code>false</code> otherwise.
     */
    public boolean getLogX() {
		return curve.getLogX();
	}
    
    @Override
	public String getElementName() {
		return SEDMLTags.OUTPUT_SURFACE;
	}


	private Curve curve =null;
	private boolean logZ = false;
   
    private String zDataReference = null;	// DataGenerator.id

    /**
	 * 
	 * @param argId A <code>String</code> identifier that is unique in this document.
	 * @param argName An optional <code>String</code> name
	 * @param logX  <code>boolean</code> as to whether x-axis is a log scale.
	 * @param logY	<code>boolean</code> as to whether y-axis is a log scale.
	 * @param logZ	<code>boolean</code> as to whether z-axis is a log scale.
	 * @param xDataReference A <code>String</code> reference to the {@link DataGenerator} for the x-axis.
	 * @param yDataReference  A <code>String</code> reference to the {@link DataGenerator} for the y-axis.
	 * @param zDataReference  A <code>String</code> reference to the {@link DataGenerator} for the z-axis.
	 * @throws IllegalArgumentException if any argument except <code>name</code> is null or empty.
	 */
	public Surface(String argId, String argName, boolean logX, boolean logY, boolean logZ, String xDataReference, String yDataReference, String zDataReference) {
		super(argId,argName);
		curve = new Curve(argId, argName,logX, logY, xDataReference, yDataReference);
		if(SEDMLElementFactory.getInstance().isStrictCreation()){
		Assert.checkNoNullArgs(zDataReference,logZ);
		Assert.stringsNotEmpty(zDataReference);
		}
		this.logZ=logZ;
		this.zDataReference=zDataReference;
	}

   
	/**
	 * @return the reference to the {@link DataGenerator} for the z-axis
	 */
    public final String getZDataReference () {
	   return zDataReference;
	}
    
    /**
	 * @return <code>true</code> if the z-axis should be displayed on a  log scale, <code>false</code> otherwise.
	 */
	public boolean getLogZ() {
		return logZ;
	}
	
	public boolean accept(SEDMLVisitor visitor) {
        return visitor.visit(this);
    }
}

