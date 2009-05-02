package org.vcell.util.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (8/16/00 12:49:31 PM)
 * @author: 
 */
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
 * @param columnNames java.util.Vector
 * @param numRows int
 */
public NonEditableDefaultTableModel(java.util.Vector columnNames, int numRows) {
	super(columnNames, numRows);
}
/**
 * NonEditableDefaultTableModel constructor comment.
 * @param data java.util.Vector
 * @param columnNames java.util.Vector
 */
public NonEditableDefaultTableModel(java.util.Vector data, java.util.Vector columnNames) {
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
