/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package org.vcell.sbml;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;

@SuppressWarnings("serial")
public class SimSpec implements Serializable {
	private final String[] varsList;
	private final double endTime;
	private final int numTimeSteps;
	//private double absTolerance = 1e-10;
	private final double absTolerance;
	public double getAbsTolerance() {
		return absTolerance;
	}

	public double getRelTolerance() {
		return relTolerance;
	}

	private final double relTolerance;
	//private double relTolerance = 1e-12;
	
	public SimSpec(String[] argVarsList, double argEndTime, int argSteps) {
		this(argVarsList,argEndTime,argSteps,1e-10,1e-12);
	}

	public SimSpec(String[] argVarsList, double argEndTime, int argSteps, double absTol, double relTol) {
		if (argVarsList == null) {
			throw new IllegalArgumentException("No variables in list");
		}
		this.varsList = argVarsList;
		this.endTime = argEndTime;
		this.numTimeSteps = argSteps;
		this.absTolerance = absTol;
		this.relTolerance = relTol;
	}
	
	public String getMathematicaVarsListString(String mathematicaContextPrefix) {
		// Write out the variables from file as {v1,v2,...,vn} - one string as required by dataTable in Mathematica.
		String varsListStr = "{";
		for (int i = 0; i < varsList.length; i++) {
			varsListStr += mathematicaContextPrefix+varsList[i];
			if (i < varsList.length-1) { 
				varsListStr += ","; 
			}
		}
		varsListStr += "}";
		return varsListStr;
	}
	public double getEndTime(){
		return endTime;
	}
	public int getNumTimeSteps() {
		return numTimeSteps;
	}
	public double getStepSize(){
		return endTime/numTimeSteps;
	}
	
	public String[] getVarsList(){
		return varsList;
	}
	
	public static SimSpec fromSBML(String sbmlText) throws IOException, SbmlException {
		Element rootSBML = SBMLUtils.readXML(new StringReader(sbmlText));
		Namespace sbmlNamespace = rootSBML.getNamespace();
		//Namespace sbmlNamespace = Namespace.getNamespace("http://www.sbml.org/sbml/level2");
		Element sbmlModelElement = rootSBML.getChild("model",sbmlNamespace);
		//
		// collect names of Species to compare
		//
		Vector<String> varNames = new Vector<String>();
		Element listOfSpeciesElement = sbmlModelElement.getChild("listOfSpecies",sbmlNamespace);
		if (listOfSpeciesElement!=null){
			@SuppressWarnings("unchecked")
			List<Element> speciesElementList = listOfSpeciesElement.getChildren("species", sbmlNamespace);
			for (Element speciesElement : speciesElementList){
				varNames.add(speciesElement.getAttributeValue("id"));
			}
		}else{
			System.out.println("NO species in SBML model (parameters only) ... must be rate-rules only");
		}

		//
		// Specify simulation task.
		//
		String[] varsToTest = varNames.toArray(new String[varNames.size()]);
		float duration = 10f;
		int numTimeSteps = 100;
		SimSpec simSpec = new SimSpec(varsToTest,duration,numTimeSteps);
		return simSpec;
	}
}
