/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization.gui;
import java.util.Comparator;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ReferenceDataMappingSpecTableModel extends VCellSortTableModel<ReferenceDataMappingSpec> implements java.beans.PropertyChangeListener {
	private static final int COLUMN_DATACOLUMNNAME = 0;
	public static final int COLUMN_MODELENTITY = 1;
	private static String LABELS[] = { "Experimental Data", "Model Association" };
	private ModelOptimizationSpec fieldModelOptimizationSpec = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public ReferenceDataMappingSpecTableModel() {
	super(LABELS);
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_DATACOLUMNNAME:{
			return String.class;
		}
		case COLUMN_MODELENTITY:{
			return SymbolTableEntry.class;
		}
		default:{
			return Object.class;
		}
	}
}

/**
 * Gets the modelOptimizationSpec property (cbit.vcell.modelopt.ModelOptimizationSpec) value.
 * @return The modelOptimizationSpec property value.
 * @see #setModelOptimizationSpec
 */
private ModelOptimizationSpec getModelOptimizationSpec() {
	return fieldModelOptimizationSpec;
}

/**
 * getRowCount method comment.
 */
public int getRowCount() {
	if (getModelOptimizationSpec()==null || getModelOptimizationSpec().getReferenceDataMappingSpecs()==null){
		return 0;
	}else{
		return getModelOptimizationSpec().getReferenceDataMappingSpecs().length;
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	ReferenceDataMappingSpec referenceDataMappingSpec = getModelOptimizationSpec().getReferenceDataMappingSpecs()[row];
	switch (col){
		case COLUMN_DATACOLUMNNAME:{
			return referenceDataMappingSpec.getReferenceDataColumnName();
		}
		case COLUMN_MODELENTITY:{
			return referenceDataMappingSpec.getModelObject();
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
	return false;
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getModelOptimizationSpec() && evt.getPropertyName().equals("referenceDataMappingSpecs")) {
		ReferenceDataMappingSpec[] oldSpecs = (ReferenceDataMappingSpec[])evt.getOldValue();
		for (int i = 0; oldSpecs!=null && i < oldSpecs.length; i++){
			oldSpecs[i].removePropertyChangeListener(this);
		}
		ReferenceDataMappingSpec[] newSpecs = (ReferenceDataMappingSpec[])evt.getNewValue();
		for (int i = 0; newSpecs!=null && i < newSpecs.length; i++){
			newSpecs[i].addPropertyChangeListener(this);
		}
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof ReferenceDataMappingSpec){
		fireTableRowsUpdated(0, getRowCount() - 1);
	}
}

/**
 * Sets the modelOptimizationSpec property (cbit.vcell.modelopt.ModelOptimizationSpec) value.
 * @param modelOptimizationSpec The new value for the property.
 * @see #getModelOptimizationSpec
 */
public void setModelOptimizationSpec(ModelOptimizationSpec newValue) {
	ModelOptimizationSpec oldValue = fieldModelOptimizationSpec;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
		ReferenceDataMappingSpec[] refDataMappingSpecs = oldValue.getReferenceDataMappingSpecs();
		for (int i = 0;refDataMappingSpecs!=null && i < refDataMappingSpecs.length; i++){
			refDataMappingSpecs[i].removePropertyChangeListener(this);
		}
	}
	fieldModelOptimizationSpec = newValue;
	if (newValue!=null){
		newValue.addPropertyChangeListener(this);
		ReferenceDataMappingSpec[] refDataMappingSpecs = newValue.getReferenceDataMappingSpecs();
		for (int i = 0;refDataMappingSpecs!=null && i < refDataMappingSpecs.length; i++){
			refDataMappingSpecs[i].addPropertyChangeListener(this);
		}
	}
	fireTableDataChanged();
}

@Override
protected Comparator<ReferenceDataMappingSpec> getComparator(final int col, final boolean ascending) {
	return new Comparator<ReferenceDataMappingSpec>() {
		public int compare(ReferenceDataMappingSpec o1, ReferenceDataMappingSpec o2) {
			int scale = ascending ? 1 : -1;
			switch(col) {
			case COLUMN_DATACOLUMNNAME:
				return scale * o1.getReferenceDataColumnName().compareTo(o2.getReferenceDataColumnName());
			case COLUMN_MODELENTITY:
				return scale * o1.getModelObject().getName().compareTo(o2.getModelObject().getName());
			}
			return 0;
		}		
	};
}

}
