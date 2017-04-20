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
 * Creation date: (1/16/2006 11:12:08 AM)
 * @author: Jim Schaff
 */
public class ObservableRule {
	public static final int SpeciesType = 0;
	public static final int MoleculeType = 1;
	
	private String name = null;
	private int type = 0;
	private BNGSpecies rule = null;
	
/**
 * ObservableRule constructor comment.
 */
public ObservableRule(String argName, int argType, BNGSpecies argObservableRule) {
	super();
	name = argName;
	type = argType;
	rule = argObservableRule;
}


/**
 * ObservableRule constructor comment.
 */
public String getName() {
	return name;
}


/**
 * ObservableRule constructor comment.
 */
public BNGSpecies getRule() {
	return rule;
}


/**
 * ObservableRule constructor comment.
 */
public int getType() {
	return type;
}


/**
 * ObservableRule constructor comment.
 */
public String getTypeStr() {
	String typeStr = null;
	if (type == ObservableRule.SpeciesType) {
		typeStr = "Species";
	}
	if (type == ObservableRule.MoleculeType) {
		typeStr = "Molecules";
	}
	return typeStr;
}


/**
 * ObservableRule constructor comment.
 */
public void setName(String argName) {
	name = argName;
}


/**
 * ObservableRule constructor comment.
 */
public void setRule(BNGSpecies argSpecies) {
	rule = argSpecies;
}


/**
 * ObservableRule constructor comment.
 */
public void setType(int argType) {
	type = argType;
}
}
