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

import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.ModelProcessSpec;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.ReactionRuleSpec.ReactionRuleMappingType;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.ModelProcess;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ModelProcessSpecsTableModel extends VCellSortTableModel<ModelProcessSpec> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_TYPE = 1;
	public static final int COLUMN_ENABLED = 2;
	public static final int COLUMN_FAST = 3;
	private final static String LABELS[] = { "Name", "Type", "Enabled", "Fast" };
	private SimulationContext fieldSimulationContext = null;
	
/**
 * ModelProcessSpecsTableModel constructor comment.
 */
public ModelProcessSpecsTableModel(ScrollTable table) {
	super(table, LABELS);
	addPropertyChangeListener(this);
}

private String searchText;
public void setSearchText(String searchText){
	this.searchText = searchText;
	refreshData();
}

protected List<ModelProcessSpec> computeData() {
	ArrayList<ModelProcessSpec> allParameterList = new ArrayList<ModelProcessSpec>();
	if(getSimulationContext() != null){
		allParameterList.addAll(Arrays.asList(getSimulationContext().getReactionContext().getReactionSpecs()));
		allParameterList.addAll(Arrays.asList(getSimulationContext().getReactionContext().getReactionRuleSpecs()));
	}else{
		return null;
	}
	boolean bSearchInactive = searchText == null || searchText.length() == 0;
	if(bSearchInactive){
		return allParameterList;
	}
	String lowerCaseSearchText = bSearchInactive ? null : searchText.toLowerCase();
	ArrayList<ModelProcessSpec> parameterList = new ArrayList<ModelProcessSpec>();
	for (ModelProcessSpec parameter : allParameterList) {
		if (bSearchInactive
			|| parameter.getModelProcess().getName().toLowerCase().contains(lowerCaseSearchText)
			|| parameter.getModelProcess().getStructure().getName().toLowerCase().contains(lowerCaseSearchText)
				/*|| parameter.getNameScope().getPathDescription().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getName().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getExpression() != null && parameter.getExpression().infix().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getDescription().toLowerCase().contains(lowerCaseSearchText)*/) {
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
	switch (column){
		case COLUMN_NAME:{
			return ModelProcess.class;
		}
		case COLUMN_TYPE:{
			return String.class;
		}
		case COLUMN_ENABLED:{
			return Boolean.class;
		}
		case COLUMN_FAST:{
			return Boolean.class;
		}
		default:{
			return Object.class;
		}
	}
}

@Override
public int getColumnCount() {
	if (fieldSimulationContext == null || !fieldSimulationContext.isStoch()) {
		return super.getColumnCount();
	}
	return super.getColumnCount() - 1;
}

/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	ModelProcessSpec modelProcessSpec = getValueAt(row);
	switch (col){
		case COLUMN_NAME:{
			return modelProcessSpec.getModelProcess();
		}
		case COLUMN_TYPE:{
			return modelProcessSpec.getModelProcess().getDisplayType();
		}
		case COLUMN_ENABLED:{
			return new Boolean(!modelProcessSpec.isExcluded());
		}
		case COLUMN_FAST:{
			if (!(modelProcessSpec.getModelProcess() instanceof SimpleReaction)){
				return new Boolean(false);
			}else{
				return new Boolean(modelProcessSpec.isFast());
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
	if (columnIndex == COLUMN_ENABLED){
		return true;
	}else if (columnIndex == COLUMN_FAST && getSimulationContext()!=null){
		ModelProcessSpec ModelProcessSpec = getValueAt(rowIndex);
		//
		// the "fast" column is only editable if not FluxReaction
		//
		return (ModelProcessSpec.getModelProcess() instanceof SimpleReaction);
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
	if (evt.getSource() == this && evt.getPropertyName().equals("simulationContext")) {
		refreshData();
		fireTableStructureChanged();
	}
	if (evt.getSource() instanceof ReactionContext && evt.getPropertyName().equals("ModelProcessSpecs")) {
		refreshData();
	}
	if (evt.getSource() instanceof ReactionContext && evt.getPropertyName().equals("reactionRuleSpecs")) {
		refreshData();
	}
	if(evt.getSource() instanceof ReactionRule) {
		System.out.println("Reaction Rule event, property " + evt.getPropertyName());
		refreshData();
	}
	if (evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("name")) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof ModelProcessSpec) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
}

/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		ReactionContext reactionContext = oldValue.getReactionContext();
		reactionContext.removePropertyChangeListener(this);
		ReactionSpec oldReactionSpecs[] = reactionContext.getReactionSpecs();
		for (int i=0;i<oldReactionSpecs.length;i++){
			oldReactionSpecs[i].getReactionStep().removePropertyChangeListener(this);
			oldReactionSpecs[i].removePropertyChangeListener(this);
		}
		ReactionRuleSpec oldReactionRuleSpecs[] = reactionContext.getReactionRuleSpecs();
		for (int i=0;i<oldReactionRuleSpecs.length;i++){
			oldReactionRuleSpecs[i].getReactionRule().removePropertyChangeListener(this);
			oldReactionRuleSpecs[i].removePropertyChangeListener(this);
		}
	}
	fieldSimulationContext = simulationContext;
	if (simulationContext!=null){
		simulationContext.addPropertyChangeListener(this);
		ReactionContext reactionContext = fieldSimulationContext.getReactionContext();
		reactionContext.addPropertyChangeListener(this);
		ReactionSpec newReactionSpecs[] = reactionContext.getReactionSpecs();
		for (int i=0;i<newReactionSpecs.length;i++){
			newReactionSpecs[i].getReactionStep().addPropertyChangeListener(this);
			newReactionSpecs[i].addPropertyChangeListener(this);
		}
		ReactionRuleSpec newReactionRuleSpecs[] = reactionContext.getReactionRuleSpecs();
		for (int i=0;i<newReactionRuleSpecs.length;i++){
			newReactionRuleSpecs[i].getReactionRule().addPropertyChangeListener(this);
			newReactionRuleSpecs[i].addPropertyChangeListener(this);
		}
	}
	firePropertyChange("simulationContext", oldValue, simulationContext);
}

private void refreshData() {
	List<ModelProcessSpec> rslist = computeData();
	setData(rslist);
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	ModelProcessSpec modelProcessSpec = getValueAt(rowIndex);
	try {
		switch (columnIndex){
			case COLUMN_ENABLED:{
				boolean bEnabled = ((Boolean)aValue).booleanValue();
				if (modelProcessSpec instanceof ReactionSpec){
					ReactionSpec reactionSpec = (ReactionSpec)modelProcessSpec;
					if (bEnabled){
						reactionSpec.setReactionMapping(ReactionSpec.INCLUDED);
					}else{
						reactionSpec.setReactionMapping(ReactionSpec.EXCLUDED);
					}
				}else if (modelProcessSpec instanceof ReactionRuleSpec){
					ReactionRuleSpec reactionRuleSpec = (ReactionRuleSpec)modelProcessSpec;
					if (bEnabled){
						reactionRuleSpec.setReactionRuleMapping(ReactionRuleMappingType.INCLUDED);
					}else{
						reactionRuleSpec.setReactionRuleMapping(ReactionRuleMappingType.EXCLUDED);
					}
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
				break;
			}
			case COLUMN_FAST:{
				boolean bFast = ((Boolean)aValue).booleanValue();
				if (modelProcessSpec instanceof ReactionSpec){
					ReactionSpec reactionSpec = (ReactionSpec)modelProcessSpec;
					if (bFast){
						reactionSpec.setReactionMapping(ReactionSpec.FAST);
					}else{
						reactionSpec.setReactionMapping(ReactionSpec.INCLUDED);
					}
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
				break;
			}
		}
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
	}
}

	public Comparator<ModelProcessSpec> getComparator(int col, boolean ascending) {
		return new ModelProcessSpecComparator(col, ascending);
	}

private class ModelProcessSpecComparator implements Comparator<ModelProcessSpec> {
	protected int index;
	protected boolean ascending;

	public ModelProcessSpecComparator(int index, boolean ascending){
		this.index = index;
		this.ascending = ascending;
	}
	
	public int compare(ModelProcessSpec parm1, ModelProcessSpec parm2){
		
		switch (index) {
			case COLUMN_NAME:{
				int bCompare = parm1.getModelProcess().getName().compareToIgnoreCase(parm2.getModelProcess().getName());
				return ascending ? bCompare : -bCompare;
			}
			case COLUMN_TYPE:{
				String type1 = parm1.getModelProcess().getDisplayType();
				String type2 = parm2.getModelProcess().getDisplayType();
				int bCompare = type1.compareTo(type2);
				return ascending ? bCompare : -bCompare;
			}
			case COLUMN_ENABLED:{
				int bCompare = new Boolean(!parm1.isExcluded()).compareTo(new Boolean(!parm2.isExcluded()));
				return ascending ? bCompare : -bCompare;
			}
			case COLUMN_FAST:{
				Boolean fast1 = (parm1.isFast() && (parm1.getModelProcess() instanceof SimpleReaction)) ? true : false;
				Boolean fast2 = (parm2.isFast() && (parm2.getModelProcess() instanceof SimpleReaction)) ? true : false;
				int bCompare = fast1.compareTo(fast2);
				return ascending ? bCompare : -bCompare;
			}
		}
		return 1;
	}
}

}
