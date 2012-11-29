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

import java.util.Date;

/**
 * Metadata for SolverResultSets.
 * Creation date: (8/17/2000 9:10:20 PM)
 * @author: John Wagner
 */
public class SolverResultSetInfo implements java.io.Serializable, org.vcell.util.Matchable {
	/**
	 * The date and time the solver began.
	 */
	private java.util.Date fieldStartingDate = new java.util.Date();
	/**
	 * The date and time the solver completed.
	 */
	private java.util.Date fieldEndingDate = new java.util.Date();
	/**
	 * Specifies the problem that generated the result set..
	 */
	private VCSimulationDataIdentifier fieldVCSimDataID = null;
	private java.lang.String fieldDataFilePath = new String();

/**
 * ODESolverResultSetInfo constructor comment.
 */
public SolverResultSetInfo(VCSimulationDataIdentifier arg_vcSimID, String dataFilePath, Date startingDate, Date endingDate) {
	super();
	fieldVCSimDataID = arg_vcSimID;
	fieldDataFilePath = dataFilePath;
	fieldStartingDate = startingDate;
	fieldEndingDate = endingDate;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof SolverResultSetInfo){
		SolverResultSetInfo otherRSI = (SolverResultSetInfo)obj;

		if (!org.vcell.util.Compare.isEqual(getDataFilePath(),otherRSI.getDataFilePath())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqual(getEndingDate(),otherRSI.getEndingDate())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqual(getStartingDate(),otherRSI.getStartingDate())){
			return false;
		}
		if (!org.vcell.util.Compare.isEqual(getVCSimulationDataIdentifier().getID(),otherRSI.getVCSimulationDataIdentifier().getID())){
			return false;
		}		
		return true;
	}else{
		return false;
	}
}


/**
 * Gets the dataFilePath property (java.lang.String) value.
 * @return The dataFilePath property value.
 * @see #setDataFilePath
 */
public java.lang.String getDataFilePath() {
	return fieldDataFilePath;
}


/**
 * Gets the endDate property (java.util.Date) value.
 * @return The endDate property value.
 * @see #setEndDate
 */
public java.util.Date getEndingDate() {
	return fieldEndingDate;
}


/**
 * Gets the startDate property (java.util.Date) value.
 * @return The startDate property value.
 * @see #setStartDate
 */
public java.util.Date getStartingDate() {
	return fieldStartingDate;
}


/**
 * Gets the oDESolverSpecification property (cbit.vcell.solver.ode.ODESolverSpecification) value.
 * @return The oDESolverSpecification property value.
 * @see #setODESolverSpecification
 */
public VCSimulationDataIdentifier getVCSimulationDataIdentifier() {
	return fieldVCSimDataID;
}


/**
 * Insert the method's description here.
 * Creation date: (12/30/2000 3:32:02 PM)
 * @return java.lang.String
 */
public String toString() {
	return "SolverResultSetInfo("+getStartingDate()+","+getEndingDate()+","+getDataFilePath()+") for " + getVCSimulationDataIdentifier();
}
}
