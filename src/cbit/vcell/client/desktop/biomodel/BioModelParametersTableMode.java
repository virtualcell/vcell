package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class BioModelParametersTableMode extends BioModelEditorRightSideTableModel<Parameter> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_PATH = 0;
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_DESCRIPTION = 2;
	public static final int COLUMN_EXPRESSION = 3;
	public static final int COLUMN_UNIT = 4;
	private static String LABELS[] = {"Path", "Name", "Description", "Expression", "Units"};
	private boolean bIncludeReactions = true;
	private boolean bIncludeSimulationContexts = true;
	private boolean bIncludeGlobal = true;
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public BioModelParametersTableMode(EditorScrollTable table) {
	super(table);
	setColumns(LABELS);
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int col) {
	switch (col){
		case COLUMN_PATH:
			return NameScope.class;
		case COLUMN_NAME:
			return String.class;
		case COLUMN_DESCRIPTION:
			return String.class;
		case COLUMN_EXPRESSION:
			return ScopedExpression.class;
		case COLUMN_UNIT:
			return String.class;
	}
	return Object.class;
}

/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
protected List<Parameter> computeData() {
	ArrayList<Parameter> allParameterList = new ArrayList<Parameter>();
	if (bioModel == null){
		return null;
	}
	if (bIncludeGlobal) {
		allParameterList.addAll(Arrays.asList(bioModel.getModel().getModelParameters()));
	}
	if (bIncludeReactions) {
		for (ReactionStep reactionStep : bioModel.getModel().getReactionSteps()) {
			allParameterList.addAll(Arrays.asList(reactionStep.getKinetics().getUnresolvedParameters()));
			allParameterList.addAll(Arrays.asList(reactionStep.getKinetics().getKineticsParameters()));		
		}
	}
	if (bIncludeSimulationContexts) {
		for (SimulationContext simContext : bioModel.getSimulationContexts()) {
			allParameterList.addAll(getApplicationParameterList(simContext));
		}
	}
	boolean bSearchInactive = searchText == null || searchText.length() == 0;
	String lowerCaseSearchText = bSearchInactive ? null : searchText.toLowerCase();
	ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	for (Parameter parameter : allParameterList) {
		if (bSearchInactive
				|| parameter.getNameScope().getPathDescription().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getName().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getExpression() != null && parameter.getExpression().infix().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getDescription().toLowerCase().contains(lowerCaseSearchText)) {
			parameterList.add(parameter);
		}
	}
	return parameterList;
}

private List<Parameter> getApplicationParameterList(SimulationContext simulationContext) {	
	ArrayList<Parameter> parameterList = new ArrayList<Parameter>();	
	for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
		parameterList.addAll(mapping.computeApplicableParameterList());
	}
	for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
		parameterList.addAll(spec.computeApplicableParameterList());
	}
	for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
		parameterList.addAll(Arrays.asList(elect.getParameters()));
	}	
	
	return parameterList;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		Parameter parameter = getValueAt(row);
		switch (col){
			case COLUMN_PATH:
				return parameter.getNameScope();
			case COLUMN_NAME:
				return parameter.getName();
			case COLUMN_DESCRIPTION: 
				return parameter.getDescription();
			case COLUMN_EXPRESSION:{					
				if (parameter.getExpression() == null) {						
					return null;						
				} 
				boolean bValidateBinding = !(parameter instanceof KineticsParameter);
				return new ScopedExpression(parameter.getExpression(), parameter.getNameScope(), bValidateBinding, null);				
			}
			case COLUMN_UNIT:{
				if (parameter.getUnitDefinition() != null) {
					return parameter.getUnitDefinition().getSymbol();
				}
				return null;				
			}			
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	return null;
}

public boolean isCellEditable(int row, int col) {
	Parameter parameter = getValueAt(row);
	switch (col) {
	case COLUMN_PATH:
		return false;
	case COLUMN_NAME:
		return parameter.isNameEditable();
	case COLUMN_DESCRIPTION:
		return parameter.isDescriptionEditable();
	case COLUMN_EXPRESSION:
		return parameter.isExpressionEditable();
	case COLUMN_UNIT:
		return parameter.isUnitEditable();
	}
	return false;
}

@Override
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	super.propertyChange(evt);
	int[] selectedRows = ownerTable.getSelectedRows();
	Object[] objects = null;
	if (selectedRows.length > 0) {
		objects = new Object[selectedRows.length];
		for (int i = 0; i < objects.length; i ++) {
			objects[i] = getValueAt(selectedRows[i]);
		}
	}
    refreshData();
    if (objects != null) {
    	DocumentEditorSubPanel.setTableSelections(objects, ownerTable, this);
    }
}

public void setValueAt(Object value, int row, int col) {
	if (value == null) {
		return;
	}
	try {
		String inputValue = (String)value;
		inputValue = inputValue.trim();
		Parameter parameter = getValueAt(row);
		switch (col){
			case COLUMN_NAME:{
				if (inputValue.length() == 0) {
					return;
				}
				parameter.setName(inputValue);
				break;
			}
			case COLUMN_EXPRESSION:{
				String newExpressionString = inputValue;
				if (parameter instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
					SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)parameter;
					Expression newExp = null;
					if (newExpressionString == null || newExpressionString.trim().length() == 0) {
						if (scsParm.getRole() == SpeciesContextSpec.ROLE_InitialConcentration
								|| scsParm.getRole() == SpeciesContextSpec.ROLE_DiffusionRate
								|| scsParm.getRole() == SpeciesContextSpec.ROLE_InitialCount) {
							newExp = new Expression(0.0);
						}
					} else {
						newExp = new Expression(newExpressionString);
					}
					scsParm.setExpression(newExp);				
				} else if (parameter instanceof KineticsParameter){
					Expression exp1 = new Expression(inputValue);
					Kinetics kinetics = ((KineticsParameter) parameter).getKinetics();
					kinetics.setParameterValue((Kinetics.KineticsParameter)parameter, exp1);
				} else {
					Expression exp1 = new Expression(inputValue);
					exp1.bindExpression(parameter.getNameScope().getScopedSymbolTable());
					parameter.setExpression(exp1);
				}
				break;
			}
			case COLUMN_UNIT:{
				if (inputValue.length() == 0) {
					parameter.setUnitDefinition(VCUnitDefinition.UNIT_TBD);
				} else if (!parameter.getUnitDefinition().getSymbol().equals(inputValue)){
					parameter.setUnitDefinition(VCUnitDefinition.getInstance(inputValue));
				}
				break;
			}
		}
	} catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(ownerTable, e.getMessage());
	}
}


  public Comparator<Parameter> getComparator(final int col, final boolean ascending) {
	  return new Comparator<Parameter>() {

		public int compare(Parameter parm1, Parameter parm2) {
			int scale = ascending ? 1 : -1;
			switch (col){
				case COLUMN_NAME:
					return scale * parm1.getName().compareToIgnoreCase(parm2.getName());
				case COLUMN_DESCRIPTION:
					return scale * parm1.getDescription().compareToIgnoreCase(parm2.getDescription());
				case COLUMN_EXPRESSION:
					String exp1 = parm1.getExpression() == null ? "" : parm1.getExpression().infix();						
					String exp2 = parm2.getExpression() == null ? "" : parm2.getExpression().infix();						
					return scale * exp1.compareToIgnoreCase(exp2);
				case COLUMN_PATH:
					return scale * parm1.getNameScope().getPathDescription().compareToIgnoreCase(parm2.getNameScope().getPathDescription());
				case COLUMN_UNIT:
					String unit1 = parm1.getUnitDefinition() == null ? "" : parm1.getUnitDefinition().getSymbol();
					String unit2 = parm2.getUnitDefinition() == null ? "" : parm2.getUnitDefinition().getSymbol();
					return scale * unit1.compareToIgnoreCase(unit2);
			}
			return 0;
		}
	  };
  }

@Override
protected void bioModelChange(PropertyChangeEvent evt) {
	super.bioModelChange(evt);
	BioModel oldValue = (BioModel)evt.getOldValue();
	if (oldValue!=null){
		oldValue.getModel().removePropertyChangeListener(this);
		for (Parameter parameter : oldValue.getModel().getModelParameters()) {
			parameter.removePropertyChangeListener(this);
		}
		for (ReactionStep reactionStep : oldValue.getModel().getReactionSteps()){
			reactionStep.removePropertyChangeListener(this);
			reactionStep.getKinetics().removePropertyChangeListener(this);
			for (KineticsParameter kineticsParameter : reactionStep.getKinetics().getKineticsParameters()) {
				kineticsParameter.removePropertyChangeListener(this);
			}
			for (ProxyParameter proxyParameter : reactionStep.getKinetics().getProxyParameters()) {
				proxyParameter.removePropertyChangeListener(this);
			}
			for (UnresolvedParameter unresolvedParameter: reactionStep.getKinetics().getUnresolvedParameters()) {
				unresolvedParameter.removePropertyChangeListener(this);
			}
		}
		for (SimulationContext simulationContext : oldValue.getSimulationContexts()) {
			simulationContext.removePropertyChangeListener(this);
			simulationContext.getGeometryContext().removePropertyChangeListener(this);
			for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
				for (Parameter parameter : mapping.getParameters()) {
					parameter.removePropertyChangeListener(this);
				}
			}
			for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
				spec.removePropertyChangeListener(this);
				for (Parameter parameter : spec.getParameters()) {
					parameter.removePropertyChangeListener(this);
				}
			}
			for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
				elect.removePropertyChangeListener(this);
				for (Parameter parameter : elect.getParameters()) {
					parameter.removePropertyChangeListener(this);
				}
			}
		}
	}
	BioModel newValue = (BioModel)evt.getNewValue();
	if (newValue != null){
		newValue.getModel().addPropertyChangeListener(this);
		for (ModelParameter modelParameter : newValue.getModel().getModelParameters()) {
			modelParameter.addPropertyChangeListener(this);
		}
		for (ReactionStep reactionStep : newValue.getModel().getReactionSteps()){
			reactionStep.addPropertyChangeListener(this);
			reactionStep.getKinetics().addPropertyChangeListener(this);
			for (KineticsParameter kineticsParameter : reactionStep.getKinetics().getKineticsParameters()) {
				kineticsParameter.addPropertyChangeListener(this);
			}
			for (ProxyParameter proxyParameter : reactionStep.getKinetics().getProxyParameters()) {
				proxyParameter.addPropertyChangeListener(this);
			}
			for (UnresolvedParameter unresolvedParameter: reactionStep.getKinetics().getUnresolvedParameters()) {
				unresolvedParameter.addPropertyChangeListener(this);
			}
		}
		for (SimulationContext simulationContext : newValue.getSimulationContexts()) {
			simulationContext.addPropertyChangeListener(this);
			simulationContext.getGeometryContext().addPropertyChangeListener(this);
			for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
				for (Parameter parameter : mapping.getParameters()) {
					parameter.addPropertyChangeListener(this);
				}
			}
			for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
				spec.addPropertyChangeListener(this);
				for (Parameter parameter : spec.getParameters()) {
					parameter.addPropertyChangeListener(this);
				}
			}
			for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
				elect.addPropertyChangeListener(this);
				for (Parameter parameter : elect.getParameters()) {
					parameter.addPropertyChangeListener(this);
				}
			}
		}
	}
}

public String checkInputValue(String inputValue, int row, int column) {
	return null;
}

public SymbolTable getSymbolTable(int row, int column) {
	return null;
}

public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
	return null;
}

public Set<String> getAutoCompletionWords(int row, int column) {
	return null;
}

public final void setIncludeReactionParameters(boolean newValue) {
	if (newValue == bIncludeReactions) {
		return;
	}	
	this.bIncludeReactions = newValue;
	refreshData();
}

public final void setIncludeGlobalParameters(boolean newValue) {
	if (newValue == bIncludeGlobal) {
		return;
	}	
	this.bIncludeGlobal = newValue;
	refreshData();
}

public final void setIncludeSimulationContextParameters(boolean newValue) {
	if (newValue == bIncludeSimulationContexts) {
		return;
	}	
	this.bIncludeSimulationContexts = newValue;
	refreshData();
}

@Override
public int getRowCount() {
	return getDataSize();
}

}