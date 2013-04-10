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
 * Class that represents a single enzyme from a database
 * Creation date: (6/24/2002 10:38:00 AM)
 * @author: Steven Woolley
 */
public class EnzymeInfo extends FormalSpeciesInfo implements org.vcell.util.Cacheable{
	
    private String reaction = null;
    private String sysname = null;
    private String casID = null;

    /*
    Kegg information	
    >        ENTRY
    >        NAME
            CLASS
            SYSNAME
            REACTION
            SUBSTRATE
            PRODUCT
            INHIBITOR
            COFACTOR
            EFFECTOR
            COMMENT
    >        PATHWAY
            GENES
            DISEASE
            MOTIF
    >        STRUCTURES
    >        DBLINKS
            ///
    	*/
/**
 * Create a new Enzyme object that will store the info from the database.
 * Creation date: (6/24/2002 10:38:43 AM)
 */

 
public EnzymeInfo(
    String argFormalID,
    String[] argNames,
    String reaction,
    String sysname,
    String argcasID) {
	    
    super(argFormalID, argNames);

    this.reaction = reaction;
    this.sysname = sysname;
    this.casID = argcasID;

    /*
    
    For the ENZYME section the following data items appear on columns 1-12:
    
    >        ENTRY
    >        NAME
        CLASS
        SYSNAME
        	REACTION
        	SUBSTRATE
        	PRODUCT
        	INHIBITOR
        	COFACTOR
            EFFECTOR
        COMMENT
    >   PATHWAY
    	    	GENES
    	 		DISEASE
    	   		MOTIF
    		>STRUCTURES
    >   DBLINKS
        ///
    
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
	
	if (obj instanceof EnzymeInfo){
		EnzymeInfo enzymeInfo = (EnzymeInfo)obj;
		if (!org.vcell.util.Compare.isEqualOrNull(reaction,enzymeInfo.getReaction())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqualOrNull(sysname,enzymeInfo.getSysname())){
			return false;
		}
	}else{
		return false;
	}
	
	return true;
}
/**
 * Returns the systematic name of the Enzyme
 * Creation date: (6/25/2002 4:30:29 PM)
 * @return java.lang.String
 */
public String getCasID() {
	return casID;
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 3:34:18 PM)
 * @return int
 */
public FormalSpeciesType getFormalSpeciesType() {
	return FormalSpeciesType.enzyme;
}
/**
 * Returns the reaction associated with this Enzyme object
 * Creation date: (6/26/2002 9:29:12 AM)
 * @return cbit.vcell.dictionary.EnzymeRef[]
 */
public String getReaction() {
	return reaction;
}
/**
 * Returns the systematic name of the Enzyme
 * Creation date: (6/25/2002 4:30:29 PM)
 * @return java.lang.String
 */
public String getSysname() {
	return sysname;
}
}
