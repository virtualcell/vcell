package org.vcell.physics.component;

import org.vcell.expression.IExpression;
import org.vcell.expression.NameScope;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (11/17/2005 3:48:12 PM)
 * @author: Jim Schaff
 */
public abstract class PhysicalSymbol {

	private String name = null;
	private VCUnitDefinition unit = null;
	
	public PhysicalSymbol(String argName, VCUnitDefinition vcUnit) {
		this.name = argName;
		this.unit = vcUnit;
	}
	public String getName() {
		return name;
	}
	public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
		return unit;
	}
	public String toString() {
		return getClass().getName()
			+ "@"
			+ Integer.toHexString(hashCode())
			+ " "
			+ getName();
	}
}
