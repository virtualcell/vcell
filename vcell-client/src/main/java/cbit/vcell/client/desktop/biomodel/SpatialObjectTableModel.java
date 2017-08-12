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
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class SpatialObjectTableModel extends BioModelEditorApplicationRightSideTableModel<SpatialObject> implements PropertyChangeListener{

	public final static int COLUMN_SpatialObject_NAME = 0;
	public final static int COLUMN_SpatialObject_DESCRIPTION = 1;
	public final static int COLUMN_SpatialObject_QUANTITIES = 2;
	
	private static String[] columnNames = new String[] {"Name", "Description", "Quantities"};

	SelectionManager selectionManager = null;

	public SpatialObjectTableModel(ScrollTable table) {
		super(table, columnNames);
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_SpatialObject_NAME:{
				return String.class;
			}
			case COLUMN_SpatialObject_DESCRIPTION:{
				return String.class;
			}
			case COLUMN_SpatialObject_QUANTITIES:{
				return String.class;
			}
			default:{
				return Object.class;
			}
		}
	}

	protected List<SpatialObject> computeData() {
		if (simulationContext == null || simulationContext.getSpatialObjects() == null){
			return null;
		}
		List<SpatialObject> SpatialObjectsList = new ArrayList<SpatialObject>();
		for (SpatialObject spatialObject : simulationContext.getSpatialObjects()) {
			if (searchText == null || searchText.length() == 0) {
				SpatialObjectsList.add(spatialObject);
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();	
				if (spatialObject.getName().toLowerCase().contains(lowerCaseSearchText) ||
						spatialObject.getDescription() != null && spatialObject.getDescription().toLowerCase().contains(lowerCaseSearchText) ||
						getQuantitiesString(spatialObject).toLowerCase().contains(lowerCaseSearchText)) {
					SpatialObjectsList.add(spatialObject);
				}
			}
		}
		return SpatialObjectsList;
	}
	
	private String getQuantitiesString(SpatialObject spatialObject) {
		List<QuantityCategory> categories = spatialObject.getQuantityCategories();
		ArrayList<String> categoryNames = new ArrayList<String>();
		for (QuantityCategory cat : categories){
			if(spatialObject.isQuantityCategoryEnabled(cat)) {
				categoryNames.add("<b>" + cat.varSuffix + "</b>");
			} else {
				categoryNames.add(cat.varSuffix);
			}
		}
		String ret = categoryNames.toString().replace("]","").replace("[","");
		ret = "<html>" + ret + "</html>";
		return ret;
	}

	public Object getValueAt(int row, int column) {
		try{
			SpatialObject spatialObject = getValueAt(row);
			if (spatialObject != null) {
				switch (column) {
					case COLUMN_SpatialObject_NAME: {
						return spatialObject.getName();
					} 
					case COLUMN_SpatialObject_DESCRIPTION: {
						return spatialObject.getDescription();
					}	
					case COLUMN_SpatialObject_QUANTITIES: {
						return getQuantitiesString(spatialObject);
					}	
				}
			} else {
				if (column == COLUMN_SpatialObject_NAME) {
					return BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT;
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
		//Make none of the fields editable until code for adding new rate rules is fixed. 10/20/2014
		//return columnIndex == COLUMN_SpatialObject_NAME; 
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
		if (evt.getPropertyName().equals("spatialObjects")) {
			SpatialObject[] oldValue = (SpatialObject[])evt.getOldValue();
			if (oldValue != null) {			
				for (SpatialObject rr : oldValue) {
					rr.removePropertyChangeListener(this);						
				}
			}
			SpatialObject[] newValue = (SpatialObject[])evt.getNewValue();
			if (newValue != null) {			
				for (SpatialObject rr : newValue) {
					rr.addPropertyChangeListener(this);						
				}
			}
		}
		refreshData();
	}
	
	public void setValueAt(Object value, int row, int column) {
		try{
			if (value == null || value.toString().length() == 0 || BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT.equals(value)) {
				return;
			}
			SpatialObject spatialObject = getValueAt(row);
			if (spatialObject == null) {
				spatialObject = simulationContext.createPointObject();
			} else {
				spatialObject = getValueAt(row);
			}
			switch (column) {
				case COLUMN_SpatialObject_NAME: {
					/** @author anu : TODO : RATE RULES */
					spatialObject.setName((String)value);
				} 
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage());
		}
	}

	public Comparator<SpatialObject> getComparator(int col, boolean ascending) {
		return null;
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}

	public String checkInputValue(String inputValue, int row, int column) {
		SpatialObject SpatialObject = getValueAt(row);
		switch (column) {
		case COLUMN_SpatialObject_NAME: {
			if (SpatialObject == null || !SpatialObject.getName().equals(inputValue)) {
				if (simulationContext.getSpatialObject(inputValue) != null) {
					return "A SpatialObject with name '" + inputValue + "' already exists!";
				}
				if (simulationContext.getModel().getReservedSymbolByName(inputValue) != null) {
					return "Cannot use reserved symbol '" + inputValue + "' as an event name";
				}
			}
		}
		}
		return null;
	}

	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}

	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		return null;
	}
	
	@Override
	public int getRowCount() {
		// -1 added 10/20/2014 to suppress extra row for adding new rule until adding rate rules framework is fixed.  Had been return getRowCountWithAddNew();
		return getRowCountWithAddNew()-1;  
	}
	
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}
	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

}
