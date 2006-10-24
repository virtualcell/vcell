package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/13/2004 5:18:34 PM)
 * @author: Jim Schaff
 */
public class TimeDerivative extends Derivative {
/**
 * TimeDerivative constructor comment.
 * @param argName java.lang.String
 * @param argUnit ucar.units.Unit
 */
public TimeDerivative(Variable var) {
	super(var, Variable.TIME_SECONDS, 1, var.getUnit().divideBy(Variable.TIME_SECONDS.getUnit()));
}
}