package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
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
public class BioModelEditorGlobalParameterTableModel extends BioModelEditorRightSideTableModel<Parameter> implements java.beans.PropertyChangeListener {

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
				case COLUMN_DESCRIPTION:{
					if (ascending){
						return parm1.getDescription().compareToIgnoreCase(parm2.getDescription());
					}else{
						return parm2.getDescription().compareToIgnoreCase(parm1.getDescription());
					}
					//break;
				}
				case COLUMN_EXPRESSION:{
					if (ascending){
						return parm1.getExpression().infix().compareToIgnoreCase(parm2.getExpression().infix());
					}else{
						return parm2.getExpression().infix().compareToIgnoreCase(parm1.getExpression().infix());
					}
					//break;
				}
				case COLUMN_SCOPE:{
					if (ascending){
						return parm1.getNameScope().getName().compareToIgnoreCase(parm2.getNameScope().getName());
					}else{
						return parm2.getNameScope().getName().compareToIgnoreCase(parm1.getNameScope().getName());
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
	public static final int COLUMN_SCOPE = 0;
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_DESCRIPTION = 2;
	public static final int COLUMN_EXPRESSION = 3;
	public static final int COLUMN_UNIT = 4;
	public static final int COLUMN_ANNOTATION = 5;
	private static String LABELS[] = { "Context", "Name", "Description", "Expression", "Units" , "Annotation" };
	private boolean bGlobalOnly = false;
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public BioModelEditorGlobalParameterTableModel(EditorScrollTable table, boolean bGlobalOnly) {
	super(table);
	setColumns(LABELS);
	this.bGlobalOnly = bGlobalOnly;
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
	if (getModel() == null){
		return null;
	} 
	ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	for (Parameter parameter : getModel().getModelParameters()) {
		if (searchText == null || searchText.length() == 0 || parameter.getName().indexOf(searchText) >= 0
				|| parameter.getExpression() != null && parameter.getExpression().infix().indexOf(searchText) >= 0
				|| parameter.getDescription().indexOf(searchText) >= 0) {
			parameterList.add(parameter);
		}
	}
	if (!bGlobalOnly) {
		for (ReactionStep reactionStep : getModel().getReactionSteps()) {
			for (Parameter parameter : reactionStep.getKinetics().getUnresolvedParameters()) {
				if (searchText == null || searchText.length() == 0 || parameter.getName().indexOf(searchText) >= 0
						|| parameter.getExpression() != null && parameter.getExpression().infix().indexOf(searchText) >= 0
						|| parameter.getDescription().indexOf(searchText) >= 0) {
					parameterList.add(parameter);
				}
			}
			for (Parameter parameter : reactionStep.getKinetics().getKineticsParameters()) {
				if (searchText == null || searchText.length() == 0 || parameter.getName().indexOf(searchText) >= 0
						|| parameter.getExpression() != null && parameter.getExpression().infix().indexOf(searchText) >= 0
						|| parameter.getDescription().indexOf(searchText) >= 0) {
					parameterList.add(parameter);
				}
			}
		}
	}
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
						return ""; 
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
				if (bioModel != null) {
					return bioModel.getModel().getNameScope();
				}
			}
			if (col == COLUMN_NAME) {
				return ADD_NEW_HERE_TEXT;
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
		if (column == COLUMN_SCOPE || column == COLUMN_DESCRIPTION){
			return false;
		}
		if (column == COLUMN_NAME) {
			return parameter.isNameEditable();
		}
		
		if (column == COLUMN_EXPRESSION) {
			return parameter.isExpressionEditable();
		}
		if (column == COLUMN_UNIT){
			return parameter.isUnitEditable();
		}
		if (column == COLUMN_ANNOTATION) {
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
	if (evt.getSource() == getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_PARAMETERS)) {
		refreshData();
	}
	if (evt.getSource() instanceof Parameter) {
		fireTableDataChanged();
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
					Expression exp1 = new Expression(inputValue);
					exp1.bindExpression(getModel());
					parameter.setExpression(exp1);
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
				if (inputValue.length() == 0 || inputValue.equals(ADD_NEW_HERE_TEXT)) {
					return;
				}
				ModelParameter modelParameter = getModel().new ModelParameter(inputValue, new Expression(0), Model.ROLE_UserDefined, VCUnitDefinition.UNIT_TBD);
				getModel().addModelParameter(modelParameter);
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
    return new ParameterColumnComparator(col, ascending);
  }

public String checkInputValue(String inputValue, int row, int column) {
	Parameter parameter = null;
	if (row >= 0 && row < getDataSize()) {
		parameter = getValueAt(row);
	}
	switch (column) {
	case COLUMN_NAME:
		if (parameter == null || !parameter.getName().equals(inputValue)) {
			if (getModel().getModelParameter(inputValue) != null) {
				return "Global parameter '" + inputValue + "' already exists!";
			}
		}
		break;
	}
	return null;
}

public SymbolTable getSymbolTable(int row, int column) {
	// TODO Auto-generated method stub
	return null;
}

public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
	// TODO Auto-generated method stub
	return null;
}

public Set<String> getAutoCompletionWords(int row, int column) {
	// TODO Auto-generated method stub
	return null;
}

@Override
protected void bioModelChange(PropertyChangeEvent evt) {
	super.bioModelChange(evt);
	BioModel oldValue = (BioModel)evt.getOldValue();
	if (oldValue!=null){
		for (ModelParameter modelParameter : oldValue.getModel().getModelParameters()) {
			modelParameter.removePropertyChangeListener(this);
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
	BioModel newValue = (BioModel)evt.getNewValue();
	if (newValue!=null){
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
	}
}
}