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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.vcell.util.BigString;
import org.vcell.util.document.VCDocument.VCDocumentType;

import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MathOverrides.Element;


/**
 * Insert the type's description here.
 * Creation date: (11/13/00 4:54:24 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class MathModelInfo implements VCDocumentInfo {
	private Version version = null;
	private KeyValue mathKey = null;
	private MathModelChildSummary mathModelChildSummary = null;
	private BigString mathModelChildSummaryString = null;
	private VCellSoftwareVersion softwareVersion = null;
	private ArrayList<PublicationInfo> publicationInfos = new ArrayList<>();
	private String annotatedFunctionsStr = null;

/**
 * BioModelInfo constructor comment.
 */
public MathModelInfo(Version argVersion, KeyValue argMathKey, MathModelChildSummary argMathModelChildSummary,VCellSoftwareVersion softwareVersion) {
	this.version = argVersion;
	this.mathKey = argMathKey;
	this.mathModelChildSummary = argMathModelChildSummary;
	this.softwareVersion = softwareVersion;
}

public MathModelInfo(Version argVersion, KeyValue argMathKey, String argMathModelChildSummaryString, VCellSoftwareVersion softwareVersion) {
	this.version = argVersion;
	this.mathKey = argMathKey;
	if (argMathModelChildSummaryString != null) {
		this.mathModelChildSummaryString = new BigString(argMathModelChildSummaryString);
	}
	this.softwareVersion = softwareVersion;
}

public void setAnnotatedFunctionsStr(String outputFunctionsXML) {
	annotatedFunctionsStr = outputFunctionsXML;
}
public String getAnnotatedFunctionsStr(){
	return annotatedFunctionsStr;
}

/**
 * 
 * 
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof MathModelInfo){
		if (!getVersion().getVersionKey().equals(((MathModelInfo)object).getVersion().getVersionKey())){
			return false;
		}
		return true;
	}
	return false;
}
@Override
public PublicationInfo[] getPublicationInfos() {
	return publicationInfos.toArray(new PublicationInfo[0]);
}
@Override
public void addPublicationInfo(PublicationInfo publicationInfo) {
	publicationInfos.add(publicationInfo);
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:57:53 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getMathKey() {
	return mathKey;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 10:08:56 PM)
 * @return cbit.vcell.mathmodel.MathModelChildSummary
 */
public MathModelChildSummary getMathModelChildSummary() {
	if (mathModelChildSummary == null && mathModelChildSummaryString != null) {
		mathModelChildSummary = MathModelChildSummary.fromDatabaseSerialization(mathModelChildSummaryString.toString());
		mathModelChildSummaryString = null;
	}
	return mathModelChildSummary;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
public Version getVersion() {
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
	String swVersion = (softwareVersion == null ? "null" : softwareVersion.getSoftwareVersionString());
	return "MathModelInfo(mathKey="+mathKey+",Version="+version+", softwareVersion="+swVersion+")";
}
public VersionableType getVersionType() {	
	return VersionableType.MathModelMetaData;
}
public VCellSoftwareVersion getSoftwareVersion() {
	return softwareVersion;
}
public VCDocumentType getVCDocumentType(){
	return VCDocument.VCDocumentType.MATHMODEL_DOC;
}

}
