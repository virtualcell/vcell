/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.xml.viewer;

/**
 * Insert the type's description here.
 * Creation date: (3/30/2001 4:27:45 PM)
 * @author: Daniel Lucio
 */
public class XmlTreeViewerNode extends javax.swing.tree.DefaultMutableTreeNode {
/**
 * XmlTreeViewerNode constructor comment.
 */
public XmlTreeViewerNode() {
	super();
}
/**
 * XmlTreeViewerNode constructor comment.
 * @param userObject java.lang.Object
 */
public XmlTreeViewerNode(Object userObject) {
	super(userObject);
}
/**
 * XmlTreeViewerNode constructor comment.
 * @param userObject java.lang.Object
 * @param allowsChildren boolean
 */
public XmlTreeViewerNode(Object userObject, boolean allowsChildren) {
	super(userObject, allowsChildren);
}
public String toString() {
	if (getUserObject() instanceof org.jdom2.Element) {
		org.jdom2.Element element = (org.jdom2.Element) getUserObject();
		String text = element.getTextTrim();
		if (text.length() > 0) text = " (" + text + ")";
		return ("Element : " + element.getName() + text);
	} else if (getUserObject() instanceof org.jdom2.Attribute) {
		org.jdom2.Attribute attribute = (org.jdom2.Attribute) getUserObject();
		return ("Attribute : " + attribute.getName() + " = " + attribute.getValue());
	}
	// Otherwise, do the default...though
	// this should never happen...
	return (super.toString());
}
}
