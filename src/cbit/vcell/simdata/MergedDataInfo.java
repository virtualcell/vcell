/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import org.vcell.util.document.User;
/**
 * Insert the type's description here.
 * Creation date: (10/7/2003 4:21:41 PM)
 * @author: Anuradha Lakshminarayana
 */
public class MergedDataInfo implements org.vcell.util.document.VCDataIdentifier, java.io.Serializable, org.vcell.util.Matchable {
	private java.lang.String datasetName = null;
	private User datasetUser = null;
	private org.vcell.util.document.VCDataIdentifier[] dataIDs = null;
	private String[] dataSetPrefix = null;

public MergedDataInfo(User argOwner, org.vcell.util.document.VCDataIdentifier[] argdataIDs, String[] prefix) {
	super();
	if(prefix == null)
	{
		throw new IllegalArgumentException("Prefixes can not be null in MergedDataInfo.");
	}
	String compDataName = argOwner.getName();
	for (int i = 0; i < argdataIDs.length; i++) {
		compDataName = compDataName+"_"+argdataIDs[i].getID();
	}
	datasetName = compDataName;
	datasetUser = argOwner;
	dataIDs = argdataIDs;
	dataSetPrefix = prefix;
}

public static String[] createDefaultPrefixNames(int dataSetCount)
{
		String[] dataSetPrefix = new String[dataSetCount];
		for (int i = 0; i < dataSetCount; i++)
		{
			dataSetPrefix[i] = "Data"+(i+1); 
		}
		return dataSetPrefix;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	MergedDataInfo mergedDataInfo = null;
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof MergedDataInfo)) {
		return false;
	} else {
		mergedDataInfo = (MergedDataInfo) obj;
	}

	if (getDatasetName().equals(mergedDataInfo.getDatasetName())) {
		return false;
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof MergedDataInfo){
		MergedDataInfo mergedInfo = (MergedDataInfo)object;
		if (!getID().equals(mergedInfo.getID())){
			return false;
		}
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/21/2003 12:47:21 PM)
 * @return cbit.vcell.server.VCDataIdentifier[]
 */
public org.vcell.util.document.VCDataIdentifier[] getDataIDs() {
	return dataIDs;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 4:22:40 PM)
 * @return java.lang.String
 */
public java.lang.String getDatasetName() {
	return datasetName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 11:22:01 AM)
 * @return cbit.vcell.server.User
 */
public org.vcell.util.document.User getDatasetUser() {
	return datasetUser;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 4:22:40 PM)
 * @return java.lang.String
 */
public String getID() {
	return datasetName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 4:22:40 PM)
 * @return java.lang.String
 */
public User getOwner() {
	return getDatasetUser();
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
public int hashCode() {
	return getID().hashCode();
}

public String[] getDataSetPrefix() {
	return dataSetPrefix;
}
}
