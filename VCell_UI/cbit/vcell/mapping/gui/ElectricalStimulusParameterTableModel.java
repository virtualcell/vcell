package cbit.vcell.mapping.gui;

import cbit.vcell.model.*;
import java.util.*;


import cbit.vcell.model.Model;
import cbit.vcell.modelapp.ElectricalStimulus;
import cbit.vcell.modelapp.SpeciesContextSpec;
import cbit.vcell.modelapp.ElectricalStimulus.ElectricalStimulusParameter;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
import cbit.vcell.geometry.*;
import cbit.vcell.parser.ExpressionException;
import cbit.util.BeanUtils;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ElectricalStimulusParameterTableModel extends cbit.vcell.messaging.admin.sorttable.ManageTableModel implements java.beans.PropertyChangeListener {

	static {
		System.out.println("ElectricalStimulusParameterTableModel: artifically filtering out voltage or current parameters that are not applicable");
	}

	
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
				case COLUMN_VALUE:{
					String infix1 = (parm1.getExpression()!=null)?(parm1.getExpression().infix()):("");
					String infix2 = (parm2.getExpression()!=null)?(parm2.getExpression().infix()):("");
					if (ascending){
						return infix1.compareToIgnoreCase(infix2);
					}else{
						return infix2.compareToIgnoreCase(infix1);
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
	private final int NUM_COLUMNS = 4;
	private final int COLUMN_DESCRIPTION = 0;
	private final int COLUMN_NAME = 1;
	private final int COLUMN_VALUE = 2;
	private final int COLUMN_UNIT = 3;
	private String LABELS[] = { "description", "Parameter", "Expression", "Units" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private final int indexes[] = new int[0];
	private cbit.vcell.modelapp.ElectricalStimulus fieldElectricalStimulus = null;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ElectricalStimulusParameterTableModel() {
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
		case COLUMN_DESCRIPTION:{
			return String.class;
		}
		case COLUMN_UNIT:{
			return String.class;
		}
		case COLUMN_VALUE:{
			return cbit.vcell.parser.ScopedExpression.class;
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
 * Gets the electricalStimulus property (cbit.vcell.mapping.ElectricalStimulus) value.
 * @return The electricalStimulus property value.
 * @see #setElectricalStimulus
 */
public cbit.vcell.modelapp.ElectricalStimulus getElectricalStimulus() {
	return fieldElectricalStimulus;
}
/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private cbit.vcell.model.Parameter getParameter(int row) {

	if (getElectricalStimulus()==null){
		return null;
	}
	int count = getElectricalStimulus().getElectricalStimulusParameters().length;
	if (row<0 || row>=count){
		throw new RuntimeException("ElectricalStimulusParameterTableModel.getParameter("+row+") out of range ["+0+","+(count-1)+"]");
	}
	if (row==0){
		if (getElectricalStimulus() instanceof cbit.vcell.modelapp.VoltageClampStimulus){
			return getElectricalStimulus().getElectricalStimulusParameterFromRole(ElectricalStimulus.ROLE_Voltage);
		}else{
			return getElectricalStimulus().getElectricalStimulusParameterFromRole(ElectricalStimulus.ROLE_Current);
		}
	}else{
		return getElectricalStimulus().getElectricalStimulusParameters(row+1);
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
	if (getElectricalStimulus()==null){
		return 0;
	}else{
		return getElectricalStimulus().getElectricalStimulusParameters().length-1;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private List getUnsortedParameters() {

	if (getElectricalStimulus()==null){
		return null;
	}
	int count = getRowCount();
	java.util.ArrayList list = new java.util.ArrayList();
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
	cbit.vcell.model.Parameter parameter = (cbit.vcell.model.Parameter)getData().get(row);
	switch (col){
		case COLUMN_NAME:{
			//return getBioModel().getModel().getNameScope().getSymbolName(parameter);
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
			//return new cbit.vcell.parser.ScopedExpression(parameter.getExpression(),getBioModel().getModel().getNameScope());
			if (parameter instanceof ElectricalStimulus.ElectricalStimulusParameter){
				ElectricalStimulus.ElectricalStimulusParameter scsParm = (ElectricalStimulus.ElectricalStimulusParameter)parameter;
				if (parameter.getExpression()==null){
					return new String("");
				}else{
					return new cbit.vcell.parser.ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());
				}
			}
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
	cbit.vcell.model.Parameter parameter = getParameter(rowIndex);
	if (columnIndex == COLUMN_NAME){
		return parameter.isNameEditable();
	}else if (columnIndex == COLUMN_DESCRIPTION){
		return false;
	}else if (columnIndex == COLUMN_UNIT){
		return parameter.isUnitEditable();
	}else if (columnIndex == COLUMN_VALUE){
		return parameter.isExpressionEditable();
	}else{
		return false;
	}
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
	if (evt.getSource() == this && evt.getPropertyName().equals("electricalStimulus")) {
		cbit.vcell.modelapp.ElectricalStimulus oldValue = (cbit.vcell.modelapp.ElectricalStimulus)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
			Parameter oldParameters[] = oldValue.getElectricalStimulusParameters();
			for (int i = 0; i<oldParameters.length; i++){
				oldParameters[i].removePropertyChangeListener(this);
			}
		}
		cbit.vcell.modelapp.ElectricalStimulus newValue = (cbit.vcell.modelapp.ElectricalStimulus)evt.getNewValue();
		if (newValue!=null){
			newValue.addPropertyChangeListener(this);
			Parameter newParameters[] = newValue.getElectricalStimulusParameters();
			for (int i = 0; i<newParameters.length; i++){
				newParameters[i].addPropertyChangeListener(this);
			}
		}
		setData(getUnsortedParameters());
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.modelapp.ElectricalStimulus && evt.getPropertyName().equals("electricalStimulusParameters")) {
		Parameter oldParameters[] = (Parameter[])evt.getOldValue();
		for (int i = 0; i<oldParameters.length; i++){
			oldParameters[i].removePropertyChangeListener(this);
		}
		Parameter newParameters[] = (Parameter[])evt.getNewValue();
		for (int i = 0; i<newParameters.length; i++){
			newParameters[i].addPropertyChangeListener(this);
		}
		setData(getUnsortedParameters());
		fireTableDataChanged();
	}
	if(evt.getSource() instanceof cbit.vcell.modelapp.ElectricalStimulus.ElectricalStimulusParameter
		&& evt.getPropertyName().equals("expression")){
		fireTableDataChanged();
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
 * Sets the electricalStimulus property (cbit.vcell.mapping.ElectricalStimulus) value.
 * @param electricalStimulus The new value for the property.
 * @see #getElectricalStimulus
 */
public void setElectricalStimulus(cbit.vcell.modelapp.ElectricalStimulus electricalStimulus) {
	cbit.vcell.modelapp.ElectricalStimulus oldValue = fieldElectricalStimulus;
	fieldElectricalStimulus = electricalStimulus;
	firePropertyChange("electricalStimulus", oldValue, electricalStimulus);
}
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.model.Parameter parameter = getParameter(rowIndex);
//	try {
		switch (columnIndex){
			case COLUMN_VALUE:{
				try {
					if (aValue instanceof cbit.vcell.parser.ScopedExpression){
						Expression exp = ((cbit.vcell.parser.ScopedExpression)aValue).getExpression();
						if (parameter instanceof ElectricalStimulus.ElectricalStimulusParameter){
							ElectricalStimulus.ElectricalStimulusParameter scsParm = (ElectricalStimulus.ElectricalStimulusParameter)parameter;
							getElectricalStimulus().setParameterValue(scsParm,exp);
							//fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}else if (aValue instanceof String) {
						String newExpressionString = (String)aValue;
						if (parameter instanceof ElectricalStimulus.ElectricalStimulusParameter){
							ElectricalStimulus.ElectricalStimulusParameter scsParm = (ElectricalStimulus.ElectricalStimulusParameter)parameter;
							getElectricalStimulus().setParameterValue(scsParm,new Expression(newExpressionString));
							//fireTableRowsUpdated(rowIndex,rowIndex);
						}
					}
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog(e.getMessage());
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
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
    Collections.sort(rows,
      new ParameterColumnComparator(col, ascending));
  }
}
