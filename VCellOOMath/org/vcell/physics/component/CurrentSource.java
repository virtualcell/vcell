package org.vcell.physics.component;
import org.vcell.expression.ExpressionFactory;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:24:50 PM)
 * @author: Jim Schaff
 */
public class CurrentSource extends TwoPortElectricalComponent {
/**
 * CurrentSource constructor comment.
 */
public CurrentSource(String argName, double current) {
	super(argName);
	Parameter IConstant = new Parameter("IConstant");
	addSymbol(IConstant);
	try {
		addEquation(ExpressionFactory.createExpression("IConstant - Ip"));
		addEquation(ExpressionFactory.createExpression("IConstant - "+current));
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}