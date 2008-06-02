package cbit.vcell.opt;

public class SpatialReferenceData implements ReferenceData, java.io.Serializable {
	private String variableNames[] = null;
	private int dataSize;
	private double[] variableWeights = null;
	private java.util.Vector<double[]> rowData = new java.util.Vector<double[]>();

	public SpatialReferenceData(String[] argVariableNames, double[] argVariableWeights, int argDataSize, java.util.Vector<double[]> argRowData) {
		super();
		this.variableNames = argVariableNames;
		dataSize = argDataSize;
		variableWeights = argVariableWeights;

		for (int i = 0; i < argRowData.size(); i++){
			double[] rowData = argRowData.elementAt(i);
			if (rowData.length != 1 +  (variableNames.length-1) * dataSize) {
				throw new IllegalArgumentException("rowData not same size as number of variableSize * dataSize + 1 (for t)");
			}
		}

		this.rowData = argRowData;
	}

public boolean compareEqual(cbit.util.Matchable obj) {
	if (obj instanceof SpatialReferenceData){
		SpatialReferenceData srd = (SpatialReferenceData)obj;

		if (!cbit.util.Compare.isEqual(variableNames, srd.variableNames)){
			return false;
		}
		
		if (!cbit.util.Compare.isEqual(variableWeights,srd.variableWeights)){
			return false;
		}

		if (rowData.size()!=srd.rowData.size()){
			return false;
		}

		for (int i = 0; i < rowData.size(); i++){
			double[] thisData = rowData.get(i);
			double[] otherData = srd.rowData.get(i);
			if (!cbit.util.Compare.isEqual(thisData,otherData)){
				return false;
			}
		}
		
		return true;
	}
	return false;
}

public int findVariable(java.lang.String varName) {
	for (int i = 0; i < variableNames.length; i++){
		if (variableNames[i].equals(varName)){
			return i;
		}
	}
	return -1;
}

public java.lang.String[] getVariableNames() {
	return variableNames;
}

public double[] getVariableWeights() {
	return this.variableWeights;
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

public int getNumRows() {
	return rowData.size();
}

public double[] getRowData(int rowIndex) {
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
	for (int i = 0; i < numVariables; i++){
		if (i>0){
			buffer.append(" ");
		}
		buffer.append(variableWeights[i]);
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
	return dataSize;
}


public int findColumn(String colName) {
	return findVariable(colName);
}


public double[] getColumnData(int columnIndex) {
	throw new RuntimeException("SpatialReferenceData doesn't support getColumeData(int columnIndex)");
}


public String[] getColumnNames() {
	return variableNames;
}


public double[] getColumnWeights() {
	return variableWeights;
}


public int getNumColumns() {
	return variableNames.length;
}

}