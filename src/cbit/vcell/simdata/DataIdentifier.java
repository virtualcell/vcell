package cbit.vcell.simdata;

import org.vcell.util.Compare;

import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;

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
	private String displayName = null;
	private VariableType variableType = null;
	private Domain domain = null;
	private boolean bFunction = false;


/**
 * DataIdentifier constructor comment.
 */
public DataIdentifier(String argName, VariableType argVariableType, Domain argDomain, boolean arg_bFunction, String argDisplayName) {
	super();
	name = argName;
	variableType = argVariableType;
	domain = argDomain;
	bFunction = arg_bFunction;
	displayName = argDisplayName;
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
	if (!Compare.isEqualOrNull(dsi.name,name)) {
		return false;
	}
	if (!Compare.isEqualOrNull(dsi.domain,domain)) {
		return false;
	}
	
	return true;
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
 * Creation date: (7/6/01 3:01:24 PM)
 * @return java.lang.String
 */
public Domain getDomain() {
	return domain;
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
	return "DataIdentifier[\""+getName()+"\","+getVariableType()+",\""+getDomain()+"\"]";
}

public String getDisplayName(){
	return displayName;	
}

}