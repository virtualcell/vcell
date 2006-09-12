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
 * Creation date: (10/13/2002 2:31:34 AM)
 * @author: Frank Morgan
 */
public abstract class ParsedElement implements java.io.Serializable {

	private String sourceRef = null;
	//private Hashtable attributes = new Hashtable(); // name(String) -> value(String)
	//private Vector content = new Vector(); // ParsedElements

	
	//XML parse tags
	public static final String XML_PARSE_ATTR_SOURCE	=	"source";		//common
	public static final String XML_PARSE_ATTR_NAME		=	"name";			//common
	public static final String XML_PARSE_ATTR_STOICH	=	"stoich";		//reaction_part
	//public static final String XML_PARSE_ATTR_DEFINITION=	"definition";	//reaction
	public static final String XML_PARSE_ATTR_VALUE		=	"value";		//common
	public static final String XML_PARSE_ATTR_ORGANISM	=	"organism";		//common
	public static final String XML_PARSE_ATTR_SUBSTRATE	=	"substrate";	//km
	public static final String XML_PARSE_ATTR_TYPE		=	"type";			//id
	
	public static final String XML_PARSE_TYPE_PECONTAINER	= "PEContainer";
	public static final String XML_PARSE_TYPE_REACTION		= "Reaction";
	public static final String XML_PARSE_TYPE_REACTION_PART	= "ReactionPart";
	public static final String XML_PARSE_TYPE_ENZYME		= "Enzyme";
	public static final String XML_PARSE_TYPE_KM			= "Km";
	public static final String XML_PARSE_TYPE_CITATION		= "Citation";
	public static final String XML_PARSE_TYPE_ID			= "Id";
	public static final String XML_PARSE_TYPE_COMPOUND		= "Compound";
	public static final String XML_PARSE_TYPE_PARAM			= "Param";
/**
 * Insert the method's description here.
 * Creation date: (10/14/2002 4:37:52 PM)
 * @param arg java.lang.Object
 * @param classType java.lang.Class
 */
protected Vector checkArgument(Object arg, String classType,String errorMessage,boolean isNullOK) throws IllegalArgumentException{

	Class compareClass = null;
	
	if(errorMessage == null){
		throw new Error("checkArgument errorMessage cannot be null");
	}
	try{
		compareClass = Class.forName(classType);
	}catch(ClassNotFoundException e){
		throw new Error("checkArgument classType Error"+e.getMessage());
	}
	if(arg != null){
		if(compareClass.isAssignableFrom(arg.getClass())){
			Vector v = new Vector();
			v.add(arg);
			return v;
		}else if (arg instanceof Vector){
			Vector argV = (Vector)arg;
			if(argV.size() > 0){
				boolean isError = false;
				for(int i = 0; i < argV.size();i+= 1){
					Object obj = argV.get(i);
					if(!compareClass.isAssignableFrom(obj.getClass())){
						break;
					}
				}
				if(isError){
						errorMessage = errorMessage+" : Vector contents not match "+classType;
				}else{
					return argV;
				}
				
			}else if (isNullOK){
				errorMessage = null;
			}else{
				errorMessage = errorMessage+" : Vector Empty";
			}
		}else{
			throw new Error("checkArgument Only checks Vectors and named class types");
		}
	}else if(isNullOK){
		errorMessage = null;
	}else{
		errorMessage = errorMessage+" : value cannot be null";
	}
	
	if(errorMessage == null){
		return null;
	}else{
		throw new IllegalArgumentException("Error Creating "+this.getClass().getName()+" : "+errorMessage);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 12:45:22 PM)
 * @param argTypeIndex int
 * @param rangeMax int
 */
protected final static void checkIndexRange(int argTypeIndex, int rangeMax){

	if(argTypeIndex < 0 || argTypeIndex > rangeMax){
		throw new Error("Type Index out of bounds.  value= "+argTypeIndex+".  Must be >= 0 and < "+rangeMax);
	}
	

	
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 2:32:47 AM)
 * @return org.jdom.Element
 */
public org.jdom.Element getElement(){

	org.jdom.Element newElement = new org.jdom.Element(getElementName());
	
	String[][] attributes = getElementAttributes();
	ParsedElement[] content = getElementContent();

	if(attributes != null){
		for(int i = 0; i < attributes.length;i+= 1){
			newElement.setAttribute(attributes[i][0],attributes[i][1]);
		}
	}

	if(content != null){
		for(int i = 0;i < content.length;i+= 1){
			newElement.addContent(content[i].getElement());
		}
	}
		
	return newElement;
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 1:42:18 PM)
 * @return java.lang.String
 */
public abstract String[][] getElementAttributes();
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 1:42:18 PM)
 * @return java.lang.String
 */
public abstract ParsedElement[] getElementContent();
/**
 * Insert the method's description here.
 * Creation date: (10/29/2002 1:42:18 PM)
 * @return java.lang.String
 */
public abstract String getElementName();
/**
 * Insert the method's description here.
 * Creation date: (10/30/2002 2:00:39 PM)
 * @return java.lang.String[][]
 * @param argAttributes java.util.Hashtable
 */
public final static String[][] getNameValuePairs(Hashtable argAttributes) {
	
	String[][] nameValuePairs = new String[0][0];
	if(argAttributes != null && argAttributes.size() > 0){
		nameValuePairs = new String[argAttributes.size()][2];
		int currentIndex = 0;
		Enumeration names = argAttributes.keys();
		while(names.hasMoreElements()){
			String name = (String)names.nextElement();
			String value = (String)argAttributes.get(name);
			nameValuePairs[currentIndex][0] = name;
			nameValuePairs[currentIndex][1] = value;
			currentIndex+= 1;
		}
	}
	return nameValuePairs;
}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2002 2:07:47 PM)
 * @return cbit.vcell.webparser.ParsedElement[]
 * @param argPE java.util.Vector
 */
public final static ParsedElement[] getParsedElements(Vector argPE) {
	
	ParsedElement[] pe = new ParsedElement[0];
	if(argPE != null && argPE.size() > 0){
		pe = new ParsedElement[argPE.size()];
		argPE.toArray(pe);
	}
	return pe;
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2002 1:34:23 PM)
 * @param source java.net.URL
 */
public String getSourceRef() {

	return sourceRef;
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2002 1:34:23 PM)
 * @param source java.net.URL
 */
public void setSourceRef(String argSource) {

	sourceRef = argSource;
}
}
