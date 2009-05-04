package cbit.vcell.simdata;
import cbit.vcell.simdata.gui.SpatialSelection;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.solvers.*;
import java.beans.*;
import java.util.Comparator;
import cbit.gui.PropertyChangeListenerProxyVCell;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.Range;
import org.vcell.util.TimeSeriesJobResults;
import org.vcell.util.TimeSeriesJobSpec;
import cbit.image.*;
import cbit.util.*;
/**
 * Insert the type's description here.
 * Creation date: (10/3/00 3:21:23 PM)
 * @author: 
 */
public abstract class PDEDataContext implements PropertyChangeListener {
		
	private class RefreshedData {
		public double[] newData;
		public VariableType newVarType;
		public double newTimePoint;
		public ParticleDataBlock newParticleDataBlock;
		public Range newRange;
		public SourceDataInfo newSourceDataInfo;
		
		public RefreshedData(double[] argNewData,
				VariableType argNewVarType,
				double argNewTimePoint,
				ParticleDataBlock argNewParticleDataBlock){
			newData = argNewData;
			newVarType = argNewVarType;
			newTimePoint = argNewTimePoint;
			newParticleDataBlock = argNewParticleDataBlock;
			calculateRange();
			newSourceDataInfo = calculateSourceDataInfo(newData, newVarType,newRange);
		}
		private void calculateRange(){
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for(int i = 0; i < newData.length; i++){
				if(!Double.isNaN(newData[i])){
					min = Math.min(min, newData[i]);
					max = Math.max(max, newData[i]);
				}
			}
			newRange = new Range(min,max);
		}
	}
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
	
	public static final String PROP_CHANGE_FUNC_ADDED = "functionAdded";
	public static final String PROP_CHANGE_FUNC_REMOVED = "functionRemoved";
	
	private Comparator<String > varNameSortComparator =
		new Comparator<String>(){
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		};

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
 * @throws org.vcell.util.DataAccessException if Function cannot be bound to this dataset or SimulationInfo not found.
 */
public abstract void addFunctions(cbit.vcell.math.AnnotatedFunction[] functionArr,boolean[] bReplaceArr) throws org.vcell.util.DataAccessException;


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(new PropertyChangeListenerProxyVCell(listener));
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
private SourceDataInfo calculateSourceDataInfo(double[] sdiData,VariableType sdiVarType,Range newRange) {
	SourceDataInfo sdi = null;
	CartesianMesh mesh = getCartesianMesh();
	//
	if (sdiVarType.equals(VariableType.VOLUME)) {
		//Set data to display
		int yIncr = mesh.getSizeX();
		int zIncr = mesh.getSizeX() * mesh.getSizeY();
		sdi = 
			new SourceDataInfo(
				SourceDataInfo.RAW_VALUE_TYPE, 
				sdiData, 
				mesh.getExtent(), 
				mesh.getOrigin(), 
				newRange, 
				0, 
				mesh.getSizeX(), 
				1, 
				mesh.getSizeY(), 
				yIncr, 
				mesh.getSizeZ(), 
				zIncr); 
	} else if(sdiVarType.equals(VariableType.VOLUME_REGION)) {
		//
		double[] expandedVolumeRegionValues = new double[mesh.getSizeX()*mesh.getSizeY()*mesh.getSizeZ()];
		double[] volumeRegionDataValues = sdiData;
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
				newRange, 
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
				newRange, 
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

public void externalRefresh() throws DataAccessException {
	refreshData(getVariableName(), getTimePoint(),true);
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
private org.vcell.util.Range getDataRange() {
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
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public abstract cbit.vcell.math.AnnotatedFunction[] getFunctions() throws DataAccessException;


/**
 * tests if resultSet contains ODE data for the specified simulation.
 *
 * @returns <i>true</i> if results are of type ODE, <i>false</i> otherwise.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public abstract boolean getIsODEData() throws org.vcell.util.DataAccessException;


/**
 * retrieves a line scan (data sampled along a curve in space) for the specified simulation.
 *
 * @param variable name of variable to be sampled
 * @param time simulation time which is to be sampled.
 * @param spatialSelection spatial curve.
 *
 * @returns annotated array of 'concentration vs. distance' in a plot ready format.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see PlotData
 */
public abstract cbit.plot.PlotData getLineScan(String variable, double time, SpatialSelection spatialSelection) throws org.vcell.util.DataAccessException;

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
protected abstract ParticleDataBlock getParticleDataBlock(double time) throws org.vcell.util.DataAccessException;


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
protected abstract SimDataBlock getSimDataBlock(String varName, double time) throws org.vcell.util.DataAccessException;


/**
 * Gets the sourceDataInfo property (cbit.image.SourceDataInfo) value.
 * @return The sourceDataInfo property value.
 * @see #setSourceDataInfo
 */
public cbit.image.SourceDataInfo getSourceDataInfo() {
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
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh for transformation between indices and coordinates.
 */
public abstract TimeSeriesJobResults getTimeSeriesValues(TimeSeriesJobSpec timeSeriesJobSpec) throws org.vcell.util.DataAccessException;


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
		java.util.Arrays.sort(varNames,varNameSortComparator);
		return varNames;
	}
	return null;
}


/**
 * Gets the simulationInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The simulationInfo property value.
 */
public abstract org.vcell.util.VCDataIdentifier getVCDataIdentifier();


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
public abstract void makeRemoteFile(cbit.vcell.export.server.ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException;


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
//	if (evt.getSource() == this && evt.getPropertyName().equals("timePoint")) {
//		refreshData();
//	}
//	if (evt.getSource() == this && evt.getPropertyName().equals("variableName")) {
//		refreshData();
//	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
private void refreshData(String varName,double timePoint,boolean bForce) throws DataAccessException{
	
	if(!bForce){
		if(getVariableName() != null && getVariableName().equals(varName) &&
				getTimePoint() == timePoint){
			return;
		}
	}
	if(varName == null){
		varName = dataIdentifiers[0].getName();
	}
	if(timePoint == -1){
		timePoint = fieldTimePoints[0];
	}
	
	ParticleDataBlock newParticleDataBlock = null;
	if (! BeanUtils.arrayContains(getVariableNames(), varName)) {
		throw new DataAccessException("Requested variable not found");
	}
	if (BeanUtils.firstIndexOf(getTimePoints(), timePoint) == -1) {
		if (getTimePoints() == null || getTimePoints().length == 0) {
			throw new DataAccessException("No timepoints available");
		} else {
			throw new DataAccessException("Requested time not found");
		}
	}
	SimDataBlock simdataBlock = getSimDataBlock(varName,timePoint);
	if (hasParticleData()) {
		newParticleDataBlock = getParticleDataBlock(timePoint);
	}

	RefreshedData refreshedData = new RefreshedData(
				simdataBlock.getData(),
				simdataBlock.getVariableType(),
				timePoint,
				newParticleDataBlock
				);
	
	boolean bVarNameChanged = false;
	String oldVarname = getVariableName();
	boolean bTimePointChanged = false;
	double oldTimePoint = getTimePoint();
	
	if(!varName.equals(getVariableName())){
		DataIdentifier foundDataIdentifier = null;
		for (int i = 0; i < dataIdentifiers.length; i++){
			if (dataIdentifiers[i].getName().equals(varName)){
				foundDataIdentifier = dataIdentifiers[i];
				break;
			}
		}
		if(foundDataIdentifier == null){
			throw new DataAccessException("Couldn't find DataIdentifier for variable name "+varName);
		}
		fieldDataIdentifier = foundDataIdentifier;
		bVarNameChanged = true;
	}

	if(timePoint != getTimePoint()){
		fieldTimePoint = timePoint;
		bTimePointChanged = true;
	}
	
	setDataValues(refreshedData.newData);
	setParticleDataBlock(refreshedData.newParticleDataBlock);
	setDataRange(refreshedData.newRange);
	setSourceDataInfo(refreshedData.newSourceDataInfo);
	
	if(bVarNameChanged){
		firePropertyChange("variableName", oldVarname, getVariableName());
	}
	if(bTimePointChanged){
		firePropertyChange("timePoint", new Double(oldTimePoint), new Double(getTimePoint()));
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
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * @throws org.vcell.util.PermissionException if not the owner of this dataset.
 */
public abstract void removeFunction(cbit.vcell.math.AnnotatedFunction function) throws org.vcell.util.DataAccessException, org.vcell.util.PermissionException;


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
protected void setDataIdentifiers(DataIdentifier[] newDataIdentifiers) throws DataAccessException{
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
protected void setDataRange(org.vcell.util.Range newDataRange) {
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
protected void setSourceDataInfo(cbit.image.SourceDataInfo sourceDataInfo) {
	cbit.image.SourceDataInfo oldValue = fieldSourceDataInfo;
	fieldSourceDataInfo = sourceDataInfo;
	firePropertyChange("sourceDataInfo", oldValue, sourceDataInfo);
}

public void setVariableAndTime(String variable, double timePoint) throws DataAccessException {
	refreshData(variable, timePoint, false);
}

public void setTimePoint(double timePoint) throws DataAccessException {
	setVariableAndTime(getVariableName(), timePoint);
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

public void setVariableName(String variable) throws DataAccessException {
	setVariableAndTime(variable, getTimePoint());
}

}