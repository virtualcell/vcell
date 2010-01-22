package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class SpeciesContextSpecsTableModel extends ManageTableModel implements java.beans.PropertyChangeListener {
	public static final int NUM_COLUMNS = 6;
	public static final int COLUMN_SPECIESCONTEXT = 1;
	public static final int COLUMN_SPECIES = 0;
	public static final int COLUMN_STRUCTURE = 2;
	public static final int COLUMN_FIXED = 3;
	public static final int COLUMN_INITIAL = 4;
	public static final int COLUMN_DIFFUSION = 5;
	private String LABELS[] = { "Species", "Species Context", "Structure", "Clamped", "Initial Condition", "Diffusion Constant"};
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext fieldSimulationContext = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
	private JTable ownerTable = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public SpeciesContextSpecsTableModel(JTable table) {
	super();
	ownerTable = table;
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
		case COLUMN_SPECIESCONTEXT:{
			return SpeciesContext.class;
		}
		case COLUMN_SPECIES:{
			return Species.class;
		}
		case COLUMN_STRUCTURE:{
			return Structure.class;
		}
		case COLUMN_FIXED:{
			return Boolean.class;
		}
		case COLUMN_INITIAL:
		case COLUMN_DIFFUSION:{
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
	if (getSimulationContext() != null && getSimulationContext().getGeometry().getDimension() > 0) {
		return NUM_COLUMNS;
	} else {
		return NUM_COLUMNS - 1;
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
private SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


private void refreshData() {

	if (getSimulationContext()==null){
		return;
	}
	int count = getRowCount();
	rows.clear();
	for (int i = 0; i < count; i++){
		rows.add(getSimulationContext().getReactionContext().getSpeciesContextSpecs(i));
	}
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		if (row<0 || row>=getRowCount()){
			throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		if (col<0 || col>=NUM_COLUMNS){
			throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
		}
		
		if (getData().size() <= row){
			refreshData();
		}	
		SpeciesContextSpec scSpec = getSpeciesContextSpec(row);
		switch (col){
			case COLUMN_SPECIESCONTEXT:{
				return scSpec.getSpeciesContext();
			}
			case COLUMN_SPECIES:{
				return scSpec.getSpeciesContext().getSpecies();
			}
			case COLUMN_STRUCTURE:{
				return scSpec.getSpeciesContext().getStructure();
			}
			case COLUMN_FIXED:{
				return new Boolean(scSpec.isConstant());
			}
			case COLUMN_INITIAL:{
				SpeciesContextSpecParameter initialConditionParameter = scSpec.getInitialConditionParameter();
				if(initialConditionParameter != null) {
					return new ScopedExpression(initialConditionParameter.getExpression(),initialConditionParameter.getNameScope(),  true, autoCompleteSymbolFilter);
				} else	{
					return null;
				}
				
			}
			case COLUMN_DIFFUSION:{
				SpeciesContextSpecParameter diffusionParameter = scSpec.getDiffusionParameter();
				if(diffusionParameter != null) 	{
					return new ScopedExpression(diffusionParameter.getExpression(),diffusionParameter.getNameScope(), true, autoCompleteSymbolFilter);
				} else {
					return null;
				}			
			}
			default:{
				return null;
			}
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		return null;
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
		case COLUMN_INITIAL:
		case COLUMN_DIFFUSION: {
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
		refreshData();
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
	if (evt.getSource() instanceof GeometryContext) {
		fireTableStructureChanged();
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
		oldValue.getGeometryContext().removePropertyChangeListener(this);
		updateListenersReactionContext(oldValue.getReactionContext(),true);
	}
	fieldSimulationContext = simulationContext;	
	if (simulationContext!=null){
		simulationContext.addPropertyChangeListener(this);
		simulationContext.getGeometryContext().addPropertyChangeListener(this);
		updateListenersReactionContext(simulationContext.getReactionContext(),false);
		
		autoCompleteSymbolFilter  = simulationContext.getAutoCompleteSymbolFilter();
		refreshData();
	}
	firePropertyChange("simulationContext", oldValue, simulationContext);
	fireTableStructureChanged();
	fireTableDataChanged();
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("SpeciesContextSpecsTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("SpeciesContextSpecsTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	SpeciesContextSpec scSpec = getSpeciesContextSpec(rowIndex);
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
				if (aValue instanceof String) {
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
				PopupGenerator.showErrorDialog(ownerTable, "Wrong Expression:\n" + e.getMessage());
				//throw new RuntimeException(e.getMessage());
			}
			break;
		}
		case COLUMN_DIFFUSION:{
			try {
				if (aValue instanceof String) {
					String newExpressionString = (String)aValue;
					scSpec.getDiffusionParameter().setExpression(new Expression(newExpressionString));
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
				PopupGenerator.showErrorDialog(ownerTable, "Wrong Expression:\n" + e.getMessage());
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


@Override
public void sortColumn(final int col, final boolean ascending) {
	Collections.sort(rows, new Comparator<SpeciesContextSpec>() {	
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(SpeciesContextSpec speciesContextSpec1, SpeciesContextSpec speciesContextSpec2){			
			
			SpeciesContext speciesContext1 = speciesContextSpec1.getSpeciesContext();
			SpeciesContext speciesContext2 = speciesContextSpec2.getSpeciesContext();			
			switch (col){
				case COLUMN_SPECIESCONTEXT:{
					String name1 = speciesContext1.getName();
					String name2 = speciesContext2.getName();
					if (ascending){
						return name1.compareToIgnoreCase(name2);
					}else{
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_SPECIES:{
					String commonName1 = speciesContext1.getSpecies().getCommonName();
					String commonName2 = speciesContext2.getSpecies().getCommonName();
					if (ascending){
						return commonName1.compareToIgnoreCase(commonName2);
					}else{
						return commonName2.compareToIgnoreCase(commonName1);
					}
				}
				case COLUMN_STRUCTURE:{
					String name1 = speciesContext1.getStructure().getName();
					String name2 = speciesContext2.getStructure().getName();
					if (ascending){						
						return name1.compareToIgnoreCase(name2);
					}else{						
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_FIXED : {
					Boolean bClamped1 = new Boolean(speciesContextSpec1.isConstant());
					Boolean bClamped2 = new Boolean(speciesContextSpec2.isConstant());
					if (ascending){
						return bClamped1.compareTo(bClamped2);
					}else{
						return bClamped2.compareTo(bClamped1);
					}
				}
				case COLUMN_INITIAL: {
					Expression initExp1 = speciesContextSpec1.getInitialConditionParameter().getExpression();
					String infix1 = (initExp1!=null)?(initExp1.infix()):("");
					Expression initExp2 = speciesContextSpec2.getInitialConditionParameter().getExpression();
					String infix2 = (initExp2!=null)?(initExp2.infix()):("");
					if (ascending){
						return infix1.compareToIgnoreCase(infix2);
					}else{
						return infix2.compareToIgnoreCase(infix1);
					}
				}
				case COLUMN_DIFFUSION: {
					Expression diffExp1 = speciesContextSpec1.getDiffusionParameter().getExpression();
					String infix1 = (diffExp1!=null)?(diffExp1.infix()):("");
					Expression diffExp2 = speciesContextSpec2.getDiffusionParameter().getExpression();
					String infix2 = (diffExp2!=null)?(diffExp2.infix()):("");
					if (ascending){
						return infix1.compareToIgnoreCase(infix2);
					}else{
						return infix2.compareToIgnoreCase(infix1);
					}
				}	
			}
			return 1;
		};
	});	
	fireTableDataChanged();
}

public SpeciesContextSpec getSpeciesContextSpec(int row) {
return (SpeciesContextSpec)getData().get(row);
}

}