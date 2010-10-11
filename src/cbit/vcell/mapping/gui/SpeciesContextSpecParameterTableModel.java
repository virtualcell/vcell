package cbit.vcell.mapping.gui;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class SpeciesContextSpecParameterTableModel extends ManageTableModel implements java.beans.PropertyChangeListener {

	static {
		System.out.println("SpeciesContextSpecParameterTableModel: artifically filtering out membrane diffusion parameters and diffusion/bc's that are not applicable");
	}

	
	private class ParameterColumnComparator implements Comparator<Parameter> {
		protected int index;
		protected boolean ascending;

		public ParameterColumnComparator(int index, boolean ascending){
			this.index = index;
			this.ascending = ascending;
		}
		
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(Parameter parm1, Parameter parm2){	
			
			switch (index){
				case COLUMN_NAME:{
					if (ascending){
						return parm1.getName().compareToIgnoreCase(parm2.getName());
					}else{
						return parm2.getName().compareToIgnoreCase(parm1.getName());
					}
					//break;
				}
				case COLUMN_VALUE:{
					String expression1 = parm1.getExpression() != null ? parm1.getExpression().infix() : "";
					String expression2 = parm2.getExpression() != null ? parm2.getExpression().infix() : "";
					if (ascending){
						return expression1.compareToIgnoreCase(expression2);
					}else{
						return expression2.compareToIgnoreCase(expression1);
					}
					//break;
				}
				case COLUMN_DESCRIPTION:{
					if (ascending){
						return parm1.getDescription().compareToIgnoreCase(parm2.getDescription());
					}else{
						return parm2.getDescription().compareToIgnoreCase(parm1.getDescription());
					}
					//break;
				}
				case COLUMN_UNIT:{
					String unit1 = (parm1.getUnitDefinition()!=null)?(parm1.getUnitDefinition().getSymbol()):"null";
					String unit2 = (parm2.getUnitDefinition()!=null)?(parm2.getUnitDefinition().getSymbol()):"null";
					if (ascending){
						return unit1.compareToIgnoreCase(unit2);
					}else{
						return unit2.compareToIgnoreCase(unit1);
					}
					//break;
				}
			}
			return 1;
		}
	}
	private static final int COLUMN_DESCRIPTION = 0;
	private static final int COLUMN_NAME = 1;
	public static final int COLUMN_VALUE = 2;
	private final int COLUMN_UNIT = 3;
	private final String LABELS[] = { "Description", "Parameter", "Expression", "Units" };
	private SpeciesContextSpec fieldSpeciesContextSpec = null;
	
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
	private JTable ownerTable = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public SpeciesContextSpecParameterTableModel(JTable table) {
	super();
	ownerTable = table;
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
			return String.class;
		}
		case COLUMN_DESCRIPTION:{
			return String.class;
		}
		case COLUMN_UNIT:{
			return String.class;
		}
		case COLUMN_VALUE:{
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
	return LABELS.length;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public String getColumnName(int column) {
	return LABELS[column];
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
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private List<Parameter> getUnsortedParameters() {
	if (fieldSpeciesContextSpec==null){
		return null;
	}
	ArrayList<Parameter> paramList = new ArrayList<Parameter>();
	paramList.add(fieldSpeciesContextSpec.getInitialConditionParameter());
	
	SimulationContext simulationContext = fieldSpeciesContextSpec.getSimulationContext();
	if (simulationContext==null){
		return paramList;
	}
	if (fieldSpeciesContextSpec.isConstant()){
		return paramList;
	}

	SpeciesContext speciesContext = fieldSpeciesContextSpec.getSpeciesContext();
	if (speciesContext.getStructure() instanceof Membrane){
		MembraneMapping membraneMapping = (MembraneMapping)simulationContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
		if (simulationContext.getGeometry()!=null && !fieldSpeciesContextSpec.isWellMixed()){
			int dimension = simulationContext.getGeometry().getDimension();
			if (dimension > 1) {
				// diffusion
				paramList.add(fieldSpeciesContextSpec.getDiffusionParameter());
				
				if (!simulationContext.isStoch()) {
					// boundary condition
					paramList.add(fieldSpeciesContextSpec.getBoundaryXmParameter());
					paramList.add(fieldSpeciesContextSpec.getBoundaryXpParameter());
					paramList.add(fieldSpeciesContextSpec.getBoundaryYmParameter());
					paramList.add(fieldSpeciesContextSpec.getBoundaryYpParameter());
					
					if (dimension > 2) {
						paramList.add(fieldSpeciesContextSpec.getBoundaryZmParameter());
						paramList.add(fieldSpeciesContextSpec.getBoundaryZpParameter());
					}
				}
			}
		}		
	} else if (speciesContext.getStructure() instanceof Feature){
		if (simulationContext.getGeometry()!=null && !fieldSpeciesContextSpec.isWellMixed()){
			int dimension = simulationContext.getGeometry().getDimension();
			if (dimension > 0) {
				paramList.add(fieldSpeciesContextSpec.getDiffusionParameter());
				
				if (!simulationContext.isStoch()) {
					// boundary condition
					paramList.add(fieldSpeciesContextSpec.getBoundaryXmParameter());
					paramList.add(fieldSpeciesContextSpec.getBoundaryXpParameter());
				 
					if (dimension > 1) {
						paramList.add(fieldSpeciesContextSpec.getBoundaryYmParameter());
						paramList.add(fieldSpeciesContextSpec.getBoundaryYpParameter());
					
					
						if (dimension > 2) {
							paramList.add(fieldSpeciesContextSpec.getBoundaryZmParameter());
							paramList.add(fieldSpeciesContextSpec.getBoundaryZpParameter());
						}
					}
					
					// velocity
					paramList.add(fieldSpeciesContextSpec.getVelocityXParameter());
					if (dimension > 1) {
						paramList.add(fieldSpeciesContextSpec.getVelocityYParameter());
					
						if (dimension > 2) {
							paramList.add(fieldSpeciesContextSpec.getVelocityZParameter());
						}
					}
				}
			}
		}
	} else {
		throw new RuntimeException("unsupported Structure type '"+speciesContext.getStructure().getClass().getName()+"'");
	}
	return paramList;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		Parameter parameter = (Parameter)getData().get(row);
		switch (col){
			case COLUMN_NAME:{
				return parameter.getName();
			}
			case COLUMN_DESCRIPTION:{
				return parameter.getDescription();
			}
			case COLUMN_UNIT:{
				if (parameter.getUnitDefinition()!=null){
					return parameter.getUnitDefinition().getSymbol();
				}else{
					return "null";
				}
			}
			case COLUMN_VALUE:{
				if (parameter instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
					if (parameter.getExpression()==null){
						return null;
					}else{
						return new ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable(), autoCompleteSymbolFilter);
					}
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
	Parameter parameter = (Parameter)getData().get(rowIndex);
	if (columnIndex == COLUMN_NAME){
		return parameter.isNameEditable();
	}else if (columnIndex == COLUMN_VALUE){
		return parameter.isExpressionEditable();
	}
	return false;
}


/**
 * isSortable method comment.
 */
public boolean isSortable(int col) {
	return true;
}


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	//
	// must listen to the following events:
	//    this.simulationContext
	//		simulationContext.GeometryContext.geometry
	//      simulationContext.GeometryContext.structureMappings
	//        StructureMapping.resolved
	//
	//    this.speciesContextSpec
	//      SpeciesContextSpec.parameters
	//        Parameter.*
	//
	try {		
		//
		// if geometry changes (could affect spatially resolved boundaries).
		//
		if (fieldSpeciesContextSpec != null && evt.getSource() == fieldSpeciesContextSpec.getSimulationContext().getGeometryContext() 
				&& evt.getPropertyName().equals("geometry")){
			setData(getUnsortedParameters());
			fireTableDataChanged();
		}
		//
		// if structureMappings array changes (could affect spatially resolved boundaries).
		//
		if (fieldSpeciesContextSpec != null && evt.getSource() == fieldSpeciesContextSpec.getSimulationContext().getGeometryContext() 
				&& evt.getPropertyName().equals("structureMappings")){
			StructureMapping[] oldStructureMappings = (StructureMapping[])evt.getOldValue();
			for (int i = 0; oldStructureMappings!=null && i < oldStructureMappings.length; i++){
				oldStructureMappings[i].removePropertyChangeListener(this);
			}
			StructureMapping[] newStructureMappings = (StructureMapping[])evt.getNewValue();
			for (int i = 0; newStructureMappings!=null && i < newStructureMappings.length; i++){
				newStructureMappings[i].addPropertyChangeListener(this);
			}
			setData(getUnsortedParameters());
			fireTableDataChanged();
		}
		//
		// if structureMapping changes (could affect spatially resolved boundaries).
		//
		if (evt.getSource() instanceof StructureMapping){
			setData(getUnsortedParameters());
			fireTableDataChanged();
		}
		
		if (evt.getSource() == this && evt.getPropertyName().equals("speciesContextSpec")) {
			SpeciesContextSpec oldValue = (SpeciesContextSpec)evt.getOldValue();
			if (oldValue!=null){
				oldValue.removePropertyChangeListener(this);
				Parameter oldParameters[] = oldValue.getParameters();
				if (oldParameters!=null) {
					for (int i = 0; i<oldParameters.length; i++){
						oldParameters[i].removePropertyChangeListener(this);
					}
				}
				SimulationContext oldSimContext = oldValue.getSimulationContext();
				if (oldSimContext!=null){
					oldSimContext.getGeometryContext().removePropertyChangeListener(this);
					StructureMapping[] oldStructureMappings = oldSimContext.getGeometryContext().getStructureMappings();
					if (oldStructureMappings!=null) {
						for (int i = 0; i < oldStructureMappings.length; i++){
							oldStructureMappings[i].removePropertyChangeListener(this);
						}
					}
				}
			}
			SpeciesContextSpec newValue = (SpeciesContextSpec)evt.getNewValue();
			if (newValue!=null){
				newValue.addPropertyChangeListener(this);
				Parameter newParameters[] = newValue.getParameters();
				if (newParameters != null) {
					for (int i = 0; i < newParameters.length; i ++){
						newParameters[i].addPropertyChangeListener(this);
					}
				}
				SimulationContext newSimContext = newValue.getSimulationContext();
				if (newSimContext!=null){
					newSimContext.getGeometryContext().addPropertyChangeListener(this);
					StructureMapping[] newStructureMappings = newSimContext.getGeometryContext().getStructureMappings();
					if (newStructureMappings != null) {
						for (int i = 0; i < newStructureMappings.length; i++){
							newStructureMappings[i].addPropertyChangeListener(this);
						}
					}
				}
			}
			
			setData(getUnsortedParameters());
			fireTableDataChanged();
		}
		if (evt.getSource() instanceof SpeciesContextSpec){
			// if parameters changed must update listeners
			if (evt.getPropertyName().equals("parameters")) {
				Parameter oldParameters[] = (Parameter[])evt.getOldValue();
				if (oldParameters!=null) {
					for (int i = 0;i<oldParameters.length; i++){
						oldParameters[i].removePropertyChangeListener(this);
					}
				}
				Parameter newParameters[] = (Parameter[])evt.getNewValue();
				if (newParameters!=null) {
					for (int i = 0;i<newParameters.length; i++){
						newParameters[i].addPropertyChangeListener(this);
					}
				}
			}
			// for any change to the SpeciesContextSpec, want to update all.
			setData(getUnsortedParameters());
			fireTableDataChanged();
		}
		if(evt.getSource() instanceof Parameter){
			setData(getUnsortedParameters());
			fireTableDataChanged();
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
}

/**
 * Sets the speciesContextSpec property (cbit.vcell.mapping.SpeciesContextSpec) value.
 * @param speciesContextSpec The new value for the property.
 * @see #getSpeciesContextSpec
 */
public void setSpeciesContextSpec(SpeciesContextSpec speciesContextSpec) {
	SpeciesContextSpec oldValue = fieldSpeciesContextSpec;
	fieldSpeciesContextSpec = speciesContextSpec;
	firePropertyChange("speciesContextSpec", oldValue, speciesContextSpec);
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	System.out.println("setValueAt called");
	Parameter parameter = (Parameter)getData().get(rowIndex);
	switch (columnIndex){
		case COLUMN_NAME:{
			try {
				if (aValue instanceof String){
					String newName = (String)aValue;
					if (!parameter.getName().equals(newName)){
						if (parameter instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
							SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)parameter;
							scsParm.setName(newName);
						}
					}
				}
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "error changing parameter name\n"+e.getMessage());
			}
			break;
		}
		case COLUMN_VALUE:{
			try {
				if (aValue instanceof String) {
					String newExpressionString = (String)aValue;
					if (parameter instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
						SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)parameter;
						if (!(scsParm.getRole() == SpeciesContextSpec.ROLE_VelocityX 
								|| scsParm.getRole() == SpeciesContextSpec.ROLE_VelocityY 
								|| scsParm.getRole() == SpeciesContextSpec.ROLE_VelocityZ )) {
							Expression newExp = null;
							if (newExpressionString == null || newExpressionString.trim().length() == 0) {
								newExp = new Expression(0.0);
							} else {
								newExp = new Expression(newExpressionString);
							}
							scsParm.setExpression(newExp);
						} else {
							// scsParam is a velocity parameter
							Expression newExp = null;
							if (newExpressionString != null && newExpressionString.trim().length() > 0) {
								newExp = new Expression(newExpressionString);
							} 
							scsParm.setExpression(newExp);							
						}
					}
				}
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, e.getMessage());
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "expression error\n"+e.getMessage());
			}
			break;
		}
	}
}


  public void sortColumn(int col, boolean ascending) {
    Collections.sort(rows, new ParameterColumnComparator(col, ascending));
  }
}