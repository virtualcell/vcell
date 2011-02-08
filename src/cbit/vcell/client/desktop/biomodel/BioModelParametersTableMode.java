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
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
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
public class BioModelParametersTableMode extends BioModelEditorRightSideTableModel<ApplicationParameter> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_APPLICATION = 0;
	public static final int COLUMN_SCOPE = 1;
	public static final int COLUMN_NAME = 2;
	public static final int COLUMN_DESCRIPTION = 3;
	public static final int COLUMN_EXPRESSION = 4;
	public static final int COLUMN_UNIT = 5;
	private static String LABELS[] = {"Application", "Context", "Name", "Description", "Expression", "Units"};
	private boolean bIncludeReactions = true;
	private boolean bIncludeSimulationContexts = false;
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
	int column = col;
	if (!bIncludeSimulationContexts) {
		column += 1;
	}
	switch (column){
		case COLUMN_APPLICATION:
			return String.class;
		case COLUMN_SCOPE:
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
protected List<ApplicationParameter> computeData() {
	ArrayList<Parameter> modelParameterList = new ArrayList<Parameter>();
	ArrayList<ApplicationParameter> appParameterList0 = new ArrayList<ApplicationParameter>();
	if (bioModel == null){
		return null;
	}
	if (bIncludeGlobal) {
		modelParameterList.addAll(Arrays.asList(bioModel.getModel().getModelParameters()));
	}
	if (bIncludeReactions) {
		for (ReactionStep reactionStep : bioModel.getModel().getReactionSteps()) {
			modelParameterList.addAll(Arrays.asList(reactionStep.getKinetics().getUnresolvedParameters()));
			modelParameterList.addAll(Arrays.asList(reactionStep.getKinetics().getKineticsParameters()));		
		}
	}
	for (Parameter parameter : modelParameterList) {
		appParameterList0.add(new ApplicationParameter(null, parameter));
	}
	if (bIncludeSimulationContexts) {
		for (SimulationContext simContext : bioModel.getSimulationContexts()) {
			appParameterList0.addAll(getApplicationParameterList(simContext));
		}
	}
	boolean bSearchInactive = searchText == null || searchText.length() == 0;
	String lowerCaseSearchText = bSearchInactive ? null : searchText.toLowerCase();
	ArrayList<ApplicationParameter> appParameterList = new ArrayList<ApplicationParameter>();
	for (ApplicationParameter appParameter : appParameterList0) {
		Parameter parameter = appParameter.getParameter();
		SimulationContext simulationContext = appParameter.getSimulationContext();
		if (bSearchInactive || bIncludeSimulationContexts && simulationContext != null && simulationContext.getName().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getNameScope().getConextDescription().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getName().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getExpression() != null && parameter.getExpression().infix().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getDescription().toLowerCase().contains(lowerCaseSearchText)) {
			appParameterList.add(appParameter);
		}
	}
	return appParameterList;
}

private List<ApplicationParameter> getApplicationParameterList(SimulationContext simulationContext) {	
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
	
	ArrayList<ApplicationParameter> appParameterList = new ArrayList<ApplicationParameter>();
	for (Parameter parameter : parameterList) {
		appParameterList.add(new ApplicationParameter(simulationContext, parameter));
	}
	return appParameterList;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		int column = col;
		if (!bIncludeSimulationContexts) {
			column += 1;
		}
		ApplicationParameter appParameter = getValueAt(row);
		Parameter parameter = appParameter.getParameter();
		switch (column){
			case COLUMN_APPLICATION:
				return appParameter.getSimulationContext() == null ? null : appParameter.getSimulationContext().getName();
			case COLUMN_SCOPE:
				return parameter.getNameScope();
			case COLUMN_NAME:
				return parameter.getName();
			case COLUMN_DESCRIPTION: 
				return parameter.getDescription();
			case COLUMN_EXPRESSION:{					
				if (parameter.getExpression() == null) {						
					return null;						
				} 
				return new ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());				
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
	int column = col;
	if (!bIncludeSimulationContexts) {
		column += 1;
	}
	ApplicationParameter appParameter = getValueAt(row);
	Parameter parameter = appParameter.getParameter();	
	switch (column) {
	case COLUMN_APPLICATION:
	case COLUMN_SCOPE:
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
	if (evt.getSource() instanceof Parameter) {
		refreshData();
	} else if (evt.getSource() == bioModel.getModel()) {
		if (evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_PARAMETERS)) {
			refreshData();
		} else if (bIncludeReactions) {
			refreshData();
		}
	} else {
		refreshData();
	}	
}

public void setValueAt(Object value, int row, int col) {
	if (value == null) {
		return;
	}
	int column = col;
	if (!bIncludeSimulationContexts) {
		column += 1;
	}
	try {
		String inputValue = (String)value;
		inputValue = inputValue.trim();
		ApplicationParameter appParameter = getValueAt(row);
		Parameter parameter = appParameter.getParameter();
		switch (column){
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
				} else {
					Expression exp1 = new Expression(inputValue);
					exp1.bindExpression(appParameter.getSimulationContext());
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


  public Comparator<ApplicationParameter> getComparator(final int col, final boolean ascending) {
	  return new Comparator<ApplicationParameter>() {

		public int compare(ApplicationParameter o1, ApplicationParameter o2) {
			int scale = ascending ? 1 : -1;
			Parameter parm1 = o1.getParameter();
			Parameter parm2 = o2.getParameter();
			int column = col;
			if (!bIncludeSimulationContexts) {
				column += 1;
			}
			switch (column){
				case COLUMN_APPLICATION:
					String name1 = o1.getSimulationContext() == null ? "" : o1.getSimulationContext().getName();
					String name2 = o2.getSimulationContext() == null ? "" : o2.getSimulationContext().getName();
					return scale * name1.compareToIgnoreCase(name2);
				case COLUMN_NAME:
					return scale * parm1.getName().compareToIgnoreCase(parm2.getName());
				case COLUMN_DESCRIPTION:
					return scale * parm1.getDescription().compareToIgnoreCase(parm2.getDescription());
				case COLUMN_EXPRESSION:
					String exp1 = parm1.getExpression() == null ? "" : parm1.getExpression().infix();						
					String exp2 = parm2.getExpression() == null ? "" : parm2.getExpression().infix();						
					return scale * exp1.compareToIgnoreCase(exp2);
				case COLUMN_SCOPE:
					return scale * parm1.getNameScope().getName().compareToIgnoreCase(parm2.getNameScope().getName());
				case COLUMN_UNIT:
					String unit1 = parm1.getUnitDefinition() == null ? "" : parm1.getUnitDefinition().getSymbol();
					String unit2 = parm2.getUnitDefinition() == null ? "" : parm2.getUnitDefinition().getSymbol();
					return scale * unit1.compareToIgnoreCase(unit2);
			}
			return 0;
		}
	  };
  }

protected void simulationContextChange(PropertyChangeEvent evt) {
//	super.simulationContextChange(evt);
	SimulationContext oldValue = (SimulationContext)evt.getOldValue();
	if (oldValue!=null){
		oldValue.getModel().removePropertyChangeListener(this);
		for (Parameter parameter : oldValue.getModel().getModelParameters()) {
			parameter.removePropertyChangeListener(this);
		}
		for (StructureMapping mapping : oldValue.getGeometryContext().getStructureMappings()) {
			for (Parameter parameter : mapping.getParameters()) {
				parameter.removePropertyChangeListener(this);
			}
		}
		for (SpeciesContextSpec spec : oldValue.getReactionContext().getSpeciesContextSpecs()) {
			spec.removePropertyChangeListener(this);
			for (Parameter parameter : spec.getParameters()) {
				parameter.removePropertyChangeListener(this);
			}
		}
		for (ElectricalStimulus elect : oldValue.getElectricalStimuli()) {
			elect.removePropertyChangeListener(this);
			for (Parameter parameter : elect.getParameters()) {
				parameter.removePropertyChangeListener(this);
			}
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
	}
	SimulationContext newValue = (SimulationContext)evt.getNewValue();
	if (newValue != null){
		newValue.getModel().addPropertyChangeListener(this);
		for (ModelParameter modelParameter : newValue.getModel().getModelParameters()) {
			modelParameter.addPropertyChangeListener(this);
		}
		for (StructureMapping mapping : newValue.getGeometryContext().getStructureMappings()) {
			for (Parameter parameter : mapping.getParameters()) {
				parameter.addPropertyChangeListener(this);
			}
		}
		for (SpeciesContextSpec spec : newValue.getReactionContext().getSpeciesContextSpecs()) {
			spec.addPropertyChangeListener(this);
			for (Parameter parameter : spec.getParameters()) {
				parameter.addPropertyChangeListener(this);
			}
		}
		for (ElectricalStimulus elect : newValue.getElectricalStimuli()) {
			elect.addPropertyChangeListener(this);
			for (Parameter parameter : elect.getParameters()) {
				parameter.addPropertyChangeListener(this);
			}
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
	fireTableStructureChanged();
}

@Override
public String getColumnName(int column) {
	if (bIncludeSimulationContexts) {
		return LABELS[column];
	}
	
	return LABELS[column + 1];
}

@Override
public int getColumnCount() {
	if (bIncludeSimulationContexts) {
		return LABELS.length;
	}
	return LABELS.length - 1;
}

@Override
public int getRowCount() {
	return getDataSize();
}

}