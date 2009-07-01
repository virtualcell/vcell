package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class SpeciesContextSpecsTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	public static final int NUM_COLUMNS = 5;
	public static final int COLUMN_SPECIESCONTEXT = 1;
	public static final int COLUMN_SPECIES = 0;
	public static final int COLUMN_STRUCTURE = 2;
	public static final int COLUMN_FIXED = 3;
	public static final int COLUMN_INITIAL = 4;
	private String LABELS[] = { "Species", "Species Context", "Structure", "Clamped", "Initial Conditions" };
	
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext fieldSimulationContext = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public SpeciesContextSpecsTableModel() {
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
public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_SPECIESCONTEXT:{
			return String.class;
		}
		case COLUMN_SPECIES:{
			return String.class;
		}
		case COLUMN_STRUCTURE:{
			return String.class;
		}
		case COLUMN_FIXED:{
			return Boolean.class;
		}
		case COLUMN_INITIAL:{
			return ScopedExpression.class;
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
		return getSimulationContext().getReactionContext().getSpeciesContextSpecs().length;
	}
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
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	SpeciesContextSpec scSpec = getSpeciesContextSpec(row);
	switch (col){
		case COLUMN_SPECIESCONTEXT:{
			return scSpec.getSpeciesContext().getName();
		}
		case COLUMN_SPECIES:{
			return scSpec.getSpeciesContext().getSpecies().getCommonName();
		}
		case COLUMN_STRUCTURE:{
			return scSpec.getSpeciesContext().getStructure().getName();
		}
		case COLUMN_FIXED:{
			return new Boolean(scSpec.isConstant());
		}
		case COLUMN_INITIAL:{
			if(scSpec.getInitialConditionParameter() != null)
			{
				return new ScopedExpression(scSpec.getInitialConditionParameter().getExpression(),scSpec.getInitialConditionParameter().getNameScope(), 
						true, autoCompleteSymbolFilter);
			}
			else 
			{
				return null;
			}
			
		}
		default:{
			return null;
		}
	}
}

public SpeciesContextSpec getSpeciesContextSpec(int row) {
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	return getSimulationContext().getReactionContext().getSpeciesContextSpecs(row);
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
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	switch (columnIndex){
		case COLUMN_SPECIESCONTEXT:{
			return false;
		}
		case COLUMN_SPECIES:{
			return false;
		}
		case COLUMN_STRUCTURE:{
			return false;
		}
		case COLUMN_FIXED:{
			return true;
		}
		case COLUMN_INITIAL:{
			// editing in place is impractical for expression
			// this way, we can pop-up a custom dialog to enter a new value (GUI has to take care of this)
			return true;
		}
		default:{
			return false;
		}
	}
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() instanceof ReactionContext
		&& evt.getPropertyName().equals("speciesContextSpecs")) {

		updateListenersReactionContext((ReactionContext)evt.getSource(),true);
		updateListenersReactionContext((ReactionContext)evt.getSource(),false);
		fireTableDataChanged();
		
	}
	if (evt.getSource() instanceof SpeciesContext && evt.getPropertyName().equals("name")) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof SpeciesContextSpec) {
		fireTableRowsUpdated(0,getRowCount()-1);
	}
	if (evt.getSource() instanceof SpeciesContextSpec.SpeciesContextSpecParameter) {
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
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		updateListenersReactionContext(oldValue.getReactionContext(),true);
	}
	fieldSimulationContext = simulationContext;	
	if (simulationContext!=null){
		simulationContext.addPropertyChangeListener(this);
		updateListenersReactionContext(simulationContext.getReactionContext(),false);
		
		autoCompleteSymbolFilter  = simulationContext.getAutoCompleteSymbolFilter();
	}
	firePropertyChange("simulationContext", oldValue, simulationContext);
	fireTableDataChanged();
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("SpeciesContextSpecsTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("SpeciesContextSpecsTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	SpeciesContextSpec scSpec = getSimulationContext().getReactionContext().getSpeciesContextSpecs(rowIndex);
	switch (columnIndex){
		case COLUMN_FIXED:{
			boolean bFixed = ((Boolean)aValue).booleanValue();
			if (bFixed){
				scSpec.setConstant(true);
			}else{
				scSpec.setConstant(false);
			}
			fireTableRowsUpdated(rowIndex,rowIndex);
			break;
		}
		case COLUMN_INITIAL:{
			try {
				if (aValue instanceof ScopedExpression){
					Expression exp = ((ScopedExpression)aValue).getExpression();
					if(getSimulationContext().isUsingConcentration())
					{
						scSpec.getInitialConcentrationParameter().setExpression(exp);
					}
					else
					{
						scSpec.getInitialCountParameter().setExpression(exp);
					}
				}else if (aValue instanceof String) {
					String newExpressionString = (String)aValue;
					if(getSimulationContext().isUsingConcentration())
					{
						scSpec.getInitialConcentrationParameter().setExpression(new Expression(newExpressionString));
					}
					else
					{
						scSpec.getInitialCountParameter().setExpression(new Expression(newExpressionString));
					}
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				//
				// don't handle exception here, InitialConditionsPanel needs it.
				//
				throw new RuntimeException(e.getMessage());
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				//
				// don't handle exception here, InitialConditionsPanel needs it.
				//
				PopupGenerator.showErrorDialog("Wrong Expression:\n" + e.getMessage());
				//throw new RuntimeException(e.getMessage());
			}
			break;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/12/2005 2:44:36 PM)
 */
private void updateListenersReactionContext(ReactionContext reactionContext,boolean bRemove) {

	if(bRemove){
		reactionContext.removePropertyChangeListener(this);
		SpeciesContextSpec oldSpecs[] = reactionContext.getSpeciesContextSpecs();
		for (int i=0;i<oldSpecs.length;i++){
			oldSpecs[i].removePropertyChangeListener(this);
			oldSpecs[i].getSpeciesContext().removePropertyChangeListener(this);
			Parameter oldParameters[] = oldSpecs[i].getParameters();
			for (int j = 0; j < oldParameters.length ; j++){
				oldParameters[j].removePropertyChangeListener(this);
			}
		}
	}else{
		reactionContext.addPropertyChangeListener(this);
		SpeciesContextSpec newSpecs[] = reactionContext.getSpeciesContextSpecs();
		for (int i=0;i<newSpecs.length;i++){
			newSpecs[i].addPropertyChangeListener(this);
			newSpecs[i].getSpeciesContext().addPropertyChangeListener(this);
			Parameter newParameters[] = newSpecs[i].getParameters();
			for (int j = 0; j < newParameters.length ; j++){
				newParameters[j].addPropertyChangeListener(this);
			}
		}
	}

}
}