/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest.gui;

import cbit.vcell.parser.Expression;
import cbit.vcell.numericstest.*;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class SolutionTemplateTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	public static final int NUM_COLUMNS = 3;
	public static final int COLUMN_VARIABLE = 0;
	public static final int COLUMN_SUBDOMAIN = 1;
	public static final int COLUMN_EXPRESSION = 2;
	private String LABELS[] = { "Variable", "Subdomain", "Expression" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.numericstest.ConstructedSolutionTemplate fieldConstructedSolutionTemplate = null;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public SolutionTemplateTableModel() {
	super();
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
		case COLUMN_VARIABLE:{
			return String.class;
		}
		case COLUMN_SUBDOMAIN:{
			return String.class;
		}
		case COLUMN_EXPRESSION:{
			return Expression.class;
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
public String getColumnName(int column) {
	try {
		return LABELS[column];
	} catch (Throwable exc) {
		System.out.println("WARNING - no such column index: " + column);
		exc.printStackTrace(System.out);
		return null;
	}
}
/**
 * Gets the constructedSolutionTemplate property (cbit.vcell.numericstest.ConstructedSolutionTemplate) value.
 * @return The constructedSolutionTemplate property value.
 * @see #setConstructedSolutionTemplate
 */
public cbit.vcell.numericstest.ConstructedSolutionTemplate getConstructedSolutionTemplate() {
	return fieldConstructedSolutionTemplate;
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
	if (getConstructedSolutionTemplate()==null){
		return 0;
	}else{
		return getConstructedSolutionTemplate().getSolutionTemplates().length;
	}
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("SolutionTemplateTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("SolutionTemplateTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	SolutionTemplate solnTemplates[] = getConstructedSolutionTemplate().getSolutionTemplates();
	switch (col){
		case COLUMN_VARIABLE:{
			return solnTemplates[row].getVarName();
		}
		case COLUMN_SUBDOMAIN:{
			return solnTemplates[row].getDomainName();
		}
		case COLUMN_EXPRESSION:{
			return solnTemplates[row].getTemplateExpression().infix();
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
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("SolutionTemplateTableModel.getValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("SolutionTemplateTableModel.getValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	switch (columnIndex){
		case COLUMN_VARIABLE:{
			return false;
		}
		case COLUMN_SUBDOMAIN:{
			return false;
		}
		case COLUMN_EXPRESSION:{
			// editing in place is impractical for expression
			// this way, we can pop-up a custom dialog to enter a new value (GUI has to take care of this)
			return true;
		}
		default:{
			return false;
		}
	}
}
/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	/*if (evt.getSource() instanceof cbit.vcell.mapping.ReactionContext
		&& evt.getPropertyName().equals("speciesContextSpecs")) {
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.model.SpeciesContext
		&& evt.getPropertyName().equals("name")) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof cbit.vcell.mapping.SpeciesContextSpec) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}*/
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
 * Sets the constructedSolutionTemplate property (cbit.vcell.numericstest.ConstructedSolutionTemplate) value.
 * @param constructedSolutionTemplate The new value for the property.
 * @see #getConstructedSolutionTemplate
 */
public void setConstructedSolutionTemplate(cbit.vcell.numericstest.ConstructedSolutionTemplate constructedSolutionTemplate) {
	ConstructedSolutionTemplate oldValue = fieldConstructedSolutionTemplate;
	fieldConstructedSolutionTemplate = constructedSolutionTemplate;
	firePropertyChange("constructedSolutionTemplate", oldValue, constructedSolutionTemplate);
	fireTableDataChanged();
}
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("SolutionTemplateTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("SolutionTemplateTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	SolutionTemplate solnTemplate = getConstructedSolutionTemplate().getSolutionTemplates()[rowIndex];
	switch (columnIndex){
		case COLUMN_EXPRESSION:{
			try {
				if (aValue instanceof Expression){
					Expression exp = (Expression)aValue;
					solnTemplate.setTemplateExpression(exp);
				}else if (aValue instanceof String) {
					String newExpressionString = (String)aValue;
					solnTemplate.setTemplateExpression(new Expression(newExpressionString));
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (cbit.vcell.parser.ExpressionException e){
				e.printStackTrace(System.out);
				//
				// don't handle exception here, InitialConditionsPanel needs it.
				//
				throw new RuntimeException(e.getMessage());
			}
			break;
		}
	}
}
}
