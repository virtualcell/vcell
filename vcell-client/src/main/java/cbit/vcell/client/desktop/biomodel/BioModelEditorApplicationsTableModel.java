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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorApplicationsTableModel extends BioModelEditorRightSideTableModel<SimulationContext> {	
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_MATH_TYPE = 1;
	public final static int COLUMN_ANNOTATION = 2;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private static String[] columnNames = new String[] {"Name", "Math Type", "Annotation"};

	public BioModelEditorApplicationsTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}

	public Class<?> getColumnClass(int column) {
		switch (column){		
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_MATH_TYPE: {
				return String.class;
			}
			case COLUMN_ANNOTATION: {
				return String.class;
			}
		}
		return Object.class;
	}

	protected ArrayList<SimulationContext> computeData() {
		ArrayList<SimulationContext> simulationContextList = new ArrayList<SimulationContext>();
		if (bioModel != null){
			for (SimulationContext simulationContext : bioModel.getSimulationContexts()){
				if (searchText == null || searchText.length() == 0) {
					simulationContextList.add(simulationContext);
				} else {
					String lowerCaseSearchText = searchText.toLowerCase();		
					if (simulationContext.getName().toLowerCase().contains(lowerCaseSearchText)) {
						simulationContextList.add(simulationContext);
					}
				}
			}
		}
		return simulationContextList;
	}

	public Object getValueAt(int row, int column) {
		if (bioModel == null) {
			return null;
		}
		try{
			SimulationContext simulationContext = getValueAt(row);
			switch (column) {
				case COLUMN_NAME: {
					return simulationContext.getName();
				} 
				case COLUMN_MATH_TYPE: {
					String spatialDescription = "";
					int dimension = simulationContext.getGeometry().getDimension(); 
					if (dimension == 0) {
						spatialDescription = "compartmental";
					} else {
						String temp = simulationContext.getGeometry().getGeometrySpec().hasImage() ? "(image)" : "(analytic)";
						spatialDescription = dimension + "D " + temp + " spatial ";
					}					
					switch (simulationContext.getApplicationType()){
					case NETWORK_DETERMINISTIC:{
						return "explicit network model, "+spatialDescription+", determinstic (ODE)";
					}
					case NETWORK_STOCHASTIC:{
						if (dimension == 0){
							return "explicit network model, "+spatialDescription+", stochastic (SSA)";
						}else{
							return "explicit network model, "+spatialDescription+", stochastic (Particles)";
						}
					}
					case RULE_BASED_STOCHASTIC:{
						return "Agent-based model, "+spatialDescription+", stochastic (SSA)";
					}
					case SPRINGSALAD:{
						return "Agent-based spatial model, "+spatialDescription+", stochastic (Particles)";
					}
					default:{
						throw new RuntimeException("math type description not yet implemented");
					}
					}
				} 
				case COLUMN_ANNOTATION: {
					return simulationContext.getDescription();
				} 
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex != COLUMN_MATH_TYPE;
	}
	
	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == bioModel && evt.getPropertyName().equals(BioModel.PROPERTY_NAME_SIMULATION_CONTEXTS)) {
			SimulationContext[] oldValue = (SimulationContext[]) evt.getOldValue();
			if (oldValue != null) {
				for (SimulationContext simulationContext : oldValue) {
					simulationContext.removePropertyChangeListener(this);
				}
			}
			SimulationContext[] newValue = (SimulationContext[]) evt.getNewValue();
			if (newValue != null) {
				for (SimulationContext simulationContext : newValue) {
					simulationContext.addPropertyChangeListener(this);
				}
			}
			refreshData();
		} else if (evt.getSource() instanceof SimulationContext) {
			int changeRow = getRowIndex((SimulationContext) evt.getSource());
			if (changeRow >= 0) {
				fireTableRowsUpdated(changeRow, changeRow);
			}
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (bioModel == null) {
			return;
		}
		try{
			SimulationContext simulationContext = getValueAt(row);
			if (simulationContext == null) {
				return;
			}
			String inputValue = (String)value;
			switch (column) {
			case COLUMN_NAME: {
				if (!inputValue.equals(ADD_NEW_HERE_TEXT)) {
					simulationContext.setName(inputValue);
				}
				break;
			} 
			case COLUMN_ANNOTATION: {
				simulationContext.setDescription(inputValue);
				break;
			}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage(), e);
		}
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}
	
	@Override
	public Comparator<SimulationContext> getComparator(int col, boolean ascending) {
		return null;
	}
	
	public String checkInputValue(String inputValue, int row, int column) {
		SimulationContext simulationContext = getValueAt(row);
		switch (column) {
		case COLUMN_NAME:
			if (simulationContext != null && simulationContext.getName().equals(inputValue)) {
				return null; // name did not change
			}
			if (bioModel.getSimulationContext(inputValue) != null) {
				return BioModel.SIMULATION_CONTEXT_DISPLAY_NAME + " '" + inputValue + "' already exist!";
			}
			break;
		}
		return null;
	}

	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final int row, final int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		return null;
	}

	@Override
	protected void bioModelChange(PropertyChangeEvent evt) {
		super.bioModelChange(evt);
		BioModel oldValue = (BioModel) evt.getOldValue();
		if (oldValue != null) {
			for (SimulationContext simulationContext : oldValue.getSimulationContexts()) {
				simulationContext.removePropertyChangeListener(this);
			}
		}
		BioModel newValue = (BioModel) evt.getNewValue();
		if (newValue != null) {
			for (SimulationContext simulationContext : newValue.getSimulationContexts()) {
				simulationContext.addPropertyChangeListener(this);
			}
		}
	}
}
