package cbit.vcell.geometry.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.TokenMangler;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.parser.ASTFuncNode;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ScopedExpression;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class GeometrySubVolumeTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 2;
	private final int COLUMN_NAME = 0;
	private final int COLUMN_VALUE = 1;
	private String LABELS[] = { "Name", "Value" };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter = null;

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
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
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
			return SubVolume.class;
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
		throw new RuntimeException("GeometrySubVolumeTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	return LABELS[column];
}


/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public Geometry getGeometry() {
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
				return new ScopedExpression(((AnalyticSubVolume)subVolume).getExpression(), ReservedSymbol.X.getNameScope(), true, 
						autoCompleteSymbolFilter);
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
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(Geometry geometry) {
	Geometry oldValue = fieldGeometry;
	if (oldValue != null){
		oldValue.getGeometrySpec().removePropertyChangeListener(this);
	}
	fieldGeometry = geometry;
	if (fieldGeometry != null){
		fieldGeometry.getGeometrySpec().addPropertyChangeListener(this);
		autoCompleteSymbolFilter = new AutoCompleteSymbolFilter() {
			public boolean accept(SymbolTableEntry ste) {
				int dimension = fieldGeometry.getDimension();
				if (ste.equals(ReservedSymbol.X) || dimension > 1 && ste.equals(ReservedSymbol.Y) || dimension > 2 && ste.equals(ReservedSymbol.Z)) {
					return true;
				}
				return false;
			}
			public boolean acceptFunction(String funcName) {
				if (funcName.equals(ASTFuncNode.getFunctionNames()[ASTFuncNode.FIELD]) || funcName.equals(ASTFuncNode.getFunctionNames()[ASTFuncNode.GRAD])) {
					return false;
				}
				return true;
			}	   
		};
	}
	firePropertyChange("geometry", oldValue, fieldGeometry);

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
						if (aValue instanceof ScopedExpression){
							Expression exp = ((ScopedExpression)aValue).getExpression();
							analyticSubVolume.setExpression(exp);
						}else if (aValue instanceof String) {
							String newExpressionString = (String)aValue;
							analyticSubVolume.setExpression(new Expression(newExpressionString));
						}
						fireTableRowsUpdated(rowIndex,rowIndex);
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}
				break;
			}
		}
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
	}
}

}