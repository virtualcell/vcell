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
 * Creation date: (1/16/2006 4:44:22 PM)
 * @author: Jim Schaff
 */
public class BNGInputFileGeneratorTest {
/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 4:44:49 PM)
 * @param args java.lang.String[]
 */
private static ObservableRule[] getObservables() throws cbit.vcell.parser.ExpressionException {
	ObservableRule[] obsRules = new ObservableRule[2];

	obsRules[0] = new ObservableRule("RecDimer", ObservableRule.MoleculeType, new BNGComplexSpecies("R(*, *).*", new Expression("0.0"), 0));
	obsRules[1] = new ObservableRule("SpecDimer", ObservableRule.SpeciesType, new BNGComplexSpecies("R(*, *).*", new Expression("0.0"), 0));
	
	return obsRules;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 4:44:49 PM)
 * @param args java.lang.String[]
 */
private static BNGParameter[] getParams() {
	BNGParameter[] params = new BNGParameter[5];

	params[0] = new BNGParameter("A_init", 0.5);
	params[1] = new BNGParameter("B_init", 0.25);
	params[2] = new BNGParameter("C_init", 0.0);
	params[3] = new BNGParameter("kf", 1.00);
	params[4] = new BNGParameter("kr", 0.25);
	
	return params;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 4:44:49 PM)
 * @param args java.lang.String[]
 */
private static BNGReactionRule[] getReactionRules() throws cbit.vcell.parser.ExpressionException {
	BNGReactionRule[] rules = new BNGReactionRule[2];

	BNGParameter[] rxnParams = null;
	BNGSpecies[] reactants = null;
	BNGSpecies[] pdts = null;
	
	rxnParams = new BNGParameter[1];
	rxnParams[0] = new BNGParameter("kf", 0.34);
	reactants = new BNGSpecies[2];
	reactants[0] = new BNGMultiStateSpecies("R(0, *)", new Expression("1.2"), 0);
	reactants[1] = new BNGSingleStateSpecies("A", new Expression("0.23"), 0);
	pdts = new BNGSpecies[1];
	pdts[0] = new BNGMultiStateSpecies("R(1, *)", new Expression("1.2"), 0);
	
	rules[0] = new BNGReactionRule(rxnParams, reactants, pdts, false);

	rxnParams = new BNGParameter[2];
	rxnParams[0] = new BNGParameter("kf", 0.75);
	rxnParams[1] = new BNGParameter("kr", 0.25);
	reactants = new BNGSpecies[2];
	reactants[0] = new BNGMultiStateSpecies("R(*, 0)", new Expression("0.56"), 0);
	reactants[1] = new BNGSingleStateSpecies("B", new Expression("0.3"), 0);
	pdts = new BNGSpecies[1];
	pdts[0] = new BNGMultiStateSpecies("R(*, 1)", new Expression("0.22"), 2);
	
	rules[1] = new BNGReactionRule(rxnParams, reactants, pdts, true);

	return rules;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 4:44:49 PM)
 * @param args java.lang.String[]
 */
private static BNGSpecies[] getSpecies() throws cbit.vcell.parser.ExpressionException {
	BNGSpecies[] species = new BNGSpecies[3];
	species[0] = new BNGSingleStateSpecies("A", new Expression("1.25"), 0);
	species[1] = new BNGSingleStateSpecies("B", new Expression(".25"), 0);
	species[2] = new BNGMultiStateSpecies("R(0,0)", new Expression("0.0"), 0);
	
	return species;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 4:44:49 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		BNGParameter[] params = getParams();
		BNGSpecies[] species = getSpecies();
		BNGReactionRule[] rxnRules = getReactionRules();
		ObservableRule[] observables = getObservables();
		
		BNGInputSpec inputSpec = new BNGInputSpec(params, null, species, rxnRules, observables);

		BNGInputFileGenerator inputFileGen = new BNGInputFileGenerator("C:\\VCell\\inputFile.in", inputSpec, 100, 1000);
		inputFileGen.generateBNGInputFile();
	} catch (cbit.vcell.parser.ExpressionException e) {
		throw new RuntimeException("Could not create conc expression for species : "+e.getMessage());
	}
}
}
