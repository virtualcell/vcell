package cbit.vcell.client.desktop.biomodel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;

import org.vcell.util.gui.DialogUtils;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class BioModelEditorGlobalParameterTableModel extends BioModelEditorRightSideTableModel<ModelParameter> implements java.beans.PropertyChangeListener {

	private class ParameterColumnComparator implements Comparator<ModelParameter> {
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
		public int compare(ModelParameter parm1, ModelParameter parm2){
			
			switch (index){
				case COLUMN_NAME:{
					if (ascending){
						return parm1.getName().compareToIgnoreCase(parm2.getName());
					}else{
						return parm2.getName().compareToIgnoreCase(parm1.getName());
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
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_EXPRESSION = 1;
	public static final int COLUMN_UNIT = 2;
	public static final int COLUMN_ANNOTATION = 3;
	private static String LABELS[] = {"Name", "Expression", "Unit" , "Annotation" };

/**
 * ReactionSpecsTableModel constructor comment.
 */
public BioModelEditorGlobalParameterTableModel(JTable table) {
	super(table);
	setColumns(LABELS);
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
		case COLUMN_EXPRESSION:{
			return ScopedExpression.class;
		}
		case COLUMN_UNIT:{
			return String.class;
		}
		case COLUMN_ANNOTATION:{
			return String.class;
		}
		default:{
			return Object.class;
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
protected List<ModelParameter> computeData() {
	if (getModel() == null){
		return null;
	} else {
		return Arrays.asList(getModel().getModelParameters());
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		if (row < getDataSize()) {
			Parameter parameter = getValueAt(row);
			switch (col){
				case COLUMN_NAME:{
					return parameter.getName();
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
			if (col == COLUMN_NAME) {
				return ADD_NEW_HERE_TEXT;
			}			
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:27:46 AM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	return true;
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


/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("model")) {
		Model oldValue = (Model)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
			ReactionStep[] oldRS = oldValue.getReactionSteps();
			for (int i = 0; oldRS!=null && i < oldRS.length; i++){
				oldRS[i].removePropertyChangeListener(this);
				oldRS[i].getKinetics().removePropertyChangeListener(this);
				for (int j = 0; j < oldRS[i].getKinetics().getKineticsParameters().length; j++) {
					oldRS[i].getKinetics().getKineticsParameters()[j].removePropertyChangeListener(this);
				}
				for (int j = 0; j < oldRS[i].getKinetics().getProxyParameters().length; j++) {
					oldRS[i].getKinetics().getProxyParameters()[j].removePropertyChangeListener(this);
				}
				for (int j = 0; j < oldRS[i].getKinetics().getUnresolvedParameters().length; j++) {
					oldRS[i].getKinetics().getUnresolvedParameters()[j].removePropertyChangeListener(this);
				}
			}
		}
		Model newValue = (Model)evt.getNewValue();
		if (newValue!=null){
			newValue.addPropertyChangeListener(this);
			ReactionStep[] newRS = newValue.getReactionSteps();
			for (int i = 0; newRS!=null && i < newRS.length; i++){
				newRS[i].addPropertyChangeListener(this);
				newRS[i].getKinetics().addPropertyChangeListener(this);
				for (int j = 0; j < newRS[i].getKinetics().getKineticsParameters().length; j++) {
					newRS[i].getKinetics().getKineticsParameters()[j].addPropertyChangeListener(this);
				}
				for (int j = 0; j < newRS[i].getKinetics().getProxyParameters().length; j++) {
					newRS[i].getKinetics().getProxyParameters()[j].addPropertyChangeListener(this);
				}
				for (int j = 0; j < newRS[i].getKinetics().getUnresolvedParameters().length; j++) {
					newRS[i].getKinetics().getUnresolvedParameters()[j].addPropertyChangeListener(this);
				}
			}
		}
		refreshData();
	} else if (evt.getSource() == getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_PARAMETERS)) {
		refreshData();
	}
	if (evt.getSource() instanceof Parameter) {
		fireTableDataChanged();
	}
}

public void setValueAt(Object value, int row, int column) {
	try {
		ModelParameter parameter = null;
		if (row < getDataSize()) {
			parameter = getValueAt(row);
		} else {
			parameter = getModel().new ModelParameter(getModel().getFreeModelParamName(), new Expression(0), Model.ROLE_UserDefined, VCUnitDefinition.UNIT_TBD);
		}
		String inputValue = (String)value;
		switch (column){
			case COLUMN_NAME:{
				if (!inputValue.equals(ADD_NEW_HERE_TEXT)) {
					parameter.setName(inputValue);
				}
				break;
			}
			case COLUMN_EXPRESSION:{
				Expression exp1 = new Expression(inputValue);
				exp1.bindExpression(getModel());
				parameter.setExpression(exp1);
				break;
			}
			case COLUMN_UNIT:{	
				if (!parameter.getUnitDefinition().getSymbol().equals(inputValue)){
					parameter.setUnitDefinition(VCUnitDefinition.getInstance(inputValue));
				}
				break;
			}
			case COLUMN_ANNOTATION:{
				parameter.setModelParameterAnnotation(inputValue);
				break;
			}
		}
		if (row == getDataSize()) {
			getModel().addModelParameter(parameter);
		}
	} catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(ownerTable, e.getMessage());
	}
}


  public Comparator<ModelParameter> getComparator(int col, boolean ascending) {
    return new ParameterColumnComparator(col, ascending);
  }

public String checkInputValue(String inputValue, int row, int column) {
	ModelParameter modelParameter = null;
	if (row >= 0 && row < getDataSize()) {
		modelParameter = getValueAt(row);
	}
	switch (column) {
	case COLUMN_NAME:
		if (modelParameter == null || !modelParameter.getName().equals(inputValue)) {
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
}