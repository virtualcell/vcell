package org.jlibsedml;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for ChangeXXX classes that manipulate a {@link Model}. 
 * This class is not intended to be sub-classed by clients.
 * @author anu/radams
 *
 */
public abstract class Change extends SEDBase {
	   private XPathTarget target = null;
	   
	   /**
	    * Sorts a list of Changes into the correct order specified in the schema. 
	    * @author radams
	    *
	    */
	   static class ChangeComparator implements Comparator<Change>{
	       static Map<String, Integer> changeKindOrder;
	       static {
          changeKindOrder = new HashMap<String, Integer>();
          changeKindOrder.put(SEDMLTags.CHANGE_ATTRIBUTE_KIND, 1);
          changeKindOrder.put(SEDMLTags.CHANGE_XML_KIND, 2);
          changeKindOrder.put(SEDMLTags.ADD_XML_KIND, 3);
          changeKindOrder.put(SEDMLTags.REMOVE_XML_KIND, 4);
          changeKindOrder.put(SEDMLTags.COMPUTE_CHANGE_KIND, 5);
          changeKindOrder.put(SEDMLTags.SET_VALUE_KIND, 6);
	       }
        public int compare(Change o1, Change o2) {
            return changeKindOrder.get(o1.getChangeKind()).compareTo(changeKindOrder.get(o2.getChangeKind()));
        }
	   }

	   /**
	    * 
	    * @param target  A non-null, non-empty <code>String</code>
	    *   that identifies an XPath expression to change.
	    */
	   public   Change(XPathTarget target) {
		   if(SEDMLElementFactory.getInstance().isStrictCreation())
			   Assert.checkNoNullArgs(target);
		  
		   this.target = target;
	   }
	   
	   /**
	    * Gets the XPath expression that identifies the target XML to which the change will be applied.
	    * @return An XPath <code>String</code>.
	    */
	   public final XPathTarget getTargetXPath() {
		   return target;
	   }
   
	   /**
	    * Type definition of the type of change to be applied.<br/>
	    * Returns one of:
	    * <ul>
	    * <li>SEDMLTags.CHANGE_ATTRIBUTE_KIND
	    * <li>SEDMLTags.CHANGE_XML_KIND
	    * <li>SEDMLTags.ADD_XML_KIND
	    * <li>SEDMLTags.REMOVE_XML_KIND
        * <li>SEDMLTags.COMPUTE_CHANGE_KIND
        * <li>SET_VALUE_KIND
	    * </ul>
	    * @return a <code>String</code> for the type of change element.
	    */
	   public abstract String getChangeKind();
	   
	   /**
	    * Boolean test for whether this object is of type <code>ChangeAttribute</code>.
	    * @return a boolean
	    */
	   public boolean isChangeAttribute(){
	       return SEDMLTags.CHANGE_ATTRIBUTE_KIND.equals(getChangeKind());
	   }
	   /**
        * Boolean test for whether this object is of type <code>ChangeXML</code>.
        * @return a boolean
        */
	   public boolean isChangeXML(){
           return SEDMLTags.CHANGE_XML_KIND.equals(getChangeKind());
       }
	   /**
        * Boolean test for whether this object is of type <code>AddXML</code>.
        * @return a boolean
        */
	   public boolean isAddXML(){
           return SEDMLTags.ADD_XML_KIND.equals(getChangeKind());
       }
	   /**
        * Boolean test for whether this object is of type <code>RemoveXML</code>.
        * @return a boolean
        */
	   public boolean isRemoveXML(){
           return SEDMLTags.REMOVE_XML_KIND.equals(getChangeKind());
       }
	   /**
        * Boolean test for whether this object is of type <code>ComputeChange</code>.
        * @return a boolean
        */
	   public boolean isComputeChange(){
           return SEDMLTags.COMPUTE_CHANGE_KIND.equals(getChangeKind());
       }
	   public boolean isSetValue() {
	       return SEDMLTags.SET_VALUE_KIND.equals(getChangeKind());
	   }
   
	/**
	 * Setter for the target XPath expression that identifies where the change should be
	 *  applied.
	 * @param target A non-null {@link XPathTarget}
	 * @since 1.2.0
	 */
    public void setTarget(XPathTarget target) {
        if(SEDMLElementFactory.getInstance().isStrictCreation())
            Assert.checkNoNullArgs(target);
        this.target = target;
    }
	   
	   
}