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
import java.util.Comparator;

import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class SpeciesContextSpecParameterTableModel extends VCellSortTableModel<SpeciesContextSpecParameter> implements java.beans.PropertyChangeListener {

	static {
		System.out.println("SpeciesContextSpecParameterTableModel: artifically filtering out membrane diffusion parameters and diffusion/bc's that are not applicable");
	}

	
	private class ParameterColumnComparator implements Comparator<SpeciesContextSpecParameter> {
		protected int index;
		protected boolean ascending;

		public ParameterColumnComparator(int index, boolean ascending){
			this.index = index;
			this.ascending = ascending;
		}
		
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(SpeciesContextSpecParameter parm1, SpeciesContextSpecParameter parm2){	
			
			switch (index){
				case COLUMN_NAME:{
					if (ascending){
						return parm1.getName().compareToIgnoreCase(parm2.getName());
					}else{
						return parm2.getName().compareToIgnoreCase(parm1.getName());
					}
				}
				case COLUMN_VALUE:{
					String expression1 = parm1.getExpression() != null ? parm1.getExpression().infix() : "";
					String expression2 = parm2.getExpression() != null ? parm2.getExpression().infix() : "";
					if (ascending){
						return expression1.compareToIgnoreCase(expression2);
					}else{
						return expression2.compareToIgnoreCase(expression1);
					}
				}
				case COLUMN_DESCRIPTION:{
					if (ascending){
						return parm1.getDescription().compareToIgnoreCase(parm2.getDescription());
					}else{
						return parm2.getDescription().compareToIgnoreCase(parm1.getDescription());
					}
				}
				case COLUMN_UNIT:{
					String unit1 = (parm1.getUnitDefinition()!=null)?(parm1.getUnitDefinition().getSymbol()):"null";
					String unit2 = (parm2.getUnitDefinition()!=null)?(parm2.getUnitDefinition().getSymbol()):"null";
					if (ascending){
						return unit1.compareToIgnoreCase(unit2);
					}else{
						return unit2.compareToIgnoreCase(unit1);
					}
				}
			}
			return 0;
		}
	}
	private static final int COLUMN_DESCRIPTION = 0;
	private static final int COLUMN_NAME = 1;
	public static final int COLUMN_VALUE = 2;
	private static final int COLUMN_UNIT = 3;
	private final static String columnNames[] = { "Description", "Parameter", "Expression", "Units" };
	private SpeciesContextSpec fieldSpeciesContextSpec = null;
	
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public SpeciesContextSpecParameterTableModel(ScrollTable table) {
	super(table, columnNames);
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
			return String.class;
		}
		case COLUMN_DESCRIPTION:{
			return String.class;
		}
		case COLUMN_UNIT:{
			return String.class;
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
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private void refreshData() {
	if (fieldSpeciesContextSpec == null){
		setData(null);
	} else {
		setData(fieldSpeciesContextSpec.computeApplicableParameterList());
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		Parameter parameter = getValueAt(row);
		switch (col){
			case COLUMN_NAME:{
				return parameter.getName();
			}
			case COLUMN_DESCRIPTION:{
				return parameter.getDescription();
			}
			case COLUMN_UNIT:{
				if (parameter.getUnitDefinition()!=null){
					return parameter.getUnitDefinition().getSymbolUnicode();
				}else{
					return "null";
				}
			}
			case COLUMN_VALUE:{
				if (parameter.getExpression()==null){
					return null;
				}else{
					return new ScopedExpression(parameter.getExpression(),parameter.getNameScope(), true, true, autoCompleteSymbolFilter);
				}
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
	Parameter parameter = getValueAt(rowIndex);
	if (columnIndex == COLUMN_NAME){
		return parameter.isNameEditable();
	}else if (columnIndex == COLUMN_VALUE){
		return parameter.isExpressionEditable();
	}
	return false;
}


/**
 * isSortable method comment.
 */
public boolean isSortable(int col) {
	return false;
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	//
	// must listen to the following events:
	//    this.simulationContext
	//		simulationContext.GeometryContext.geometry
	//      simulationContext.GeometryContext.structureMappings
	//        StructureMapping.resolved
	//
	//    this.speciesContextSpec
	//      SpeciesContextSpec.parameters
	//        Parameter.*
	//
	try {		
		//
		// if geometry changes (could affect spatially resolved boundaries).
		//
		if (fieldSpeciesContextSpec != null && evt.getSource() == fieldSpeciesContextSpec.getSimulationContext().getGeometryContext() 
				&& evt.getPropertyName().equals("geometry")){
			refreshData();
		}
		//
		// if structureMappings array changes (could affect spatially resolved boundaries).
		//
		if (fieldSpeciesContextSpec != null && evt.getSource() == fieldSpeciesContextSpec.getSimulationContext().getGeometryContext() 
				&& evt.getPropertyName().equals("structureMappings")){
			StructureMapping[] oldStructureMappings = (StructureMapping[])evt.getOldValue();
			for (int i = 0; oldStructureMappings!=null && i < oldStructureMappings.length; i++){
				oldStructureMappings[i].removePropertyChangeListener(this);
			}
			StructureMapping[] newStructureMappings = (StructureMapping[])evt.getNewValue();
			for (int i = 0; newStructureMappings!=null && i < newStructureMappings.length; i++){
				newStructureMappings[i].addPropertyChangeListener(this);
			}
			refreshData();
		}
		//
		// if structureMapping changes (could affect spatially resolved boundaries).
		//
		if (evt.getSource() instanceof StructureMapping){
			refreshData();
		}
		
		if (evt.getSource() == this && evt.getPropertyName().equals("speciesContextSpec")) {
			SpeciesContextSpec oldValue = (SpeciesContextSpec)evt.getOldValue();
			if (oldValue!=null){
				oldValue.removePropertyChangeListener(this);
				Parameter oldParameters[] = oldValue.getParameters();
				if (oldParameters!=null) {
					for (int i = 0; i<oldParameters.length; i++){
						oldParameters[i].removePropertyChangeListener(this);
					}
				}
				SimulationContext oldSimContext = oldValue.getSimulationContext();
				if (oldSimContext!=null){
					oldSimContext.getGeometryContext().removePropertyChangeListener(this);
					StructureMapping[] oldStructureMappings = oldSimContext.getGeometryContext().getStructureMappings();
					if (oldStructureMappings!=null) {
						for (int i = 0; i < oldStructureMappings.length; i++){
							oldStructureMappings[i].removePropertyChangeListener(this);
						}
					}
				}
			}
			SpeciesContextSpec newValue = (SpeciesContextSpec)evt.getNewValue();
			if (newValue!=null){
				newValue.addPropertyChangeListener(this);
				Parameter newParameters[] = newValue.getParameters();
				if (newParameters != null) {
					for (int i = 0; i < newParameters.length; i ++){
						newParameters[i].addPropertyChangeListener(this);
					}
				}
				SimulationContext newSimContext = newValue.getSimulationContext();
				if (newSimContext!=null){
					newSimContext.getGeometryContext().addPropertyChangeListener(this);
					StructureMapping[] newStructureMappings = newSimContext.getGeometryContext().getStructureMappings();
					if (newStructureMappings != null) {
						for (int i = 0; i < newStructureMappings.length; i++){
							newStructureMappings[i].addPropertyChangeListener(this);
						}
					}
				}
			}
			
			refreshData();
		}
		if (evt.getSource() instanceof SpeciesContextSpec){
			// if parameters changed must update listeners
			if (evt.getPropertyName().equals("parameters")) {
				Parameter oldParameters[] = (Parameter[])evt.getOldValue();
				if (oldParameters!=null) {
					for (int i = 0;i<oldParameters.length; i++){
						oldParameters[i].removePropertyChangeListener(this);
					}
				}
				Parameter newParameters[] = (Parameter[])evt.getNewValue();
				if (newParameters!=null) {
					for (int i = 0;i<newParameters.length; i++){
						newParameters[i].addPropertyChangeListener(this);
					}
				}
			}
			if (!evt.getPropertyName().equals(SpeciesContextSpec.PARAMETER_NAME_PROXY_PARAMETERS)) {
				// for any change to the SpeciesContextSpec, want to update all.
				// proxy parameters don't affect table
				refreshData();
			}
		}
		if(evt.getSource() instanceof Parameter){
			refreshData();
		}
	} catch (Exception e){
		e.printStackTrace(System.out);
	}
}

/**
 * Sets the speciesContextSpec property (cbit.vcell.mapping.SpeciesContextSpec) value.
 * @param speciesContextSpec The new value for the property.
 * @see #getSpeciesContextSpec
 */
public void setSpeciesContextSpec(SpeciesContextSpec speciesContextSpec) {
	SpeciesContextSpec oldValue = fieldSpeciesContextSpec;
	fieldSpeciesContextSpec = speciesContextSpec;
	firePropertyChange("speciesContextSpec", oldValue, speciesContextSpec);
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	Parameter parameter = getValueAt(rowIndex);
	switch (columnIndex){
		case COLUMN_NAME:{
			try {
				if (aValue instanceof String){
					String newName = (String)aValue;
					if (!parameter.getName().equals(newName)){
						if (parameter instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
							SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)parameter;
							scsParm.setName(newName);
						}
					}
				}
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "error changing parameter name\n"+e.getMessage());
			}
			break;
		}
		case COLUMN_VALUE:{
			try {
				if (aValue instanceof String) {
					String newExpressionString = (String)aValue;
					if (parameter instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
						SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)parameter;
						Expression newExp = null;
						if (newExpressionString == null || newExpressionString.trim().length() == 0) {
							if (scsParm.getRole() == SpeciesContextSpec.ROLE_InitialConcentration
									|| scsParm.getRole() == SpeciesContextSpec.ROLE_DiffusionRate
									|| scsParm.getRole() == SpeciesContextSpec.ROLE_InitialCount) {
								newExp = new Expression(0.0);
							}
						} else {
							newExp = new Expression(newExpressionString);
						}
						scsParm.setExpression(newExp);
					}
				}
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "expression error\n"+e.getMessage());
			}
			break;
		}
	}
}


  public Comparator<SpeciesContextSpecParameter> getComparator(int col, boolean ascending) {
    return new ParameterColumnComparator(col, ascending);
  }
}
