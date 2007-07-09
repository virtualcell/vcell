package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.expression.ui.ScopedExpression;
import org.vcell.util.BeanUtils;

import cbit.vcell.model.Membrane;
import cbit.vcell.modelapp.FeatureMapping;
import cbit.vcell.modelapp.GeometryContext;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.StructureMapping;
import cbit.vcell.mapping.*;
import edu.uchc.vcell.expression.internal.*;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class StructureMappingTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 5;
	public final int COLUMN_STRUCTURE = 0;
	public final int COLUMN_SUBDOMAIN = 1;
	public final int COLUMN_RESOLVED = 2;
	public final int COLUMN_SURFVOL = 3;
	public final int COLUMN_VOLFRACT = 4;
	public final String LABEL_STRUCTURE = "Structure";
	public final String LABEL_SUBDOMAIN = "Subdomain";
	public final String LABEL_RESOLVED = "Resolved";
	public final String LABEL_SURFVOL = "Surf/Vol";
	public final String LABEL_VOLFRACT = "VolFract";
	private String LABELS[] = { LABEL_STRUCTURE, LABEL_SUBDOMAIN, LABEL_RESOLVED, LABEL_SURFVOL, LABEL_VOLFRACT };
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.modelapp.GeometryContext fieldGeometryContext = null;
/**
 * ReactionSpecsTableModel constructor comment.
 */
public StructureMappingTableModel() {
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
		case COLUMN_STRUCTURE:{
			return String.class;
		}
		case COLUMN_SUBDOMAIN:{
			return String.class;
		}
		case COLUMN_RESOLVED:{
			return Boolean.class;
		}
		case COLUMN_SURFVOL:{
			return org.vcell.expression.ui.ScopedExpression.class;
		}
		case COLUMN_VOLFRACT:{
			return org.vcell.expression.ui.ScopedExpression.class;
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
 * Insert the method's description here.
 * Creation date: (4/3/01 10:02:00 AM)
 * @return cbit.vcell.mapping.FeatureMapping
 * @param row int
 */
public FeatureMapping getFeatureMapping(int row) {
	if (getGeometryContext()==null){
		return null;
	}
	int featureMappingIndex = 0;
	StructureMapping structureMappings[] = getGeometryContext().getStructureMappings();
	for (int i=0;i<structureMappings.length;i++){
		if (structureMappings[i] instanceof FeatureMapping){
			if (featureMappingIndex==row){
				return (FeatureMapping)structureMappings[i];
			}
			featureMappingIndex++;
		}
	}
	return null;
}
/**
 * Gets the geometryContext property (cbit.vcell.mapping.GeometryContext) value.
 * @return The geometryContext property value.
 * @see #setGeometryContext
 */
public cbit.vcell.modelapp.GeometryContext getGeometryContext() {
	return fieldGeometryContext;
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
			if (structureMappings[i] instanceof FeatureMapping){
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
		throw new RuntimeException("StructureMappingTableModel.getValueAt(), row = "+row+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (col<0 || col>=NUM_COLUMNS){
		throw new RuntimeException("StructureMappingTableModel.getValueAt(), column = "+col+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}

	FeatureMapping featureMapping = getFeatureMapping(row);
	if (featureMapping == null){
		return null;
	}
	switch (col){
		case COLUMN_STRUCTURE:{
			if (featureMapping.getStructure()!=null){
				return featureMapping.getStructure().getName();
			}else{
				return null;
			}
		}
		case COLUMN_SUBDOMAIN:{
			if (featureMapping.getSubVolume()!=null){
				return featureMapping.getSubVolume().getName();
			}else{
				return null;
			}
		}
		case COLUMN_RESOLVED:{
			return new Boolean(featureMapping.getResolved());
		}
		case COLUMN_SURFVOL:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null){
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				return new ScopedExpression(membraneMapping.getSurfaceToVolumeParameter().getExpression(),membraneMapping.getNameScope(),true);
			}else{
				return null;
			}
		}
		case COLUMN_VOLFRACT:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null){
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				return new ScopedExpression(membraneMapping.getVolumeFractionParameter().getExpression(),membraneMapping.getNameScope(),true);
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
	FeatureMapping fm = getFeatureMapping(rowIndex);
	//
	// see if feature is distributed and has a membrane (not top)
	//
	if (columnIndex == COLUMN_SURFVOL || columnIndex == COLUMN_VOLFRACT){
		if (fm.getResolved()==false && fm.getFeature().getMembrane()!=null){
			return true;
		}else{
			return false;
		}
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
	if (evt.getSource() instanceof GeometryContext
		&& evt.getPropertyName().equals("structureMappings")) {
		StructureMapping[] oldStructureMappings = (StructureMapping[])evt.getOldValue();
		StructureMapping[] newStructureMappings = (StructureMapping[])evt.getNewValue();
		for (int i=0;oldStructureMappings!=null && i<oldStructureMappings.length;i++){
			oldStructureMappings[i].removePropertyChangeListener(this);
		}
		for (int i=0;newStructureMappings!=null && i<newStructureMappings.length;i++){
			newStructureMappings[i].addPropertyChangeListener(this);
		}
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
public void setGeometryContext(cbit.vcell.modelapp.GeometryContext geometryContext) {
	GeometryContext oldValue = fieldGeometryContext;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
		StructureMapping oldStructureMappings[] = oldValue.getStructureMappings();
		for (int i=0;i<oldStructureMappings.length;i++){
			oldStructureMappings[i].removePropertyChangeListener(this);
		}
	}
	fieldGeometryContext = geometryContext;
	if (geometryContext!=null){
		geometryContext.addPropertyChangeListener(this);
		StructureMapping newStructureMappings[] = geometryContext.getStructureMappings();
		for (int i=0;i<newStructureMappings.length;i++){
			newStructureMappings[i].addPropertyChangeListener(this);
		}
	}
	firePropertyChange("geometryContext", oldValue, geometryContext);
	fireTableDataChanged();
}
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("ReactionSpecsTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("ReactionSpecsTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	FeatureMapping featureMapping = getFeatureMapping(rowIndex);
	switch (columnIndex){
		case COLUMN_SURFVOL:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null){
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				org.vcell.expression.SymbolTable symbolTable = new cbit.vcell.model.ReservedSymbolTable(false);
				try {
					IExpression exp = null;
					if (aValue instanceof String){
						String newExpressionString = (String)aValue;
						exp = ExpressionFactory.createExpression(newExpressionString);
					}else if (aValue instanceof ScopedExpression){
						exp = ((ScopedExpression)aValue).getExpression();
					}
					membraneMapping.getSurfaceToVolumeParameter().setExpression(exp);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}
			}
			break;
		}
		case COLUMN_VOLFRACT:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null){
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				org.vcell.expression.SymbolTable symbolTable = new cbit.vcell.model.ReservedSymbolTable(false);
				try {
					IExpression exp = null;
					if (aValue instanceof String){
						String newExpressionString = (String)aValue;
						exp = ExpressionFactory.createExpression(newExpressionString);
					}else if (aValue instanceof ScopedExpression){
						exp = ((ScopedExpression)aValue).getExpression();
					}
					membraneMapping.getVolumeFractionParameter().setExpression(exp);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					cbit.vcell.client.PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}
			}
			break;
		}
	}
}
}
