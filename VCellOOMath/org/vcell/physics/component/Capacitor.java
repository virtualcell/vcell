package org.vcell.physics.component;
import org.vcell.expression.ExpressionFactory;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2006 11:14:25 PM)
 * @author: Jim Schaff
 */
public class Capacitor extends TwoPortElectricalComponent {
/**
 * Resistor constructor comment.
 */
public Capacitor(String argName, double capacitance) {
	super(argName);
	Parameter C = new Parameter("C");
	addSymbol(C);
	try {
		addEquation(ExpressionFactory.createExpression("C*V.prime + Ip"));
		addEquation(ExpressionFactory.createExpression("C - "+capacitance));
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}