package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.PropertyVetoException;

import org.vcell.sbml.vcell.StructureSizeSolver;

import cbit.vcell.parser.*;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Membrane;
import cbit.vcell.mapping.*;
import cbit.vcell.math.BoundaryConditionType;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
public class StructureMappingTableModel extends javax.swing.table.AbstractTableModel implements java.beans.PropertyChangeListener {
	private final int NUM_COLUMNS = 13;
	public final static int COLUMN_STRUCTURE = 0;
	public final static int COLUMN_SUBDOMAIN = 1;
	public final static int COLUMN_RESOLVED = 2;
	public final static int COLUMN_SURFVOL = 3;
	public final static int COLUMN_VOLFRACT = 4;
	public final static int COLUMN_VOLUME = 5;
	public final static int COLUMN_SURFACE = 6;
	public final static int COLUMN_X_MINUS = 7;
	public final static int COLUMN_X_PLUS = 8;
	public final static int COLUMN_Y_MINUS = 9;
	public final static int COLUMN_Y_PLUS = 10;
	public final static int COLUMN_Z_MINUS = 11;
	public final static int COLUMN_Z_PLUS = 12;
	
	public final String LABEL_STRUCTURE = "Structure";
	public final String LABEL_SUBDOMAIN = "Subdomain";
	public final String LABEL_RESOLVED = "Resolved";
	public final String LABEL_SURFVOL = "Surf/Vol";
	public final String LABEL_VOLFRACT = "VolFract";
	public final String LABEL_VOLUME = "Volume(um3)";
	public final String LABEL_SURFACE = "Surface(um2)";
	public final String LABEL_X_MINUS = "X-";
	public final String LABEL_X_PLUS = "X+";
	public final String LABEL_Y_MINUS = "Y-";
	public final String LABEL_Y_PLUS = "Y+";
	public final String LABEL_Z_MINUS = "Z-";
	public final String LABEL_Z_PLUS = "Z+";
	
	public final static String[] COLUMN_TOOLTIPS = {
			null, // 0
			null, // 1
			null, // 2
			"ratio of membrane area to total enclosed volume", // 3
			"ratio of total enclosed volume to parent's total enclosed volume", // 4
			"<html>The volume of a compartment does <b>NOT</b> include the volumes of <br> any other compartments residing within that compartment.</html>", // 5
			"membrane surface area", // 6
			"<html>boundary condition type for X- boundary <ul><li>flux: specify flux at boundary</li><li>value: specify concentration at boundary</li></ul><html>", // 7
			"<html>boundary condition type for X+ boundary <ul><li>flux: specify flux at boundary</li><li>value: specify concentration at boundary</li></ul><html>", // 8
			"<html>boundary condition type for Y- boundary <ul><li>flux: specify flux at boundary</li><li>value: specify concentration at boundary</li></ul><html>", // 9
			"<html>boundary condition type for Y+ boundary <ul><li>flux: specify flux at boundary</li><li>value: specify concentration at boundary</li></ul><html>", // 10
			"<html>boundary condition type for Z- boundary <ul><li>flux: specify flux at boundary</li><li>value: specify concentration at boundary</li></ul><html>", // 11
			"<html>boundary condition type for Z+ boundary <ul><li>flux: specify flux at boundary</li><li>value: specify concentration at boundary</li></ul><html>" // 12
	};
		
	private final String LABELS[] = { LABEL_STRUCTURE, LABEL_SUBDOMAIN, LABEL_RESOLVED, LABEL_SURFVOL, LABEL_VOLFRACT, LABEL_VOLUME, LABEL_SURFACE, LABEL_X_MINUS, LABEL_X_PLUS, LABEL_Y_MINUS, LABEL_Y_PLUS, LABEL_Z_MINUS, LABEL_Z_PLUS };
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private GeometryContext fieldGeometryContext = null;

/**
 * StructureMappingTableModel constructor comment.
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
public Class<?> getColumnClass(int column) {
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
			return Double.class;
		}
		case COLUMN_VOLFRACT:{
			return Double.class;
		}
		case COLUMN_VOLUME:{
			return Double.class;
		}
		case COLUMN_SURFACE:{
			return Double.class;
		}
		case COLUMN_X_MINUS:{
			return String.class;
		}
		case COLUMN_X_PLUS:{
			return String.class;
		}
		case COLUMN_Y_MINUS:{
			return String.class;
		}
		case COLUMN_Y_PLUS:{
			return String.class;
		}
		case COLUMN_Z_MINUS:{
			return String.class;
		}
		case COLUMN_Z_PLUS:{
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
	if (column<0 || column>=NUM_COLUMNS){
		throw new RuntimeException("StructureMappingTableModel.getColumnName(), column = "+column+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
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
public GeometryContext getGeometryContext() {
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
				if(membraneMapping.getSurfaceToVolumeParameter().getExpression() != null)
				{
					double val=0;
					try{
						val = membraneMapping.getSurfaceToVolumeParameter().getExpression().evaluateConstant();
					}catch(ExpressionException e){
						e.printStackTrace(System.out);
						//DialogUtils.showErrorDialog("Surface to volume ratio of "+featureMapping.getFeature().getName()+" cannot be evaluated as a constant.");
					}
					return val;
				}
				else return null;
			}else{
				return null;
			}
		}
		case COLUMN_VOLFRACT:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null){
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				if(membraneMapping.getVolumeFractionParameter().getExpression() != null)
				{
					double val=0;
					try{
						val = membraneMapping.getVolumeFractionParameter().getExpression().evaluateConstant();
					}catch(ExpressionException e){
						e.printStackTrace(System.out);
						//DialogUtils.showErrorDialog("Volume fraction of "+featureMapping.getFeature().getName()+" cannot be evaluated as a constant.");
					}
					return val;
				}
				else return null;
			}else{
				return null;
			}
		}
		case COLUMN_VOLUME:{
			if (featureMapping.getSizeParameter().getExpression() != null)
			{
				double val=0;
				try{
					val = featureMapping.getSizeParameter().getExpression().evaluateConstant();
				}catch(ExpressionException e){
					e.printStackTrace(System.out);
					//DialogUtils.showErrorDialog("Volume of "+featureMapping.getFeature().getName()+" cannot be evaluated as a constant.");
				}
				return val;
			}else{
				return null;
			}
		}
		case COLUMN_SURFACE:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null){
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				if(membraneMapping.getSizeParameter().getExpression() != null)
				{
					double val=0;
					try{
						val = membraneMapping.getSizeParameter().getExpression().evaluateConstant();
					}catch(ExpressionException e){
						e.printStackTrace(System.out);
						//DialogUtils.showErrorDialog("Surface area of "+membraneMapping.getMembrane().getName()+" cannot be evaluated as a constant.");
					}
					return val;
				}
				else return null;
			}else{
				return null;
			}
		}
		case COLUMN_X_MINUS:{
			if(featureMapping.getBoundaryConditionTypeXm() != null){
				return featureMapping.getBoundaryConditionTypeXm();
			}else{
				return null;
			}
		}
		case COLUMN_X_PLUS:{
			if(featureMapping.getBoundaryConditionTypeXp() != null){
				return featureMapping.getBoundaryConditionTypeXp();
			}else{
				return null;
			}
		}
		case COLUMN_Y_MINUS:{
			if(featureMapping.getBoundaryConditionTypeYm() != null){
				return featureMapping.getBoundaryConditionTypeYm();
			}else{
				return null;
			}
		}
		case COLUMN_Y_PLUS:{
			if(featureMapping.getBoundaryConditionTypeYp() != null){
				return featureMapping.getBoundaryConditionTypeYp();
			}else{
				return null;
			}
		}
		case COLUMN_Z_MINUS:{
			if(featureMapping.getBoundaryConditionTypeZm() != null){
				return featureMapping.getBoundaryConditionTypeZm();
			}else{
				return null;
			}
		}
		case COLUMN_Z_PLUS:{
			if(featureMapping.getBoundaryConditionTypeZp() != null){
				return featureMapping.getBoundaryConditionTypeZp();
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
	if (columnIndex == COLUMN_SUBDOMAIN) {
		return true;
	}
	if (columnIndex == COLUMN_VOLUME) // feature size are editable  
		return true;
	if ((columnIndex == COLUMN_SURFACE) && (fm.getFeature().getMembrane() != null)) //membrane size are editable
		return true;
	// the VolFrac and Surf/Vol are editable for non-compartmental models
	if ((getGeometryContext().getGeometry().getDimension() > 0) && (!fm.getResolved()))
	{
		if((fm.getFeature().getMembrane() != null) && ((columnIndex == COLUMN_VOLFRACT)||(columnIndex == COLUMN_SURFVOL))) return true;
	}
	// bounday conditions are editable
	if ((columnIndex >= COLUMN_X_MINUS) && (columnIndex <= COLUMN_Z_PLUS))
		return true;

	return false;
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
public void setGeometryContext(GeometryContext geometryContext) {
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


public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	if (rowIndex<0 || rowIndex>=getRowCount()){
		throw new RuntimeException("StructureMappingTableModel.setValueAt(), row = "+rowIndex+" out of range ["+0+","+(getRowCount()-1)+"]");
	}
	if (columnIndex<0 || columnIndex>=NUM_COLUMNS){
		throw new RuntimeException("StructureMappingTableModel.setValueAt(), column = "+columnIndex+" out of range ["+0+","+(NUM_COLUMNS-1)+"]");
	}
	FeatureMapping featureMapping = getFeatureMapping(rowIndex);
	switch (columnIndex){
		case COLUMN_SUBDOMAIN: {
			String svname = (String)aValue;			
			try {
				getGeometryContext().assignFeature(featureMapping.getFeature(), getGeometryContext().getGeometry().getGeometrySpec().getSubVolume(svname));
			} catch (IllegalMappingException e) {
				PopupGenerator.showErrorDialog(e.getMessage());
			} catch (PropertyVetoException e) {				
				PopupGenerator.showErrorDialog(e.getMessage());
			}
			break;
		}
		case COLUMN_SURFVOL:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null){
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				try {
					Expression exp = null;
					if (aValue instanceof String){
						exp = new Expression((String)aValue);
					}else if (aValue instanceof Double){
						exp = new Expression(((Double)aValue).doubleValue());
					}
					membraneMapping.getSurfaceToVolumeParameter().setExpression(exp);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}
			}
			break;
		}
		case COLUMN_VOLFRACT:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null){
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				try {
					Expression exp = null;
					if (aValue instanceof String){
						exp = new Expression((String)aValue);
					}else if (aValue instanceof Double){
						exp = new Expression(((Double)aValue).doubleValue());
					}
					membraneMapping.getVolumeFractionParameter().setExpression(exp);
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}
			}
			break;
		}
		case COLUMN_VOLUME:{
			try {
				Expression exp = null;
				if (aValue instanceof String){
					exp = new Expression((String)aValue);
				}else if (aValue instanceof Double){
					exp = new Expression(((Double)aValue).doubleValue());
				}
				//if the input volumn is null, leave it as it was.
				if(exp != null)
				{
					//for old ode model, once one size is input, solve the rest.                                                                                                          if it is unnamed compartment(the only one), we don't need to solve anything
					if(!getGeometryContext().getSimulationContext().isStoch() && getGeometryContext().isAllSizeSpecifiedNull() && getGeometryContext().isAllVolFracAndSurfVolSpecified() && getGeometryContext().getStructureMappings().length > 1) 
					{
						featureMapping.getSizeParameter().setExpression(exp);
						StructureSizeSolver sizeSolver = new StructureSizeSolver();
						double size;
						try{
							size = exp.evaluateConstant();
							sizeSolver.updateAbsoluteStructureSizes(getGeometryContext().getSimulationContext(), featureMapping.getFeature(), size, VCUnitDefinition.UNIT_um3);
							fireTableRowsUpdated(0,getRowCount());
						}catch(ExpressionException ex){
							ex.printStackTrace(System.out);
							PopupGenerator.showErrorDialog("Size of Feature " + featureMapping.getFeature().getName() + " can not be solved as constant!");
						}
					}
					else 
					{
						featureMapping.getSizeParameter().setExpression(exp);
						//solve relative structure sizes(surface volume ratio, volume fraction) for non-stochastic applications
						//amended Sept. 27th, 2007
						//set fraction in stoch math description, because these might be used when copy from stoch app to ode app.
						if(getGeometryContext().isAllSizeSpecifiedPositive()/*&& !getGeometryContext().getSimulationContext().isStoch()*/) 
						{
							StructureSizeSolver sizeSolver = new StructureSizeSolver();
							sizeSolver.updateRelativeStructureSizes(getGeometryContext().getSimulationContext());
						}
						fireTableRowsUpdated(0, getRowCount());
					}
				}
				fireTableRowsUpdated(rowIndex,rowIndex);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
			}
			break;
		}
		case COLUMN_SURFACE:{
			if (featureMapping.getResolved() == false && featureMapping.getFeature()!=null && featureMapping.getFeature().getMembrane()!=null)
			{
				Membrane membrane = featureMapping.getFeature().getMembrane();
				MembraneMapping membraneMapping = (MembraneMapping)getGeometryContext().getStructureMapping(membrane);
				try {
					Expression exp = null;
					if (aValue instanceof String){
						exp = new Expression((String)aValue);
					}else if (aValue instanceof Double){
						exp = new Expression(((Double)aValue).doubleValue());
					}
					//if the input volumn is null, leave it as it was.
					if(exp != null)
					{
						//for old model, once one size is input, solve the rest.
						if(getGeometryContext().isAllSizeSpecifiedNull() && getGeometryContext().isAllVolFracAndSurfVolSpecified()) 
						{
							membraneMapping.getSizeParameter().setExpression(exp);
							StructureSizeSolver sizeSolver = new StructureSizeSolver();
							double size;
							try{
								size = exp.evaluateConstant();
								sizeSolver.updateAbsoluteStructureSizes(getGeometryContext().getSimulationContext(), membraneMapping.getMembrane(), size, VCUnitDefinition.UNIT_um2);
								fireTableRowsUpdated(0,getRowCount());
							}catch(ExpressionException ex){
								ex.printStackTrace(System.out);
								PopupGenerator.showErrorDialog("Size of Membrane " + membraneMapping.getMembrane().getName() + " can not be solved as constant!");
							}
						}
						else
						{
							membraneMapping.getSizeParameter().setExpression(exp);
							//solve relative structure sizes(surface volume ratio, volume fraction) for non-stochastic applications
							//amended Sept. 27th, 2007
							//set ratio in stoch math description, because these might be used when copy from stoch app to ode app.
							if(getGeometryContext().isAllSizeSpecifiedPositive() /*&& !getGeometryContext().getSimulationContext().isStoch()*/)
							{
								StructureSizeSolver sizeSolver = new StructureSizeSolver();
								sizeSolver.updateRelativeStructureSizes(getGeometryContext().getSimulationContext());
							}
							fireTableRowsUpdated(0,getRowCount());
						}
					}
					fireTableRowsUpdated(rowIndex,rowIndex);
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					PopupGenerator.showErrorDialog("expression error\n"+e.getMessage());
				}
			}
			break;
		}
		case COLUMN_X_MINUS:{
			if(aValue != null)
			{
				featureMapping.setBoundaryConditionTypeXm(new BoundaryConditionType((String)aValue));
			}
			break;
		}
		case COLUMN_X_PLUS:{
			if(aValue != null)
			{
				featureMapping.setBoundaryConditionTypeXp(new BoundaryConditionType((String)aValue));
			}
			break;
		}
		case COLUMN_Y_MINUS:{
			if(aValue != null)
			{
				featureMapping.setBoundaryConditionTypeYm(new BoundaryConditionType((String)aValue));
			}
			break;
		}
		case COLUMN_Y_PLUS:{
			if(aValue != null)
			{
				featureMapping.setBoundaryConditionTypeYp(new BoundaryConditionType((String)aValue));
			}
			break;
		}
		case COLUMN_Z_MINUS:{
			if(aValue != null)
			{
				featureMapping.setBoundaryConditionTypeZm(new BoundaryConditionType((String)aValue));
			}
			break;
		}
		case COLUMN_Z_PLUS:{
			if(aValue != null)
			{
				featureMapping.setBoundaryConditionTypeZp(new BoundaryConditionType((String)aValue));
			}
			break;
		}
	}
}
}