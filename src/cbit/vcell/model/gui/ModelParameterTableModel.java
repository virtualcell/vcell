package cbit.vcell.model.gui;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.DefaultSortTableModel;

import cbit.gui.ScopedExpression;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ModelParameterTableModel extends DefaultSortTableModel<Parameter> implements java.beans.PropertyChangeListener {

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
				case COLUMN_VALUE:{
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
	public static final int COLUMN_VALUE = 3;
	public static final int COLUMN_UNIT = 4;
	public static final int COLUMN_ANNOTATION = 5;
	private static String LABELS[] = { "Context", "Name", "Description", "Expression", "Units" , "Annotation" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Model fieldModel = null;
	private JTable ownerTable = null;
	private boolean bGlobalOnly = false;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public ModelParameterTableModel(JTable table, boolean flag) {
	super(LABELS);
	ownerTable = table;
	bGlobalOnly = flag;
	addPropertyChangeListener(this);
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
		case COLUMN_NAME:{
			return String.class;
		}
		case COLUMN_SCOPE:{
			return String.class;
		}
		case COLUMN_UNIT:{
			return String.class;
		}
		case COLUMN_VALUE:{
			return ScopedExpression.class;
		}
		case COLUMN_DESCRIPTION:{
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
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
private Model getModel() {
	return fieldModel;
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
	return rows.size();
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private void refreshData() {
	rows.clear();
	if (getModel()== null){
		return;
	}
	for (Parameter parameter : getModel().getModelParameters()) {
		rows.add(parameter);
	}
	if (!bGlobalOnly) {
		for (ReactionStep reactionStep : getModel().getReactionSteps()) {
			for (Parameter parameter : reactionStep.getKinetics().getUnresolvedParameters()) {
				rows.add(parameter);
			}
			for (Parameter parameter : reactionStep.getKinetics().getKineticsParameters()) {
				rows.add(parameter);
			}
		}
	}
	resortColumn();
	fireTableDataChanged();
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		if (col<0 || col>=getColumnCount()){
			throw new RuntimeException("ParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(getColumnCount()-1)+"]");
		}
		if (row<0 || row>=getRowCount()){
			throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		Parameter parameter = getValueAt(row);
		switch (col){
			case COLUMN_NAME:{
				//return getBioModel().getModel().getNameScope().getSymbolName(parameter);
				return parameter.getName();
			}
			case COLUMN_SCOPE:{
				if (parameter instanceof Kinetics.UnresolvedParameter){
					return "unresolved";
				}else if (parameter.getNameScope()==null){
					return "null";
				}else{
					if (parameter instanceof ModelParameter) {
						return "Model";
					} else if (parameter instanceof KineticsParameter) {
						return "Reaction : " + parameter.getNameScope().getName();
					} else {
						return parameter.getNameScope().getName();
					}
				}
			}
			case COLUMN_UNIT:{
				if (parameter.getUnitDefinition()!=null){
					return parameter.getUnitDefinition().getSymbol();
				}else{
					return "null";
				}
			}
			case COLUMN_DESCRIPTION:{
				return parameter.getDescription();
			}
			case COLUMN_VALUE:{
				//return new cbit.vcell.parser.ScopedExpression(parameter.getExpression(),getBioModel().getModel().getNameScope());
				
				if (parameter.getExpression() == null) {
					return ""; 
				} else {
					return new ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());
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
				// TODO need to add ReservedSymbols
				return null;
				//return (parameter instanceof Kinetics.KineticsParameter?parameter.getDescription():"");
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
	Parameter parameter = getValueAt(rowIndex);
	if (columnIndex == COLUMN_NAME){
		return parameter.isNameEditable();
	}else if (columnIndex == COLUMN_SCOPE){
		return false;
	}else if (columnIndex == COLUMN_UNIT){
		return parameter.isUnitEditable();
	}else if (columnIndex == COLUMN_VALUE){
		return parameter.isExpressionEditable();
	}else if (columnIndex == COLUMN_DESCRIPTION){
		return false;
	}else if (columnIndex == COLUMN_ANNOTATION){
		return false;
	}else{
		return false;
	}
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
	} else if (evt.getSource() == getModel() && evt.getPropertyName().equals(Model.MODEL_PARAMETERS_PROPERTY_NAME)) {
		refreshData();
	}
	if (evt.getSource() == getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_REACTION_STEPS)) {
		ReactionStep[] oldRS = (ReactionStep[])evt.getOldValue();
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
		ReactionStep[] newRS = (ReactionStep[])evt.getNewValue();
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
		refreshData();
	}
	if (evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("kinetics")) {
		Kinetics oldValue = (Kinetics)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
			for (int j = 0; j < oldValue.getKineticsParameters().length; j++) {
				oldValue.getKineticsParameters()[j].removePropertyChangeListener(this);
			}
			for (int j = 0; j < oldValue.getProxyParameters().length; j++) {
				oldValue.getProxyParameters()[j].removePropertyChangeListener(this);
			}
			for (int j = 0; j < oldValue.getUnresolvedParameters().length; j++) {
				oldValue.getUnresolvedParameters()[j].removePropertyChangeListener(this);
			}
		}
		Kinetics newValue = (Kinetics)evt.getNewValue();
		if (newValue!=null){
			newValue.addPropertyChangeListener(this);
			for (int j = 0; j < newValue.getKineticsParameters().length; j++) {
				newValue.getKineticsParameters()[j].addPropertyChangeListener(this);
			}
			for (int j = 0; j < newValue.getProxyParameters().length; j++) {
				newValue.getProxyParameters()[j].addPropertyChangeListener(this);
			}
			for (int j = 0; j < newValue.getUnresolvedParameters().length; j++) {
				newValue.getUnresolvedParameters()[j].addPropertyChangeListener(this);
			}
		}
		refreshData();
	}
	if (evt.getSource() instanceof Parameter) {
		fireTableDataChanged();
	}

	if (evt.getSource() instanceof Kinetics && evt.getPropertyName().equals("kineticsParameters")) {
		KineticsParameter[] old = (KineticsParameter[])evt.getOldValue();
		for (int i = 0; old!=null && i < old.length; i++){
			old[i].removePropertyChangeListener(this);
		}
		KineticsParameter[] newVal = (KineticsParameter[])evt.getNewValue();
		for (int i = 0; newVal!=null && i < newVal.length; i++){
			newVal[i].addPropertyChangeListener(this);
		}
		refreshData();
	}
	if (evt.getSource() instanceof Kinetics && evt.getPropertyName().equals("proxyParameters")) {
		KineticsProxyParameter[] old = (KineticsProxyParameter[])evt.getOldValue();
		for (int i = 0; old!=null && i < old.length; i++){
			old[i].removePropertyChangeListener(this);
		}
		KineticsProxyParameter[] newVal = (KineticsProxyParameter[])evt.getNewValue();
		for (int i = 0; newVal!=null && i < newVal.length; i++){
			newVal[i].addPropertyChangeListener(this);
		}
		refreshData();
	}
	if (evt.getSource() instanceof Kinetics && evt.getPropertyName().equals("unresolvedParameters")) {
		UnresolvedParameter[] old = (UnresolvedParameter[])evt.getOldValue();
		for (int i = 0; old!=null && i < old.length; i++){
			old[i].removePropertyChangeListener(this);
		}
		UnresolvedParameter[] newVal = (UnresolvedParameter[])evt.getNewValue();
		for (int i = 0; newVal!=null && i < newVal.length; i++){
			newVal[i].addPropertyChangeListener(this);
		}
		refreshData();
	}
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(Model model) {
	Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (columnIndex<0 || columnIndex>=getColumnCount()){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(getColumnCount()-1)+"]");
	}
	Parameter parameter = getValueAt(rowIndex);
//	try {
		switch (columnIndex){
			case COLUMN_NAME:{
				try {
					if (aValue instanceof String){
						String newName = (String)aValue;
						if (!parameter.getName().equals(newName)){
							if (parameter instanceof KineticsParameter){
								Kinetics kinetics = ((ReactionStep) parameter.getNameScope().getScopedSymbolTable()).getKinetics();
								kinetics.renameParameter(parameter.getName(),newName);
								fireTableRowsUpdated(rowIndex,rowIndex);
							} else if (parameter instanceof ModelParameter){
								// Model model = (Model) parameter.getNameScope().getScopedSymbolTable();
								((ModelParameter)parameter).setName(newName);
								fireTableRowsUpdated(rowIndex,rowIndex);
							}
						}
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Error changing parameter name:\n"+e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Error changing parameter name:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_VALUE:{
				try {
					if (aValue instanceof ScopedExpression){
//						Expression exp = ((ScopedExpression)aValue).getExpression();
//						if (parameter instanceof KineticsParameter){
//							Kinetics kinetics = ((ReactionStep) parameter.getNameScope().getScopedSymbolTable()).getKinetics();
//							kinetics.setParameterValue((KineticsParameter)parameter,exp);
//							kinetics.resolveUndefinedUnits();
//							fireTableRowsUpdated(rowIndex,rowIndex);
//						} else if (parameter instanceof ModelParameter){
//							exp.bindExpression(getModel());
//							((ModelParameter)parameter).setExpression(exp);
//							fireTableRowsUpdated(rowIndex,rowIndex);
//						}
						throw new RuntimeException("unexpected value type ScopedExpression");
					}else if (aValue instanceof String) {
						String newExpressionString = (String)aValue;
						if (parameter instanceof KineticsParameter){
							Kinetics kinetics = ((ReactionStep) parameter.getNameScope().getScopedSymbolTable()).getKinetics();
							kinetics.setParameterValue((KineticsParameter)parameter,new Expression(newExpressionString));
							kinetics.resolveUndefinedUnits();
							fireTableRowsUpdated(rowIndex,rowIndex);
						} else if (parameter instanceof ModelParameter){
							Expression exp1 = new Expression(newExpressionString);
							exp1.bindExpression(getModel());
							((ModelParameter)parameter).setExpression(exp1);
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Error:\n"+e.getMessage());
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Expression error:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_UNIT:{
				try {
					if (aValue instanceof String){
						String newUnitSymbol = (String)aValue;
						if (!parameter.getUnitDefinition().getSymbol().equals(newUnitSymbol)){
							if (parameter instanceof KineticsParameter){
								Kinetics.KineticsParameter kineticsParameter = (Kinetics.KineticsParameter)parameter;
								kineticsParameter.setUnitDefinition(VCUnitDefinition.getInstance(newUnitSymbol));
								kineticsParameter.getKinetics().resolveUndefinedUnits();
								fireTableRowsUpdated(rowIndex,rowIndex);
							} else if (parameter instanceof ModelParameter){
//								Model model = (Model) parameter.getNameScope().getScopedSymbolTable();
								((ModelParameter)parameter).setUnitDefinition(VCUnitDefinition.getInstance(newUnitSymbol));
								fireTableRowsUpdated(rowIndex,rowIndex);
							}
						}
					}
				}catch (VCUnitException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "Error changing parameter units:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_ANNOTATION:{
				if (aValue instanceof String){
					String annotation = (String)aValue;
					if (parameter instanceof Kinetics.KineticsParameter){
						ReactionStep rxStep = getEditableAnnotationReactionStep(rowIndex);
						if(rxStep != null){
							VCMetaData vcMetaData = rxStep.getModel().getVcMetaData();
							vcMetaData.setFreeTextAnnotation(rxStep, annotation);
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					} else if (parameter instanceof ModelParameter){
						((ModelParameter)parameter).setModelParameterAnnotation(annotation);
						fireTableRowsUpdated(rowIndex,rowIndex);
					}

				}
				break;
			}
		}
//	}catch (java.beans.PropertyVetoException e){
//		e.printStackTrace(System.out);
//	}
}


  public void sortColumn(int col, boolean ascending)
  {
    Collections.sort(rows, new ParameterColumnComparator(col, ascending));
  }

}