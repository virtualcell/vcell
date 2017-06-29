/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Level;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class GeometrySubVolumeTableModel extends VCellSortTableModel<SubVolume> implements java.beans.PropertyChangeListener {
	private final int COLUMN_NAME = 0;
	private final int COLUMN_VALUE = 1;
	private final static String LABELS[] = { "Name", "Value" };
	private Geometry fieldGeometry = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public GeometrySubVolumeTableModel(ScrollTable table) {
	super(table, LABELS);
	addPropertyChangeListener(this);
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_NAME:{
			return SubVolume.class;
		}
		case COLUMN_VALUE:{
			return ScopedExpression.class;
		}
		default:{
			return Object.class;
		}
	}
}

/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public Geometry getGeometry() {
	return fieldGeometry;
}

/**
 * getRowCount method comment.
 */
private void refreshData() {
	List<SubVolume> subVolumeList = null;
	if (getGeometry()!= null){
		subVolumeList = new ArrayList<SubVolume>();
		subVolumeList.addAll(Arrays.asList(getGeometry().getGeometrySpec().getSubVolumes()));
	}
	setData(subVolumeList);
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		SubVolume subVolume = getValueAt(row);
		switch (col){
			case COLUMN_NAME:{
				return subVolume;
			}
			case COLUMN_VALUE:{
				if (subVolume instanceof AnalyticSubVolume){
					return new ScopedExpression(((AnalyticSubVolume)subVolume).getExpression(), ReservedVariable.X.getNameScope(), true, true, 
							autoCompleteSymbolFilter);
				}
				if (subVolume instanceof CSGObject){
					return "Constructed Solid Geometry";
				} 
				return null;
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
 * Insert the method's description here.
 * Creation date: (2/24/01 12:27:46 AM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	if (columnIndex == COLUMN_NAME){
		return true;
	}
	if (columnIndex == COLUMN_VALUE){
		SubVolume subVolume = getValueAt(rowIndex);
		//
		// the "value" column is only editable if it is an expression for a AnalyticSubVolume
		//
		return (subVolume instanceof AnalyticSubVolume);
	}
	
	return false;	
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("geometry")) {
		refreshData();
	}
	if (evt.getSource() == getGeometry().getGeometrySpec() && evt.getPropertyName().equals("subVolumes")) {
		refreshData();
	}
	if (evt.getSource() instanceof SubVolume) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
}

/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(Geometry geometry) {
	if (fieldGeometry == geometry) {
		return;
	}
	Geometry oldValue = fieldGeometry;
	if (oldValue != null){
		oldValue.getGeometrySpec().removePropertyChangeListener(this);
		for (SubVolume sv : oldValue.getGeometrySpec().getSubVolumes()) {
			sv.removePropertyChangeListener(this);
		}
	}
	fieldGeometry = geometry;
	if (fieldGeometry != null){
		fieldGeometry.getGeometrySpec().addPropertyChangeListener(this);
		for (SubVolume sv : fieldGeometry.getGeometrySpec().getSubVolumes()) {
			sv.addPropertyChangeListener(this);
		}
		autoCompleteSymbolFilter = new AutoCompleteSymbolFilter() {
			public boolean accept(SymbolTableEntry ste) {
				int dimension = fieldGeometry.getDimension();
				if (ste.equals(ReservedVariable.X) || dimension > 1 && ste.equals(ReservedVariable.Y) || dimension > 2 && ste.equals(ReservedVariable.Z)) {
					return true;
				}
				return false;
			}
			public boolean acceptFunction(String funcName) {
				return true;
			}	   
		};
	}
	firePropertyChange("geometry", oldValue, fieldGeometry);
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("GeometrySubVolumeTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=getColumnCount()){
		throw new RuntimeException("GeometrySubVolumeTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
	}
	final SubVolume subVolume = getValueAt(rowIndex);
	try {
		switch (columnIndex){
			case COLUMN_NAME:{
				final String newName = (String)aValue;
				final String oldName = subVolume.getName();
				subVolume.setName(newName);
				AsynchClientTask task1 = new AsynchClientTask("changing the name", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
						GeometrySpec gs = getGeometry().getGeometrySpec();
						if (gs != null) {
							gs.geometryNameChanged(oldName,newName);
						}
						else {
							if (lg.isEnabledFor(Level.WARN)) {
								lg.warn(getGeometry().getDescription() + " has no GeometrySpec?");
							}
							
						}
					}
				};
				ClientTaskDispatcher.dispatch(ownerTable, new Hashtable<String, Object>(), new AsynchClientTask[] {task1}, false);
				break;
			}
			case COLUMN_VALUE:{
				if (subVolume instanceof AnalyticSubVolume){
					final AnalyticSubVolume analyticSubVolume = (AnalyticSubVolume)subVolume;
					if (aValue instanceof ScopedExpression){
						throw new RuntimeException("unexpected value type ScopedExpression");
					}else if (aValue instanceof String) {
						final String newExpressionString = (String)aValue;
						analyticSubVolume.setExpression(new Expression(newExpressionString));
						AsynchClientTask task1 = new AsynchClientTask("changing the expression", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
							@Override
							public void run(Hashtable<String, Object> hashTable) throws Exception {
								getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
							}
						};
						ClientTaskDispatcher.dispatch(ownerTable, new Hashtable<String, Object>(), new AsynchClientTask[] {task1}, false);
					}
				}
				break;
			}
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(ownerTable, e.getMessage(), e);
	}
}


@Override
protected Comparator<SubVolume> getComparator(int col, boolean ascending) {
	return null;
}

@Override
public boolean isSortable(int col) {
	return false;
}

}
