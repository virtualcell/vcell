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

import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;

/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ResolvedLocationTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 4;
	private final int COLUMN_NAME = 0;
	private final int COLUMN_SIZE = 1;
	private final int COLUMN_INSIDE = 2;
	private final int COLUMN_OUTSIDE = 3;
	private String LABELS[] = { "Name", "Size", "inside", "outside" };
	private cbit.vcell.geometry.surface.GeometrySurfaceDescription fieldGeometrySurfaceDescription = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public ResolvedLocationTableModel() {
	super();
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
		case COLUMN_SIZE:{
			return String.class;
		}
		case COLUMN_INSIDE:{
			return String.class;
		}
		case COLUMN_OUTSIDE:{
			return String.class;
		}
		default:{
			return Object.class;
		}
	}
}


/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return NUM_COLUMNS;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}


/**
 * Gets the geometrySurfaceDescription property (cbit.vcell.geometry.surface.GeometrySurfaceDescription) value.
 * @return The geometrySurfaceDescription property value.
 * @see #setGeometrySurfaceDescription
 */
public cbit.vcell.geometry.surface.GeometrySurfaceDescription getGeometrySurfaceDescription() {
	return fieldGeometrySurfaceDescription;
}


/**
 * getRowCount method comment.
 */
public int getRowCount() {
	if (getGeometrySurfaceDescription()==null || getGeometrySurfaceDescription().getGeometricRegions()==null){
		return 0;
	}else{
		return getGeometrySurfaceDescription().getGeometricRegions().length;
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.geometry.surface.GeometricRegion region = getGeometrySurfaceDescription().getGeometricRegions(row);
	switch (col){
		case COLUMN_NAME:{
			return region.getName();
		}
		case COLUMN_SIZE:{
			double size = region.getSize();
			cbit.vcell.units.VCUnitDefinition sizeUnit = region.getSizeUnit();
			return size+" ["+sizeUnit.getSymbolUnicode()+"]";
		}
		case COLUMN_INSIDE:{
			if (region instanceof SurfaceGeometricRegion){
				GeometricRegion adjacentRegions[] = region.getAdjacentGeometricRegions();
				return adjacentRegions[0].getName();
			}
		}
		case COLUMN_OUTSIDE:{
			if (region instanceof SurfaceGeometricRegion){
				GeometricRegion adjacentRegions[] = region.getAdjacentGeometricRegions();
				return adjacentRegions[1].getName();
			}
		}
		default:{
			return null;
		}
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
		return false;
	}else if (columnIndex == COLUMN_SIZE){
		return false;
	}else if (columnIndex == COLUMN_INSIDE){
		return false;
	}else if (columnIndex == COLUMN_OUTSIDE){
		return false;
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
	if (evt.getSource() instanceof cbit.vcell.geometry.surface.GeometrySurfaceDescription && evt.getPropertyName().equals("geometricRegions")) {
		fireTableDataChanged();
	}
}


/**
 * Sets the geometrySurfaceDescription property (cbit.vcell.geometry.surface.GeometrySurfaceDescription) value.
 * @param geometrySurfaceDescription The new value for the property.
 * @see #getGeometrySurfaceDescription
 */
public void setGeometrySurfaceDescription(cbit.vcell.geometry.surface.GeometrySurfaceDescription geometrySurfaceDescription) {
	cbit.vcell.geometry.surface.GeometrySurfaceDescription oldValue = fieldGeometrySurfaceDescription;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
	}
	fieldGeometrySurfaceDescription = geometrySurfaceDescription;
	if (geometrySurfaceDescription!=null){
		geometrySurfaceDescription.addPropertyChangeListener(this);
	}
	fireTableDataChanged();
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ParameterTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	//cbit.vcell.model.Kinetics.KineticsParameter parameter = getKinetics().getKineticsParameters(rowIndex);
////	try {
		//switch (columnIndex){
			//case COLUMN_NAME:{
				//try {
					//if (aValue instanceof String){
						//String newName = (String)aValue;
						//if (!parameter.getName().equals(newName)){
							//getKinetics().renameParameter(parameter.getName(),newName);
							//fireTableRowsUpdated(rowIndex,rowIndex);
						//}
					//}
				//}catch (ExpressionException e){
					//e.printStackTrace(System.out);
					//javax.swing.JOptionPane.showMessageDialog(cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().getMainClientWindow(),
						//e.getMessage(),"error changing parameter name",javax.swing.JOptionPane.YES_OPTION);
				//}catch (java.beans.PropertyVetoException e){
					//e.printStackTrace(System.out);
					//javax.swing.JOptionPane.showMessageDialog(cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().getMainClientWindow(),
						//e.getMessage(),"error changing parameter name",javax.swing.JOptionPane.YES_OPTION);
				//}
				//break;
			//}
			//case COLUMN_VALUE:{
				//try {
					//if (aValue instanceof cbit.vcell.parser.ScopedExpression){
						//Expression exp = ((cbit.vcell.parser.ScopedExpression)aValue).getExpression();
						//getKinetics().setParameterValue(parameter,exp);
					//}else if (aValue instanceof String) {
						//String newExpressionString = (String)aValue;
						//getKinetics().setParameterValue(parameter,new Expression(newExpressionString));
					//}
					//fireTableRowsUpdated(rowIndex,rowIndex);
				//}catch (java.beans.PropertyVetoException e){
					//e.printStackTrace(System.out);
					//javax.swing.JOptionPane.showMessageDialog(cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().getMainClientWindow(),
						//e.getMessage(),"error",javax.swing.JOptionPane.YES_OPTION);
				//}catch (ExpressionException e){
					//e.printStackTrace(System.out);
					//javax.swing.JOptionPane.showMessageDialog(cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().getMainClientWindow(),
						//e.getMessage(),"expression error",javax.swing.JOptionPane.YES_OPTION);
				//}
				//break;
			//}
			//case COLUMN_UNITS:{
				//try {
					//if (aValue instanceof String && parameter instanceof Kinetics.KineticsParameter && ((Kinetics.KineticsParameter)parameter).getRole()==Kinetics.ROLE_UserDefined){
						//String newUnitString = (String)aValue;
						//Kinetics.KineticsParameter kineticsParm = (Kinetics.KineticsParameter)parameter;
						//if (!kineticsParm.getUnitDefinition().getSymbol().equals(newUnitString)){
							//kineticsParm.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.getInstance(newUnitString));
							//fireTableRowsUpdated(rowIndex,rowIndex);
						//}
					//}
				//}catch (VCUnitException e){
					//e.printStackTrace(System.out);
					//javax.swing.JOptionPane.showMessageDialog(cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().getMainClientWindow(),
						//e.getMessage(),"error changing parameter unit",javax.swing.JOptionPane.YES_OPTION);
				//}
				//break;
			//}
		//}
//	}catch (java.beans.PropertyVetoException e){
//		e.printStackTrace(System.out);
//	}
}
}
