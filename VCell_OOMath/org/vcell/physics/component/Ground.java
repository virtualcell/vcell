package org.vcell.physics.component;
import org.vcell.expression.ExpressionFactory;

/**
 * Insert the type's description here.
 * Creation date: (1/22/2006 9:39:29 PM)
 * @author: Jim Schaff
 */
public class Ground extends OnePortElectricalDevice {
/**
 * Ground constructor comment.
 * @param argName java.lang.String
 */
public Ground(String argName) {
	super(argName);
	try {
		addEquation(ExpressionFactory.createExpression("V-0"));
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}