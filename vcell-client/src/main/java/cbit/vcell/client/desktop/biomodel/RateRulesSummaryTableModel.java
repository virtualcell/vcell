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
import cbit.vcell.mapping.RateRule;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

@SuppressWarnings("serial")
public class RateRulesSummaryTableModel extends BioModelEditorApplicationRightSideTableModel<RateRule> implements PropertyChangeListener{

	public final static int COLUMN_RATERULE_NAME = 0;
	public final static int COLUMN_RATERULE_VAR = 1;
	public final static int COLUMN_RATERULE_TYPE = 2;
	public final static int COLUMN_RATERULE_EXPR = 3;
	
	private static String[] columnNames = new String[] {"Name", "Variable", "Type", "Expression"};

	public RateRulesSummaryTableModel(ScrollTable table) {
		super(table, columnNames);
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_RATERULE_NAME:{
				return String.class;
			}
			case COLUMN_RATERULE_VAR:{
				return String.class;
			}
			case COLUMN_RATERULE_EXPR:{
				return ScopedExpression.class;
			}
			case COLUMN_RATERULE_TYPE:{
				return String.class;
			}
			default:{
				return Object.class;
			}
		}
	}

	protected List<RateRule> computeData() {
		if (simulationContext == null || simulationContext.getRateRules() == null){
			return null;
		}
		List<RateRule> rateRulesList = new ArrayList<RateRule>();
		for (RateRule rateRule : simulationContext.getRateRules()) {
			if (searchText == null || searchText.length() == 0) {
				rateRulesList.add(rateRule);
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();	
				if (rateRule.getName().toLowerCase().contains(lowerCaseSearchText) ||
					rateRule.getRateRuleExpression() != null && rateRule.getRateRuleExpression().infix().toLowerCase().contains(lowerCaseSearchText)) {
					rateRulesList.add(rateRule);
				}
			}
		}
		return rateRulesList;
	}

	public Object getValueAt(int row, int column) {
		try{
			RateRule rateRule = getValueAt(row);
			if (rateRule != null) {
				switch (column) {
					case COLUMN_RATERULE_NAME: {
						return rateRule.getName();
					} 
					case COLUMN_RATERULE_VAR: {
						if (rateRule.getRateRuleVar() == null){
							return null;
						} else {
							return rateRule.getRateRuleVar().getName();
						}
					}	
					case COLUMN_RATERULE_EXPR: {
						if (rateRule.getRateRuleExpression() == null) {
							return null; 
						} else {
							return new ScopedExpression(rateRule.getRateRuleExpression(), simulationContext.getModel().getNameScope());
						}
					}
					case COLUMN_RATERULE_TYPE: {
						SymbolTableEntry sto = rateRule.getRateRuleVar();
						if(sto instanceof Displayable) {
							return ((Displayable)sto).getDisplayType();
						} else {
							return "Unknown";
						}
					}
				}
			} else {
				if (column == COLUMN_RATERULE_NAME) {
					return BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT;
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//return false;
		//Make none of the fields editable until code for adding new rate rules is fixed. 10/20/2014
		switch (columnIndex) {
		case COLUMN_RATERULE_NAME:
		case COLUMN_RATERULE_VAR:
		case COLUMN_RATERULE_EXPR:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
		if (evt.getPropertyName().equals("rateRules")) {
			RateRule[] oldValue = (RateRule[])evt.getOldValue();
			if (oldValue != null) {			
				for (RateRule rr : oldValue) {
					rr.removePropertyChangeListener(this);						
				}
			}
			RateRule[] newValue = (RateRule[])evt.getNewValue();
			if (newValue != null) {			
				for (RateRule rr : newValue) {
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
			RateRule rateRule = getValueAt(row);
			if (rateRule == null) {
				rateRule = simulationContext.createRateRule(null);
			} else {
				rateRule = getValueAt(row);
			}
			switch (column) {
				case COLUMN_RATERULE_NAME:
					/** @author anu : TODO : RATE RULES */
					rateRule.setName((String)value);
					break;
				case COLUMN_RATERULE_VAR:
					String var = (String)value;
					SymbolTableEntry ste = simulationContext.getModel().getEntry(var);
					rateRule.setRateRuleVar(ste);
					break;
				case COLUMN_RATERULE_EXPR:
					Expression exp = new Expression((String)value);
					rateRule.setRateRuleExpression(exp);
					rateRule.bind();
					break;
				case COLUMN_RATERULE_TYPE:
					return;
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage());
		}
	}

	public Comparator<RateRule> getComparator(int col, boolean ascending) {
		return null;
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}

	public String checkInputValue(String inputValue, int row, int column) {
		RateRule rateRule = getValueAt(row);
		switch (column) {
		case COLUMN_RATERULE_NAME: {
			if (rateRule == null || !rateRule.getName().equals(inputValue)) {
				if (simulationContext.getRateRule(inputValue) != null) {
					return "A rateRule with name '" + inputValue + "' already exists!";
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
		// -1 added 10/20/2014 to suppress extra row for adding new rule until adding rate rules framework is fixed.  Had been return getRowCountWithAddNew();
		// return getRowCountWithAddNew()-1;
		return super.getRowCount();
	}
}
