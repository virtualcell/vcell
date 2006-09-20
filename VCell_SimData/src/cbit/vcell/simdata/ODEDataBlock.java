package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ODEDataBlock implements java.io.Serializable {

	private ODEDataInfo odeDataInfo = null;
	private cbit.vcell.simdata.ODESimData odeSimData = null;

/**
 * ODEDataBlock constructor comment.
 */
public ODEDataBlock(ODEDataInfo odeDataInfo, cbit.vcell.simdata.ODESimData odeSimData) {
	this.odeDataInfo = odeDataInfo;
	this.odeSimData = odeSimData;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/00 3:50:35 PM)
 * @return cbit.vcell.simdata.ODESimData
 */
public cbit.vcell.simdata.ODESimData getODESimData() {
	return odeSimData;
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public long getSizeInBytes() {
	return odeSimData.getSizeInBytes();
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public long getTimeStamp() {
	return odeDataInfo.getTimeStamp();
}
}
