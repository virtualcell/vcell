package org.jlibsedml;

/**
 * Encapsulates an AddXML element in SED-ML. This requires the value of the 
 * 'target' attribute to refer to  a parent or container element into which the XML will be added.
 * * @author anu/radams
 * 
 */
public final class AddXML extends Change {
	@Override
	public String toString() {
		return "AddXML [AddXML=" + newXML + ", getTarget()=" + getTargetXPath()
				+ "]";
	}
	
	 public boolean accept(SEDMLVisitor visitor) {
        
             return visitor.visit(this);
         
       }

	private NewXML newXML = null;

	/**
	 * 
	 * @param target
	 *            A non-null <code>XPathTarget</code> of the XPath target
	 * @param newXML
	 *            A non-null <code>NewXML</code> of new XML
	 * @throws IllegalArgumentException
	 *             if either argument is <code>null</code>.
	 */
	public AddXML(XPathTarget target, NewXML newXML) {
		super(target);
		if(SEDMLElementFactory.getInstance().isStrictCreation())
			Assert.checkNoNullArgs(newXML);
		this.newXML = newXML;
	}

	/**
	 * Getter for the change kind.
	 * @return SEDMLTags.ADD_XML_KIND
	 */
	@Override
	public String getChangeKind() {
		return SEDMLTags.ADD_XML_KIND;
	}

	/**
	 * Getter for the new XML to be added to the target.
	 * 
	 * @return the {@link NewXML} to be added.
	 */
	public NewXML getNewXML() {
		return newXML;
	}

	@Override
	public String getElementName() {
		return SEDMLTags.ADD_XML;
	}
	
	/**
	 * Sets the NewXML for this element.
	 * @param newXML A non-null {@link NewXML} object.
	 * @since 1.2.0
	 */
    public void setNewXML(NewXML newXML) {
        this.newXML = newXML;
    }
}
