/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import cbit.vcell.math.Variable;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
public class SpeciesContextMapping {
	
	private SpeciesContext speciesContext = null;	
	private Variable       variable = null;    // variable, constant, or function

	//
	// input to structural analysis
	//
	private boolean bFastParticipant    = false;
	private boolean bPDERequired	    = false;
	private boolean bHasEventAssignment = false;
	private boolean bHasHybridReaction = false;
//	private boolean bAdvecting        = false;
//	private boolean bDiffusing        = false;

	//
	// output from structural analysis of slow reactions
	//
	private Expression dependencyExp = null;   // if absolute dependent
	private Expression rateExp = null;

	//
	// output from structural analysis of fast reactions
	//
	private Expression fastInvariant = null;   // if only dependent on the fast time scale
	private Expression fastRate      = null;          
/**
 * SpeciesContextMapping constructor comment.
 */
public SpeciesContextMapping(SpeciesContext speciesContext) {
	this.speciesContext = speciesContext;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:55:08 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getDependencyExpression() {
	return this.dependencyExp;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:59:19 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getFastInvariant() {
	return this.fastInvariant;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:50:50 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getFastRate() {
	return this.fastRate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:57:40 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getRate() {
	return this.rateExp;
}
/**
 * @return cbit.vcell.model.SpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return speciesContext;
}
/**
 * @return cbit.vcell.math.Variable
 */
public Variable getVariable() {
	return variable;
}

public boolean isFastParticipant() {
	return bFastParticipant;
}

public boolean isPDERequired() {
	return this.bPDERequired;
}

public boolean hasEventAssignment() {
	return this.bHasEventAssignment;
}

public boolean hasHybridReaction() {
	return this.bHasHybridReaction;
}

/*
public boolean isDiffusing() {
	return this.bDiffusing;
}

public boolean isAdvecting() {
	return this.bAdvecting;
}
*/

/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 4:16:14 PM)
 * @param argDepencencyExpression cbit.vcell.parser.Expression
 */
public void setDependencyExpression(Expression argDepencencyExpression) {
	this.dependencyExp = argDepencencyExpression;
}

/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:53:28 PM)
 * @param fastInvariantExpression cbit.vcell.parser.Expression
 */
public void setFastInvariant(Expression fastInvariantExpression) {
	this.fastInvariant = fastInvariantExpression;
}

public void setPDERequired(boolean pdeRequired) {
	this.bPDERequired = pdeRequired;
}

public void setHasEventAssignment(boolean hasEventAssignment) {
	this.bHasEventAssignment = hasEventAssignment;
}

public void setHasHybridReaction(boolean hasHybridReaction) {
	this.bHasHybridReaction = hasHybridReaction;
}

/*
public void setDiffusing(boolean diffusing) {
	this.bDiffusing = diffusing;
}

public void setAdvecting(boolean advecting) {
	this.bAdvecting = advecting;
}
*/

/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 4:12:59 PM)
 * @param isFastParticipant boolean
 */
public void setFastParticipant(boolean isFastParticipant) {
	this.bFastParticipant = isFastParticipant;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 3:50:13 PM)
 * @param fastRateExpression cbit.vcell.parser.Expression
 */
public void setFastRate(Expression argFastRateExpression) {
	this.fastRate = argFastRateExpression;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2001 4:18:22 PM)
 * @param rate cbit.vcell.parser.Expression
 */
public void setRate(Expression argRate) {
	this.rateExp = argRate;
}
/**
 * This method was created in VisualAge.
 * @param variable cbit.vcell.math.Variable
 */
public void setVariable(Variable variable) {
	this.variable = variable;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Mapping <sc="+speciesContext.getName()+"> to <var="+variable+">";
}
}
