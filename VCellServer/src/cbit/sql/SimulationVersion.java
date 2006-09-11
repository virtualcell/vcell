package cbit.sql;

import cbit.util.KeyValue;
import cbit.util.Version;
import cbit.util.VersionFlag;

/**
 * Insert the type's description here.
 * Creation date: (7/12/2004 11:53:58 AM)
 * @author: Jim Schaff
 */
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
public SimulationVersion(KeyValue versionKeyNew, String versionNameNew, cbit.util.User versionOwnerNew, cbit.util.GroupAccess versionGroupAccessNew, KeyValue versionBranchPointRefNew, java.math.BigDecimal versionBranchIDNew, java.util.Date versionDateNew, VersionFlag versionFlagNew, String versionAnnotNew, KeyValue argParentSimulationReference) {
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
}