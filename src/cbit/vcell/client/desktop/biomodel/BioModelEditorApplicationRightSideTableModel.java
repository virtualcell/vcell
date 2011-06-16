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
import java.util.List;

import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.mapping.SimulationContext;

/**
 * BioModelEditorRightSideTableModel extends DefaultSortTableModel and always has an extra row for adding new row.
 * 
 * 
 * It has the following abstract methods 
 * protected abstract String checkInputValue(String inputValue, int row, int column);
 * after a value is typed, check to see if the new value is valid. If not, 
 * editing is not stopped and tooltip is set to the error message.
 *  
 * protected abstract SymbolTable getSymbolTable(int row, int column);
 * protected abstract AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column);
 * these 2 methods provide the auto completion if there is a symbol table.  
 *  
 * protected abstract Set<String> getAutoCompletionWords(int row, int column);
 * this method provodes auto completion if there is no symbol table.
 *  
 * @author fgao
 *
 */

@SuppressWarnings("serial")
public abstract class BioModelEditorApplicationRightSideTableModel<T> extends VCellSortTableModel<T> implements PropertyChangeListener, AutoCompleteTableModel {
	protected static final String PROPERTY_NAME_SIMULATOIN_CONTEXT = "simulationContext";
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	
	protected SimulationContext simulationContext = null;
	protected String searchText = null;
	
	public BioModelEditorApplicationRightSideTableModel(ScrollTable table, String[] columnNames) {
		super(table, columnNames);
		addPropertyChangeListener(this);
	}
	
	protected int getRowCountWithAddNew() {
		return super.getRowCount() + (searchText == null || searchText.length() == 0 ? 1 : 0);
	}
	
	protected abstract List<T> computeData();
	
	protected void refreshData() {
		List<T> newData = computeData();
		setData(newData);
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() == this) {
			if (evt.getPropertyName().equals(PROPERTY_NAME_SIMULATOIN_CONTEXT)) {
				simulationContextChange(evt);
			} else if (evt.getPropertyName().equals(PROPERTY_NAME_SEARCH_TEXT)) {
				refreshData();
			}
		} else if (evt.getSource() == simulationContext) {
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
	
	public void setSimulationContext(SimulationContext newValue) {
		SimulationContext oldValue = simulationContext;
		simulationContext = newValue;
		firePropertyChange(PROPERTY_NAME_SIMULATOIN_CONTEXT, oldValue, newValue);
	}

	public void setSearchText(String newValue) {
		String oldValue = searchText;
		searchText = newValue;
		firePropertyChange(PROPERTY_NAME_SEARCH_TEXT, oldValue, newValue);		
	}
}
