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

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:43:29 AM)
 * @author: Jim Schaff
 */
public class ObservableGroup  implements Serializable {
	//
	// 'speciesMultiplicity' and 'listofSpecies' should be of the same length, since the 'speciesMultiplicity' represents the
	// coefficient of a species concentration if the observable is of 'molecule' type. By default, it is 1 for each species in 
	// the 'listofSpecies' (if it is of'species' type, the coefficient is 1).
	//
	private String observableGroupName = null;
	private BNGSpecies[] listofSpecies = null;
	private int[] speciesMultiplicity = null;

public ObservableGroup(String argName, BNGSpecies[] argSpeciesList, int[] argSpecMultiplicity) {
	super();
	observableGroupName = argName;
	listofSpecies = argSpeciesList;
	if (argSpecMultiplicity != null) {
		if (argSpeciesList.length != argSpecMultiplicity.length) {
			throw new RuntimeException("The species list and speciesMultiplicity list should have same length.");
		} else {
			speciesMultiplicity = argSpecMultiplicity;
		}
	} else {
		// if argSpecMultiplicity is null, create it to have same length as listofSpecies and fill it with default coeffs of 1
		speciesMultiplicity = new int[listofSpecies.length];
		for (int i = 0; i < speciesMultiplicity.length; i++){
			speciesMultiplicity[i] = 1;
		}
	}
}
public BNGSpecies[] getListofSpecies() {
	return listofSpecies;
}
public String getObservableGroupName() {
	return observableGroupName;
}
public int[] getSpeciesMultiplicity() {
	return speciesMultiplicity;
}
public void setListofSpecies(BNGSpecies[] argSpeciesList) {
	listofSpecies = argSpeciesList;
}
public void setObservableName(String argName) {
	observableGroupName = argName;
}
public void setSpeciesMultiplicity(int[] argSpeciesMultiplicity) {
	speciesMultiplicity = argSpeciesMultiplicity;
}

public String toString() {
	String obsGp = getObservableGroupName() + ";\t\t";
	for (int i = 0; i < listofSpecies.length; i++){
		if (i == listofSpecies.length-1) {
			if (speciesMultiplicity[i] == 1) {
				obsGp = obsGp + listofSpecies[i].getNetworkFileIndex();
			} else {
				obsGp = obsGp + speciesMultiplicity[i] + "*" + listofSpecies[i].getNetworkFileIndex();
			}
		} else {
			if (speciesMultiplicity[i] == 1) {
				obsGp = obsGp + listofSpecies[i].getNetworkFileIndex() + ",";
			} else {
				obsGp = obsGp + speciesMultiplicity[i] + "*" + listofSpecies[i].getNetworkFileIndex() + ",";
			}
		}
	}
	return obsGp;
}
public String toBnglString() {
	String obsGp = getObservableGroupName() + " ";
	for (int i = 0; i < listofSpecies.length; i++){
		if (i == listofSpecies.length-1) {
			if (speciesMultiplicity[i] == 1) {
				obsGp = obsGp + listofSpecies[i].getNetworkFileIndex();
			} else {
				obsGp = obsGp + speciesMultiplicity[i] + "*" + listofSpecies[i].getNetworkFileIndex();
			}
		} else {
			if (speciesMultiplicity[i] == 1) {
				obsGp = obsGp + listofSpecies[i].getNetworkFileIndex() + ",";
			} else {
				obsGp = obsGp + speciesMultiplicity[i] + "*" + listofSpecies[i].getNetworkFileIndex() + ",";
			}
		}
	}
	return obsGp;
}

}
