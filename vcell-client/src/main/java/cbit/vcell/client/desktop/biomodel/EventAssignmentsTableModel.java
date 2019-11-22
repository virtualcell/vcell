/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;

import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

@SuppressWarnings("serial")
public class EventAssignmentsTableModel extends VCellSortTableModel<EventAssignment> implements PropertyChangeListener {
	protected static final String PROPERTY_NAME_SIMULATOIN_CONTEXT = "simulationContext";

		private class VariableColumnComparator implements Comparator<EventAssignment> {
			protected int index;
			protected boolean ascending;

			public VariableColumnComparator(int index, boolean ascending){
				this.index = index;
				this.ascending = ascending;
			}
			
			public int compare(EventAssignment parm1, EventAssignment parm2){
				
				switch (index){
					case COLUMN_EVENTASSGN_VARNAME:{
						if (ascending){
							return parm1.getTarget().getName().compareToIgnoreCase(parm2.getTarget().getName());
						}else{
							return parm2.getTarget().getName().compareToIgnoreCase(parm1.getTarget().getName());
						}
					}
				}
				return 0;
			}
		}

		public final static int COLUMN_EVENTASSGN_VARNAME = 0;
		public final static int COLUMN_EVENTASSIGN_EXPRESSION = 1;
		public final static int COLUMN_EVENTASSIGN_UNITS = 2;
		private static String[] columnNames = new String[] {"Variable to modify", "Expression to evaluate when action triggered", "Units"};
		
		private SimulationContext fieldSimContext = null;
		private BioEvent fieldBioEvent = null;
		private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;

	/**
	 * SimulationListTableModel constructor comment.
	 */
	public EventAssignmentsTableModel(ScrollTable table) {
		super(table, columnNames);
		addPropertyChangeListener(this);
	}

	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_EVENTASSGN_VARNAME:{
				return EventAssignment.class;
			}
			case COLUMN_EVENTASSIGN_EXPRESSION:{
				return cbit.gui.ScopedExpression.class;
			}
			case COLUMN_EVENTASSIGN_UNITS:{
				return String.class;
			}
			default:{
				return Object.class;
			}
		}
	}


	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int column) {
		try{
			if (row<0 || row>=getRowCount()){
				throw new RuntimeException("EventAssignmentsTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
			}
			if (column<0 || column>=getColumnCount()){
				throw new RuntimeException("EventAssignmentsTableModel.getValueAt(), column = "+column+" out of range ["+0+","+(getColumnCount()-1)+"]");
			}

			if (getRowCount() <= row){
				refreshData();
			}
			
			EventAssignment eventAssignment = getValueAt(row);
			if (row >= 0 && row < getRowCount()) {
				switch (column) {
					case COLUMN_EVENTASSGN_VARNAME: {
						return eventAssignment;
					} 
					case COLUMN_EVENTASSIGN_EXPRESSION: {
						if (eventAssignment.getAssignmentExpression() == null) {
							return null; 
						} else {
							return new ScopedExpression(eventAssignment.getAssignmentExpression(), fieldBioEvent.getNameScope(), true, true, autoCompleteSymbolFilter);
						}
					}
					case COLUMN_EVENTASSIGN_UNITS: {
						return eventAssignment.getTarget().getUnitDefinition().getSymbolUnicode();
					} 
					default: {
						return null;
					}
				}
			} else {
				return null;
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	public boolean isSortable(int col) {
		if (col == COLUMN_EVENTASSGN_VARNAME){
			return true;
		}else {
			return false;
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (7/12/2004 1:56:12 PM)
	 * @return boolean
	 * @param rowIndex int
	 * @param columnIndex int
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == COLUMN_EVENTASSGN_VARNAME || columnIndex == COLUMN_EVENTASSIGN_UNITS){
			return false;
		}else {
			return true;
		}
	}

	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() == this && (evt.getPropertyName().equals("bioEvent"))) {
			refreshData();
		}
		if (evt.getSource() == this && (evt.getPropertyName().equals(PROPERTY_NAME_SIMULATOIN_CONTEXT))) {
			simulationContextChange(evt);
		}
		if (evt.getSource() == fieldBioEvent && (evt.getPropertyName().equals("eventAssignments"))) {
			refreshData();
		}
	}
	
	protected void simulationContextChange(java.beans.PropertyChangeEvent evt) {
		refreshData();
		SimulationContext oldValue = (SimulationContext)evt.getOldValue();
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
		}
		SimulationContext newValue = (SimulationContext)evt.getNewValue();
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
		}
	}

	private void refreshData() {
		ArrayList<EventAssignment> eventAssignments = null;
		if (getSimulationContext() != null && fieldBioEvent != null) {
			eventAssignments = fieldBioEvent.getEventAssignments();
		}
		setData(eventAssignments);
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex<0 || rowIndex>=getRowCount()){
			throw new RuntimeException("EventAssignmentsTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		if (columnIndex<0 || columnIndex>=getColumnCount()){
			throw new RuntimeException("EventAssignmentsTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
		}
		EventAssignment eventAssignment = (EventAssignment)getValueAt(rowIndex);
		switch (columnIndex){
			case COLUMN_EVENTASSIGN_EXPRESSION:{
				try {
					if (aValue instanceof ScopedExpression){
						throw new RuntimeException("unexpected value type ScopedExpression");
					}else if (aValue instanceof String) {
						Expression exp = new Expression((String)aValue);
						eventAssignment.setAssignmentExpression(exp);
					}
					fireTableRowsUpdated(rowIndex, rowIndex);
				} catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
				}
				break;
			}

		}
	}

	public void setBioEvent(BioEvent argBioEvent) {
		BioEvent oldValue = fieldBioEvent;
		fieldBioEvent = argBioEvent;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
		}
		if (fieldBioEvent != null) {
			fieldBioEvent.addPropertyChangeListener(this);
		}
		firePropertyChange("bioEvent", oldValue, argBioEvent);
	}

	private SimulationContext getSimulationContext() {
		return fieldSimContext;
	}

	public void setSimulationContext(SimulationContext newValue) {
		if(fieldSimContext == newValue) {
			return;
		}
		SimulationContext oldValue = fieldSimContext;
		fieldSimContext = newValue;
		if (newValue != null) {
			autoCompleteSymbolFilter = newValue.getAutoCompleteSymbolFilter();
		}
		firePropertyChange(PROPERTY_NAME_SIMULATOIN_CONTEXT, oldValue, newValue);
	}

	public Comparator<EventAssignment> getComparator(int col, boolean ascending)
	{
	  return new VariableColumnComparator(col, ascending);
	}

}
