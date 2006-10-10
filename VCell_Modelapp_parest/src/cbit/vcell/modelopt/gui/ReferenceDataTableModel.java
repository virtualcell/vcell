package cbit.vcell.modelopt.gui;
import java.util.*;



/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.messaging.admin.sorttable.ManageTableModel;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class ReferenceDataTableModel extends ManageTableModel implements java.beans.PropertyChangeListener {

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
			if (o1 instanceof Double && o2 instanceof Double){
				if (ascending){
					return ((Double)o1).compareTo((Double)o2);
				}else{
					return ((Double)o1).compareTo((Double)o2);
				}
			}else{
				return 1;
			}
		}
	}
	private final int COLUMN_INDEX = 0;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private final int indexes[] = new int[0];
	private cbit.vcell.opt.ReferenceData fieldReferenceData = null;

/**
 * ReactionSpecsTableModel constructor comment.
 */
public ReferenceDataTableModel() {
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
	return Double.class;
}


/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	if (getReferenceData()!=null){
		return getReferenceData().getColumnNames().length;
	}else{
		return 0;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public String getColumnName(int column) {
	if (getReferenceData()==null){
		return "";
	}else{
		return getReferenceData().getColumnNames()[column];
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
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.opt.ReferenceData getReferenceData() {
	return fieldReferenceData;
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
private List getUnsortedRows() {

	if (getReferenceData()==null){
		return null;
	}
	
	int count = getReferenceData().getNumRows();

	java.util.ArrayList list = new java.util.ArrayList();
	for (int i = 0; i < count; i++){
		list.add(getReferenceData().getRowData(i));
	}
	return list;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (col<0 || col>=getColumnCount()){
		throw new RuntimeException("ReferenceDataTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(getColumnCount()-1)+"]");
	}
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("ReferenceDataTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	return new Double(getReferenceData().getRowData(row)[col]);
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
	return false;
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
	if (evt.getSource() == this && evt.getPropertyName().equals("referenceData")) {
		setData(getUnsortedRows());
		fireTableStructureChanged();
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
public void setReferenceData(cbit.vcell.opt.ReferenceData referenceData) {
	cbit.vcell.opt.ReferenceData oldValue = fieldReferenceData;
	fieldReferenceData = referenceData;
	firePropertyChange("referenceData", oldValue, referenceData);
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	return;
}


  public void sortColumn(int col, boolean ascending)
  {
    Collections.sort(rows,
      new ParameterColumnComparator(col, ascending));
  }
}