package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Kinetics;
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
public class ApplicationParameterTableMode extends BioModelEditorApplicationRightSideTableModel<Parameter> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_SCOPE = 0;
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_DESCRIPTION = 2;
	public static final int COLUMN_EXPRESSION = 3;
	public static final int COLUMN_UNIT = 4;
	public static final int COLUMN_ANNOTATION = 5;
	private static String LABELS[] = { "Context", "Name", "Description", "Expression", "Units"/* , "Annotation" */};
	private boolean bIncludeReactionParameters = false;
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ApplicationParameterTableMode(ScrollTable table, boolean bIncludeReactionParameters) {
	super(table);
	setColumns(LABELS);
	this.bIncludeReactionParameters = bIncludeReactionParameters;
}

@Override
public int getRowCount() {
	return getDataSize();
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_SCOPE:{
			return NameScope.class;
		}
		case COLUMN_NAME:{
			return String.class;
		}
		case COLUMN_DESCRIPTION:{
			return String.class;
		}
		case COLUMN_EXPRESSION:{
			return ScopedExpression.class;
		}
		case COLUMN_UNIT:{
			return String.class;
		}
		case COLUMN_ANNOTATION:{
			return String.class;
		}
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
	if (simulationContext == null){
		return null;
	}
	Model model = simulationContext.getModel();
	allParameterList.addAll(Arrays.asList(model.getModelParameters()));
	if (bIncludeReactionParameters) {
		for (ReactionStep reactionStep : model.getReactionSteps()) {
			allParameterList.addAll(Arrays.asList(reactionStep.getKinetics().getUnresolvedParameters()));
			allParameterList.addAll(Arrays.asList(reactionStep.getKinetics().getKineticsParameters()));		
		}
	}
	for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
		allParameterList.addAll(mapping.computeApplicableParameterList());
	}
	for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
		allParameterList.addAll(spec.computeApplicableParameterList());
	}
	for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
		allParameterList.addAll(Arrays.asList(elect.getParameters()));
	}
	ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	for (Parameter parameter : allParameterList) {
		if (searchText == null || searchText.length() == 0) {
			parameterList.add(parameter);
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();		
			if (parameter.getNameScope().getConextDescription().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getName().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getExpression() != null && parameter.getExpression().infix().toLowerCase().contains(lowerCaseSearchText)
				|| parameter.getDescription().toLowerCase().contains(lowerCaseSearchText)) {
				parameterList.add(parameter);
			}
		}
	}
	// later, events,
	
	return parameterList;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		if (row < getDataSize()) {
			Parameter parameter = getValueAt(row);
			switch (col){
				case COLUMN_SCOPE:{
					return parameter.getNameScope();
				}
				case COLUMN_NAME:{
					return parameter.getName();
				}
				case COLUMN_DESCRIPTION: {
					return parameter.getDescription();
				}					
				case COLUMN_EXPRESSION:{					
					if (parameter.getExpression() == null) {						
						return null;						
					} else {
						return new ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());
					}
				}
				case COLUMN_UNIT:{
					if (parameter.getUnitDefinition()!=null){
						return parameter.getUnitDefinition().getSymbol();
					}else{
						return "null";
					}
				}
				case COLUMN_ANNOTATION:{
					if (parameter instanceof ModelParameter) {
						String modelParameterAnnotation = ((ModelParameter)parameter).getModelParameterAnnotation();
						return (modelParameterAnnotation != null ? modelParameterAnnotation	: "");
					} else {
						ReactionStep rxStep = getEditableAnnotationReactionStep(row);
						if(rxStep != null && parameter instanceof Kinetics.KineticsParameter){
							KineticsParameter param = (KineticsParameter)parameter;
							if (param.getKinetics().getAuthoritativeParameter() == param){
								VCMetaData vcMetaData = rxStep.getModel().getVcMetaData();
								return (vcMetaData != null ? vcMetaData.getFreeTextAnnotation(rxStep) : "");
							}
						}
					}
					break;
				}
			}
		} else {
			if (col == COLUMN_SCOPE) {
				if (simulationContext != null) {
					return simulationContext.getModel().getNameScope();
				}
			}
			if (col == COLUMN_NAME) {
				return BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT;
			}
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	return null;
}

public boolean isCellEditable(int row, int column) {
	if (row < getDataSize()) {
		Parameter parameter = getValueAt(row);
		if (parameter instanceof KineticsParameter || parameter instanceof UnresolvedParameter) {
			return false;
		}
		switch (column) {
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
		case COLUMN_ANNOTATION:
			return parameter instanceof ModelParameter;
		}
		return false;
	} 
	return column == COLUMN_NAME;
}


public ReactionStep getEditableAnnotationReactionStep(int rowIndex){

	Parameter sortedParameter = getValueAt(rowIndex);
	if(sortedParameter instanceof Kinetics.KineticsParameter){
		KineticsParameter param = (KineticsParameter)sortedParameter;
		if (param.getKinetics().getAuthoritativeParameter() == param){
			return param.getKinetics().getReactionStep();
		}
	}
	return null;
}

/**
 * isSortable method comment.
 */
public boolean isSortable(int col) {
	return (col != COLUMN_ANNOTATION);
}

@Override
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	super.propertyChange(evt);
	if (simulationContext == null) {
		return;
	}
	if (evt.getSource() instanceof Parameter) {
		refreshData();
	} else if (evt.getSource() == simulationContext.getModel()) {
		if (evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_PARAMETERS)) {
			refreshData();
		} else if (bIncludeReactionParameters) {
			refreshData();
		}
	} else {
		refreshData();
	}	
}

public void setValueAt(Object value, int row, int column) {
	if (value == null) {
		return;
	}
	try {
		String inputValue = (String)value;
		inputValue = inputValue.trim();
		if (row < getDataSize()) {
			Parameter parameter = getValueAt(row);
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
						exp1.bindExpression(simulationContext);
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
				case COLUMN_ANNOTATION:{
					if (parameter instanceof ModelParameter) {
						((ModelParameter)parameter).setModelParameterAnnotation(inputValue);
					}
					break;
				}
			}
		} else {
			switch (column) {
			case COLUMN_NAME: {
				if (inputValue.length() == 0 || inputValue.equals(BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT)) {
					return;
				}
				ModelParameter modelParameter = simulationContext.getModel().new ModelParameter(inputValue, new Expression(0), Model.ROLE_UserDefined, VCUnitDefinition.UNIT_TBD);
				simulationContext.getModel().addModelParameter(modelParameter);
				break;
			}
			}
		}
	} catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(ownerTable, e.getMessage());
	}
}


  public Comparator<Parameter> getComparator(int col, boolean ascending) {
    return new BioModelEditorModelParameterTableModel.ParameterColumnComparator(col, ascending);
  }

@Override
protected void simulationContextChange(PropertyChangeEvent evt) {
	super.simulationContextChange(evt);
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
	if (newValue == bIncludeReactionParameters) {
		return;
	}	
	this.bIncludeReactionParameters = newValue;
	refreshData();
}
}