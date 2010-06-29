package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 4:07:40 PM)
 * @author: Ion Moraru
 */
public class SimulationListTableModel extends AbstractTableModel implements PropertyChangeListener {
	public final static int COLUMN_NAME = 0;
	private final static int COLUMN_LASTSAVED = 1;
	private final static int COLUMN_STATUS = 2;
	private final static int COLUMN_RESULTS = 3;
	
	private String[] columnNames = new String[] {"Name", "Last Saved", "Running Status", "Results"};
	private SimulationWorkspace simulationWorkspace = null;
	private JTable ownerTable = null;

/**
 * SimulationListTableModel constructor comment.
 */
public SimulationListTableModel(JTable table) {
	super();
	ownerTable = table;
}


/**
 * getColumnCount method comment.
 */
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
	if (getSimulationWorkspace() != null && getSimulationWorkspace().getSimulations() != null) {
		return getSimulationWorkspace().getSimulations().length;
	} else {
		return 0;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:22:06 AM)
 * @return cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
private SimulationWorkspace getSimulationWorkspace() {
	return simulationWorkspace;
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int column) {
	try{
		Simulation simulation = getSimulationWorkspace().getSimulations(row);
		if (row >= 0 && row < getRowCount()) {
			switch (column) {
				case COLUMN_NAME: {
					return simulation.getName();
				} 
				case COLUMN_LASTSAVED: {
					if (!simulation.getIsDirty() && simulation.getVersion() != null) {
						return simulation.getVersion().getDate().toString();
					} else {
						return "not yet saved";
					}
				} 
				case COLUMN_STATUS: {
					return getSimulationWorkspace().getSimulationStatusDisplay(simulation);
				} 
				case COLUMN_RESULTS: {
					return getSimulationWorkspace().getSimulationStatus(simulation).getHasData() ? "yes" : "no";
				} 
				default: {
					return null;
				}
			}
		} else {
			return null;
		}
	}catch(Exception e){
		e.printStackTrace();
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 1:56:12 PM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	switch (columnIndex){
		case COLUMN_NAME: {
			return true;
		}
		default: {
			return false;
		}
	}
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getSimulationWorkspace() && evt.getPropertyName().equals("simulations")) {
		fireTableDataChanged();
	}
	if (evt.getSource() == getSimulationWorkspace() && evt.getPropertyName().equals("status")) {
		fireTableRowsUpdated(((Integer)evt.getNewValue()).intValue(), ((Integer)evt.getNewValue()).intValue());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 6:32:09 PM)
 * @param newSimContext cbit.vcell.mapping.SimulationContext
 */
public void setSimulationWorkspace(SimulationWorkspace newSimulationWorkspace) {
	if (getSimulationWorkspace() != null) {
		getSimulationWorkspace().removePropertyChangeListener(this);
	}
	simulationWorkspace = newSimulationWorkspace;
	if (newSimulationWorkspace != null) {
		newSimulationWorkspace.addPropertyChangeListener(this);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 2:01:23 PM)
 * @param aValue java.lang.Object
 * @param rowIndex int
 * @param columnIndex int
 */
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("SimulationListTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=getColumnCount()){
		throw new RuntimeException("SimulationListTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
	}
	Simulation simulation = getSimulationWorkspace().getSimulations(rowIndex);
	switch (columnIndex){
		case COLUMN_NAME:{
			try {
				if (aValue instanceof String){
					String newName = (String)aValue;
					if (!simulation.getName().equals(newName)){
						simulation.setName(newName);
						fireTableRowsUpdated(rowIndex,rowIndex);
					}
				}
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, e.getMessage());
			}
			break;
		}
	}
}

public Simulation getSelectedSimulation() {
	int row = ownerTable.getSelectedRow();
	if (row < 0) {
		return null;
	}
	Simulation simulation = getSimulationWorkspace().getSimulations(row);
	return simulation;
}
}