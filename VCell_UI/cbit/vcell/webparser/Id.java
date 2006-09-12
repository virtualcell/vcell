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
 * Creation date: (10/13/2002 2:33:36 AM)
 * @author: Frank Morgan
 */
public class Id extends ParsedElement {

	private Hashtable attributes = new Hashtable(); // name(String) -> value(String)
	private Vector content = new Vector(); // ParsedElements
	
	//private final String value;
	//private final int type;

	
	//ID Type index
	private static final String[] idTypes = {"EC","KEGG","CASRN","PUBMED"};
	public static final int XML_ID_TYPE_EC 		= 0;
	public static final int XML_ID_TYPE_KEGG 	= 1;
	public static final int XML_ID_TYPE_CASRN 	= 2;
	public static final int XML_ID_TYPE_PUBMED 	= 3;
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 2:34:24 AM)
 * @param value java.lang.String
 * @param type int
 */
public Id(int argTypeIndex, String argValue) {

	checkIndexRange(argTypeIndex,this.idTypes.length-1);
	
	checkArgument(argValue,"java.lang.String","Id must have value",false);
	
	attributes.put(XML_PARSE_ATTR_VALUE,argValue);
	attributes.put(XML_PARSE_ATTR_TYPE,getIdType(argTypeIndex));
	
	//this.value = argValue;
	//this.type = argTypeIndex;
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
 * Creation date: (10/29/2002 2:39:09 PM)
 * @return java.lang.String
 */
public java.lang.String getElementName() {
	return XML_PARSE_TYPE_ID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/2002 3:28:22 PM)
 * @return java.lang.String
 * @param idIndex int
 */
private static final String getIdType(int idTypesIndex) {
	if(idTypesIndex < 0 || idTypesIndex > idTypes.length){
		throw new IllegalArgumentException("getIdType Error: No idType for idTypesIndex="+idTypesIndex);
	}
	return idTypes[idTypesIndex];
}
}
