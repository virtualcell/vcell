package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.document.User;
/**
 * This type was created in VisualAge.
 */
public abstract class DataInfo implements java.io.Serializable, SimDataConstants {

	private User user = null;
	private String simID = null;
	private String varName = null;
	private double simTime = -1.0;  // time in simulation (seconds)
	private long dataBlockTimeStamp = 0;
	private String dataType = null;
	
/**
 * SimDataBlockInfo constructor comment.
 */
protected DataInfo(User user, String simID, String varName, double simTime, long dataBlockTimeStamp, String dataType) {
	this.user = user;
	this.simID = simID;
	this.varName = varName;
	this.simTime = simTime;
	this.dataBlockTimeStamp = dataBlockTimeStamp;
	this.dataType = dataType;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param simResultsInfo cbit.vcell.simdata.SimResultsInfo
 */
public boolean belongsTo(org.vcell.util.document.VCDataIdentifier vcDataID){
	return (getSimID().equals(vcDataID.getID()) && 
			getUser().equals(vcDataID.getOwner()));
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof DataInfo){
		DataInfo otherInfo = (DataInfo)obj;
		if (otherInfo.toString().equals(toString())){
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getSimID() {
	return simID;
}
/**
 * This method was created in VisualAge.
 * @return double
 */
public double getSimTime() {
	return simTime;
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public long getTimeStamp() {
	return dataBlockTimeStamp;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return user;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getVarName() {
	return varName;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return user.getName()+"::"+simID+"::"+dataType+"::"+varName+"::"+simTime+"::"+dataBlockTimeStamp;
}
}
