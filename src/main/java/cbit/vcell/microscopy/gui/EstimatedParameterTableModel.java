/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.Component;
import java.util.Vector;

import cbit.gui.ScopedExpression;
import cbit.vcell.microscopy.EstimatedParameter;
import cbit.vcell.parser.ExpressionException;


@SuppressWarnings("serial")
public class EstimatedParameterTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {

	private final int NUM_COLUMNS = 5;
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_DESCRIPTION = 1;
	public final static int COLUMN_VALUE = 2;
	public final static int COLUMN_EXPRESSION = 3;
	public final static int COLUMN_UNITS = 4;
	private String LABELS[] = { "Name", "Description", "Value", "Expression", "Units" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Vector<EstimatedParameter> estParams = new Vector<EstimatedParameter>();
	/**
 * ReactionSpecsTableModel constructor comment.
 */
public EstimatedParameterTableModel(Component errorDialogParent) {
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
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_NAME:{
			return String.class;
		}
		case COLUMN_DESCRIPTION:{
			return String.class;
		}
		case COLUMN_VALUE: {
			return Double.class;
		}
		case COLUMN_EXPRESSION:{
			return ScopedExpression.class;
		}
		case COLUMN_UNITS:{
			return String.class;
		}
		
		default:{
			return Object.class;
		}
	}
}

public int getColumnCount() {
	return NUM_COLUMNS;
}

public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}

public Vector<EstimatedParameter> getEstimatedParammeters() {
	return estParams;
}
/**
 * getValueAt method comment.
 */
private EstimatedParameter getParameter(int row) {
	if (estParams == null || estParams.size()<1 ||
		row<0 || row>=getRowCount())
	{
		throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	return estParams.elementAt(row);
}

protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}

public int getRowCount() {
	if (estParams == null || estParams.size()<1){
		return 0;
	}else{
		return estParams.size();
	}
}

public Object getValueAt(int row, int col) {
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	EstimatedParameter parameter = getParameter(row);
	switch (col){
		case COLUMN_NAME:{
			return parameter.getName();
		}
		case COLUMN_DESCRIPTION:{
			return parameter.getDescrition();
		}
		case COLUMN_VALUE:{
			return parameter.getValue();
		}
		case COLUMN_EXPRESSION:{
			ScopedExpression exp = null;
			if(parameter != null && parameter.getExpression() != null)
			{
				try {
					exp = new ScopedExpression(parameter.getExpression(), null);
					return exp;
				} catch (ExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				return null; 
			}
		}
		case COLUMN_UNITS:{
			if (parameter.getUnitDefinition() != null){
				return parameter.getUnitDefinition().getSymbol();
			}else{
				return null;
			}
		}
		default:{
			return null;
		}
	}
}

public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}

public boolean isCellEditable(int rowIndex, int columnIndex) {
	return false;
}

public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("EstimatedParameters"))
	{
		fireTableDataChanged();
	}
}

public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

@SuppressWarnings("unchecked")
public void setEstimatedParameters(EstimatedParameter[] arg_estParams) {
	Vector<EstimatedParameter> oldEstParams = (Vector<EstimatedParameter>)estParams.clone(); 
	estParams.removeAllElements();
	for(int i=0; i<arg_estParams.length; i++)
	{
		if(arg_estParams[i] != null) //don't display those are null(not calculated yet)
		{
			estParams.addElement(arg_estParams[i]);
		}
	}
	
	firePropertyChange("EstimatedParameters", oldEstParams, estParams);
}
//Not used so far, the table is not editable.
public void setValueAt(Object aValue, int row, int col) 
{
	
}
}
