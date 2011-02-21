package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
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
@SuppressWarnings("serial")
public class ReactionSpecsTableModel extends VCellSortTableModel<ReactionSpec> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_TYPE = 1;
	public static final int COLUMN_ENABLED = 2;
	public static final int COLUMN_FAST = 3;
	private final static String LABELS[] = { "Name", "Type", "Enabled", "Fast" };
	private SimulationContext fieldSimulationContext = null;
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ReactionSpecsTableModel(ScrollTable table) {
	super(table, LABELS);
	addPropertyChangeListener(this);
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

@Override
public int getColumnCount() {
	if (fieldSimulationContext == null || !fieldSimulationContext.isStoch()) {
		return super.getColumnCount();
	}
	return super.getColumnCount() - 1;
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
	ReactionSpec reactionSpec = getValueAt(row);
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
		ReactionSpec reactionSpec = getValueAt(rowIndex);
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
		refreshData();
		fireTableStructureChanged();
	}
	if (evt.getSource() instanceof ReactionContext && evt.getPropertyName().equals("reactionSpecs")) {
		refreshData();
	}
	if (evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("name")) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof ReactionSpec) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
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

private void refreshData() {
	List<ReactionSpec> rslist = null;
	if (getSimulationContext() != null) {
		rslist = Arrays.asList(getSimulationContext().getReactionContext().getReactionSpecs());
	}
	setData(rslist);
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	ReactionSpec reactionSpec = getValueAt(rowIndex);
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

	public Comparator<ReactionSpec> getComparator(int col, boolean ascending) {
		return new ReactionSpecComparator(col, ascending);
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
