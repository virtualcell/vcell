package cbit.vcell.simdata;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (7/6/01 2:59:41 PM)
 * @author: Jim Schaff
 */
public class DataIdentifier implements java.io.Serializable {
	private String name = null;
	private VariableType variableType = null;
	private boolean bFunction = false;

/**
 * DataIdentifier constructor comment.
 */
public DataIdentifier(String argName, VariableType argVariableType) {
	this(argName, argVariableType, false);
}


/**
 * DataIdentifier constructor comment.
 */
public DataIdentifier(String argName, VariableType argVariableType, boolean arg_bFunction) {
	super();
	name = argName;
	variableType = argVariableType;
	bFunction = arg_bFunction;
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2004 10:19:24 AM)
 */
public boolean equals(Object obj) {
	if (!(obj instanceof DataIdentifier)) {
		return false;
	}

	DataIdentifier dsi = (DataIdentifier)obj;
	if (dsi.name.equals(name)) {
		return true;
	}
	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/01 3:01:24 PM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/01 3:01:49 PM)
 * @return cbit.vcell.simdata.VariableType
 */
public VariableType getVariableType() {
	return variableType;
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2004 5:20:14 PM)
 * @return boolean
 */
public boolean isFunction() {
	return bFunction;
}


/**
 * Insert the method's description here.
 * Creation date: (7/9/2001 7:06:40 AM)
 * @return java.lang.String
 */
public String toString() {
	return "DataIdentifier[\""+getName()+"\","+getVariableType()+"]";
}
}