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

import org.vcell.util.BigString;
import org.vcell.util.document.VCDocument.VCDocumentType;


/**
 * Insert the type's description here.
 * Creation date: (11/13/00 4:54:24 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelInfo implements org.vcell.util.document.VCDocumentInfo {
	private Version version = null;
	private KeyValue modelKey = null;
	private BioModelChildSummary bioModelChildSummary = null;
	private BigString bioModelChildSummaryString = null;
	private VCellSoftwareVersion softwareVersion = null;
/**
 * BioModelInfo constructor comment.
 */
public BioModelInfo(Version argVersion, KeyValue argModelKey, BioModelChildSummary argBioModelChildSummary, VCellSoftwareVersion softwareVersion) {
	this.version = argVersion;
	this.modelKey = argModelKey;
	this.bioModelChildSummary = argBioModelChildSummary;
	this.softwareVersion = softwareVersion;
}
public BioModelInfo(Version argVersion, KeyValue argModelKey, String argBioModelChildSummaryString, VCellSoftwareVersion softwareVersion) {
	this.version = argVersion;
	this.modelKey = argModelKey;
	if (argBioModelChildSummaryString != null) {
		this.bioModelChildSummaryString = new BigString(argBioModelChildSummaryString);
	}
	this.softwareVersion = softwareVersion;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof BioModelInfo){
		if (!getVersion().getVersionKey().equals(((BioModelInfo)object).getVersion().getVersionKey())){
			return false;
		}
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/2004 10:35:26 AM)
 * @return cbit.vcell.biomodel.BioModelChildSummary
 */
public BioModelChildSummary getBioModelChildSummary() {
	if (bioModelChildSummary == null && bioModelChildSummaryString != null) {
		bioModelChildSummary = BioModelChildSummary.fromDatabaseSerialization(bioModelChildSummaryString.toString());
		bioModelChildSummaryString = null;
	}
	return bioModelChildSummary;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:57:53 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getModelKey() {
	return modelKey;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
public org.vcell.util.document.Version getVersion() {
	return version;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
public int hashCode() {
	return getVersion().getVersionKey().hashCode();
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 7:44:59 PM)
 * @return java.lang.String
 */
public String toString() {
	return "BioModelInfo(modelKey="+modelKey+",Version="+version+",softwareVersion="+softwareVersion+")";
}
public VersionableType getVersionType() {	
	return VersionableType.BioModelMetaData;
}
public VCellSoftwareVersion getSoftwareVersion() {
	return softwareVersion;
}
public VCDocumentType getVCDocumentType(){
	return VCDocument.VCDocumentType.BIOMODEL_DOC;
}

}
