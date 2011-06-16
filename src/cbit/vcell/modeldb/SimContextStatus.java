/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;
/**
 * Insert the type's description here.
 * Creation date: (11/6/2002 1:41:27 PM)
 * @author: Jim Schaff
 */
public class SimContextStatus implements java.io.Serializable {
	private org.vcell.util.document.KeyValue simContextKey = null;
	private boolean hasData;
	private Boolean equiv = null;
	private String status = null;
	private Boolean curatorEquiv = null;
	private String comments = null;

	private final static int ERROR_NONE = 0;
	private final static int ERROR_POSSIBLE = 1;
	private final static int ERROR_CONFIRMED = 2;

/**
 * SimContextStatus constructor comment.
 */
public SimContextStatus(org.vcell.util.document.KeyValue argSimContextKey,boolean argHasData,Boolean argEquiv,String argStatus,Boolean argCuratorEquiv,String argComments) {
	this.simContextKey = argSimContextKey;
	this.hasData = argHasData;
	this.equiv = argEquiv;
	this.status = argStatus;
	this.curatorEquiv = argCuratorEquiv;
	this.comments = argComments;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2002 11:22:34 AM)
 * @return java.lang.String
 */
public java.lang.String getComments() {
	return comments;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2002 11:22:34 AM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getCuratorEquiv() {
	return curatorEquiv;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:49:15 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getEquiv() {
	return equiv;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:52:13 PM)
 * @return int
 */
public int getErrorLevel() {
	//
	// no data no big deal
	//
	if (!hasData){
		return ERROR_NONE;
	}
	//
	// check for curated results first
	//
	if (curatorEquiv!=null){
		if (curatorEquiv.booleanValue()){
			return ERROR_NONE;
		}else{
			return ERROR_CONFIRMED;
		}
	}	
	//
	// if not determined (equiv==null) then potential problem
	//
	if (equiv==null){
		return ERROR_POSSIBLE;
	//
	// if equivalent, no error
	//
	}else if (equiv.equals(Boolean.TRUE)) {
		return ERROR_NONE;
	//
	// if not equivalent due to change of variable, and no detected error, then only a potential problem
	//
	}else{  // equiv == FALSE
		//
		// if no other information, then error possible
		//
		if (status == null){
			return ERROR_POSSIBLE;
		//
		// if bugs identified, then confirm error
		//
		}else if (status.indexOf("BUGFound")>=0){
			return ERROR_CONFIRMED;
		//
		// if change of variables prohibited a proper check, and no identified bugs or duplicate reaction, then only a possible problem
		//
		}else if (status.indexOf("VariablesDontMatch")>=0){
			return ERROR_POSSIBLE;
		//
		// all other cases are problems
		//
		}else{
			return ERROR_CONFIRMED;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:49:15 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getSimContextKey() {
	return simContextKey;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:49:15 PM)
 * @return java.lang.String
 */
public java.lang.String getStatus() {
	return status;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 1:49:15 PM)
 * @return boolean
 */
public boolean isHasData() {
	return hasData;
}
}
