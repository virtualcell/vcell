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

import cbit.image.SourceDataInfo;



/**
 * Insert the type's description here.
 * Creation date: (5/10/2004 3:40:51 PM)
 * @author: Jim Schaff
 *
 * copied from BigString for serialization of uncompressing/compressing bytes
 * 
 */
public class DataProcessingOutput implements Serializable {
	private byte[] compressedBytes = null;
	private transient byte[] uncompressedBytes = null;
	private double[] times;
	private String[] variableStatNames;
	private double[][] variableStatValues;
	private HashMap<String, Vector<SourceDataInfo>> dataGenerators = new HashMap<String, Vector<SourceDataInfo>>();

/**
 * BigString constructor comment.
 */
public DataProcessingOutput(byte bytes[]) {
	super();
	uncompressedBytes = bytes;
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:46:15 AM)
 */
private void deflate() throws java.io.IOException {
	if (compressedBytes == null) {
		compressedBytes = BeanUtils.compress(uncompressedBytes);
		System.out.println("Deflating data: " + uncompressedBytes.length + "/" + compressedBytes.length);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:46:15 AM)
 */
private void inflate() throws java.io.IOException {
	uncompressedBytes = BeanUtils.uncompress(compressedBytes);
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
	int compressedSize = s.readInt();
	compressedBytes = new byte[compressedSize];
	s.readFully(compressedBytes, 0, compressedSize);
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:42:08 PM)
 * @return java.lang.String
 */
public byte[] toBytes() {
	try {
		if (uncompressedBytes == null) {
			inflate();
		}
		return uncompressedBytes;
	} catch (IOException ex) {
		throw new RuntimeException("BigString serialization: uncompressing error");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
private void writeObject(ObjectOutputStream s) throws IOException {
	deflate();
	s.writeInt(compressedBytes.length);
	s.write(compressedBytes);
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
