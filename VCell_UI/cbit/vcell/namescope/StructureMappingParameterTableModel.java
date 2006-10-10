package cbit.vcell.namescope;


import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import cbit.vcell.model.Parameter;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.modelapp.StructureMapping;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class StructureMappingParameterTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 4;
	private final int COLUMN_DESCRIPTION = 0;
	private final int COLUMN_NAME = 1;
	private final int COLUMN_VALUE = 2;
	private final int COLUMN_UNITS = 3;
	private String LABELS[] = { "Description", "Parameter", "Expression", "Units" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.modelapp.StructureMapping fieldStructureMapping = null;
	private boolean fieldBGeoMappingParams = true;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public StructureMappingParameterTableModel() {
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
 * Gets the bGeoMappingParams property (boolean) value.
 * @return The bGeoMappingParams property value.
 * @see #setBGeoMappingParams
 */
public boolean getBGeoMappingParams() {
	return fieldBGeoMappingParams;
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
		case COLUMN_UNITS:{
			return String.class;
		}
		case COLUMN_VALUE:{
			return cbit.vcell.parser.gui.ScopedExpression.class;
		}
		case COLUMN_DESCRIPTION:{
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
		throw new RuntimeException("StructureMappingParameterTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}
/**
 * getValueAt method comment.
 */
private cbit.vcell.model.Parameter getParameter(int row) {
	if (row < 0 || row >= getRowCount()){
		throw new RuntimeException("StructureMappingParameterTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	cbit.vcell.model.Parameter parameter = null;
	if (!getBGeoMappingParams()) {
		// if parameters are membrane potential option parameters (SpecCapacitance, Init Volt),
		// they are the first 2 among the list of 4 StructureMapping params.
		parameter = getStructureMapping().getParameters(row); 
	} else {
		// if parameters are geometry mapping parameters (SurfToVolRatio, VolFrac),
		// they are the last 2 among the list of 4 StructureMapping params, hence adding 2 to rowIndex
		parameter = getStructureMapping().getParameters(row+2);		
	}		
	return parameter;
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
	// If the boolean bGeoMapping is true, it represents the Geometry Mapping params (SurfToVolRatio,
	// VolFrac), hence there will be 2 rows.
	// If bGeoMapping is false, it represents Membrane Potential Options (InitVolt, SpecificCapacitance),
	// hence there will be 2 rows.
	
	return 2;
}
/**
 * Gets the structureMapping property (cbit.vcell.mapping.StructureMapping) value.
 * @return The structureMapping property value.
 * @see #setStructureMapping
 */
public cbit.vcell.modelapp.StructureMapping getStructureMapping() {
	return fieldStructureMapping;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (col < 0 || col >= NUM_COLUMNS){
		throw new RuntimeException("StructureMappingParameterTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.model.Parameter parameter = getParameter(row);
	switch (col){
		case COLUMN_NAME:{
			return parameter.getName();
		}
		case COLUMN_UNITS:{
			if (parameter.getUnitDefinition() != null){
				return parameter.getUnitDefinition().getSymbol();
			}else{
				return "??";
			}
		}
		case COLUMN_VALUE:{
			return new cbit.vcell.parser.gui.ScopedExpression(parameter.getExpression(),parameter.getNameScope(),parameter.isExpressionEditable());
		}
		case COLUMN_DESCRIPTION:{
			return parameter.getDescription();
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
	}else if (columnIndex == COLUMN_UNITS){
		return parameter.isUnitEditable();
	}else if (columnIndex == COLUMN_DESCRIPTION){
		return false;
	}else if (columnIndex == COLUMN_VALUE){
		return parameter.isExpressionEditable();
	}else{
		return false;
	}
}
/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("kinetics")) {
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof cbit.vcell.model.Kinetics && evt.getPropertyName().equals("kineticsParameters")) {
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
 * Sets the bGeoMappingParams property (boolean) value.
 * @param bGeoMappingParams The new value for the property.
 * @see #getBGeoMappingParams
 */
public void setBGeoMappingParams(boolean bGeoMappingParams) {
	fieldBGeoMappingParams = bGeoMappingParams;
}
/**
 * Sets the structureMapping property (cbit.vcell.mapping.StructureMapping) value.
 * @param structureMapping The new value for the property.
 * @see #getStructureMapping
 */
public void setStructureMapping(cbit.vcell.modelapp.StructureMapping structureMapping) {
	cbit.vcell.modelapp.StructureMapping oldValue = fieldStructureMapping;
	fieldStructureMapping = structureMapping;
	firePropertyChange("structureMapping", oldValue, structureMapping);
}
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex < 0 || rowIndex >= getRowCount()){
		throw new RuntimeException("StructureMappingParameterTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex < 0 || columnIndex >= NUM_COLUMNS){
		throw new RuntimeException("StructureMappingParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	Parameter parameter = getParameter(rowIndex);
//	try {
		switch (columnIndex){
			case COLUMN_NAME:{
				try {
					if (aValue instanceof String){
						String newName = (String)aValue;
						if (!parameter.getName().equals(newName)){
							if (parameter instanceof StructureMapping.StructureMappingParameter){
								StructureMapping.StructureMappingParameter smParm = (StructureMapping.StructureMappingParameter)parameter;
								smParm.setName(newName);
								fireTableRowsUpdated(rowIndex,rowIndex);
							}
						}
					}
				} catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing parameter name\n"+e.getMessage());
				}
				break;
			}
			case COLUMN_VALUE:{
				try {
					if (aValue instanceof cbit.vcell.parser.gui.ScopedExpression){
						IExpression exp = ((cbit.vcell.parser.gui.ScopedExpression)aValue).getExpression();
						if (parameter instanceof StructureMapping.StructureMappingParameter){
							StructureMapping.StructureMappingParameter smParm = (StructureMapping.StructureMappingParameter)parameter;
							smParm.setExpression(exp);
						}
					}else if (aValue instanceof String) {
						String newExpressionString = (String)aValue;
						if (parameter instanceof StructureMapping.StructureMappingParameter){
							StructureMapping.StructureMappingParameter smParm = (StructureMapping.StructureMappingParameter)parameter;
							smParm.setExpression(ExpressionFactory.createExpression(newExpressionString));
						}
					}
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Error changing parameter value\n"+e.getMessage());
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("Expression error\n"+e.getMessage());
				}
				break;
			}
		}
//	}catch (java.beans.PropertyVetoException e){
//		e.printStackTrace(System.out);
//	}
}
}
