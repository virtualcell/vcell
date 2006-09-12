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
 * Creation date: (10/13/2002 5:32:19 AM)
 * @author: Frank Morgan
 */
public class Reaction extends ParsedElement {

	private Hashtable attributes = new Hashtable(); // name(String) -> value(String)
	private Vector content = new Vector(); // ParsedElements

	//private final String name;
	//private final java.util.Vector ids = new java.util.Vector();//cbit.vcell.webparser.Id
	//private final java.util.Vector reactionParts = new java.util.Vector();//cbit.vcell.webparser.Reactionpart
	//private final cbit.vcell.webparser.Enzyme enzyme;
	//private final java.util.Vector citations = new java.util.Vector();//cbit.vcell.webparser.Citation
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 5:39:50 AM)
 * @param argName java.lang.String
 * @param argIds java.util.Vector
 * @param argreactionParts java.util.Vector
 * @param argEnzyme cbit.vcell.webparser.Enzyme
 * @param argCitations java.util.Vector
 */
public Reaction(String argName, Object argReactionParts, Object argIds, Enzyme argEnzyme, Object argCitations) {
	
	checkArgument(argName,"java.lang.String","Name",true);	
	java.util.Vector rpV = checkArgument(argReactionParts,"cbit.vcell.webparser.ReactionPart","ReactionPart",false);
	java.util.Vector idV = checkArgument(argIds,"cbit.vcell.webparser.Id","Id",true);
	java.util.Vector citV = checkArgument(argCitations,"cbit.vcell.webparser.Citation","Citation",true);

	if(argName != null){
		attributes.put(XML_PARSE_ATTR_NAME,argName);
	}
	if(argEnzyme != null){
		content.add(argEnzyme);
	}
	if(rpV != null){
		content.addAll(rpV);
	}
	if(idV != null){
		content.addAll(idV);
	}
	if(citV != null){
		content.addAll(citV);
	}
	
	//this.name = argName;
	//this.reactionParts.addAll(rpV);	
	//this.enzyme = argEnzyme;
	//if(idV != null){
	//	this.ids.addAll(idV);
	//}
	//if(citV != null){
	//	this.citations.addAll(citV);
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
 * Creation date: (10/29/2002 3:22:36 PM)
 * @return java.lang.String
 */
public java.lang.String getElementName() {
	return XML_PARSE_TYPE_REACTION;
}
}
