package cbit.vcell.webparser;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (10/13/2002 4:37:57 AM)
 * @author: Frank Morgan
 */
public class Citation extends ParsedElement {

	private Hashtable attributes = new Hashtable(); // name(String) -> value(String)
	private Vector content = new Vector(); // ParsedElements
	
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 4:38:27 AM)
 * @param argValue java.lang.String
 * @param argID cbit.vcell.webparser.Id
 */
public Citation(String argValue) {

	this(argValue,null);
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 4:38:27 AM)
 * @param argValue java.lang.String
 * @param argID cbit.vcell.webparser.Id
 */
public Citation(String argValue, Id argID) {

	checkArgument(argValue,"java.lang.String","Citation Must have value",false);

	attributes.put(XML_PARSE_ATTR_VALUE,argValue);
	if(argID != null){
		content.add(argID);
	}
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
 * Creation date: (10/29/2002 2:12:58 PM)
 * @return java.lang.String
 */
public java.lang.String getElementName() {
	return XML_PARSE_TYPE_CITATION;
}
}
