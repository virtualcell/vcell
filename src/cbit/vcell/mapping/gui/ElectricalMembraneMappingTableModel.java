package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.JTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedExpression;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ElectricalMembraneMappingTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final static int NUM_COLUMNS = 4;
	public final static int COLUMN_MEMBRANE = 0;
	public final static int COLUMN_CALCULATE_POTENTIAL = 1;
	public final static int COLUMN_INITIAL_POTENTIAL = 2;
	public final static int COLUMN_SPECIFIC_CAPACITANCE = 3;
	public final static String LABEL_MEMBRANE = "Membrane";
	public final static String LABEL_CALCULATE_POTENTIAL = "Calculate V?";
	public final static String LABEL_INITIAL_POTENTIAL = "V initial";
	public final static String LABEL_SPECIFIC_CAPACITANCE = "specific capacitance (pF/um2)";
	private static String LABELS[] = { LABEL_MEMBRANE, LABEL_CALCULATE_POTENTIAL, LABEL_INITIAL_POTENTIAL, LABEL_SPECIFIC_CAPACITANCE };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private GeometryContext fieldGeometryContext = null;
	private JTable ownerTable = null;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public ElectricalMembraneMappingTableModel(JTable table) {
	super();
	ownerTable = table;
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
public Class<?> getColumnClass(int column) {
	switch (column){
		case COLUMN_MEMBRANE:{
			return String.class;
		}
		case COLUMN_CALCULATE_POTENTIAL:{
			return Boolean.class;
		}
		case COLUMN_INITIAL_POTENTIAL:{
			return ScopedExpression.class;
		}
		case COLUMN_SPECIFIC_CAPACITANCE:{
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
public String getColumnName(int column) {
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}
/**
 * Gets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @return The geometryContext property value.
 * @see #setGeometryContext
 */
public GeometryContext getGeometryContext() {
	return fieldGeometryContext;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/01 10:02:00 AM)
 * @return cbit.vcell.mapping.FeatureMapping
 * @param row int
 */
public MembraneMapping getMembraneMapping(int row) {
	if (getGeometryContext()==null){
		return null;
	}
	int membraneMappingIndex = 0;
	StructureMapping structureMappings[] = getGeometryContext().getStructureMappings();
	for (int i=0;i<structureMappings.length;i++){
		if (structureMappings[i] instanceof MembraneMapping){
			if (membraneMappingIndex==row){
				return (MembraneMapping)structureMappings[i];
			}
			membraneMappingIndex++;
		}
	}
	return null;
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
	if (getGeometryContext()==null){
		return 0;
	}else{
		StructureMapping structureMappings[] = getGeometryContext().getStructureMappings();
		int count = 0;
		for (int i=0;structureMappings!=null && i<structureMappings.length;i++){
			if (structureMappings[i] instanceof MembraneMapping){
				count++;
			}
		}
		return count;
	}
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("ElectricalMembraneMappingTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ElectricalMembraneMappingTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}

	MembraneMapping membraneMapping = getMembraneMapping(row);
	if (membraneMapping == null){
		return null;
	}
	switch (col){
		case COLUMN_MEMBRANE:{
			if (membraneMapping.getStructure()!=null){
				return membraneMapping.getStructure().getName();
			}else{
				return null;
			}
		}
		case COLUMN_CALCULATE_POTENTIAL:{
			return new Boolean(membraneMapping.getCalculateVoltage());
		}
		case COLUMN_INITIAL_POTENTIAL:{
			return new ScopedExpression(membraneMapping.getInitialVoltageParameter().getExpression(),membraneMapping.getInitialVoltageParameter().getNameScope());
		}
		case COLUMN_SPECIFIC_CAPACITANCE:{
			if (membraneMapping.getCalculateVoltage()){
				return new ScopedExpression(membraneMapping.getSpecificCapacitanceParameter().getExpression(),membraneMapping.getSpecificCapacitanceParameter().getNameScope());
			}else{
				return null;
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
	MembraneMapping mm = getMembraneMapping(rowIndex);
	//
	// see if solving for potential (otherwise ignore capacitance).
	//
	if (columnIndex == COLUMN_SPECIFIC_CAPACITANCE){
		if (mm.getCalculateVoltage()){
			return true;
		}else{
			return false;
		}
	}else if (columnIndex == COLUMN_INITIAL_POTENTIAL){
		return true;
	}else if (columnIndex == COLUMN_CALCULATE_POTENTIAL){
		return true;
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
	if (evt.getSource() instanceof cbit.vcell.mapping.ReactionContext
		&& evt.getPropertyName().equals("structureMappings")) {
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof StructureMapping) {
		fireTableRowsUpdated(0,getRowCount()-1);
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
 * Sets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @param geometryContext The new value for the property.
 * @see #getGeometryContext
 */
public void setGeometryContext(GeometryContext geometryContext) {
	GeometryContext oldValue = fieldGeometryContext;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		StructureMapping oldStructureMappings[] = oldValue.getStructureMappings();
		for (int i=0;i<oldStructureMappings.length;i++){
			if (oldStructureMappings[i] instanceof MembraneMapping){
				oldStructureMappings[i].removePropertyChangeListener(this);
			}
		}
	}
	fieldGeometryContext = geometryContext;
	if (geometryContext!=null){
		geometryContext.addPropertyChangeListener(this);
		StructureMapping newStructureMappings[] = geometryContext.getStructureMappings();
		for (int i=0;i<newStructureMappings.length;i++){
			if (newStructureMappings[i] instanceof MembraneMapping){
				newStructureMappings[i].addPropertyChangeListener(this);
			}
		}
	}
	firePropertyChange("geometryContext", oldValue, geometryContext);
	fireTableDataChanged();
}
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ElectricalMembraneMappingTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ElectricalMembraneMappingTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	MembraneMapping membraneMapping = getMembraneMapping(rowIndex);
	switch (columnIndex){
		case COLUMN_CALCULATE_POTENTIAL:{
			boolean bCalculatePotential = ((Boolean)aValue).booleanValue();
			membraneMapping.setCalculateVoltage(bCalculatePotential);
			fireTableRowsUpdated(rowIndex,rowIndex);
			break;
		}
		case COLUMN_INITIAL_POTENTIAL:{
			Expression newExpression = null;
			try {
				if (aValue instanceof String){
					String newExpressionString = (String)aValue;
					newExpression = new Expression(newExpressionString);
				}else if (aValue instanceof ScopedExpression){
					newExpression = ((ScopedExpression)aValue).getExpression();
				}
				membraneMapping.getInitialVoltageParameter().setExpression(newExpression);
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "expression error\n"+e.getMessage());
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "error setting initial potential\n"+e.getMessage());
			}
			break;
		}
		case COLUMN_SPECIFIC_CAPACITANCE:{
			Expression newExpression = null;
			try {
				if (aValue instanceof String){
					String newExpressionString = (String)aValue;
					newExpression = new Expression(newExpressionString);
				}else if (aValue instanceof ScopedExpression){
					newExpression = ((ScopedExpression)aValue).getExpression();
				}
				membraneMapping.getSpecificCapacitanceParameter().setExpression(newExpression);
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "expression error\n"+e.getMessage());
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ownerTable, "error setting capacitance\n"+e.getMessage());
			}
			break;
		}
	}
}
}
