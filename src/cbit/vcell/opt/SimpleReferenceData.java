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

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.util.ColumnDescription;
/**
 * Insert the type's description here.
 * Creation date: (8/3/2005 3:07:17 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class SimpleReferenceData implements ReferenceData, Serializable 
{
	private String dataNames[] = null;
	private Weights weights = null;
	//double[].length is number of variables, vector saves time series of rows(number of variables)
	private Vector<double[]> rowData = new Vector<double[]>();
	
	/**
	 * @param argRowData, vector size is the number of rows(e.g.times) and double array length is the number of columns(e.g. variables)
	 */
	public SimpleReferenceData(String[] argColumnNames, Weights argWeights, Vector<double[]> argRowData) {
		super();
		
		this.dataNames = argColumnNames;
		for (int i = 0; i < argRowData.size(); i++){
			double[] dataRow = argRowData.elementAt(i);
			if (dataRow == null || dataRow.length != argColumnNames.length){
				throw new IllegalArgumentException("The row No." + i + " of Data is null or its length is not the same size as number of columns.");
			}
		}
		this.weights = argWeights;
		this.rowData = argRowData;
	}
	
	/**
	 * The argWeights here are the column weights(one variable corresponding to one column, data in the same column have the same weights).
	 * The time should not be weighted. argWeights.length = argColumnNames - 1(excluding 't')
	 * The argRowData, vector size is the number of rows(e.g.times) and double array length is the number of columns(e.g. variables) 
	 */
	public SimpleReferenceData(String[] argColumnNames, double[] argWeights, Vector<double[]> argRowData) {
		super();
		this.dataNames = argColumnNames;
		
		for (int i = 0; i < argRowData.size(); i++){
			double[] dataRow = argRowData.elementAt(i);
			if (dataRow == null || dataRow.length != argColumnNames.length){
				throw new IllegalArgumentException("The row No." + i + " of Data is null or its length is not the same size as number of columns.");
			}
		}
		
		this.weights = new VariableWeights(argWeights);
		this.rowData = argRowData;
	}

	/**
	 * @param columnData, first dimension is the number of columns(e.g.variables), the second dimension is the number of rows(e.g.times)
	 */
	public SimpleReferenceData(String[] argColumnNames, Weights argWeights, double[][] columnData) {
		super();
		this.dataNames = argColumnNames;
		this.weights = argWeights;
		if(columnData != null && columnData.length > 0 && columnData[0] != null)
		{
			if(argColumnNames.length == columnData.length)
			{
				for (int i = 0; i < columnData[0].length; i++)//loop through number of rows
				{
					double[] dataRow = new double[columnData.length];
					for (int j = 0; j < columnData.length; j++){
						dataRow[j] = columnData[j][i];
					}
					this.rowData.add(dataRow);
				}
			}else{
				throw new IllegalArgumentException("Number of variables of data are not the same as number of column names.");
			}
		}else{
			throw new IllegalArgumentException("Data are null or have no elements.");
		}
	}
	
	/**
	 * The argWeights here are the column weights(one variable corresponding to one column, data in the same column have the same weights).
	 * The time should not be weighted. argWeights.length = argColumnNames - 1(excluding 't')
	 * The columnData, first dimension is the number of columns(e.g.variables), the second dimension is the number of rows(e.g.times) 
	 */
	public SimpleReferenceData(String[] argColumnNames, double[] argWeights, double[][] columnData) {
		super();
		this.dataNames = argColumnNames;
		this.weights = new VariableWeights(argWeights);
		
		if(columnData != null && columnData.length > 0 && columnData[0] != null)
		{
			if(argColumnNames.length == columnData.length)
			{
				//data
				for (int i = 0; i < columnData[0].length; i++)//loop through number of rows
				{
					double[] dataRow = new double[columnData.length];
					for (int j = 0; j < columnData.length; j++){
						dataRow[j] = columnData[j][i];
					}
					this.rowData.add(dataRow);
				}
			}else{
				throw new IllegalArgumentException("Number of variables of data are not the same as number of column names.");
			}
		}else{
			throw new IllegalArgumentException("Data are null or have no elements.");
		}
	}


/**
 * SimpleConstraintData constructor comment.
 */
public SimpleReferenceData(ReferenceData argReferenceData) {
	super();
	
	this.dataNames = (String[])argReferenceData.getColumnNames().clone();
	if(argReferenceData.getWeights() instanceof VariableWeights){
		this.weights = new VariableWeights((VariableWeights)argReferenceData.getWeights()); 
	} else if(argReferenceData.getWeights() instanceof ElementWeights){
		this.weights = new ElementWeights((ElementWeights)argReferenceData.getWeights()); 
	} else if(argReferenceData.getWeights() instanceof TimeWeights){
		this.weights = new TimeWeights((TimeWeights)argReferenceData.getWeights()); 
	} else{
		throw new IllegalArgumentException("Unknown weight type: " + argReferenceData.getWeights().getClass().getName() + ".");
	}
		
	for (int i = 0; i < argReferenceData.getNumDataRows(); i++){
		this.rowData.add(argReferenceData.getDataByRow(i).clone());
	}
}


/**
 * The argWeights here are the column weights(one variable corresponding to one column, data in the same column have the same weights).
 */
public SimpleReferenceData(RowColumnResultSet rowColumnResultSet, double[] argWeights) {
	super();
	
	ColumnDescription[] columnDescriptions = rowColumnResultSet.getDataColumnDescriptions();
	if(rowColumnResultSet == null || rowColumnResultSet.getRowCount() == 0)
	{
		throw new IllegalArgumentException("The RowColumnResultSet is null or doesn't have row elements.");
	}
	this.dataNames = new String[columnDescriptions.length];
	//dataNames
	for (int i = 0; i < dataNames.length; i++){
		this.dataNames[i] = columnDescriptions[i].getName();
	}
	//weights
	this.weights = new VariableWeights(argWeights);
	//data
	for (int i = 0; i < rowColumnResultSet.getRowCount(); i++){
		this.rowData.add(rowColumnResultSet.getRow(i).clone());
	}
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof SimpleReferenceData){
		SimpleReferenceData srd = (SimpleReferenceData)obj;

		if (!Compare.isEqual(dataNames,srd.dataNames)){
			return false;
		}
		
		if (!Compare.isEqual(weights, srd.weights)){
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


/**
 * Insert the method's description here.
 * Creation date: (8/5/2005 11:49:42 AM)
 * @return int
 * @param colName java.lang.String
 */
public int findColumn(String colName) {
	for (int i = 0; i < dataNames.length; i++){
		if (dataNames[i].equals(colName)){
			return i;
		}
	}
	return -1;
}

/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 8:23:18 PM)
 * @return cbit.vcell.opt.SimpleConstraintData
 * @param tokens cbit.vcell.math.CommentStringTokenizer
 */
@SuppressWarnings("unchecked")
public static SimpleReferenceData fromVCML(CommentStringTokenizer tokens) throws DataAccessException {
	String token = tokens.nextToken();
	if (!token.equals("SimpleReferenceData")){
		throw new DataAccessException("unexpected identifier '"+token+"', expecting '"+"Data"+"'");
	}
	token = tokens.nextToken();
	if (!token.equals("{")){
		throw new RuntimeException("unexpected symbol '"+token+"', expecting '{'");
	}
	int numRows = 0;
	int numColumns = 0;
	try {
		numRows = Integer.parseInt(tokens.nextToken());
	}catch (NumberFormatException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("error reading number of rows: "+e.getMessage());
	}
	
	try {
		numColumns = Integer.parseInt(tokens.nextToken());
	}catch (NumberFormatException e){
		e.printStackTrace(System.out);
		throw new DataAccessException("error reading number of columns: "+e.getMessage());
	}

	String names[] = new String[numColumns];
	for (int i = 0; i < numColumns; i++){
		names[i] = tokens.nextToken();
	}

	double weights[] = new double[numColumns];
	for (int i = 0; i < numColumns; i++){
		weights[i] = Double.parseDouble(tokens.nextToken());
	}
	Vector rowData = new Vector();		
	for (int i = 0; i < numRows; i++){
		double row[] = new double[numColumns];
		for (int j = 0; j < numColumns; j++){
			row[j] = Double.parseDouble(tokens.nextToken());
		}
		rowData.add(row);
	}

	SimpleReferenceData simpleReferenceData = new SimpleReferenceData(names,weights,rowData);

	// read "}" for Data block
	token = tokens.nextToken();
	if (!token.equals("}")){
		throw new RuntimeException("unexpected symbol '"+token+"', expecting '}'");
	}
	return simpleReferenceData;
}


/**
 * Insert the method's description here.
 * Creation date: (8/5/2005 11:43:37 AM)
 * @return double
 * @param columnIndex int
 */
public double[] getDataByColumn(int columnIndex) {
	//
	// bounds check
	//
	int numRows = getNumDataRows();
	int numCols = dataNames.length;
	if (columnIndex<0 || columnIndex>=numCols){
		throw new RuntimeException("columnIndex "+columnIndex+" out of bounds");
	}
	double[] colData = new double[numRows];
	for (int i = 0; i < numRows; i++){
		colData[i] = rowData.elementAt(i)[columnIndex];
	}
	return colData;
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 3:07:17 PM)
 * @return java.lang.String[]
 */
public String[] getColumnNames() {
	return dataNames;
}

/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:09:38 PM)
 * @return java.lang.String
 */
public String getCSV() {
	
	StringBuffer buffer = new StringBuffer();
	
	
	int numColumns = dataNames.length;
	int numRows = rowData.size();
	//
	// print names of columns
	//
	for (int i = 0; i < numColumns; i++){
		if (i>0){
			buffer.append(", ");
		}
		buffer.append(dataNames[i]);
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
		double row[] = rowData.get(i);
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

/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 3:07:17 PM)
 * @return double[]
 * @param rowIndex int
 */
public double[] getDataByRow(int rowIndex) {
	return rowData.elementAt(rowIndex);
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:09:38 PM)
 * @return java.lang.String
 */
public String getVCML() {
	
	StringBuffer buffer = new StringBuffer();
	
	buffer.append("SimpleReferenceData {\n");
	
	int numColumns = dataNames.length;
	int numRows = rowData.size();
	buffer.append(numRows+" "+numColumns+"\n");
	//
	// print names of columns
	//
	for (int i = 0; i < numColumns; i++){
		if (i>0){
			buffer.append(" ");
		}
		buffer.append(dataNames[i]);
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
	else if(weights instanceof ElementWeights)
	{
		double[][] weightData = ((ElementWeights)weights).getWeightData();
		for(int i=0; i<weightData.length; i++)
		{
			double[] rowWeights = weightData[i];
			for (int j = 0; j < weightData[i].length; j++){
				if (j>0){
					buffer.append(" ");
				}
				buffer.append(rowWeights[j]);
			}
			buffer.append("\n");
		}
	}
	buffer.append("\n");
	//
	// print data
	//
	for (int i = 0; i < numRows; i++){
		double row[] = rowData.get(i);
		for (int j = 0; j < numColumns; j++){
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

public int getDataSize() {//means data is 0 dimension. ISize = 1.
	return 1;
}

/**
 * It's a legacy method. Only when the weights are Variable Weights
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

public int getNumDataColumns() {
	return dataNames.length;
}

public int getNumDataRows() {
	return rowData.size();
}

public Weights getWeights() {
	return weights;
}

}//end of class
