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
 * Superclass of all possible species to be added to the VCell
 * Creation date: (6/24/2002 10:44:36 AM)
 * @author: Steven Woolley
 */
public abstract class DBSpecies extends DBFormalSpecies implements org.vcell.util.Cacheable{

	private org.vcell.util.document.KeyValue dbSpeciesKey = null;
	
/**
 * Create a new Species object based on the given info
 * Creation date: (6/24/2002 12:10:41 PM)
 */
public DBSpecies(org.vcell.util.document.KeyValue argDBSpeciesKey,org.vcell.util.document.KeyValue argFormalSpeciesKey, FormalSpeciesInfo argFormalSpeciesInfo) {

	super(argFormalSpeciesKey,argFormalSpeciesInfo);
	
	if(argDBSpeciesKey == null || argDBSpeciesKey.compareEqual(argFormalSpeciesKey)){
			
		throw new IllegalArgumentException(this.getClass().getName());
	}

	this.dbSpeciesKey = argDBSpeciesKey;
}
/**
 * Create a new Species object based on the given info
 * Creation date: (6/24/2002 12:10:41 PM)
 */
public DBSpecies(org.vcell.util.document.KeyValue argDBSpeciesKey,DBFormalSpecies argDBFormalSpecies) {

	super(argDBFormalSpecies.getDBFormalSpeciesKey(),argDBFormalSpecies.getFormalSpeciesInfo());
	
	if(argDBSpeciesKey == null || argDBSpeciesKey.compareEqual(getDBFormalSpeciesKey())){
			
		throw new IllegalArgumentException(this.getClass().getName());
	}

	this.dbSpeciesKey = argDBSpeciesKey;
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
	
	if (obj instanceof DBSpecies){
		DBSpecies dbSpecies = (DBSpecies)obj;
		if (!dbSpeciesKey.compareEqual(dbSpecies.getDBSpeciesKey())){
			return false;
		}
	}else{
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 4:45:54 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getDBSpeciesKey() {
	return dbSpeciesKey;
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2003 3:52:14 PM)
 * @return java.lang.String
 */
public String toString() {
	return "DBSK="+getDBSpeciesKey() + "-" + super.toString();
}
}
