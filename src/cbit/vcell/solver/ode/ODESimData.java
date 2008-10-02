package cbit.vcell.solver.ode;

import cbit.vcell.util.ColumnDescription;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.server.*;
import cbit.vcell.math.*;
import java.io.*;
import java.util.*;
import cbit.vcell.simdata.*;
import cbit.vcell.solvers.FunctionFileGenerator;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:57:59 PM)
 * @author: John Wagner
 */
public class ODESimData extends ODESolverResultSet implements SimDataConstants, Serializable {
	private String formatID = null;
	private String mathName = null;

/**
 * SimpleODEData constructor comment.
 */
private ODESimData() {
	
}

public ODESimData(VCDataIdentifier vcdId, ODESolverResultSet odeSolverResultSet) {
	int rowCount = odeSolverResultSet.getRowCount();
	//
	this.formatID = COMPACT_ODE_DATA_FORMAT_ID;
	this.mathName = vcdId.getID();
	ColumnDescription dataColumns[] = odeSolverResultSet.getDataColumnDescriptions();
	for (int c = 0; c < dataColumns.length; c++) {
		if(dataColumns[c] instanceof ODESolverResultSetColumnDescription)
		    addDataColumn(new ODESolverResultSetColumnDescription((ODESolverResultSetColumnDescription)dataColumns[c]));
	}
	for (int r = 0; r < rowCount; r++) {
		addRow(odeSolverResultSet.getRow(r));
	}
	FunctionColumnDescription functionColumns[] = odeSolverResultSet.getFunctionColumnDescriptions();
	for (int c = 0; c < functionColumns.length; c++) {
		try {
			addFunctionColumn(new FunctionColumnDescription(functionColumns[c]));
		}catch (cbit.vcell.parser.ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
}


/**
 * SimpleODEData constructor comment.
 */
public ODESimData(DataInputStream input) throws IOException {
	readIn(input);
	input.close();
}


/**
 * getVariableNames method comment.
 */
public String getFormatID() {
	return formatID;
}


/**
 * getVariableNames method comment.
 */
public MathDescription getMathDescription () {
	//  Why should this not be called???  JMW needs to doc this class!!!
	// cbit.util.Assertion.assert(false, "ODESimData.getMathDescription() should not be called");
	return (null);
}


/**
 * getVariableNames method comment.
 */
public String getMathName() {
	return mathName;
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 2:10:15 PM)
 * @return int
 */
public long getSizeInBytes() {
	long sizeInBytes = getFormatID().length() + getMathName().length();
	cbit.vcell.util.ColumnDescription dataColumns[] = getDataColumnDescriptions();
	for (int c = 0; c < dataColumns.length; c++) {
		if(dataColumns[c] instanceof ODESolverResultSetColumnDescription)
		{
			sizeInBytes += ((ODESolverResultSetColumnDescription)dataColumns[c]).getVariableName().length();
			sizeInBytes += ((ODESolverResultSetColumnDescription)dataColumns[c]).getDisplayName().length();
			sizeInBytes += (((ODESolverResultSetColumnDescription)dataColumns[c]).getParameterName()!=null)?(dataColumns[c].getParameterName().length()):((new String("null")).length());
		}
	}
	sizeInBytes += 8*(getRowCount())*(getDataColumnCount());
	FunctionColumnDescription functionColumns[] = getFunctionColumnDescriptions();
	for (int c = 0; c < functionColumns.length; c++) {
		sizeInBytes += functionColumns[c].getFunctionName().length();
		sizeInBytes += functionColumns[c].getDisplayName().length();
		sizeInBytes += (functionColumns[c].getParameterName()!=null)?(functionColumns[c].getParameterName().length()):((new String("null")).length());
		sizeInBytes += functionColumns[c].getExpression().toString().length();
	}
	return (sizeInBytes);
}


/**
 * JMW : This really should be synchronized...
 */
public void readIn(DataInputStream input) throws IOException {
	formatID = input.readUTF();
	if (formatID.equals(SIMPLE_ODE_DATA_FORMAT_ID)) {
		this.mathName = input.readUTF();
		// read data from old format file
		double saveInterval = input.readDouble();
		int savedNumber = input.readInt();
		int variableNumber = input.readInt();
		String[] variableNames = new String[variableNumber];
		double[][] dataValues = new double[savedNumber][variableNumber];
		for (int i = 0; i < variableNumber; i++) {
			int flag = input.readInt();
			variableNames[i] = input.readUTF();
			for (int j = 0; j < savedNumber; j++) {
				dataValues[j][i] = input.readDouble();
			}
		}
		// now put data in new data structure
		int rowCount = savedNumber;
		int columnCount = variableNumber + 1;
		addDataColumn(new ODESolverResultSetColumnDescription("t", "t"));
		for (int c = 1; c < columnCount; c++) {
			addDataColumn(new ODESolverResultSetColumnDescription(variableNames[c - 1], variableNames[c - 1]));
		}
		double[] values = new double[columnCount];
		for (int c = 0; c < columnCount; c++)
			values[c] = 0.0;
		for (int r = 0; r < rowCount; r++) {
			values[0] = r * saveInterval / 1000.0;
			addRow(values);
		}
		for (int c = 1; c < columnCount; c++) {
			for (int r = 0; r < rowCount; r++) {
				setValue(r, c, dataValues[r][c - 1]);
			}
		}
	} else if (formatID.equals(GENERIC_ODE_DATA_FORMAT_ID)) {
		this.mathName = input.readUTF();
		int rowCount = input.readInt();
		int columnCount = input.readInt();
		for (int c = 0; c < columnCount; c++) {
			String columnName = input.readUTF();
			String columnDisplayName = input.readUTF();
			addDataColumn(new ODESolverResultSetColumnDescription(columnName, columnDisplayName));
		}
		double[] values = new double[columnCount];
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < columnCount; c++) {
				values[c] = input.readDouble();
			}
			addRow(values);
		}
	} else if (formatID.equals(COMPACT_ODE_DATA_FORMAT_ID)){
		this.mathName = input.readUTF();
		int rowCount = input.readInt();
		int columnCount = input.readInt();
		for (int c = 0; c < columnCount; c++) {
			String columnName = input.readUTF();
			String columnDisplayName = input.readUTF();
			String columnParameterName = input.readUTF();
			if (columnParameterName.equals("null")){
				columnParameterName = null;
			}
			addDataColumn(new ODESolverResultSetColumnDescription(columnName, columnParameterName, columnDisplayName));
		}
		double[] values = new double[columnCount];
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < columnCount; c++) {
				values[c] = input.readDouble();
			}
			addRow(values);
		}
		try
		{
			int functionCount = input.readInt();
			for (int c = 0; c < functionCount; c++) {
				String columnName = input.readUTF();
				String columnDisplayName = input.readUTF();
				String columnParameterName = input.readUTF();
				if (columnParameterName.equals("null")){
					columnParameterName = null;
				}
				String expressionString = input.readUTF();
				try {
					Expression expression = new Expression(expressionString);
					addFunctionColumn(new FunctionColumnDescription(expression, columnName, columnParameterName, columnDisplayName, false));
				}catch (cbit.vcell.parser.ExpressionBindingException e){
					e.printStackTrace(System.out);
					System.out.println("ODESimData.readIn(): unable to bind expression '"+expressionString+"'");
				}catch (cbit.vcell.parser.ExpressionException e){
					e.printStackTrace(System.out);
					System.out.println("ODESimData.readIn(): unable to parse expression '"+expressionString+"'");
				}
			}
		}catch (EOFException e){}
	} else {
		throw new IOException("DataInputStream is wrong format '"+formatID+"'");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 3:55:39 PM)
 * @return cbit.vcell.simdata.ODESimData
 * @param odeDataFile java.io.File
 * @exception cbit.vcell.server.DataAccessException The exception description.
 */
public static ODESimData readODEDataFile(File odeDataFile) throws DataAccessException {
	try {
		FileInputStream fileIn = new FileInputStream(odeDataFile);
		// for performance reasons, esp. if relying on network, esp. when using Samba (sucks !!)
		long length = odeDataFile.length();
		byte[] bytes = new byte[(int)length];
		try {
			//
			// loop through and read all 'length' bytes or until end-of-file (bytesRead==-1)
			// this is because fileIn.read(buffer) not guarenteed to get all bytes in one call.
			//
			int totalBytesRead = 0;
			while (totalBytesRead < length){
				int offset = totalBytesRead;
				int bytesRead = fileIn.read(bytes,offset,(int)length-offset);
				if (bytesRead == -1){
					break;
				}else{
					totalBytesRead += bytesRead;
				}
			}
		}finally{
			if (fileIn != null) fileIn.close();
		}
		ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
		//
		DataInputStream dataIn = new DataInputStream(bytesIn);
		String formatID = dataIn.readUTF();
		dataIn.close();
		if (formatID.equals(SIMPLE_ODE_DATA_FORMAT_ID) ||
			formatID.equals(GENERIC_ODE_DATA_FORMAT_ID) ||
			formatID.equals(COMPACT_ODE_DATA_FORMAT_ID)) {
				
			bytesIn = new ByteArrayInputStream(bytes);
			dataIn = new DataInputStream(bytesIn);
			ODESimData simpleODEData = new ODESimData(dataIn);
			return simpleODEData;
		}else{
			throw new DataAccessException("Unknown file format \""+formatID+"\" for " + odeDataFile.getPath());
		}
	} catch (Exception exc) {
		throw new DataAccessException(exc.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 12:57:22 PM)
 * @param odeDataFile java.io.File
 * @param odeSimData cbit.vcell.export.data.ODESimData
 * @exception java.io.IOException The exception description.
 */
public static void writeODEDataFile(ODESimData odeSimData, File odeDataFile) throws IOException {
	
	FileOutputStream fileOut = new FileOutputStream(odeDataFile);
	try {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(bytesOut);
		synchronized (odeSimData) {
			odeSimData.writeOut(dataOut);
		}
		dataOut.close();
		byte[] bytes = bytesOut.toByteArray();
		fileOut.write(bytes);
	} finally {
		if (fileOut != null) {
			fileOut.close();
		}
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 12:49:44 PM)
 * @param odeLogFile java.io.File
 * @param odeDataFile java.io.File
 * @param formatID java.lang.String
 */
public void writeODELogFile(File odeLogFile, File odeDataFile) throws IOException {

	FileWriter fw = new FileWriter(odeLogFile);
	fw.write(
		ODE_DATA_IDENTIFIER + "\n" +
		COMPACT_ODE_DATA_FORMAT_ID + "\n" +
		odeDataFile.getName() + "\n"
	);
	fw.close();
		
}


/**
 * JMW : This really should be synchronized...
 */
public void writeOut(DataOutputStream output) throws IOException {
	output.writeUTF(COMPACT_ODE_DATA_FORMAT_ID);
	output.writeUTF(mathName);
	output.writeInt(getRowCount());
	output.writeInt(getDataColumnCount());
	ColumnDescription dataColumns[] = getDataColumnDescriptions();
	for (int c = 0; c < dataColumns.length; c++) 
	{
		if(dataColumns[c] instanceof ODESolverResultSetColumnDescription)
		{
			output.writeUTF(((ODESolverResultSetColumnDescription)dataColumns[c]).getVariableName());
			output.writeUTF(((ODESolverResultSetColumnDescription)dataColumns[c]).getDisplayName());
			if (((ODESolverResultSetColumnDescription)dataColumns[c]).getParameterName()!=null){
				output.writeUTF(((ODESolverResultSetColumnDescription)dataColumns[c]).getParameterName());
			}else{
				output.writeUTF("null");
			}
		}
	}                          
	for (int r = 0; r < getRowCount(); r++) {
		double row[] = getRow(r);
		for (int c = 0; c < getDataColumnCount(); c++) {
			output.writeDouble(row[c]);
		}
	}
	output.writeInt(getFunctionColumnCount());
	FunctionColumnDescription functionColumns[] = getFunctionColumnDescriptions();
	for (int c = 0; c < getFunctionColumnCount(); c++) {
		//
		// Write only the functions that are not user-defined (those generated in the model) to the output.
		//
		if (!functionColumns[c].getIsUserDefined()) {
			output.writeUTF(functionColumns[c].getFunctionName());
			output.writeUTF(functionColumns[c].getDisplayName());
			if (functionColumns[c].getParameterName()!=null){
				output.writeUTF(functionColumns[c].getParameterName());
			}else{
				output.writeUTF("null");
			}
			output.writeUTF(functionColumns[c].getExpression().infix());
		}
	}                          
}


public static ODESimData readIDADataFile(VCDataIdentifier vcdId, File dataFile, int keepMost, File functionsFile) throws DataAccessException {
	// read ida file
	System.out.println("reading ida file : " + dataFile);
	ODESimData odeSimData = new ODESimData();	
	odeSimData.formatID = IDA_DATA_FORMAT_ID;
	odeSimData.mathName = vcdId.getID();
	
	BufferedReader bufferedReader = null;
	try {
		bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
		//  Read header
		String line = bufferedReader.readLine();
		if (line == null) {
			//  throw exception
			return null;
		}
		StringTokenizer st = new StringTokenizer(line, ":");
		while (st.hasMoreTokens()) {
			odeSimData.addDataColumn(new ODESolverResultSetColumnDescription(st.nextToken()));
		}
		//  Read data
		while ((line = bufferedReader.readLine()) != null) {
			st = new StringTokenizer(line);
			double[] values = new double[odeSimData.getDataColumnCount()];
			int count = 0;
			while (st.hasMoreTokens()) {
				values[count ++] = Double.valueOf(st.nextToken()).doubleValue();
			}
			if (count == odeSimData.getDataColumnCount()){
				odeSimData.addRow(values);
			} else {
				break;
			}
		}
		//
	} catch (Exception e) {
		e.printStackTrace(System.out);
		return null;
	} finally {
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
	
	// read functions file
	
	Vector<AnnotatedFunction> funcList;
	try {
		funcList = FunctionFileGenerator.readFunctionsFile(functionsFile);
		for (AnnotatedFunction func : funcList){
			try {
				Expression expression = new Expression(func.getExpression());
				odeSimData.addFunctionColumn(new FunctionColumnDescription(expression, func.getName(), null, func.getName(), false));
			} catch (ExpressionException e) {
				throw new RuntimeException("Could not add function " + func.getName() + " to annotatedFunctionList");
			}
		}	
	} catch (FileNotFoundException e1) {
		e1.printStackTrace(System.out);
		throw new DataAccessException(e1.getMessage());
	} catch (IOException e1) {
		e1.printStackTrace(System.out);
		throw new DataAccessException(e1.getMessage());
	}

	if (keepMost > 0) {
		odeSimData.trimRows(keepMost);
	}
	return odeSimData;
}
}