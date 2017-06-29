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
import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.SimpleBounds;
import net.sourceforge.interval.ia_math.RealInterval;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class SimpleBoundsTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 6;
	private final int COLUMN_VARNAME = 0;
	private final int COLUMN_TYPE = 1;
	private final int COLUMN_DESCRIPTION = 2;
	private final int COLUMN_LOWERBOUND = 3;
	private final int COLUMN_UPPERBOUND = 4;
	private final int COLUMN_INCLUDED = 5;
	private String LABELS[] = { "name", "type", "description", "lower bound", "upper bound", "included" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ConstraintContainerImpl fieldConstraintContainerImpl = null;
	private JTable ownerTable = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public SimpleBoundsTableModel(JTable table) {
	super();
	ownerTable = table;
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
public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_VARNAME:{
			return String.class;
		}
		case COLUMN_TYPE:{
			return String.class;
		}
		case COLUMN_DESCRIPTION:{
			return String.class;
		}
		case COLUMN_LOWERBOUND:{
			return String.class;
		}
		case COLUMN_UPPERBOUND:{
			return String.class;
		}
		case COLUMN_INCLUDED:{
			return Boolean.class;
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
		throw new RuntimeException("ParameterTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}


/**
 * Gets the constraintContainerImpl property (cbit.vcell.constraints.ConstraintContainerImpl) value.
 * @return The constraintContainerImpl property value.
 * @see #setConstraintContainerImpl
 */
public ConstraintContainerImpl getConstraintContainerImpl() {
	return fieldConstraintContainerImpl;
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
	if (getConstraintContainerImpl()==null){
		return 0;
	}else{
		return getConstraintContainerImpl().getSimpleBounds().length;
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
	SimpleBounds simpleBound = getConstraintContainerImpl().getSimpleBounds(row);
	switch (col){
		case COLUMN_VARNAME:{
			return simpleBound.getIdentifier();
		}
		case COLUMN_TYPE:{
			return simpleBound.getTypeName();
		}
		case COLUMN_DESCRIPTION:{
			return simpleBound.getDescription();
		}
		case COLUMN_LOWERBOUND:{
			return Double.toString(simpleBound.getBounds().lo());
		}
		case COLUMN_UPPERBOUND:{
			return Double.toString(simpleBound.getBounds().hi());
		}
		case COLUMN_INCLUDED:{
			return new Boolean(getConstraintContainerImpl().getActive(simpleBound));
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
	if (columnIndex == COLUMN_INCLUDED){
		return true;
	}else if (columnIndex == COLUMN_TYPE){
		return false;
	}else if (columnIndex == COLUMN_DESCRIPTION){
		return true;
	}else if (columnIndex == COLUMN_LOWERBOUND){
		return true;
	}else if (columnIndex == COLUMN_UPPERBOUND){
		return true;
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
	if (evt.getSource() == this && evt.getPropertyName().equals("constraintContainerImpl")) {
		fireTableDataChanged();
	}
	if (evt.getSource() == getConstraintContainerImpl() || evt.getSource() instanceof SimpleBounds) {
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
public void setConstraintContainerImpl(ConstraintContainerImpl constraintContainerImpl) {
	ConstraintContainerImpl oldValue = fieldConstraintContainerImpl;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
		SimpleBounds oldBounds[] = oldValue.getSimpleBounds();
		for (int i = 0; i < oldBounds.length; i++){
			oldBounds[i].removePropertyChangeListener(this);
		}
	}
	fieldConstraintContainerImpl = constraintContainerImpl;
	firePropertyChange("constraintContainerImpl", oldValue, constraintContainerImpl);
	if (constraintContainerImpl!=null){
		constraintContainerImpl.addPropertyChangeListener(this);
		SimpleBounds newBounds[] = constraintContainerImpl.getSimpleBounds();
		for (int i = 0; i < newBounds.length; i++){
			newBounds[i].addPropertyChangeListener(this);
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
	SimpleBounds simpleBound = getConstraintContainerImpl().getSimpleBounds(rowIndex);
	try {
		switch (columnIndex){
			case COLUMN_LOWERBOUND:{
				if (aValue instanceof String){
					String value = (String)aValue;
					try {
						simpleBound.setBounds(new RealInterval(Double.parseDouble(value),simpleBound.getBounds().hi()));
					}catch (NumberFormatException e){
						//
						// now try special values (-Infinity,Infinity)
						//
						if (value.equals(Double.toString(Double.NEGATIVE_INFINITY))){
							simpleBound.setBounds(new RealInterval(Double.NEGATIVE_INFINITY,simpleBound.getBounds().hi()));
						}else if (value.equals(Double.toString(Double.POSITIVE_INFINITY))){
							simpleBound.setBounds(new RealInterval(Double.POSITIVE_INFINITY,simpleBound.getBounds().hi()));
						}else{
							e.printStackTrace(System.out);
							DialogUtils.showErrorDialog(ownerTable, "Number format error:\n"+e.getMessage());
						}
					}
					fireTableRowsUpdated(rowIndex,rowIndex);
				}
				break;
			}
			case COLUMN_UPPERBOUND:{
				if (aValue instanceof String){
					String value = (String)aValue;
					try {
						simpleBound.setBounds(new RealInterval(simpleBound.getBounds().lo(),Double.parseDouble(value)));
					}catch (NumberFormatException e){
						//
						// now try special values (-Infinity,Infinity)
						//
						if (value.equals(Double.toString(Double.NEGATIVE_INFINITY))){
							simpleBound.setBounds(new RealInterval(simpleBound.getBounds().hi(),Double.NEGATIVE_INFINITY));
						}else if (value.equals(Double.toString(Double.POSITIVE_INFINITY))){
							simpleBound.setBounds(new RealInterval(simpleBound.getBounds().hi(),Double.POSITIVE_INFINITY));
						}else{
							e.printStackTrace(System.out);
							DialogUtils.showErrorDialog(ownerTable, "Number format error:\n"+e.getMessage());
						}
					}
					fireTableRowsUpdated(rowIndex,rowIndex);
				}
				break;
			}
			case COLUMN_DESCRIPTION:{
				if (aValue instanceof String){
					simpleBound.setDescription((String)aValue);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}else{
					System.out.println("ConstraintsTableModel.setValueAt(), unsupported type "+aValue.getClass().getName()+" for COLUMN_DESCRIPTION");
				}
				break;
			}
			case COLUMN_INCLUDED:{
				if (aValue instanceof Boolean){
					getConstraintContainerImpl().setActive(simpleBound,((Boolean)aValue).booleanValue());
					fireTableRowsUpdated(rowIndex,rowIndex);
				}else{
					System.out.println("ConstraintsTableModel.setValueAt(), unsupported type "+aValue.getClass().getName()+" for COLUMN_INCLUDED");
				}
				break;
			}
		}
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
	}
}
}
