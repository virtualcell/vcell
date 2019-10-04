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
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

@SuppressWarnings("serial")
public class RateRulesSummaryTableModel extends BioModelEditorApplicationRightSideTableModel<RateRule> implements PropertyChangeListener {
	
	JComboBox<String> steComboBox = null;

	public final static int COLUMN_RATERULE_NAME = 0;
	public final static int COLUMN_RATERULE_VAR = 1;
	public final static int COLUMN_RATERULE_TYPE = 2;
	public final static int COLUMN_RATERULE_EXPR = 3;
	
	private static String[] columnNames = new String[] {"Name", "Variable", "Type", "Rate Expression"};

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
		steComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
			}
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
			}
		});
		
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
		ownerTable.getColumnModel().getColumn(COLUMN_RATERULE_VAR).setCellEditor(new DefaultCellEditor(steComboBox));
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
						SymbolTableEntry ste = rateRule.getRateRuleVar();
						if(ste != null) {
							return ste.getName();
						} else {
							return "";
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
						SymbolTableEntry ste = rateRule.getRateRuleVar();
						if(ste instanceof Displayable) {
							return ((Displayable)ste).getDisplayType();
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
		try {
			RateRule rateRule = getValueAt(row);
			if (rateRule == null) {
				rateRule = simulationContext.createRateRule(null);
			} else {
				rateRule = getValueAt(row);
			}
			switch (column) {
				case COLUMN_RATERULE_NAME:
					rateRule.setName((String)value);
					break;
				case COLUMN_RATERULE_VAR:
					if(value instanceof String) {
						String var = (String)value;
						SymbolTableEntry newVar = simulationContext.getModel().getEntry(var);
						SymbolTableEntry oldVar = rateRule.getRateRuleVar();
						RateRule oldRule = new RateRule(null, oldVar, null, simulationContext);	// dummy rule to propagate the old var
						rateRule.setRateRuleVar(newVar);
						if(simulationContext != null && newVar != null) {	// broadcast the change
							simulationContext.fireRateRuleChanged(oldRule, rateRule);
						}
					}
					break;
				case COLUMN_RATERULE_EXPR:
					SymbolTableEntry ste = null;
					if(rateRule.getRateRuleVar() != null) {
						ste = simulationContext.getModel().getEntry(rateRule.getRateRuleVar().getName());
					}
					Expression exp = new Expression((String)value);
					rateRule.setRateRuleExpression(exp);
					rateRule.bind();
					if(simulationContext != null && ste != null) {	// broadcast the change
						simulationContext.fireRateRuleChanged(null, rateRule);
					}
					break;
				case COLUMN_RATERULE_TYPE:
					return;
			}
		} catch(Exception e) {
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

	@Override
	public void setSimulationContext(SimulationContext newValue) {
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
		// -1 added 10/20/2014 to suppress extra row for adding new rule until adding rate rules framework is fixed.  Had been return getRowCountWithAddNew();
		// return getRowCountWithAddNew()-1;
		return super.getRowCount();
	}
}
