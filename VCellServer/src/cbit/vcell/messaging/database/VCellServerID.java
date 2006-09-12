package cbit.vcell.messaging.database;
/**
 * Insert the type's description here.
 * Creation date: (4/27/2005 2:00:13 PM)
 * @author: Fei Gao
 */
public class VCellServerID implements java.io.Serializable {
	private java.lang.String serverID = null;

/**
 * VCellServerID constructor comment.
 */
private VCellServerID(String arg_serverID) {
	super();
	if (arg_serverID == null) {
		throw new RuntimeException("VCell ServerID can't be null");
	}
	serverID = arg_serverID.toUpperCase();
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2005 2:05:50 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof VCellServerID){
		VCellServerID vsi = (VCellServerID)obj;
		if (vsi.serverID == null && serverID == null 
			|| vsi.serverID != null && serverID != null && vsi.serverID.equals(serverID)) {
			return true;
		}
	}
	return false;

}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2005 2:29:23 PM)
 * @return cbit.vcell.messaging.db.VCellServerID
 */
public static VCellServerID getServerID(String arg_serverID) {
	return new VCellServerID(arg_serverID);
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2005 2:29:23 PM)
 * @return cbit.vcell.messaging.db.VCellServerID
 */
public static VCellServerID getSystemServerID() {
	return new VCellServerID(cbit.gui.PropertyLoader.getRequiredProperty(cbit.gui.PropertyLoader.vcellServerIDProperty));
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2005 2:06:21 PM)
 * @return int
 */
public int hashCode() {
	return serverID.hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/2005 2:15:04 PM)
 * @return java.lang.String
 */
public String toString() {
	return serverID;
}
}