package org.jlibsedml;

/**
 * Class encapsulating a RemoveXML element to be applied to a model.
 * @author radams
 *
 */
public class RemoveXML extends Change {

    /**
     * 
     * @param target A non-null {@link XPathTarget} object
     */
	public RemoveXML(XPathTarget target) {
		super(target);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getElementName() {
		return SEDMLTags.REMOVE_XML;
	}

	@Override
	public final String getChangeKind() {
		return SEDMLTags.REMOVE_XML_KIND;
	}
	
	 public boolean accept(SEDMLVisitor visitor) {
	        
         return visitor.visit(this);
     
   }

}
