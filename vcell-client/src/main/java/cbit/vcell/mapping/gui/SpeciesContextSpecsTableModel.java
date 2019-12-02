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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.AssignmentRule;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class SpeciesContextSpecsTableModel extends VCellSortTableModel<SpeciesContextSpec> implements java.beans.PropertyChangeListener {
	
	public class RulesProvenance implements Displayable {
		
		public static final String displayName = "RulesProvenance";
		public static final String typeName = "RulesProvenance";
		@Override
		public String getDisplayName() {
			return displayName;
		}
		@Override
		public String getDisplayType() {
			return typeName;
		}
	}
	
	public enum ColumnType {
		COLUMN_SPECIESCONTEXT("Species"),
		COLUMN_STRUCTURE("Structure"),
		COLUMN_DEPICTION("Depiction"),
		COLUMN_CLAMPED("Clamped"),
		COLUMN_RULES("Rules"),
		COLUMN_INITIAL("Initial Condition"),
		COLUMN_WELLMIXED("Well Mixed"),
		COLUMN_DIFFUSION("Diffusion Constant"),
		COLUMN_FORCECONTINUOUS("Force Continuous");
		
		public final String label;
		private ColumnType(String label){
			this.label = label;
		}
	}
	
	ArrayList<ColumnType> columns = new ArrayList<ColumnType>();
	
	private SimulationContext fieldSimulationContext = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public SpeciesContextSpecsTableModel(ScrollTable table) {
	super(table);
	refreshColumns();
}

private String searchText;
public void setSearchText(String searchText){
	this.searchText = searchText;
	refreshData();
}

protected List<SpeciesContextSpec> computeData() {
	ArrayList<SpeciesContextSpec> allParameterList = new ArrayList<SpeciesContextSpec>();
	if(getSimulationContext() != null){
		allParameterList.addAll(Arrays.asList(getSimulationContext().getReactionContext().getSpeciesContextSpecs()));
	}else{
		return null;
	}
	boolean bSearchInactive = searchText == null || searchText.length() == 0;
	if(bSearchInactive){
		return allParameterList;
	}
	String lowerCaseSearchText = bSearchInactive ? null : searchText.toLowerCase();
	ArrayList<SpeciesContextSpec> parameterList = new ArrayList<SpeciesContextSpec>();
	for (SpeciesContextSpec parameter : allParameterList) {
		if (bSearchInactive
			|| parameter.getSpeciesContext().getName().toLowerCase().contains(lowerCaseSearchText)
			/*|| parameter.getSpeciesContext().getStructure().getName().toLowerCase().contains(lowerCaseSearchText)*/) {
			parameterList.add(parameter);
		}
	}
	return parameterList;
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int column) {
	ColumnType columnType = columns.get(column);
	switch (columnType){
		case COLUMN_SPECIESCONTEXT:{
			return SpeciesContext.class;
		}
		case COLUMN_STRUCTURE:{
			return Structure.class;
		}
		case COLUMN_DEPICTION:{
			return SpeciesPattern.class;
		}
		case COLUMN_RULES:{
			return RulesProvenance.class;
		}
		case COLUMN_CLAMPED:
		case COLUMN_WELLMIXED:
		case COLUMN_FORCECONTINUOUS:{
			return Boolean.class;
		}
		case COLUMN_INITIAL:
		case COLUMN_DIFFUSION:{
			return ScopedExpression.class;
		}
		default:{
			return Object.class;
		}
	}
}

@Override
public String getColumnName(int columnIndex){
	return columns.get(columnIndex).label;
}

private void refreshColumns(){
	columns.clear();
	columns.addAll(Arrays.asList(ColumnType.values())); // initialize to all columns
	if (getSimulationContext() == null || !getSimulationContext().isStoch()){
		columns.remove(ColumnType.COLUMN_FORCECONTINUOUS);
	}
	if (getSimulationContext() == null || getSimulationContext().getGeometry().getDimension() == 0){
		columns.remove(ColumnType.COLUMN_WELLMIXED);
		columns.remove(ColumnType.COLUMN_DIFFUSION);
	}
	if (getSimulationContext() == null || getSimulationContext().isRuleBased()) {
		// the NFSim simulator used for rule-based doesn't accept clamped or force continuous
		columns.remove(ColumnType.COLUMN_CLAMPED);
		columns.remove(ColumnType.COLUMN_FORCECONTINUOUS);
	}
}
/**
 * getColumnCount method comment.
 */
@Override
public int getColumnCount() {
	return columns.size();
}

/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
private SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


private void refreshData() {
	List<SpeciesContextSpec> speciesContextSpecList = computeData();
	setData(speciesContextSpecList);
	GuiUtils.flexResizeTableColumns(ownerTable);
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {	
		SpeciesContextSpec scSpec = getValueAt(row);
		ColumnType columnType = columns.get(col);
		switch (columnType){
			case COLUMN_SPECIESCONTEXT:{
				return scSpec.getSpeciesContext();
			}
			case COLUMN_STRUCTURE:{
				return scSpec.getSpeciesContext().getStructure();
			}
			case COLUMN_DEPICTION:{
				return scSpec.getSpeciesContext().getSpeciesPattern();
			}
			case COLUMN_CLAMPED:{
				return new Boolean(scSpec.isConstant());
			}
			case COLUMN_RULES:{
				return null;
			}
			case COLUMN_WELLMIXED:{
				return (scSpec.isConstant() || scSpec.isWellMixed()) && !getSimulationContext().isStoch();
			}
			case COLUMN_INITIAL:{
				SpeciesContextSpecParameter initialConditionParameter = scSpec.getInitialConditionParameter();
				if(initialConditionParameter != null) {
					return new ScopedExpression(initialConditionParameter.getExpression(),initialConditionParameter.getNameScope(),  true, true, autoCompleteSymbolFilter);
				} else	{
					return null;
				}
			}
			case COLUMN_DIFFUSION:{
				SpeciesContextSpecParameter diffusionParameter = scSpec.getDiffusionParameter();
				if(diffusionParameter != null && !scSpec.isConstant() && scSpec.isWellMixed()!=null && !scSpec.isWellMixed()) 	{
					return new ScopedExpression(diffusionParameter.getExpression(),diffusionParameter.getNameScope(), true, true, autoCompleteSymbolFilter);
				} else {
					return null;
				}
			}
			case COLUMN_FORCECONTINUOUS:{
				return new Boolean(scSpec.isForceContinuous());
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
	SpeciesContextSpec speciesContextSpec = getValueAt(rowIndex);
	ColumnType columnType = columns.get(columnIndex);
	switch (columnType){
		case COLUMN_SPECIESCONTEXT:{
			return false;
		}
		case COLUMN_STRUCTURE:{
			return false;
		}
		case COLUMN_DEPICTION:{
			return false;
		}
		case COLUMN_CLAMPED:{
			return true;
		}
		case COLUMN_RULES:{
			return false;
		}
		case COLUMN_WELLMIXED:{
			return !speciesContextSpec.isConstant() && !getSimulationContext().isStoch();
		}
		case COLUMN_FORCECONTINUOUS:{
			return !speciesContextSpec.isConstant() && getSimulationContext().isStoch();
		}
		case COLUMN_INITIAL:{
//			RateRule rr = fieldSimulationContext.getRateRule(speciesContextSpec.getSpeciesContext());
			AssignmentRule ar = fieldSimulationContext.getAssignmentRule(speciesContextSpec.getSpeciesContext());
			if(/*rr != null || */ar != null) {
				return false;
			}
			return true;
		}
		case COLUMN_DIFFUSION: {
			return !speciesContextSpec.isConstant() && (!speciesContextSpec.isWellMixed() || getSimulationContext().isStoch());
		}
		default:{
			return false;
		}
	}
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	
	if (evt.getSource() instanceof ReactionContext && evt.getPropertyName().equals("speciesContextSpecs")) {
		updateListenersReactionContext((ReactionContext)evt.getSource(),true);
		updateListenersReactionContext((ReactionContext)evt.getSource(),false);
		refreshData();
	}
	if (evt.getSource() instanceof SpeciesContext && evt.getPropertyName().equals("name")) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if(evt.getSource() == getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_ASSIGNMENT_RULE_CHANGE)) {
		AssignmentRule oldRule = (AssignmentRule)evt.getOldValue();
		AssignmentRule newRule = (AssignmentRule)evt.getNewValue();
		onAssignmentRuleVariableChanged(oldRule, newRule);
	} else if(evt.getSource() == getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_RATE_RULE_CHANGE)) {
		RateRule oldRule = (RateRule)evt.getOldValue();
		RateRule newRule = (RateRule)evt.getNewValue();
		onRateRuleVariableChanged(oldRule, newRule);
	} else if(evt.getSource() == getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_ASSIGNMENTRULES)) {
		System.out.println(evt.getPropertyName());
		AssignmentRule[] oldRules = (AssignmentRule[])evt.getOldValue();
		AssignmentRule[] newRules = (AssignmentRule[])evt.getNewValue();
		if(oldRules != null && newRules != null && oldRules.length > newRules.length) {
			onAssignmentRuleDelete(oldRules, newRules);
		}
	} else if(evt.getSource() == getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_RATERULES)) {
		System.out.println(evt.getPropertyName());
		RateRule[] oldRules = (RateRule[])evt.getOldValue();
		RateRule[] newRules = (RateRule[])evt.getNewValue();
		if(oldRules != null && newRules != null && oldRules.length > newRules.length) {
			onRateRuleDelete(oldRules, newRules);
		}
	}
	if (evt.getSource() instanceof SpeciesContextSpec) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof SpeciesContextSpec.SpeciesContextSpecParameter) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof GeometryContext) {
		refreshColumns();
		fireTableStructureChanged();
	}
}

private void removeRuleVariableMark(SpeciesContext sc, boolean unclamp) {
	SpeciesContextSpec[] scss = fieldSimulationContext.getReactionContext().getSpeciesContextSpecs();
	for(SpeciesContextSpec scs : scss) {
		if(scs.getSpeciesContext() != null && scs.getSpeciesContext() == sc) {
			if(unclamp) {
				scs.setConstant(false);
			}
//			try {
//				if(getSimulationContext().isUsingConcentration()) {
//					scs.getInitialConcentrationParameter().setExpression(new Expression("0"));
//				} else {
//					scs.getInitialCountParameter().setExpression(new Expression("0"));
//				}
//			} catch(ExpressionException e) {
//				e.printStackTrace();
//			}
		}
	}
}
private void setRuleVariableMark(SpeciesContext sc, Expression ex, boolean clamp) {
	SpeciesContextSpec[] scss = fieldSimulationContext.getReactionContext().getSpeciesContextSpecs();
	for(SpeciesContextSpec scs : scss) {
		if(scs.getSpeciesContext() != null && scs.getSpeciesContext() == sc) {
			if(clamp) {
				scs.setConstant(true);
			}
//			try {
//				if(getSimulationContext().isUsingConcentration()) {
//					scs.getInitialConcentrationParameter().setExpression(new Expression(ex));
//				} else {
//					scs.getInitialCountParameter().setExpression(new Expression(ex));
//				}
//			} catch (ExpressionBindingException e) {
//				e.printStackTrace();
//			}
			fireTableRowsUpdated(0,getRowCount()-1);
			break;		// can't find more than one
		}
	}
}
private void onAssignmentRuleVariableChanged(AssignmentRule oldRule, AssignmentRule newRule) {
	if(oldRule != null && oldRule.getSimulationContext() == fieldSimulationContext && oldRule.getAssignmentRuleVar() != null) {
		SymbolTableEntry ste = oldRule.getAssignmentRuleVar();
		if(ste instanceof SpeciesContext) {
			SpeciesContext sc = (SpeciesContext)ste;
			removeRuleVariableMark(sc, true);
		}
	}
	if(newRule != null && newRule.getSimulationContext() == fieldSimulationContext && newRule.getAssignmentRuleVar() != null) {
		SymbolTableEntry ste = newRule.getAssignmentRuleVar();
		if(ste instanceof SpeciesContext) {
			SpeciesContext sc = (SpeciesContext)ste;
			Expression ex = newRule.getAssignmentRuleExpression();
			setRuleVariableMark(sc, ex, true);
		}
	}
}
private void onRateRuleVariableChanged(RateRule oldRule, RateRule newRule) {
	if(oldRule != null && oldRule.getSimulationContext() == fieldSimulationContext && oldRule.getRateRuleVar() != null) {
		SymbolTableEntry ste = oldRule.getRateRuleVar();
		if(ste instanceof SpeciesContext) {
			SpeciesContext sc = (SpeciesContext)ste;
			removeRuleVariableMark(sc, true);
		}
	}
	if(newRule != null && newRule.getSimulationContext() == fieldSimulationContext && newRule.getRateRuleVar() != null) {
		SymbolTableEntry ste = newRule.getRateRuleVar();
		if(ste instanceof SpeciesContext) {
			SpeciesContext sc = (SpeciesContext)ste;
			Expression ex = newRule.getRateRuleExpression();
			setRuleVariableMark(sc, ex, true);
		}
	}
}
private void onAssignmentRuleDelete(AssignmentRule[] oldRules, AssignmentRule[] newRules ) {
	System.out.println("old: " + oldRules.length + ", new: " + newRules.length);
	AssignmentRule deleted = null;
	for(AssignmentRule candidate : oldRules) {
		boolean found = false;
		for(AssignmentRule rule : newRules) {
			if(candidate.getName().equals(rule.getName())) {
				found = true;
				break;
			}
		}
		if(found == false) {
			deleted = candidate;
			break;
		}
	}
	if(deleted == null || deleted.getSimulationContext() != fieldSimulationContext) {
		return;
	}
	SymbolTableEntry ste = deleted.getAssignmentRuleVar();
	if(ste instanceof SpeciesContext) {
		SpeciesContext sc = (SpeciesContext)ste;
		removeRuleVariableMark(sc, true);
	}
}
private void onRateRuleDelete(RateRule[] oldRules, RateRule[] newRules ) {
	System.out.println("old: " + oldRules.length + ", new: " + newRules.length);
	RateRule deleted = null;
	for(RateRule candidate : oldRules) {
		boolean found = false;
		for(RateRule rule : newRules) {
			if(candidate.getName().equals(rule.getName())) {
				found = true;
				break;
			}
		}
		if(found == false) {
			deleted = candidate;
			break;
		}
	}
	if(deleted == null || deleted.getSimulationContext() != fieldSimulationContext) {
		return;
	}
	SymbolTableEntry ste = deleted.getRateRuleVar();
	if(ste instanceof SpeciesContext) {
		SpeciesContext sc = (SpeciesContext)ste;
		removeRuleVariableMark(sc, true);
	}
}

/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	int oldColumnCount = getColumnCount();
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		oldValue.getGeometryContext().removePropertyChangeListener(this);
		updateListenersReactionContext(oldValue.getReactionContext(),true);
	}
	fieldSimulationContext = simulationContext;
	refreshColumns();
	int newColumnCount = getColumnCount();
	if (oldColumnCount != newColumnCount) {
		fireTableStructureChanged();
	}
	if (simulationContext!=null){
		simulationContext.addPropertyChangeListener(this);
		simulationContext.getGeometryContext().addPropertyChangeListener(this);
		updateListenersReactionContext(simulationContext.getReactionContext(),false);
		
		autoCompleteSymbolFilter  = simulationContext.getAutoCompleteSymbolFilter();
		refreshData();
	}
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	SpeciesContextSpec scSpec = getValueAt(rowIndex);
	ColumnType columnType = columns.get(columnIndex);
	switch (columnType){
		case COLUMN_CLAMPED:{
			boolean bFixed = ((Boolean)aValue).booleanValue();
			if (bFixed){
				scSpec.setConstant(true);
			}else{
				scSpec.setConstant(false);
			}
			fireTableRowsUpdated(rowIndex,rowIndex);
			break;
		}
		case COLUMN_WELLMIXED:{
			boolean bWellMixed = ((Boolean)aValue).booleanValue();
			scSpec.setWellMixed(bWellMixed);
			fireTableRowsUpdated(rowIndex,rowIndex);
			break;
		}
		case COLUMN_FORCECONTINUOUS:{
			boolean bForceContinuous = ((Boolean)aValue).booleanValue();
			scSpec.setForceContinuous(bForceContinuous);
			fireTableRowsUpdated(rowIndex,rowIndex);
			break;
		}
		case COLUMN_INITIAL:{
			try {
				if (aValue instanceof String) {
					String newExpressionString = (String)aValue;
					if(getSimulationContext().isUsingConcentration())
					{
						scSpec.getInitialConcentrationParameter().setExpression(new Expression(newExpressionString));
					}
					else
					{
						scSpec.getInitialCountParameter().setExpression(new Expression(newExpressionString));
					}
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				//
				// don't handle exception here, InitialConditionsPanel needs it.
				//
				PopupGenerator.showErrorDialog(ownerTable, "Wrong Expression:\n" + e.getMessage());
				//throw new RuntimeException(e.getMessage());
			}
			break;
		}
		case COLUMN_DIFFUSION:{
			try {
				if (aValue instanceof String) {
					String newExpressionString = (String)aValue;
					scSpec.getDiffusionParameter().setExpression(new Expression(newExpressionString));
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				//
				// don't handle exception here, InitialConditionsPanel needs it.
				//
				PopupGenerator.showErrorDialog(ownerTable, "Wrong Expression:\n" + e.getMessage());
				//throw new RuntimeException(e.getMessage());
			}
			break;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2005 2:44:36 PM)
 */
private void updateListenersReactionContext(ReactionContext reactionContext,boolean bRemove) {

	if(bRemove){
		reactionContext.removePropertyChangeListener(this);
		SpeciesContextSpec oldSpecs[] = reactionContext.getSpeciesContextSpecs();
		for (int i=0;i<oldSpecs.length;i++){
			oldSpecs[i].removePropertyChangeListener(this);
			oldSpecs[i].getSpeciesContext().removePropertyChangeListener(this);
			Parameter oldParameters[] = oldSpecs[i].getParameters();
			for (int j = 0; j < oldParameters.length ; j++){
				oldParameters[j].removePropertyChangeListener(this);
			}
		}
	}else{
		reactionContext.addPropertyChangeListener(this);
		SpeciesContextSpec newSpecs[] = reactionContext.getSpeciesContextSpecs();
		for (int i=0;i<newSpecs.length;i++){
			newSpecs[i].addPropertyChangeListener(this);
			newSpecs[i].getSpeciesContext().addPropertyChangeListener(this);
			Parameter newParameters[] = newSpecs[i].getParameters();
			for (int j = 0; j < newParameters.length ; j++){
				newParameters[j].addPropertyChangeListener(this);
			}
		}
	}

}


public Comparator<SpeciesContextSpec> getComparator(final int col, final boolean ascending) {
	return new Comparator<SpeciesContextSpec>() {	
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(SpeciesContextSpec speciesContextSpec1, SpeciesContextSpec speciesContextSpec2){			
			
			SpeciesContext speciesContext1 = speciesContextSpec1.getSpeciesContext();
			SpeciesContext speciesContext2 = speciesContextSpec2.getSpeciesContext();			
			ColumnType columnType = columns.get(col);
			switch (columnType){
				case COLUMN_SPECIESCONTEXT:{
					String name1 = speciesContext1.getName();
					String name2 = speciesContext2.getName();
					if (ascending){
						return name1.compareToIgnoreCase(name2);
					}else{
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_STRUCTURE:{
					String name1 = speciesContext1.getStructure().getName();
					String name2 = speciesContext2.getStructure().getName();
					if (ascending){						
						return name1.compareToIgnoreCase(name2);
					}else{						
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_CLAMPED : {
					Boolean bClamped1 = new Boolean(speciesContextSpec1.isConstant());
					Boolean bClamped2 = new Boolean(speciesContextSpec2.isConstant());
					if (ascending){
						return bClamped1.compareTo(bClamped2);
					}else{
						return bClamped2.compareTo(bClamped1);
					}
				}
				case COLUMN_FORCECONTINUOUS : {
					Boolean bForceContinuous1 = new Boolean(speciesContextSpec1.isForceContinuous());
					Boolean bForceContinuous2 = new Boolean(speciesContextSpec2.isForceContinuous());
					if (ascending){
						return bForceContinuous1.compareTo(bForceContinuous2);
					}else{
						return bForceContinuous2.compareTo(bForceContinuous1);
					}
				}
				case COLUMN_WELLMIXED : {
					Boolean bWellMixed1 = new Boolean(speciesContextSpec1.isWellMixed());
					Boolean bWellMixed2 = new Boolean(speciesContextSpec2.isWellMixed());
					if (ascending){
						return bWellMixed1.compareTo(bWellMixed2);
					}else{
						return bWellMixed2.compareTo(bWellMixed1);
					}
				}
				case COLUMN_INITIAL: {
					Expression initExp1 = speciesContextSpec1.getInitialConditionParameter().getExpression();
					Expression initExp2 = speciesContextSpec2.getInitialConditionParameter().getExpression();
					return TableUtil.expressionCompare(initExp1, initExp2, ascending);
				}
				case COLUMN_DIFFUSION: {
					Expression diffExp1 = speciesContextSpec1.getDiffusionParameter().getExpression();
					Expression diffExp2 = speciesContextSpec2.getDiffusionParameter().getExpression();
					return TableUtil.expressionCompare(diffExp1, diffExp2, ascending);
				}	
			}
			return 1;
		}
	};
}

public static class TableUtil {
	// detects whether expressions within this column contain numbers, alphanumeric expressions or a mix
	// and sorts accordingly (numbers first (sorted numerically), alphanumeric expr next (sorted alphabetically w. ignore case))
	public static int expressionCompare(Expression e1, Expression e2, boolean ascending) {
		if(e1 == null || e2 == null) {
			return 0;
		}
		if(e1.isNumeric() && !e2.isNumeric()) {
			if (ascending) {
				return -1;
			} else {
				return 1;
			}
		} else if(!e1.isNumeric() && e2.isNumeric()) {
			if (ascending) {
				return 1;
			} else {
				return -1;
			}
		} else if(!e1.isNumeric() && !e2.isNumeric()) {		// both are not-numbers
			String infix1 = (e1!=null)?(e1.infix()):("");
			String infix2 = (e2!=null)?(e2.infix()):("");
			if (ascending){
				return infix1.compareToIgnoreCase(infix2);
			}else{
				return infix2.compareToIgnoreCase(infix1);
			}

		} else {	// both are numbers
			String infix1 = (e1!=null)?(e1.infix()):("");
			Float f1 = Float.valueOf(infix1);
			String infix2 = (e2!=null)?(e2.infix()):("");
			Float f2 = Float.valueOf(infix2);
			if(ascending) {
				return f1.compareTo(f2);
			} else {
				return f2.compareTo(f1);
			}
		}
	}
}

}
