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
 * Creation date: (1/13/2006 5:33:12 PM)
 * @author: Jim Schaff
 */
public class BNGSingleStateSpecies extends BNGSpecies {
/**
 * BNGSingleStateSpecies constructor comment.
 * @param argName java.lang.String
 */
public BNGSingleStateSpecies(String argName, Expression argConc, int argNtwkFileIndx) {
	super(argName, argConc, argNtwkFileIndx);
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 5:33:12 PM)
 * @return boolean
 */
public boolean isWellDefined() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (3/13/2006 2:50:46 PM)
 * @return boolean
 */
public BNGSpecies[] parseBNGSpeciesName() {
	return new BNGSpecies[] {this};
}
}
