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
import cbit.vcell.parser.Expression;


/**
 * Insert the type's description here.
 * Creation date: (1/13/2006 5:47:24 PM)
 * @author: Jim Schaff
 */
public class BNGReaction {
	private Expression paramExpression = null; 
	private BNGSpecies[] reactants = null;
	private BNGSpecies[] products = null;
	private String ruleName = null;
	private boolean bRuleReversed = false;

/**
 * BNGReaction constructor comment.
 */
public BNGReaction(BNGSpecies[] argReactants, BNGSpecies[] argPdts, Expression argExpr, String ruleName, boolean bRuleReversed) {
	super();
	if (argReactants.length > 0) {
		reactants = argReactants;
	}
	if (argPdts.length > 0) {
		products = argPdts;
	}
	paramExpression = argExpr;
	this.ruleName = ruleName;
	this.bRuleReversed = bRuleReversed;
}


public String getRuleName() {
	return ruleName;
}


public boolean isRuleReversed() {
	return bRuleReversed;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public Expression getParamExpression() {
	return paramExpression;
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
public void setParamExpression(Expression argExpr) {
	paramExpression = argExpr;
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
public String writeReaction() {
	//
	// Writes out the reaction represented by this class in the proper reaction format :
	// Since this is a reaction rather than a reaction rule, for now, it is a forward reaction, with reactant(s), pdt(s), rate const expression.
	//
	// e.g., 		 A + B  -> C		kf
	//
	
	String reactionStr = "";

	int numReactants = getReactants().length;
	int numPdts = getProducts().length;
	
	// Write out the reactants
	for (int i = 0; i < numReactants; i++){
		if (i == 0) {
			reactionStr = getReactants()[i].getName();
			continue;
		}
		reactionStr = reactionStr + " + " + getReactants()[i].getName();
	}

	// Write the irreversible reaction symbol
	reactionStr = reactionStr + " -> ";

	// Write the products
	for (int i = 0; i < numPdts; i++){
		if (i == 0) {
			reactionStr = reactionStr + getProducts()[i].getName();
			continue;
		}
		reactionStr = reactionStr + " + " + getProducts()[i].getName();
	}

	reactionStr = reactionStr + "\t\t";
	
	// Write the parameter expression
	reactionStr = reactionStr + getParamExpression().infix();

	return reactionStr;
}


public String toStringShort() {
	// almost like writeReaction() above except there's no expression
	String reactionStr = "";
	int numReactants = getReactants().length;
	int numPdts = getProducts().length;
	
	for (int i = 0; i < numReactants; i++){
		if (i == 0) {
			reactionStr = getReactants()[i].getName();
			continue;
		}
		reactionStr = reactionStr + " + " + getReactants()[i].getName();
	}
	reactionStr = reactionStr + " -> ";
	for (int i = 0; i < numPdts; i++){
		if (i == 0) {
			reactionStr = reactionStr + getProducts()[i].getName();
			continue;
		}
		reactionStr = reactionStr + " + " + getProducts()[i].getName();
	}
	return reactionStr;
}
}
