package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.DefaultSortTableModel;

import cbit.gui.ScopedExpression;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.Delay;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class EventsSummaryTableModel extends DefaultSortTableModel<BioEvent> implements PropertyChangeListener{

	public final static int COLUMN_EVENT_NAME = 0;
	public final static int COLUMN_EVENT_TRIGGER_EXPR = 1;
	public final static int COLUMN_EVENT_DELAY_EXPR = 2;
	public final static int COLUMN_EVENT_ASSIGN_VARS_LIST = 3;
	
	private SimulationContext fieldSimContext = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private static String[] columnNames = new String[] {"Name", "Trigger", "Delay", "Event Assignment Vars"};
	private JTable ownerTable = null;

	public EventsSummaryTableModel(JTable table) {
		super(columnNames);
		ownerTable = table;
		addPropertyChangeListener(this);
	}
	
	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}


	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
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

	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_EVENT_NAME:{
				return BioEvent.class;
			}
			case COLUMN_EVENT_TRIGGER_EXPR:{
				return ScopedExpression.class;
			}
			case COLUMN_EVENT_DELAY_EXPR:{
				return ScopedExpression.class;
			}
			case COLUMN_EVENT_ASSIGN_VARS_LIST:{
				return String.class;
			}
			default:{
				return Object.class;
			}
		}
	}

	private void refreshData() {

		rows.clear();
		if (getSimulationContext()==null){
			return;
		}
		for (BioEvent bioEvent : getSimulationContext().getBioEvents()){
			rows.add(bioEvent);
		}
		fireTableDataChanged();
	}

	public Object getValueAt(int row, int column) {
		try{
			if (getRowCount() <= row){
				refreshData();
			}	
			BioEvent event = getValueAt(row);
			if (row >= 0 && row < getRowCount()) {
				switch (column) {
					case COLUMN_EVENT_NAME: {
						return event;
					} 
					case COLUMN_EVENT_TRIGGER_EXPR: {
						if (event.getTriggerExpression() == null) {
							return null; 
						} else {
							return new ScopedExpression(event.getTriggerExpression(), event.getNameScope(), true);
						}
					}
					case COLUMN_EVENT_DELAY_EXPR: {
						Delay delay = event.getDelay();
						if (delay == null) {
							return "None"; 
						} else {
							return new ScopedExpression(delay.getDurationExpression(), event.getNameScope(), true);
						}
					}
					case COLUMN_EVENT_ASSIGN_VARS_LIST: {
						ArrayList<EventAssignment> eas = event.getEventAssignments();
						if (eas.size() == 0) {
							return "None";
						} 
						String varNames = "";
						for (EventAssignment ea : eas) {
							varNames = varNames.concat(ea.getTarget().getName() + ", ");
						}
						varNames = varNames.substring(0, varNames.lastIndexOf(","));
						return varNames;
					} 
					default: {
						return null;
					}
				}
			} else {
				return null;
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	/**
	 * The hasListeners method was generated to support the propertyChange field.
	 */
	public synchronized boolean hasListeners(java.lang.String propertyName) {
		return getPropertyChange().hasListeners(propertyName);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// for now make all cells uneditable ?
		return false;
	}

	public void propertyChange(java.beans.PropertyChangeEvent evt) {
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
	
	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex<0 || rowIndex>=getRowCount()){
			throw new RuntimeException("EventsSummaryTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		if (columnIndex<0 || columnIndex>=getColumnCount()){
			throw new RuntimeException("EventsSummaryTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
		}
		// BioEvent event = (BioEvent)getData().get(rowIndex);
	}
	
	public SimulationContext getSimulationContext() {
		return fieldSimContext;
	}

	public void setSimulationContext(SimulationContext argSimContext) {
		SimulationContext oldValue = fieldSimContext;
		fieldSimContext = argSimContext;
		
		if (oldValue != null) {			
			oldValue.removePropertyChangeListener(this);
			if (oldValue.getBioEvents() != null) {		
				for (BioEvent be : oldValue.getBioEvents()) {
					be.removePropertyChangeListener(this);
				}
			}
		}
			
		if (argSimContext != null) {
			argSimContext.addPropertyChangeListener(this);
			if (argSimContext.getBioEvents() != null) {		
				for (BioEvent be : argSimContext.getBioEvents()) {
					be.addPropertyChangeListener(this);
				}
			}
		}
		firePropertyChange("simulationContext", oldValue, argSimContext);
	}
	
	public void selectEvent(BioEvent bioEvent) {
		for (int i = 0; i < getRowCount(); i ++) {
			BioEvent valueAt = (BioEvent)getValueAt(i, COLUMN_EVENT_NAME);
			if (valueAt.getName().equals(bioEvent.getName())) {
				ownerTable.changeSelection(i, 0, false, false);
				break;
			}
		}		
	}

	@Override
	public void sortColumn(int col, boolean ascending) {
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}

}
