package cbit.vcell.biomodel;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.document.KeyValue;
import cbit.util.document.Version;
import cbit.util.document.Versionable;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (11/13/00 3:26:20 PM)
 * @author: Jim Schaff
 */
public class BioModelMetaData implements Versionable, java.io.Serializable {
	
	//
	// the version may be 'null' when created (before first insert)
	//
	private Version version = null;

	//
	// this list must be generated via the database or the client document manager
	//
	private KeyValue modelKey = null;
	private Vector simulationContextKeyList = new Vector();
	private Vector simulationKeyList = new Vector();

	private java.lang.String fieldName = new String("NoName");
	private java.lang.String fieldDescription = new String();

/**
 * BioModelMetaData constructor comment.
 */
public BioModelMetaData(KeyValue argModelKey, KeyValue simContextKeys[], KeyValue simulationKeys[], String argName, String argDescription) {
	this.version = null;
	this.fieldName = argName;
	this.fieldDescription = argDescription;
	this.modelKey = argModelKey;
	if (simContextKeys!=null){
		for (int i=0;i<simContextKeys.length;i++){
			this.simulationContextKeyList.addElement(simContextKeys[i]);
		}
	}
	if (simulationKeys!=null){
		for (int i=0;i<simulationKeys.length;i++){
			this.simulationKeyList.addElement(simulationKeys[i]);
		}
	}
}


/**
 * BioModelMetaData constructor comment.
 */
public BioModelMetaData(Version argVersion, KeyValue argModelKey, KeyValue simContextKeys[], KeyValue simulationKeys[]) {
	this.version = argVersion;
	if (version!=null){
		this.fieldName = version.getName();
		this.fieldDescription = version.getAnnot();
	}
	this.modelKey = argModelKey;
	if (simContextKeys!=null){
		for (int i=0;i<simContextKeys.length;i++){
			this.simulationContextKeyList.addElement(simContextKeys[i]);
		}
	}
	if (simulationKeys!=null){
		for (int i=0;i<simulationKeys.length;i++){
			this.simulationKeyList.addElement(simulationKeys[i]);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:28:57 PM)
 */
public void clearVersion() {
	version = null;
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:39:30 PM)
 * @return boolean
 * @param matchable cbit.util.Matchable
 */
public boolean compareEqual(cbit.util.Matchable matchable) {
	if (!(matchable instanceof BioModelMetaData)){
		return false;
	}
	BioModelMetaData obj = (BioModelMetaData)matchable;
	if (!cbit.util.Compare.isEqual(getName(),obj.getName())){
		return false;
	}
	if (!cbit.util.Compare.isEqual(getDescription(),obj.getDescription())){
		return false;
	}
	if (!cbit.util.Compare.isEqual(version,obj.version)){
		return false;
	}
	if (!cbit.util.Compare.isEqual(simulationKeyList,obj.simulationKeyList)){
		return false;
	}
	if (!cbit.util.Compare.isEqual(simulationContextKeyList,obj.simulationContextKeyList)){
		return false;
	}
	if (!cbit.util.Compare.isEqual(modelKey,obj.modelKey)){
		return false;
	}
	return true;
}


/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
public java.lang.String getDescription() {
	return fieldDescription;
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:26:41 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getModelKey() {
	return modelKey;
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}


/**
 * Insert the method's description here.
 * Creation date: (1/23/01 3:22:21 PM)
 * @return int
 */
public int getNumSimulationContexts() {
	return simulationContextKeyList.size();
}


/**
 * Insert the method's description here.
 * Creation date: (1/23/01 3:22:21 PM)
 * @return int
 */
public int getNumSimulations() {
	return simulationKeyList.size();
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:21:49 PM)
 * @return java.util.Collection
 */
public Enumeration getSimulationContextKeys() {
	return simulationContextKeyList.elements();
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:21:49 PM)
 * @return java.util.Collection
 */
public Enumeration getSimulationKeys() {
	return simulationKeyList.elements();
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:27:17 PM)
 * @return cbit.sql.Version
 */
public Version getVersion() {
	return this.version;
}


/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException {
	fieldDescription = description;
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	fieldName = name;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2002 10:20:24 AM)
 * @return java.lang.String
 */
public String toString() {
	return super.toString()+"("+version+")";
}
}