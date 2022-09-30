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

import org.vcell.util.Cacheable;
import org.vcell.util.Relatable;
import org.vcell.util.RelationVisitor;

public abstract class DBSpecies extends DBFormalSpecies implements Cacheable, Relatable {

	private org.vcell.util.document.KeyValue dbSpeciesKey = null;
	
public DBSpecies(org.vcell.util.document.KeyValue argDBSpeciesKey,org.vcell.util.document.KeyValue argFormalSpeciesKey, FormalSpeciesInfo argFormalSpeciesInfo) {

	super(argFormalSpeciesKey,argFormalSpeciesInfo);
	
	if(argDBSpeciesKey == null || argDBSpeciesKey.compareEqual(argFormalSpeciesKey)){
			
		throw new IllegalArgumentException(this.getClass().getName());
	}

	this.dbSpeciesKey = argDBSpeciesKey;
}

public DBSpecies(org.vcell.util.document.KeyValue argDBSpeciesKey,DBFormalSpecies argDBFormalSpecies) {

	super(argDBFormalSpecies.getDBFormalSpeciesKey(),argDBFormalSpecies.getFormalSpeciesInfo());
	
	if(argDBSpeciesKey == null || argDBSpeciesKey.compareEqual(getDBFormalSpeciesKey())){
			
		throw new IllegalArgumentException(this.getClass().getName());
	}

	this.dbSpeciesKey = argDBSpeciesKey;
}

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

	@Override
	public boolean relate(Relatable obj, RelationVisitor rv) {
		if(!super.relate0(obj, rv)){
			return false;
		}
		if (obj instanceof DBSpecies){
			DBSpecies dbSpecies = (DBSpecies)obj;
			if (!rv.relate(dbSpeciesKey, dbSpecies.getDBSpeciesKey())){
				return false;
			}
		}else{
			return false;
		}

		return true;
	}

	public org.vcell.util.document.KeyValue getDBSpeciesKey() {
	return dbSpeciesKey;
}

public String toString() {
	return "DBSK="+getDBSpeciesKey() + "-" + super.toString();
}
}
