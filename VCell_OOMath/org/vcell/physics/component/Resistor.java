package org.vcell.physics.component;
import org.vcell.expression.ExpressionFactory;

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
	Parameter R = new Parameter("R");
	addSymbol(R);
	try {
		addEquation(ExpressionFactory.createExpression("V - Ip*R"));
		addEquation(ExpressionFactory.createExpression("R - "+resistance));
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}