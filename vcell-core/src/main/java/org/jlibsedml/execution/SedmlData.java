package org.jlibsedml.execution;

 /**
  * Non-API Implementation of {@link IProcessedSedMLSimulationResults}
  * Because this class is non-API we don't have exhaustive arg checking etc.,
  * @author radams
  *
  */
 class SedmlData implements IProcessedSedMLSimulationResults {

	private double [][] _data;
	private String [] _headers;
	
	SedmlData(double [][]data, String []headers) {
		_headers = new String[headers.length];
		System.arraycopy(headers, 0, _headers, 0, headers.length);
		_data = new double[data.length][];
		copyDataFromTo(data, _data);
		
	}
	
	public String[] getColumnHeaders() {
		String[] rc = new String[_headers.length];
		System.arraycopy(_headers, 0, rc, 0, _headers.length);
		return rc;
	}

	public double[][] getData() {
		double[][] copy = new double[_data.length][];
		copyDataFromTo(_data, copy);
		return copy;
		
	}

	private void copyDataFromTo(double[][] data2, double[][] copy) {
		int i = 0;
		for (double[] row : data2) {
			double[] copyRow = new double[row.length];
			System.arraycopy(row, 0, copyRow, 0, row.length);

			copy[i++] = copyRow;
		}
		
	}


	public int getNumColumns() {
		return _headers.length;
	}

	public int getNumDataRows() {
		return _data.length;
	}

	public Double [] getDataByColumnId(String colID) {
		int colInd = getIndexByColumnID(colID);
		if(colInd == -1){
			return null;
		}
		Double [] rc = new Double[_data.length];
		for (int i=0; i< _data.length;i++){
			rc[i]=_data[i][colInd];
		}
		return rc;
	}
	
	public int getIndexByColumnID(String colID){
		int colInd=-1;
		for (int i =0; i< _headers.length;i++){
			if(_headers[i].equals(colID)){
				colInd=i;
			}
		}
		return colInd;
	}

    public Double[] getDataByColumnIndex(int index) {
        Double [] rc = new Double[_data.length];
        for (int i=0; i< _data.length;i++){
            rc[i]=_data[i][index];
        }
        return rc;
    }

}
