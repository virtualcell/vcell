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

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.LocalProxyParameter;
import cbit.vcell.mapping.ParameterContext.UnresolvedParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelQuantity;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ReactionRulePropertiesTableModel extends VCellSortTableModel<Object> implements java.beans.PropertyChangeListener {
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_DESCRIPTION = 1;
	public final static int COLUMN_IS_GLOBAL = 2;
	public final static int COLUMN_VALUE = 3;
	public final static int COLUMN_UNITS = 4;
	private final static String LABELS[] = { "Name", "Description", "Global", "Expression", "Units" };
	private ReactionRule reactionRule = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
	private boolean bEditable = true;
	
public ReactionRulePropertiesTableModel(ScrollTable argParentComponent, boolean bEditable) {
	super(argParentComponent, LABELS);
	this.bEditable = bEditable;
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
		case COLUMN_UNITS:{
			return VCUnitDefinition.class;
		}
		case COLUMN_VALUE:{
			return ScopedExpression.class;
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

void refreshData() {
	List<Object> parameterList = null;
	if (reactionRule != null) {
		parameterList = new ArrayList<Object>();
//		parameterList.add(reactionRule.getKineticLaw());
		parameterList.addAll(Arrays.asList(reactionRule.getKineticLaw().getLocalParameters()));
		parameterList.addAll(Arrays.asList(reactionRule.getKineticLaw().getProxyParameters()));
		parameterList.addAll(Arrays.asList(reactionRule.getKineticLaw().getUnresolvedParameters()));
//		parameterList.addAll(reactionRule.getReactantPatterns());
//		parameterList.addAll(reactionRule.getProductPatterns());
	}
	setData(parameterList);
}

public Object getValueAt(int row, int col) {
	try {
		Object o = getValueAt(row);
		if(o instanceof RbmKineticLaw) {		// we don't display these because it's not quite the same as with simple reactions
			RbmKineticLaw kl = (RbmKineticLaw)o;
			switch (col){
			case COLUMN_NAME:
				return "J";
			case COLUMN_UNITS:
				return "NA";
			case COLUMN_VALUE:
				return kl.getRateLawType().name();
			case COLUMN_DESCRIPTION:
				return "reaction rate";
			case COLUMN_IS_GLOBAL:
				return false;
			}
		} else if(o instanceof ReactantPattern) {		// we don't display these because it's not quite the same as with simple reactions
			ReactantPattern rp = (ReactantPattern)o;
			switch (col){
			case COLUMN_NAME:
				return "Reactant Pattern";
			case COLUMN_UNITS:
				return "NA";
			case COLUMN_VALUE:
				return rp.getSpeciesPattern().toString();
			case COLUMN_DESCRIPTION:
				return "Species Concentration";
			case COLUMN_IS_GLOBAL:
				return false;
			}
		} else if(o instanceof ProductPattern) {		// // we don't display these because it's not quite the same as with simple reactions
			ProductPattern rp = (ProductPattern)o;
			switch (col){
			case COLUMN_NAME:
				return "Product Pattern";
			case COLUMN_UNITS:
				return "NA";
			case COLUMN_VALUE:
				return rp.getSpeciesPattern().toString();
			case COLUMN_DESCRIPTION:
				return "Species Concentration";
			case COLUMN_IS_GLOBAL:
				return false;
			}
		} else if(o instanceof Parameter) {
			Parameter parameter = (Parameter)o;
			switch (col){
				case COLUMN_NAME:{
					return new Expression(parameter, reactionRule.getNameScope()).infix();
				}
				case COLUMN_UNITS:{
					if (parameter.getUnitDefinition() != null){
						return parameter.getUnitDefinition();
					}else{
						return "";
					}
				}
				case COLUMN_VALUE:{
					Expression exp = parameter.getExpression();
					if (exp!=null){
						return new ScopedExpression(parameter.getExpression(),parameter.getNameScope(), false, false, autoCompleteSymbolFilter);						
					}else{
						return "Variable";
					}
				}
				case COLUMN_DESCRIPTION:{
					return parameter.getDescription();
				}
				case COLUMN_IS_GLOBAL: {
					return !(parameter instanceof LocalParameter);
				}
			}
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}		
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:27:46 AM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	if (!bEditable) {
		return false;
	}
	Object o = getValueAt(rowIndex);
	if(!(o instanceof Parameter)) {
		return false;
	}
	Parameter parameter = (Parameter)o;
	if(reactionRule != null && reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate)==parameter) {
		if(!reactionRule.isReversible()) {
			return false;		// disable Kr if rule is not reversible
		}
	}
	switch (columnIndex) {
	case COLUMN_NAME:
		return parameter.isNameEditable();
	case COLUMN_DESCRIPTION:
		return false;
	case COLUMN_IS_GLOBAL:
		// if the parameter is reaction rate param or a ReservedSymbol in the model, it should not be editable
		if ( (parameter instanceof LocalParameter) && (((LocalParameter)parameter).getRole() != RbmKineticLaw.RbmKineticLawParameterType.UserDefined) ) {
			return false;
		}
		if (parameter instanceof UnresolvedParameter) {
			return false;
		}
		if (parameter instanceof LocalProxyParameter) { 
			LocalProxyParameter kpp = (LocalProxyParameter)parameter; 
			SymbolTableEntry ste = kpp.getTarget();
			if ((ste instanceof Model.ReservedSymbol) || (ste instanceof SpeciesContext) || (ste instanceof ModelQuantity)) {
				return false;
			}
		}
		return true;
	case COLUMN_VALUE:
		return parameter.isExpressionEditable();
	case COLUMN_UNITS:
		return parameter.isUnitEditable();
	}
	return false;	
}
/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("reactionRule")) {
		ReactionRule oldValue = (ReactionRule)evt.getOldValue();
		if (oldValue != null){
			oldValue.removePropertyChangeListener(this);
			oldValue.getKineticLaw().removePropertyChangeListener(this);
			for (int i = 0; i < oldValue.getKineticLaw().getLocalParameters().length; i++){
				oldValue.getKineticLaw().getLocalParameters()[i].removePropertyChangeListener(this);
			}
			for (int i = 0; i < oldValue.getKineticLaw().getProxyParameters().length; i++){
				oldValue.getKineticLaw().getProxyParameters()[i].removePropertyChangeListener(this);
			}
		}
		ReactionRule newValue = (ReactionRule)evt.getNewValue();
		if (newValue != null){
			newValue.addPropertyChangeListener(this);
			newValue.getKineticLaw().addPropertyChangeListener(this);
			for (int i = 0; i < newValue.getKineticLaw().getLocalParameters().length; i++){
				newValue.getKineticLaw().getLocalParameters()[i].addPropertyChangeListener(this);
			}
			for (int i = 0; i < newValue.getKineticLaw().getProxyParameters().length; i++){
				newValue.getKineticLaw().getProxyParameters()[i].addPropertyChangeListener(this);
			}
			autoCompleteSymbolFilter = reactionRule.getAutoCompleteSymbolFilter();
		}
		refreshData();
	}
	if (evt.getSource() == reactionRule && evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_KINETICLAW)) {
		RbmKineticLaw oldValue = (RbmKineticLaw)evt.getOldValue();		
		if (oldValue != null){
			oldValue.removePropertyChangeListener(this);
			for (int i = 0; i < oldValue.getLocalParameters().length; i++){
				oldValue.getLocalParameters()[i].removePropertyChangeListener(this);
			}
			for (int i = 0; i < oldValue.getProxyParameters().length; i++){
				oldValue.getProxyParameters()[i].removePropertyChangeListener(this);
			}
		}
		RbmKineticLaw newValue = (RbmKineticLaw)evt.getNewValue();
		if (newValue != null){
			newValue.addPropertyChangeListener(this);
			for (int i = 0; i < newValue.getLocalParameters().length; i++){
				newValue.getLocalParameters()[i].addPropertyChangeListener(this);
			}
			for (int i = 0; i < newValue.getProxyParameters().length; i++){
				newValue.getProxyParameters()[i].addPropertyChangeListener(this);
			}
		}
		refreshData();
	}
	if (evt.getSource() instanceof RbmKineticLaw && 
			(evt.getPropertyName().equals("localParameters") ||  evt.getPropertyName().equals("proxyParameters"))) {
		Parameter oldParams[] = (Parameter[])evt.getOldValue();
		Parameter newParams[] = (Parameter[])evt.getNewValue();
		for (int i = 0; oldParams!=null && i < oldParams.length; i++){
			oldParams[i].removePropertyChangeListener(this);
		}
		for (int i = 0; newParams!=null && i < newParams.length; i++){
			newParams[i].addPropertyChangeListener(this);
		}
		refreshData();
	}
	if (evt.getSource() instanceof LocalParameter ||  evt.getSource() instanceof LocalProxyParameter) {
		refreshData();
	}
	if (evt.getSource() instanceof Parameter) {
		fireTableRowsUpdated(0, getRowCount() - 1);
	}
}

/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setReactionRule(ReactionRule newValue) {
	ReactionRule oldValue = reactionRule;
	reactionRule = newValue;
	firePropertyChange("reactionRule", oldValue, newValue);
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	Object o = getValueAt(rowIndex);
	if(!(o instanceof Parameter)) {
		return;
	}
	Parameter parameter = (Parameter)o;
//	try {
		switch (columnIndex){
			case COLUMN_NAME:{
				try {
					if (aValue instanceof String){
						String newName = (String)aValue;
						if (!parameter.getName().equals(newName)){
							if (parameter instanceof LocalParameter){
								reactionRule.getKineticLaw().renameParameter(parameter.getName(),newName);
							}else if (parameter instanceof LocalProxyParameter){
								parameter.setName(newName);
							}
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Error changing parameter name:\n"+e.getMessage());
				}catch (PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Error changing parameter name:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_IS_GLOBAL: {
				if (aValue.equals(Boolean.FALSE)) {
					// check box has been <unset> (<true> to <false>) : change param from global to local  
					if ( (parameter instanceof LocalProxyParameter) && 
						( (((LocalProxyParameter)parameter).getTarget() instanceof Model.ReservedSymbol) ||
						(((LocalProxyParameter)parameter).getTarget() instanceof SpeciesContext) ||
						(((LocalProxyParameter)parameter).getTarget() instanceof ModelQuantity) ) ) {
							PopupGenerator.showErrorDialog(ownerTable, "Parameter : \'" + parameter.getName() + "\' is a " + ((LocalProxyParameter)parameter).getTarget().getClass() + " in the model; cannot convert it to a local kinetic parameter.");
					} else {
						try {
							reactionRule.getKineticLaw().convertParameterType(parameter, false);
						} catch (PropertyVetoException pve) {
							pve.printStackTrace(System.out);
							PopupGenerator.showErrorDialog(ownerTable, "Unable to convert parameter : \'" + parameter.getName() + "\' to local kinetics parameter : " + pve.getMessage());
						} catch (ExpressionBindingException e) {
							e.printStackTrace(System.out);
							PopupGenerator.showErrorDialog(ownerTable, "Unable to convert parameter : \'" + parameter.getName() + "\' to local kinetics parameter : " + e.getMessage());
						}
					}
				} else {
					// check box has been <set> (<false> to <true>) : change param from local to global  
					if ( (parameter instanceof LocalParameter) && (((LocalParameter)parameter).getRole() != RbmKineticLaw.RbmKineticLawParameterType.UserDefined) ) {
						PopupGenerator.showErrorDialog(ownerTable, "Parameter : \'" + parameter.getName() + "\' is a pre-defined kinetics parameter (not user-defined); cannot convert it to a model level (global) parameter.");
					} else {
						ModelParameter mp = reactionRule.getModel().getModelParameter(parameter.getName());
						// model already had the model parameter 'param', but check if 'param' value is different from 
						// model parameter with same name. If it is, the local value will be overridden by global (model) param
						// value, and user should be warned.
						String choice = "Ok";
						if (mp != null && !(mp.getExpression().compareEqual(parameter.getExpression()))) {
							String msgStr = "Model already has a global parameter named : \'" + parameter.getName() + "\'; with value = \'" 
									+ mp.getExpression().infix() + "\'; This local parameter \'" + parameter.getName() + "\' with value = \'" + 
									parameter.getExpression().infix() + "\' will be overridden by the global value. \nPress \'Ok' to override " + 
									"local value with global value of \'" + parameter.getName() + "\'. \nPress \'Cancel\' to retain new local value.";
							choice = PopupGenerator.showWarningDialog(ownerTable, msgStr, new String[] {"Ok", "Cancel"}, "Ok");
						}
						if (choice.equals("Ok")) {
							try {
								// Now 'parameter' is a local kinetic parameter. If it is not numeric, and if its expression
								// contains other local kinetic parameters, warn user that 'parameter' cannot be promoted because 
								// of its expression containing other local parameters.
								boolean bPromoteable = true;
								if (!parameter.getExpression().isNumeric()) {
									String[] symbols = parameter.getExpression().getSymbols();
									for (int i = 0; i < symbols.length; i++) {
										if (reactionRule.getKineticLaw().getLocalParameter(symbols[i]) != null) {
											PopupGenerator.showErrorDialog(ownerTable, "Parameter \'" + parameter.getName() + "\' contains other local kinetic parameters; Cannot convert it to global until the referenced parameters are global.");
											bPromoteable = false;
										}
									}
								}
								if (bPromoteable) {
									reactionRule.getKineticLaw().convertParameterType(parameter, true);
								}
							} catch (PropertyVetoException pve) {
								pve.printStackTrace(System.out);
								PopupGenerator.showErrorDialog(ownerTable, "Cannot convert parameter \'" + parameter.getName() + "\' to global parameter : " + pve.getMessage());
							} catch (ExpressionBindingException e) {
								e.printStackTrace(System.out);
								PopupGenerator.showErrorDialog(ownerTable, "Cannot convert parameter \'" + parameter.getName() + "\' to global parameter : " + e.getMessage());
							}
						}
					}
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
				break;
			}
			case COLUMN_VALUE:{
				try {
					if (aValue instanceof ScopedExpression){
//						Expression exp = ((ScopedExpression)aValue).getExpression();
//						if (parameter instanceof Kinetics.KineticsParameter){
//							getKinetics().setParameterValue((Kinetics.KineticsParameter)parameter,exp);
//						}else if (parameter instanceof Kinetics.KineticsProxyParameter){
//							parameter.setExpression(exp);
//						}
						throw new RuntimeException("unexpected value type ScopedExpression");
					}else if (aValue instanceof String) {
						String newExpressionString = (String)aValue;
						if (parameter instanceof LocalParameter){
							LocalParameter localParameter = (LocalParameter)parameter;
							reactionRule.getKineticLaw().setParameterValue(localParameter,new Expression(newExpressionString),true);
						}else if (parameter instanceof LocalProxyParameter){
							parameter.setExpression(new Expression(newExpressionString));
						}
					}
					reactionRule.getKineticLaw().resolveUndefinedUnits();
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Error:\n"+e.getMessage());
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_UNITS:{
				try {
					if (aValue instanceof String && parameter instanceof LocalParameter && ((LocalParameter)parameter).getRole()==RbmKineticLaw.RbmKineticLawParameterType.UserDefined){
						String newUnitString = (String)aValue;
						LocalParameter kineticsParm = (LocalParameter)parameter;
						ModelUnitSystem modelUnitSystem = reactionRule.getModel().getUnitSystem();
						if (!kineticsParm.getUnitDefinition().getSymbol().equals(newUnitString)){
							kineticsParm.setUnitDefinition(modelUnitSystem.getInstance(newUnitString));
							reactionRule.getKineticLaw().resolveUndefinedUnits();
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}catch (VCUnitException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Error changing parameter unit:\n"+e.getMessage());
				}
				break;
			}
		}
//	}catch (java.beans.PropertyVetoException e){
//		e.printStackTrace(System.out);
//	}
}

public void setEditable(boolean bNewValue) {
	bEditable = bNewValue;
	fireTableDataChanged();
}

@Override
protected Comparator<Object> getComparator(int col, boolean ascending) {
	return null;
}

@Override
public boolean isSortable(int col) {
	return false;
}
}
