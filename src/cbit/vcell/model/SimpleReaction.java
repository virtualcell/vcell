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

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;


@SuppressWarnings("serial")
public class SimpleReaction extends ReactionStep
{
	private String fieldAnnotation = null;
	
public SimpleReaction(Model model, Structure structure, KeyValue key, String name, String argAnnotation) throws java.beans.PropertyVetoException {
	super(model, structure,key,name);
	this.fieldAnnotation = argAnnotation;
	try {
		setKinetics(new MassActionKinetics(this));
	} catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
public SimpleReaction(Model model, Structure structure, KeyValue key, String name) throws java.beans.PropertyVetoException {
	this(model, structure, key, name, null);
}

public SimpleReaction(Model model, Structure structure,String name, String argAnnotation) throws java.beans.PropertyVetoException {
	this(model, structure, null, name, argAnnotation);
}
public SimpleReaction(Model model, Structure structure,String name) throws java.beans.PropertyVetoException {
	this(model, structure, null, name, null);
}

public void addProduct(SpeciesContext speciesContext,int stoichiometry) throws Exception {

	int count = countNumReactionParticipants(speciesContext);

	// NOTE : right now, we are not taking into account the possibility of allowing 
	// a speciesContext to be a Catalyst as well as pdt or reactant.
	if (count == 0) {
		// No matching reactionParticipant was found for the speciesContext, hence add it as a Pdt
		addReactionParticipant(new Product(null,this, speciesContext, stoichiometry));
	} else if (count == 1) {
		ReactionParticipant[] rps = getReactionParticipants();
		ReactionParticipant rp0 = null;
		for (ReactionParticipant rp : rps) {
			if (rp.getSpeciesContext() == speciesContext) {
				rp0 = rp;
				break;
			}
		}
		// One matching reactionParticipant was found for the speciesContext, 
		// if rp[0] is a reactant, add speciesContext as pdt, else throw exception since it is already a pdt (refer NOTE above)
		if (rp0 instanceof Reactant){
			addReactionParticipant(new Product(null,this, speciesContext, stoichiometry));
		} else if (rp0 instanceof Product || rp0 instanceof Catalyst) {
			throw new Exception("reactionParticipant " + speciesContext.getName() + " already defined as a Product or Catalyst of the reaction.");
		}
	} else if (count > 1) {
		// if rps.length is > 1, speciesContext occurs both as reactant and pdt, so throw exception
		throw new Exception("reactionParticipant " + speciesContext.getName() + " already defined as a Reactant and Product of the reaction.");
	}
}   
public void addReactant(SpeciesContext speciesContext,int stoichiometry) throws Exception {

	int count = countNumReactionParticipants(speciesContext);

	// NOTE : right now, we are not taking into account the possibility of allowing 
	// a speciesContext to be a Catalyst as well as pdt or reactant.
	if (count == 0) {
		// No matching reactionParticipant was found for the speciesContext, hence add it as a Pdt
		addReactionParticipant(new Reactant(null,this, speciesContext, stoichiometry));
	} else if (count == 1) {
		ReactionParticipant[] rps = getReactionParticipants();
		ReactionParticipant rp0 = null;
		for (ReactionParticipant rp : rps) {
			if (rp.getSpeciesContext() == speciesContext) {
				rp0 = rp;
				break;
			}
		}
		// One matching reactionParticipant was found for the speciesContext, 
		// if rp[0] is a product, add speciesContext as reactant, else throw exception since it is already a reactant (refer NOTE above)
		if (rp0 instanceof Product){
			addReactionParticipant(new Reactant(null,this, speciesContext, stoichiometry));
		} else if (rp0 instanceof Reactant || rp0 instanceof Catalyst) {
			throw new Exception("reactionParticipant " + speciesContext.getName() + " already defined as a Reactant or a Catalyst of the reaction.");
		}
	} else if (count > 1) {
		// if rps.length is > 1, speciesContext occurs both as reactant and pdt, so throw exception
		throw new Exception("reactionParticipant " + speciesContext.getName() + " already defined as a Reactant AND Product of the reaction.");
	}
		
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
 * This method was created in VisualAge.
 * @return double
 * @param speciesContext cbit.vcell.model.SpeciesContext
 */
public int getStoichiometry(SpeciesContext speciesContext) {
	ReactionParticipant[] rps = getReactionParticipants();

	int totalStoich = 0;
	for (ReactionParticipant rp : rps) {
		if (rp.getSpeciesContext() == speciesContext) {
			if (rp instanceof Product){
				totalStoich += rp.getStoichiometry();
			}else if (rp instanceof Reactant){
				totalStoich += (-1)*rp.getStoichiometry();
			}
		}
	}
	return totalStoich;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/00 10:22:19 PM)
 * @return java.lang.String
 */
public String getTerm() {
	return "Reaction";
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

}
