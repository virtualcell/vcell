package org.vcell.physics.component;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;

import org.vcell.expression.ExpressionFactory;

import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2006 11:14:25 PM)
 * @author: Jim Schaff
 */
public class Inductor extends TwoPortElectricalComponent {
/**
 * Resistor constructor comment.
 */
public Inductor(String argName, double inductance) {
	super(argName);
	Parameter L = new Parameter("L", VCUnitDefinition.getInstance("GH"));
	addSymbol(L);
	try {
		addEquation(Expression.valueOf("L*d(Ip,t) - V(t)"));
		addEquation(Expression.valueOf("L - "+inductance));
	}catch (ParseException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}
