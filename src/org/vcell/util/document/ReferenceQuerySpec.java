/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;


/**
 * Insert the type's description here.
 * Creation date: (11/6/2005 9:37:17 AM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class ReferenceQuerySpec implements java.io.Serializable{

	private org.vcell.util.document.VersionableType versionableType;
	private org.vcell.util.document.KeyValue versionableKeyValue;
	private ExternalDataIdentifier extDataID;

/**
 * ReferenceQuesrySpec constructor comment.
 */
public ReferenceQuerySpec(org.vcell.util.document.VersionableType vType,org.vcell.util.document.KeyValue key) {
	
	versionableType = vType;
	versionableKeyValue = key;
}

public ReferenceQuerySpec(ExternalDataIdentifier argEDI) {
	extDataID = argEDI;
}

public boolean isVersionableType(){
	return versionableType != null;
}
public boolean isExternalDataIdentiferType(){
	return extDataID != null;
}
public ExternalDataIdentifier getExternalDataIdentifier(){
	return extDataID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:31:20 AM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getKeyValue() {
	if(isVersionableType()){
		return versionableKeyValue;
	}else if(isExternalDataIdentiferType()){
		return extDataID.getKey();
	}
	throw new RuntimeException("ReferenceQuesrySpec error: Unknown key type");
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:31:20 AM)
 * @return cbit.sql.VersionableType
 */
public org.vcell.util.document.VersionableType getVersionableType() {
	return versionableType;
}
}
