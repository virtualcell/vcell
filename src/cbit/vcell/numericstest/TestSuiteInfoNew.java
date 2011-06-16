/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest;
import java.math.BigDecimal;
/**
 * Insert the type's description here.
 * Creation date: (10/16/2004 1:43:54 PM)
 * @author: Frank Morgan
 */
public class TestSuiteInfoNew implements org.vcell.util.Matchable, java.io.Serializable {

	private BigDecimal tsKey;
	private String tsID;
	private String tsVCellBuild;
	private String tsNumericsBuild;
	private java.util.Date tsDate;
	private String tsAnnot;
	private boolean isLocked;

/**
 * TestSuiteInfoNew constructor comment.
 * @param argType java.lang.String
 * @param argAnnotation java.lang.String
 */
public TestSuiteInfoNew(BigDecimal argTSKey,String argTSID,String argTSVCellBuild,String argTSNumericsBuild,java.util.Date argTSDate,String argTSAnnot,boolean isLocked) {

	tsKey = argTSKey;
	tsID = argTSID;
	tsVCellBuild = argTSVCellBuild;
	tsNumericsBuild = argTSNumericsBuild;
	tsDate = argTSDate;
	tsAnnot = argTSAnnot;
	this.isLocked = isLocked;
}

/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	
	if(obj == this){
		return true;
	}
	if(!(obj instanceof TestSuiteInfoNew)){
		return false;
	}

	TestSuiteInfoNew tsin = (TestSuiteInfoNew)obj;

	if(!this.tsKey.equals(tsin.tsKey)){
		return false;
	}
	if(!this.tsID.equals(tsin.tsID)){
		return false;
	}
	if(!this.tsVCellBuild.equals(tsin.tsVCellBuild)){
		return false;
	}
	if(!this.tsNumericsBuild.equals(tsin.tsNumericsBuild)){
		return false;
	}
	if(!this.tsDate.equals(tsin.tsDate)){
		return false;
	}
	if(this.isLocked != tsin.isLocked){
		return false;
	}
	return true;
}

public Boolean isLocked(){
	return isLocked;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 1:52:59 PM)
 * @return java.lang.String
 */
public java.util.Date getTSDate() {
	return tsDate;
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 1:52:59 PM)
 * @return java.lang.String
 */
public java.lang.String getTSID() {
	return tsID;
}


public java.lang.String getTSAnnotation() {
	return tsAnnot;
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 1:52:59 PM)
 * @return cbit.sql.KeyValue
 */
public BigDecimal getTSKey() {
	return tsKey;
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 1:52:59 PM)
 * @return java.lang.String
 */
public java.lang.String getTSNumericsBuild() {
	return tsNumericsBuild;
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 1:52:59 PM)
 * @return java.lang.String
 */
public java.lang.String getTSVCellBuild() {
	return tsVCellBuild;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 11:19:52 AM)
 */
public String toShortString() {
	
	return ":DBKey="+getTSKey()+" TSID="+getTSID()+" VCBuild="+getTSVCellBuild()+" NumerBuild="+getTSNumericsBuild();
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 11:19:52 AM)
 */
public String toString() {
	
	return
		getClass().getName() + "@" + Integer.toHexString(hashCode()) +
		":DBKey="+getTSKey()+" TSID="+getTSID()+" VCBuild="+getTSVCellBuild()+" NumerBuild="+getTSNumericsBuild();
}
}
