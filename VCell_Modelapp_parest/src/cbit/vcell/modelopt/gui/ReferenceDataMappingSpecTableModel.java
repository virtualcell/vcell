package cbit.vcell.modelopt.gui;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.parser.SymbolTableEntry;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ReferenceDataMappingSpecTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 2;
	private final int COLUMN_DATACOLUMNNAME = 0;
	private final int COLUMN_MODELENTITY = 1;
	private String LABELS[] = { "Data Name", "Model Association" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.modelopt.ModelOptimizationSpec fieldModelOptimizationSpec = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public ReferenceDataMappingSpecTableModel() {
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
		case COLUMN_DATACOLUMNNAME:{
			return String.class;
		}
		case COLUMN_MODELENTITY:{
			return SymbolTableEntry.class;
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
		throw new RuntimeException("ConstraintSolverTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}


/**
 * Gets the modelOptimizationSpec property (cbit.vcell.modelopt.ModelOptimizationSpec) value.
 * @return The modelOptimizationSpec property value.
 * @see #setModelOptimizationSpec
 */
public cbit.vcell.modelopt.ModelOptimizationSpec getModelOptimizationSpec() {
	return fieldModelOptimizationSpec;
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
	if (getModelOptimizationSpec()==null || getModelOptimizationSpec().getReferenceDataMappingSpecs()==null){
		return 0;
	}else{
		return getModelOptimizationSpec().getReferenceDataMappingSpecs().length;
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("ReferenceDataMappingSpecTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("ReferenceDataMappingSpecTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	cbit.vcell.modelopt.ReferenceDataMappingSpec referenceDataMappingSpec = getModelOptimizationSpec().getReferenceDataMappingSpecs()[row];
	switch (col){
		case COLUMN_DATACOLUMNNAME:{
			return referenceDataMappingSpec.getReferenceDataColumnName();
		}
		case COLUMN_MODELENTITY:{
			return referenceDataMappingSpec.getModelObject();
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
	if (columnIndex == COLUMN_DATACOLUMNNAME){
		return false;
	}else if (columnIndex == COLUMN_MODELENTITY){
		return false;
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
	if (evt.getSource() == this && evt.getPropertyName().equals("modelOptimizationSpec")) {
		cbit.vcell.modelopt.ModelOptimizationSpec oldValue = (ModelOptimizationSpec)evt.getOldValue();
		if (oldValue!=null){
			oldValue.removePropertyChangeListener(this);
			ReferenceDataMappingSpec[] refDataMappingSpecs = oldValue.getReferenceDataMappingSpecs();
			for (int i = 0;refDataMappingSpecs!=null && i < refDataMappingSpecs.length; i++){
				refDataMappingSpecs[i].removePropertyChangeListener(this);
			}
		}
		cbit.vcell.modelopt.ModelOptimizationSpec newValue = (ModelOptimizationSpec)evt.getNewValue();
		if (newValue!=null){
			newValue.addPropertyChangeListener(this);
			ReferenceDataMappingSpec[] refDataMappingSpecs = newValue.getReferenceDataMappingSpecs();
			for (int i = 0;refDataMappingSpecs!=null && i < refDataMappingSpecs.length; i++){
				refDataMappingSpecs[i].addPropertyChangeListener(this);
			}
		}
		fireTableDataChanged();
	}
	if (evt.getSource() == getModelOptimizationSpec() && evt.getPropertyName().equals("referenceDataMappingSpecs")) {
		ReferenceDataMappingSpec[] oldSpecs = (ReferenceDataMappingSpec[])evt.getOldValue();
		for (int i = 0; oldSpecs!=null && i < oldSpecs.length; i++){
			oldSpecs[i].removePropertyChangeListener(this);
		}
		ReferenceDataMappingSpec[] newSpecs = (ReferenceDataMappingSpec[])evt.getNewValue();
		for (int i = 0; newSpecs!=null && i < newSpecs.length; i++){
			newSpecs[i].addPropertyChangeListener(this);
		}
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof ReferenceDataMappingSpec){
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
 * Sets the modelOptimizationSpec property (cbit.vcell.modelopt.ModelOptimizationSpec) value.
 * @param modelOptimizationSpec The new value for the property.
 * @see #getModelOptimizationSpec
 */
public void setModelOptimizationSpec(cbit.vcell.modelopt.ModelOptimizationSpec modelOptimizationSpec) {
	cbit.vcell.modelopt.ModelOptimizationSpec oldValue = fieldModelOptimizationSpec;
	fieldModelOptimizationSpec = modelOptimizationSpec;
	firePropertyChange("modelOptimizationSpec", oldValue, modelOptimizationSpec);
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ParameterTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ParameterTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	switch (columnIndex){
		case COLUMN_MODELENTITY:{
			//try {
				//ReferenceDataMappingSpec referenceDataMappingSpec = getModelOptimizationSpec().getReferenceDataMappingSpecs()[rowIndex];
				//referenceDataMappingSpec.setModelObject((SymbolTableEntry)aValue);
			//}catch (Exception e){
				//e.printStackTrace(System.out);
			//}
		}
	}
}
}