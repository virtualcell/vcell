package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.DataIdentifier;
import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMesh;

import java.beans.*;

import cbit.image.render.SourceDataInfo;
import cbit.util.*;
/**
 * Insert the type's description here.
 * Creation date: (10/3/00 3:21:23 PM)
 * @author: 
 */
public abstract class PDEDataContext implements PropertyChangeListener {
	//
	protected transient PropertyChangeSupport propertyChange = null;
	private SourceDataInfo fieldSourceDataInfo = null;
	private DataIdentifier fieldDataIdentifier = null;
	private double fieldTimePoint = -1;
	private double[] dataValues = null;
	private DataIdentifier[] dataIdentifiers = null;
	private boolean particleData = false;
	private ParticleDataBlock fieldParticleDataBlock = null;
	private CartesianMesh cartesianMesh = null;
	private Range dataRange = null;
	private double[] fieldTimePoints = null;

/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:18:21 PM)
 * @param simManager cbit.vcell.desktop.controls.SimulationManager
 */
public PDEDataContext() {
	addPropertyChangeListener(this);
}


/**
 * adds a named <code>Function</code> to the list of variables that are availlable for this Simulation.
 *
 * @param function named expression that is to be bound to dataset and whose name is added to variable list.
 *
 * @throws cbit.util.DataAccessException if Function cannot be bound to this dataset or SimulationInfo not found.
 */
public abstract void addFunction(cbit.vcell.math.AnnotatedFunction function) throws cbit.util.DataAccessException;


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
 * Comment
 */
protected SourceDataInfo calculateSourceDataInfo() {
	SourceDataInfo sdi = null;
	CartesianMesh mesh = getCartesianMesh();
	//
	if (getDataIdentifier()!=null && getDataIdentifier().getVariableType().equals(VariableType.VOLUME)) {
		//Set data to display
		int yIncr = mesh.getSizeX();
		int zIncr = mesh.getSizeX() * mesh.getSizeY();
		sdi = 
			new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				getDataValues(), 
				mesh.getExtent(), 
				mesh.getOrigin(), 
				getDataRange(), 
				0, 
				mesh.getSizeX(), 
				1, 
				mesh.getSizeY(), 
				yIncr, 
				mesh.getSizeZ(), 
				zIncr); 
	} else if(getDataIdentifier()!=null && getDataIdentifier().getVariableType().equals(VariableType.VOLUME_REGION)) {
		//
		double[] expandedVolumeRegionValues = new double[mesh.getSizeX()*mesh.getSizeY()*mesh.getSizeZ()];
		double[] volumeRegionDataValues = getDataValues();
		for(int i = 0;i<expandedVolumeRegionValues.length;i+= 1){
			expandedVolumeRegionValues[i] = volumeRegionDataValues[mesh.getVolumeRegionIndex(i)];
		}
		//
		int yIncr = mesh.getSizeX();
		int zIncr = mesh.getSizeX() * mesh.getSizeY();
		sdi = 
			new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				expandedVolumeRegionValues, 
				mesh.getExtent(), 
				mesh.getOrigin(), 
				getDataRange(), 
				0, 
				mesh.getSizeX(), 
				1, 
				mesh.getSizeY(), 
				yIncr, 
				mesh.getSizeZ(), 
				zIncr); 

	}else {// Membranes
		//Create placeholder SDI with null data
		sdi = 
			new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				null,
				mesh.getExtent(), 
				mesh.getOrigin(), 
				getDataRange(), 
				0, 
				mesh.getSizeX(), 
				0, 
				mesh.getSizeY(), 
				0, 
				mesh.getSizeZ(), 
				0); 
	}
	return sdi;
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
 * Creation date: (5/22/2001 3:47:36 PM)
 * @return cbit.vcell.solvers.CartesianMesh
 */
public CartesianMesh getCartesianMesh() {
	return cartesianMesh;
}


/**
 * Gets the variableName property (java.lang.String) value.
 * @return The variableName property value.
 * @see #setVariableName
 */
public DataIdentifier getDataIdentifier() {
	return fieldDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:29:26 PM)
 * @return java.lang.String[]
 */
public DataIdentifier[] getDataIdentifiers() {
	return dataIdentifiers;
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:32:00 PM)
 * @return cbit.image.Range
 */
private cbit.util.Range getDataRange() {
	return dataRange;
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:27:11 PM)
 * @return double[]
 */
public double[] getDataValues() {
	return dataValues;
}


/**
 * gets list of named Functions defined for the resultSet for this Simulation.
 *
 * @returns array of functions, or null if no functions.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public abstract cbit.vcell.math.AnnotatedFunction[] getFunctions() throws DataAccessException;


/**
 * tests if resultSet contains ODE data for the specified simulation.
 *
 * @returns <i>true</i> if results are of type ODE, <i>false</i> otherwise.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public abstract boolean getIsODEData() throws cbit.util.DataAccessException;


/**
 * retrieves a line scan (data sampled along a curve in space) for the specified simulation.
 *
 * @param variable name of variable to be sampled
 * @param time simulation time which is to be sampled.
 * @param spatialSelection spatial curve.
 *
 * @returns annotated array of 'concentration vs. distance' in a plot ready format.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see PlotData
 */
public abstract cbit.plot.PlotData getLineScan(String variable, double time, SpatialSelection spatialSelection) throws cbit.util.DataAccessException;

/**
 * Gets the particleDataBlock property (cbit.vcell.simdata.ParticleDataBlock) value.
 * @return The particleDataBlock property value.
 * @see #setParticleDataBlock
 */
public ParticleDataBlock getParticleDataBlock() {
	return fieldParticleDataBlock;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:20:57 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected abstract ParticleDataBlock getParticleDataBlock(double time) throws cbit.util.DataAccessException;


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
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:20:57 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected abstract SimDataBlock getSimDataBlock(String varName, double time) throws cbit.util.DataAccessException;


/**
 * Gets the sourceDataInfo property (cbit.image.SourceDataInfo) value.
 * @return The sourceDataInfo property value.
 * @see #setSourceDataInfo
 */
public SourceDataInfo getSourceDataInfo() {
	return fieldSourceDataInfo;
}


/**
 * Gets the timePoint property (double) value.
 * @return The timePoint property value.
 * @see #setTimePoint
 */
public double getTimePoint() {
	return fieldTimePoint;
}


/**
 * Gets the timePoints property (double[]) value.
 * @return The timePoints property value.
 * @see #setTimePoints
 */
public double[] getTimePoints() {
	return fieldTimePoints;
}


/**
 * retrieves a time series (single point as a function of time) of a specified spatial data set.
 *
 * @param variable name of variable to be sampled
 * @param index identifies index into data array.
 *
 * @returns annotated array of 'concentration vs. time' in a plot ready format.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh for transformation between indices and coordinates.
 */
public abstract TimeSeriesJobResults getTimeSeriesValues(TimeSeriesJobSpec timeSeriesJobSpec) throws cbit.util.DataAccessException;


/**
 * Gets the variableName property (java.lang.String) value.
 * @return The variableName property value.
 * @see #setVariableName
 */
public java.lang.String getVariableName() {
	if (fieldDataIdentifier==null){
		return null;
	}else{
		return fieldDataIdentifier.getName();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:29:26 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getVariableNames() {
	if (dataIdentifiers!=null){
		String varNames[] = new String[dataIdentifiers.length];
		for (int i = 0; i < dataIdentifiers.length; i++){
			varNames[i] = dataIdentifiers[i].getName();
		}
		java.util.Arrays.sort(varNames);
		return varNames;
	}
	return null;
}


/**
 * Gets the simulationInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The simulationInfo property value.
 */
public abstract VCDataIdentifier getVCDataIdentifier();


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:38:21 PM)
 * @return boolean
 */
public boolean hasParticleData() {
	return particleData;
}


/**
 * This method was created in VisualAge.
 *
 * @param exportSpec cbit.vcell.export.server.ExportSpecs
 */
public abstract void makeRemoteFile(cbit.vcell.export.server.ExportSpecs exportSpecs) throws cbit.util.DataAccessException;


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("timePoint")) {
		refreshData();
	}
	if (evt.getSource() == this && evt.getPropertyName().equals("variableName")) {
		refreshData();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
protected void refreshData() {
	double[] newDataValues = null;
	ParticleDataBlock newParticleDataBlock = null;
	if (getVariableNames() != null && getTimePoints() != null && getVariableName() != null) {
		try {
			if (! BeanUtils.arrayContains(getVariableNames(), getVariableName())) {
				throw new DataAccessException("Requested variable not found");
			}
			if (BeanUtils.firstIndexOf(getTimePoints(), getTimePoint()) == -1) {
				if (getTimePoints() == null || getTimePoints().length == 0) {
					throw new DataAccessException("No timepoints available");
				} else {
					setTimePoint(getTimePoints()[0]);
				}
			}
			newDataValues = getSimDataBlock(getVariableName(), getTimePoint()).getData();
			if (hasParticleData()) {
				newParticleDataBlock = getParticleDataBlock(getTimePoint());
			}
		} catch (DataAccessException exc) {
			exc.printStackTrace(System.out);
		}
	}
	setDataValues(newDataValues);
	setParticleDataBlock(newParticleDataBlock);
	if (getDataValues() != null) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < getDataValues().length; i++){
			min = Math.min(min, getDataValues()[i]);
			max = Math.max(max, getDataValues()[i]);
		}
		setDataRange(new Range(min,max));
		setSourceDataInfo(calculateSourceDataInfo());
	} else {
		setDataRange(null);
		setSourceDataInfo(null);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public abstract void refreshIdentifiers();


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public abstract void refreshTimes() throws DataAccessException;


/**
 * removes the specified <i>function</i> from this Simulation.
 *
 * @param function function to be removed.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 * @throws cbit.vcell.server.PermissionException if not the owner of this dataset.
 */
public abstract void removeFunction(cbit.vcell.math.AnnotatedFunction function) throws cbit.util.DataAccessException, cbit.vcell.server.PermissionException;


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
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:47:36 PM)
 * @param newCartesianMesh cbit.vcell.solvers.CartesianMesh
 */
protected void setCartesianMesh(CartesianMesh newCartesianMesh) {
	cartesianMesh = newCartesianMesh;
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:29:26 PM)
 * @param newVariableNames java.lang.String[]
 */
protected void setDataIdentifiers(DataIdentifier[] newDataIdentifiers) {
	DataIdentifier[] oldDataIdentifiers = dataIdentifiers;
	if (getVariableName()==null && dataIdentifiers!=null && dataIdentifiers.length>0){
		setVariableName(dataIdentifiers[0].getName());
	}
	dataIdentifiers = newDataIdentifiers;

	boolean fire = false;
	if (dataIdentifiers != null) {
		if (oldDataIdentifiers == null) {
			fire = true;
		} else if (oldDataIdentifiers.length != dataIdentifiers.length) {
			fire = true;
		} else {
			for (int i = 0; i < dataIdentifiers.length; i++){
				if (!dataIdentifiers[i].getName().equals(oldDataIdentifiers[i].getName())) {
					fire = true;
					break;
				}		
			}
		}
	} else if (oldDataIdentifiers != null) {
		fire = true;
	}

	if (fire) {
		firePropertyChange("dataIdentifiers", oldDataIdentifiers, newDataIdentifiers);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:32:00 PM)
 * @param newDataRange cbit.image.Range
 */
protected void setDataRange(cbit.util.Range newDataRange) {
	dataRange = newDataRange;
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:27:11 PM)
 * @param newDataValues double[]
 */
protected void setDataValues(double[] newDataValues) {
	dataValues = newDataValues;
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:38:21 PM)
 * @param newParticleData boolean
 */
protected void setParticleData(boolean newParticleData) {
	particleData = newParticleData;
}


/**
 * Sets the particleDataBlock property (cbit.vcell.simdata.ParticleDataBlock) value.
 * @param particleDataBlock The new value for the property.
 * @see #getParticleDataBlock
 */
protected void setParticleDataBlock(ParticleDataBlock particleDataBlock) {
	ParticleDataBlock oldValue = fieldParticleDataBlock;
	fieldParticleDataBlock = particleDataBlock;
	firePropertyChange("particleDataBlock", oldValue, particleDataBlock);
}


/**
 * Sets the sourceDataInfo property (cbit.image.SourceDataInfo) value.
 * @param sourceDataInfo The new value for the property.
 * @see #getSourceDataInfo
 */
protected void setSourceDataInfo(SourceDataInfo sourceDataInfo) {
	SourceDataInfo oldValue = fieldSourceDataInfo;
	fieldSourceDataInfo = sourceDataInfo;
	firePropertyChange("sourceDataInfo", oldValue, sourceDataInfo);
}


/**
 * Sets the timePoint property (double) value.
 * @param timePoint The new value for the property.
 * @see #getTimePoint
 */
public void setTimePoint(double timePoint) {
	//
	if (BeanUtils.firstIndexOf(getTimePoints(), timePoint) == -1) {
		throw new IllegalArgumentException("Time point="+timePoint+" does not exist");
	}
	//
	double oldValue = fieldTimePoint;
	fieldTimePoint = timePoint;
	firePropertyChange("timePoint", new Double(oldValue), new Double(timePoint));
}


/**
 * Sets the timePoints property (double[]) value.
 * @param timePoints The new value for the property.
 * @see #getTimePoints
 */
protected void setTimePoints(double[] timePoints) {
	double[] oldValue = fieldTimePoints;
	fieldTimePoints = timePoints;
	firePropertyChange("timePoints", oldValue, timePoints);
}


/**
 * Sets the variableName property (java.lang.String) value.
 * @param variableName The new value for the property.
 * @see #getVariableName
 */
public void setVariableName(java.lang.String variableName) {
	//
	if (!BeanUtils.arrayContains(getVariableNames(),variableName)) {
		throw new IllegalArgumentException("Variable Name="+variableName+" does not exist");
	}
	//
	String oldName = getVariableName();
	//
	// select new DataIdentifier
	//
	fieldDataIdentifier = null;
	for (int i = 0; i < dataIdentifiers.length; i++){
		if (dataIdentifiers[i].getName().equals(variableName)){
			fieldDataIdentifier = dataIdentifiers[i];
		}
	}
	
	String newName = null;
	if (fieldDataIdentifier!=null){
		newName = fieldDataIdentifier.getName();
	}
	firePropertyChange("variableName", oldName, newName);
}
}