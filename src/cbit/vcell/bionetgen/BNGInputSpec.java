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
/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 12:48:12 PM)
 * @author: Jim Schaff
 */
public class BNGInputSpec {
	private BNGParameter[] bngParams = null;				// Parameter list
	private BNGMolecule[] bngMoleculeTypes = null;			// Molecule Type list
	private BNGSpecies[] bngSpecies = null;					// Species list
	private BNGReactionRule[] bngReactionRules = null;		// Reaction rules list
	private ObservableRule[] observableRules = null;		// Observable rules list

/**
 * BNGInputSpec constructor comment.
 */
public BNGInputSpec(BNGParameter[] argParams, BNGMolecule[] argMolTypes, BNGSpecies[] argSpecies, BNGReactionRule[] argReactionRules, ObservableRule[] argObservables) {
	super();
	if (argParams.length >= 0) {
		bngParams = argParams;
	}
	if (argMolTypes.length >= 0) {
		bngMoleculeTypes = argMolTypes;
	}
	if (argSpecies.length >= 0) {
		bngSpecies = argSpecies;
	}
	if (argReactionRules.length >= 0) {
		bngReactionRules = argReactionRules;
	}
	if (argObservables.length >= 0) {
		observableRules = argObservables;
	}
}


/**
 * BNGInputSpec constructor comment.
 */
public BNGMolecule[] getBngMoleculeTypes() {
	return bngMoleculeTypes;
}


/**
 * BNGInputSpec constructor comment.
 */
public BNGParameter[] getBngParams() {
	return bngParams;
}


/**
 * BNGInputSpec constructor comment.
 */
public BNGReactionRule[] getBngReactionRules() {
	return bngReactionRules;
}


/**
 * BNGInputSpec constructor comment.
 */
public BNGSpecies[] getBngSpecies() {
	return bngSpecies;
}


/**
 * BNGInputSpec constructor comment.
 */
public ObservableRule[] getObservableRules() {
	return	observableRules;
}


/**
 * BNGInputSpec constructor comment.
 */
public void setBngMoleculeTypes(BNGMolecule[] argMolecules) {
	if (argMolecules.length >= 0) {
		bngMoleculeTypes = argMolecules;
	}
}


/**
 * BNGInputSpec constructor comment.
 */
public void setBngParams(BNGParameter[] argParams) {
	if (argParams.length >= 0) {
		bngParams = argParams;
	}
}


/**
 * BNGInputSpec constructor comment.
 */
public void setBngReactionRules(BNGReactionRule[] argReactionRules) {
	if (argReactionRules.length >= 0) {
		bngReactionRules = argReactionRules;
	}
}


/**
 * BNGInputSpec constructor comment.
 */
public void setBngSpecies(BNGSpecies[] argSpecies) {
	if (argSpecies.length >= 0) {
		bngSpecies = argSpecies;
	}
}


/**
 * BNGInputSpec constructor comment.
 */
public void setObservableRules(ObservableRule[] argObservables) {
	if (argObservables.length >= 0) {
		observableRules = argObservables;
	}
}
}
