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

import cbit.vcell.model.DBFormalSpecies;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 6:10:04 PM)
 * @author: Frank Morgan
 */
public class FormalCompound extends DBFormalSpecies {

	private CompoundInfo compoundInfo = null;

/**
 * Compound constructor comment.
 * @param argKey cbit.sql.KeyValue
 * @param argDBFormalSpeciesInfo DBFormalSpeciesInfo
 */
public FormalCompound(org.vcell.util.document.KeyValue argCompoundInfoKey, CompoundInfo argCompoundInfo) {
	
	super(argCompoundInfoKey,argCompoundInfo);

	
	this.compoundInfo = argCompoundInfo;
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public CompoundInfo getCompoundInfo() {
	return compoundInfo;
}
}
