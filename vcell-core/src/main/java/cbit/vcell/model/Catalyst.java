/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;


public class Catalyst extends ReactionParticipant
{
/**
 * This method was created in VisualAge.
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
Catalyst(KeyValue key, ReactionStep reactionStep) {
	super(key, reactionStep);
}


public Catalyst(KeyValue key,ReactionStep parent,SpeciesContext speciesContext){
	super(key,parent,speciesContext, 0);
}   


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof Catalyst){
		Catalyst c = (Catalyst)obj;
		return compareEqual0(c);
	}else{
		return false;
	}
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void fromTokens(CommentStringTokenizer tokens, Model model) throws Exception {

	String scName = tokens.nextToken();				// read speciesContext name
	String speciesName = tokens.nextToken();		// read species name
	String structureName = tokens.nextToken();		// read structure name
	SpeciesContext sc = model.getSpeciesContext(scName);
	if (sc==null){
		throw new Exception("speciesContext "+scName+" not found");
	}
	setSpeciesContext(sc);
	setStoichiometry(0);

}


/**
 * Insert the method's description here.
 * Creation date: (5/22/00 10:19:56 PM)
 * @return java.lang.String
 */
public static String getTerm() {
	return "Modifier";
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	String scName = (getSpeciesContext()!=null)?(getSpeciesContext().getName()):"null";
	return "Catalyst(id="+getKey()+", speciesContext="+scName+")";
}


/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public void writeTokens(java.io.PrintWriter pw) {
	pw.println("\t\t"+VCMODL.Catalyst+" "+getSpeciesContext().getName());
}
}
