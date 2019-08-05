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
import java.util.LinkedList;
import java.util.List;

import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class SpatialQuantityTableModel extends VCellSortTableModel<SpatialQuantity> implements java.beans.PropertyChangeListener {

	private class SpatialQuantityColumnComparator implements Comparator<SpatialQuantity> {
		protected int index;
		protected boolean ascending;

		public SpatialQuantityColumnComparator(int index, boolean ascending){
			this.index = index;
			this.ascending = ascending;
		}
		
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(SpatialQuantity parm1, SpatialQuantity parm2){	
			
			switch (index){
				case COLUMN_NAME:{
					if (ascending){
						return parm1.getName().compareToIgnoreCase(parm2.getName());
					}else{
						return parm2.getName().compareToIgnoreCase(parm1.getName());
					}
				}
				case COLUMN_DESCRIPTION:{
					if (ascending){
						return parm1.getDescription().compareToIgnoreCase(parm2.getDescription());
					}else{
						return parm2.getDescription().compareToIgnoreCase(parm1.getDescription());
					}
				}
				case COLUMN_ENABLED:{
					boolean p1Enabled = parm1.isEnabled();
					boolean p2Enabled = parm1.isEnabled();
					if (ascending){
						return Boolean.toString(p1Enabled).toString().compareTo(Boolean.toString(p2Enabled));
					}else{
						return Boolean.toString(p2Enabled).toString().compareTo(Boolean.toString(p1Enabled));
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
	private static final int COLUMN_NAME = 0;
	private static final int COLUMN_DESCRIPTION = 1;
	private static final int COLUMN_ENABLED = 2;
	private static final int COLUMN_UNIT = 3;
	private final static String columnNames[] = { "Spatial Quantity Name", "Description", "Enabled", "Units" };
	private SpatialObject spatialObject = null;
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public SpatialQuantityTableModel(ScrollTable table) {
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
		case COLUMN_ENABLED:{
			return Boolean.class;
		}
		case COLUMN_UNIT:{
			return String.class;
		}
		default:{
			return Object.class;
		}
	}
}

private void refreshData() {
	if (spatialObject == null){
		setData(null);
	} else {
		List<SpatialQuantity> actual = new LinkedList<>();
		List<SpatialQuantity> all = Arrays.asList(spatialObject.getSpatialQuantities());
		for(SpatialQuantity sc : all) {
			// TODO: disable features not supported yet
			QuantityComponent qk = sc.getQuantityComponent();
			if(qk != QuantityComponent.Z) {
				actual.add(sc);
			}
		}
		setData(actual);
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		SpatialQuantity spatialQuantity = getValueAt(row);
		switch (col){
			case COLUMN_NAME:{
				return spatialQuantity.getName();
			}
			case COLUMN_DESCRIPTION:{
				return spatialQuantity.getDescription();
			}
			case COLUMN_ENABLED:{
				// the SpatialQuantity state (enabled or disabled) is not stored individually
				// this returns the state of the QuantityCategory
				return new Boolean(spatialQuantity.isEnabled());
			}
			case COLUMN_UNIT:{
				if (spatialQuantity.getUnitDefinition()!=null){
					return spatialQuantity.getUnitDefinition().getSymbolUnicode();
				}else{
					return "null";
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
	SpatialQuantity spatialQuantity = getValueAt(rowIndex);
	if (columnIndex == COLUMN_NAME){
		return spatialQuantity.isNameEditable();
	}else if (columnIndex == COLUMN_UNIT){
		return spatialQuantity.isUnitEditable();
	}else if (columnIndex == COLUMN_DESCRIPTION){
		return spatialQuantity.isDescriptionEditable();
	}
	return false;
}


/**
 * isSortable method comment.
 */
public boolean isSortable(int col) {
	return true;
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	try {		
		if (spatialObject != null && evt.getSource() == spatialObject 
				&& evt.getPropertyName().equals(SpatialObject.PROPERTY_NAME_SPATIALQUANTITIES)){
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
public void setSpatialObject(SpatialObject spatialObject) {
	SpatialObject oldValue = spatialObject;
	this.spatialObject = spatialObject;
	refreshData();
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	SpatialQuantity spatialQuantity = getValueAt(rowIndex);
	switch (columnIndex){
		case COLUMN_NAME:{
			try {
				if (aValue instanceof String){
					String newName = (String)aValue;
					if (!spatialQuantity.getName().equals(newName) && spatialQuantity.isNameEditable()){
						spatialQuantity.setName(newName);
					}
				}
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "error changing spatial quantity name\n"+e.getMessage());
			}
			break;
		}
		case COLUMN_DESCRIPTION:{
			try {
				if (aValue instanceof String) {
					String newDescription = (String)aValue;
					if (spatialQuantity.getDescription().equals(newDescription) && spatialQuantity.isDescriptionEditable()){
						spatialQuantity.setDescription(newDescription);
					}
				}
			}catch (Exception e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "error changing spatial quantity description\n"+e.getMessage());
			}
			break;
		}
	}
}


  public Comparator<SpatialQuantity> getComparator(int col, boolean ascending) {
    return new SpatialQuantityColumnComparator(col, ascending);
  }
}
