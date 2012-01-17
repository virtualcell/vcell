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

/**
 * This type was created in VisualAge.
 */
public class ODEDataBlock implements java.io.Serializable {

	private ODEDataInfo odeDataInfo = null;
	private cbit.vcell.solver.ode.ODESimData odeSimData = null;

/**
 * ODEDataBlock constructor comment.
 */
public ODEDataBlock(ODEDataInfo odeDataInfo, cbit.vcell.solver.ode.ODESimData odeSimData) {
	this.odeDataInfo = odeDataInfo;
	this.odeSimData = odeSimData;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/00 3:50:35 PM)
 * @return cbit.vcell.simdata.ODESimData
 */
public cbit.vcell.solver.ode.ODESimData getODESimData() {
	return odeSimData;
}
/**
 * This method was created in VisualAge.
 * @return long
 */
 public long getSizeInBytes() {
	return odeSimData.getSizeInBytes();
}
 
public long getEstimatedSizeInBytes() {
	return odeSimData.getEstimatedSizeInBytes();
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public long getTimeStamp() {
	return odeDataInfo.getTimeStamp();
}
}
