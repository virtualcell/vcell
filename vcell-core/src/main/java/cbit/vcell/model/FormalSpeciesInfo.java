/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;


/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 3:07:17 PM)
 * @author: Frank Morgan
 */
public abstract class FormalSpeciesInfo implements org.vcell.util.Cacheable{

	private String formalID = null;
	private String[] names = null;
    
/**
 * DBSpeciesInfo constructor comment.
 */
protected FormalSpeciesInfo(String argFormalID,String[] argNames) {

	if(argFormalID == null || argNames == null || argNames.length == 0){
		throw new IllegalArgumentException("FormalSpeciesInfo formalID="+argFormalID+" names="+argNames);
	}

	this.formalID = argFormalID;
	this.names = argNames;

	//if(this instanceof CompoundInfo){
		//this.type = this.SPECIES_TYPE_COMPOUND;
	//}else if(this instanceof EnzymeInfo){
		//this.type = this.SPECIES_TYPE_ENZYME;
	//}else if(this instanceof ProteinInfo){
		//this.type = this.SPECIES_TYPE_PROTEIN;
	//}else{
		//throw new IllegalArgumentException(this.getClass().getName()+" Unknown type");
	//}
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	
	if (obj instanceof FormalSpeciesInfo){
		FormalSpeciesInfo formalSpeciesInfo = (FormalSpeciesInfo)obj;
		if (!formalID.equals(formalSpeciesInfo.getFormalID())){
			return false;
		}
		if(!org.vcell.util.Compare.isEqual(names,formalSpeciesInfo.getNames())){
			return false;
		}
	}else{
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 3:45:40 PM)
 * @return java.lang.String
 */
public java.lang.String getFormalID() {
	return formalID;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 5:54:39 PM)
 * @return int
 */
public abstract FormalSpeciesType getFormalSpeciesType();
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 3:45:40 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getNames() {
	return names;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 3:45:40 PM)
 * @return java.lang.String[]
 */
public java.lang.String getPreferredName() {
	return names[0];
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2003 2:42:23 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	for(int i = 0; i < names.length;i+= 1){
		if(i != 0){
			sb.append(",");
		}
		sb.append(names[i]);
	}
	return getFormalID()+"-"+sb.toString();
}
}
