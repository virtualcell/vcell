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

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.vcell.util.Displayable;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.mapping.AssignmentRule;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

@SuppressWarnings("serial")
public class AssignmentRulesSummaryTableModel extends BioModelEditorApplicationRightSideTableModel<AssignmentRule> implements PropertyChangeListener{

	JComboBox<String> steComboBox = null;

	public final static int COLUMN_ASSIGNMENTRULE_NAME = 0;
	public final static int COLUMN_ASSIGNMENTRULE_VAR = 1;
	public final static int COLUMN_ASSIGNMENTRULE_TYPE = 2;
	public final static int COLUMN_ASSIGNMENTRULE_EXPR = 3;
	
	private static String[] columnNames = new String[] {"Name", "Variable", "Type", "Assignment Function Expression"};

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

	private void updateSubdomainComboBox() {
		if(steComboBox == null) {
			steComboBox = new JComboBox<> ();
		}
		if(simulationContext == null) {
			return;
		}
		
		SpeciesContext[] scs = simulationContext.getModel().getSpeciesContexts();
		ModelParameter[] mps = simulationContext.getModel().getModelParameters();
		DefaultComboBoxModel<String> aModel = new DefaultComboBoxModel<> ();
		for (SpeciesContext sc : scs) {
			aModel.addElement(sc.getName());
		}
		for (ModelParameter mp : mps) {
			aModel.addElement(mp.getName());
		}
		steComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusGained(java.awt.event.FocusEvent evt) {
				DefaultComboBoxModel<String> aModel = (DefaultComboBoxModel<String>) steComboBox.getModel();
				SpeciesContext[] scs = simulationContext.getModel().getSpeciesContexts();
				ModelParameter[] mps = simulationContext.getModel().getModelParameters();
				for (SpeciesContext sc : scs) {
					int position = aModel.getIndexOf(sc.getName());
					if(position == -1) {	// element is missing, add it
						aModel.addElement(sc.getName());
					}
				}
				for (ModelParameter mp : mps) {
					int position = aModel.getIndexOf(mp.getName());
					if(position == -1) {
						aModel.addElement(mp.getName());
					}
				}
				Set<String> elementsToRemove = new HashSet<> ();
				for(int i=0; i<aModel.getSize(); i++) {
					String candidate = aModel.getElementAt(i);
					SpeciesContext sc = simulationContext.getModel().getSpeciesContext(candidate);
					ModelParameter mp = simulationContext.getModel().getModelParameter(candidate);
					if(sc == null && mp == null) {
						elementsToRemove.add(candidate);
					}
				}
				for(String candidate : elementsToRemove) {
					aModel.removeElement(candidate);
				}
			}
		});
		
		steComboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setHorizontalTextPosition(SwingConstants.LEFT);
				if(value == null) {
					setText("");
				} else if (value instanceof String) {
					setText((String)value);
				} else {
					setText(value.toString());
					System.out.println("not String");
				}
				return this;
			}
		});
		
		steComboBox.setModel(aModel);
		ownerTable.getColumnModel().getColumn(COLUMN_ASSIGNMENTRULE_VAR).setCellEditor(new DefaultCellEditor(steComboBox));
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
						SymbolTableEntry ste = assignmentRule.getAssignmentRuleVar();
						if(ste != null) {
							return ste.getName();
						} else {
							return "";
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
		if(evt.getSource() == simulationContext && evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_ENTITY_NAME)) {
			String oldName = (String)evt.getOldValue();
			String newName = (String)evt.getNewValue();
			
			boolean replaced = false;
			for(int i=0; simulationContext.getAssignmentRules() != null && i<simulationContext.getAssignmentRules().length; i++) {
				AssignmentRule rule = simulationContext.getAssignmentRules()[i];
				Expression exp = rule.getAssignmentRuleExpression();
				if(exp == null || exp.getSymbols() == null || exp.getSymbols().length == 0) {
					continue;
				}
				String errMsg = "Failed to rename symbol '" + oldName + "' with '" + newName + "' in the Expression of " + AssignmentRule.typeName + " '" + rule.getDisplayName() + "'.";
				for(String symbol : exp.getSymbols()) {
					if(symbol.contentEquals(oldName)) {
						try {
							exp.substituteInPlace(new Expression(oldName), new Expression(newName));
							replaced = true;
						} catch (ExpressionException e) {
							e.printStackTrace();
							throw new RuntimeException(errMsg);
						}
					}
				}
				try {
					rule.bind();
				} catch (ExpressionBindingException e) {
					e.printStackTrace();
					throw new RuntimeException(errMsg);
				}
			}
		}
		refreshData();
	}
	
	public void setValueAt(Object value, int row, int column) {
		try {
			AssignmentRule assignmentRule = getValueAt(row);
			if (assignmentRule == null) {
				assignmentRule = simulationContext.createAssignmentRule(null);
			} else {
				assignmentRule = getValueAt(row);
			}
			switch (column) {
				case COLUMN_ASSIGNMENTRULE_NAME:
					assignmentRule.setName((String)value);
					break;
				case COLUMN_ASSIGNMENTRULE_VAR:
					if(value instanceof String) {
						String var = (String)value;
						SymbolTableEntry newVar = simulationContext.getModel().getEntry(var);
						SymbolTableEntry oldVar = assignmentRule.getAssignmentRuleVar();
						AssignmentRule oldRule = new AssignmentRule(null, oldVar, null, simulationContext);	// dummy rule, just for the var
						assignmentRule.setAssignmentRuleVar(newVar);
						if(simulationContext != null && newVar != null) {	// broadcast the change
							// the event generated by the simContext used to clamp the SpeciesContextSpec
							simulationContext.fireAssignmentRuleChanged(oldRule, assignmentRule);
						}
					}
					break;
				case COLUMN_ASSIGNMENTRULE_EXPR:
					SymbolTableEntry ste = null;
					if(assignmentRule.getAssignmentRuleVar() != null) {
						ste = simulationContext.getModel().getEntry(assignmentRule.getAssignmentRuleVar().getName());
					}
					Expression exp = new Expression((String)value);
					assignmentRule.setAssignmentRuleExpression(exp);
					assignmentRule.bind();
					if(simulationContext != null && ste != null) {	// broadcast the change
						// the event generated by the simContext used to clamp the SpeciesContextSpec
						simulationContext.fireAssignmentRuleChanged(null, assignmentRule);
					}
					break;
				case COLUMN_ASSIGNMENTRULE_TYPE:
					return;
			}
		} catch(Exception e) {
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

	@Override
	public void setSimulationContext(SimulationContext newValue) {
		if(simulationContext == newValue) {
			return;
		}
		super.setSimulationContext(newValue);
		updateSubdomainComboBox();
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
