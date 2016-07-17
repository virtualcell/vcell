/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyChangeListenerProxyVCell;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataIdentifier;

import cbit.plot.PlotData;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.math.Function;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (10/3/00 3:21:23 PM)
 * @author: 
 */
public abstract class PDEDataContext implements PropertyChangeListener {
		
public static final String PROPERTY_NAME_VCDATA_IDENTIFIER = "vcDataIdentifier";
public static final String PROPERTY_NAME_TIME_POINTS = "timePoints";
public static final String PROPERTY_NAME_TIME_POINT = "timePoint";
public static final String PROPERTY_NAME_VARIABLE = "variable";
	//	private class RefreshedData {
//		public double[] newData;
//		public VariableType newVarType;
//		public double newTimePoint;
//		public ParticleDataBlock newParticleDataBlock;
//		public Range newRange;
//		
//		public RefreshedData(double[] argNewData,
//				VariableType argNewVarType,
//				double argNewTimePoint,
//				ParticleDataBlock argNewParticleDataBlock){
//			newData = argNewData;
//			newVarType = argNewVarType;
//			newTimePoint = argNewTimePoint;
//			newParticleDataBlock = argNewParticleDataBlock;
//			//calculateRange();
//		}
////		private void calculateRange(){
////			double min = Double.POSITIVE_INFINITY;
////			double max = Double.NEGATIVE_INFINITY;
////			for(int i = 0; i < newData.length; i++){
////				if(!Double.isNaN(newData[i])){
////					min = Math.min(min, newData[i]);
////					max = Math.max(max, newData[i]);
////				}
////			}
////			newRange = new Range(min,max);
////		}
//	}
	//
	protected transient PropertyChangeSupport propertyChange = null;
	private DataIdentifier fieldDataIdentifier = null;
	/**
	 * support lazy retrieval of {@link #fieldParticleDataBlock}, among other things
	 */
	private double fieldTimePoint = -1;
	private double[] dataValues = null;
	private DataIdentifier[] dataIdentifiers = null;
	private boolean particleData = false;
	/**
	 * lazily retrieved to avoid data transfer to VCellClient when not needed
	 */
	private ParticleDataBlock fieldParticleDataBlock = null;
	private CartesianMesh cartesianMesh = null;
//	private Range dataRange = null;
	private double[] fieldTimePoints = null;
	private VCDataIdentifier vcDataIdentifier = null;
	public static final String PROP_PDE_DATA_CONTEXT = "pdeDataContext";

	
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
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.addProxyListener(getPropertyChange(), listener);
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

protected void externalRefresh() throws DataAccessException {
	refreshData(fieldDataIdentifier, getTimePoint(),true);
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
public abstract AnnotatedFunction[] getFunctions() throws DataAccessException;


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
public abstract PlotData getLineScan(String variable, double time, SpatialSelection spatialSelection) throws DataAccessException;

/**
 * Gets the particleDataBlock property (cbit.vcell.simdata.ParticleDataBlock) value.
 * @return The particleDataBlock property value.
 * @see #setParticleDataBlock
 */
public ParticleDataBlock getParticleDataBlock() {
	if (fieldParticleDataBlock == null) {
		try {
			fieldParticleDataBlock = getParticleDataBlock(fieldTimePoint);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	return fieldParticleDataBlock;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:20:57 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected abstract ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException;


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
protected abstract SimDataBlock getSimDataBlock(String varName, double time) throws DataAccessException;
public abstract DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException;


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
public abstract TimeSeriesJobResults getTimeSeriesValues(TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException;


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
public abstract void makeRemoteFile(ExportSpecs exportSpecs) throws DataAccessException;


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

private boolean bBusy = false;
public boolean isBusy(){
	return bBusy;
}
public synchronized void waitWhileBusy(){
	//
	//This method added for synchronized blocking of access to PDEDatacontext
	//when refreshData is updating to prevent threads seeing an inconsistent view of PDEDataContext data.
	//Any thread calling this method will block if another thread is executing refreshData() until refreshData() has finished.
	//
//	System.out.println("Reading bBusy="+bBusy);
	while (bBusy) {
		//We should never get here,  waitWhileBusy() method will not be entered by any thread
		//when bBusy == true (because refreshData(...) is synchronized).  If we do get here,
		//wait() is guaranteed to be notified (woken up)
		//by refreshData(...) when bBusy == false because refreshData controls the value of bBusy.
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
private synchronized void refreshData(DataIdentifier selectedDataIdentifier, double timePoint,boolean bForce) throws DataAccessException{
	try{
		bBusy = true;
//		System.out.println("Setting bBusy="+bBusy);
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		if(!bForce){
			if(getDataIdentifier() != null && getDataIdentifier().equals(selectedDataIdentifier) && getTimePoint() == timePoint){
				return;
			}
		}
		if(selectedDataIdentifier == null){
			selectedDataIdentifier = dataIdentifiers[0];
		}
		if(timePoint == -1){
			timePoint = fieldTimePoints[0];
		}
		
		if (! BeanUtils.arrayContains(dataIdentifiers, selectedDataIdentifier)) {
			throw new DataAccessException("Requested variable not found");
		}
		if (BeanUtils.firstIndexOf(getTimePoints(), timePoint) == -1) {
			if (getTimePoints() == null || getTimePoints().length == 0) {
				throw new DataAccessException("No timepoints available");
			} else {
				throw new DataAccessException("Requested time not found");
			}
		}
		ParticleDataBlock newParticleDataBlock = null;
		SimDataBlock simdataBlock = getSimDataBlock(selectedDataIdentifier.getName(),timePoint);
		if (hasParticleData()) {
			newParticleDataBlock = null; 
		}
	
	//	RefreshedData refreshedData = new RefreshedData(
	//				simdataBlock.getData(),
	//				simdataBlock.getVariableType(),
	//				timePoint,
	//				newParticleDataBlock
	//				);
		
		boolean bVarChanged = false;
		DataIdentifier oldDataiDataIdentifier = fieldDataIdentifier;
		boolean bTimePointChanged = false;
		double oldTimePoint = getTimePoint();
		
		if(!selectedDataIdentifier.equals(fieldDataIdentifier)){
			fieldDataIdentifier = selectedDataIdentifier;
			bVarChanged = true;
		}
	
		if(timePoint != getTimePoint()){
			fieldTimePoint = timePoint;
			bTimePointChanged = true;
		}
		
		setDataValues(simdataBlock.getData());
		setParticleDataBlock(newParticleDataBlock);
	//	setDataRange(refreshedData.newRange);	
		
		if(bVarChanged){
			firePropertyChange(PROPERTY_NAME_VARIABLE, oldDataiDataIdentifier, fieldDataIdentifier);
		}
		if(bTimePointChanged){
			firePropertyChange(PROPERTY_NAME_TIME_POINT, new Double(oldTimePoint), new Double(getTimePoint()));
		}
	}finally{
		bBusy = false;
	//	System.out.println("Setting bBusy="+bBusy);
		notifyAll();
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
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.removeProxyListener(getPropertyChange(), listener);
	getPropertyChange().removePropertyChangeListener(listener);
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
	dataIdentifiers = newDataIdentifiers;
	if (getVariableName()==null && dataIdentifiers!=null && dataIdentifiers.length>0){
		setVariable(dataIdentifiers[0]);
	}

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
		fieldDataIdentifier = null;
		firePropertyChange(SimDataConstants.PROPERTY_NAME_DATAIDENTIFIERS, oldDataIdentifiers, newDataIdentifiers);
	}
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

public void setVariableAndTime(DataIdentifier selectedDataIdentifier, double timePoint) throws DataAccessException {
	refreshData(selectedDataIdentifier, timePoint, false);
}

public void setTimePoint(double timePoint) throws DataAccessException {
	setVariableAndTime(getDataIdentifier(), timePoint);
}

/**
 * Sets the timePoints property (double[]) value.
 * @param timePoints The new value for the property.
 * @see #getTimePoints
 */
protected void setTimePoints(double[] timePoints) {
	double[] oldValue = fieldTimePoints;
	fieldTimePoints = timePoints;
	firePropertyChange(PROPERTY_NAME_TIME_POINTS, oldValue, timePoints);
}

public void setVariable(DataIdentifier selectedDataIdentifier) throws DataAccessException {
	setVariableAndTime(selectedDataIdentifier, getTimePoint());
}

public void setVariableName(String varName) throws DataAccessException {
	DataIdentifier dataIdentifier = findDataIdentifier(varName);
	if(dataIdentifier == null){
		throw new DataAccessException("Couldn't find DataIdentifier for variable name "+varName);
	}
	setVariableAndTime(dataIdentifier, getTimePoint());
}


private DataIdentifier findDataIdentifier(String varName) {
	DataIdentifier foundDataIdentifier = null;
	for (int i = 0; i < dataIdentifiers.length; i++){
		if (dataIdentifiers[i].getName().equals(varName)){
			foundDataIdentifier = dataIdentifiers[i];
			break;
		}
	}
	return foundDataIdentifier;
}

public void setVariableNameAndTime(String varName, double timePoint) throws DataAccessException {
	DataIdentifier dataIdentifier = findDataIdentifier(varName);
	if (dataIdentifier == null){
		throw new DataAccessException("Couldn't find DataIdentifier for variable name "+varName);
	}
	setVariableAndTime(dataIdentifier, timePoint);
}

final void setVCDataIdentifier(VCDataIdentifier newValue) {
	if (vcDataIdentifier == newValue) {
		return;
	}
	VCDataIdentifier oldValue = vcDataIdentifier;
	this.vcDataIdentifier = newValue;
	
	firePropertyChange(PROPERTY_NAME_VCDATA_IDENTIFIER, oldValue, newValue);
}


public final VCDataIdentifier getVCDataIdentifier() {
	return vcDataIdentifier;
}

}
