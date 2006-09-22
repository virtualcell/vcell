package org.vcell.modelapp;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.KeyValue;
import cbit.util.Version;
import cbit.util.VersionInfo;
/**
 * This type was created in VisualAge.
 */
public class SimulationContextInfo implements java.io.Serializable,VersionInfo {
	private KeyValue mathRef = null;
	private KeyValue geomRef = null;
	private KeyValue modelRef = null;
	private Version version = null;

/**
 * SimulationContextInfo constructor comment.
 */
public SimulationContextInfo(KeyValue argMathRef,KeyValue argGeomRef,KeyValue argModelRef,Version argVersion) {
	this.mathRef = argMathRef;
	this.geomRef = argGeomRef;
	this.modelRef = argModelRef;
	this.version = argVersion;
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
}