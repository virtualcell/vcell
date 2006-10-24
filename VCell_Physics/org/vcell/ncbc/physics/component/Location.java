package org.vcell.ncbc.physics.component;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2004 6:56:14 AM)
 * @author: Jim Schaff
 */
public abstract class Location implements java.io.Serializable {
	private String name = null;
	//private Identifier identifiers[] = new Identifier[0];
	//private Equation equations[] = new Equation[0];
	private Location adjacentLocations[] = new Location[0];
/**
 * Insert the method's description here.
 * Creation date: (1/7/2004 8:55:56 AM)
 * @param argName java.lang.String
 */
public Location(String argName) {
	super();
	this.name = argName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 3:34:15 PM)
 * @param identifier ncbc.physics.component.Identifier
 */
public void addAdjacentLocation(Location adjacentLocation) {
	setAdjacentLocations((Location[])cbit.util.BeanUtils.addElement(adjacentLocations,adjacentLocation));
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 8:01:11 PM)
 * @return ncbc.physics.component.Location[]
 */
public org.vcell.ncbc.physics.component.Location[] getAdjacentLocations() {
	return adjacentLocations;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 2:51:25 PM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (1/17/2004 8:23:11 AM)
 * @return boolean
 */
public abstract boolean getResolved();
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 8:01:11 PM)
 * @param newAdjacentLocations ncbc.physics.component.Location[]
 */
public void setAdjacentLocations(org.vcell.ncbc.physics.component.Location[] newAdjacentLocations) {
	adjacentLocations = newAdjacentLocations;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:55:00 PM)
 * @return java.lang.String
 */
public String toString() {
	return getClass().getName()+"@"+Integer.toHexString(hashCode())+"("+getName()+")";
}
}
