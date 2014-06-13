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

import java.util.Arrays;
import java.util.Comparator;

import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ElectricalStimulusParameterTableModel extends VCellSortTableModel<Parameter> implements java.beans.PropertyChangeListener {
	
	private class ParameterColumnComparator implements Comparator<Parameter> {
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
		public int compare(Parameter parm1, Parameter parm2){
			
			switch (index){
				case COLUMN_NAME:{
					if (ascending){
						return parm1.getName().compareToIgnoreCase(parm2.getName());
					}else{
						return parm2.getName().compareToIgnoreCase(parm1.getName());
					}
					//break;
				}
				case COLUMN_VALUE:{
					String infix1 = (parm1.getExpression()!=null)?(parm1.getExpression().infix()):("");
					String infix2 = (parm2.getExpression()!=null)?(parm2.getExpression().infix()):("");
					if (ascending){
						return infix1.compareToIgnoreCase(infix2);
					}else{
						return infix2.compareToIgnoreCase(infix1);
					}
					//break;
				}
				case COLUMN_DESCRIPTION:{
					if (ascending){
						return parm1.getDescription().compareToIgnoreCase(parm2.getDescription());
					}else{
						return parm2.getDescription().compareToIgnoreCase(parm1.getDescription());
					}
					//break;
				}
				case COLUMN_UNIT:{
					String unit1 = (parm1.getUnitDefinition()!=null)?(parm1.getUnitDefinition().getSymbol()):"null";
					String unit2 = (parm2.getUnitDefinition()!=null)?(parm2.getUnitDefinition().getSymbol()):"null";
					if (ascending){
						return unit1.compareToIgnoreCase(unit2);
					}else{
						return unit2.compareToIgnoreCase(unit1);
					}
					//break;
				}
			}
			return 1;
		}
	}
	final static int COLUMN_DESCRIPTION = 0;
	final static int COLUMN_NAME = 1;
	final static int COLUMN_VALUE = 2;
	final static int COLUMN_UNIT = 3;
	private static final String LABELS[] = { "Description", "Parameter", "Expression", "Units" };
	private ElectricalStimulus fieldElectricalStimulus = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ElectricalStimulusParameterTableModel(ScrollTable table) {
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
 * Gets the electricalStimulus property (cbit.vcell.mapping.ElectricalStimulus) value.
 * @return The electricalStimulus property value.
 * @see #setElectricalStimulus
 */
public ElectricalStimulus getElectricalStimulus() {
	return fieldElectricalStimulus;
}

/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private void refreshData() {
	if (getElectricalStimulus()== null){
		setData(null);
	} else {
		setData(Arrays.asList(getElectricalStimulus().getParameters()));
	}
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		if (col<0 || col>=getColumnCount()){
			throw new RuntimeException("ParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(getColumnCount()-1)+"]");
		}
		if (row<0 || row>=getRowCount()){
			throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
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
					return "Variable";
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
	if (parameter instanceof ProxyParameter) {
		return false;
	}
	if (columnIndex == COLUMN_NAME){
		return parameter.isNameEditable();
	}else if (columnIndex == COLUMN_DESCRIPTION){
		return false;
	}else if (columnIndex == COLUMN_UNIT){
		return parameter.isUnitEditable();
	}else if (columnIndex == COLUMN_VALUE){
		return parameter.isExpressionEditable();
	}else{
		return false;
	}
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
	if (evt.getSource() == this && evt.getPropertyName().equals("electricalStimulus")) {
		ElectricalStimulus oldValue = (ElectricalStimulus)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
			Parameter oldParameters[] = oldValue.getParameters();
			for (int i = 0; i<oldParameters.length; i++){
				oldParameters[i].removePropertyChangeListener(this);
			}
		}
		ElectricalStimulus newValue = (ElectricalStimulus)evt.getNewValue();
		if (newValue!=null){
			newValue.addPropertyChangeListener(this);
			Parameter newParameters[] = newValue.getParameters();
			for (int i = 0; i<newParameters.length; i++){
				newParameters[i].addPropertyChangeListener(this);
			}
		}
		refreshData();
	}
	if (evt.getSource() instanceof ElectricalStimulus && (evt.getPropertyName().equals("localParameters") || evt.getPropertyName().equals("proxyParameters"))) {
		Parameter oldParameters[] = (Parameter[])evt.getOldValue();
		for (int i = 0; i<oldParameters.length; i++){
			oldParameters[i].removePropertyChangeListener(this);
		}
		Parameter newParameters[] = (Parameter[])evt.getNewValue();
		for (int i = 0; i<newParameters.length; i++){
			newParameters[i].addPropertyChangeListener(this);
		}
		refreshData();
	}
	if(evt.getSource() instanceof LocalParameter && evt.getPropertyName().equals("expression")){
		fireTableRowsUpdated(0, getRowCount() - 1);
	}
}
/**
 * Sets the electricalStimulus property (cbit.vcell.mapping.ElectricalStimulus) value.
 * @param electricalStimulus The new value for the property.
 * @see #getElectricalStimulus
 */
public void setElectricalStimulus(ElectricalStimulus electricalStimulus) {
	ElectricalStimulus oldValue = fieldElectricalStimulus;
	fieldElectricalStimulus = electricalStimulus;
	if (electricalStimulus != null) {
		autoCompleteSymbolFilter = electricalStimulus.getAutoCompleteSymbolFilter();
	}
	firePropertyChange("electricalStimulus", oldValue, electricalStimulus);
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (columnIndex<0 || columnIndex>=getColumnCount()){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
	}
	Parameter parameter = getValueAt(rowIndex);
//	try {
		switch (columnIndex){
			case COLUMN_VALUE:{
				try {
					String newExpressionString = (String)aValue;
					if (parameter instanceof LocalParameter){
						LocalParameter scsParm = (LocalParameter)parameter;
						getElectricalStimulus().setParameterValue(scsParm,new Expression(newExpressionString));
						//fireTableRowsUpdated(rowIndex,rowIndex);
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "expression error\n"+e.getMessage());
				}catch (Exception e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, e.getMessage(), e);
				}
				break;
			}
			case COLUMN_NAME: {
				try {
					getElectricalStimulus().renameParameter(parameter.getName(), (String)aValue);
				} catch (Exception e) {
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, e.getMessage(), e);
				}
			}
		}
//	}catch (java.beans.PropertyVetoException e){
//		e.printStackTrace(System.out);
//	}
}
	public Comparator<Parameter> getComparator(int col, boolean ascending) {
		return new ParameterColumnComparator(col, ascending);
	}
}
