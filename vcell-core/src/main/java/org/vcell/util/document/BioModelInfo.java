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
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.vcell.util.BigString;
import org.vcell.util.document.VCDocument.VCDocumentType;

import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MathOverrides.Element;


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
	private ArrayList<PublicationInfo> publicationInfos = new ArrayList<>();
//	private TreeMap<BigDecimal,ArrayList<AnnotatedFunction>> mapSimContextRefToAnnotatedFunctions;
	private TreeMap<BigDecimal,String> mapSimContextRefToAnnotatedFunctionsStr;
	private TreeMap<String,BigDecimal> mapSimContextNameToSimContextID;
	private TreeMap<String,BigDecimal> mapSimNameToSimD;
//	private TreeMap<String,Integer> mapSimNameToScanCount;
	private TreeMap<String,List<MathOverrides.Element>> mapSimNameToMathOverrideElements;
	private TreeMap<Integer,String> mapSubVolumeNameToSubVolumeID;

//	private TreeMap<String,BigDecimal> mapSimNameToSimID;
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

//public void addAnnotatedFunctions(BigDecimal simContextRef,ArrayList<AnnotatedFunction> annotatedFunctions) {
//	if(mapSimContextRefToAnnotatedFunctions == null) {
//		mapSimContextRefToAnnotatedFunctions = new TreeMap<BigDecimal,ArrayList<AnnotatedFunction>>();
//	}
//	if(mapSimContextRefToAnnotatedFunctions.containsKey(simContextRef)) {
//		throw new IllegalArgumentException("simcontextRef "+simContextRef+" already has entry.");
//	}
//	mapSimContextRefToAnnotatedFunctions.put(simContextRef, annotatedFunctions);
//}
//public ArrayList<AnnotatedFunction> getAnnotatedFunctions(BigDecimal simContextRef){
//	return (mapSimContextRefToAnnotatedFunctions==null?null:mapSimContextRefToAnnotatedFunctions.get(simContextRef));
//}
public boolean hasSCIDForAnnotFunc(BigDecimal simContextRef) {
	return mapSimContextRefToAnnotatedFunctionsStr != null && mapSimContextRefToAnnotatedFunctionsStr.containsKey(simContextRef);
}
public void addSCID(String simContextName,BigDecimal simContextRef) {
	if(mapSimContextNameToSimContextID == null) {
		mapSimContextNameToSimContextID = new TreeMap<String,BigDecimal>();
	}
	mapSimContextNameToSimContextID.put(simContextName,simContextRef);

}
public void addAnnotatedFunctionsStr(String simContextName,BigDecimal simContextRef,String annotatedFunctionsXML) {
	if(mapSimContextRefToAnnotatedFunctionsStr == null) {
		mapSimContextRefToAnnotatedFunctionsStr = new TreeMap<BigDecimal,String>();
	}
	if(mapSimContextRefToAnnotatedFunctionsStr.containsKey(simContextRef)) {
		return;//throw new IllegalArgumentException("simcontextRef "+simContextRef+" already has entry.");
	}
	mapSimContextRefToAnnotatedFunctionsStr.put(simContextRef, annotatedFunctionsXML);
}
public String getAnnotatedFunctionsStr(String scName){
	if(mapSimContextRefToAnnotatedFunctionsStr == null ||
		mapSimContextNameToSimContextID == null ||
		mapSimContextNameToSimContextID.get(scName) == null) {
		return null;
	}
	return (mapSimContextRefToAnnotatedFunctionsStr==null?null:mapSimContextRefToAnnotatedFunctionsStr.get(mapSimContextNameToSimContextID.get(scName)));
}

//public void addSimNameSimID(String simName,BigDecimal simID) {
//	if(mapSimNameToSimID ==null) {
//		mapSimNameToSimID = new TreeMap<String,BigDecimal>();
//	}
//	mapSimNameToSimID.put(simName, simID);
//}
//public BigDecimal getSimName(String simName) {
//	return (mapSimNameToSimID==null?null:mapSimNameToSimID.get(simName));
//}

public void addMathOverrides(String simName,List<MathOverrides.Element> mathOverrideElements) {
	if(mapSimNameToMathOverrideElements == null) {
		mapSimNameToMathOverrideElements = new TreeMap<String, List<MathOverrides.Element>>();
	}
	mapSimNameToMathOverrideElements.put(simName, mathOverrideElements);
}


public void addSimID(String simName,BigDecimal simID) {
	if(mapSimNameToSimD == null) {
		mapSimNameToSimD = new TreeMap<String, BigDecimal>();
	}
	mapSimNameToSimD.put(simName, simID);
}

public BigDecimal getSimID(String simName) {
	return (mapSimNameToSimD == null?null:mapSimNameToSimD.get(simName));
}
public int getScanCount(String simName) {
	if(mapSimNameToMathOverrideElements == null || mapSimNameToMathOverrideElements.get(simName) == null) {
		return 1;
	}
	int scanCount=1;
	for(Element ele:mapSimNameToMathOverrideElements.get(simName) ) {
		if(ele.getSpec() != null) {
			scanCount*=ele.getSpec().getNumValues();
		}
	}
	return scanCount;
}
public List<MathOverrides.Element> getMathOverrides(String simName){
	return (mapSimNameToMathOverrideElements==null?null:mapSimNameToMathOverrideElements.get(simName));
}

public void addSubVolume(int subVolumeID,String subVolumeName) {
	if(mapSubVolumeNameToSubVolumeID == null) {
		mapSubVolumeNameToSubVolumeID = new TreeMap<Integer,String>();
	}
	mapSubVolumeNameToSubVolumeID.put(subVolumeID, subVolumeName);
}
public String getSubVolumeName(int subVolumeID) {
	return (mapSubVolumeNameToSubVolumeID==null?null:mapSubVolumeNameToSubVolumeID.get(subVolumeID));
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
