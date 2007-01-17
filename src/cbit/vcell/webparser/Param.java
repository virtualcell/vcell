package cbit.vcell.webparser;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (10/29/2002 12:27:27 PM)
 * @author: Frank Morgan
 */
public class Param extends ParsedElement {
	
	private Hashtable attributes = new Hashtable(); // name(String) -> value(String)
	private Vector content = new Vector(); // ParsedElements

	//private final int type;
	//private final String value;
	
	//private final String[][] attributes;
	//private final java.util.Vector citations = new java.util.Vector();
	
	private static final String[] paramTypes = {"Km","Kf","Kb","Vmax","k2k3"};
	public static final int XML_PARAM_TYPE_KM 		= 0;
	public static final int XML_PARAM_TYPE_KF 		= 1;
	public static final int XML_PARAM_TYPE_KB 		= 2;
	public static final int XML_PARAM_TYPE_VMAX 	= 3;
	public static final int XML_PARAM_TYPE_K2K3 	= 4;
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 12:40:15 PM)
 */
public Param(int argTypeIndex, String argValue,java.util.Hashtable additionalAttributes,java.util.Vector parsedElementsContent) {

	checkIndexRange(argTypeIndex,this.paramTypes.length-1);
	checkArgument(argValue,"java.lang.String","Param must have value",false);
	java.util.Vector peV = checkArgument(parsedElementsContent,"cbit.vcell.webparser.ParsedElement","Must all be ParsedElements",true);

	this.attributes.put(XML_PARSE_ATTR_TYPE,this.paramTypes[argTypeIndex]);
	this.attributes.put(XML_PARSE_ATTR_VALUE,argValue);
	if(additionalAttributes != null){
		this.attributes.putAll(additionalAttributes);
	}
	if(peV != null){
		this.content.addAll(peV);
	}

	//this.type = argTypeIndex;
	//this.value = argValue;
	//if(citV != null){
	//	this.citations.addAll(citV);
	//}
	//this.attributes = new String[argAttributes.size()][1];
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2002 3:21:27 PM)
 * @param argParam cbit.vcell.webparser.Param
 * @param addedContent cbit.vcell.webparser.ParsedElement
 */
public Param(Param argParam, ParsedElement addedContent) {

	this.attributes.putAll(argParam.attributes);
	this.content.addAll(argParam.content);
	this.content.add(addedContent);

	}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2002 3:21:27 PM)
 * @param argParam cbit.vcell.webparser.Param
 * @param addedContent cbit.vcell.webparser.ParsedElement
 */
public Param(Param argParam, Vector newContent) {

	this.attributes.putAll(argParam.attributes);
	if(newContent != null){
		this.content.addAll(newContent);
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
 * Creation date: (10/29/2002 3:02:22 PM)
 * @return java.lang.String
 */
public java.lang.String getElementName() {
	return XML_PARSE_TYPE_PARAM;
}
}
