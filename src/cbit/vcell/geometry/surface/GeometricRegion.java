/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (5/27/2004 11:31:42 AM)
 * @author: Jim Schaff
 */
public abstract class GeometricRegion implements java.io.Serializable, org.vcell.util.Matchable {
	private String fieldName = null;
	private GeometricRegion[] fieldAdjacentGeometricRegions = new GeometricRegion[0];
	private double fieldSize = -1;
	private VCUnitDefinition fieldSizeUnit = null;

/**
 * GeometricRegion constructor comment.
 */
public GeometricRegion(String argName, double argSize, VCUnitDefinition argSizeUnit) {
	super();
	this.fieldName = argName;
	this.fieldSize = argSize;
	this.fieldSizeUnit = argSizeUnit;
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 12:19:49 PM)
 * @param adjacentRegion cbit.vcell.geometry.surface.GeometricRegion
 */
public void addAdjacentGeometricRegion(GeometricRegion adjacentRegion) {
	setAdjacentGeometricRegions((GeometricRegion[])org.vcell.util.BeanUtils.addElement(fieldAdjacentGeometricRegions,adjacentRegion));
}


/**
 * Insert the method's description here.
 * Creation date: (5/27/2004 11:43:14 AM)
 * @return boolean
 * @param obj cbit.util.Matchable
 */
protected boolean compareEqual0(GeometricRegion geometricRegion) {
	if (!org.vcell.util.Compare.isEqual(getName(),geometricRegion.getName())){
		return false;
	}
	if (fieldAdjacentGeometricRegions!=null || geometricRegion.fieldAdjacentGeometricRegions!=null){
		if (fieldAdjacentGeometricRegions==null || geometricRegion.fieldAdjacentGeometricRegions==null){
			return false;
		}
		if (fieldAdjacentGeometricRegions.length != geometricRegion.fieldAdjacentGeometricRegions.length){
			return false;
		}
		for (int i = 0; i < fieldAdjacentGeometricRegions.length; i++){
			if (!fieldAdjacentGeometricRegions[i].getName().equals(geometricRegion.fieldAdjacentGeometricRegions[i].getName())){
				return false;
			}
		}
	}
	if (fieldSize != geometricRegion.fieldSize){
		return false;
	}
	if (!org.vcell.util.Compare.isEqual(getSizeUnit(),geometricRegion.getSizeUnit())){
		return false;
	}
	return true;
}


/**
 * Gets the adjacentGeometricRegions property (cbit.vcell.geometry.surface.GeometricRegion[]) value.
 * @return The adjacentGeometricRegions property value.
 * @see #setAdjacentGeometricRegions
 */
public cbit.vcell.geometry.surface.GeometricRegion[] getAdjacentGeometricRegions() {
	return fieldAdjacentGeometricRegions;
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}


/**
 * Gets the size property (java.lang.Double) value.
 * @return The size property value.
 * @see #setSize
 */
public double getSize() {
	return fieldSize;
}


/**
 * Gets the sizeUnit property (cbit.vcell.units.VCUnitDefinition) value.
 * @return The sizeUnit property value.
 * @see #setSizeUnit
 */
public cbit.vcell.units.VCUnitDefinition getSizeUnit() {
	return fieldSizeUnit;
}


/**
 * Sets the adjacentGeometricRegions property (cbit.vcell.geometry.surface.GeometricRegion[]) value.
 * @param adjacentGeometricRegions The new value for the property.
 * @see #getAdjacentGeometricRegions
 */
public void setAdjacentGeometricRegions(cbit.vcell.geometry.surface.GeometricRegion[] adjacentGeometricRegions) {
	fieldAdjacentGeometricRegions = adjacentGeometricRegions;
}


public void setName(String name) {
	fieldName = name;
}

}
