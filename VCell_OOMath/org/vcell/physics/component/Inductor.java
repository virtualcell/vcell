package org.vcell.physics.component;

import org.vcell.expression.ExpressionFactory;

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
	Parameter L = new Parameter("L");
	addSymbol(L);
	try {
		addEquation(ExpressionFactory.createExpression("L*Ip.prime - V"));
		addEquation(ExpressionFactory.createExpression("L - "+inductance));
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}
