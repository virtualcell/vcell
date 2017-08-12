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
 * Class that represents a single compound from a database
 * Creation date: (6/24/2002 10:38:00 AM)
 * @author: Steven Woolley
 */
public class CompoundInfo extends FormalSpeciesInfo {
	private String formula = null;
	private EnzymeRef[] enzymes = null;
	private String casID = null;
	private double molecularWeight = -1;

/**
 * Create a new Compound object that will store the info from the database.
 * Creation date: (6/24/2002 10:38:43 AM)
 */
public CompoundInfo(String argFormalID, String[] argNames, 
					String formula, String casID, EnzymeRef[] enzymes) {

		
    super(argFormalID, argNames);
    
    this.formula = formula;
	this.casID = casID;
	this.enzymes = enzymes;
	
    /*
    2.1.  General Data Format
    
    
    For the COMPOUND section the following data items appear on columns 1-12:
    
        >ENTRY
        >NAME
        FORMULA
        >PATHWAY
        ENZYME
        >STRUCTURES
        >DBLINKS
        ///
    
    A COMPOUND entry starts with the ENTRY data item and ends with the 
    end-of-entry data item (///).  The data items ENTRY, NAME, and end-of-entry
    are mandatory, while the other data items are optional.
    
    Example:
    
    ENTRY       C00116
    NAME        Glycerol
            Glycerin
            1,2,3-trihydroxypropane
            1,2,3-propanetriol
    FORMULA     C3H8O3
    PATHWAY     PATH: MAP00052  Galactose metabolism
            PATH: MAP00561  Glycerolipid metabolism
    ENZYME      1.1.1.6 (R)     1.1.1.72 (R)    1.1.1.156 (R)   1.1.99.22 (R)
            2.7.1.30 (R)    2.7.1.79 (R)    2.7.1.142 (R)   3.1.1.23 (R)
            3.1.3.19 (R)    3.1.3.21 (R)    3.1.4.38 (R)    3.1.4.43 (R)
            3.1.4.46 (R)    4.2.1.30 (R)
    DBLINKS     CAS: 56-81-5
    ///
    
    In addition, the molecular structure is stored in a separate MDL/MOL
    format file as well as in a GIF image format file for each compound.
    The GIF image is automatically displayed between the FORMULA and
    PATHWAY data items in the WWW version of DBGET.
    */
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
	
	if (obj instanceof CompoundInfo){
		CompoundInfo compoundInfo = (CompoundInfo)obj;
		if (!org.vcell.util.Compare.isEqualOrNull(casID,compoundInfo.getCasID())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqualOrNull(formula,compoundInfo.getFormula())){
			return false;
		}
		if(!org.vcell.util.Compare.isEqualOrNull(enzymes,compoundInfo.getEnzymes())){
			return false;
		}
	}else{
		return false;
	}
	
	return true;
}


/**
 * Returns the CasID
 * Creation date: (6/25/2002 4:30:29 PM)
 * @return java.lang.String
 */
public String getCasID() {
	return casID;
}


/**
 * Returns the array of EnzymeRef objects
 * Creation date: (6/26/2002 9:29:12 AM)
 * @return cbit.vcell.dictionary.EnzymeRef[]
 */
public EnzymeRef[] getEnzymes() {
	return enzymes;
}


/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 3:34:18 PM)
 * @return int
 */
public FormalSpeciesType getFormalSpeciesType() {
	return FormalSpeciesType.compound;
}


/**
 * Returns the formula
 * Creation date: (6/25/2002 4:30:29 PM)
 * @return java.lang.String
 */
public String getFormula() {
	return formula;
}


/**
 * Insert the method's description here.
 * Creation date: (4/24/2005 3:48:23 PM)
 * @return double
 */
public double getMolecularWeight() {
	
	if(molecularWeight == -1 && getFormula() != null){
		try{
			molecularWeight = MolMassTable.calcMass(getFormula());
		}catch(Throwable e){
			molecularWeight = -1;
		}
	}
	return molecularWeight;
}
}
