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
import java.util.Vector;
import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (1/13/2006 5:40:40 PM)
 * @author: Jim Schaff
 */
public class BNGComplexSpecies extends BNGSpecies {
/**
 * BNGComplexSpecies constructor comment.
 * @param argName java.lang.String
 */
public BNGComplexSpecies(String argName, Expression argConc, int argNtwkFileIndx) {
	super(argName, argConc, argNtwkFileIndx);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 5:40:40 PM)
 * @return boolean
 */
public boolean isWellDefined() {
	String nameStr = getName();
	if (nameStr.indexOf("*") >= 0) {
		return false;
	} else {
		return true;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/13/2006 2:50:30 PM)
 * @return boolean
 */
public BNGSpecies[] parseBNGSpeciesName() {
	// Parse the complexSpecies to get the individual species; add them to a vector, and return the vector.
	java.util.StringTokenizer complexSpeciesNameTokenizer = new java.util.StringTokenizer(getName(), ".");
	Vector complexSpeciesComponentsVector = new Vector();
	String token1 = null;
	while (complexSpeciesNameTokenizer.hasMoreTokens()) {
		token1 = complexSpeciesNameTokenizer.nextToken();
		if (token1.indexOf("(") > 0) {
			BNGMultiStateSpecies msSpecies = new BNGMultiStateSpecies(token1, new Expression(0.0), -1);
			complexSpeciesComponentsVector.addElement(msSpecies);
		} else {
			BNGSingleStateSpecies ssSpecies = new BNGSingleStateSpecies(token1, new Expression(0.0), -1);
			complexSpeciesComponentsVector.addElement(ssSpecies);
		}
	}
	BNGSpecies[] complexSpeciesComponents = (BNGSpecies[])org.vcell.util.BeanUtils.getArray(complexSpeciesComponentsVector, BNGSpecies.class);
	return complexSpeciesComponents;
}
}
