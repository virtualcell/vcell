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
import java.util.List;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 12:49:34 PM)
 * @author: Jim Schaff
 */
public class BNGOutputSpec  implements Serializable {
	private BNGParameter[] bngParams = null;
	private BNGMolecule[] bngMoleculeTypes = null;
	private BNGSpecies[] bngSpecies = null;
	private BNGReactionRule[] bngReactionRules = null;
	private BNGReaction[] bngReactions = null;
	private ObservableGroup[] bngObservableGroups = null;

public BNGOutputSpec(BNGParameter[] argParams, BNGMolecule[] argMols, BNGSpecies[] argSpecies, BNGReactionRule[] argRxnRules, BNGReaction[] argReactions, ObservableGroup[] argObservables) {
	super();
	if (argParams.length >= 0) {
		bngParams = argParams;
	}
	if (argMols.length >= 0) {
		bngMoleculeTypes = argMols;
	}
	if (argSpecies.length >= 0) {
		bngSpecies = argSpecies;
	}
	// if (argRxnRules.length >= 0) {
	// Its ok to have ReactionRules array to be null in the output.
	bngReactionRules = argRxnRules;
	// }
	if (argReactions.length >= 0) {
		bngReactions = argReactions;
	}
	if (argObservables.length >= 0) {
		bngObservableGroups = argObservables;
	}
}

public BNGOutputSpec deepClone() {
	BNGParameter[] bngParamsClone = new BNGParameter[bngParams.length];
	for (int i = 0; i < bngParams.length; i++) {
		bngParamsClone[i] = bngParams[i].deepClone();
	}
	BNGMolecule[] bngMoleculeTypesClone = new BNGMolecule[bngMoleculeTypes.length];
	for (int i = 0; i < bngMoleculeTypes.length; i++) {
		bngMoleculeTypesClone[i] = bngMoleculeTypes[i].deepClone();
	}
	BNGSpecies[] bngSpeciesClone = new BNGSpecies[bngSpecies.length];
	for (int i = 0; i < bngSpecies.length; i++) {
		bngSpeciesClone[i] = bngSpecies[i].deepClone();
	}
	BNGReactionRule[] bngReactionRulesClone = new BNGReactionRule[bngReactionRules.length];
	for (int i = 0; i < bngReactionRules.length; i++) {
		bngReactionRulesClone[i] = bngReactionRules[i].deepClone();
	}
	BNGReaction[] bngReactionsClone = new BNGReaction[bngReactions.length];
	for (int i = 0; i < bngReactions.length; i++) {
		bngReactionsClone[i] = bngReactions[i].deepClone();
	}
	ObservableGroup[] bngObservableGroupsClone = new ObservableGroup[bngObservableGroups.length];
	for (int i = 0; i < bngObservableGroups.length; i++) {
		bngObservableGroupsClone[i] = bngObservableGroups[i].deepClone();
	}
	return new BNGOutputSpec(bngParamsClone, bngMoleculeTypesClone, bngSpeciesClone, bngReactionRulesClone, bngReactionsClone, bngObservableGroupsClone);
}
public BNGMolecule[] getBNGMolecules() {
	return	bngMoleculeTypes;
}
public BNGParameter[] getBNGParams() {
	return bngParams;
}
public BNGReactionRule[] getBNGReactionRules() {
	return bngReactionRules;
}
public BNGReaction[] getBNGReactions() {
	return bngReactions;
}
public BNGSpecies[] getBNGSpecies() {
	return	bngSpecies;
}
public ObservableGroup[] getObservableGroups() {
	return bngObservableGroups;
}

public void setBNGMolecules(BNGMolecule[] argMolecules) {
	if (argMolecules.length >= 0) {
		bngMoleculeTypes = argMolecules;
	}
}
public void setBNGParams(BNGParameter[] argParams) {
	if (argParams.length >= 0) {
		bngParams = argParams;
	}
}
public void setBNGReactions(BNGReaction[] argReactions) {
	if (argReactions.length >= 0) {
		bngReactions = argReactions;
	}
}
public void setBNGReactions(BNGReactionRule[] argRxnRules) {
	if (argRxnRules.length >= 0) {
		bngReactionRules = argRxnRules;
	}
}
public void setBNGSpecies(BNGSpecies[] argSpecies) {
	if (argSpecies.length >= 0) {
		bngSpecies = argSpecies;
	}
}
public void setObservableGroups(ObservableGroup[] argObservables) {
	if (argObservables.length >= 0) {
		bngObservableGroups = argObservables;
	}
}

public static int getFirstAvailableSpeciesIndex(List<BNGSpecies> bngSpecies) {
	int indexCandidate = 0;
	for(BNGSpecies s : bngSpecies) {
		if(indexCandidate < s.getNetworkFileIndex()) {
			indexCandidate = s.getNetworkFileIndex();
		}
	}
	return indexCandidate+1;	// this is the first index not in use
}
public static BNGSpecies findMatch(BNGSpecies ours, List<BNGSpecies> theirsList, BNGSpecies.SignatureDetailLevel sigDetailLevel) {
	
	// more expensive and difficult - we verify if the 2 species are isomorphic (different only in bond numbering)
//	SpeciesIsomorphismInspector sii = new SpeciesIsomorphismInspector();
//	sii.initialize(ours);
//	for(BNGSpecies theirs : theirsList) {
//		if(sii.isIsomorphism(theirs)) {
//			return theirs;
//		}
//	}
	String ourSignature = BNGSpecies.getShortSignature(ours, sigDetailLevel);
	// using jgrapht
	SpeciesGraphIsomorphismInspector sgii = new SpeciesGraphIsomorphismInspector();
	for(BNGSpecies theirs : theirsList) {
		String theirSignature = BNGSpecies.getShortSignature(theirs, sigDetailLevel);
		if(!theirSignature.equals(ourSignature)) {
			continue;			// no point to compute the isomorfism if the signatures don't match
		}
		if(sgii.isIsomorphism(ours, theirs)) {
			return theirs;
		}
	}
	return null;
}

}
