package org.vcell.physics.component;
import org.vcell.units.VCUnitDefinition;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;


/**
 * Insert the type's description here.
 * Creation date: (1/9/2006 11:14:25 PM)
 * @author: Jim Schaff
 */
public class Resistor extends TwoPortElectricalComponent {
/**
 * Resistor constructor comment.
 */
public Resistor(String argName, double resistance) {
	super(argName);
	Parameter R = new Parameter("R",VCUnitDefinition.getInstance("gigaohm"));
	addSymbol(R);
	try {
		addEquation(Expression.valueOf("V(t) - Ip(t)*R"));
		addEquation(Expression.valueOf("R - "+resistance));
	}catch (ParseException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}