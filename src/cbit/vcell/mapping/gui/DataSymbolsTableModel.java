/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.desktop.biomodel.BioModelEditorApplicationRightSideTableModel;
import cbit.vcell.data.DataContext;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;
/**
 * Insert the type's description here.
 * @author: 
 */
@SuppressWarnings("serial")
public class DataSymbolsTableModel extends BioModelEditorApplicationRightSideTableModel<DataSymbol> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_DATA_SYMBOL_NAME = 0;
	public static final int COLUMN_DATA_SYMBOL_TYPE = 1;
	public static final int COLUMN_DATA_SET_NAME = 2;
	public static final int COLUMN_DATA_CHANNEL_NAME = 3;
	public static final int COLUMN_DATA_CHANNEL_TYPE = 4;
	private final static String LABELS[] = { "Symbol Name", "Symbol Type", "Dataset Name", "Channel Name", "Channel Type"};
	

/**
 * ReactionSpecsTableModel constructor comment.
 */
public DataSymbolsTableModel(ScrollTable table) {
	super(table, LABELS);
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int column) {
	switch (column){
	case COLUMN_DATA_SYMBOL_NAME:{
		return String.class;
	}
	case COLUMN_DATA_SYMBOL_TYPE:{
		return String.class;
	}
	case COLUMN_DATA_SET_NAME:{
		return String.class;
	}
	case COLUMN_DATA_CHANNEL_NAME:{
		return String.class;
	}
	case COLUMN_DATA_CHANNEL_TYPE:{
		return String.class;
	}
		default:{
			return Object.class;
		}
	}
}

protected List<DataSymbol> computeData() {
	if (simulationContext == null){
		return null;
	} 
	
	List<DataSymbol> dataSymbolList = new ArrayList<DataSymbol>();
	for (DataSymbol dataSymbol : simulationContext.getDataContext().getDataSymbols()) {
		if (searchText == null || searchText.length() == 0) {
			dataSymbolList.add(dataSymbol);
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();	
			if (dataSymbol.getName().toLowerCase().contains(lowerCaseSearchText)
				|| dataSymbol.getDataSymbolType().getDisplayName().toLowerCase().contains(lowerCaseSearchText)
				|| (dataSymbol instanceof FieldDataSymbol && ((FieldDataSymbol)dataSymbol).getExternalDataIdentifier().getName().toLowerCase().contains(lowerCaseSearchText)
						|| ((FieldDataSymbol)dataSymbol).getFieldDataVarName().toLowerCase().contains(lowerCaseSearchText)
						|| ((FieldDataSymbol)dataSymbol).getFieldDataVarType().toLowerCase().contains(lowerCaseSearchText))) {					
				dataSymbolList.add(dataSymbol);
			}
		}
	}
	
	return dataSymbolList;
	
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		DataSymbol ds = getValueAt(row);
		switch (col){
			case COLUMN_DATA_SYMBOL_NAME:{
				return ds.getName();
			}
			case COLUMN_DATA_SYMBOL_TYPE:{
				return ds.getDataSymbolType().getDisplayName();
			}
			case COLUMN_DATA_SET_NAME:{
				if (ds instanceof FieldDataSymbol) {
					return ((FieldDataSymbol)ds).getExternalDataIdentifier().getName();
				} else {
					return null;
				}
			}
			case COLUMN_DATA_CHANNEL_NAME:{
				if (ds instanceof FieldDataSymbol) {
					return ((FieldDataSymbol)ds).getFieldDataVarName();
				} else {
					return null;
				}
			}
			case COLUMN_DATA_CHANNEL_TYPE:{
				if (ds instanceof FieldDataSymbol) {
					return ((FieldDataSymbol)ds).getFieldDataVarType();
				} else {
					return null;
				}
			}			
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}		
	return null;
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
		throw new RuntimeException("DataSymbolsTableModel.isCellEditable(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=getColumnCount()){
		throw new RuntimeException("DataSymbolsTableModel.isCellEditable(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
	}
	switch (columnIndex){
	case COLUMN_DATA_SYMBOL_NAME:{
		return false;
	}
	case COLUMN_DATA_SYMBOL_TYPE:{
		return false;
	}
	case COLUMN_DATA_SET_NAME:{
		return false;
	}
	case COLUMN_DATA_CHANNEL_NAME:{
		return false;
	}
	case COLUMN_DATA_CHANNEL_TYPE:{
		return false;
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
@Override
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	super.propertyChange(evt);
	if (evt.getSource() instanceof DataContext && evt.getPropertyName().equals("dataSymbols")) {
		refreshData();
	}
	if (evt.getSource() instanceof DataSymbol && evt.getPropertyName().equals("name")) {
		fireTableRowsUpdated(0, getRowCount() - 1);
	}
	if (evt.getSource() instanceof DataSymbol && evt.getPropertyName().equals("type")) {
		fireTableRowsUpdated(0, getRowCount() - 1);
	}
}

/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
@Override
protected void simulationContextChange(java.beans.PropertyChangeEvent evt) {
	super.simulationContextChange(evt);
	SimulationContext oldValue = (SimulationContext) evt.getOldValue();
	if (oldValue != null){
		oldValue.getDataContext().removePropertyChangeListener(this);
		for (DataSymbol ds : oldValue.getDataContext().getDataSymbols()){
			ds.removePropertyChangeListener(this);
		}
	}	
	SimulationContext newValue = (SimulationContext) evt.getNewValue();
	if (newValue!=null){
		newValue.getDataContext().addPropertyChangeListener(this);
		for (DataSymbol ds : newValue.getDataContext().getDataSymbols()){
			ds.addPropertyChangeListener(this);
		}
	}
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	DataSymbol dataSymbol = getValueAt(rowIndex);
	switch (columnIndex){
		case COLUMN_DATA_SYMBOL_NAME:{
			dataSymbol.setName((String)aValue);
			break;
		}
		case COLUMN_DATA_SYMBOL_TYPE:{
			break;
		}
		case COLUMN_DATA_SET_NAME:{
			break;
		}
// TODO: anything to do here?
		case COLUMN_DATA_CHANNEL_NAME:{
			break;
		}
		case COLUMN_DATA_CHANNEL_TYPE:{
			break;
		}
	}
}

@Override
public Comparator<DataSymbol> getComparator(final int col, final boolean ascending) {
	return new Comparator<DataSymbol>() {	
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(DataSymbol ds1, DataSymbol ds2 ){			
			
			switch (col){
				case COLUMN_DATA_SYMBOL_NAME:
				{
					String name1 = ds1.getName();
					String name2 = ds2.getName();
					if (ascending){
						return name1.compareToIgnoreCase(name2);
					}else{
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_DATA_SYMBOL_TYPE:
				{
					String name1 = ds1.getDataSymbolType().getDisplayName();
					String name2 = ds2.getDataSymbolType().getDisplayName();
					if (ascending){
						return name1.compareToIgnoreCase(name2);
					}else{
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_DATA_SET_NAME:
				{
					String name1 = ((FieldDataSymbol)ds1).getExternalDataIdentifier().getName();
					String name2 = ((FieldDataSymbol)ds2).getExternalDataIdentifier().getName();
					if (ascending){
						return name1.compareToIgnoreCase(name2);
					}else{
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_DATA_CHANNEL_NAME:
				{
					String name1 = ((FieldDataSymbol)ds1).getFieldDataVarName();
					String name2 = ((FieldDataSymbol)ds2).getFieldDataVarName();
					if (ascending){
						return name1.compareToIgnoreCase(name2);
					}else{
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_DATA_CHANNEL_TYPE:
				{
					String name1 = ((FieldDataSymbol)ds1).getFieldDataVarType();
					String name2 = ((FieldDataSymbol)ds2).getFieldDataVarType();
					if (ascending){
						return name1.compareToIgnoreCase(name2);
					}else{
						return name2.compareToIgnoreCase(name1);
					}
				}
			}
			return 1;
		};
	};	
}

public String checkInputValue(String inputValue, int row, int column) {
	// TODO Auto-generated method stub
	return null;
}

public SymbolTable getSymbolTable(int row, int column) {
	// TODO Auto-generated method stub
	return null;
}

public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
	// TODO Auto-generated method stub
	return null;
}

public Set<String> getAutoCompletionWords(int row, int column) {
	// TODO Auto-generated method stub
	return null;
}

}
