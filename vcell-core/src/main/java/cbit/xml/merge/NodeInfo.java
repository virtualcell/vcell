/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.xml.merge;
public class NodeInfo extends javax.swing.tree.DefaultMutableTreeNode {
	public final static int STATUS_REMOVED = 102;
	public final static int STATUS_NEW = 101;
	public final static int STATUS_NORMAL = 100;
	public final static int STATUS_CHANGED = 103;
	public final static int STATUS_PROBLEM = 105;
	//
	private java.lang.String fieldName = null;
	private java.lang.String fieldValue = null;
	private int fieldStatus = 0;
	private boolean fieldAttribute;
/**
 * This constructor creates a new NodeInfo by hand.
 * Creation date: (8/1/2000 1:12:37 PM)
 * @param name String
 * @param status int
 */
public NodeInfo(String name, String value, int status, boolean isAttribute) {
	super(name);
	this.fieldName = name;
	this.fieldValue = value;
	this.fieldStatus = status;
	this.fieldAttribute = isAttribute;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2000 3:11:02 PM)
 * @param newattr org.jdom.Attribute
 * @param newcolor java.awt.Color
 */
public NodeInfo(org.jdom.Attribute newattr, int status) {
	super(newattr.getName());
	if (newattr == null) {
		throw new IllegalArgumentException("This constructor needs a non null arguments");
	}
	this.fieldName = newattr.getName();
	this.fieldStatus = status;
	this.fieldAttribute = true;
	this.fieldValue = (newattr.getValue().trim().length() != 0) ? newattr.getValue() : null;
}
/**
 * This constructor creates a new NodeInfo from a JDOM Element.
 * Creation date: (8/1/2000 1:12:37 PM)
 * @param newelement org.jdom.Element
 * @param newcolor java.awt.Color
 */
public NodeInfo(org.jdom.Element newelement, int status) {
	super(newelement.getName());
	if (newelement == null) {
		throw new IllegalArgumentException("This constructor needs a non null argument");
	}
	this.fieldName = newelement.getName();
	this.fieldValue = (newelement.getTextTrim().length() != 0) ? newelement.getText() : null;
	this.fieldStatus = status;
	this.fieldAttribute = false;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2000 5:46:59 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return fieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 11:58:22 AM)
 * @return short
 */
public int getStatus() {
	return fieldStatus;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2000 5:47:18 PM)
 * @return java.lang.String
 */
public java.lang.String getValue() {
	return fieldValue;
}
/**
 * Gets the attribute property (boolean) value.
 * @return The attribute property value.
 */
public boolean isAttribute() {
	return fieldAttribute;
}
/**
 * Gets the element property (boolean) value.
 * @return The element property value.
 */
public boolean isElement() {
	return (!isAttribute());
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2000 5:46:59 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String name) {
	fieldName = name;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 11:58:22 AM)
 * @param newStatus short
 */
public void setStatus(int status) {
	fieldStatus = status;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2000 5:47:18 PM)
 * @param newValue java.lang.String
 */
public void setValue(java.lang.String value) {
	fieldValue = (value.trim().length() != 0) ? value : null;
}
/**
 * Returns the a string containing the name and status..
 * @return a string representation of the receiver
 */
public String toString() {
	String string = getName();

	if (getValue() != null && isAttribute() && getStatus() != STATUS_CHANGED) {
		string = string + " : " + getValue();
	}/*
	if (getStatus() == STATUS_CHANGED) {
		string = string + " <CHANGED>";
	} else if (getStatus() == STATUS_NEW) {
		string = string + " <NEW>";
	} else if (getStatus() == STATUS_REMOVED) {
		string = string + " <REMOVED>";
	}*/
	return (string);
}
/**
 * This method returns a XML string containing the content of this node.
 * Creation date: (9/13/2001 5:47:26 PM)
 * @return java.lang.String
 */
public String toXmlString() {
	StringBuffer result = new StringBuffer();
	String unescapedValue = (getValue()!=null)?getValue():"";
	String value = org.vcell.util.TokenMangler.getEscapedString(unescapedValue.trim());
	
	if (isElement()) {
		//this node is an Element
		//begining
		result.append("<"+getName() +" ");
		//attributes
		java.util.Enumeration children = this.children();
		while (children.hasMoreElements()) {
			NodeInfo temp = (NodeInfo)children.nextElement();
			if ( temp.isAttribute() ) {
				result.append( " " + temp.toXmlString());
			}
		}
		result.append(">");
		//content
		result.append(value);
		//elements
		children = this.children();
		while (children.hasMoreElements()) {
			NodeInfo temp = (NodeInfo)children.nextElement();

			if ( temp.isElement() ) {
				result.append( " " + temp.toXmlString());
			}
		}
		//end
		result.append("</"+getName()+">");
	}else {
		//this node is an attribute
		result.append( getName() + "=\"" + value + "\"" );
	}
	
	return result.toString();
}
}
