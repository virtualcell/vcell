package cbit.vcell.webparser;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (10/13/2002 2:59:36 AM)
 * @author: Frank Morgan
 */
public class Compound extends ParsedElement {
	
	private Hashtable attributes = new Hashtable(); // name(String) -> value(String)
	private Vector content = new Vector(); // ParsedElements

	
	//private final String name;
	//private final java.util.Vector ids = new java.util.Vector();//cbit.vcell.webparser.Id
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 3:01:02 AM)
 * @param argName java.lang.String
 * @param argId cbit.vcell.webparser.Id
 */
public Compound(String argName, cbit.vcell.webparser.Id argId) {
	
	this(argName,(Object)argId);
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 3:01:02 AM)
 * @param argName java.lang.String
 * @param argId cbit.vcell.webparser.Id
 */
private Compound(String argName, Object argIds) {

	checkArgument(argName,"java.lang.String","Name",true);
	java.util.Vector idV = checkArgument(argIds,"cbit.vcell.webparser.Id","Id",true);
	if(argName == null && idV == null){
		throw new IllegalArgumentException("Compound Error: Name and Id cannot both be null");
	}
	
	if(argName != null){
		attributes.put(XML_PARSE_ATTR_NAME,argName);
	}
	content.addAll(idV);
	
	//this.name = argName;
	//if(idV != null){
	//	this.ids.addAll(idV);
	//}
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 3:01:02 AM)
 * @param argName java.lang.String
 * @param argId cbit.vcell.webparser.Id
 */
public Compound(String argName, java.util.Vector argIds) {

	this(argName,(Object)argIds);
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
 * Creation date: (10/29/2002 2:17:37 PM)
 * @return java.lang.String
 */
public java.lang.String getElementName() {
	return XML_PARSE_TYPE_COMPOUND;
}
}
