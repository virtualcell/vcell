package org.jlibsedml;

/**
 *  Encapsulates a changeXML element in SED-ML.  Currently this is achieved by replacing 
 *  the 'target' content element with the 'newXML' content. 
 * * @author anu/radams
 *
 */
public final class ChangeXML extends Change {
	   @Override
	public String toString() {
		return "ChangeXML [newXML=" + newXML + ", getTarget()=" + getTargetXPath()
				+ "]";
	}

	/**
	 * Setter for  the {@link NewXML} for this object.
	 * @param newXML A non-null {@link NewXML} object.
	 * @throws IllegalArgumentException if <code>newXML</code> is <code>null</code>.
	 * @since 1.2.0
	 */
	public void setNewXML(NewXML newXML) {
        this.newXML = newXML;
    }

    private NewXML newXML = null;

	   
	   /**
	    * 
	    * @param target A <code>XPathTarget</code>object
	    * @param newXML A <code>String</code> of new XML
	    */
	   public ChangeXML(XPathTarget target, NewXML newXML) {
		   super(target);
		   this.newXML = newXML;
	   }
	   
	   public boolean accept(SEDMLVisitor visitor) {
	        
           return visitor.visit(this);
       
     }
	   /**
        * Getter for the change kind.
        * @return SEDMLTags.CHANGE_XML_KIND;
        */
	   @Override
	   public String getChangeKind() {
		   return SEDMLTags.CHANGE_XML_KIND;
	   }
	   
	   /**
	    * Getter for the new XML that replaces the old XML.
	    * @return a NewXML object
	    */
	   public NewXML getNewXML() {
		   return newXML;
	   }

	@Override
	public String getElementName() {
		return SEDMLTags.CHANGE_XML;
	}
}
