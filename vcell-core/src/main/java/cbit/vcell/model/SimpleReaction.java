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

import java.beans.PropertyVetoException;

import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

import cbit.vcell.parser.ExpressionException;


@SuppressWarnings("serial")
public class SimpleReaction extends ReactionStep
{
	private String fieldAnnotation = null;
	
public SimpleReaction(Model model, Structure structure, KeyValue key, String name, boolean bReversible, String argAnnotation) throws java.beans.PropertyVetoException {
	super(model, structure,key,name,bReversible);
	this.fieldAnnotation = argAnnotation;
	try {
		setKinetics(new MassActionKinetics(this));
	} catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
public SimpleReaction(Model model, Structure structure, KeyValue key, String name, boolean bReversible) throws java.beans.PropertyVetoException {
	this(model, structure, key, name, bReversible, null);
}

public SimpleReaction(Model model, Structure structure,String name, boolean bReversible, String argAnnotation) throws java.beans.PropertyVetoException {
	this(model, structure, null, name, bReversible, argAnnotation);
}
public SimpleReaction(Model model, Structure structure,String name, boolean bReversible) throws java.beans.PropertyVetoException {
	this(model, structure, null, name, bReversible, null);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof SimpleReaction){
		SimpleReaction sr = (SimpleReaction)obj;
		if (!super.compareEqual0(sr)){
			return false;
		}
		return true;
	}else{
		return false;
	}
}
public int getNumProducts() {

	int count = 0;

	ReactionParticipant rp_Array[] = getReactionParticipants();
	
	for (int i = 0; i < rp_Array.length; i++) {
		if (rp_Array[i] instanceof Product){
			count++;
		}
	}
	
	return count;
}   
public int getNumReactants() {

	int count = 0;

	ReactionParticipant rp_Array[] = getReactionParticipants();

	for (int i = 0; i < rp_Array.length; i++) {
		if (rp_Array[i] instanceof Reactant){
			count++;
		}
	}
	
	return count;
}   

/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isMembrane() {

	ReactionParticipant rp_Array[] = getReactionParticipants();

	for (int i = 0; i < rp_Array.length; i++) {
		if (rp_Array[i].getStructure() instanceof Membrane){
			return true;
		}
	}			
	
	return false;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "SimpleReaction@"+Integer.toHexString(hashCode())+"("+getName()+")";
}
@Override
public void setReactionParticipantsFromDatabase(Model model, ReactionParticipant[] reactionParticipants) throws PropertyVetoException {
	// no special logic needed here.
	setReactionParticipants(reactionParticipants);
}

private static final String typeName = "Reaction";
@Override
public final String getDisplayName() {
	return getName();
}
@Override
public final String getDisplayType() {
	return typeName;
}

}
