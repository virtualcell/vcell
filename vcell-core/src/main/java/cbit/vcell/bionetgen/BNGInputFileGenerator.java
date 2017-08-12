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
import java.io.PrintWriter;
/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 3:16:28 PM)
 * @author: Jim Schaff
 */
public class BNGInputFileGenerator {
	private String baseFileName = null;
	private BNGInputSpec bngInputSpecification = null;
	private int maxIterations = 100;					// Maximum # of rule applications to perform for iterative network generation
	private int maxAggregates = 1000;					// Maximum # of multi-state species in an aggregate.

/**
 * BNGInputFileGenerator constructor comment.
 */
public BNGInputFileGenerator(String argFileName, BNGInputSpec argBngInputSpec, int argMaxIters, int argMaxAggr) {
	super();
	baseFileName = argFileName;
	bngInputSpecification = argBngInputSpec;
	maxIterations = argMaxIters;
	maxAggregates = argMaxAggr;
}


/**
 * BNGInputFileGenerator constructor comment.
 */
public static String createBNGInput(BNGInputSpec bngInputSpecification, int maxIterations, int maxAggregates) {
	java.io.StringWriter stringWriter = new java.io.StringWriter();
	PrintWriter out = new PrintWriter(stringWriter);
	
	if (bngInputSpecification != null) {
		// Fill in the parameters list
		if (bngInputSpecification.getBngParams().length >= 0) {
			out.println("begin parameters");
			for (int i = 0; i < bngInputSpecification.getBngParams().length; i++){
				BNGParameter bngParam = bngInputSpecification.getBngParams()[i];
				out.println(bngParam.getName() + "\t\t" + bngParam.getValue());
			}
			out.println("end parameters");
		}
		out.println("\n\n");

		// Fill in the allowed set of molecule types
		if (bngInputSpecification.getBngSpecies().length >= 0) {
			out.println("begin molecule types");
			for (int i = 0; i < bngInputSpecification.getBngMoleculeTypes().length; i++){
				BNGMolecule bngMoleculeType = bngInputSpecification.getBngMoleculeTypes()[i];
				out.println(bngMoleculeType.getName());
			}
			out.println("end molecule types");
		}
		out.println("\n\n");

		// Fill in the seed set of species
		if (bngInputSpecification.getBngSpecies().length >= 0) {
			out.println("begin species");
			for (int i = 0; i < bngInputSpecification.getBngSpecies().length; i++){
				BNGSpecies bngSpecies = bngInputSpecification.getBngSpecies()[i];
				if (! bngSpecies.isWellDefined()) {
					throw new RuntimeException("Cannot have wildcard species in the initial set of species.");
				}
				out.println(bngSpecies.getName() + "\t\t" + bngSpecies.getConcentration().infix());
			}
			out.println("end species");
		}
		out.println("\n\n");
		
		// Fill in the reaction rules
		if (bngInputSpecification.getBngReactionRules().length >= 0) {
			out.println("begin reaction rules");
			for (int i = 0; i < bngInputSpecification.getBngReactionRules().length; i++){
				BNGReactionRule bngRxn = bngInputSpecification.getBngReactionRules()[i];
				out.println(bngRxn.writeReaction());
			}
			out.println("end reaction rules");
		}
		out.println("\n\n");
		
		// Fill in the observables rules
		if (bngInputSpecification.getObservableRules().length >= 0) {
			out.println("begin observables");
			for (int i = 0; i < bngInputSpecification.getObservableRules().length; i++){
				ObservableRule bngObsRule = bngInputSpecification.getObservableRules()[i];
				out.println(bngObsRule.getTypeStr() + "\t\t" + bngObsRule.getName() + "\t\t" + bngObsRule.getRule().getName());
			}
			out.println("end observables");
		}
		out.println("\n\n#------Iterative network generation \n");

		// Fill out network generation and sbml file generation commands
		out.println("generate_network({overwrite=>1});");
		out.println("writeSBML({});");
	}
	out.println("");

	out.flush();
	out.close();
	stringWriter.flush();
	return stringWriter.getBuffer().toString();
}


/**
 * BNGInputFileGenerator constructor comment.
 */
public void generateBNGInputFile() {
	java.io.FileOutputStream osBngInput = null;
	try {
		osBngInput = new java.io.FileOutputStream(baseFileName);
	}catch (java.io.IOException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("error opening BioNetGen input file '"+baseFileName+": "+e.getMessage());
	}	
		
	PrintWriter bngInputFile = new PrintWriter(osBngInput);
	String bngInputString = createBNGInput(getBNGInputSpec(), maxIterations, maxAggregates);
	bngInputFile.print(bngInputString);
	bngInputFile.close();
}


/**
 * BNGInputFileGenerator constructor comment.
 */
public String getBaseFileName() {
	return baseFileName;
}


/**
 * BNGInputFileGenerator constructor comment.
 */
public BNGInputSpec getBNGInputSpec() {
	return bngInputSpecification;
}
}
