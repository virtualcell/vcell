/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.namescope;

/**
 * Insert the type's description here.
 * Creation date: (4/13/2004 12:49:54 PM)
 * @author: Anuradha Lakshminarayana
 */
public class ModelNameScopedParameterTableModel extends javax.swing.table.AbstractTableModel {
	private final int NUM_COLUMNS = 4;
	private final int COLUMN_NAME = 0;
	private final int COLUMN_EXPRESSION = 1;
	private final int COLUMN_UNITS = 2;
	private final int COLUMN_CONSTANT_VAL = 3;
	private String LABELS[] = { "Parameter", "Expression", "Units", "Constant Value" };
	private java.util.Vector rowDataVector = new java.util.Vector();
	//
	// Later, add relevant types (eg, subvolume, reaction, whatever is applicable)
	//
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldColumnCount = 0;
	private int fieldRowCount = 0;
/**
 * NameScopedParameterTableModel constructor comment.
 */
public ModelNameScopedParameterTableModel() {
	super();
	clear();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * Insert the method's description here.
 * Creation date: (4/14/2004 4:53:25 PM)
 */
public void clear() {
	rowDataVector = new java.util.Vector();
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 *  Returns Object.class by default
 */
public Class getColumnClass(int columnIndex) {
	 switch (columnIndex) {
		 case COLUMN_NAME : {
		 	return String.class;
		 	// break;
		 }
		 case COLUMN_EXPRESSION : {
			 return cbit.vcell.parser.ScopedExpression.class;
			 // break;
		 }
		 case COLUMN_UNITS : {
			 return String.class;
			 // break;
		 }
		 case COLUMN_CONSTANT_VAL : {
			 return String.class;
			 // break;
		 }
		 default : {
			 return Object.class;				 
			 // break;
		 }
	 }
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return NUM_COLUMNS;
}
/**
 * Gets the columnName property (java.lang.String[]) value.
 * @return The columnName property value.
 */
public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return getColumnNames()[column];
}
/**
 * Gets the columnName property (java.lang.String[]) value.
 * @return The columnName property value.
 */
public java.lang.String[] getColumnNames() {
	return LABELS;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return rowDataVector.size();
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int rowIndex, int columnIndex) {
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("NameScopedParameterTableModel.getValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	if (rowIndex<0 || rowIndex>=rowDataVector.size()){
		throw new RuntimeException("NameScopedParameterTableModel.getValueAt(), row = "+rowIndex+" out of range ["+0+","+(rowDataVector.size()-1)+"]");
	}

	Object[] obj = (Object[])rowDataVector.get(rowIndex);
	return obj[columnIndex];

	
	//switch (col){
		//case COLUMN_NAME:{
			////return parameter Name();
			//return "Parameter Name";
		//}
		//case COLUMN_EXPRESSION:{
			//// return expression
			//return "Parameter Expression";
		//}
		//case COLUMN_UNITS:{
			//// return units
			//return "Parameter Units";
		//}
		//case COLUMN_CONSTANT_VAL:{
			//// return constant value
			//return "Constant Value";
		//}
		//default:{
			//return null;
		//}
	//}	
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:27:46 AM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	//
	// Add any additional code here depending on whether you want cell to be editable or not. ...
	// CHANGE code below depending on cell-editability
	//
	
	if (columnIndex == COLUMN_NAME){
		return true;
	}else if (columnIndex == COLUMN_EXPRESSION){
		return true;
	}else if (columnIndex == COLUMN_UNITS){
		return true;
	}else if (columnIndex == COLUMN_CONSTANT_VAL){
		return false;
	}else{
		return false;
	}
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0){
		throw new RuntimeException("NameScopedParameterTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex >= NUM_COLUMNS){
		throw new RuntimeException("NameScopedParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	Object[] obj = null;
	if (rowIndex < getRowCount()) {
		obj = (Object[])rowDataVector.get(rowIndex);
	} else {
		obj = new Object[getColumnCount()];
		rowDataVector.add(rowIndex, obj);
	}

	obj[columnIndex] = aValue;
	
	//switch (columnIndex){
		//case COLUMN_NAME:{
			//// FILL THIS IN LATER !!!
			//break;
		//}
		//case COLUMN_EXPRESSION:{
			//// FILL THIS IN LATER !!!
			//break;
		//}
		//case COLUMN_UNITS:{
			//// FILL THIS IN LATER !!!
			//break;
		//} 
		//case COLUMN_CONSTANT_VAL:{
			//// FILL THIS IN LATER !!!
			//break;
		//}	
	//}
}
}
