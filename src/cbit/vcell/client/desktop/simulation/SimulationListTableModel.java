package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 4:07:40 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class SimulationListTableModel extends VCellSortTableModel<Simulation> implements PropertyChangeListener {
	private static final String PROPERTY_NAME_SIMULATION_WORKSPACE = "simulationWorkspace";
	private final static int COLUMN_NAME = 0;
	private final static int COLUMN_ENDTIME = 1;
	private final static int COLUMN_OUTPUT = 2;
	private final static int COLUMN_SOLVER = 3;
	private final static int COLUMN_STATUS = 4;
	private final static int COLUMN_RESULTS = 5;
	
	private static final String[] columnNames = new String[] {"Name", "End Time", "Output Option", "Solver", "Running Status", "Results"};
	private SimulationWorkspace simulationWorkspace = null;

/**
 * SimulationListTableModel constructor comment.
 */
public SimulationListTableModel(ScrollTable table) {
	super(table, columnNames);
	addPropertyChangeListener(this);
}


/**
 * getRowCount method comment.
 */
private void refreshData() {
	List<Simulation> simList = null;
	if (getSimulationWorkspace() != null && getSimulationWorkspace().getSimulations() != null) {
		simList = Arrays.asList(getSimulationWorkspace().getSimulations());
	}
	setData(simList);
	GuiUtils.flexResizeTableColumns(ownerTable);
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
		if (row >= 0 && row < getRowCount()) {
			Simulation simulation = getValueAt(row);
			switch (column) {
				case COLUMN_NAME: {
					return simulation.getName();
				} 
				case COLUMN_ENDTIME: {
					return simulation.getSolverTaskDescription().getTimeBounds().getEndingTime();
				} 
				case COLUMN_OUTPUT: {
					return simulation.getSolverTaskDescription().getOutputTimeSpec();
				} 
				case COLUMN_SOLVER: {
					return simulation.getSolverTaskDescription().getSolverDescription().getDisplayLabel();
				} 
				case COLUMN_STATUS: {
					return getSimulationWorkspace().getSimulationStatusDisplay(simulation);
				} 
				case COLUMN_RESULTS: {
					return getSimulationWorkspace().getSimulationStatus(simulation).getHasData() ? "yes" : "no";
				} 
			}
		}
	} catch(Exception e){
		e.printStackTrace();
	}
	return null;
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
		case COLUMN_NAME: 
			return true;
		case COLUMN_ENDTIME:
			return true;
		case COLUMN_OUTPUT:
			return true;
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
	if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_WORKSPACE)) {
		SimulationWorkspace oldValue = (SimulationWorkspace) evt.getOldValue();
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
		}
		SimulationWorkspace newValue = (SimulationWorkspace) evt.getNewValue();
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
		}
		refreshData();
	}
	if (evt.getSource() == getSimulationWorkspace() && evt.getPropertyName().equals("simulations")) {
		refreshData();
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
	SimulationWorkspace oldValue = this.simulationWorkspace;
	simulationWorkspace = newSimulationWorkspace;
	firePropertyChange(PROPERTY_NAME_SIMULATION_WORKSPACE, oldValue, newSimulationWorkspace);
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 2:01:23 PM)
 * @param aValue java.lang.Object
 * @param rowIndex int
 * @param columnIndex int
 */
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	Simulation simulation = getValueAt(rowIndex);
	try {
		switch (columnIndex){
			case COLUMN_NAME:
				if (aValue instanceof String){
					String newName = (String)aValue;
					if (!simulation.getName().equals(newName)){
						simulation.setName(newName);
						fireTableRowsUpdated(rowIndex,rowIndex);
					}
				}
				break;
			case COLUMN_ENDTIME:
				if (aValue instanceof Double){
					double newTime = (Double) aValue;
					TimeBounds oldTimeBounds = simulation.getSolverTaskDescription().getTimeBounds();
					TimeBounds timeBounds = new TimeBounds(oldTimeBounds.getStartingTime(), newTime);
					simulation.getSolverTaskDescription().setTimeBounds(timeBounds);
				}
				break;
			case COLUMN_OUTPUT:
				if (aValue instanceof String){
					OutputTimeSpec ots = simulation.getSolverTaskDescription().getOutputTimeSpec();
					OutputTimeSpec newOts = null;
					if (ots instanceof DefaultOutputTimeSpec) {
						int newValue = Integer.parseInt((String)aValue);
						newOts = new DefaultOutputTimeSpec(newValue, ((DefaultOutputTimeSpec) ots).getKeepAtMost());
					} else if (ots instanceof UniformOutputTimeSpec) {
						double newTime = Double.parseDouble((String)aValue);
						newOts = new UniformOutputTimeSpec(newTime);
					} else if (ots instanceof ExplicitOutputTimeSpec) {
						newOts = ExplicitOutputTimeSpec.fromString((String) aValue);
					}
					if (newOts != null) {
						simulation.getSolverTaskDescription().setOutputTimeSpec(newOts);
					}
				}
				break;
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(ownerTable, ex.getMessage());
	}
}

@Override
protected Comparator<Simulation> getComparator(int col, boolean ascending) {
	return null;
}

@Override
public boolean isSortable(int col) {
	return false;
}


@Override
public Class<?> getColumnClass(int columnIndex) {
	switch (columnIndex) {
	case COLUMN_ENDTIME:
		return Double.class;
	case COLUMN_OUTPUT:
		return OutputTimeSpec.class;
	default:
		return String.class;
	}
}
}