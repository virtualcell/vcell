/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

import java.io.Serializable;

import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.Extent;

/**
 * Insert the type's description here.
 * Creation date: (10/11/00 10:14:12 AM)
 * @author: 
 */
public class ImagePlaneManager implements WorldCoordinateCalculator {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SourceDataInfo fieldImagePlaneData = null;
	private int fieldSlice = 0;
	private int fieldNormalAxis = Coordinate.Z_AXIS;
	private SourceDataInfo fieldSourceDataInfo = null;
	//
	private int lastImagePlaneDataSlice = -1;
	private int lastImagePlaneDataNormalAxis = -1;
/**
 * ImageContainer2 constructor comment.
 */
public ImagePlaneManager() {
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
 * Creation date: (10/11/00 12:27:01 PM)
 * @return cbit.vcell.geometry.Coordinate
 */
public CoordinateIndex getDataIndexFromUnitized2D(double x, double y) {
	if(getSourceDataInfo() != null){
		Coordinate standardXYZ = standardXYZFromUnitized(x,y);
		return getSourceDataInfo().getDataIndexFromUnitized(standardXYZ.getX(),standardXYZ.getY(),standardXYZ.getZ());		
	}
	return null;
}
/**
 * Gets the imagePlaneData property (cbit.image.SourceDataInfo) value.
 * @return The imagePlaneData property value.
 * @see #setImagePlaneData
 */
public SourceDataInfo getImagePlaneData() {
	return fieldImagePlaneData;
}
/**
 * Gets the normalAxis property (int) value.
 * @return The normalAxis property value.
 * @see #setNormalAxis
 */
public int getNormalAxis() {
	return fieldNormalAxis;
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
 * Gets the slice property (int) value.
 * @return The slice property value.
 * @see #setSlice
 */
public int getSlice() {
	return fieldSlice;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 11:49:11 AM)
 * @return double
 */
public double getSliceWorldCoordinate() {
	Coordinate wc = getWorldCoordinateFromUnitized2D(0,0);
	if(wc != null){
		return Coordinate.convertAxisFromStandardXYZToNormal(wc,Coordinate.Z_AXIS,getNormalAxis());
	}else{
		return 0;
	}
}
/**
 * Gets the sourceDataInfo property (cbit.image.SourceDataInfo) value.
 * @return The sourceDataInfo property value.
 * @see #setSourceDataInfo
 */
public SourceDataInfo getSourceDataInfo() {
	return fieldSourceDataInfo;
}

public boolean isCellCentered(){
	if(getSourceDataInfo() != null){
		return getSourceDataInfo().isCellCentered();
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 12:27:01 PM)
 * @return cbit.vcell.geometry.Coordinate
 */
public org.vcell.util.Coordinate getWorldCoordinateFromUnitized2D(double x, double y) {
	if(getSourceDataInfo() != null){
		//
		// numSlices (sliceBoundary) should be >= 1
		//
		if (sliceBoundary()<1){
			throw new RuntimeException("numSlices = "+sliceBoundary()+", should be > 1");
		}
		Coordinate standardXYZ = standardXYZFromUnitized(x,y);
		return getSourceDataInfo().getWorldCoordinateFromUnitized(standardXYZ.getX(),standardXYZ.getY(),standardXYZ.getZ());		
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
 * Sets the imagePlaneData property (cbit.image.SourceDataInfo) value.
 * @param imagePlaneData The new value for the property.
 * @see #getImagePlaneData
 */
private void setImagePlaneData(SourceDataInfo imagePlaneData) {
	SourceDataInfo oldValue = fieldImagePlaneData;
	fieldImagePlaneData = imagePlaneData;
	firePropertyChange("imagePlaneData", oldValue, imagePlaneData);
}
/**
 * Sets the normalAxis property (int) value.
 * @param normalAxis The new value for the property.
 * @see #getNormalAxis
 */
public void setNormalAxis(int normalAxis) {
	if (getSourceDataInfo() == null) {
		return;
	}
	if (normalAxis != Coordinate.X_AXIS && normalAxis != Coordinate.Y_AXIS && normalAxis != Coordinate.Z_AXIS) {
		return;
	}
	if (normalAxis == fieldNormalAxis) {
		return;
	}
	int oldValue = fieldNormalAxis;
	fieldNormalAxis = normalAxis;
	if (getSlice() >= sliceBoundary()) {
		setSlice(0);
	}
	firePropertyChange("normalAxis", new Integer(oldValue), new Integer(normalAxis));
	updateImagePlaneData();
}
/**
 * Sets the slice property (int) value.
 * @param slice The new value for the property.
 * @see #getSlice
 */
public void setSlice(int slice) {
    if (getSourceDataInfo() == null) {
        return;
    }
    if (slice < 0) {
        slice = 0;
    }
    if (slice >= sliceBoundary()) {
        slice = sliceBoundary() - 1;
    }
    if (slice == fieldSlice) {
        return;
    }
    int oldValue = fieldSlice;
    fieldSlice = slice;
    firePropertyChange("slice", new Integer(oldValue), new Integer(slice));
    updateImagePlaneData();
}
/**
 * Sets the sourceDataInfo property (cbit.image.SourceDataInfo) value.
 * @param sourceDataInfo The new value for the property.
 * @see #getSourceDataInfo
 */
public void setSourceDataInfo(SourceDataInfo sourceDataInfo) {
	if(	sourceDataInfo == null ||
		sourceDataInfo.getZSize() <= 1){
		setSlice(0);
		setNormalAxis(Coordinate.Z_AXIS);
	}else{
		int normalZ = (int)Coordinate.convertAxisFromStandardXYZToNormal(
			sourceDataInfo.getXSize(),
			sourceDataInfo.getYSize(),
			sourceDataInfo.getZSize(),
			Coordinate.Z_AXIS,
			getNormalAxis());
		if((normalZ-1) < getSlice()){
			setSlice(0);
			setNormalAxis(Coordinate.Z_AXIS);
		}
	}
	SourceDataInfo oldValue = fieldSourceDataInfo;
	fieldSourceDataInfo = sourceDataInfo;
	firePropertyChange("sourceDataInfo", oldValue, sourceDataInfo);
	lastImagePlaneDataNormalAxis = -1;
	lastImagePlaneDataSlice = -1;
	updateImagePlaneData();
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:22:20 PM)
 * @return int
 */
public int sliceBoundary() {
	//Return the Z_AXIS
	if (getSourceDataInfo() != null) {
		switch (getNormalAxis()) {
			case Coordinate.X_AXIS :
				return getSourceDataInfo().getXSize();
			case Coordinate.Y_AXIS :
				return getSourceDataInfo().getYSize();
			case Coordinate.Z_AXIS :
				return getSourceDataInfo().getZSize();
			default :
				throw new IllegalArgumentException("normalAxis wrong");
		}
	} else {
		return -1;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 1:52:04 PM)
 */
public org.vcell.util.Coordinate snapWorldCoordinate(org.vcell.util.Coordinate targetC) {
	if(getSourceDataInfo() != null){
		double distance = 0;
		org.vcell.util.CoordinateIndex centerCI = getSourceDataInfo().getDataIndexFromWorldCoordinate(targetC);
		Coordinate centerCoord = getSourceDataInfo().getWorldCoordinateFromIndex(centerCI);
		return centerCoord;
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/13/2004 4:27:15 PM)
 */
public Coordinate snapWorldCoordinateFace(Coordinate targetC) {
	
		if (getSourceDataInfo().isCellCentered())
		{
			return targetC;
		}
		org.vcell.util.CoordinateIndex centerCI = getSourceDataInfo().getDataIndexFromWorldCoordinate(targetC);
		Coordinate centerCoord = getSourceDataInfo().getWorldCoordinateFromIndex(centerCI);

		double diffX = centerCoord.getX()-targetC.getX();
		double diffY = centerCoord.getY()-targetC.getY();
		double diffZ = centerCoord.getZ()-targetC.getZ();
		
		if(Math.abs(diffX) >= Math.abs(diffY) && Math.abs(diffX) >= Math.abs(diffZ)){diffY=0;diffZ=0;}
		else if(Math.abs(diffY) >= Math.abs(diffX) && Math.abs(diffY) >= Math.abs(diffZ)){diffX=0;diffZ=0;}
		else {diffX=0;diffY=0;}
		
		CoordinateIndex offsetCI = new CoordinateIndex(
										centerCI.x-(new java.math.BigDecimal(diffX).signum()),
										centerCI.y-(new java.math.BigDecimal(diffY).signum()),
										centerCI.z-(new java.math.BigDecimal(diffZ).signum()));

		Coordinate offsetCoord = getSourceDataInfo().getWorldCoordinateFromIndex(offsetCI);
		Coordinate faceCoord = new Coordinate(
								(centerCoord.getX()+offsetCoord.getX())/2.0,
								(centerCoord.getY()+offsetCoord.getY())/2.0,
								(centerCoord.getZ()+offsetCoord.getZ())/2.0);
		return faceCoord;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 11:14:33 AM)
 * @return cbit.vcell.geometry.Coordinate
 * @param x double
 * @param y double
 */
private Coordinate standardXYZFromUnitized(double x, double y) {

	double z = 0;
	if (sliceBoundary() > 1)
	{
		z = (isCellCentered())?(double)worldSlice()/(double)(sliceBoundary()):(double)worldSlice()/(double)(sliceBoundary()-1);
	}
	return Coordinate.convertCoordinateFromNormalToStandardXYZ(x,y,z,getNormalAxis());
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 1:35:27 PM)
 */
private void updateImagePlaneData() {
	if (getSourceDataInfo() == null) {
		setImagePlaneData(null);
		return;
	}
	if (lastImagePlaneDataNormalAxis == getNormalAxis()) {
		return;
	}
	if (lastImagePlaneDataSlice == getSlice()) {
		return;
	}
	int type = getSourceDataInfo().getType();
	Serializable data = getSourceDataInfo().getData();
	int startIndex = getSourceDataInfo().getStartIndex();
	SourceDataInfo sdiSource = getSourceDataInfo();
	//
	org.vcell.util.Origin org = sdiSource.getOrigin();
	Extent ext = sdiSource.getExtent();
	//
	int startDelta = worldSlice() * (int)Coordinate.convertAxisFromStandardXYZToNormal(sdiSource.getXIncrement(),sdiSource.getYIncrement(),sdiSource.getZIncrement(),Coordinate.Z_AXIS,getNormalAxis()) ;
	int xSize = (int)Coordinate.convertAxisFromStandardXYZToNormal(sdiSource.getXSize(),sdiSource.getYSize(),sdiSource.getZSize(),Coordinate.X_AXIS,getNormalAxis());
	int xIncrement = (int)Coordinate.convertAxisFromStandardXYZToNormal(sdiSource.getXIncrement(),sdiSource.getYIncrement(),sdiSource.getZIncrement(),Coordinate.X_AXIS,getNormalAxis());
	double xOrigin = Coordinate.convertAxisFromStandardXYZToNormal(org.getX(),org.getY(),org.getZ(),Coordinate.X_AXIS,getNormalAxis());
	double xExtent = Coordinate.convertAxisFromStandardXYZToNormal(ext.getX(),ext.getY(),ext.getZ(),Coordinate.X_AXIS,getNormalAxis());
	int ySize = (int)Coordinate.convertAxisFromStandardXYZToNormal(sdiSource.getXSize(),sdiSource.getYSize(),sdiSource.getZSize(),Coordinate.Y_AXIS,getNormalAxis());
	int yIncrement = (int)Coordinate.convertAxisFromStandardXYZToNormal(sdiSource.getXIncrement(),sdiSource.getYIncrement(),sdiSource.getZIncrement(),Coordinate.Y_AXIS,getNormalAxis());
	double yOrigin = Coordinate.convertAxisFromStandardXYZToNormal(org.getX(),org.getY(),org.getZ(),Coordinate.Y_AXIS,getNormalAxis());
	double yExtent = Coordinate.convertAxisFromStandardXYZToNormal(ext.getX(),ext.getY(),ext.getZ(),Coordinate.Y_AXIS,getNormalAxis());
	//
	SourceDataInfo sdi = new SourceDataInfo(type, data, null, startIndex + startDelta, xSize, xIncrement, xOrigin, xExtent, ySize, yIncrement, yOrigin, yExtent);
	sdi.setIsChombo(sdiSource.isChombo());
	sdi.setSolverDataType(sdiSource.getSolverDataType());
	setImagePlaneData(sdi);
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2003 11:25:48 AM)
 * @return int
 */
private int worldSlice() {

	//if(getNormalAxis() == Coordinate.Y_AXIS){
		//return sliceBoundary()-1-getSlice();
	//}else{
		return getSlice();
	//}
}
}
