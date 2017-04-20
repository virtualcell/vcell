/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.bionetgen;

import java.io.Serializable;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2006 5:47:24 PM)
 * @author: Jim Schaff
 */
public class BNGReactionRule  implements Serializable {
	private BNGParameter[] parameters = null;
	private BNGSpecies[] reactants = null;
	private BNGSpecies[] products = null;
	private boolean bReversible = false;
	//private int numReactants = 0;
	//private int numProducts = 0;
	//private int numParams = 0;
/**
 * BNGReaction constructor comment.
 */
public BNGReactionRule(BNGParameter[] argParams, BNGSpecies[] argReactants, BNGSpecies[] argPdts, boolean argReversible) {
	super();
	if (argParams.length > 0) {
		parameters = argParams;
	}
	if (argReactants.length > 0) {
		reactants = argReactants;
	}
	if (argPdts.length > 0) {
		products = argPdts;
	}
	bReversible = argReversible;
	//numReactants = reactants.length;
	//numProducts = products.length;
	//numParams = parameters.length;
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public int getNumParameters() {
	if (parameters != null) {
		return parameters.length;
	} else {
		throw new RuntimeException("Null parameters array");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public int  getNumProducts() {
	if (products != null) {
		return products.length;
	} else {
		throw new RuntimeException("Null products array");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public int  getNumReactants() {
	if (reactants != null) {
		return reactants.length;
	} else {
		throw new RuntimeException("Null reactants array");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public BNGParameter[] getParameters() {
	return parameters;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public BNGSpecies[] getProducts() {
	return products;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public BNGSpecies[] getReactants() {
	return reactants;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public boolean isReversible() {
	return bReversible;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public void setParameters(BNGParameter[] argParams) {
	parameters = argParams;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public void setProducts(BNGSpecies[] argPdts) {
	products = argPdts;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public void setReactants(BNGSpecies[] argReacts) {
	reactants = argReacts;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public void setReversible(boolean argReversible) {
	bReversible = argReversible;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public String writeReaction() {
	//
	// Writes out the reaction represented by this class in the proper reaction format : 
	// e.g., A + B <-> C		kf, kr
	// or
	// 		 A + B  -> D		kf
	//
	
	String reactionStr = "";

	// Write out the reactants
	for (int i = 0; i < getNumReactants(); i++){
		if (i == 0) {
			reactionStr = getReactants()[i].getName();
			continue;
		}
		reactionStr = reactionStr + " + " + getReactants()[i].getName();
	}

	// Write the reversible or irreversible sign
	if (isReversible()) {
		reactionStr = reactionStr + " <-> ";
	} else {
		reactionStr = reactionStr + " -> ";
	}

	// Write the products
	for (int i = 0; i < getNumProducts(); i++){
		if (i == 0) {
			reactionStr = reactionStr + getProducts()[i].getName();
			continue;
		}
		reactionStr = reactionStr + " + " + getProducts()[i].getName();
	}

	reactionStr = reactionStr + "\t\t";
	
	// Write the parameters
	for (int i = 0; i < getNumParameters(); i++){
		if (i == 0) {
			reactionStr = reactionStr + getParameters()[i].getName();
			continue;
		}
		reactionStr = reactionStr + ", " + getParameters()[i].getName();
	}

	return reactionStr;
}
}
