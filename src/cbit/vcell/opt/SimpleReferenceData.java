package cbit.vcell.opt;
import cbit.vcell.server.DataAccessException;
/**
 * Insert the type's description here.
 * Creation date: (8/3/2005 3:07:17 PM)
 * @author: Jim Schaff
 */
public class SimpleReferenceData implements ReferenceData, java.io.Serializable {
	private String dataNames[] = null;
	private double[] columnWeights = null;
	private java.util.Vector<double[]> rowData = new java.util.Vector<double[]>();

	/**
	 * SimpleConstraintData constructor comment.
	 */
	public SimpleReferenceData(String[] argColumnNames, double[] argColumnWeights, java.util.Vector<double[]> argRowData) {
		super();
		this.dataNames = argColumnNames;
		this.columnWeights = argColumnWeights;

		for (int i = 0; i < argRowData.size(); i++){
			if ((argRowData.elementAt(i) instanceof double[])){
				double[] rowData = (double[])argRowData.elementAt(i);
				if (rowData.length!=argColumnNames.length){
					throw new IllegalArgumentException("rowData not same size as number of columns");
				}
			}else{
				throw new IllegalArgumentException("rowdata argument not of type double[] in SimpleReferenceData");
			}
		}

		this.rowData = argRowData;
	}

	/**
	 * SimpleConstraintData constructor comment.
	 */
	public SimpleReferenceData(String[] argColumnNames, double[] argColumnWeights, double[][] columnData) {
		super();
		this.dataNames = argColumnNames;
		this.columnWeights = argColumnWeights;

		for (int i = 0; i < columnData[0].length; i++){
			double[] row = new double[columnData.length];
			for (int j = 0; j < columnData.length; j++){
				row[j] = columnData[j][i];
			}
			rowData.add(row);
		}
	}


/**
 * SimpleConstraintData constructor comment.
 */
public SimpleReferenceData(ReferenceData argReferenceData) {
	super();
	//
	// make local copy of data
	//
	this.dataNames = (String[])argReferenceData.getColumnNames().clone();
	this.columnWeights = (double[])argReferenceData.getColumnWeights().clone();
	for (int i = 0; i < argReferenceData.getNumRows(); i++){
		this.rowData.add(argReferenceData.getRowData(i).clone());
	}
}


/**
 * SimpleConstraintData constructor comment.
 */
public SimpleReferenceData(cbit.vcell.util.RowColumnResultSet rowColumnResultSet, double[] argDataColumnWeights) {
	super();
	//
	// make local copy of data
	//
	cbit.vcell.util.ColumnDescription[] columnDescriptions = rowColumnResultSet.getDataColumnDescriptions();
	this.dataNames = new String[columnDescriptions.length];
	this.columnWeights = (double[])argDataColumnWeights.clone();
	if (dataNames.length != columnWeights.length) {
		throw new RuntimeException("Number of data columns should equal number of column weights");
	}
	for (int i = 0; i < dataNames.length; i++){
		this.dataNames[i] = columnDescriptions[i].getName();
	}
	for (int i = 0; i < rowColumnResultSet.getRowCount(); i++){
		this.rowData.add(rowColumnResultSet.getRow(i).clone());
	}
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof SimpleReferenceData){
		SimpleReferenceData srd = (SimpleReferenceData)obj;

		if (!org.vcell.util.Compare.isEqual(dataNames,srd.dataNames)){
			return false;
		}
		
		if (!org.vcell.util.Compare.isEqual(columnWeights,srd.columnWeights)){
			return false;
		}

		if (rowData.size()!=srd.rowData.size()){
			return false;
		}

		for (int i = 0; i < rowData.size(); i++){
			double[] thisData = (double[])rowData.get(i);
			double[] otherData = (double[])srd.rowData.get(i);
			if (!org.vcell.util.Compare.isEqual(thisData,otherData)){
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
public int findColumn(java.lang.String colName) {
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
public static SimpleReferenceData fromVCML(org.vcell.util.CommentStringTokenizer tokens) throws DataAccessException {
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
	java.util.Vector rowData = new java.util.Vector();		
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
public double[] getColumnData(int columnIndex) {
	//
	// bounds check
	//
	int numRows = getNumRows();
	int numCols = dataNames.length;
	if (columnIndex<0 || columnIndex>=numCols){
		throw new RuntimeException("columnIndex "+columnIndex+" out of bounds");
	}
	double[] colData = new double[numRows];
	for (int i = 0; i < numRows; i++){
		colData[i] = ((double[])rowData.elementAt(i))[columnIndex];
	}
	return colData;
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 3:07:17 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getColumnNames() {
	return dataNames;
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/2005 2:59:23 PM)
 * @return double[]
 */
public double[] getColumnWeights() {
	return this.columnWeights;
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


/**
 * Insert the method's description here.
 * Creation date: (5/2/2006 2:34:55 PM)
 * @return int
 */
public int getNumColumns() {
	return dataNames.length;
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 3:07:17 PM)
 * @return int
 */
public int getNumRows() {
	return rowData.size();
}


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 3:07:17 PM)
 * @return double[]
 * @param rowIndex int
 */
public double[] getRowData(int rowIndex) {
	return (double[])rowData.elementAt(rowIndex);
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
	for (int i = 0; i < numColumns; i++){
		if (i>0){
			buffer.append(" ");
		}
		buffer.append(columnWeights[i]);
	}
	buffer.append("\n");
	//
	// print data
	//
	for (int i = 0; i < numRows; i++){
		double row[] = (double[])rowData.get(i);
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

public int getDataSize() {	
	return 1;
}
}