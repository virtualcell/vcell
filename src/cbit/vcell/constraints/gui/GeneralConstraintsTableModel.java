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

import cbit.gui.ScopedExpression;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.GeneralConstraint;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class GeneralConstraintsTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 4;
	private final int COLUMN_EXPRESSION = 0;
	private final int COLUMN_TYPE = 1;
	private final int COLUMN_DESCRIPTION = 2;
	private final int COLUMN_INCLUDED = 3;
	private String LABELS[] = { "expression", "type", "description", "included" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ConstraintContainerImpl fieldConstraintContainerImpl = null;
	private JTable ownerTable = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public GeneralConstraintsTableModel(JTable table) {
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
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
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
		case COLUMN_EXPRESSION:{
			return cbit.gui.ScopedExpression.class;
		}
		case COLUMN_TYPE:{
			return String.class;
		}
		case COLUMN_DESCRIPTION:{
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
		return getConstraintContainerImpl().getGeneralConstraints().length;
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		if (row<0 || row>=getRowCount()){
			throw new RuntimeException("GeneralConstraintsTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		if (col<0 || col>=NUM_COLUMNS){
			throw new RuntimeException("GemeralConstraintsTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
		}
		GeneralConstraint constraint = getConstraintContainerImpl().getGeneralConstraints(row);
		switch (col){
			case COLUMN_EXPRESSION:{
				return new ScopedExpression(constraint.getExpression(),null);
			}
			case COLUMN_TYPE:{
				return constraint.getTypeName();
			}
			case COLUMN_DESCRIPTION:{
				return constraint.getDescription();
			}
			case COLUMN_INCLUDED:{
				return new Boolean(getConstraintContainerImpl().getActive(constraint));
			}
			default:{
				return null;
			}
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		return null;
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
	}else if (columnIndex == COLUMN_EXPRESSION){
		return true;
	}else if (columnIndex == COLUMN_TYPE){
		return false;
	}else if (columnIndex == COLUMN_DESCRIPTION){
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
	if (evt.getSource() == getConstraintContainerImpl() || evt.getSource() instanceof GeneralConstraint) {
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
 * Sets the constraintContainerImpl property (cbit.vcell.constraints.ConstraintContainerImpl) value.
 * @param constraintContainerImpl The new value for the property.
 * @see #getConstraintContainerImpl
 */
public void setConstraintContainerImpl(ConstraintContainerImpl constraintContainerImpl) {
	ConstraintContainerImpl oldValue = fieldConstraintContainerImpl;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
		GeneralConstraint oldConstraints[] = oldValue.getGeneralConstraints();
		for (int i = 0; i < oldConstraints.length; i++){
			oldConstraints[i].removePropertyChangeListener(this);
		}
	}
	fieldConstraintContainerImpl = constraintContainerImpl;
	firePropertyChange("constraintContainerImpl", oldValue, constraintContainerImpl);
	if (constraintContainerImpl!=null){
		constraintContainerImpl.addPropertyChangeListener(this);
		GeneralConstraint newConstraints[] = constraintContainerImpl.getGeneralConstraints();
		for (int i = 0; i < newConstraints.length; i++){
			newConstraints[i].addPropertyChangeListener(this);
		}
	}
	
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
System.out.println("GeneralConstraintsTableModel().setValueAt("+aValue+","+rowIndex+","+columnIndex+")");
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("GeneralConstraintsTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("GeneralConstraintsTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	GeneralConstraint generalConstraint = getConstraintContainerImpl().getGeneralConstraints(rowIndex);
	try {
		switch (columnIndex){
			case COLUMN_EXPRESSION:{
				try {
					if (aValue instanceof ScopedExpression){
//						Expression exp = ((ScopedExpression)aValue).getExpression();
//						generalConstraint.setExpression(exp);
						throw new RuntimeException("unexpected value type ScopedExpression");
					}else if (aValue instanceof String) {
						String newExpressionString = (String)aValue;
						generalConstraint.setExpression(new Expression(newExpressionString));
					}
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					DialogUtils.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_DESCRIPTION:{
				if (aValue instanceof String){
					generalConstraint.setDescription((String)aValue);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}else{
					System.out.println("ConstraintsTableModel.setValueAt(), unsupported type "+aValue.getClass().getName()+" for COLUMN_DESCRIPTION");
				}
				break;
			}
			case COLUMN_INCLUDED:{
				if (aValue instanceof Boolean){
					getConstraintContainerImpl().setActive(generalConstraint,((Boolean)aValue).booleanValue());
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
