/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

import java.math.BigDecimal;
import java.util.Random;


/**
 * Insert the type's description here.
 * Creation date: (7/12/2004 11:53:58 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class SimulationVersion extends Version {
	private KeyValue parentSimulationReference = null;

/**
 * SimulationVersion constructor comment.
 * @param versionKeyNew cbit.sql.KeyValue
 * @param versionNameNew java.lang.String
 * @param versionOwnerNew cbit.vcell.server.User
 * @param versionGroupAccessNew cbit.vcell.server.GroupAccess
 * @param versionBranchPointRefNew cbit.sql.KeyValue
 * @param versionBranchIDNew java.math.BigDecimal
 * @param versionDateNew java.util.Date
 * @param versionFlagNew cbit.sql.VersionFlag
 * @param versionAnnotNew java.lang.String
 */
public SimulationVersion(KeyValue versionKeyNew, String versionNameNew, org.vcell.util.document.User versionOwnerNew, org.vcell.util.document.GroupAccess versionGroupAccessNew, KeyValue versionBranchPointRefNew, java.math.BigDecimal versionBranchIDNew, java.util.Date versionDateNew, VersionFlag versionFlagNew, String versionAnnotNew, KeyValue argParentSimulationReference) {
	super(versionKeyNew, versionNameNew, versionOwnerNew, versionGroupAccessNew, versionBranchPointRefNew, versionBranchIDNew, versionDateNew, versionFlagNew, versionAnnotNew);
	this.parentSimulationReference = argParentSimulationReference;
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 12:21:17 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getParentSimulationReference() {
	return parentSimulationReference;
}

public static SimulationVersion createTempSimulationVersion() {
	return  new SimulationVersion(
			new KeyValue(new BigDecimal(Math.abs(new Random().nextInt()))), 
			"temp simulation", User.tempUser,
			new GroupAccessNone(), null, // versionBranchPointRef
			new java.math.BigDecimal(1.0), // branchID
			new java.util.Date(), VersionFlag.Archived, "",  null);
}
}
