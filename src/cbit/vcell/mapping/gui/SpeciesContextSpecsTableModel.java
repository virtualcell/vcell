package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.DefaultSortTableModel;

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
@SuppressWarnings("serial")
public class SpeciesContextSpecsTableModel extends DefaultSortTableModel<SpeciesContextSpec> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_SPECIESCONTEXT = 1;
	public static final int COLUMN_SPECIES = 0;
	public static final int COLUMN_STRUCTURE = 2;
	public static final int COLUMN_CLAMPED = 3;
	public static final int COLUMN_INITIAL = 4;
	public static final int COLUMN_WELLMIXED = 5;
	public static final int COLUMN_DIFFUSION = 6;
	private static String columnNames[] = { "Species", "Species Context", "Structure", "Clamped", "Initial Condition", "Well Mixed", "Diffusion Constant"};
	
	private SimulationContext fieldSimulationContext = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
	private JTable ownerTable = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public SpeciesContextSpecsTableModel(JTable table) {
	super(columnNames);
	ownerTable = table;
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
		case COLUMN_CLAMPED:
		case COLUMN_WELLMIXED:{
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
@Override
public int getColumnCount() {
	if (getSimulationContext() != null && getSimulationContext().getGeometry().getDimension() > 0) {
		return super.getColumnCount();
	} else {
		return super.getColumnCount() - 2;
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
	if (getSimulationContext()== null){
		setData(null);
	} else {
		setData(Arrays.asList(getSimulationContext().getReactionContext().getSpeciesContextSpecs()));
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
		if (col<0 || col>=getColumnCount()){
			throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(getColumnCount()-1)+"]");
		}
		if (getRowCount() <= row){
			refreshData();
		}	
		SpeciesContextSpec scSpec = getValueAt(row);
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
			case COLUMN_CLAMPED:{
				return new Boolean(scSpec.isConstant());
			}
			case COLUMN_WELLMIXED:{
				return (scSpec.isConstant() || scSpec.isWellMixed()) && !getSimulationContext().isStoch();
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
				if(diffusionParameter != null && !scSpec.isConstant() && scSpec.isWellMixed()!=null && !scSpec.isWellMixed()) 	{
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
	if (columnIndex<0 || columnIndex>=getColumnCount()){
		throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
	}
	SpeciesContextSpec speciesContextSpec = getValueAt(rowIndex);
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
		case COLUMN_CLAMPED:{
			return true;
		}
		case COLUMN_WELLMIXED:{
			return !speciesContextSpec.isConstant() && !getSimulationContext().isStoch();
		}
		case COLUMN_INITIAL:{
			return true;
		}
		case COLUMN_DIFFUSION: {
			return !speciesContextSpec.isConstant() && (!speciesContextSpec.isWellMixed() || getSimulationContext().isStoch());
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
	if (columnIndex<0 || columnIndex>=getColumnCount()){
		throw new RuntimeException("SpeciesContextSpecsTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
	}
	SpeciesContextSpec scSpec = getValueAt(rowIndex);
	switch (columnIndex){
		case COLUMN_CLAMPED:{
			boolean bFixed = ((Boolean)aValue).booleanValue();
			if (bFixed){
				scSpec.setConstant(true);
			}else{
				scSpec.setConstant(false);
			}
			fireTableRowsUpdated(rowIndex,rowIndex);
			break;
		}
		case COLUMN_WELLMIXED:{
			boolean bWellMixed = ((Boolean)aValue).booleanValue();
			scSpec.setWellMixed(bWellMixed);
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


public Comparator<SpeciesContextSpec> getComparator(final int col, final boolean ascending) {
	return new Comparator<SpeciesContextSpec>() {	
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
				case COLUMN_CLAMPED : {
					Boolean bClamped1 = new Boolean(speciesContextSpec1.isConstant());
					Boolean bClamped2 = new Boolean(speciesContextSpec2.isConstant());
					if (ascending){
						return bClamped1.compareTo(bClamped2);
					}else{
						return bClamped2.compareTo(bClamped1);
					}
				}
				case COLUMN_WELLMIXED : {
					Boolean bWellMixed1 = new Boolean(speciesContextSpec1.isWellMixed());
					Boolean bWellMixed2 = new Boolean(speciesContextSpec2.isWellMixed());
					if (ascending){
						return bWellMixed1.compareTo(bWellMixed2);
					}else{
						return bWellMixed2.compareTo(bWellMixed1);
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
		}
	};
}

}