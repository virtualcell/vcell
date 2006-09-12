package cbit.vcell.export;

/**
 * Insert the type's description here.
 * Creation date: (6/28/2005 4:46:48 PM)
 * @author: Ben Ramot
 */
public class MAT {
	
/**
 * MATImportExport constructor comment.
 */
public MAT() {
	super();
}
/**
 * this appends 8 bytes.
 * Creation date: (6/30/2005 6:28:18 PM)
 * @param x java.io.DataOutputStream
 */
private void append(double x, java.io.DataOutputStream stream) {
	try {
		stream.writeDouble(x);
	} catch (java.io.IOException e) {
		
	}
	
}
/**
 * this appends 4 bytes.
 * Creation date: (6/30/2005 6:28:18 PM)
 * @param x java.io.DataOutputStream
 */
private void append(int n, java.io.DataOutputStream stream) {
	try {
		stream.writeInt(n);
	} catch (java.io.IOException e) {
		
	}
	
}
/**
 * This appends a string to a DataOutputStream while catching the exception.
 * Creation date: (6/30/2005 6:28:18 PM)
 * @param x java.io.DataOutputStream
 */
private void append(String s, java.io.DataOutputStream stream) {
	try {
		stream.writeBytes(s);
	} catch (java.io.IOException e) {
		
	}
	
}
/**
 * This appends a byte to a DataOutputStream while catching the exception.
 * Creation date: (6/30/2005 6:28:18 PM)
 * @param x java.io.DataOutputStream
 */
private void appendByte(int b, java.io.DataOutputStream stream) {
	try {
		stream.writeByte(b);
	} catch (java.io.IOException e) {
		
	}
	
}
/**
 * This exports an UnstructuredData to a MAT file as a collection of column vectors, one for each variable, with the values in the same order of the data points in the original data structure.
 * Creation date: (6/28/2005 4:56:15 PM)
 * @param theFile java.io.OutputStream
 * @param data data_structures.UnstructuredData
 */
public void exportTo(java.io.OutputStream theFile, cbit.vcell.util.RowColumnResultSet data) throws cbit.vcell.parser.ExpressionException {
	java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(theFile);

	insertHeader(dataOutputStream);
	flush(dataOutputStream);

	int varCount = data.getDataColumnCount();

	for (int i = 0; i < varCount; i++){
		insertArray(data.getDataColumnDescriptions()[i].getName(), data.extractColumn(i), dataOutputStream);
		flush(dataOutputStream);
	}
}
/**
 * This flushes a DataOutputStream while catching the exception.
 * Creation date: (6/30/2005 6:24:14 PM)
 * @param stream java.io.DataOutputStream
 */
private void flush(java.io.DataOutputStream stream) {
	try {
		stream.flush();
	} catch (java.io.IOException e) {
		
	}
}
/**
 * This adds a double-array subelement to the MAT file.
 * Creation date: (6/30/2005 6:04:41 PM)
 * @param name java.lang.String
 * @param array double[]
 * @param stream java.io.DataOutputStream
 */
private void insertArray(String name, double[] array, java.io.DataOutputStream stream) {
	String namePadding = "";
	int namePaddingCount = 8 - (name.length() % 8);
	if (namePaddingCount==8) {namePaddingCount=0;}	//apparently matlab doesnt like complete blank lines
	for (int i = 1; i <= namePaddingCount; i++){
		namePadding += " ";
	}

	//array element header, with total byte count
	append(14, stream);
	append(6*8 + name.length() + namePaddingCount + 8*array.length, stream);

	//flags
	append(6, stream);
	append(8, stream);
	appendByte(0, stream);
	appendByte(0, stream);
	appendByte(4, stream);
	appendByte(6, stream);
	append("    ", stream);

	//dim
	append(5, stream);
	append(8, stream);
	append(array.length, stream);
	append(1, stream);

	//name
	append(1, stream);
	append(name.length(), stream);
	append(name, stream);
	append(namePadding, stream);

	//data
	append(9, stream);
	append(8*array.length, stream);
	for (int i = 0; i < array.length; i++){
		append(array[i], stream);
	}
}
/**
 * This adds the header section of the MAT file.
 * Creation date: (6/30/2005 6:03:26 PM)
 * @param stream java.io.DataOutputStream
 */
private void insertHeader(java.io.DataOutputStream stream) {
	String header = "created by Virtual Cell\n" + new java.util.Date();
	int padBytes = 128-4-header.length();
	append(header, stream);
	for (int i = 1; i <= padBytes; i++){
		appendByte(0, stream);
	}
	appendByte(1, stream); appendByte(0, stream);
	append("MI", stream);
}
}
