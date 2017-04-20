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

import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ElectricalMembraneMappingTableModel extends VCellSortTableModel<MembraneMapping> implements java.beans.PropertyChangeListener {
	public final static int COLUMN_MEMBRANE = 0;
	public final static int COLUMN_CALCULATE_POTENTIAL = 1;
	public final static int COLUMN_INITIAL_POTENTIAL = 2;
	public final static int COLUMN_SPECIFIC_CAPACITANCE = 3;
	public final static String LABEL_MEMBRANE = "Membrane";
	public final static String LABEL_CALCULATE_POTENTIAL = "Calculate V?";
	public final static String LABEL_INITIAL_POTENTIAL = "V initial";
	public final static String LABEL_SPECIFIC_CAPACITANCE = "Specific Capacitance";
	private final static String LABELS[] = { LABEL_MEMBRANE, LABEL_CALCULATE_POTENTIAL, LABEL_INITIAL_POTENTIAL, LABEL_SPECIFIC_CAPACITANCE };
	private GeometryContext fieldGeometryContext = null;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ElectricalMembraneMappingTableModel(ScrollTable table) {
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
		case COLUMN_MEMBRANE:{
			return String.class;
		}
		case COLUMN_CALCULATE_POTENTIAL:{
			return Boolean.class;
		}
		case COLUMN_INITIAL_POTENTIAL:{
			return ScopedExpression.class;
		}
		case COLUMN_SPECIFIC_CAPACITANCE:{
			return ScopedExpression.class;
		}
		default:{
			return Object.class;
		}
	}
}

/**
 * Gets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @return The geometryContext property value.
 * @see #setGeometryContext
 */
public GeometryContext getGeometryContext() {
	return fieldGeometryContext;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/01 10:02:00 AM)
 * @return cbit.vcell.mapping.FeatureMapping
 * @param row int
 */
private void refreshData() {
	List<MembraneMapping> membraneMappingList = new ArrayList<MembraneMapping>();
	if (getGeometryContext() != null){
		StructureMapping structureMappings[] = getGeometryContext().getStructureMappings();
		for (int i=0;i<structureMappings.length;i++){
			if (structureMappings[i] instanceof MembraneMapping){
				membraneMappingList.add((MembraneMapping)structureMappings[i]);
			}
		}
	}
	setData(membraneMappingList);
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		MembraneMapping membraneMapping = getValueAt(row);
		if (membraneMapping == null){
			return null;
		}
		switch (col){
			case COLUMN_MEMBRANE:{
				if (membraneMapping.getStructure()!=null){
					return membraneMapping.getStructure().getName();
				}else{
					return null;
				}
			}
			case COLUMN_CALCULATE_POTENTIAL:{
				return new Boolean(membraneMapping.getCalculateVoltage());
			}
			case COLUMN_INITIAL_POTENTIAL:{
				return new ScopedExpression(membraneMapping.getInitialVoltageParameter().getExpression(),membraneMapping.getInitialVoltageParameter().getNameScope());
			}
			case COLUMN_SPECIFIC_CAPACITANCE:{
				if (membraneMapping.getCalculateVoltage()){
					return new ScopedExpression(membraneMapping.getSpecificCapacitanceParameter().getExpression(),membraneMapping.getSpecificCapacitanceParameter().getNameScope());
				}else{
					return null;
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
	MembraneMapping mm = getValueAt(rowIndex);
	//
	// see if solving for potential (otherwise ignore capacitance).
	//
	if (columnIndex == COLUMN_SPECIFIC_CAPACITANCE){
		if (mm.getCalculateVoltage()){
			return true;
		}else{
			return false;
		}
	}else if (columnIndex == COLUMN_INITIAL_POTENTIAL){
		return true;
	}else if (columnIndex == COLUMN_CALCULATE_POTENTIAL){
		return true;
	}else{
		return false;
	}
}
/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getGeometryContext() && evt.getPropertyName().equals("structureMappings")) {
		refreshData();
	}
	if (evt.getSource() instanceof StructureMapping) {
		fireTableRowsUpdated(0, getRowCount() - 1);
	}
}

/**
 * Sets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @param geometryContext The new value for the property.
 * @see #getGeometryContext
 */
public void setGeometryContext(GeometryContext geometryContext) {
	GeometryContext oldValue = fieldGeometryContext;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		StructureMapping oldStructureMappings[] = oldValue.getStructureMappings();
		for (int i=0;i<oldStructureMappings.length;i++){
			if (oldStructureMappings[i] instanceof MembraneMapping){
				oldStructureMappings[i].removePropertyChangeListener(this);
			}
		}
	}
	fieldGeometryContext = geometryContext;
	if (geometryContext!=null){
		geometryContext.addPropertyChangeListener(this);
		StructureMapping newStructureMappings[] = geometryContext.getStructureMappings();
		for (int i=0;i<newStructureMappings.length;i++){
			if (newStructureMappings[i] instanceof MembraneMapping){
				newStructureMappings[i].addPropertyChangeListener(this);
			}
		}
	}
	refreshData();
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	MembraneMapping membraneMapping = getValueAt(rowIndex);
	switch (columnIndex){
		case COLUMN_CALCULATE_POTENTIAL:{
			boolean bCalculatePotential = ((Boolean)aValue).booleanValue();
			membraneMapping.setCalculateVoltage(bCalculatePotential);
			fireTableRowsUpdated(rowIndex,rowIndex);
			break;
		}
		case COLUMN_INITIAL_POTENTIAL:{
			Expression newExpression = null;
			try {
				if (aValue instanceof String){
					String newExpressionString = (String)aValue;
					newExpression = new Expression(newExpressionString);
				}
				membraneMapping.getInitialVoltageParameter().setExpression(newExpression);
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "expression error\n"+e.getMessage());
			}
			break;
		}
		case COLUMN_SPECIFIC_CAPACITANCE:{
			Expression newExpression = null;
			try {
				if (aValue instanceof String){
					String newExpressionString = (String)aValue;
					newExpression = new Expression(newExpressionString);
				}else if (aValue instanceof ScopedExpression){
//					newExpression = ((ScopedExpression)aValue).getExpression();
					throw new RuntimeException("unexpected value type ScopedExpression");
				}
				membraneMapping.getSpecificCapacitanceParameter().setExpression(newExpression);
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "expression error\n"+e.getMessage());
			}
			break;
		}
	}
}

@Override
protected Comparator<MembraneMapping> getComparator(int col, boolean ascending) {
	return null;
}
@Override
public boolean isSortable(int col) {
	return false;
}
}
