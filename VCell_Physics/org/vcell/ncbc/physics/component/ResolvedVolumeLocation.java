package org.vcell.ncbc.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/14/2004 8:02:02 PM)
 * @author: Jim Schaff
 */
public class ResolvedVolumeLocation extends VolumeLocation {
	private String subVolumeName = null;
	private cbit.vcell.geometry.surface.VolumeGeometricRegion volumeGeometricRegion = null;
/**
 * ResolvedVolume constructor comment.
 * @param argName java.lang.String
 */
public ResolvedVolumeLocation(String argName) {
	super(argName);
}
/**
 * Insert the method's description here.
 * Creation date: (1/17/2004 8:23:39 AM)
 * @return boolean
 */
public boolean getResolved() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2004 11:34:51 AM)
 * @return java.lang.String
 */
public java.lang.String getSubVolumeName() {
	return subVolumeName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 3:40:32 PM)
 * @return int
 */
public cbit.vcell.geometry.surface.VolumeGeometricRegion getVolumeGeometricRegion() {
	return volumeGeometricRegion;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2004 11:35:13 AM)
 * @param newSubVolumeName java.lang.String
 */
public void setSubVolumeName(java.lang.String newSubVolumeName) {
	subVolumeName = newSubVolumeName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 3:40:32 PM)
 * @param newRegionID int
 */
public void setVolumeGeometricRegion(cbit.vcell.geometry.surface.VolumeGeometricRegion argVolumeGeometricRegion){
	volumeGeometricRegion = argVolumeGeometricRegion;
}
}
