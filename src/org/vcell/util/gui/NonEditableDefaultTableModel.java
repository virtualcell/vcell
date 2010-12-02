package org.vcell.util.gui;

import java.util.Vector;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
/**
 * Insert the type's description here.
 * Creation date: (8/16/00 12:49:31 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class NonEditableDefaultTableModel extends javax.swing.table.DefaultTableModel {
/**
 * NonEditableDefaultTableModel constructor comment.
 */
public NonEditableDefaultTableModel() {
	super();
}
/**
 * NonEditableDefaultTableModel constructor comment.
 * @param data java.lang.Object[][]
 * @param columnNames java.lang.Object[]
 */
public NonEditableDefaultTableModel(java.lang.Object[][] data, java.lang.Object[] columnNames) {
	super(data, columnNames);
}
/**
 * NonEditableDefaultTableModel constructor comment.
 * @param columnNames java.lang.Object[]
 * @param numRows int
 */
public NonEditableDefaultTableModel(java.lang.Object[] columnNames, int numRows) {
	super(columnNames, numRows);
}
/**
 * NonEditableDefaultTableModel constructor comment.
 * @param numRows int
 * @param numColumns int
 */
public NonEditableDefaultTableModel(int numRows, int numColumns) {
	super(numRows, numColumns);
}
/**
 * NonEditableDefaultTableModel constructor comment.
 * @param columnNames Vector
 * @param numRows int
 */
public NonEditableDefaultTableModel(Vector<String> columnNames, int numRows) {
	super(columnNames, numRows);
}
/**
 * NonEditableDefaultTableModel constructor comment.
 * @param data Vector
 * @param columnNames Vector
 */
public NonEditableDefaultTableModel(Vector<String> data, Vector<String> columnNames) {
	super(data, columnNames);
}
/**
 * Insert the method's description here.
 * Creation date: (8/16/00 12:50:15 PM)
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) {
	return false;
}
}
