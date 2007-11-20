package org.vcell.ncbc.physics.component;

import org.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 11:11:35 AM)
 * @author: Jim Schaff
 */
public class Port {
	public static final int ROLE_USES = 1;
	public static final int ROLE_INFLUENCES = 2;
	public static final int ROLE_DEFINES = 3;
	public static final int ROLE_NONE = 4;


	private int role = 0;
	private Variable variable = null;

/**
 * Port constructor comment.
 */
public Port(Variable argVariable, int argRole) {
	super();
	this.variable = argVariable;
	this.role = argRole;
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 5:25:05 PM)
 * @return int
 */
public int getRole() {
	return role;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 1:30:06 PM)
 * @return ucar.units.Unit
 */
public VCUnitDefinition getUnit() {
	return getVariable().getUnit();
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2004 1:05:49 PM)
 * @return ncbc.physics.component.Variable
 */
public Variable getVariable() {
	return variable;
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/2004 5:25:05 PM)
 * @param newRole int
 */
public void setRole(int newRole) {
	role = newRole;
}
}