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

import org.jdom2.Attribute;
import org.jdom2.Element;

public class ChangedNodeInfo extends NodeInfo {
	private java.lang.String fieldModified = null;


public ChangedNodeInfo(Attribute base, Attribute newmodified) {
	super(base, NodeInfo.STATUS_CHANGED);
	this.setModified(newmodified.getValue());
}


public ChangedNodeInfo(Element base, Element modified) {
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
