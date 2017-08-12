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

import cbit.vcell.model.DBSpecies;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 6:10:04 PM)
 * @author: Frank Morgan
 */
public class BoundEnzyme extends DBSpecies implements org.vcell.util.Cacheable{

	private FormalEnzyme formalEnzyme = null;

/**
 * Compound constructor comment.
 * @param argKey cbit.sql.KeyValue
 * @param argDBFormalSpeciesInfo DBFormalSpeciesInfo
 */
public BoundEnzyme(org.vcell.util.document.KeyValue argDBSpeciesKey,FormalEnzyme argFormalEnzyme) {
	
	super(argDBSpeciesKey,argFormalEnzyme);

	this.formalEnzyme = argFormalEnzyme;
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public EnzymeInfo getEnzymeInfo() {
	return formalEnzyme.getEnzymeInfo();
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public org.vcell.util.document.KeyValue getEnzymeKey() {
	return formalEnzyme.getDBFormalSpeciesKey();
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 6:51:31 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public FormalEnzyme getFormalEnzyme() {
	return formalEnzyme;
}
}
