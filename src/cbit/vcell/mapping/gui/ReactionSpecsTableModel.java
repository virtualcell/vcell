package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.mapping.ReactionSpec;
import cbit.util.BeanUtils;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ReactionSpecsTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 4;
	private final int COLUMN_NAME = 0;
	private final int COLUMN_TYPE = 1;
	private final int COLUMN_ENABLED = 2;
	private final int COLUMN_FAST = 3;
	private String LABELS[] = { "Name", "Type", "Enabled", "Fast" };
	
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ReactionSpecsTableModel() {
	super();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column) {
	switch (column){
		case COLUMN_NAME:{
			return String.class;
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
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return NUM_COLUMNS;
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
 * getRowCount method comment.
 */
public int getRowCount() {
	if (getSimulationContext()==null){
		return 0;
	}else{
		return getSimulationContext().getReactionContext().getNumReactionSpecs();
	}
}
/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public cbit.vcell.mapping.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("ReactionSpecsTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ReactionSpecsTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	ReactionSpec reactionSpec = getSimulationContext().getReactionContext().getReactionSpecs(row);
	switch (col){
		case COLUMN_NAME:{
			return reactionSpec.getReactionStep().getName();
		}
		case COLUMN_TYPE:{
			if (reactionSpec.getReactionStep() instanceof cbit.vcell.model.SimpleReaction){
				return "Reaction";
			}else if (reactionSpec.getReactionStep() instanceof cbit.vcell.model.FluxReaction){
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
				return null;
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
		ReactionSpec reactionSpec = getSimulationContext().getReactionContext().getReactionSpecs(rowIndex);
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
	if (evt.getSource() instanceof cbit.vcell.mapping.ReactionContext
		&& evt.getPropertyName().equals("reactionSpecs")) {
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.model.ReactionStep
		&& evt.getPropertyName().equals("name")) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof cbit.vcell.mapping.ReactionSpec) {
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
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(cbit.vcell.mapping.SimulationContext simulationContext) {
	cbit.vcell.mapping.SimulationContext oldValue = fieldSimulationContext;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		updateListenersReactionContext(oldValue.getReactionContext(),true);
		//ReactionStep oldSteps[] = oldValue.getModel().getReactionSteps();
		//for (int i=0;i<oldSteps.length;i++){
			//oldSteps[i].removePropertyChangeListener(this);
		//}
		//oldValue.getReactionContext().removePropertyChangeListener(this);
		//ReactionSpec oldSpecs[] = oldValue.getReactionContext().getReactionSpecs();
		//for (int i=0;i<oldSpecs.length;i++){
			//oldSpecs[i].removePropertyChangeListener(this);
		//}
	}
	fieldSimulationContext = simulationContext;
	if (simulationContext!=null){
		simulationContext.addPropertyChangeListener(this);
		updateListenersReactionContext(simulationContext.getReactionContext(),false);
		//ReactionStep newSteps[] = simulationContext.getModel().getReactionSteps();
		//for (int i=0;i<newSteps.length;i++){
			//newSteps[i].addPropertyChangeListener(this);
		//}
		//simulationContext.getReactionContext().addPropertyChangeListener(this);
		//ReactionSpec newSpecs[] = simulationContext.getReactionContext().getReactionSpecs();
		//for (int i=0;i<newSpecs.length;i++){
			//newSpecs[i].addPropertyChangeListener(this);
		//}
	}
	firePropertyChange("simulationContext", oldValue, simulationContext);
	fireTableDataChanged();
}
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ReactionSpecsTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ReactionSpecsTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	ReactionSpec reactionSpec = getSimulationContext().getReactionContext().getReactionSpecs(rowIndex);
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
/**
 * Insert the method's description here.
 * Creation date: (9/12/2005 2:44:36 PM)
 */
private void updateListenersReactionContext(cbit.vcell.mapping.ReactionContext reactionContext,boolean bRemove) {

	if(bRemove){
		reactionContext.removePropertyChangeListener(this);
		ReactionSpec oldSpecs[] = reactionContext.getReactionSpecs();
		for (int i=0;i<oldSpecs.length;i++){
			oldSpecs[i].getReactionStep().removePropertyChangeListener(this);
			oldSpecs[i].removePropertyChangeListener(this);
		}
	}else{
		reactionContext.addPropertyChangeListener(this);
		ReactionSpec newSpecs[] = reactionContext.getReactionSpecs();
		for (int i=0;i<newSpecs.length;i++){
			newSpecs[i].getReactionStep().addPropertyChangeListener(this);
			newSpecs[i].addPropertyChangeListener(this);
		}
	}

}
}
