/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints.gui;
import cbit.vcell.constraints.ConstraintSolver;
import cbit.vcell.constraints.SimpleBounds;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ConstraintSolverTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 3;
	private final int COLUMN_NAME = 0;
	private final int COLUMN_BASELINE = 1;
	private final int COLUMN_NARROWED = 2;
	private String LABELS[] = { "Name", "Baseline", "Narrowed" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ConstraintSolver fieldConstraintSolver = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public ConstraintSolverTableModel() {
	super();
	addPropertyChangeListener(this);
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
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
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
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	switch (column){
		case COLUMN_NAME:{
			return String.class;
		}
		case COLUMN_BASELINE:{
			return net.sourceforge.interval.ia_math.RealInterval.class;
		}
		case COLUMN_NARROWED:{
			return net.sourceforge.interval.ia_math.RealInterval.class;
		}
		default:{
			return Object.class;
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
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("ConstraintSolverTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}


/**
 * Gets the constraintContainerImpl property (cbit.vcell.constraints.ConstraintContainerImpl) value.
 * @return The constraintContainerImpl property value.
 * @see #setConstraintContainerImpl
 */
public ConstraintSolver getConstraintSolver() {
	return fieldConstraintSolver;
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
	if (getConstraintSolver()==null){
		return 0;
	}else{
		return getConstraintSolver().getSymbols().length;
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("ConstraintsTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ConstraintsTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.parser.SymbolTableEntry ste = getConstraintSolver().getSymbolTableEntries()[row];
	switch (col){
		case COLUMN_NAME:{
			return ste.getName();
		}
		case COLUMN_BASELINE:{
			return getConstraintSolver().getConstraintContainerImpl().getBounds(ste.getName());
		}
		case COLUMN_NARROWED:{
			return getConstraintSolver().getIntervals(ste.getIndex());
		}
		default:{
			return null;
		}
	}
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
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
	if (columnIndex == COLUMN_NAME){
		return false;
	}else if (columnIndex == COLUMN_BASELINE){
		return false;
	}else if (columnIndex == COLUMN_NARROWED){
		return false;
	}else{
		return false;
	}
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("constraintSolver")) {
		fireTableDataChanged();
	}
	if (evt.getSource() == getConstraintSolver()) {
		fireTableDataChanged();
	}
	if (evt.getSource() == getConstraintSolver().getConstraintContainerImpl()){
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof SimpleBounds){
		fireTableDataChanged();
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
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Sets the constraintContainerImpl property (cbit.vcell.constraints.ConstraintContainerImpl) value.
 * @param constraintContainerImpl The new value for the property.
 * @see #getConstraintContainerImpl
 */
public void setConstraintSolver(ConstraintSolver constraintSolver) {
	ConstraintSolver oldValue = fieldConstraintSolver;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
		if (oldValue.getConstraintContainerImpl()!=null){
			oldValue.getConstraintContainerImpl().removePropertyChangeListener(this);
			SimpleBounds oldSimpleBounds[] = oldValue.getConstraintContainerImpl().getSimpleBounds();
			for (int i = 0; i < oldSimpleBounds.length; i++){
				oldSimpleBounds[i].removePropertyChangeListener(this);
			}
		}
	}
	fieldConstraintSolver = constraintSolver;
	firePropertyChange("constraintSolver", oldValue, constraintSolver);
	if (constraintSolver!=null){
		constraintSolver.addPropertyChangeListener(this);
		if (constraintSolver.getConstraintContainerImpl()!=null){
			constraintSolver.getConstraintContainerImpl().addPropertyChangeListener(this);
			SimpleBounds newSimpleBounds[] = constraintSolver.getConstraintContainerImpl().getSimpleBounds();
			for (int i = 0; i < newSimpleBounds.length; i++){
				newSimpleBounds[i].removePropertyChangeListener(this);
			}
		}
	}
	
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ParameterTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return;
}
}
