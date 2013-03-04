/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.dictionary;

import cbit.vcell.model.FormalSpeciesInfo;
import cbit.vcell.model.FormalSpeciesType;

/**
 * Class that represents a single Protein from a database
 * Creation date: (6/24/2002 10:38:00 AM)
 * @author: Steven Woolley
 */
 
public class ProteinInfo extends FormalSpeciesInfo {

	public static final double UNKNOWN_MW = -1;
	
    private String organism = null;
    private String accession = null;
    private String keyWords = null;
    private String description = null;
    private double molecularWeight = UNKNOWN_MW;

/**
 * Create a new Protein object that will store the info from the database.
 * Creation date: (6/24/2002 10:38:43 AM)
 */

public ProteinInfo(String argFormalID,String[] argNames,String argOrganism,String argAccession,String argKeyWords,String argDescription) {

    super(argFormalID,argNames);
    this.organism = argOrganism;
    this.accession = argAccession;
    this.keyWords = argKeyWords;
    this.description = argDescription;
}


/**
 * Create a new Protein object that will store the info from the database.
 * Creation date: (6/24/2002 10:38:43 AM)
 */

public ProteinInfo(String argFormalID,String[] argNames,String argOrganism,String argAccession,String argKeyWords,String argDescription,double argMW) {

    super(argFormalID,argNames);
    this.organism = argOrganism;
    this.accession = argAccession;
    this.keyWords = argKeyWords;
    this.description = argDescription;
    this.molecularWeight = argMW;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {

	if(!super.compareEqual(obj)){
		return false;
	}
	
	if (obj instanceof ProteinInfo){
		ProteinInfo proteinInfo = (ProteinInfo)obj;
		if (!org.vcell.util.Compare.isEqualOrNull(accession,proteinInfo.getAccession())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqualOrNull(organism,proteinInfo.getOrganism())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqualOrNull(keyWords,proteinInfo.getKeyWords())){
			return false;
		}
	}else{
		return false;
	}
	
	return true;
}


/**
 * Returns the Organism name associated with this Protein object
 * Creation date: (6/26/2002 9:29:12 AM)
 * @return cbit.vcell.dictionary.EnzymeRef[]
 */
public String getAccession() {
	return accession;
}


/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 1:29:28 PM)
 * @return java.lang.String[]
 */
public String getDescription() {
	return description;
}


/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 3:34:18 PM)
 * @return int
 */
public FormalSpeciesType getFormalSpeciesType() {
	return FormalSpeciesType.protein;
}


/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 1:29:28 PM)
 * @return java.lang.String[]
 */
public String getKeyWords() {
	return keyWords;
}


/**
 * Returns the Organism name associated with this Protein object
 * Creation date: (6/26/2002 9:29:12 AM)
 * @return cbit.vcell.dictionary.EnzymeRef[]
 */
public double getMolecularWeight() {
	return molecularWeight;
}


/**
 * Returns the Organism name associated with this Protein object
 * Creation date: (6/26/2002 9:29:12 AM)
 * @return cbit.vcell.dictionary.EnzymeRef[]
 */
public String getOrganism() {
	return organism;
}
}
