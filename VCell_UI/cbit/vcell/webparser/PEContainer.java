/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.webparser;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (10/15/2002 10:52:29 AM)
 * @author: Frank Morgan
 */
public class PEContainer extends ParsedElement {

	private final java.util.Vector parsedElements = new java.util.Vector(); // ParsedElements
/**
 * Insert the method's description here.
 * Creation date: (10/15/2002 10:55:26 AM)
 * @param pe cbit.vcell.webparser.ParsedElement
 */
public void add(ParsedElement pe) {

	if(size() != 0){
		if(!pe.getClass().getName().equals(parsedElements.elementAt(0).getClass().getName())){
			throw new RuntimeException("PEContainer must have all same");
		}
	}
	parsedElements.add(pe);
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2002 10:56:11 AM)
 * @return cbit.vcell.webparser.ParsedElement
 * @param i int
 */
public ParsedElement get(int i) {
	return (ParsedElement)parsedElements.elementAt(i);
}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2002 2:48:33 PM)
 * @return java.lang.String
 */
public java.lang.String[][] getElementAttributes() {
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2002 2:48:33 PM)
 * @return java.lang.String
 */
public cbit.vcell.webparser.ParsedElement[] getElementContent() {
	return getParsedElements(parsedElements);
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 3:16:01 PM)
 * @return java.lang.String
 */
public java.lang.String getElementName() {
	return XML_PARSE_TYPE_PECONTAINER;
	//throw new Error("Cannot call getElementName on "+this.getClass().getName()+".  Only use as container");
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2002 10:53:48 AM)
 * @return int
 */
public int size() {
	return parsedElements.size();
}
}
