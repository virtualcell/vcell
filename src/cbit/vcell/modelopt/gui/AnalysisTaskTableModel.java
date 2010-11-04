package cbit.vcell.modelopt.gui;

import java.beans.PropertyChangeListener;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.modelopt.AnalysisTask;

public class AnalysisTaskTableModel extends ManageTableModel implements PropertyChangeListener {

	private static final String PROPERTY_NAME_SIMULATION_CONTEXT = "simulationContext";
	
	public final static int COLUMN_TASK_NAME = 0;
	private String[] columnNames = new String[] {"Parameter Estimation"};
	private JTable ownerTable = null;
	private SimulationContext simulationContext = null;
	
	public AnalysisTaskTableModel(JTable table) {
		super();
		ownerTable = table;
		addPropertyChangeListener(this);
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * getColumnCount method comment.
	 */
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/**
	 * getRowCount method comment.
	 */
	public int getRowCount() {
		if (simulationContext == null) {
			return 0;
		}
		AnalysisTask[] tasks = simulationContext.getAnalysisTasks();
		if (tasks != null) {
			return tasks.length;
		} else {
			return 0;
		}
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_TASK_NAME:{
				return AnalysisTask.class;
			}
			default:{
				return Object.class;
			}
		}
	}
	
	public Object getValueAt(int row, int column) {
		if (row >= 0 && row < getRowCount()) {
			AnalysisTask task = (AnalysisTask)getData().get(row);
			switch (column) {
				case COLUMN_TASK_NAME: {
					return task;
				} 
			}
		}
		return null;
	}
	
	private void refreshData() {
		rows.clear();
		int count = getRowCount();
		for (int i = 0; i < count; i++){
			rows.add(simulationContext.getAnalysisTasks()[i]);
		}		
		fireTableDataChanged();
	}
	
	public void setSimulationContext(SimulationContext newValue) {
		SimulationContext oldValue = simulationContext;
		simulationContext = newValue;
		firePropertyChange(PROPERTY_NAME_SIMULATION_CONTEXT, oldValue, newValue);
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXT)) {
			SimulationContext oldValue = (SimulationContext)evt.getOldValue();
			if (oldValue != null) {
				oldValue.removePropertyChangeListener(this);
			}
			SimulationContext newValue = (SimulationContext)evt.getNewValue();
			if (newValue != null) {
				newValue.addPropertyChangeListener(this);
			}
			refreshData();
		} else if (evt.getSource() == simulationContext && evt.getPropertyName().equals("analysisTasks")){
			 AnalysisTask[] oldAnalysisTasks = (AnalysisTask[])evt.getOldValue();
			 if (oldAnalysisTasks!=null && oldAnalysisTasks.length > 0){
				 for (int i = 0; i < oldAnalysisTasks.length; i++){
				 	oldAnalysisTasks[i].removePropertyChangeListener(this);
				 }
			 }
			 AnalysisTask[] newAnalysisTasks = (AnalysisTask[])evt.getNewValue();		 
			 if (newAnalysisTasks!=null && newAnalysisTasks.length > 0){
				 for (int i = 0; i < newAnalysisTasks.length; i++){
				 	newAnalysisTasks[i].addPropertyChangeListener(this);
				 }
			 }
			 refreshData();
		 }
	}
	
	public AnalysisTask getAnalysisTask(int row) {
		return row < 0 || row >= getRowCount() ? null : (AnalysisTask)getData().get(row);
	}
}
