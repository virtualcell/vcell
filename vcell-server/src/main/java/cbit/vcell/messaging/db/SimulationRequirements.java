/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.db;
import org.vcell.util.document.KeyValue;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2006 3:55:48 PM)
 * @author: Jim Schaff
 */
public class SimulationRequirements implements java.io.Serializable {
	private KeyValue simKey = null;
	private int dimension = 0;

/**
 * SimulationJobStatusInfo constructor comment.
 */
public SimulationRequirements(KeyValue simKey, int dim) {
	super();
	this.simKey = simKey;
	dimension = dim;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2006 3:57:30 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 */
public KeyValue getSimKey() {
	return this.simKey;
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2006 3:57:30 PM)
 * @return boolean
 */
public boolean isPDE() {
	return dimension > 0;
}

public String toString() {
	return "SimKey="+simKey+"(dim="+dimension+")";
}

}
