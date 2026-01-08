package org.jlibsedml.components.model;

import org.jlibsedml.*;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;

/**
 * Abstract class for ChangeXXX classes that manipulate a {@link Model}. 
 * This class is not intended to be sub-classed by clients.
 * @author anu/radams
 *
 */
public abstract class Change extends SedBase {
	   private XPathTarget target;

	   /**
	    * 
	    * @param target  A non-null, non-empty <code>String</code>
	    *   that identifies an XPath expression to change.
	    */
	   public Change(SId id, String name, XPathTarget target) {
           super(id, name);
		   if(SedMLElementFactory.getInstance().isStrictCreation())
			  SedGeneralClass.checkNoNullArgs(target);
		  
		   this.target = target;
	   }

        /**
         * Setter for the target XPath expression that identifies where the change should be
         *  applied.
         * @param target A non-null {@link XPathTarget}
         * @since 1.2.0
         */
        public void setTarget(XPathTarget target) {
            if(SedMLElementFactory.getInstance().isStrictCreation())
                SedGeneralClass.checkNoNullArgs(target);
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
	       return SedMLTags.CHANGE_ATTRIBUTE_KIND.equals(getChangeKind());
	   }
	   /**
        * Boolean test for whether this object is of type <code>ChangeXML</code>.
        * @return a boolean
        */
	   public boolean isChangeXML(){
           return SedMLTags.CHANGE_XML_KIND.equals(getChangeKind());
       }
	   /**
        * Boolean test for whether this object is of type <code>AddXML</code>.
        * @return a boolean
        */
	   public boolean isAddXML(){
           return SedMLTags.ADD_XML_KIND.equals(getChangeKind());
       }
	   /**
        * Boolean test for whether this object is of type <code>RemoveXML</code>.
        * @return a boolean
        */
	   public boolean isRemoveXML(){
           return SedMLTags.REMOVE_XML_KIND.equals(getChangeKind());
       }
	   /**
        * Boolean test for whether this object is of type <code>ComputeChange</code>.
        * @return a boolean
        */
	   public boolean isComputeChange(){
           return SedMLTags.COMPUTE_CHANGE_KIND.equals(getChangeKind());
       }
	   public boolean isSetValue() {
	       return SedMLTags.SET_VALUE_KIND.equals(getChangeKind());
	   }
   


    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     * @return the parameters and their values, listed in string form
     */
    @OverridingMethodsMustInvokeSuper
    public String parametersToString(){
        if (this.target == null) return super.parametersToString();
        else return super.parametersToString() + ", target=[" + this.target.toString() + ']';
    }
	   
	   
}