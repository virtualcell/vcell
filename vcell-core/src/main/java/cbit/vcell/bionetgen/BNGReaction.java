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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.vcell.model.rbm.RbmUtils;

import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.RbmKineticLaw.RateLawType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;


/**
 * Insert the type's description here.
 * Creation date: (1/13/2006 5:47:24 PM)
 * @author: Jim Schaff
 */
public class BNGReaction  implements Serializable {	// obtained from parsing the .net file (result of flattening .bngl file)
	private Expression paramExpression = null;
	String matchingKey = null;
	private BNGSpecies[] reactants = null;
	private BNGSpecies[] products = null;
	private String ruleName = null;
	private boolean bRuleReversed = false;

/**
 * BNGReaction constructor comment.
 */
public BNGReaction(String reactantsSubkey, String productsSubkey, BNGSpecies[] argReactants, BNGSpecies[] argPdts, Expression argExpr, String ruleName, boolean bRuleReversed) {
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
	
	if(bRuleReversed == false) {
		this.matchingKey = ruleName + "_" + reactantsSubkey + " " + productsSubkey;
	} else {
		this.matchingKey = ruleName + "_" + productsSubkey + " " + reactantsSubkey;
	}
}

public String getKey() {
	return matchingKey;
}

public String getRuleName() {
	return ruleName;
}
@Deprecated
// we shouldn't have the _reverse_ as part of the name anymore, during parsing we 
//   take the _reverse_ out of the name and set the bRuleReversed flag instead
public String extractRuleName() {
	if(ruleName.contains("_reverse_")) {
		return ruleName.substring("_reverse_".length());
	} else {
		return ruleName;
	}
}

public List<Integer> findProductPositions(BNGSpecies our) {
	
	List<Integer> productPositions = new ArrayList<>();
	for(int i=0; i<products.length; i++) {		// always only look in the products
		BNGSpecies candidate = products[i];
		if(our.equals(candidate)) {
			productPositions.add(i);
		}
	}
	return productPositions;
}

public Expression getParamExpression() {
	return paramExpression;
}
public BNGSpecies[] getProducts() {
	return products;
}
public BNGSpecies[] getReactants() {
	return reactants;
}

public void setParamExpression(Expression argExpr) {
	paramExpression = argExpr;
}
public void setProducts(BNGSpecies[] argPdts) {
	products = argPdts;
}
public void setReactants(BNGSpecies[] argReacts) {
	reactants = argReacts;
}

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

public boolean isRuleReversed() {
	return bRuleReversed;
}
public boolean isMichaelisMenten() {
	String value = paramExpression.infix();
	if(value == null) {
		return false;
	}
	value = value.replaceAll("\\s+","");		// 2*Sat(Vmax,Km)
	if(!value.contains(RbmUtils.MM_Prefix)) {
		return false;
	}
	StringTokenizer tokenizer = new StringTokenizer(value, "*");
	if(!tokenizer.hasMoreTokens()) {
		return false;
	}
	String token = tokenizer.nextToken();
	if(token.startsWith(RbmUtils.MM_Prefix)) {
		return true;
	}
	if(!tokenizer.hasMoreTokens()) {
		return false;
	}
	token = tokenizer.nextToken();
	if(token.startsWith(RbmUtils.MM_Prefix)) {
		return true;
	}
	return false;
}
public String extractSymmetryFactor() {
	if(!isMichaelisMenten()) {
		return null;
	}
	String value = paramExpression.infix();		// already verified in isMichaelisMenten() that value is not null
	value = value.replaceAll("\\s+","");
	StringTokenizer tokenizer = new StringTokenizer(value, "*");
	if(!tokenizer.hasMoreTokens()) {
		return null;
	}
	String token = tokenizer.nextToken();
	if(token.startsWith(RbmUtils.MM_Prefix)) {
		return "1";
	} else {
		return token;
	}
}
public String extractMMPrefix() {
	if(!isMichaelisMenten()) {
		return null;
	}
	String value = paramExpression.infix();		// already verified in isMichaelisMenten() that value is not null
	value = value.replaceAll("\\s+","");
	StringTokenizer tokenizer = new StringTokenizer(value, "*");
	if(!tokenizer.hasMoreTokens()) {
		return null;
	}
	String token = tokenizer.nextToken();
	if(token.startsWith(RbmUtils.MM_Prefix)) {
		token = token.substring(0, RbmUtils.MM_Prefix.length()-1);
		return token;
	}
	if(!tokenizer.hasMoreTokens()) {
		return null;
	}
	token = tokenizer.nextToken();
	token = token.substring(0, RbmUtils.MM_Prefix.length()-1);
	return token;
}
public String extractVmax() {
	if(!isMichaelisMenten()) {
		return null;
	}
	String value = paramExpression.infix();
	value = value.replaceAll("\\s+","");
	StringTokenizer tokenizer = new StringTokenizer(value, "*");
	if(!tokenizer.hasMoreTokens()) {
		return null;
	}
	value = tokenizer.nextToken();
	if(!value.startsWith(RbmUtils.MM_Prefix)) {
		if(!tokenizer.hasMoreTokens()) {
			return null;
		}
		value = tokenizer.nextToken();
	}
	value = value.substring(0, value.lastIndexOf(")"));			// get rid of the last ")"
	value = value.substring(RbmUtils.MM_Prefix.length());		// get rid of the prefix  "Sat("
	String strVmax = value.substring(0, value.indexOf(","));	// part before the ","
	return strVmax;
}
public String extractKm() {
	if(!isMichaelisMenten()) {
		return null;
	}
	String value = paramExpression.infix();
	value = value.replaceAll("\\s+","");
	StringTokenizer tokenizer = new StringTokenizer(value, "*");
	if(!tokenizer.hasMoreTokens()) {
		return null;
	}
	value = tokenizer.nextToken();
	if(!value.startsWith(RbmUtils.MM_Prefix)) {
		if(!tokenizer.hasMoreTokens()) {
			return null;
		}
		value = tokenizer.nextToken();
	}
	value = value.substring(0, value.lastIndexOf(")"));
	value = value.substring(RbmUtils.MM_Prefix.length());
	String strKm = value.substring(value.indexOf(",")+1);		// part after the ","
	return strKm;
}
public Expression getMichaelisMentenParamExpression(KineticsParameter rateParameter) {
	if(!isMichaelisMenten()) {
		return null;
	}
	String str = null;
	if(rateParameter.getRole() == Kinetics.ROLE_Vmax) {
		str = extractVmax();
	} else if(rateParameter.getRole() == Kinetics.ROLE_Km) {
		str = extractKm();
	} else {
		throw new RuntimeException("Unexpected kinetic parameter " + rateParameter.getName());
	}
	Expression expression = null;
	try {
		expression = new Expression(str);
	} catch (ExpressionException e) {
		throw new RuntimeException("Unable to initialize expression for " + rateParameter.getName());
	}
	return expression;
}

public String toBnglString() {
	String reactionStr = "";
	
	int numReactants = getReactants().length;
	int numPdts = getProducts().length;
	
	for (int i = 0; i < numReactants; i++){
		if (i == numReactants-1) {
			reactionStr += getReactants()[i].getNetworkFileIndex();
		} else {
			reactionStr += getReactants()[i].getNetworkFileIndex() + ",";

		}
	}
	reactionStr += " ";
	for (int i = 0; i < numPdts; i++){
		if (i == numPdts-1) {
			reactionStr += getProducts()[i].getNetworkFileIndex();
		} else {
			reactionStr += getProducts()[i].getNetworkFileIndex() + ",";
		}
	}
	
	if(!isMichaelisMenten()) {
		String infix = paramExpression.infix();
		if(infix.startsWith("(") && infix.endsWith(")")) {
			infix = infix.replaceAll("\\s+","");	// remove spaces
		}
		reactionStr += " " + infix;
		reactionStr += " #";
		if(bRuleReversed) {
			reactionStr += "_reverse_";
		}
		reactionStr += ruleName;
	} else {
		String str1 = extractMMPrefix();
		String str2 = extractVmax();
		String str3 = extractKm();
		if(str1 != null && str2 != null && str3 != null) {
			reactionStr += " " + str1 + " " + str2 + " " + str3;
		} else {
			throw new RuntimeException("Invalid format for BNGReaction parameterExpression");
		}
		reactionStr += " #";
		reactionStr += ruleName;
	}
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
