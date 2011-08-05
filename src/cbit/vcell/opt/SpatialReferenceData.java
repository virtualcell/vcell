/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;

import java.io.Serializable;
import java.util.Vector;

import org.vcell.util.Compare;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")
/**
 * Stores spatial data in a vector of double array.
 * The size of the vector is the number of time points, and the length of
 * double array is 1("t") + dataSize(x*y*z)*number of variables
 * 
 * In addition, SpatialReferenceData should used Time or Variable weights. ElementWeights are
 * not proper since it's going to double the data size and therefore take a lot memory.  
 */
public class SpatialReferenceData implements ReferenceData, Serializable {
	private String variableNames[] = null;//include "t"
	private ISize dataSize;
	private Weights weights = null;
	private Vector<double[]> rowData = new Vector<double[]>();

	public SpatialReferenceData(String[] argVariableNames, double[] argVariableWeights, ISize argDataSize, Vector<double[]> argRowData) {
		super();
		this.variableNames = argVariableNames;
		dataSize = argDataSize;
		if(argVariableWeights.length == (argVariableNames.length - 1))
		{
			weights = new VariableWeights(argVariableWeights);
		}
		else
		{
			throw new IllegalArgumentException("The length of weights should be the same as number of variables (excluding 't').");
		}
		for (int i = 0; i < argRowData.size(); i++){
			double[] rowData = argRowData.elementAt(i);
			if (rowData.length != 1 +  (variableNames.length - 1) * getDataSize()) {
				throw new IllegalArgumentException("rowData not same size as number of variableSize * dataSize + 1 (for t)");
			}
		}

		this.rowData = argRowData;
	}

public boolean compareEqual(Matchable obj) {
	if (obj instanceof SpatialReferenceData){
		SpatialReferenceData srd = (SpatialReferenceData)obj;

		if (!Compare.isEqual(variableNames, srd.variableNames)){
			return false;
		}
		
		if (!Compare.isEqual(weights,srd.weights)){
			return false;
		}

		if (rowData.size()!=srd.rowData.size()){
			return false;
		}

		for (int i = 0; i < rowData.size(); i++){
			double[] thisData = rowData.get(i);
			double[] otherData = srd.rowData.get(i);
			if (!Compare.isEqual(thisData,otherData)){
				return false;
			}
		}
		
		return true;
	}
	return false;
}

public int findVariable(String varName) {
	for (int i = 0; i < variableNames.length; i++){
		if (variableNames[i].equals(varName)){
			return i;
		}
	}
	return -1;
}

public String[] getVariableNames() {
	return variableNames;
}

public double[] getVariableWeights() {
	if(weights instanceof VariableWeights)
	{
		return ((VariableWeights)weights).getWeightData();
	}
	return null;
}

public String getCSV() {
	
	StringBuffer buffer = new StringBuffer();
	
	
	int numColumns = variableNames.length;
	int numRows = rowData.size();
	//
	// print names of columns
	//
	for (int i = 0; i < numColumns; i++){
		if (i>0){
			buffer.append(", ");
		}
		buffer.append(variableNames[i]);
	}
	buffer.append("\n");
	//
	// print weights
	//
	//for (int i = 0; i < numColumns; i++){
		//if (i>0){
			//buffer.append(", ");
		//}
		//buffer.append(columnWeights[i]);
	//}
	//buffer.append("\n");
	//
	// print data
	//
	for (int i = 0; i < numRows; i++){
		double row[] = (double[])rowData.get(i);
		for (int j = 0; j < numColumns; j++){
			if (j>0){
				buffer.append(", ");
			}
			buffer.append(row[j]);
		}
		buffer.append("\n");
	}
	
	return buffer.toString();	
}

public int getNumVariables() {
	return variableNames.length;
}

public int getNumDataRows() {
	return rowData.size();
}

public double[] getDataByRow(int rowIndex) {
	return (double[])rowData.elementAt(rowIndex);
}

public String getVCML() {
	
	StringBuffer buffer = new StringBuffer();
	
	buffer.append("SpatialReferenceData {\n");
	
	int numVariables = variableNames.length;
	int numRows = rowData.size();
	buffer.append(numRows+" "+numVariables+"\n");
	//
	// print names of columns
	//
	for (int i = 0; i < numVariables; i++){
		if (i>0){
			buffer.append(" ");
		}
		buffer.append(variableNames[i]);
	}
	buffer.append("\n");
	//
	// print weights
	//
	if(weights instanceof TimeWeights || weights instanceof VariableWeights)
	{
		double[] weightData = null;
		if(weights instanceof TimeWeights)
		{
			weightData = ((TimeWeights)weights).getWeightData();
		}
		else
		{
			weightData= ((VariableWeights)weights).getWeightData();
		}
		for (int i = 0; i < weightData.length; i++){
			if (i>0){
				buffer.append(" ");
			}
			buffer.append(weightData[i]);
		}
		buffer.append("\n");
	}
	buffer.append("\n");
	//
	// print data
	//
	for (int i = 0; i < numRows; i++){
		double row[] = (double[])rowData.get(i);
		for (int j = 0; j < row.length; j++){
			if (j>0){
				buffer.append(" ");
			}
			buffer.append(row[j]);
		}
		buffer.append("\n");
	}
	
	buffer.append("}\n");
	
	return buffer.toString();	
}


public int getDataSize() {
	return dataSize.getXYZ();
}


public int findColumn(String colName) {
	return findVariable(colName);
}


public double[] getDataByColumn(int columnIndex) {
	if (columnIndex != 0) {
		throw new RuntimeException("SpatialReferenceData only supports getColumeData(int columnIndex) for time");
	}
	int count = 0;
	double[] times = new double[rowData.size()];
	for (double[] data : rowData) {
		times[count ++] = data[0];
	}
	return times;
}


public String[] getColumnNames() {
	return variableNames;
}

public int getNumDataColumns() {
	return variableNames.length;
}

public ISize getDataISize() {
	return dataSize;
}

/**
 * It's a legacy method, which expectes weights as VariableWeights.
 * returns an array of the length of variables (excluding 't')
 */
public double[] getColumnWeights() 
{
	if(weights instanceof VariableWeights)
	{
		return ((VariableWeights)weights).getWeightData();
	}
	return null;
}

public Weights getWeights() {
	return weights;
}

}
