/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.bionetgen;
import org.vcell.util.gui.DefaultListModelCivilized;

import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:07:05 PM)
 * @author: Jim Schaff
 */
public class BNGDataPlotListModel extends DefaultListModelCivilized implements java.beans.PropertyChangeListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ODESolverResultSet fieldOdeSolverResultSet = null;

/**
 * MultisourcePlotListModel constructor comment.
 */
public BNGDataPlotListModel() {
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
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("odeSolverResultSet")){
		refreshAll();
		fireIntervalAdded(this,0,getSize()-1);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:18:09 PM)
 */
private void refreshAll() {
//
// get list of objects (data names)
//
if (fieldOdeSolverResultSet == null) {
	return;
}
	ColumnDescription[] columnDescriptions = fieldOdeSolverResultSet.getColumnDescriptions(); 
	int numColumns = columnDescriptions.length;
	String[] columnNames = new String[numColumns-1];
	int i = 0;
	for (int j = 0; j < columnDescriptions.length; j++){
		if (columnDescriptions[j].getName().equals("t")){
			continue;
		}
		columnNames[i++] = columnDescriptions[j].getName();
	}
	setContents(columnNames);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

/**
 * Sets the dataSource property (cbit.vcell.modelopt.gui.DataSource) value.
 * @param dataSource The new value for the property.
 * @see #getDataSource
 */
public void setOdeSolverResultSet(ODESolverResultSet odeSolverResultSet) {
	ODESolverResultSet oldValue = fieldOdeSolverResultSet;
	fieldOdeSolverResultSet = odeSolverResultSet;
	firePropertyChange("odeSolverResultSet", oldValue, odeSolverResultSet);
}
}
