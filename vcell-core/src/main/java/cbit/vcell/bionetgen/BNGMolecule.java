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
import org.vcell.util.ArrayUtils;
import java.io.Serializable;
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (9/5/2006 9:41:36 AM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGMolecule implements Serializable {
	private final String molName;
	BNGSpeciesComponent[] molComponents = null;

	/**
	 * BNGMolecule constructor comment.
	 */
	public BNGMolecule(String argName) {
		super();
		molName = argName;
		setComponentsStates();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (1/13/2006 5:34:26 PM)
	 * @return boolean
	 */
	public String getActualName() {
		// parse the species name to get the actual name of molecule. It is the string preceding the '(' char in the name.
		String speciesName = getName();
		int openParanIndx = speciesName.indexOf("(");
        return speciesName.substring(0, openParanIndx);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (1/13/2006 5:34:26 PM)
	 * @return boolean
	 */
	public String getName() {
		return molName;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (1/13/2006 5:34:26 PM)
	 */
	public void setComponentsStates() {
		// parse the species name to get the number of components and its current state.
		String speciesName = getName();
		String nameSubString;
		int openParanIndx = speciesName.indexOf("(");
		int closeParanIndx = speciesName.indexOf(")");
		if (openParanIndx > -1 && closeParanIndx > -1) {
			nameSubString = speciesName.substring(openParanIndx+1, closeParanIndx);
		} else {
			return;
		}

		String componentDelimiter = ",";
		String tildaDelimiter = "~";
		java.util.StringTokenizer nameStrToken = new java.util.StringTokenizer(nameSubString, componentDelimiter);

		String token1;
		String token2;
		Vector<BNGSpeciesComponent> componentsVector = new Vector<>();
		Vector<String> statesVector = new Vector<>();
		while (nameStrToken.hasMoreTokens()) {
			token1 = nameStrToken.nextToken();

			java.util.StringTokenizer componentStrToken = new java.util.StringTokenizer(token1, tildaDelimiter);
			String componentName = null;
			int count = 0;
			while (componentStrToken.hasMoreTokens()) {
				token2 = componentStrToken.nextToken();

				if (count == 0) {
					componentName = token2;
				} else {
                    statesVector.addElement(token2);
				}
				count++;
			}

			String[] componentStatesNames = statesVector.toArray(String[]::new);
			statesVector.clear();
			componentsVector.addElement(new BNGSpeciesComponent(componentName, null, componentStatesNames));
		}

		this.molComponents = componentsVector.toArray(BNGSpeciesComponent[]::new);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (1/13/2006 5:34:26 PM)
	 * @return boolean
	 */
	public String toString() {
		return getName();
	}
}
