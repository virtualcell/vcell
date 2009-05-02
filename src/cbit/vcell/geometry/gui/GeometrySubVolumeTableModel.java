package cbit.vcell.geometry.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.BeanUtils;

import cbit.vcell.parser.Expression;
import cbit.vcell.geometry.*;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class GeometrySubVolumeTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener,org.vcell.util.gui.TableCellValidator {
	private final int NUM_COLUMNS = 2;
	private final int COLUMN_NAME = 0;
	private final int COLUMN_VALUE = 1;
	private String LABELS[] = { "Name", "Value" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private int[] colormap = cbit.image.DisplayAdapterService.createContrastColorModel();

/**
 * ReactionSpecsTableModel constructor comment.
 */
public GeometrySubVolumeTableModel() {
	super();
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
			return cbit.vcell.geometry.SubVolume.class;
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
		throw new RuntimeException("GeometrySubVolumeTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}


/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
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
	if (getGeometry()==null){
		return 0;
	}else{
		return getGeometry().getGeometrySpec().getNumSubVolumes();
	}
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	if (row<0 || row>=getRowCount()){
		throw new RuntimeException("GeometrySubVolumeTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("GeometrySubVolumeTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	SubVolume subVolume = getGeometry().getGeometrySpec().getSubVolumes(row);
	switch (col){
		case COLUMN_NAME:{
			return subVolume;
		}
		case COLUMN_VALUE:{
			if (subVolume instanceof AnalyticSubVolume){
				return new cbit.vcell.parser.ScopedExpression(((AnalyticSubVolume)subVolume).getExpression(),cbit.vcell.model.ReservedSymbol.TIME.getNameScope(),true);
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
	if (columnIndex == COLUMN_NAME){
		return true;
	}else if (columnIndex == COLUMN_VALUE && getGeometry()!=null){
		SubVolume subVolume = getGeometry().getGeometrySpec().getSubVolumes(rowIndex);
		//
		// the "value" column is only editable if it is an expression for a AnalyticSubVolume
		//
		return (subVolume instanceof AnalyticSubVolume);
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
	if (evt.getSource() == this && evt.getPropertyName().equals("geometry")) {
		fireTableStructureChanged();
	}
	if (evt.getSource() instanceof GeometrySpec && evt.getPropertyName().equals("subVolumes")) {
		fireTableDataChanged();
	}
	if (evt.getSource() instanceof SubVolume) {
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
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	cbit.vcell.geometry.Geometry oldValue = fieldGeometry;
	if (oldValue != null){
		oldValue.getGeometrySpec().removePropertyChangeListener(this);
	}
	fieldGeometry = geometry;
	if (geometry != null){
		geometry.getGeometrySpec().addPropertyChangeListener(this);
	}
	firePropertyChange("geometry", oldValue, geometry);

	fireTableDataChanged();
}


public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("GeometrySubVolumeTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("GeometrySubVolumeTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	SubVolume subVolume = getGeometry().getGeometrySpec().getSubVolumes(rowIndex);
	try {
		switch (columnIndex){
			case COLUMN_NAME:{
				String newName = (String)aValue;
				subVolume.setName(newName);
				fireTableRowsUpdated(rowIndex,rowIndex);
				break;
			}
			case COLUMN_VALUE:{
				try {
					if (subVolume instanceof AnalyticSubVolume){
						AnalyticSubVolume analyticSubVolume = (AnalyticSubVolume)subVolume;
						if (aValue instanceof cbit.vcell.parser.ScopedExpression){
							Expression exp = ((cbit.vcell.parser.ScopedExpression)aValue).getExpression();
							analyticSubVolume.setExpression(exp);
						}else if (aValue instanceof String) {
							String newExpressionString = (String)aValue;
							analyticSubVolume.setExpression(new Expression(newExpressionString));
						}
						fireTableRowsUpdated(rowIndex,rowIndex);
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}
				break;
			}
		}
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2005 11:51:43 AM)
 * @return boolean
 * @param obj java.lang.Object
 */
public org.vcell.util.gui.TableCellValidator.EditValidation validate(java.lang.Object obj,int row,int col) {
	//
	//Verify that object(obj) is an appropriate value for a specific class type  in a table cell(row,col)
	//
	try{
		if(col == COLUMN_VALUE && getColumnClass(col).isAssignableFrom(cbit.vcell.parser.ScopedExpression.class)){
			//ScopedExpression column:
			//Value needs to be a String Expression appropriate for AnalyticSubVolume
			if(obj instanceof String){
					new AnalyticSubVolume("test",new Expression((String)obj));//Try to have and error
					return org.vcell.util.gui.TableCellValidator.VALIDATE_OK;
			}
		}else if(col == COLUMN_NAME && getColumnClass(col).isAssignableFrom(SubVolume.class)){
			//Subvolume "name" column
			//Value needs to be String Name appropriate for AnalyticSubVolume
			if(obj instanceof String){
					new AnalyticSubVolume((String)obj);//Try to have and error
					return org.vcell.util.gui.TableCellValidator.VALIDATE_OK;
			}
		}
	}catch(Throwable e){
		return new org.vcell.util.gui.TableCellValidator.EditValidation(e.getClass().getName()+"\n"+(e.getMessage() != null?e.getMessage():"")+".");
	}
	
	//Couldn't verify object
	throw new IllegalArgumentException(
		this.getClass().getName()+" verify not implemented for parameters: target="+obj+" row="+row+" col="+col+".");
}
}