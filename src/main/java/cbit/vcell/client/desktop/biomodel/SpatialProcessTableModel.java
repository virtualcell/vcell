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
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class SpatialProcessTableModel extends BioModelEditorApplicationRightSideTableModel<SpatialProcess> implements PropertyChangeListener{

	public final static int COLUMN_SpatialProcess_NAME = 0;
	public final static int COLUMN_SpatialProcess_DESCRIPTION = 1;
	public final static int COLUMN_SpatialProcess_SPATIALOBJECTS = 2;
	
	private static String[] columnNames = new String[] {"Name", "Description", "Spatial Objects (and Quantities)"};

	SelectionManager selectionManager = null;

	public SpatialProcessTableModel(ScrollTable table) {
		super(table, columnNames);
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_SpatialProcess_NAME:{
				return String.class;
			}
			case COLUMN_SpatialProcess_DESCRIPTION:{
				return String.class;
			}
			case COLUMN_SpatialProcess_SPATIALOBJECTS:{
				return String.class;
			}
			default:{
				return Object.class;
			}
		}
	}

	protected List<SpatialProcess> computeData() {
		if (simulationContext == null || simulationContext.getSpatialProcesses() == null){
			return null;
		}
		List<SpatialProcess> SpatialProcesssList = new ArrayList<SpatialProcess>();
		for (SpatialProcess spatialProcess : simulationContext.getSpatialProcesses()) {
			if (searchText == null || searchText.length() == 0) {
				SpatialProcesssList.add(spatialProcess);
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();	
				if (spatialProcess.getName().toLowerCase().contains(lowerCaseSearchText) ||
						spatialProcess.getDescription() != null && spatialProcess.getDescription().toLowerCase().contains(lowerCaseSearchText) ||
						getSpatialObjectsString(spatialProcess).toLowerCase().contains(lowerCaseSearchText)) {
					SpatialProcesssList.add(spatialProcess);
				}
			}
		}
		return SpatialProcesssList;
	}
	
	private String getSpatialObjectsString(SpatialProcess spatialProcess){
		List<SpatialObject> spatialObjects = spatialProcess.getSpatialObjects();
		List<SpatialQuantity> spatialQuantities = spatialProcess.getReferencedSpatialQuantities();
		ArrayList<String> spatialObjectNames = new ArrayList<String>();
		for (SpatialObject obj : spatialObjects){
			ArrayList<String> categoryNames = new ArrayList<String>();
			for (SpatialQuantity quantity : spatialQuantities){
				if (quantity.getSpatialObject() == obj && !categoryNames.contains(quantity.getQuantityCategory().varSuffix)){
					categoryNames.add(quantity.getQuantityCategory().varSuffix);
				}
			}
			spatialObjectNames.add(obj.getName()+"  ("+categoryNames.toString().replace("[", "").replace("]", "")+")");
		}
		return spatialObjectNames.toString().replace("]","").replace("[","");
	}

	public Object getValueAt(int row, int column) {
		try{
			SpatialProcess spatialProcess = getValueAt(row);
			if (spatialProcess != null) {
				switch (column) {
					case COLUMN_SpatialProcess_NAME: {
						return spatialProcess.getName();
					} 
					case COLUMN_SpatialProcess_DESCRIPTION: {
						return spatialProcess.getDescription();
					}	
					case COLUMN_SpatialProcess_SPATIALOBJECTS: {
						return getSpatialObjectsString(spatialProcess);
					}	
				}
			} else {
				if (column == COLUMN_SpatialProcess_NAME) {
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
		//return columnIndex == COLUMN_SpatialProcess_NAME; 
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
		if (evt.getPropertyName().equals("SpatialProcesss")) {
			SpatialProcess[] oldValue = (SpatialProcess[])evt.getOldValue();
			if (oldValue != null) {			
				for (SpatialProcess rr : oldValue) {
					rr.removePropertyChangeListener(this);						
				}
			}
			SpatialProcess[] newValue = (SpatialProcess[])evt.getNewValue();
			if (newValue != null) {			
				for (SpatialProcess rr : newValue) {
					rr.addPropertyChangeListener(this);						
				}
			}
		}
		refreshData();
	}
	
	public void setValueAt(Object value, int row, int column) {
		try{
//			if (value == null || value.toString().length() == 0 || BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT.equals(value)) {
//				return;
//			}
//			SpatialProcess spatialProcess = getValueAt(row);
//			if (spatialProcess == null) {
//				spatialProcess = simulationContext.createPointKinematics(pointObject)PointObject();
//			} else {
//				spatialProcess = getValueAt(row);
//			}
//			switch (column) {
//				case COLUMN_SpatialProcess_NAME: {
//					/** @author anu : TODO : RATE RULES */
//					SpatialProcess.setName((String)value);
//				} 
//			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage());
		}
	}

	public Comparator<SpatialProcess> getComparator(int col, boolean ascending) {
		return null;
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}

	public String checkInputValue(String inputValue, int row, int column) {
		SpatialProcess SpatialProcess = getValueAt(row);
		switch (column) {
		case COLUMN_SpatialProcess_NAME: {
			if (SpatialProcess == null || !SpatialProcess.getName().equals(inputValue)) {
				if (simulationContext.getSpatialProcess(inputValue) != null) {
					return "A SpatialProcess with name '" + inputValue + "' already exists!";
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
