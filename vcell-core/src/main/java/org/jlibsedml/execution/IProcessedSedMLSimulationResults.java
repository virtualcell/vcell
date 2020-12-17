package org.jlibsedml.execution;

/**
 * Specifies processed time-series simulation results following post-processing. 
 * @author radams
 *
 */
public interface IProcessedSedMLSimulationResults {
	
	/**
	 * First dimension is a row for each time step, second dimension is the column. Each column is the 
	 *  data for a specific model entity, or its processed data.
	 * Implementations should return a copy of the data generated for use by  clients.
	 * This should not return null.
	 * @return a <code>double[][]</code>of data.
	 */
	public double[][] getData();

	/**
	 * Gets an array of String column names. 
	 * @return A non-null <code>String []</code>.
	 */
	public String[] getColumnHeaders();
	
	/**
	 * Gets the  results for the specified column header
	 * @param colID A non-null column header
	 * @return A double[] or <code>null</code> if there is no data column with a header of
	 *    that title.
	 */
	public Double [] getDataByColumnId(String colID);
	
	/**
    * Gets the  results for the specified column index.
    * @param index A column index > 0
    * @return A double[] or <code>null</code> if there is no data column with that index.
    */
   public Double [] getDataByColumnIndex(int index);
	
	
	
	
	/**
	 * Gets the 0-based column index for the specified column header.
	 * @param colID A non-null column header.
	 * @return An int >=0 but < getNumColumns(), or -1 if if there is no data column with a header of
	 *    that title. 
	 */
	public int getIndexByColumnID(String colID);

	
	/**
	 * Getter for the number of columns in the returned data set.
	 * @return A non-negative <code>int</code> of the number of columns in this result set.
	 */
	public int getNumColumns();
	
	/**
	 * Gets the number of rows (e.g., time points) in the processed data.
	 * @return the number of rows
	 */
	public int getNumDataRows();

}
