package cbit.vcell.solver.stoch;
import cbit.vcell.util.ColumnDescription;
import cbit.vcell.solver.ode.*;
import cbit.vcell.parser.*;
import cbit.vcell.server.*;
import cbit.vcell.math.*;
import java.io.*;
import java.util.*;
import cbit.vcell.simdata.*;

/**
 * Insert the type's description here.
 * Creation date: (7/26/2006 5:55:03 PM)
 * @author: Tracy LI
 */
public class StochSimData extends StochSolverResultSet implements SimDataConstants, java.io.Serializable {
/**
 * StochSimData constructor comment.
 */
	private String formatID = null;
	private String mathName = null;

public StochSimData(VCDataIdentifier vcdId, StochSolverResultSet stochSolverResultSet) {
	int rowCount = stochSolverResultSet.getRowCount();
	//
	this.formatID = COMPACT_STOCH_DATA_FORMAT_ID;
	this.mathName = vcdId.getID();
	ColumnDescription dataColumns[] = stochSolverResultSet.getDataColumnDescriptions();
	for (int c = 0; c < dataColumns.length; c++) {
		if(dataColumns[c] instanceof StochSolverResultSetColumnDescription)
			addDataColumn(new StochSolverResultSetColumnDescription((StochSolverResultSetColumnDescription)dataColumns[c]));
	}
	for (int r = 0; r < rowCount; r++) {
		addRow(stochSolverResultSet.getRow(r));
	}
	
}


/**
 * Stochastic simulation data  constructor.
 * @Input: DataInputStream
 */
public StochSimData(DataInputStream input) throws IOException {
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
	ColumnDescription dataColumns[] = getDataColumnDescriptions();
	for (int c = 0; c < dataColumns.length; c++) 
	{
		if(dataColumns[c] instanceof StochSolverResultSetColumnDescription)
		{
			sizeInBytes += ((StochSolverResultSetColumnDescription)dataColumns[c]).getVariableName().length();
			sizeInBytes += ((StochSolverResultSetColumnDescription)dataColumns[c]).getDisplayName().length();
			sizeInBytes += (((StochSolverResultSetColumnDescription)dataColumns[c]).getParameterName()!=null)?(((StochSolverResultSetColumnDescription)dataColumns[c]).getParameterName().length()):((new String("null")).length());
		}
	}
	sizeInBytes += 8*(getRowCount())*(getDataColumnCount());
	return (sizeInBytes);
}


/**
 * it is called from the constructor. DataInputStream is generated from '.stochbi' file.
 * it is not being used so far, since in order to display data in ODEDataViewer(actually is time-series viewer),
 * we have to load result binary (".stochbi") to ODESimData.
 */
public void readIn(DataInputStream input) throws IOException {
	formatID = COMPACT_ODE_DATA_FORMAT_ID;
	if (formatID.equals(COMPACT_ODE_DATA_FORMAT_ID)){
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
public static ODESimData readStochDataFile(File stochDataFile) throws DataAccessException {
	try {
		FileInputStream fileIn = new FileInputStream(stochDataFile);
		// for performance reasons, esp. if relying on network.
		long length = stochDataFile.length();
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
		if (formatID.equals(COMPACT_STOCH_DATA_FORMAT_ID)) {
				
			bytesIn = new ByteArrayInputStream(bytes);
			dataIn = new DataInputStream(bytesIn);
			ODESimData simpleStochData = new ODESimData(dataIn);
			return simpleStochData;
		}else{
			throw new DataAccessException("Unknown file format \""+formatID+"\" for " + stochDataFile.getPath());
		}
	} catch (Exception exc) {
		throw new DataAccessException(exc.getMessage());
	}
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
		if(dataColumns[c] instanceof StochSolverResultSetColumnDescription)
		{	
			output.writeUTF(((StochSolverResultSetColumnDescription)dataColumns[c]).getVariableName());
			output.writeUTF(((StochSolverResultSetColumnDescription)dataColumns[c]).getDisplayName());
			if (((StochSolverResultSetColumnDescription)dataColumns[c]).getParameterName()!=null){
				output.writeUTF(((StochSolverResultSetColumnDescription)dataColumns[c]).getParameterName());
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
	                        
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 12:57:22 PM)
 * @param odeDataFile java.io.File
 * @param odeSimData cbit.vcell.export.data.ODESimData
 * @exception java.io.IOException The exception description.
 */
public static void writeStochDataFile(StochSimData stochSimData, File stochDataFile) throws IOException {
	
	FileOutputStream fileOut = new FileOutputStream(stochDataFile);
	try {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(bytesOut);
		synchronized (stochSimData) {
			stochSimData.writeOut(dataOut);
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
public void writeStochLogFile(File stochLogFile, File stochDataFile) throws IOException {

	FileWriter fw = new FileWriter(stochLogFile);
	fw.write(
		ODE_DATA_IDENTIFIER + "\n" +
		COMPACT_ODE_DATA_FORMAT_ID + "\n" +
		stochDataFile.getName() + "\n"
	);
	fw.close();
		
}
}