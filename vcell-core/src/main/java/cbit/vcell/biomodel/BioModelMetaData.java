/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.util.Compare;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;
/**
 * Insert the type's description here.
 * Creation date: (11/13/00 3:26:20 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelMetaData implements Versionable, java.io.Serializable{
	

	//
	// the version may be 'null' when created (before first insert)
	//
	private Version version = null;

	//
	// this list must be generated via the database or the client document manager
	//
	private KeyValue modelKey = null;
	private Vector<KeyValue> simulationContextKeyList = new Vector<KeyValue>();
	private Vector<KeyValue> simulationKeyList = new Vector<KeyValue>();
	private String vcMetaDataXML = null;
	
	private java.lang.String fieldName = new String("NoName");
	private java.lang.String fieldDescription = new String();

/**
 * BioModelMetaData constructor comment.
 */
public BioModelMetaData(KeyValue argModelKey, KeyValue simContextKeys[], KeyValue simulationKeys[], String vcMetaDataXML, String argName, String argDescription) {
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
	this.vcMetaDataXML = vcMetaDataXML;
}


/**
 * BioModelMetaData constructor comment.
 */
public BioModelMetaData(Version argVersion, KeyValue argModelKey, KeyValue simContextKeys[], KeyValue simulationKeys[], String vcMetaDataXML) {
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
	this.vcMetaDataXML = vcMetaDataXML;
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
public boolean compareEqual(org.vcell.util.Matchable matchable) {
	if (!(matchable instanceof BioModelMetaData)){
		return false;
	}
	BioModelMetaData obj = (BioModelMetaData)matchable;
	if (!org.vcell.util.Compare.isEqual(getName(),obj.getName())){
		return false;
	}
	if (!org.vcell.util.Compare.isEqual(getDescription(),obj.getDescription())){
		return false;
	}
	if (!org.vcell.util.Compare.isEqual(version,obj.version)){
		return false;
	}
	if (!org.vcell.util.Compare.isEqual(simulationKeyList,obj.simulationKeyList)){
		return false;
	}
	if (!org.vcell.util.Compare.isEqual(simulationContextKeyList,obj.simulationContextKeyList)){
		return false;
	}
	if (!org.vcell.util.Compare.isEqual(modelKey,obj.modelKey)){
		return false;
	}
	if(!Compare.isEqualOrNull(vcMetaDataXML, obj.vcMetaDataXML)){
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
public Enumeration<KeyValue> getSimulationContextKeys() {
	return simulationContextKeyList.elements();
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:21:49 PM)
 * @return java.util.Collection
 */
public Enumeration<KeyValue> getSimulationKeys() {
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
@Override
public String toString() {
	return super.toString()+"("+version+")";
}


public String getVCMetaDataXML() {
	return vcMetaDataXML;
}
}
