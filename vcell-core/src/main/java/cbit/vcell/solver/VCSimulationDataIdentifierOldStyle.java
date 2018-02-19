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

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimResampleInfoProvider;

import cbit.vcell.Historical;


/**
 * Temporary, to help SimulationData to deal with multiple simulation job datasets.
 * Should be removed after making SimulationData smarter in file handling for compatibility with old stuff.
 */
@SuppressWarnings("serial")
@Historical
public class VCSimulationDataIdentifierOldStyle
	implements
		java.io.Serializable,
		SimResampleInfoProvider{


	@Override
	public KeyValue getSimulationKey() {
		return vcSimID.getSimulationKey();
	}


	public boolean isParameterScanType() {
		return false;
	}


	private VCSimulationIdentifier vcSimID = null;

/**
 * VCSimulationIdentifier constructor comment.
 */
private VCSimulationDataIdentifierOldStyle(VCSimulationDataIdentifier vcSimDataID) {
	this.vcSimID = vcSimDataID.getVcSimID();
}


/**
 * Insert the method's description here.
 * Creation date: (10/18/2005 3:28:49 PM)
 * @return cbit.vcell.solver.VCSimulationDataIdentifierOldStyle
 * @param vcSimDataID cbit.vcell.solver.VCSimulationDataIdentifier
 */
@Historical
public static VCSimulationDataIdentifierOldStyle createVCSimulationDataIdentifierOldStyle(VCSimulationDataIdentifier vcSimDataID) {
	return new VCSimulationDataIdentifierOldStyle(vcSimDataID);
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 10:56:16 AM)
 * @return boolean
 * @param object java.lang.Object
 */
@Historical
public boolean equals(Object object) {
	if (object instanceof VCSimulationDataIdentifierOldStyle){
		if (((VCSimulationDataIdentifierOldStyle)object).getID().equals(getID())){
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
@Historical
public java.lang.String getID() {
	return Simulation.createSimulationID(vcSimID.getSimulationKey());
}

public int getJobIndex() {
	return 0;
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 11:12:39 AM)
 * @return cbit.vcell.server.User
 */
@Historical
public org.vcell.util.document.User getOwner() {
	return vcSimID.getOwner();
}


/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
@Historical
public int hashCode() {
	return toString().hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (8/24/2004 1:12:48 PM)
 * @return java.lang.String
 */
@Historical
public String toString() {
	return "VCSimulationIdentifierOldStyle["+vcSimID.getSimulationKey()+","+getOwner()+"]";
}


@Override
public KeyValue getDataKey() {
	return getSimulationKey();
}
}
