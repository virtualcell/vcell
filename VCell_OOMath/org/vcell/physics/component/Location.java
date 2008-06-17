package org.vcell.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2004 6:56:14 AM)
 * @author: Jim Schaff
 */
public abstract class Location extends ModelComponent {
	private Location adjacentLocations[] = new Location[0];
	private int dimension;
/**
 * Insert the method's description here.
 * Creation date: (1/7/2004 8:55:56 AM)
 * @param argName java.lang.String
 */
public Location(String argName, int argDimension) {
	super(argName);
	dimension = argDimension;
}
/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 3:34:15 PM)
 * @param identifier ncbc.physics.component.Identifier
 */
public void addAdjacentLocation(Location adjacentLocation) {
	setAdjacentLocations((Location[])org.vcell.util.BeanUtils.addElement(adjacentLocations,adjacentLocation));
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 8:01:11 PM)
 * @return ncbc.physics.component.Location[]
 */
public Location[] getAdjacentLocations() {
	return adjacentLocations;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 8:01:11 PM)
 * @param newAdjacentLocations ncbc.physics.component.Location[]
 */
public void setAdjacentLocations(Location[] newAdjacentLocations) {
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
public int getDimension() {
	return dimension;
}

protected static VCUnitDefinition getSizeUnit(int dimension){
	switch(dimension){
	case 1: {
		return VCUnitDefinition.UNIT_um;
		//break;
	}
	case 2: {
		return VCUnitDefinition.UNIT_um2;
		//break;
	}
	case 3: {
		return VCUnitDefinition.UNIT_um3;
		//break;
	}default:{
		throw new RuntimeException("expecting dimension of 1,2 or 3");
	}
	}

}


}
