/**
 * 
 */
package org.vcell.sbml;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;

public class SimSpec {
	private String[] varsList = null;
	private double endTime = 0.0;
	private int numTimeSteps = 0;
	private double absTolerance = 1e-10;
	public double getAbsTolerance() {
		return absTolerance;
	}

	public double getRelTolerance() {
		return relTolerance;
	}

	private double relTolerance = 1e-12;
	
	public SimSpec(String[] argVarsList, double argEndTime, int argSteps) {
		if (argVarsList == null) {
			throw new IllegalArgumentException("No variables in list");
		}
		this.varsList = argVarsList;
		this.endTime = argEndTime;
		this.numTimeSteps = argSteps;
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