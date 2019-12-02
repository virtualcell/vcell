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

import org.vcell.util.Issue;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.model.Model;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class EventsSummaryTableModel extends BioModelEditorApplicationRightSideTableModel<BioEvent> implements PropertyChangeListener{

	public final static int COLUMN_EVENT_NAME = 0;
	public final static int COLUMN_EVENT_TRIGGER_DESCRIPTION = 1;
	public final static int COLUMN_EVENT_ACTIONS = 2;
	
	private static String[] columnNames = new String[] {"Event Name", "Trigger Condition", "Actions"};

	public EventsSummaryTableModel(ScrollTable table) {
		super(table, columnNames);
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_EVENT_NAME:{
				return String.class;
			}
			case COLUMN_EVENT_TRIGGER_DESCRIPTION:{
				return String.class;
			}
			case COLUMN_EVENT_ACTIONS:{
				return String.class;
			}
			default:{
				return Object.class;
			}
		}
	}

	protected List<BioEvent> computeData() {
		if (simulationContext == null || simulationContext.getBioEvents() == null){
			return null;
		}
		List<BioEvent> bioEventList = new ArrayList<BioEvent>();
		for (BioEvent bioEvent : simulationContext.getBioEvents()) {
			if (searchText == null || searchText.length() == 0) {
				bioEventList.add(bioEvent);
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();
				try{
					Expression delayExp = bioEvent.getParameter(BioEventParameterType.TriggerDelay).getExpression();
					Expression generatedTriggerExp = bioEvent.generateTriggerExpression();
					if (bioEvent.getName().toLowerCase().contains(lowerCaseSearchText)
						|| generatedTriggerExp.infix().toLowerCase().contains(lowerCaseSearchText)
						|| delayExp != null && delayExp.infix().toLowerCase().contains(lowerCaseSearchText)) {					
						bioEventList.add(bioEvent);
					}
				}catch(ExpressionException e){
					e.printStackTrace();
					throw new RuntimeException(this.getClass().getSimpleName()+".computeData() error: "+e.getMessage(),e);
				}
			}
		}
		return bioEventList;
	}

	public Object getValueAt(int row, int column) {
		try{
			BioEvent event = getValueAt(row);
			if (event != null) {
				switch (column) {
					case COLUMN_EVENT_NAME: {
						return event.getName();
					} 
					case COLUMN_EVENT_TRIGGER_DESCRIPTION: {
						return event.getTriggerDescription();
					}
					case COLUMN_EVENT_ACTIONS: {
						ArrayList<EventAssignment> eas = event.getEventAssignments();
						if (eas == null || eas.size() == 0) {
							return "No actions defined";
						} 
						String varNames = "";
						for (EventAssignment ea : eas) {
							varNames = varNames.concat(ea.getTarget().getName() + ", ");
						}
						varNames = varNames.substring(0, varNames.lastIndexOf(","));
						LocalParameter delayParam = event.getParameter(BioEventParameterType.TriggerDelay);
						if (delayParam.getExpression()!=null && !delayParam.getExpression().isZero()){
							return delayParam.getExpression().infix()+" "+delayParam.getUnitDefinition().getSymbol()+" after trigger reset "+varNames;
						}
						return "on trigger reset "+varNames;
					} 
				}
			}
//			else {
//				if (column == COLUMN_EVENT_NAME) {
//					return BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT;
//				}
//			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == COLUMN_EVENT_NAME;
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
//		if(evt.getSource() == simulationContext && evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_ENTITY_NAME)) {
//			String oldName = (String)evt.getOldValue();
//			String newName = (String)evt.getNewValue();
//			for(int i=0; simulationContext.getBioEvents() != null && i<simulationContext.getBioEvents().length; i++) {
//				boolean replaced = false;
//				BioEvent event = simulationContext.getBioEvents()[i];
//				// ---------------------- check expressions for the assignments of this event 
//				ArrayList<EventAssignment> eas = event.getEventAssignments();
//				if(eas != null) {
//					for(EventAssignment ea : eas) {
//						Expression exp = ea.getAssignmentExpression();
//						if(exp == null || exp.getSymbols() == null || exp.getSymbols().length == 0) {
//							continue;
//						}
//						for(String symbol : exp.getSymbols()) {
//							if(symbol != null && symbol.contentEquals(oldName)) {
//								try {
//									exp.substituteInPlace(new Expression(oldName), new Expression(newName));
//									replaced = true;
//								} catch (ExpressionException e) {
//									e.printStackTrace();
//									String errMsg = "Failed to rename symbol '" + oldName + "' with '" + newName + "' in an EventAssignment Expression of " + BioEvent.typeName + " '" + event.getDisplayName() + "'.";
//									throw new RuntimeException(errMsg);
//								}
//							}
//						}
//					}
//				}
//				// ---------------------- check expressions for trigger function, trigger delay and trigger time
//				LocalParameter[] params = event.getEventParameters();
//				if(params != null) {
//					for(LocalParameter param : params) {
//						if(param == null) {
//							continue;
//						}
//						Expression exp = param.getExpression();
//						if(exp == null || exp.getSymbols() == null || exp.getSymbols().length == 0) {
//							continue;
//						}
//						for(String symbol : exp.getSymbols()) {
//							if(symbol != null && symbol.contentEquals(oldName)) {
//								try {
//									exp.substituteInPlace(new Expression(oldName), new Expression(newName));
//									replaced = true;
//								} catch (ExpressionException e) {
//									e.printStackTrace();
//									String errMsg = "Failed to rename symbol '" + oldName + "' with '" + newName + "' in an Expression of " + BioEvent.typeName + " '" + event.getDisplayName() + "' Parameter.";
//									throw new RuntimeException(errMsg);
//								}
//							}
//						}
//					}
//				}
//				try {
//					if(replaced) {
//						event.bind();
//					}
//				} catch (ExpressionBindingException e) {
//					e.printStackTrace();
//					String errMsg = "Failed to bind an Expression of " + BioEvent.typeName + " '" + event.getDisplayName() + "' after renaming variable '" + oldName + "'.";
//					throw new RuntimeException(errMsg);
//				}
//			}
//			refreshData();
//		} else 
		if (evt.getPropertyName().equals("trigger") || evt.getPropertyName().equals("delay") || evt.getPropertyName().equals("eventAssignments")) {
			fireTableRowsUpdated(0, getRowCount()-1);
		} else {
			if (evt.getPropertyName().equals("bioevents")) {
				BioEvent[] oldValue = (BioEvent[])evt.getOldValue();
				if (oldValue != null) {			
					for (BioEvent be : oldValue) {
						be.removePropertyChangeListener(this);						
					}
				}
				BioEvent[] newValue = (BioEvent[])evt.getNewValue();
				if (newValue != null) {			
					for (BioEvent be : newValue) {
						be.addPropertyChangeListener(this);						
					}
				}
			}
			refreshData();
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		try{
			if (value == null || value.toString().length() == 0 || BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT.equals(value)) {
				return;
			}
			BioEvent bioEvent = getValueAt(row);
			if (bioEvent == null) {
				bioEvent = simulationContext.createBioEvent();
			} else {
				bioEvent = getValueAt(row);
			}
			switch (column) {
				case COLUMN_EVENT_NAME: {
					bioEvent.setName((String)value);
				} 
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage());
		}
	}

	public Comparator<BioEvent> getComparator(int col, boolean ascending) {
		return null;
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}

	public String checkInputValue(String inputValue, int row, int column) {
		BioEvent bioEvent = getValueAt(row);
		switch (column) {
		case COLUMN_EVENT_NAME: {
			if (bioEvent == null || !bioEvent.getName().equals(inputValue)) {
				if (simulationContext.getBioEvent(inputValue) != null) {
					return "An event with name '" + inputValue + "' already exists!";
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
	
//	@Override
//	public int getRowCount() {
//		return getRowCountWithAddNew();
//	}
}
