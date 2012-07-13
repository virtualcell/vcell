/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import org.vcell.util.BeanUtils;

import cbit.image.gui.SourceDataInfo;



/**
 * Insert the type's description here.
 * Creation date: (5/10/2004 3:40:51 PM)
 * @author: Jim Schaff
 *
 * copied from BigString for serialization of uncompressing/compressing bytes
 * 
 */
public class DataProcessingOutput implements Serializable {
	private double[] times;
	private String[] variableStatNames;
	private double[][] variableStatValues;
	private String[] variableUnits;
	private HashMap<String, Vector<SourceDataInfo>> dataGenerators = new HashMap<String, Vector<SourceDataInfo>>();

/**
 * BigString constructor comment.
 */
public DataProcessingOutput() {
	super();
}

public double[] getTimes() {
	return times;
}

public void setTimes(double[] times) {
	this.times = times;
}

public String[] getVariableStatNames() {
	return variableStatNames;
}


public void setVariableStatNames(String[] variableStatNames) {
	this.variableStatNames = variableStatNames;
}

public String[] getVariableUnits() {
	return variableUnits;
}

public void setVariableUnits(String[] variableUnits) {
	this.variableUnits = variableUnits;
}

public double[][] getVariableStatValues() {
	return variableStatValues;
}

public double[] getVariableStatValues(String varName){
	for (int i = 0; i < getVariableStatNames().length; i++) {
		if(getVariableStatNames()[i].equals(varName)){
			return getVariableStatValues()[i];
		}
	}
	return null;
}

public void setVariableStatValues(double[][] variableStatValues) {
	this.variableStatValues = variableStatValues;
}


public HashMap<String, Vector<SourceDataInfo>> getDataGenerators() {
	return dataGenerators;
}


public void setDataGenerators(HashMap<String, Vector<SourceDataInfo>> other) {
	this.dataGenerators = other;
}
}
