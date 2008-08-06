package cbit.vcell.model.gui;

import java.beans.PropertyVetoException;

import javax.swing.JTable;

import cbit.vcell.units.VCUnitException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ScopedExpression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.*;
import cbit.vcell.model.ModelQuantity;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.parser.ExpressionException;
import cbit.util.BeanUtils;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Model.ModelParameter;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ParameterTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 5;
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_DESCRIPTION = 1;
	public final static int COLUMN_IS_GLOBAL = 2;
	public final static int COLUMN_VALUE = 3;
	public final static int COLUMN_UNITS = 4;
	private String LABELS[] = { "Name", "Description", "Global", "Expression", "Units" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.model.Kinetics fieldKinetics = null;
	private JTable fieldParentComponentTable = null;		// needed for DialogUtils.showWarningDialog() 
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ParameterTableModel() {
	super();
	addPropertyChangeListener(this);
}

public ParameterTableModel(JTable argParentComponent) {
	super();
	addPropertyChangeListener(this);
	fieldParentComponentTable = argParentComponent;
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	switch (column){
		case COLUMN_NAME:{
			return String.class;
		}
		case COLUMN_UNITS:{
			return String.class;
		}
		case COLUMN_VALUE:{
			return cbit.vcell.parser.ScopedExpression.class;
		}
		case COLUMN_DESCRIPTION:{
			return String.class;
		}
		case COLUMN_IS_GLOBAL: {
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
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.model.Kinetics getKinetics() {
	return fieldKinetics;
}
/**
 * getValueAt method comment.
 */
private cbit.vcell.model.Parameter getParameter(int row) {
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	int index = row;
	if (index<getKinetics().getKineticsParameters().length){
		return getKinetics().getKineticsParameters()[index];
	}
	index = index - getKinetics().getKineticsParameters().length;
	if (index<getKinetics().getProxyParameters().length){
		return getKinetics().getProxyParameters()[index];
	}
	index = index - getKinetics().getProxyParameters().length;
	if (index<getKinetics().getUnresolvedParameters().length){
		return getKinetics().getUnresolvedParameters()[index];
	}
	return null;
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
	if (getKinetics()==null){
		return 0;
	}else{
		return getKinetics().getKineticsParameters().length + getKinetics().getUnresolvedParameters().length + getKinetics().getProxyParameters().length;
	}
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.model.Parameter parameter = getParameter(row);
	switch (col){
		case COLUMN_NAME:{
			return getKinetics().getReactionStep().getNameScope().getSymbolName(parameter);
			//return parameter.getName();
		}
		case COLUMN_UNITS:{
			if (parameter.getUnitDefinition() != null){
				return parameter.getUnitDefinition().getSymbol();
			}else{
				return "??";
			}
		}
		case COLUMN_VALUE:{
			Expression exp = parameter.getExpression();
			if (exp!=null){
				if ((parameter instanceof KineticsProxyParameter) && (((KineticsProxyParameter)parameter).getTarget() instanceof ReservedSymbol)) {
					ReservedSymbol rs = (ReservedSymbol)(((KineticsProxyParameter)parameter).getTarget());
					if (rs.isKMOLE()) {
						// KMOLE is the only ReservedSymbol that has is expressed as a rational number (1/602). Try printing this expression instead of its double value  
						try {
							return new ScopedExpression(new Expression("1.0/602.0"), parameter.getNameScope(), parameter.isExpressionEditable());
						} catch (ExpressionException e) {
							e.printStackTrace();
							throw new RuntimeException("Error writing expression for KMOLE reserved symbol" + e.getMessage());
						}
					} else {
						// if reserved symbol is not KMOLE, print it out like other parameters
						return new cbit.vcell.parser.ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());
					}
				} else {
					return new cbit.vcell.parser.ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());
				}
			}else{
				return "defined in application"; // new cbit.vcell.parser.ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());
			}
		}
		case COLUMN_DESCRIPTION:{
			return parameter.getDescription();
		}
		case COLUMN_IS_GLOBAL: {
			if (parameter instanceof KineticsParameter) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		default:{
			return null;
		}
	}
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:27:46 AM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	cbit.vcell.model.Parameter parameter = getParameter(rowIndex);
	if (columnIndex == COLUMN_NAME){
		return parameter.isNameEditable();
	}else if (columnIndex == COLUMN_UNITS){
		return parameter.isUnitEditable();
	}else if (columnIndex == COLUMN_DESCRIPTION){
		return false;
	}else if (columnIndex == COLUMN_VALUE){
		return parameter.isExpressionEditable();
	}else if (columnIndex == COLUMN_IS_GLOBAL) {
		// if the parameter is reaction rate param or a ReservedSymbol in the model, it should not be editable
		if (parameter == getKinetics().getAuthoritativeParameter()) {
			return false;
		} else if (parameter instanceof KineticsProxyParameter) { 
			KineticsProxyParameter kpp = (KineticsProxyParameter)parameter; 
			SymbolTableEntry ste = kpp.getTarget();
			if ((ste instanceof ReservedSymbol) || (ste instanceof SpeciesContext) || (ste instanceof ModelQuantity)) {
				return false;
			}
		}
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
	if (evt.getSource() == this && evt.getPropertyName().equals("kinetics")) {
		cbit.vcell.model.Kinetics oldKinetics = (Kinetics)evt.getOldValue();
		cbit.vcell.model.Kinetics newKinetics = (Kinetics)evt.getNewValue();
		if (oldKinetics != null){
			oldKinetics.removePropertyChangeListener(this);
			for (int i = 0; i < oldKinetics.getKineticsParameters().length; i++){
				oldKinetics.getKineticsParameters()[i].removePropertyChangeListener(this);
			}
			for (int i = 0; i < oldKinetics.getProxyParameters().length; i++){
				oldKinetics.getProxyParameters()[i].removePropertyChangeListener(this);
			}
		}
		if (newKinetics != null){
			newKinetics.addPropertyChangeListener(this);
			for (int i = 0; i < newKinetics.getKineticsParameters().length; i++){
				newKinetics.getKineticsParameters()[i].addPropertyChangeListener(this);
			}
			for (int i = 0; i < newKinetics.getProxyParameters().length; i++){
				newKinetics.getProxyParameters()[i].addPropertyChangeListener(this);
			}
		}
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.model.Kinetics && 
			(evt.getPropertyName().equals("kineticsParameters") ||  evt.getPropertyName().equals("proxyParameters"))) {
		cbit.vcell.model.Parameter oldParams[] = (cbit.vcell.model.Parameter[])evt.getOldValue();
		cbit.vcell.model.Parameter newParams[] = (cbit.vcell.model.Parameter[])evt.getNewValue();
		for (int i = 0; oldParams!=null && i < oldParams.length; i++){
			oldParams[i].removePropertyChangeListener(this);
		}
		for (int i = 0; newParams!=null && i < newParams.length; i++){
			newParams[i].addPropertyChangeListener(this);
		}
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.model.Parameter) {
		fireTableDataChanged();
	}
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setKinetics(cbit.vcell.model.Kinetics kinetics) {
	cbit.vcell.model.Kinetics oldValue = fieldKinetics;
	fieldKinetics = kinetics;
	firePropertyChange("kinetics", oldValue, kinetics);
}
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ParameterTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.model.Parameter parameter = getParameter(rowIndex);
//	try {
		switch (columnIndex){
			case COLUMN_NAME:{
				try {
					if (aValue instanceof String){
						String newName = (String)aValue;
						if (!parameter.getName().equals(newName)){
							if (parameter instanceof Kinetics.KineticsParameter){
								getKinetics().renameParameter(parameter.getName(),newName);
							}else if (parameter instanceof Kinetics.KineticsProxyParameter){
								parameter.setName(newName);
							}
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing parameter name:\n"+e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing parameter name:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_IS_GLOBAL: {
				if (aValue.equals(Boolean.FALSE)) {
					// check box has been <unset> (<true> to <false>) : change param from global to local  
					if ( (parameter instanceof KineticsProxyParameter) && 
						( (((KineticsProxyParameter)parameter).getTarget() instanceof ReservedSymbol) ||
						(((KineticsProxyParameter)parameter).getTarget() instanceof SpeciesContext) ||
						(((KineticsProxyParameter)parameter).getTarget() instanceof ModelQuantity) ) ) {
							PopupGenerator.showErrorDialog("Parameter : \'" + parameter.getName() + "\' is a " + ((KineticsProxyParameter)parameter).getTarget().getClass() + " in the model; cannot convert it to a local kinetic parameter.");
					} else {
						try {
							getKinetics().convertParameterType(parameter, false);
						} catch (PropertyVetoException pve) {
							pve.printStackTrace(System.out);
							PopupGenerator.showErrorDialog("Unable to convert parameter : \'" + parameter.getName() + "\' to local kinetics parameter : " + pve.getMessage());
						} catch (ExpressionBindingException e) {
							e.printStackTrace(System.out);
							PopupGenerator.showErrorDialog("Unable to convert parameter : \'" + parameter.getName() + "\' to local kinetics parameter : " + e.getMessage());
						}
					}
				} else {
					// check box has been <set> (<false> to <true>) : change param from local to global  
					if (parameter == getKinetics().getAuthoritativeParameter()) {
						PopupGenerator.showErrorDialog("Parameter : \'" + parameter.getName() + "\' is a reaction rate parameter; cannot convert it to a model level (global) parameter.");
					} else {
						ModelParameter mp = getKinetics().getReactionStep().getModel().getModelParameter(parameter.getName());
						// model already had the model parameter 'param', but check if 'param' value is different from 
						// model parameter with same name. If it is, the local value will be overridden by global (model) param
						// value, and user should be warned.
						String choice = "Ok";
						if (mp != null && !(mp.getExpression().compareEqual(parameter.getExpression()))) {
							String msgStr = "Model already has a global parameter named : \'" + parameter.getName() + "\'; with value = \'" 
									+ mp.getExpression().infix() + "\'; This local parameter \'" + parameter.getName() + "\' with value = \'" + 
									parameter.getExpression().infix() + "\' will be overridden by the global value. \nPress \'Ok' to override " + 
									"local value with global value of \'" + parameter.getName() + "\'. \nPress \'Cancel\' to retain new local value.";
							choice = PopupGenerator.showWarningDialog(fieldParentComponentTable, msgStr, new String[] {"Ok", "Cancel"}, "Ok");
						}
						if (choice.equals("Ok")) {
							try {
								getKinetics().convertParameterType(parameter, true);
							} catch (PropertyVetoException pve) {
								pve.printStackTrace(System.out);
								PopupGenerator.showErrorDialog("Unable to remove (global) proxy parameter : \'" + parameter.getName() + "\'. Cannot convert to global (proxy) parameter : " + pve.getMessage());
							} catch (ExpressionBindingException e) {
								e.printStackTrace(System.out);
								PopupGenerator.showErrorDialog("Unable to remove (global) proxy parameter : \'" + parameter.getName() + "\'. Cannot convert to global (proxy) parameter : " + e.getMessage());
							}
						}
					}
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
				break;
			}
			case COLUMN_VALUE:{
				try {
					if (aValue instanceof cbit.vcell.parser.ScopedExpression){
						Expression exp = ((cbit.vcell.parser.ScopedExpression)aValue).getExpression();
						if (parameter instanceof Kinetics.KineticsParameter){
							getKinetics().setParameterValue((Kinetics.KineticsParameter)parameter,exp);
						}else if (parameter instanceof Kinetics.KineticsProxyParameter){
							parameter.setExpression(exp);
						}
					}else if (aValue instanceof String) {
						String newExpressionString = (String)aValue;
						if (parameter instanceof Kinetics.KineticsParameter){
							getKinetics().setParameterValue((Kinetics.KineticsParameter)parameter,new Expression(newExpressionString));
						}else if (parameter instanceof Kinetics.KineticsProxyParameter){
							parameter.setExpression(new Expression(newExpressionString));
						}
					}
					getKinetics().resolveUndefinedUnits();
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Error:\n"+e.getMessage());
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Expression error:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_UNITS:{
				try {
					if (aValue instanceof String && parameter instanceof Kinetics.KineticsParameter && ((Kinetics.KineticsParameter)parameter).getRole()==Kinetics.ROLE_UserDefined){
						String newUnitString = (String)aValue;
						Kinetics.KineticsParameter kineticsParm = (Kinetics.KineticsParameter)parameter;
						if (!kineticsParm.getUnitDefinition().getSymbol().equals(newUnitString)){
							kineticsParm.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.getInstance(newUnitString));
							getKinetics().resolveUndefinedUnits();
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}catch (VCUnitException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing parameter unit:\n"+e.getMessage());
				}
				break;
			}
		}
//	}catch (java.beans.PropertyVetoException e){
//		e.printStackTrace(System.out);
//	}
}
}
