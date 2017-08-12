/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class VersionableType implements java.io.Serializable {
	private int vType;
	private String name;
	private Class<?> vClass;
	private boolean bTopLevel;

	public static final VersionableType VCImage				= new VersionableType(0,"VCImage", "cbit.image.VCImage",true);
	public static final VersionableType Geometry			= new VersionableType(1,"Geometry", "cbit.vcell.geometry.Geometry",true);
	public static final VersionableType MathDescription		= new VersionableType(2,"MathDescription", "cbit.vcell.math.MathDescription",false);
	public static final VersionableType Model				= new VersionableType(3,"Physiology", "cbit.vcell.model.Model",false);
	public static final VersionableType SimulationContext	= new VersionableType(5,"SimulationSpecification", "cbit.vcell.mapping.SimulationContext",false);
	public static final VersionableType Simulation			= new VersionableType(6,"Simulation", "cbit.vcell.solver.Simulation",false);
	public static final VersionableType BioModelMetaData	= new VersionableType(7,"BioModel", "cbit.vcell.biomodel.BioModelMetaData",true);
	public static final VersionableType MathModelMetaData	= new VersionableType(8,"MathModel", "cbit.vcell.mathmodel.MathModelMetaData",true);
/**
 * VersionableType constructor comment.
 */
private VersionableType(int aVType, String aName, String aQualifiedClassName, boolean argTopLevel) {
	this.vType = aVType;
	this.name = aName;
	this.bTopLevel = argTopLevel;
	try {
		this.vClass = Class.forName(aQualifiedClassName);
	}catch (ClassNotFoundException e){
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof VersionableType){
		VersionableType vt = (VersionableType)obj;
		if (vt.vType == vType){
			return true;
		}else{
			return false;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getCode() {
	return vType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/01 9:01:08 AM)
 * @return boolean
 */
public boolean getIsTopLevel() {
	return bTopLevel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getTypeName() {
	return name;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Class
 */
public Class<?> getVersionClass() {
	return vClass;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int hashCode() {
	return vType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return name+"_type";
}
}
