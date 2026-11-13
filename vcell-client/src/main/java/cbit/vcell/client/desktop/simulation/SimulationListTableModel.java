/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import cbit.vcell.solver.*;
import org.vcell.util.BeanUtils;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 4:07:40 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class SimulationListTableModel extends VCellSortTableModel<Simulation> implements PropertyChangeListener {
	private static final String PROPERTY_NAME_SIMULATION_WORKSPACE = "simulationWorkspace";

	// the columns the table can show; refreshColumns() decides which ones are shown and in what order
	// (same approach as SpeciesContextSpecTableModel: no hardcoded column indexes)
	private enum ColumnType {
		NAME("Name"),
		END_TIME("End Time"),
		OUTPUT("Output Option"),
		SOLVER("Solver"),
		STATUS("Running Status"),
		DURATION("Duration"),	// SpringSaLaD applications only
		RESULTS("Results");

		public final String label;

		ColumnType(String label) {
			this.label = label;
		}
	}

	// value of the Duration column: the rough duration shown in the cell and a tooltip saying what kind it is
	// (estimate while running, actual once completed); SimulationListPanel has the renderer for it
	record DurationDisplay(String text, String toolTip) {
	}

	private final List<ColumnType> columns = new ArrayList<>();	// the columns currently shown, in display order
	private SimulationWorkspace simulationWorkspace = null;


/**
 * SimulationListTableModel constructor comment.
 */
public SimulationListTableModel(ScrollTable table) {
	super(table);
	refreshColumns();
	addPropertyChangeListener(this);
}


// builds the list of columns to show, which depends on the application type of the workspace
private void refreshColumns() {
	columns.clear();
	columns.add(ColumnType.NAME);
	columns.add(ColumnType.END_TIME);
	columns.add(ColumnType.OUTPUT);
	columns.add(ColumnType.SOLVER);
	columns.add(ColumnType.STATUS);
	// SpringSaLaD applications (all their simulations are langevin) also show the estimated duration of a batch run
	// while it runs, and the actual duration once completed; other applications have no ETA, so no column
	if (simulationWorkspace != null && simulationWorkspace.getSimulationOwner() instanceof SimulationContext simContext
			&& simContext.getApplicationType() == SimulationContext.Application.SPRINGSALAD) {
		columns.add(ColumnType.DURATION);
	}
	columns.add(ColumnType.RESULTS);
}


@Override
public int getColumnCount() {
	return columns.size();
}


@Override
public String getColumnName(int column) {
	return columns.get(column).label;
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
			switch (columns.get(column)) {
				case NAME: {
					return simulation.getName();
				}
				case END_TIME: {
					if (simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver())
					{
						return simulation.getSolverTaskDescription().getChomboSolverSpec().getEndingTime();
					}
					return simulation.getSolverTaskDescription().getTimeBounds().getEndingTime();
				} 
				case OUTPUT: {
					if (simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver())
					{
						String text = "Variable";
						if (simulation.getSolverTaskDescription().getChomboSolverSpec().getTimeIntervalList().size() == 1)
						{
							text = "Every " + simulation.getSolverTaskDescription().getChomboSolverSpec().getLastTimeInterval().getOutputTimeStep() + "s";
						}
						return text;
					}
					return simulation.getSolverTaskDescription().getOutputTimeSpec();
				} 
				case SOLVER: {
					return simulation.getSolverTaskDescription().getSolverDescription();
				}
				case STATUS: {
					return getSimulationWorkspace().getSimulationStatus(simulation);
				}
				case DURATION: {
					// a langevin batch is one VCell job (job 0)
					SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(simulation);
					Integer seconds = null;
					String toolTip = null;
					if (simStatus.isRunning()) {
						if (simulation.getSolverTaskDescription().getLangevinSimulationOptions().getTotalNumberOfJobs() == 1) {
							// a single run has no watchdog, so it sends no ETA -> blank cell
							return new DurationDisplay("", "Estimate not available for single runs");
						}
						// estimated TOTAL duration of the batch run (not remaining time), from the langevin watchdog;
						// null = no ETA yet (the first one comes seconds after run #1 starts) -> blank, never "0 min"
						seconds = simStatus.getEstimatedTimeAt(0);
						if (seconds == null) {
							return new DurationDisplay("", "Estimate not available yet");
						}
						toolTip = "Estimate";
					} else if (simStatus.isCompleted()) {
						// actual duration, from the hand-off to slurm until completion: it includes the time waiting
						// in the slurm queue, preprocessing and postprocessing, which the ETA doesn't
						SimulationJobStatus jobStatus = simStatus.getJobStatus(0);
						if (jobStatus != null && jobStatus.getStartDate() != null && jobStatus.getEndDate() != null) {
							seconds = (int)((jobStatus.getEndDate().getTime() - jobStatus.getStartDate().getTime()) / 1000);
							toolTip = "Actual";
						}
					}
					// failed, stopped, never ran, ... -> blank, no tooltip
					if (seconds == null) {
						return null;
					}
					// deliberately rough, easy to read: "3 days", "17 hours", "23 min"
					String text;
					if (seconds >= 2 * 24 * 3600) {
						text = Math.round(seconds / (24 * 3600.0)) + " days";
					} else if (seconds >= 2 * 3600) {
						text = Math.round(seconds / 3600.0) + " hours";
					} else if (seconds >= 60) {
						text = Math.round(seconds / 60.0) + " min";
					} else {
						text = "< 1 min";
					}
					return new DurationDisplay(text, toolTip);
				}
				case RESULTS: {
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
	Simulation simulation = getValueAt(rowIndex);
	switch (columns.get(columnIndex)){
		case NAME:
			return true;
		case END_TIME:
			return !simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver();
		case OUTPUT:
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
			for (Simulation simulation : oldValue.getSimulations()) {
				simulation.removePropertyChangeListener(this);
				simulation.getSolverTaskDescription().removePropertyChangeListener(this);
			}
		}
		SimulationWorkspace newValue = (SimulationWorkspace) evt.getNewValue();
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			for (Simulation simulation : newValue.getSimulations()) {
				simulation.addPropertyChangeListener(this);
				simulation.getSolverTaskDescription().addPropertyChangeListener(this);
			}
		}
		// the columns depend on the application type of the new workspace
		int oldColumnCount = getColumnCount();
		refreshColumns();
		if (getColumnCount() != oldColumnCount) {
			fireTableStructureChanged();
		}
		refreshData();
	} else if (evt.getSource() == getSimulationWorkspace()) {
		if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS)) {
			Simulation[] oldValue = (Simulation[]) evt.getOldValue();
			if (oldValue != null) {
				for (Simulation simulation : oldValue) {
					simulation.removePropertyChangeListener(this);
					simulation.getSolverTaskDescription().removePropertyChangeListener(this);
				}
			}
			Simulation[] newValue = (Simulation[]) evt.getNewValue();
			if (newValue != null) {
				for (Simulation simulation : newValue) {
					simulation.addPropertyChangeListener(this);
					simulation.getSolverTaskDescription().addPropertyChangeListener(this);
				}
			}
			refreshData();

		} else if (evt.getPropertyName().equals(SimulationWorkspace.PROPERTY_NAME_SIMULATION_STATUS)) {
			int simIndex = (Integer)evt.getNewValue();
			int row = simIndex % getMaxRowsPerPage();
			fireTableRowsUpdated(row, row);
		}
	} else {
		if (evt.getSource() instanceof Simulation && evt.getPropertyName().equals(Simulation.PROPERTY_NAME_SOLVER_TASK_DESCRIPTION)) {
			SolverTaskDescription oldValue = (SolverTaskDescription)evt.getOldValue();
			if (oldValue != null) {
				oldValue.removePropertyChangeListener(this);
			}
			SolverTaskDescription newValue = (SolverTaskDescription)evt.getNewValue();
			if (newValue != null) {
				newValue.addPropertyChangeListener(this);
			}			
		}
		fireTableRowsUpdated(0, getRowCount() - 1);
	}
}

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
		switch (columns.get(columnIndex)){
			case NAME:
				if (aValue instanceof String){
					String newName = (String)aValue;
					if (!simulation.getName().equals(newName)){
						simulation.setName(newName);
					}
				}
				break;
			case END_TIME:
				if(aValue instanceof String) {
					aValue = Double.parseDouble((String)aValue);
				}
				if (aValue instanceof Double){
					SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
					double newEndTime = (Double) aValue;
					if (newEndTime != solverTaskDescription.getTimeBounds().getEndingTime()) {
						ClientTaskManager.changeEndTime(ownerTable, solverTaskDescription, newEndTime);
						simulation.setIsDirty(true);
					}
				}
				break;
			case OUTPUT:
				if (aValue instanceof String){
					SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
					OutputTimeSpec ots = solverTaskDescription.getOutputTimeSpec();
					OutputTimeSpec newOts = null;
					if (ots instanceof DefaultOutputTimeSpec) {
						int newValue = Integer.parseInt((String)aValue);
						newOts = new DefaultOutputTimeSpec(newValue, ((DefaultOutputTimeSpec) ots).getKeepAtMost());
					} else if (ots instanceof UniformOutputTimeSpec) {
						try {
							boolean bValid = true;
							double outputTime = Double.parseDouble((String)aValue);
							if (solverTaskDescription.getOutputTimeSpec().isUniform()) {
								if (solverTaskDescription.getSolverDescription().hasVariableTimestep()) {
									newOts = new UniformOutputTimeSpec(outputTime);
								} else {
									double timeStep = solverTaskDescription.getTimeStep().getDefaultTimeStep();
									
									double suggestedInterval = outputTime;
									if (outputTime < timeStep) {
										suggestedInterval = timeStep;
										bValid = false;
									} else {
										if (BeanUtils.isNotAnIntegerMultiple(outputTime, timeStep)) {
											double n = outputTime/timeStep;
											int intn = (int)Math.round(n);
											suggestedInterval = (intn * timeStep);
											if (suggestedInterval != outputTime) {
												bValid = false;
											}
										}
									} 
									if (bValid) {
										newOts = new UniformOutputTimeSpec(outputTime);
									} else {		
										String ret = PopupGenerator.showWarningDialog(ownerTable, "Output Interval", "Output Interval must " +
												"be integer multiple of time step.\n\nChange Output Interval to " + suggestedInterval + "?", 
												new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
										if (ret.equals(UserMessage.OPTION_YES)) {
											newOts = new UniformOutputTimeSpec(suggestedInterval);
										} 
									}
								}
							}
						} catch (NumberFormatException ex) {
							DialogUtils.showErrorDialog(ownerTable, "Wrong number format " + ex.getMessage().toLowerCase());
						}
					} else if (ots instanceof ExplicitOutputTimeSpec) {
						newOts = ExplicitOutputTimeSpec.fromString((String) aValue);
					}
					if (newOts != null && !newOts.compareEqual(ots)) {
						solverTaskDescription.setOutputTimeSpec(newOts);
						simulation.setIsDirty(true);
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
	switch (columns.get(columnIndex)) {
	case STATUS:
		return SimulationStatus.class;
	case END_TIME:
		return Double.class;
	case OUTPUT:
		return OutputTimeSpec.class;
	case SOLVER:
		return SolverDescription.class;
	case DURATION:
		return DurationDisplay.class;
	default:
		return String.class;
	}
}
}
