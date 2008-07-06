package cbit.vcell.model.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedExpression;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ModelParameterTableModel extends cbit.vcell.messaging.admin.ManageTableModel implements java.beans.PropertyChangeListener {

	private class ParameterColumnComparator implements Comparator {
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
		public int compare(Object o1, Object o2){
			
			cbit.vcell.model.Parameter parm1 = (cbit.vcell.model.Parameter)o1;
			cbit.vcell.model.Parameter parm2 = (cbit.vcell.model.Parameter)o2;
			
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
	private final int NUM_COLUMNS = 6;
	private final int COLUMN_SCOPE = 0;
	private final int COLUMN_DESCRIPTION = 1;
	private final int COLUMN_NAME = 2;
	private final int COLUMN_VALUE = 3;
	private final int COLUMN_UNIT = 4;
	public static final int COLUMN_ANNOTATION = 5;
	private String LABELS[] = { "Context", "Description", "Name", "Expression", "Units" , "Annotation" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.model.Model fieldModel = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public ModelParameterTableModel() {
	super();
	addPropertyChangeListener(this);
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
public Class getColumnClass(int column) {
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
			return cbit.vcell.parser.ScopedExpression.class;
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
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return NUM_COLUMNS;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}


/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private cbit.vcell.model.Parameter getParameter(int row) {

	if (getModel()==null){
		return null;
	}
	if (row<0){
		throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range");
	}
	int count = getModel().getModelParameters().length;
	int index = row;
	if (index<count){
		return getModel().getModelParameters()[index];
	}
	
	// need to include ReservedSymbols as well ...
	
	index -= count;	// not in ModelParameters, subtract offset and move on
	ReactionStep[] reactionSteps = getModel().getReactionSteps();
	for (int i = 0; index>=0 && i < reactionSteps.length; i++){
		count = reactionSteps[i].getKinetics().getUnresolvedParameters().length;
		if (index < count){
			return reactionSteps[i].getKinetics().getUnresolvedParameters()[index];
		}
		index -= count;
		count = reactionSteps[i].getKinetics().getKineticsParameters().length;
		if (index < count){
			return reactionSteps[i].getKinetics().getKineticsParameters()[index];
		}
		index -= count;
	}
	throw new RuntimeException("rowIndex = "+row+" not found");
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
public List<Parameter> getUnsortedParameters() {

	if (getModel()==null){
		return null;
	}

	int count = getModel().getModelParameters().length;
	ReactionStep[] reactionSteps = getModel().getReactionSteps();
	for (int i = 0; i < reactionSteps.length; i++){
		count += reactionSteps[i].getKinetics().getUnresolvedParameters().length;
		count += reactionSteps[i].getKinetics().getKineticsParameters().length;
	}

	ArrayList<Parameter> list = new ArrayList<Parameter>();
	for (int i = 0; i < count; i++){
		list.add(getParameter(i));
	}
	return list;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	Parameter parameter = (Parameter)getData().get(row);
	switch (col){
		case COLUMN_NAME:{
			//return getBioModel().getModel().getNameScope().getSymbolName(parameter);
			return parameter.getName();
		}
		case COLUMN_SCOPE:{
			if (parameter instanceof cbit.vcell.model.Kinetics.UnresolvedParameter){
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
			return new ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());
		}
		case COLUMN_ANNOTATION:{
			if (parameter instanceof ModelParameter) {
				return
				(((ModelParameter)parameter).getModelParameterAnnotation() != null
					? ((ModelParameter)parameter).getModelParameterAnnotation()
					: "");
			} else {
				ReactionStep rxStep = getEditableAnnotationReactionStep(row);
				if(rxStep != null && parameter instanceof Kinetics.KineticsParameter){
					KineticsParameter param = (KineticsParameter)parameter;
					if (param.getKinetics().getAuthoritativeParameter() == param){
						return (rxStep.getAnnotation() != null?rxStep.getAnnotation():"");
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
	cbit.vcell.model.Parameter parameter = (cbit.vcell.model.Parameter)getData().get(rowIndex);
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

	cbit.vcell.model.Parameter sortedParameter = (cbit.vcell.model.Parameter)getData().get(rowIndex);
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
		cbit.vcell.model.Model oldValue = (cbit.vcell.model.Model)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
			ReactionStep[] oldRS = oldValue.getReactionSteps();
			for (int i = 0; oldRS!=null && i < oldRS.length; i++){
				oldRS[i].removePropertyChangeListener(this);
				oldRS[i].getKinetics().removePropertyChangeListener(this);
			}
		}
		cbit.vcell.model.Model newValue = (cbit.vcell.model.Model)evt.getNewValue();
		if (newValue!=null){
			newValue.addPropertyChangeListener(this);
			ReactionStep[] newRS = newValue.getReactionSteps();
			for (int i = 0; newRS!=null && i < newRS.length; i++){
				newRS[i].addPropertyChangeListener(this);
				newRS[i].getKinetics().addPropertyChangeListener(this);
			}
		}
		setData(getUnsortedParameters());
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof Model && evt.getPropertyName().equals("reactionSteps")) {
		ReactionStep[] oldRS = (ReactionStep[])evt.getOldValue();
		for (int i = 0; oldRS!=null && i < oldRS.length; i++){
			oldRS[i].removePropertyChangeListener(this);
			oldRS[i].getKinetics().removePropertyChangeListener(this);
		}
		ReactionStep[] newRS = (ReactionStep[])evt.getNewValue();
		for (int i = 0; newRS!=null && i < newRS.length; i++){
			newRS[i].addPropertyChangeListener(this);
			newRS[i].getKinetics().addPropertyChangeListener(this);
		}
		setData(getUnsortedParameters());
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof ReactionStep && evt.getPropertyName().equals("kinetics")) {
		Kinetics oldValue = (Kinetics)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
		}
		Kinetics newValue = (Kinetics)evt.getNewValue();
		if (newValue!=null){
			newValue.addPropertyChangeListener(this);
		}
		setData(getUnsortedParameters());
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.model.Kinetics && evt.getPropertyName().equals("kineticsParameters")) {
		setData(getUnsortedParameters());
		fireTableRowsUpdated(0, getRowCount()-1);
//		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.model.Kinetics && evt.getPropertyName().equals("unresolvedParameters")) {
		setData(getUnsortedParameters());
		fireTableRowsUpdated(0, getRowCount()-1);
//		fireTableDataChanged();
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
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.model.Parameter parameter = (cbit.vcell.model.Parameter)getData().get(rowIndex);
//	try {
		switch (columnIndex){
			case COLUMN_NAME:{
				try {
					if (aValue instanceof String){
						String newName = (String)aValue;
						if (!parameter.getName().equals(newName)){
							if (parameter instanceof KineticsParameter){
								cbit.vcell.model.Kinetics kinetics = ((ReactionStep) parameter.getNameScope().getScopedSymbolTable()).getKinetics();
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
					cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing parameter name:\n"+e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing parameter name:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_VALUE:{
				try {
					if (aValue instanceof cbit.vcell.parser.ScopedExpression){
						Expression exp = ((cbit.vcell.parser.ScopedExpression)aValue).getExpression();
						if (parameter instanceof KineticsParameter){
							cbit.vcell.model.Kinetics kinetics = ((ReactionStep) parameter.getNameScope().getScopedSymbolTable()).getKinetics();
							kinetics.setParameterValue((KineticsParameter)parameter,exp);
							kinetics.resolveUndefinedUnits();
							fireTableRowsUpdated(rowIndex,rowIndex);
						} else if (parameter instanceof ModelParameter){
							((ModelParameter)parameter).setExpression(exp);
							fireTableRowsUpdated(rowIndex,rowIndex);
						}

					}else if (aValue instanceof String) {
						String newExpressionString = (String)aValue;
						if (parameter instanceof KineticsParameter){
							cbit.vcell.model.Kinetics kinetics = ((ReactionStep) parameter.getNameScope().getScopedSymbolTable()).getKinetics();
							kinetics.setParameterValue((KineticsParameter)parameter,new Expression(newExpressionString));
							kinetics.resolveUndefinedUnits();
							fireTableRowsUpdated(rowIndex,rowIndex);
						} else if (parameter instanceof ModelParameter){
							((ModelParameter)parameter).setExpression(new Expression(newExpressionString));
							fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("Error:\n"+e.getMessage());
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("Expression error:\n"+e.getMessage());
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
								kineticsParameter.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.getInstance(newUnitSymbol));
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
					PopupGenerator.showErrorDialog("Error changing parameter units:\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_ANNOTATION:{
				if (aValue instanceof String){
					String annotation = (String)aValue;
					if (parameter instanceof cbit.vcell.model.Kinetics.KineticsParameter){
						ReactionStep rxStep = getEditableAnnotationReactionStep(rowIndex);
						if(rxStep != null){
							rxStep.setAnnotation(annotation);
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
    fireTableRowsUpdated(0,rows.size()-1);//Added to make sure formatted table cells display with enough space
  }
}