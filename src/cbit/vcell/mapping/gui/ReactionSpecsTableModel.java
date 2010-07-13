package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ReactionSpecsTableModel extends ManageTableModel implements java.beans.PropertyChangeListener {
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_TYPE = 1;
	public static final int COLUMN_ENABLED = 2;
	public static final int COLUMN_FAST = 3;
	private String LABELS[] = { "Name", "Type", "Enabled", "Fast" };
	
	private JTable ownerTable = null;
	private boolean filterFlag;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext fieldSimulationContext = null;
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ReactionSpecsTableModel(JTable table, boolean flag) {
	super();
	ownerTable = table;
	filterFlag = flag;
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
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_NAME:{
			return ReactionStep.class;
		}
		case COLUMN_TYPE:{
			return String.class;
		}
		case COLUMN_ENABLED:{
			return Boolean.class;
		}
		case COLUMN_FAST:{
			return Boolean.class;
		}
		default:{
			return Object.class;
		}
	}
}

public String getColumnName(int column) {
	try {
		return LABELS[column];
	} catch (Throwable exc) {
		System.out.println("WARNING - no such column index: " + column);
		exc.printStackTrace(System.out);
		return null;
	}
}

/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return LABELS.length;
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

/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	ReactionSpec reactionSpec = (ReactionSpec)rows.get(row);
	switch (col){
		case COLUMN_NAME:{
			return reactionSpec.getReactionStep();
		}
		case COLUMN_TYPE:{
			if (reactionSpec.getReactionStep() instanceof SimpleReaction){
				return "Reaction";
			}else if (reactionSpec.getReactionStep() instanceof FluxReaction){
				return "Flux";
			}else{
				return null;
			}
		}
		case COLUMN_ENABLED:{
			return new Boolean(!reactionSpec.isExcluded());
		}
		case COLUMN_FAST:{
			if (reactionSpec.getReactionStep() instanceof FluxReaction){
				return new Boolean(false);
			}else{
				return new Boolean(reactionSpec.isFast());
			}
		}
		default:{
			return null;
		}
	}
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:27:46 AM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	if (columnIndex == COLUMN_ENABLED){
		return true;
	}else if (columnIndex == COLUMN_FAST && getSimulationContext()!=null){
		ReactionSpec reactionSpec = (ReactionSpec)getValueAt(rowIndex);
		//
		// the "fast" column is only editable if not FluxReaction
		//
		return (!(reactionSpec.getReactionStep() instanceof FluxReaction));
	}else{
		return false;
	}
}
/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("simulationContext")) {
		populateData();
	}
	if (evt.getSource() instanceof ReactionContext && evt.getPropertyName().equals("reactionSpecs")) {
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("name")) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof ReactionSpec) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		ReactionContext reactionContext = oldValue.getReactionContext();
		reactionContext.removePropertyChangeListener(this);
		ReactionSpec oldSpecs[] = reactionContext.getReactionSpecs();
		for (int i=0;i<oldSpecs.length;i++){
			oldSpecs[i].getReactionStep().removePropertyChangeListener(this);
			oldSpecs[i].removePropertyChangeListener(this);
		}
	}
	fieldSimulationContext = simulationContext;
	if (simulationContext!=null){
		simulationContext.addPropertyChangeListener(this);
		ReactionContext reactionContext = fieldSimulationContext.getReactionContext();
		reactionContext.addPropertyChangeListener(this);
		ReactionSpec newSpecs[] = reactionContext.getReactionSpecs();
		for (int i=0;i<newSpecs.length;i++){
			newSpecs[i].getReactionStep().addPropertyChangeListener(this);
			newSpecs[i].addPropertyChangeListener(this);
		}
	}
	firePropertyChange("simulationContext", oldValue, simulationContext);
}

private void populateData() {
	if (getSimulationContext() == null) {
		setData(new ArrayList<ReactionSpec>());
	} else {
		List<ReactionSpec> rslist = Arrays.asList(getSimulationContext().getReactionContext().getReactionSpecs());
		setData(rslist);
	}
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	ReactionSpec reactionSpec = (ReactionSpec)rows.get(rowIndex);
	try {
		switch (columnIndex){
			case COLUMN_ENABLED:{
				boolean bEnabled = ((Boolean)aValue).booleanValue();
				if (bEnabled){
					reactionSpec.setReactionMapping(ReactionSpec.INCLUDED);
				}else{
					reactionSpec.setReactionMapping(ReactionSpec.EXCLUDED);
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
				break;
			}
			case COLUMN_FAST:{
				boolean bFast = ((Boolean)aValue).booleanValue();
				if (bFast){
					reactionSpec.setReactionMapping(ReactionSpec.FAST);
				}else{
					reactionSpec.setReactionMapping(ReactionSpec.INCLUDED);
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
				break;
			}
		}
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
	}
}
@Override
public void sortColumn(int col, boolean ascending) {
	  Collections.sort(rows, new ReactionSpecComparator(col, ascending));
	  fireTableDataChanged();
}

private class ReactionSpecComparator implements Comparator<ReactionSpec> {
	protected int index;
	protected boolean ascending;

	public ReactionSpecComparator(int index, boolean ascending){
		this.index = index;
		this.ascending = ascending;
	}
	
	public int compare(ReactionSpec parm1, ReactionSpec parm2){
		
		switch (index) {
			case COLUMN_NAME:{
				int bCompare = parm1.getReactionStep().getName().compareToIgnoreCase(parm2.getReactionStep().getName());
				return ascending ? bCompare : -bCompare;
			}
			case COLUMN_TYPE:{
				String type1 = (parm1.getReactionStep() instanceof SimpleReaction) ? "Reaction" : "Flux";
				String type2 = (parm2.getReactionStep() instanceof SimpleReaction) ? "Reaction" : "Flux";
				int bCompare = type1.compareTo(type2);
				return ascending ? bCompare : -bCompare;
				
			}
			case COLUMN_ENABLED:{
				int bCompare = new Boolean(!parm1.isExcluded()).compareTo(new Boolean(!parm2.isExcluded()));
				return ascending ? bCompare : -bCompare;
			}
			case COLUMN_FAST:{
				Boolean fast1 = (parm1.isFast() && !(parm1.getReactionStep() instanceof FluxReaction)) ? true : false;
				Boolean fast2 = (parm2.isFast() && !(parm2.getReactionStep() instanceof FluxReaction)) ? true : false;
				int bCompare = fast1.compareTo(fast2);
				return ascending ? bCompare : -bCompare;
			}
		}
		return 1;
	}
}

}
