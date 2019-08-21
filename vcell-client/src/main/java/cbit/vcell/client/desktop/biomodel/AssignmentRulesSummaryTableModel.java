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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.util.Displayable;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.mapping.AssignmentRule;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

@SuppressWarnings("serial")
public class AssignmentRulesSummaryTableModel extends BioModelEditorApplicationRightSideTableModel<AssignmentRule> implements PropertyChangeListener{

	public final static int COLUMN_ASSIGNMENTRULE_NAME = 0;
	public final static int COLUMN_ASSIGNMENTRULE_VAR = 1;
	public final static int COLUMN_ASSIGNMENTRULE_TYPE = 2;
	public final static int COLUMN_ASSIGNMENTRULE_EXPR = 3;
	
	private static String[] columnNames = new String[] {"Name", "Variable", "Type", "Expression"};

	public AssignmentRulesSummaryTableModel(ScrollTable table) {
		super(table, columnNames);
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_ASSIGNMENTRULE_NAME:{
				return String.class;
			}
			case COLUMN_ASSIGNMENTRULE_VAR:{
				return String.class;
			}
			case COLUMN_ASSIGNMENTRULE_EXPR:{
				return ScopedExpression.class;
			}
			case COLUMN_ASSIGNMENTRULE_TYPE:{
				return String.class;
			}
			default:{
				return Object.class;
			}
		}
	}

	protected List<AssignmentRule> computeData() {
		if (simulationContext == null || simulationContext.getAssignmentRules() == null){
			return null;
		}
		List<AssignmentRule> assignmentRulesList = new ArrayList<AssignmentRule>();
		for (AssignmentRule assignmentRule : simulationContext.getAssignmentRules()) {
			if (searchText == null || searchText.length() == 0) {
				assignmentRulesList.add(assignmentRule);
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();	
				if (assignmentRule.getName().toLowerCase().contains(lowerCaseSearchText) ||
						assignmentRule.getAssignmentRuleExpression() != null && assignmentRule.getAssignmentRuleExpression().infix().toLowerCase().contains(lowerCaseSearchText)) {
					assignmentRulesList.add(assignmentRule);
				}
			}
		}
		return assignmentRulesList;
	}

	public Object getValueAt(int row, int column) {
		try{
			AssignmentRule assignmentRule = getValueAt(row);
			if (assignmentRule != null) {
				switch (column) {
					case COLUMN_ASSIGNMENTRULE_NAME: {
						return assignmentRule.getName();
					} 
					case COLUMN_ASSIGNMENTRULE_VAR: {
						if(assignmentRule.getAssignmentRuleVar() == null){
							return null;
						} else {
							return assignmentRule.getAssignmentRuleVar().getName();
						}
					}	
					case COLUMN_ASSIGNMENTRULE_EXPR: {
						if(assignmentRule.getAssignmentRuleExpression() == null) {
							return null; 
						} else {
							ScopedExpression se = new ScopedExpression(assignmentRule.getAssignmentRuleExpression(), simulationContext.getModel().getNameScope());
							return se;
						}
					}
					case COLUMN_ASSIGNMENTRULE_TYPE: {
						SymbolTableEntry sto = assignmentRule.getAssignmentRuleVar();
						if(sto instanceof Displayable) {
							return ((Displayable)sto).getDisplayType();
						} else {
							return "Unknown";
						}
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//return false;
		//Make none of the fields editable until code for adding new  rules is fixed. 10/20/2014
		switch (columnIndex) {
		case COLUMN_ASSIGNMENTRULE_NAME:
		case COLUMN_ASSIGNMENTRULE_VAR:
		case COLUMN_ASSIGNMENTRULE_EXPR:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
		if (evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_ASSIGNMENTRULES)) {
			AssignmentRule[] oldValue = (AssignmentRule[])evt.getOldValue();
			if (oldValue != null) {			
				for (AssignmentRule rr : oldValue) {
					rr.removePropertyChangeListener(this);						
				}
			}
			AssignmentRule[] newValue = (AssignmentRule[])evt.getNewValue();
			if (newValue != null) {			
				for (AssignmentRule rr : newValue) {
					rr.addPropertyChangeListener(this);						
				}
			}
		}
		refreshData();
	}
	
	public void setValueAt(Object value, int row, int column) {
		try{
//			if (value == null || value.toString().length() == 0 || BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT.equals(value)) {
//				return;
//			}
			AssignmentRule assignmentRule = getValueAt(row);
			if (assignmentRule == null) {
				assignmentRule = simulationContext.createAssignmentRule(null);
			} else {
				assignmentRule = getValueAt(row);
			}
			switch (column) {
				case COLUMN_ASSIGNMENTRULE_NAME:
					/** @author anu : TODO :  RULES */
					assignmentRule.setName((String)value);
					break;
				case COLUMN_ASSIGNMENTRULE_VAR:
					String var = (String)value;
					SymbolTableEntry ste = simulationContext.getModel().getEntry(var);
					assignmentRule.setAssignmentRuleVar(ste);
					break;
				case COLUMN_ASSIGNMENTRULE_EXPR:
					Expression exp = new Expression((String)value);
					assignmentRule.setAssignmentRuleExpression(exp);
					assignmentRule.bind();
					break;
				case COLUMN_ASSIGNMENTRULE_TYPE:
					return;
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage());
		}
	}

	public Comparator<AssignmentRule> getComparator(int col, boolean ascending) {
		return null;
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}

	public String checkInputValue(String inputValue, int row, int column) {
		AssignmentRule assignmentRule = getValueAt(row);
		switch (column) {
		case COLUMN_ASSIGNMENTRULE_NAME: {
			if (assignmentRule == null || !assignmentRule.getName().equals(inputValue)) {
				if (simulationContext.getAssignmentRule(inputValue) != null) {
					return "A assignmentRule with name '" + inputValue + "' already exists!";
				}
				if (simulationContext.getModel().getReservedSymbolByName(inputValue) != null) {
					return "Cannot use reserved symbol '" + inputValue + "' as an event name";
				}
			}
		}
		}
		return null;
	}

	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}

	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		return null;
	}
	
	@Override
	public int getRowCount() {
		// -1 added 10/20/2014 to suppress extra row for adding new rule until adding  rules framework is fixed.  Had been return getRowCountWithAddNew();
		// return getRowCountWithAddNew()-1;
		return super.getRowCount();
	}
}
