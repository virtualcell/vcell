/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;
/**
 * Insert the type's description here.
 * Creation date: (8/24/2004 10:55:36 AM)
 * @author: Jim Schaff
 */
public class VCSimulationIdentifier implements java.io.Serializable {
	private org.vcell.util.document.KeyValue simulationKey = null;
	private org.vcell.util.document.User owner = null;

/**
 * VCSimulationIdentifier constructor comment.
 */
public VCSimulationIdentifier(org.vcell.util.document.KeyValue argSimulationKey, org.vcell.util.document.User argOwner) {
	super();
	this.simulationKey = argSimulationKey;
	this.owner = argOwner;
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 10:56:16 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof VCSimulationIdentifier){
		if (((VCSimulationIdentifier)object).simulationKey.equals(simulationKey)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 2:07:52 PM)
 * @return java.lang.String
 */
public java.lang.String getID() {
	return Simulation.createSimulationID(getSimulationKey());
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 11:12:39 AM)
 * @return cbit.vcell.server.User
 */
public org.vcell.util.document.User getOwner() {
	return owner;
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 10:59:23 AM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getSimulationKey() {
	return simulationKey;
}


/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
public int hashCode() {
	return simulationKey.hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 1:12:48 PM)
 * @return java.lang.String
 */
public String toString() {
	return "VCSimulationIdentifier["+getSimulationKey()+","+getOwner()+"]";
}
}
