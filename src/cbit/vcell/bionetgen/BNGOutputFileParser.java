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
import cbit.vcell.model.Model;
import cbit.vcell.parser.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.*;

import org.vcell.model.rbm.RbmUtils;
import org.vcell.util.Pair;

import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (1/17/2006 10:36:29 AM)
 * @author: Jim Schaff
 */
public class BNGOutputFileParser {
/**
 * BNGOutputFileParser constructor comment.
 */
public BNGOutputFileParser() {
	super();
}


public static BNGOutputSpec createBngOutputSpec(File bngOutputFile) throws FileNotFoundException, IOException {
	// Check if BioNetGen Output file exists
	if (!bngOutputFile.exists()){
		throw new java.io.FileNotFoundException("BioNetGen Output file "+bngOutputFile.getPath()+" not found");
	}

	//
	// Read characters from BNGOutput file into character array and transfer into string buffer.
	//
	long bngoutputFileLength = bngOutputFile.length();
	StringBuffer stringBuffer = new StringBuffer();
	try (BufferedReader br = new BufferedReader(new FileReader(bngOutputFile))) {
		char charArray[] = new char[100000];
		while (true) {
			int numRead = br.read(charArray, 0, charArray.length);
			if (numRead > 0) {
				stringBuffer.append(charArray,0,numRead);
			} else if (numRead == -1) {
				break;
			}
		}
	}

	if (stringBuffer.length() != bngoutputFileLength){
		System.out.println("<<<SYSOUT ALERT>>>BNGOutputFile, read "+stringBuffer.length()+" of "+bngoutputFileLength+" bytes of input file");
	}

	String inputString = stringBuffer.toString();
	return createBngOutputSpec(inputString);
}

public static String extractCompartments (String inputString) {
	String newLineDelimiters = "\n\r";
	String resultDelimiter = "\n";
	StringTokenizer lineTokenizer = new StringTokenizer(inputString, newLineDelimiters);
	
	String token1 = new String("");
	String token2 = new String("");
	String blankDelimiters = " \t";
	String commaDelimiters = ",";

	// Need to track which type of list we are reading in from the list delimiter. For convenience, have a separate variable for each.
	int PARAM_LIST = 1;
	int MOLECULE_LIST = 2;
	int SPECIES_LIST = 3;
	int RXN_RULES_LIST = 4;
	int RXN_LIST = 5;
	int OBS_GPS_LIST = 6;
	int list = 0;

	StringBuilder result = new StringBuilder();
	
	while (lineTokenizer.hasMoreTokens()) {
		token1 = lineTokenizer.nextToken();

		// Identify type of list ...
		if (token1.equals("begin parameters")) {
			list = PARAM_LIST;
			result.append(token1+resultDelimiter);
			continue;
		} else if (token1.equals("begin molecule types")) {
			list = MOLECULE_LIST;
			result.append(token1+resultDelimiter);
			continue;
		} else if (token1.equals("begin species")) {
			list = SPECIES_LIST;
			result.append(token1+resultDelimiter);
			continue;
		} else if (token1.equals("begin reaction rules")) {
			list = RXN_RULES_LIST;
			result.append(token1+resultDelimiter);
			continue;
		} else if (token1.equals("begin reactions")) {
			list = RXN_LIST;
			result.append(token1+resultDelimiter);
			continue;
		} else if (token1.equals("begin groups")) {
			list = OBS_GPS_LIST;
			result.append(token1+resultDelimiter);
			continue;
		} else if (token1.equals("end parameters") || token1.equals("end molecule types") || token1.equals("end species") || token1.equals("end reaction rules") || token1.equals("end reactions") || token1.equals("end groups")) {
			list = 0;
			result.append(token1+resultDelimiter);
			continue;
		}

		StringTokenizer nextLine = null;
		if (list == 0) {						// keep any junk, everything is valuable!
			result.append(token1+resultDelimiter);
		}
		if (list == PARAM_LIST) {				// copy the list of parameters as is
			result.append(token1+resultDelimiter);
		}
		if (list == SPECIES_LIST) {				// extract compartment in list of molecule types
			nextLine = new StringTokenizer(token1, blankDelimiters);
			int i=0;
			while (nextLine.hasMoreTokens()) {
				token2 = nextLine.nextToken();
				if (token2 != null) {
					token2 = token2.trim();
				}
				System.out.println(token2);
				if(i==0) {
					result.append("\t" + token2);
				}
				if(i==1) {
					Pair<List<String>, String> p = RbmUtils.extractCompartment(token2);
					String structure = p.one.get(0);
					String expression = p.two;
					expression = RbmUtils.extractProduct(expression);
					String s = "@" + structure + ":" + expression;
					System.out.println(s);
					result.append(" " + s);
				}
				if(i==2) {
					result.append(" " + token2);
				}
				i++;
			}
			result.append(resultDelimiter);
		}
		if (list == RXN_LIST) {					// copy the list of reactions as is
			result.append(token1+resultDelimiter);
		}
		if (list == OBS_GPS_LIST) {				// copy the list of observables as is
			result.append(token1+resultDelimiter);
		}
	}
	String s = result.toString();
	return s;
}

public static List<BNGSpecies> createBngSpeciesOutputSpec(String inputString) {

	String newLineDelimiters = "\n\r";
	StringTokenizer lineTokenizer = new StringTokenizer(inputString, newLineDelimiters);
	
	String token1 = new String("");
	String token2 = new String("");
	String blankDelimiters = " \t";
	List<BNGSpecies> speciesVector = new ArrayList<>();

	while (lineTokenizer.hasMoreTokens()) {
		token1 = lineTokenizer.nextToken();

		StringTokenizer nextLine = null;

		// Fill in list of species ONLY
		nextLine = new StringTokenizer(token1, blankDelimiters);
		int i = 0;
		int speciesNtwkFileIndx = 0;
		String speciesName = null;
		Expression speciesConcExpr = null;
		// 'nextLine' is the line with a species and its concentration; the index is necessary to build the reactions.
		while (nextLine.hasMoreTokens()) {
			token2 = nextLine.nextToken();
			if (token2 != null) {
				token2 = token2.trim();
			}
			// First token is index number, second is the name, last token is the concentration.
			if (i == 0) {
				speciesNtwkFileIndx = Integer.parseInt(token2);
			} else if (i == 1) {
				speciesName = token2;
			} else if (i == 2) {
				try {
					speciesConcExpr = new Expression(token2);
				} catch (ExpressionException e) {
					throw new RuntimeException("Error in reading expression for species concentration \""+speciesName+"\"");
				}
			}
			i++;
		}
		// After parsing species from the line, create a new BNGSpecies and add it to speciesVector.
		if (speciesName != null && speciesConcExpr != null && speciesNtwkFileIndx > 0) {
			BNGSpecies newSpecies = null;
			if (speciesName.indexOf(".") >= 0) {
				newSpecies = new BNGComplexSpecies(speciesName, speciesConcExpr, speciesNtwkFileIndx);
			} else if ( (speciesName.indexOf("(") > 0) && (speciesName.indexOf(".") < 0) ) {
				newSpecies = new BNGMultiStateSpecies(speciesName, speciesConcExpr, speciesNtwkFileIndx);
			} else {
				newSpecies = new BNGSingleStateSpecies(speciesName, speciesConcExpr, speciesNtwkFileIndx);
			}
			speciesVector.add(newSpecies);
		}
	}
	return speciesVector;
}

public static BNGOutputSpec createBngOutputSpec(String inputString) {

	String newLineDelimiters = "\n\r";
	StringTokenizer lineTokenizer = new StringTokenizer(inputString, newLineDelimiters);
	
	String token1 = new String("");
	String token2 = new String("");
	String blankDelimiters = " \t";
	String commaDelimiters = ",";

	//
	// Each token is a line from the output (net) file generated by BioNetGen.
	// We have to distinguish the Parameters list, Species list, Reactions list, Obeservable groups list and store them in a BNGOutputSpec object
	// Each list XXX above has a 'begin XXXs' and an 'end XXXs' delimiter.
	//

	// Need to track which type of list we are reading in from the list delimiter. For convenience, have a separate variable for each.
	int PARAM_LIST = 1;
	int MOLECULE_LIST = 2;
	int SPECIES_LIST = 3;
	int RXN_RULES_LIST = 4;
	int RXN_LIST = 5;
	int OBS_GPS_LIST = 6;
	int list = 0;
	Vector<BNGParameter> paramVector = new Vector<BNGParameter>();
	Vector<BNGMolecule> moleculesVector = new Vector<BNGMolecule>();
	Vector<BNGSpecies> speciesVector = new Vector<BNGSpecies>();
//	Vector rxnRulesVector = new Vector();
	Vector<BNGReaction> rxnVector = new Vector<BNGReaction>();
	Vector<ObservableGroup> obsGroupsVector = new Vector<ObservableGroup>();

	while (lineTokenizer.hasMoreTokens()) {
		token1 = lineTokenizer.nextToken();

		// Identify type of list ...
		if (token1.equals("begin parameters")) {
			list = PARAM_LIST;
			continue;
		} else if (token1.equals("begin molecule types")) {
			list = MOLECULE_LIST;
			continue;
		} else if (token1.equals("begin species")) {
			list = SPECIES_LIST;
			continue;
		} else if (token1.equals("begin reaction rules")) {
			list = RXN_RULES_LIST;
			continue;
		} else if (token1.equals("begin reactions")) {
			list = RXN_LIST;
			continue;
		} else if (token1.equals("begin groups")) {
			list = OBS_GPS_LIST;
			continue;
		} else if (token1.equals("end parameters") || token1.equals("end molecule types") || token1.equals("end species") || token1.equals("end reaction rules") || token1.equals("end reactions") || token1.equals("end groups")) {
			list = 0;
			continue;
		}

		StringTokenizer nextLine = null;
		// Fill in list of parameters
		if (list == PARAM_LIST) {
			nextLine = new StringTokenizer(token1, blankDelimiters);
			int i = 0;
			String paramName = null;
			double paramVal = 0.0;
			// 'nextLine' is the line with a parameter and its value (there is an index, but it can be ignored).
			while (nextLine.hasMoreTokens()) {
				token2 = nextLine.nextToken();
				if (token2 != null) {
					token2 = token2.trim();
				}
				// First token is index number, ignore for now.
				if (i == 0) {
					i++;
					continue;
				} else if (i == 1) {
					paramName = token2;
				} else if (i == 2) {
					paramVal = Double.parseDouble(token2);
				}
				i++;
			}
			// After parsing parameter from the line, create a new BNGParameter and add it to paramVector.
			if (paramName != null) {
				paramVector.addElement(new BNGParameter(paramName, paramVal));
			}
		}
		// Fill in list of molecule types
		if (list == MOLECULE_LIST) {
			nextLine = new StringTokenizer(token1, blankDelimiters);
			int i = 0;
//			int speciesNtwkFileIndx = 0;
			String moleculeName = null;
			// 'nextLine' is the line with a molecule (the index can be ignored).
			while (nextLine.hasMoreTokens()) {
				token2 = nextLine.nextToken();
				if (token2 != null) {
					token2 = token2.trim();
				}
				// First token is index number, second is the name, last token is the concentration.
				if (i == 0) {
					i++;
					continue;
				} else if (i == 1) {
					moleculeName = token2;
				}
				i++;
			}
			// After parsing molecules from the line, create a new BNGMolecule and add it to speciesVector.
			if (moleculeName != null) {
				moleculesVector.addElement(new BNGMolecule(moleculeName));
			}
		}
		// Fill in list of species
		if (list == SPECIES_LIST) {
			nextLine = new StringTokenizer(token1, blankDelimiters);
			int i = 0;
			int speciesNtwkFileIndx = 0;
			String speciesName = null;
			Expression speciesConcExpr = null;
			// 'nextLine' is the line with a species and its concentration; the index is necessary to build the reactions.
			while (nextLine.hasMoreTokens()) {
				token2 = nextLine.nextToken();
				if (token2 != null) {
					token2 = token2.trim();
				}
				// First token is index number, second is the name, last token is the concentration.
				if (i == 0) {
					speciesNtwkFileIndx = Integer.parseInt(token2);
				} else if (i == 1) {
					speciesName = token2;
				} else if (i == 2) {
					try {
						speciesConcExpr = new Expression(token2);
					} catch (ExpressionException e) {
						throw new RuntimeException("Error in reading expression for species concentration \""+speciesName+"\"");
					}
				}
				i++;
			}
			// After parsing species from the line, create a new BNGSpecies and add it to speciesVector.
			if (speciesName != null && speciesConcExpr != null && speciesNtwkFileIndx > 0) {
				BNGSpecies newSpecies = null;
				if (speciesName.indexOf(".") >= 0) {
					newSpecies = new BNGComplexSpecies(speciesName, speciesConcExpr, speciesNtwkFileIndx);
				} else if ( (speciesName.indexOf("(") > 0) && (speciesName.indexOf(".") < 0) ) {
					newSpecies = new BNGMultiStateSpecies(speciesName, speciesConcExpr, speciesNtwkFileIndx);
				} else {
					newSpecies = new BNGSingleStateSpecies(speciesName, speciesConcExpr, speciesNtwkFileIndx);
				}
				speciesVector.addElement(newSpecies);
			}
		}
		// Fill in list of reaction rules
		if (list == RXN_RULES_LIST) {
			// For now, we can ignore the reaction rules in the network file, since its information has already consumed while generating reactions.
		}
		// Fill in list of reactions
		if (list == RXN_LIST) {
			nextLine = new StringTokenizer(token1, blankDelimiters);
			int i = 0;
			String reactantsSubkey = null;
			String productsSubkey = null;
			BNGSpecies[] reactantsArray = null;
			BNGSpecies[] productsArray = null;
			Expression paramExpression = null;
			String ruleName = null;
			boolean ruleReversed = false;
						
			// 'nextLine' is the line with a reaction : reactants, products, rate consts.; the index can be ignored (for now)
			while (nextLine.hasMoreTokens()) {
				token2 = nextLine.nextToken();
				if (token2 != null) {
					token2 = token2.trim();
				}
				// First token is index number, can be ignored for a reaction.
				if (i == 0) {
					i++;
					continue;
				} else if (i == 1) {
					reactantsSubkey = token2;
					// This string is a list of numbers (denoting species index) separated by commas, representing the reactant(s)
					StringTokenizer nextPart = new StringTokenizer(token2, commaDelimiters);
					String token3 = null;
					int specNo = 0;
					Vector<BNGSpecies> reactantVector = new Vector<BNGSpecies>();
					while (nextPart.hasMoreTokens()) {
						token3 = nextPart.nextToken();
						if (token3 != null) {
							token3 = token3.trim();
						}
						// Get the species index from token and extract the corresponding species from speciesVector as a reactant
						specNo = Integer.parseInt(token3);
						if (speciesVector.size() > 0) {
							reactantVector.addElement(getSpecies(specNo, speciesVector));
						}
					}
					reactantsArray = (BNGSpecies[])org.vcell.util.BeanUtils.getArray(reactantVector, BNGSpecies.class);
				} else if (i == 2) {
					// This string is a list of numbers (species indices) separated by commas, representing the product(s)
					productsSubkey = token2;
					StringTokenizer nextPart = new StringTokenizer(token2, commaDelimiters);
					String token3 = null;
					int specNo = 0;
					Vector<BNGSpecies> productVector = new Vector<BNGSpecies>();
					while (nextPart.hasMoreTokens()) {
						token3 = nextPart.nextToken();
						if (token3 != null) {
							token3 = token3.trim();
						}
						// Get the species index from token and extract the corresponding species from speciesVector as a product
						specNo = Integer.parseInt(token3);
						if (speciesVector.size() > 0) {
							productVector.addElement(getSpecies(specNo, speciesVector));
						}
					}
					productsArray = (BNGSpecies[])org.vcell.util.BeanUtils.getArray(productVector, BNGSpecies.class);
				} else if (i == 3) {
					//
					// This string is a list of parameters (rate consts) for the reaction; some of them could have coefficients.
					// For now, there is only one parameter expression, this may change if kinetic types other than MassAction
					// are supported in BioNetGen at a later date.
					//
					try {
						paramExpression = new Expression(token2);
					} catch (ExpressionException e) {
						throw new RuntimeException("Could not create parameter expression for reaction : " + e.getMessage());
					}
				} else if (i == 4) {
					if(!token2.startsWith("#")) {
						throw new RuntimeException("Unrecognized prefix for the rule name field: " + token2);
					}
					ruleName = token2.substring(1);
					if(ruleName.startsWith("_reverse_")) {
						ruleReversed = true;
						ruleName = ruleName.substring("_reverse_".length());
					} else {
						ruleReversed = false;
					}
//					
//					StringTokenizer ruleOriginTokens = new StringTokenizer(token2, "#()");
//					ruleName = ruleOriginTokens.nextToken();
//					if (ruleOriginTokens.hasMoreTokens()){
//						String reverse = ruleOriginTokens.nextToken();
//						if (reverse.equals("reverse")){
//							ruleReversed = true;
//						}
//					}
				}
				i++;
			}
			// After parsing reaction participants from the line, create a new BNGReaction and add it to rxnVector.
			if (reactantsArray.length > 0 && productsArray.length > 0 && paramExpression != null) {
				BNGReaction newRxn = new BNGReaction(reactantsSubkey, productsSubkey, reactantsArray, productsArray, paramExpression, ruleName, ruleReversed);
				rxnVector.addElement(newRxn);
			}
		}
		// Fill in list of observables (groups)
		if (list == OBS_GPS_LIST) {
			nextLine = new StringTokenizer(token1, blankDelimiters);
			int i = 0;
			String observableName = null;
			Vector<BNGSpecies> obsSpeciesVector = new Vector<BNGSpecies>();
			Vector<Integer> obsMultiplicityVector = new Vector<Integer>();
			// 'nextLine' is the line with an observable group and the species that satisfy the observable rule in the input file
			while (nextLine.hasMoreTokens()) {
				token2 = nextLine.nextToken();
				if (token2 != null) {
					token2 = token2.trim();
				}
				// First token is index number - ignore, second is the observable name, last token is the set of species that satisfy observable rule.
				if (i == 0) {
					i++;
					continue;
				} else if (i == 1) {
					observableName = token2;
				} else if (i == 2) {
					// This string is a list of numbers (species indices) with a multiplicity factor (# of molecules) separated by commas
					StringTokenizer nextPart = new StringTokenizer(token2, commaDelimiters);
					String token3 = null;
					while (nextPart.hasMoreTokens()) {
						token3 = nextPart.nextToken();
						if (token3 != null) {
							token3 = token3.trim();
						}
						if (token3.indexOf("*") > 0) {
							//
							// If the observable group corresponds to a molecule observable rule, the species list in the group
							// could have a multiplicity factor, and occurs as : 'x*yy' where 'x' is the # of molecules
							// and 'yy' is the index of the species in the list of species that appears in the beginning of the
							// network file. Strip out the multiplicity factor from 'token3' and store it in the 'obsMultiplicityVector';
							// strip out the species # and get the corresponding species and store it in 'obsSpeciesVector'.
							//
							int ii = token3.indexOf("*");
							String indx = token3.substring(0, ii);
							Integer obsSpeciesIndx = new Integer(indx);
							obsMultiplicityVector.addElement(obsSpeciesIndx);
							String specNoStr = token3.substring(ii+1);
							BNGSpecies species = getSpecies(Integer.parseInt(specNoStr), speciesVector);
							obsSpeciesVector.addElement(species);
						} else {
							//
							// If the observable group corresponds to a species observable rule, the species list in the group
							// occurs as 'x' where x is the species index in the list of species that appears in the beginning of the
							// network file. Store the species corresponding to the index in the 'obsSpeciesVector' and store a
							// multiplicity of 1 for the corresponding species in the 'obsMultiplicityVector'.
							//
							int specIndx = Integer.parseInt(token3);
							BNGSpecies species = getSpecies(specIndx, speciesVector);
							obsSpeciesVector.addElement(species);
							obsMultiplicityVector.addElement(new Integer(1));
						}
					}
				}
				i++;
			}
			// After parsing observable group from the line, create a new ObservableGroup and add it to observable group vector.
			if (observableName != null && obsMultiplicityVector.size() > 0 && obsSpeciesVector.size() > 0) {
				BNGSpecies[] obsSpeciesArray = (BNGSpecies[])org.vcell.util.BeanUtils.getArray(obsSpeciesVector, BNGSpecies.class);
				Integer[] obsMultiplicityArray = (Integer[])org.vcell.util.BeanUtils.getArray(obsMultiplicityVector, Integer.class);
				int[] obsMultiplicityFactors = new int[obsMultiplicityArray.length];
				for (int k = 0; k < obsMultiplicityFactors.length; k++){
					obsMultiplicityFactors[k] = obsMultiplicityArray[k].intValue();
				}
				ObservableGroup newObsGroup = new ObservableGroup(observableName, obsSpeciesArray, obsMultiplicityFactors);
				obsGroupsVector.addElement(newObsGroup);
			}
		}
	}

	// Create the BNGOutputSpec from the parsed BNG .net (output) file and return.
	BNGParameter[] paramsArray = (BNGParameter[])org.vcell.util.BeanUtils.getArray(paramVector, BNGParameter.class);
	BNGMolecule[] moleculesArray = (BNGMolecule[])org.vcell.util.BeanUtils.getArray(moleculesVector, BNGMolecule.class);
	BNGSpecies[] speciesArray = (BNGSpecies[])org.vcell.util.BeanUtils.getArray(speciesVector, BNGSpecies.class);
	BNGReaction[] rxnsArray = (BNGReaction[])org.vcell.util.BeanUtils.getArray(rxnVector, BNGReaction.class);
	ObservableGroup[] observableGpsArray = (ObservableGroup[])org.vcell.util.BeanUtils.getArray(obsGroupsVector, ObservableGroup.class);
	
	BNGOutputSpec bngOutput = new BNGOutputSpec(paramsArray, moleculesArray, speciesArray, null, rxnsArray, observableGpsArray);
	return bngOutput;
}

public static BNGSpecies getSpecies(int speciesIndx, Vector<BNGSpecies> speciesVector) {
	//
	// Given the species index for a reactant/product species and the species vector, return the species that matches the index.
	//
	BNGSpecies matchingSpecies = null;
	for (int i = 0; i < speciesVector.size(); i++){
		matchingSpecies = speciesVector.elementAt(i);
		if (matchingSpecies.getNetworkFileIndex() == speciesIndx) {
			return matchingSpecies;
		}
	}
	return null;
}

public static void printBNGNetOutput(BNGOutputSpec bngOutputSpec) {
	System.out.println("BNGOutputSpec : ");
	if(bngOutputSpec == null) {
		System.out.println("...bngOutputSpec is null");
		return;
	}
	BNGParameter[] params = bngOutputSpec.getBNGParams();
	BNGMolecule[] moleculeTypes = bngOutputSpec.getBNGMolecules();
	BNGSpecies[] species = bngOutputSpec.getBNGSpecies();
//	BNGReactionRule[] reactionRules = bngOutputSpec.getBNGReactionRules();
	BNGReaction[] reactions = bngOutputSpec.getBNGReactions();
	ObservableGroup[] observableGps = bngOutputSpec.getObservableGroups();

	System.out.println("Parameters : \n");
	for (int i = 0; i < params.length; i++){
		System.out.println(i+1 + ":\t\t"+ params[i].toString());
	}
	System.out.println("\n\nMolecules : \n");
	for (int i = 0; i < moleculeTypes.length; i++){
		System.out.println(i+1 + ":\t\t"+ moleculeTypes[i].toString());
	}	
	System.out.println("\n\nSpecies : \n");
	for (int i = 0; i < species.length; i++){
		System.out.println(i+1 + ":\t\t"+ species[i].toString());
	}	
//	System.out.println("\n\nReaction Rules : \n");
//	for (int i = 0; i < reactionRules.length; i++){
//		System.out.println(i+1 + ":\t\t"+ reactionRules[i].writeReaction());
//	}	
	System.out.println("\n\nReactions : \n");
	for (int i = 0; i < reactions.length; i++){
		System.out.println(i+1 + ":\t\t"+ reactions[i].writeReaction());
	}	
	System.out.println("\n\nObservable Groups : \n");
	for (int i = 0; i < observableGps.length; i++){
		System.out.println(i+1 + ":\t\t"+ observableGps[i].toString());
	}
	System.out.println("\n\nDone : \n");
}
}
