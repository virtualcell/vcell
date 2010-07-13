package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTable;

import org.vcell.util.gui.sorttable.ManageTableModel;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.data.DataContext;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.mapping.SimulationContext;
/**
 * Insert the type's description here.
 * @author: 
 */
public class DataSymbolsTableModel extends ManageTableModel implements java.beans.PropertyChangeListener {
	public static final int NUM_COLUMNS = 2;
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_TYPE = 1;
	private String LABELS[] = { "Name", "Type"};
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext fieldSimulationContext = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;
	private JTable ownerTable = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public DataSymbolsTableModel(JTable table) {
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
		case COLUMN_TYPE:{
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

public String getColumnName(int column) {
	try {
		return LABELS[column];
	} catch (Throwable exc) {
		System.out.println("WARNING - no such column index: " + column);
		exc.printStackTrace(System.out);
		return null;
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
	if (getSimulationContext()==null){
		return 0;
	}else{
		return getSimulationContext().getDataContext().getDataSymbols().length;
	}
}

/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
private SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}

private void refreshData() {
	if (getSimulationContext()==null){
		return;
	}
	int count = getRowCount();
	rows.clear();
	for (int i = 0; i < count; i++){
		rows.add(getSimulationContext().getDataContext().getDataSymbols()[i]);
	}
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		if (row<0 || row>=getRowCount()){
			throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
		}
		if (col<0 || col>=NUM_COLUMNS){
			throw new RuntimeException("SpeciesContextSpecsTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
		}
		if (getData().size() <= row){
			refreshData();
		}	
		DataSymbol ds = getDataSymbol(row);
		switch (col){
			case COLUMN_NAME:{
				return ds.getName();
			}
			case COLUMN_TYPE:{
				if (ds instanceof FieldDataSymbol) {
					return "Field Data Symbol";		// populate the column with some text for now
				} else {
					return null;
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
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("DataSymbolsTableModel.isCellEditable(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("DataSymbolsTableModel.isCellEditable(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	switch (columnIndex){
		case COLUMN_NAME:{
			return false;
		}
		case COLUMN_TYPE:{
			return false;
		}
		default:{
			return false;
		}
	}
}

/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() instanceof DataContext
		&& evt.getPropertyName().equals("dataSymbols")) {

		refreshData();
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof DataSymbol && evt.getPropertyName().equals("name")) {
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
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	if (oldValue != null){
		oldValue.getDataContext().removePropertyChangeListener(this);
		for (DataSymbol ds : oldValue.getDataContext().getDataSymbols()){
			ds.removePropertyChangeListener(this);
		}
	}
	fieldSimulationContext = simulationContext;	
	if (simulationContext!=null){
		simulationContext.getDataContext().addPropertyChangeListener(this);
		for (DataSymbol ds : simulationContext.getDataContext().getDataSymbols()){
			ds.addPropertyChangeListener(this);
		}
		autoCompleteSymbolFilter  = simulationContext.getAutoCompleteSymbolFilter();
		refreshData();
	}
	firePropertyChange("simulationContext", oldValue, simulationContext);
	fireTableStructureChanged();
	fireTableDataChanged();
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("DataSymbolsTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("DataSymbolsTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	DataSymbol dataSymbol = getDataSymbol(rowIndex);
	switch (columnIndex){
		case COLUMN_NAME:{
			dataSymbol.setName((String)aValue);
			break;
		}
		case COLUMN_TYPE:{
			break;
		}
	}
}

@SuppressWarnings("unchecked")
@Override
public void sortColumn(final int col, final boolean ascending) {
	Collections.sort(rows, new Comparator<DataSymbol>() {	
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(DataSymbol ds1, DataSymbol ds2 ){			
			
			switch (col){
				case COLUMN_NAME:{
					String name1 = ds1.getName();
					String name2 = ds2.getName();
					if (ascending){
						return name1.compareToIgnoreCase(name2);
					}else{
						return name2.compareToIgnoreCase(name1);
					}
				}
				case COLUMN_TYPE:{
					break;
				}
			}
			return 1;
		};
	});	
	fireTableDataChanged();
}

public DataSymbol getDataSymbol(int row) {
	return (DataSymbol)getData().get(row);
}

}