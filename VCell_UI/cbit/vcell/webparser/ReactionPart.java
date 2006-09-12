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
 * Creation date: (10/13/2002 3:37:40 AM)
 * @author: Frank Morgan
 */
public class ReactionPart extends ParsedElement {
	
	private Hashtable attributes = new Hashtable(); // name(String) -> value(String)
	private Vector content = new Vector(); // ParsedElements

	//private final int type;
	//private final String stoich;
	//private final Compound compound;

	//reactionPart type
	private static final String[] reactionPartTypes = {"reactant","product"};
	public static final int XML_REACTIONPART_TYPE_REACTANT 	= 0;
	public static final int XML_REACTIONPART_TYPE_PRODUCT	= 1;
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 3:39:10 AM)
 * @param argTypeIndex int
 * @param argStoich int
 * @param argCompound cbit.vcell.webparser.Compound
 */
public ReactionPart(int argTypeIndex, int argStoich, Compound argCompound) {

	
	this(argTypeIndex,argStoich+"",argCompound);
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2002 3:39:10 AM)
 * @param argTypeIndex int
 * @param argStoich int
 * @param argCompound cbit.vcell.webparser.Compound
 */
public ReactionPart(int argTypeIndex, String argStoich, Compound argCompound) {

	checkIndexRange(argTypeIndex,this.reactionPartTypes.length-1);
	
	int stoichTemp;
	try{
		stoichTemp = Integer.parseInt(argStoich);
		if(stoichTemp <= 0){
			throw new IllegalArgumentException(this.getClass().getName()+" Creation Error : Stoichiometry must be >0 "+argStoich);
		}
	}catch(NumberFormatException e){
		throw new IllegalArgumentException(this.getClass().getName()+" Creation Error : Stoichiometry must be number "+argStoich);
	}
	checkArgument(argCompound,"cbit.vcell.webparser.Compound","Compound",false);

	attributes.put(XML_PARSE_ATTR_TYPE,getRPType(argTypeIndex));
	attributes.put(XML_PARSE_ATTR_STOICH,argStoich);
	content.add(argCompound);
	
	//this.type = argTypeIndex;
	//this.stoich = argStoich;
	//this.compound = argCompound;
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
 * Creation date: (10/29/2002 3:18:21 PM)
 * @return java.lang.String
 */
public java.lang.String getElementName() {
	return XML_PARSE_TYPE_REACTION_PART;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/2002 3:28:22 PM)
 * @return java.lang.String
 * @param idIndex int
 */
public static final String getRPType(int reactionPartTypesIndex) {
	if(reactionPartTypesIndex < 0 || reactionPartTypesIndex > reactionPartTypes.length){
		throw new IllegalArgumentException("getRPType Error: No reactionPartType for reactionPartTypesIndex="+reactionPartTypesIndex);
	}
	return reactionPartTypes[reactionPartTypesIndex];
}
}
