package cbit.vcell.opt;

import org.vcell.util.Matchable;

/**
 * Insert the type's description here.
 * Creation date: (8/3/2005 12:16:18 PM)
 * @author: Jim Schaff
 */
public interface ReferenceData extends Matchable {

int findColumn(String colName);

/**
 * returns an array of the length of number of rows for data
 */
double[] getDataByColumn(int columnIndex);

/**
 * returns an array of the length of number of columns for Data
 */
double[] getDataByRow(int rowIndex);

/**
 * returns an array of column names
 */
String[] getColumnNames();

/*
 * returns a Weights object
 */
Weights getWeights();
/**
 * It's a legacy method. 
 * returns an array of the length of number of columns
 */
double[] getColumnWeights();

/**
 * returns number of columns
 */
int getNumDataColumns();


/**
 * returns number of rows 
 */
int getNumDataRows();

/**
 * returns the size of data
 */
int getDataSize();

}