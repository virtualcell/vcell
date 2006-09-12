package cbit.xml.merge;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (8/7/2000 3:24:10 PM)
 * @author: 
 */
public class ChangedNodeInfo extends NodeInfo {
	private java.lang.String fieldModified = null;
/**
 * ChangedNodeInfo constructor comment.
 * @param newattr org.jdom.Attribute
 * @param newstatus short
 */
public ChangedNodeInfo(org.jdom.Attribute base,org.jdom.Attribute newmodified) {
	super(base, NodeInfo.STATUS_CHANGED);
	this.setModified(newmodified.getValue());
}
/**
 * ChangedNodeInfo constructor comment.
 * @param newelement org.jdom.Element
 * @param newstatus short
 */
public ChangedNodeInfo(org.jdom.Element base, org.jdom.Element modified) {
	super(base, NodeInfo.STATUS_CHANGED);
	this.setModified( modified.getTextTrim() );
}
/**
 * Gets the modified property (java.lang.String) value.
 * @return The modified property value.
 * @see #setModified
 */
public java.lang.String getModified() {
	return fieldModified;
}
/**
 * Sets the modified property (java.lang.String) value.
 * @param modified The new value for the property.
 * @see #getModified
 */
public void setModified(java.lang.String modified) {
	fieldModified = modified;
}
}
