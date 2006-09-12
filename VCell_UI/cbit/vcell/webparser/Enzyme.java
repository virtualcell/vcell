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
 * Creation date: (10/13/2002 4:59:46 AM)
 * @author: Frank Morgan
 */
public class Enzyme extends ParsedElement {

	private Hashtable attributes = new Hashtable(); // name(String) -> value(String)
	private Vector content = new Vector(); // ParsedElements

	//private final String name;
	//private final java.util.Vector ids = 		new java.util.Vector();//cbit.vcell.webparse.Id
	//private final java.util.Vector citations = 	new java.util.Vector();//cbit.vcell.webparse.Citation
	//private final java.util.Vector kms = 		new java.util.Vector();//cbit.vcell.webparse.Km
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 5:01:27 AM)
 * @param argName java.lang.String
 * @param argIds java.util.Vector
 * @param argCitations java.util.Vector
 * @param argKms java.util.Vector
 */
public Enzyme(String argName, cbit.vcell.webparser.Id argId) {

	this(argName,argId,null,null);
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 5:01:27 AM)
 * @param argName java.lang.String
 * @param argIds java.util.Vector
 * @param argCitations java.util.Vector
 * @param argKms java.util.Vector
 */
public Enzyme(String argName, Object argIds, java.util.Vector argCitations, java.util.Vector argParams) {
	
	checkArgument(argName,"java.lang.String","Name Error",true);
	java.util.Vector idV = checkArgument(argIds,"cbit.vcell.webparser.Id","Id",true);
	java.util.Vector citV = checkArgument(argCitations,"cbit.vcell.webparser.Citation","Citation",true);
	java.util.Vector paramV = checkArgument(argParams,"cbit.vcell.webparser.Param","Param",true);
	if(argName == null && idV == null){
		throw new IllegalArgumentException("Enzyme Error: Name and Id cannot both be null");
	}

	if(argName != null){
		attributes.put(XML_PARSE_ATTR_NAME,argName);
	}
	if(idV != null){
		content.addAll(idV);
	}
	if(citV != null){
		content.addAll(citV);
	}
	if(paramV != null){
		content.addAll(paramV);
	}
	
	//this.name = argName;
	//if(idV != null){
	//	this.ids.addAll(idV);
	//}
	//if(citV != null){
	//	this.citations.addAll(citV);
	//}
	//if(kmV != null){
	//	this.kms.addAll(kmV);
	//}
}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2002 1:47:29 PM)
 * @return java.lang.String
 */
public java.lang.String[][] getElementAttributes() {

	return getNameValuePairs(attributes);
}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2002 1:47:29 PM)
 * @return java.lang.String
 */
public cbit.vcell.webparser.ParsedElement[] getElementContent() {

	return getParsedElements(content);
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 2:49:59 PM)
 * @return java.lang.String
 */
public java.lang.String getElementName() {
	return XML_PARSE_TYPE_ENZYME;
}
}
