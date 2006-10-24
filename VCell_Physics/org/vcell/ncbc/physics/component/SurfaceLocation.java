package org.vcell.ncbc.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2004 6:57:50 AM)
 * @author: Jim Schaff
 */
public abstract class SurfaceLocation extends Location {
	private String membraneName = null;
/**
 * Insert the method's description here.
 * Creation date: (1/7/2004 8:59:17 AM)
 * @param argName java.lang.String
 */
public SurfaceLocation(String argName) {
	super(argName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2004 12:42:27 PM)
 * @return java.lang.String
 */
public java.lang.String getMembraneName() {
	return membraneName;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2004 12:42:27 PM)
 * @param newMembraneName java.lang.String
 */
public void setMembraneName(java.lang.String newMembraneName) {
	membraneName = newMembraneName;
}
}
