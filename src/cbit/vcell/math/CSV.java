/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import java.text.ParseException;

import cbit.vcell.math.ODESolverResultSetColumnDescription;

/**
 * 	the format assumed by this class should:
 *		-have first line for names of each variable
 *		-of course all lines must have same number of fields (= number of vars), separated by commas
 *		-all lines after the first one must have numbers of type double, recognizable by Double.parseDouble(String)
 * Creation date: (7/5/2005 2:43:48 PM)
 * @author: Ben Ramot
 */
public class CSV {
/**
 * CSV constructor comment.
 */
public CSV() {
	super();
}
/**
 * This exports the given data into a CSV file via an OutputStream.
 * Creation date: (7/5/2005 3:12:09 PM)
 * @param outputStream java.io.OutputStream
 * @param data data_structures.UnstructuredData
 */
public void exportTo(java.io.OutputStream outputStream, cbit.vcell.math.RowColumnResultSet data) throws Exception {
	java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(new java.io.OutputStreamWriter(outputStream));

	int varCount = data.getDataColumnCount();
	
	//write column headings
	for (int i = 0; i < varCount; i++){
		writeString(bufferedWriter, data.getDataColumnDescriptions()[i].getName());
		if (i != varCount-1) { writeString(bufferedWriter, ","); }
	}
	writeString(bufferedWriter, "\n");
	flushString(bufferedWriter);

	//write rows
	double[] rowVector;
	for (int row = 0; row < data.getRowCount(); row++){
		rowVector = data.getRow(row);
		//for each row, write values
		for (int i = 0; i < varCount; i++){
			writeString(bufferedWriter, rowVector[i]);
			if (i != varCount-1) { writeString(bufferedWriter, ","); }
		}
		writeString(bufferedWriter, "\n");
		flushString(bufferedWriter);
	}
}
/**
 * This flushes a BufferedWriter while catching the exception.
 * Creation date: (7/5/2005 2:48:06 PM)
 * @param bufferedWriter java.io.BufferedWriter
 */
private void flushString(java.io.BufferedWriter bufferedWriter) {
	try {
		bufferedWriter.flush();
	} catch (java.io.IOException e) {
	}
}
/**
 * This imports data in CSV format from a given InputStream and returns the data.
 * Creation date: (7/5/2005 3:13:16 PM)
 * @return data_structures.UnstructuredData
 * @param inputStream java.io.InputStream
 */
public cbit.vcell.math.RowColumnResultSet importFrom(java.io.Reader reader) throws ParseException {
	java.io.BufferedReader bufferedReader = new java.io.BufferedReader(reader);
	cbit.vcell.math.RowColumnResultSet data = new cbit.vcell.math.RowColumnResultSet();
	
	String[] stringTokens;
	double[] doubleTokens;
	try{
	String firstLine = bufferedReader.readLine();//to read in column names.
	//to read in first row of data to get an idea of data length(used for parsing column names).
	doubleTokens = nextdoubles(bufferedReader);
	
	//setup column names from header line
	stringTokens = readColumnNames(firstLine, doubleTokens.length);
		
	for (int i = 0; i < stringTokens.length; i++){
		data.addDataColumn(new ODESolverResultSetColumnDescription(stringTokens[i]));
	}

	//other rows
	while (doubleTokens != null) {
		data.addRow(doubleTokens);
		doubleTokens = nextdoubles(bufferedReader);
	}

	return data;
	}catch(Exception e){
		throw (ParseException) new ParseException("Error importing values\n"+e.getMessage(), -1).initCause(e);
	}
}

/**
 * This reads a line from a BufferedReader and returns it in the form of an array of doubles, tokenized by the comma character.  If there are no more lines, this should return null.
 * Creation date: (7/5/2005 2:50:03 PM)
 * @return double[]
 * @param bufferedReader java.io.BufferedReader
 */
private double[] nextdoubles(java.io.BufferedReader bufferedReader) throws Exception {
	String[] stringTokens = nextStrings(bufferedReader);

	//check if end of file
	if (stringTokens==null) { return null; }

	double[] doubleTokens = new double[stringTokens.length];
	//convert array from nextStrings into an array of doubles
	for (int i = 0; i < stringTokens.length; i++){
		doubleTokens[i] = Double.parseDouble(stringTokens[i]);
	}
	return doubleTokens;
}
/**
 * This reads a string from a BufferedReader while catching the exception.
 * Creation date: (7/5/2005 2:50:47 PM)
 * @return java.lang.String
 * @param bufferedReader java.io.BufferedReader
 */
private String nextLine(java.io.BufferedReader bufferedReader) {
	try {
		return bufferedReader.readLine();
	} catch (java.io.IOException e) {
		return null;
	}
}
/**
 * This reads a line from a BufferedReader and returns it in the form of an array of Strings, tokenized by the comma character.  If there are no more lines, this should return null.
 * Creation date: (7/5/2005 2:51:43 PM)
 * @return java.lang.String[]
 * @param bufferedReader java.io.BufferedReader
 */
private String[] nextStrings(java.io.BufferedReader bufferedReader) throws Exception{
	try {
		String s = nextLine(bufferedReader);
		
		//check if end of file
		if ((s==null) || (s.equals(""))) { return null; }
		
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(s, ",\t ");

		int tokenCount = tokenizer.countTokens();
		String[] tokens = new String[tokenCount];
		for (int i = 0; i < tokenCount; i++){
			tokens[i] = tokenizer.nextToken();
		}
		return tokens;
	} catch (Exception e) {
		throw new Exception("Error when parsing data rows: " + e.getMessage());
	}
}

private String[] readColumnNames(String columnLine, int numDataColumn) throws Exception
{
	try {
		//check if end of file
		if ((columnLine==null) || (columnLine.equals(""))) { return null; }
		//setup columns from header line, it's a little complicated, since users can put anything into the column name
		//first use comma and tab as delimiter
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(columnLine, ",\t");
		//if using comma and tab doesn't parse the column name properly, try space delimiter
		if(tokenizer.countTokens() != numDataColumn)
		{
			tokenizer = new java.util.StringTokenizer(columnLine, " ");
			if(tokenizer.countTokens() < numDataColumn)
			{
				throw new Exception("Num of data in each row is greater than num of column names.\nPlease check your data column names(the length of column names should equal to lenght of data row.");
			}
			if(tokenizer.countTokens() > numDataColumn)
			{
				throw new Exception("Num of data in each row is smaller than num of column names.\nSolution:\n1.Check if you have put more column names than the length of data row.\n" +
						"2.Seperate your column names with comma or tab if you want to use space in any of your column name. ");
			}
		}
		
		
		int tokenCount = tokenizer.countTokens();
		String[] tokens = new String[tokenCount];
		for (int i = 0; i < tokenCount; i++){
			tokens[i] = tokenizer.nextToken().trim();
		}
		return tokens;
	} catch (Exception e) {
		throw new Exception("Error when parsing column names: " + e.getMessage());
	}
}

/**
 * This writes a double to a BufferedWriter while catching the exception.
 * Creation date: (7/5/2005 2:54:05 PM)
 * @param bufferedWriter java.io.BufferedWriter
 * @param number double
 */
private void writeString(java.io.BufferedWriter bufferedWriter, double number) {
	try {
		bufferedWriter.write(Double.toString(number));
	} catch (java.io.IOException e) {
		
	}
}
/**
 * This writes a string to a BufferedWriter while catching the exception.
 * Creation date: (7/5/2005 2:55:11 PM)
 * @param bufferedWriter java.io.BufferedWriter
 * @param string java.lang.String
 */
private void writeString(java.io.BufferedWriter bufferedWriter, String string) {
	try {
		bufferedWriter.write(string);
	} catch (java.io.IOException e) {
		
	}
}
}
