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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (1/13/2006 5:34:26 PM)
 * @author: Jim Schaff
 */
public class BNGMultiStateSpecies extends BNGSpecies {
	private BNGSpeciesComponent[] speciesComponents = null;

/**
 * BNGMultiStateSpecies constructor comment.
 * @param argName java.lang.String
 */
public BNGMultiStateSpecies(String argName, Expression argConc, int argNtwkFileIndx) {
	super(argName, argConc, argNtwkFileIndx);
	setComponentStates();
}

public String extractMolecularPatternSignature(){
	ArrayList<String> sigs = new ArrayList<String>();
	for (BNGSpeciesComponent comp : speciesComponents){
		sigs.add(comp.extractComponentPatternSignature());
	}
	Collections.sort(sigs);
	StringBuffer buffer = new StringBuffer();
	buffer.append(extractMolecularTypeName()+"(");
	for (int i=0;i<sigs.size();i++){
		buffer.append(sigs.get(i));
		if (i<sigs.size()-1){
			buffer.append(",");
		}
	}
	buffer.append(")");
	String molecularSignature = buffer.toString();
	return molecularSignature;
}

public String extractMolecularTypeName(){
	String molecularPatternString = getName();
	return molecularPatternString.substring(0, molecularPatternString.indexOf("("));
}

/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 5:34:26 PM)
 * @return boolean
 */
public BNGSpeciesComponent[] getComponents() {
	return speciesComponents;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 5:34:26 PM)
 * @return boolean
 */
public String getComponentState(int compIndx) {
	return speciesComponents[compIndx].getCurrentState();
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 5:34:26 PM)
 * @return boolean
 */
public int getNumComponents() {
	return speciesComponents.length;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 5:34:26 PM)
 * @return boolean
 */
public boolean isWellDefined() {
	// NEEDS TO BE CHANGED ...
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (3/13/2006 2:50:35 PM)
 * @return boolean
 */
public BNGSpecies[] parseBNGSpeciesName() {
	// Need to parse BNGMultiStateSpecies that have wild cards ("*") in their names.
	String name = getName();
	name = org.vcell.util.TokenMangler.fixToken(name);
	BNGMultiStateSpecies msSpecies = new BNGMultiStateSpecies(name, getConcentration(), getNetworkFileIndex());
	return new BNGSpecies[] {msSpecies};
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 5:34:26 PM)
 * @return boolean
 */
public void setComponentStates() {
	// parse the species name to get the number of components and its current state.
	String speciesName = getName();
	int openParanIndx = speciesName.indexOf("(");
	int closeParanIndx = speciesName.indexOf(")");
	String nameSubString = speciesName.substring(openParanIndx+1, closeParanIndx);

	String componentDelimiter = ",";
	java.util.StringTokenizer nameStrToken = new java.util.StringTokenizer(nameSubString, componentDelimiter);

	String component = new String("");
	Vector componentsVector = new Vector();
	while (nameStrToken.hasMoreTokens()) {
		component = nameStrToken.nextToken();

		int tildaIndx = component.indexOf("~");
		if (tildaIndx > -1) {
			String componentName = component.substring(0, tildaIndx);
			String statename = component.substring(tildaIndx+1);
			componentsVector.addElement(new BNGSpeciesComponent(componentName, statename, null));
//			System.out.println("component : " + componentName + ";\t state : " + statename);
		} else {
			String componentName = component;
			componentsVector.addElement(new BNGSpeciesComponent(componentName, null, null));
//			System.out.println("component : " + componentName);
		}
	}

	speciesComponents = (BNGSpeciesComponent[])org.vcell.util.BeanUtils.getArray(componentsVector, BNGSpeciesComponent.class);
}
}
