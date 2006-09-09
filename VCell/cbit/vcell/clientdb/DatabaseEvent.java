package cbit.vcell.clientdb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.VersionInfo;
/**
 * This is the event class to support the cbit.vcell.clientdb.DatabaseListener interface.
 */
public class DatabaseEvent extends java.util.EventObject {
	private cbit.sql.VersionInfo dbNewVersionInfo = null;
	private cbit.sql.VersionInfo dbOldVersionInfo = null;
	private int operationType = UNDEFINED;

	public static final int UNDEFINED = -1;
	public static final int DELETE = 0;
	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int REFRESH= 3;
	
	private static final String operationNames[] = { "delete", "insert", "update", "refresh" };

/**
 * DatabaseEvent constructor comment.
 * @param source java.lang.Object
 */
public DatabaseEvent(java.lang.Object source, int argOperationType, cbit.sql.VersionInfo argOldVersionInfo, cbit.sql.VersionInfo argNewVersionInfo) {
	super(source);
	this.dbOldVersionInfo = argOldVersionInfo;
	this.dbNewVersionInfo = argNewVersionInfo;
	if (argOperationType != INSERT &&
		argOperationType != DELETE &&
		argOperationType != UPDATE &&
		argOperationType != REFRESH){
		throw new IllegalArgumentException("unexpected argumentType = '"+argOperationType+"'");
	}
	this.operationType = argOperationType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/01 2:32:26 PM)
 * @return java.lang.Object
 */
public VersionInfo getNewVersionInfo() {
	return dbNewVersionInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/01 3:49:53 PM)
 * @return cbit.sql.VersionInfo
 */
public VersionInfo getOldVersionInfo() {
	return this.dbOldVersionInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/01 2:32:47 PM)
 * @return java.lang.String
 */
public String getOperationName() {
	return operationNames[operationType];
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/01 2:30:08 PM)
 * @return int
 */
public int getOperationType() {
	return operationType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/23/01 2:30:36 PM)
 * @return java.lang.String
 */
public String toString() {
	return "DatabaseEvent:" + getOperationName() + "(" + getNewVersionInfo().toString() + ")";
}
}
