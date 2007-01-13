package cbit.vcell.bionetgen;
/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 12:49:34 PM)
 * @author: Jim Schaff
 */
public class BNGOutputSpec {
	private BNGParameter[] bngParams = null;
	private BNGMolecule[] bngMoleculeTypes = null;
	private BNGSpecies[] bngSpecies = null;
	private BNGReactionRule[] bngReactionRules = null;
	private BNGReaction[] bngReactions = null;
	private ObservableGroup[] bngObservableGroups = null;

/**
 * BNGOutputSpec constructor comment.
 */
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


/**
 * BNGOutputSpec constructor comment.
 */
public BNGMolecule[] getBNGMolecules() {
	return	bngMoleculeTypes;
}


/**
 * BNGOutputSpec constructor comment.
 */
public BNGParameter[] getBNGParams() {
	return bngParams;
}


/**
 * BNGOutputSpec constructor comment.
 */
public BNGReactionRule[] getBNGReactionRules() {
	return bngReactionRules;
}


/**
 * BNGOutputSpec constructor comment.
 */
public BNGReaction[] getBNGReactions() {
	return bngReactions;
}


/**
 * BNGOutputSpec constructor comment.
 */
public BNGSpecies[] getBNGSpecies() {
	return	bngSpecies;
}


/**
 * BNGOutputSpec constructor comment.
 */
public ObservableGroup[] getObservableGroups() {
	return bngObservableGroups;
}


/**
 * BNGOutputSpec constructor comment.
 */
public void setBNGMolecules(BNGMolecule[] argMolecules) {
	if (argMolecules.length >= 0) {
		bngMoleculeTypes = argMolecules;
	}
}


/**
 * BNGOutputSpec constructor comment.
 */
public void setBNGParams(BNGParameter[] argParams) {
	if (argParams.length >= 0) {
		bngParams = argParams;
	}
}


/**
 * BNGOutputSpec constructor comment.
 */
public void setBNGReactions(BNGReaction[] argReactions) {
	if (argReactions.length >= 0) {
		bngReactions = argReactions;
	}
}


/**
 * BNGOutputSpec constructor comment.
 */
public void setBNGReactions(BNGReactionRule[] argRxnRules) {
	if (argRxnRules.length >= 0) {
		bngReactionRules = argRxnRules;
	}
}


/**
 * BNGOutputSpec constructor comment.
 */
public void setBNGSpecies(BNGSpecies[] argSpecies) {
	if (argSpecies.length >= 0) {
		bngSpecies = argSpecies;
	}
}


/**
 * BNGOutputSpec constructor comment.
 */
public void setObservableGroups(ObservableGroup[] argObservables) {
	if (argObservables.length >= 0) {
		bngObservableGroups = argObservables;
	}
}
}