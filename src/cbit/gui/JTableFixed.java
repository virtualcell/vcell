package cbit.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.table.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2001 4:30:50 PM)
 * @author: Ion Moraru
 */

/*
 * Needed to fix nasty but persistent bug (still present in JRE 1.3.0) in implementation of bound
 * property "cellEditor" - setter method fires propertyChange event but inserts the wrong
 * string, namely "tableCellEditor", and therefore standard listener implementations ignore
 * the events !!
 */
public class JTableFixed extends JTable {
/**
 * JTableFixed constructor comment.
 */
public JTableFixed() {
	super();
}
/**
 * JTableFixed constructor comment.
 * @param rowData java.lang.Object[][]
 * @param columnNames java.lang.Object[]
 */
public JTableFixed(java.lang.Object[][] rowData, java.lang.Object[] columnNames) {
	super(rowData, columnNames);
}
/**
 * JTableFixed constructor comment.
 * @param numRows int
 * @param numColumns int
 */
public JTableFixed(int numRows, int numColumns) {
	super(numRows, numColumns);
}
/**
 * JTableFixed constructor comment.
 * @param rowData java.util.Vector
 * @param columnNames java.util.Vector
 */
public JTableFixed(java.util.Vector rowData, java.util.Vector columnNames) {
	super(rowData, columnNames);
}
/**
 * JTableFixed constructor comment.
 * @param dm javax.swing.table.TableModel
 */
public JTableFixed(javax.swing.table.TableModel dm) {
	super(dm);
}
/**
 * JTableFixed constructor comment.
 * @param dm javax.swing.table.TableModel
 * @param cm javax.swing.table.TableColumnModel
 */
public JTableFixed(javax.swing.table.TableModel dm, javax.swing.table.TableColumnModel cm) {
	super(dm, cm);
}
/**
 * JTableFixed constructor comment.
 * @param dm javax.swing.table.TableModel
 * @param cm javax.swing.table.TableColumnModel
 * @param sm javax.swing.ListSelectionModel
 */
public JTableFixed(javax.swing.table.TableModel dm, javax.swing.table.TableColumnModel cm, ListSelectionModel sm) {
	super(dm, cm, sm);
}
/*
 * Bug in super (see comments in class decalration); don't know if some stupid class in Sun's JRE actually
 * uses the wrong property name, so we fire both ways
 */
public void setCellEditor(TableCellEditor anEditor) {
	TableCellEditor oldEditor = getCellEditor();
		cellEditor = anEditor;
	firePropertyChange("tableCellEditor", oldEditor, anEditor);
	firePropertyChange("cellEditor", oldEditor, anEditor);
}
}
