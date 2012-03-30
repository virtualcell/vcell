/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mathmodel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;

import cbit.vcell.solver.AnnotatedFunction;
/**
 * Insert the type's description here.
 * Creation date: (11/13/00 3:26:20 PM)
 * @author: Jim Schaff
 */
public class MathModelMetaData implements Versionable, java.io.Serializable {
	
	//
	// the version may be 'null' when created (before first insert)
	//
	private Version version = null;

	//
	// this list must be generated via the database or the client document manager
	//
	private KeyValue mathKey = null;
	private Vector<KeyValue> simulationKeyList = new Vector<KeyValue>();

	private java.lang.String fieldName = new String("NoName");
	private java.lang.String fieldDescription = new String();
	
	private ArrayList<AnnotatedFunction> outputFunctions;

/**
 * BioModelMetaData constructor comment.
 */
public MathModelMetaData(KeyValue argMathKey, KeyValue simulationKeys[], String argName, String argDescription,ArrayList<AnnotatedFunction> outputFunctions) {
	this.version = null;
	this.fieldName = argName;
	this.fieldDescription = argDescription;
	this.mathKey = argMathKey;
	if (simulationKeys!=null){
		for (int i=0;i<simulationKeys.length;i++){
			this.simulationKeyList.addElement(simulationKeys[i]);
		}
	}
	this.outputFunctions = outputFunctions;
}


/**
 * BioModelMetaData constructor comment.
 */
public MathModelMetaData(Version argVersion, KeyValue argMathKey, KeyValue simulationKeys[],ArrayList<AnnotatedFunction> outputFunctions) {
	this.version = argVersion;
	if (version!=null){
		this.fieldName = version.getName();
		this.fieldDescription = version.getAnnot();
	}
	this.mathKey = argMathKey;
	if (simulationKeys!=null){
		for (int i=0;i<simulationKeys.length;i++){
			this.simulationKeyList.addElement(simulationKeys[i]);
		}
	}
	this.outputFunctions = outputFunctions;

}

public ArrayList<AnnotatedFunction> getOutputFunctions(){
	return outputFunctions;
}

/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:32:21 PM)
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
	if (!(matchable instanceof MathModelMetaData)){
		return false;
	}
	MathModelMetaData obj = (MathModelMetaData)matchable;
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
	if (!org.vcell.util.Compare.isEqual(mathKey,obj.mathKey)){
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
public KeyValue getMathKey() {
	return mathKey;
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
public int getNumSimulations() {
	return simulationKeyList.size();
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
}
