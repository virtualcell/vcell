package org.vcell.physics.component;
import org.vcell.expression.ExpressionFactory;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:26:30 PM)
 * @author: Jim Schaff
 */
public class VoltageSource extends TwoPortElectricalComponent {
/**
 * VoltageSource constructor comment.
 */
public VoltageSource(String argName, double voltage) {
	super(argName);
	Parameter VConstant = new Parameter("VCC");
	addSymbol(VConstant);
	try {
		addEquation(ExpressionFactory.createExpression("VCC - V"));
		addEquation(ExpressionFactory.createExpression("VCC - "+voltage));
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}