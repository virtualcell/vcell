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
/**
 * Insert the type's description here.
 * Creation date: (8/3/2005 12:16:18 PM)
 * @author: Jim Schaff
 */
public interface ReferenceData extends org.vcell.util.Matchable {
/**
 * Insert the method's description here.
 * Creation date: (8/5/2005 11:49:26 AM)
 * @return int
 * @param colName java.lang.String
 */
int findColumn(String colName);


/**
 * Insert the method's description here.
 * Creation date: (8/5/2005 11:43:03 AM)
 * @return double
 * @param columnIndex int
 */
double[] getColumnData(int columnIndex);


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:17:12 PM)
 * @return java.lang.String[]
 */
String[] getColumnNames();


/**
 * Insert the method's description here.
 * Creation date: (8/4/2005 2:59:07 PM)
 * @return double[]
 */
double[] getColumnWeights();


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:17:36 PM)
 * @return int
 */
int getNumColumns();


/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:17:36 PM)
 * @return int
 */
int getNumRows();

int getDataSize();

/**
 * Insert the method's description here.
 * Creation date: (8/3/2005 12:18:02 PM)
 * @return double[]
 * @param rowIndex int
 */
double[] getRowData(int rowIndex);
}
