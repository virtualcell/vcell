/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;
import java.util.Arrays;

import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.simulation.ParameterScanPanel;
import cbit.vcell.math.Constant;
import cbit.vcell.math.FieldFunctionDefinition;
import cbit.vcell.math.GradientFunctionDefinition;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MathOverridesEvent;
import cbit.vcell.solver.MathOverridesListener;
import cbit.vcell.solver.SimulationSymbolTable;
/**
 * Insert the type's description here.
 * Creation date: (10/22/2000 11:46:26 AM)
 * @author: 
 */
public class MathOverridesTableModel extends javax.swing.table.AbstractTableModel implements MathOverridesListener {
	private String[] fieldKeys = new String[0];
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private MathOverrides fieldMathOverrides = null;
	private boolean fieldModified = false;
	private boolean fieldEditable = false;
	public final static int COLUMN_PARAMETER = 0;
	public final static int COLUMN_DEFAULT = 1;
	public final static int COLUMN_ACTUAL = 2;
	public final static int COLUMN_SCAN = 3;
	private String[] columnNames = new String[] {"Parameter Name", "Default", "New Value/Expression", "Scan"};
	private JTable ownerTable = null;
	private SimulationSymbolTable simSymbolTable = null;

/**
 * MathOverridesTableModel constructor comment.
 */
public MathOverridesTableModel(JTable table) {
	super();
	ownerTable = table;
	fieldMathOverrides = null;
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantAdded(MathOverridesEvent event) {
	updateKeys(getMathOverrides());
	fireTableDataChanged();
}


/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantChanged(MathOverridesEvent event) {
	updateKeys(getMathOverrides());
	fireTableDataChanged();
}


/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantRemoved(MathOverridesEvent event) {
	updateKeys(getMathOverrides());
	fireTableDataChanged();
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 5:06:23 PM)
 */
private void editScanValues(String name, int r) throws DivideByZeroException, ExpressionException {
	ParameterScanPanel panel = new ParameterScanPanel();
	ConstantArraySpec spec = null;
	if (getMathOverrides().isScan(name)) {
		spec = getMathOverrides().getConstantArraySpec(name);
	} else {
		spec = ConstantArraySpec.createIntervalSpec(name, "0", getMathOverrides().getDefaultExpression(name).infix(), 2, false);
	}
	panel.setConstantArraySpec(spec);
	int confirm = DialogUtils.showComponentOKCancelDialog(ownerTable, panel, "Scan values for parameter '" + fieldKeys[r]);
	if (confirm == javax.swing.JOptionPane.OK_OPTION) {
		panel.applyValues();
		getMathOverrides().putConstantArraySpec(panel.getConstantArraySpec());
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_PARAMETER:{
			return String.class;
		}
		case COLUMN_DEFAULT:{
			return String.class;
		}
		case COLUMN_ACTUAL:{
			// could be ScopedExpression or ConstantArraySpec.
			// we need auto complete cell editor when it's not scan, it's ok when it's ConstantArraySpec because cell is not editable
			return ScopedExpression.class; 
		}
		case COLUMN_SCAN:{
			return Boolean.class;
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
	return columnNames.length;
}


/**
 * getColumnCount method comment.
 */
public String getColumnName(int column) {
	try {
		return columnNames[column];
	} catch (Throwable exc) {
		System.out.println("WARNING - no such column index: " + column);
		handleException(exc);
		return null;
	}
}


/**
 * Gets the editable property (boolean) value.
 * @return The editable property value.
 * @see #setEditable
 */
public boolean getEditable() {
	return fieldEditable;
}


/**
 * Gets the mathOverrides property (cbit.vcell.solver.MathOverrides) value.
 * @return The mathOverrides property value.
 * @see #setMathOverrides
 */
public MathOverrides getMathOverrides() {
	return fieldMathOverrides;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return (fieldKeys.length);
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int column) {
	try {
		switch (column) {
			case COLUMN_PARAMETER: {
				return fieldKeys[row];
			} 
			case COLUMN_DEFAULT: {
				if(getMathOverrides().getDefaultExpression(fieldKeys[row]) == null){return null;}
				return getMathOverrides().getDefaultExpression(fieldKeys[row]).infix();
			} 
			case COLUMN_ACTUAL: {
				if (getMathOverrides().isScan(fieldKeys[row])) {
					return getMathOverrides().getConstantArraySpec(fieldKeys[row]);
				} else {
					Expression actualExpression = getMathOverrides().getActualExpression(fieldKeys[row], 0);
					if(actualExpression == null) {
						return null;
					}
					actualExpression.bindExpression(simSymbolTable);
					
					AutoCompleteSymbolFilter symbolTableEntryFilter = new AutoCompleteSymbolFilter() {

						public boolean accept(SymbolTableEntry ste) {
							if (ste instanceof Constant) {
								return (getMathOverrides().getConstant(ste.getName()) != null);
							}
							return false;
						}

						public boolean acceptFunction(String funcName) {
							if (FieldFunctionDefinition.FUNCTION_name.equals(funcName) || GradientFunctionDefinition.FUNCTION_name.equals(funcName)){
								return false;
							}
							return true;
						}
						
					};
					return new ScopedExpression(actualExpression, simSymbolTable.getNameScope(), true, true, symbolTableEntryFilter); 
				}
			}
			case COLUMN_SCAN: {
				return new Boolean(getMathOverrides().isScan(fieldKeys[row]));
			}
			default: {
				throw new Exception();
			}
		}
	} catch (Throwable exc) {
		// we don't check for coordinates, but do it this way so we can get a stacktrace if the wrong cell is being asked for
		// also, this way there is no overhead since try/catch is free unless exception is thrown
		System.out.println("WARNING - no such cell: " + row + ", " + column);
		handleException(exc);
		return null;
	}
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * getColumnCount method comment.
 */
public boolean isCellEditable(int r, int c) {
	if (!getEditable()) {
		return false;
	} else if (c == COLUMN_ACTUAL) {
		if (getMathOverrides().isScan(fieldKeys[r])) {
			return false;
		} else {
			return true;
		}
	} else if (c == COLUMN_SCAN) {
		return true;
	} else {
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 1:16:31 PM)
 * @return boolean
 * @param row int
 */
public boolean isDefaultValue(int row) {
	try {
		return getMathOverrides().isDefaultExpression(fieldKeys[row]);
	} catch (Throwable exc) {
		handleException(exc);
		return true;
	}
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Sets the editable property (boolean) value.
 * @param editable The new value for the property.
 * @see #getEditable
 */
public void setEditable(boolean editable) {
	boolean oldValue = fieldEditable;
	fieldEditable = editable;
	firePropertyChange("editable", new Boolean(oldValue), new Boolean(editable));
}


/**
 * Sets the mathOverrides property (cbit.vcell.solver.MathOverrides) value.
 * @param mathOverrides The new value for the property.
 * @see #getMathOverrides
 */
public void setMathOverrides(MathOverrides mathOverrides) {
	if (mathOverrides != fieldMathOverrides) {	
		MathOverrides oldValue = fieldMathOverrides;
		if (oldValue!=null){
			oldValue.removeMathOverridesListener(this);
		}
		fieldMathOverrides = mathOverrides;
		if (fieldMathOverrides!=null){
			fieldMathOverrides.addMathOverridesListener(this);
		}
		updateKeys(mathOverrides);
		
		firePropertyChange("mathOverrides", oldValue, mathOverrides);
	}
	//  Or should this only be fireTableDataChanged()???
	fireTableDataChanged();
}


private void updateKeys(MathOverrides mathOverrides) {
	fieldKeys = new String[0];
	if (mathOverrides != null) {
		if (getEditable()) {
			// show all
			fieldKeys = mathOverrides.getFilteredConstantNames();
		} else {
			// summary, show only overriden ones
			fieldKeys = mathOverrides.getOverridenConstantNames();
		}
		Arrays.sort(fieldKeys);
		simSymbolTable = new SimulationSymbolTable(getMathOverrides().getSimulation(), 0);
	}
}


/**
 * Sets the modified property (boolean) value.
 * @param modified The new value for the property.
 * @see #getModified
 */
public void setModified(boolean modified) {
	boolean oldValue = fieldModified;
	fieldModified = modified;
	firePropertyChange("modified", new Boolean(oldValue), new Boolean(modified));
}


/**
 * getValueAt method comment.
 */
public void setValueAt(Object object, int r, int c) {
	try {
		String name = (String) getValueAt(r,0);
		if (c == COLUMN_ACTUAL) {
			if (object instanceof ConstantArraySpec) {
				editScanValues(name, r);
			} else if (object instanceof ScopedExpression) {
				throw new RuntimeException("unexpected value type ScopedExpression");
			} else if (object instanceof String) {
				String inputValue = (String)object;
				Expression expression = null;
				if (inputValue == null || inputValue.trim().length() == 0) {
					expression = new Expression((String)getValueAt(r, COLUMN_DEFAULT));
				} else {
					expression = new Expression(inputValue);
				}
				Constant constant = new Constant(name, expression);
				getMathOverrides().putConstant(constant);
				fireTableCellUpdated(r, c);
				this.fireTableDataChanged();
				setModified(true);
			}
		} else if (c == COLUMN_SCAN) {
			if (((Boolean)object).booleanValue()) {
				// setting scan values
				editScanValues(name, r);
			} else {
				// resetting to default
				setValueAt(getValueAt(r, COLUMN_DEFAULT), r, COLUMN_ACTUAL);
			}
		}
	} catch (Throwable exc) {
		PopupGenerator.showErrorDialog(ownerTable, exc.getMessage() + "\nOld value was restored.", exc);		
	}
}


public boolean isUnusedParameterRow(int row) {
	return getMathOverrides().isUnusedParameter(fieldKeys[row]);
}

}
