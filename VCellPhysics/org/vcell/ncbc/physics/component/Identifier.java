package org.vcell.ncbc.physics.component;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 11:29:20 AM)
 * @author: Jim Schaff
 */
public class Identifier {
	private String name = null;
	private VCUnitDefinition unit = null;

/**
 * Identifier constructor comment.
 */
public Identifier(String argName, VCUnitDefinition argUnit) {
	super();
	this.name = argName;
	this.unit = argUnit;
}


/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 8:09:50 PM)
 * @return java.lang.String
 */
public String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 11:00:07 AM)
 * @return ucar.units.Unit
 */
public VCUnitDefinition getUnit() {
	return unit;
}
}