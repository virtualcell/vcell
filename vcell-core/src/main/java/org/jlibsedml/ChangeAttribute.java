package org.jlibsedml;

/**
 * Class for manipulating the value of an attribute via XPath.
 * @author anu/radams
 *
 */
public final  class ChangeAttribute extends Change {


	private String newValue = null;

	   /**
	    * 
	    * @param target An {@link XPathTarget} to an attribute whose value is to be changed.
	    * @param newValue The new value of  <code>target</code> attribute.
	    * @throws IllegalArgumentException if either argument is null or empty.
	    */
	   public ChangeAttribute(XPathTarget target, String newValue) {
		   super(target);
		   if(SEDMLElementFactory.getInstance().isStrictCreation()) {
		   Assert.checkNoNullArgs(newValue);
		   Assert.stringsNotEmpty(newValue);
		   }
		   this.newValue = newValue;
	   }
	   
	   public boolean accept(SEDMLVisitor visitor) {
	        
           return visitor.visit(this);
       
     }
	   
	   /**
	     * Getter for the change kind.
	     * @return SEDMLTags.CHANGE_ATTRIBUTE_KIND;
	     */
	   @Override
	   public String getChangeKind() {
		   return SEDMLTags.CHANGE_ATTRIBUTE_KIND;
	   }
	   
	   /**
	    * Getter for the new attribute value to apply to  the target expression.
	    * @return A non-null, non-empty <code>String</code>.
	    */
	   public String getNewValue() {
		   return newValue;
	   }
	   
	   @Override
		public String toString() {
			return "ChangeAttribute [newValue=" + newValue + ", getTarget()="
					+ getTargetXPath() + "]";
		}

	/**
	 * Setter for the new value of the this  object.
	 * @param newValue A non-null, non-empty <code>String</code>.
	 * @throws IllegalArgumentException if <code>newValue </code>is <code>null</code> or empty.
	 * @since 1.2.0
	 */
	public void setNewValue(String newValue) {
	    if(SEDMLElementFactory.getInstance().isStrictCreation()) {
	           Assert.checkNoNullArgs(newValue);
	           Assert.stringsNotEmpty(newValue);
	           }
	           this.newValue = newValue;
    }

    @Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return SEDMLTags.CHANGE_ATTRIBUTE;
	}
}
