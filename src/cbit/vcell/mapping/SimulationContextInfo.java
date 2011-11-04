/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableType;

/**
 * This type was created in VisualAge.
 */
public class SimulationContextInfo implements java.io.Serializable,VersionInfo {
	private KeyValue mathRef = null;
	private KeyValue geomRef = null;
	private KeyValue modelRef = null;
	private Version version = null;
	private VCellSoftwareVersion softwareVersion = null;

/**
 * SimulationContextInfo constructor comment.
 */
public SimulationContextInfo(KeyValue argMathRef,KeyValue argGeomRef,KeyValue argModelRef,Version argVersion,VCellSoftwareVersion softwareVersion) {
	this.mathRef = argMathRef;
	this.geomRef = argGeomRef;
	this.modelRef = argModelRef;
	this.version = argVersion;
	this.softwareVersion = softwareVersion;
}


/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof SimulationContextInfo){
		if (!getVersion().getVersionKey().equals(((SimulationContextInfo)object).getVersion().getVersionKey())){
			return false;
		}
		return true;
	}
	return false;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getGeometryRef() {
	return geomRef;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getMathRef() {
	return mathRef;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getModelRef() {
	return modelRef;
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
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
		return "SimulationContextInfo(MathRef="+mathRef+",GeometryRef="+geomRef+",ModelRef="+modelRef+
					"Version="+version+")";
}


public VersionableType getVersionType() {
	return VersionableType.SimulationContext;
}


public VCellSoftwareVersion getSoftwareVersion() {
	return softwareVersion;
}
}
