/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gui;

import javax.swing.*;
import javax.swing.table.*;

import java.util.*;

import cbit.rmi.event.*;
import cbit.vcell.export.ExportStatus;
/**
 * Insert the type's description here.
 * Creation date: (4/4/2001 12:14:46 PM)
 * @author: Ion Moraru
 */
public class ExportMonitorTableModel extends AbstractTableModel {
	public static final String ID_COLUMN_NAME = "Job ID";
	public static final String FORMAT_COLUMN_NAME = "Format";
	public static final String PROGRESS_COLUMN_NAME = "Export Progress";
	public static final String STATUS_COLUMN_NAME = "Completed ?";
	public static final String DESTINATION_COLUMN_NAME = "File Location";
	public static final String RESULT_ID_COLUMN_NAME = "Simulation";
	private String[] columnNames = null;
	private Vector rows = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldMinRowHeight = 10;
/**
 * ExportMonitorTableModel constructor comment.
 */
public ExportMonitorTableModel() {
	super();
	columnNames = new String[] {ID_COLUMN_NAME, FORMAT_COLUMN_NAME, PROGRESS_COLUMN_NAME, STATUS_COLUMN_NAME, DESTINATION_COLUMN_NAME, RESULT_ID_COLUMN_NAME};
	rows = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 1:55:19 PM)
 * @return int
 * @param resultSetID java.lang.String
 * @param event cbit.rmi.event.ExportEvent
 */
public int addExportEvent(String resultSetID, ExportEvent event) {
	ExportStatus exportStatus = null;
	int i = 0;
	// check if we already monitor this job; if not, add it to the list
	for (; i < getRowCount(); i++){
		ExportStatus row = (ExportStatus)rows.elementAt(i);
		if (row.getJobID().longValue() == event.getJobID()) {
			exportStatus = row;
			break;
		}	
	}
	if (exportStatus == null) {
		exportStatus = new ExportStatus(event.getJobID(), resultSetID);
		rows.addElement(exportStatus);
		fireTableRowsInserted(i, i);
	}
	// now update the cells
	exportStatus.setFormat(event.getFormat());
	exportStatus.setDestination(event.getLocation());
	switch (event.getEventTypeID()) {
		case cbit.rmi.event.ExportEvent.EXPORT_START: {
			exportStatus.getProgressBar().setString("Starting export...");
			break;
		}
		case cbit.rmi.event.ExportEvent.EXPORT_PROGRESS: {
			exportStatus.getProgressBar().setValue((int)(event.getProgress().doubleValue() * 100));
			exportStatus.getProgressBar().setString(null);
			break;
		}
		case cbit.rmi.event.ExportEvent.EXPORT_ASSEMBLING: {
			exportStatus.getProgressBar().setString("Building export file...");
			break;
		}
		case cbit.rmi.event.ExportEvent.EXPORT_FAILURE: {
			exportStatus.getProgressBar().setString("Export failed!");
			break;
		}
		case cbit.rmi.event.ExportEvent.EXPORT_COMPLETE: {
			exportStatus.getProgressBar().setValue(100);
			exportStatus.getProgressBar().setString("Complete");
			exportStatus.setComplete(Boolean.TRUE);
			break;
		}
	}
	setMinRowHeight((int)Math.ceil(exportStatus.getProgressBar().getMinimumSize().getHeight()));
	fireTableRowsUpdated(i, i);
	return i;
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
 * Creation date: (4/4/2001 1:00:18 PM)
 * @return java.lang.Class
 * @param columnIndex int
 */
public Class getColumnClass(int columnIndex) {
	switch (columnIndex) {
		case 0: return Long.class;
		case 1: return String.class;
		case 2: return JProgressBar.class;
		case 3: return Boolean.class;
		case 4: return String.class;
		case 5: return String.class;
		default: return Object.class;
	}
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return columnNames.length;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 12:38:31 PM)
 * @return java.lang.String
 * @param column int
 */
public String getColumnName(int column) {
	try {
		return columnNames[column];
	} catch (Throwable exc) {
		System.out.println("WARNING - no such column index: " + column);
		exc.printStackTrace(System.out);
		return null;
	}
}
/**
 * Gets the minRowHeight property (int) value.
 * @return The minRowHeight property value.
 * @see #setMinRowHeight
 */
public int getMinRowHeight() {
	return fieldMinRowHeight;
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
	return rows.size();
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int column) {
	try {
		ExportStatus es = (ExportStatus)rows.elementAt(row);
		String columnName = getColumnName(column);
		if (columnName.equals(ID_COLUMN_NAME)) {
			return es.getJobID();
		} else if (columnName.equals(FORMAT_COLUMN_NAME)) {
			return es.getFormat();
		} else if (columnName.equals(STATUS_COLUMN_NAME)) {
			return es.getComplete();
		} else if (columnName.equals(PROGRESS_COLUMN_NAME)) {
			return es.getProgressBar();
		} else if (columnName.equals(DESTINATION_COLUMN_NAME)) {
			return es.getDestination();
		} else if (columnName.equals(RESULT_ID_COLUMN_NAME)) {
			return es.getResultSetID();
		} else {
			throw new Exception();
		}
	} catch (Throwable exc) {
		System.out.println("WARNING - no such cell: " + row + ", " + column);
		exc.printStackTrace(System.out);
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
 * Sets the minRowHeight property (int) value.
 * @param minRowHeight The new value for the property.
 * @see #getMinRowHeight
 */
private void setMinRowHeight(int minRowHeight) {
	int oldValue = fieldMinRowHeight;
	fieldMinRowHeight = minRowHeight;
	firePropertyChange("minRowHeight", new Integer(oldValue), new Integer(minRowHeight));
}
}
