package cbit.vcell.mapping.gui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ElectricalStimulusParameterTableModel extends ManageTableModel implements java.beans.PropertyChangeListener {
	
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
	private ElectricalStimulus fieldElectricalStimulus = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
	private JTable ownerTable = null;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ElectricalStimulusParameterTableModel(JTable table) {
	super();
	ownerTable = table;
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
public ElectricalStimulus getElectricalStimulus() {
	return fieldElectricalStimulus;
}
/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.Parameter
 * @param row int
 */
private Parameter getParameter(int row) {

	if (getElectricalStimulus()==null){
		return null;
	}
	int count = getElectricalStimulus().getElectricalStimulusParameters().length;
	if (row<0 || row>=count){
		throw new RuntimeException("ElectricalStimulusParameterTableModel.getParameter("+row+") out of range ["+0+","+(count-1)+"]");
	}
	if (row==0){
		if (getElectricalStimulus() instanceof VoltageClampStimulus){
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
private List<Parameter> getUnsortedParameters() {

	if (getElectricalStimulus()==null){
		return null;
	}
	int count = getRowCount();
	java.util.ArrayList<Parameter> list = new java.util.ArrayList<Parameter>();
	for (int i = 0; i < count; i++){
		list.add(getParameter(i));
	}
	return list;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		if (col<0 || col>=NUM_COLUMNS){
			throw new RuntimeException("ParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
		}
		if (row<0 || row>=getRowCount()){
			throw new RuntimeException("ParameterTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
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
				if (parameter instanceof ElectricalStimulus.ElectricalStimulusParameter){
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
	Parameter parameter = getParameter(rowIndex);
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
		ElectricalStimulus oldValue = (ElectricalStimulus)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
			Parameter oldParameters[] = oldValue.getElectricalStimulusParameters();
			for (int i = 0; i<oldParameters.length; i++){
				oldParameters[i].removePropertyChangeListener(this);
			}
		}
		ElectricalStimulus newValue = (ElectricalStimulus)evt.getNewValue();
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
	if (evt.getSource() instanceof ElectricalStimulus && evt.getPropertyName().equals("electricalStimulusParameters")) {
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
	if(evt.getSource() instanceof ElectricalStimulus.ElectricalStimulusParameter
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
 * Sets the electricalStimulus property (cbit.vcell.mapping.ElectricalStimulus) value.
 * @param electricalStimulus The new value for the property.
 * @see #getElectricalStimulus
 */
public void setElectricalStimulus(ElectricalStimulus electricalStimulus) {
	ElectricalStimulus oldValue = fieldElectricalStimulus;
	fieldElectricalStimulus = electricalStimulus;
	if (electricalStimulus != null) {
		autoCompleteSymbolFilter = electricalStimulus.getAutoCompleteSymbolFilter();
	}
	firePropertyChange("electricalStimulus", oldValue, electricalStimulus);
}

public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	Parameter parameter = getParameter(rowIndex);
//	try {
		switch (columnIndex){
			case COLUMN_VALUE:{
				try {
					if (aValue instanceof ScopedExpression){
//						Expression exp = ((ScopedExpression)aValue).getExpression();
//						if (parameter instanceof ElectricalStimulus.ElectricalStimulusParameter){
//							ElectricalStimulus.ElectricalStimulusParameter scsParm = (ElectricalStimulus.ElectricalStimulusParameter)parameter;
//							getElectricalStimulus().setParameterValue(scsParm,exp);
//							//fireTableRowsUpdated(rowIndex,rowIndex);
//						}
						throw new RuntimeException("unexpected value type ScopedExpression");
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
					PopupGenerator.showErrorDialog(ownerTable, e.getMessage());
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog(ownerTable, "expression error\n"+e.getMessage());
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
