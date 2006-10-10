package org.vcell.physics.component;
import org.vcell.expression.ExpressionFactory;

/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:21:27 PM)
 * @author: Jim Schaff
 */
public class TwoPortElectricalComponent extends ModelComponent {
/**
 * TwoPortElectricalComponent constructor comment.
 */
public TwoPortElectricalComponent(String argName) {
	super(argName);
	Variable Vp = new Variable("Vp");
	Variable Vn = new Variable("Vn");
	Variable Ip = new Variable("Ip");
	Variable In = new Variable("In");
	Variable V = new Variable("V");
	addSymbol(Vp);
	addSymbol(Vn);
	addSymbol(Ip);
	addSymbol(In);
	addSymbol(V);
	Connector conPos = new Connector(this,"pos",Vp,Ip);
	Connector conNeg = new Connector(this,"neg",Vn,In);
	setConnectors(new Connector[] {conPos, conNeg});
	try {
		addEquation(ExpressionFactory.createExpression("(Vp-Vn) - V"));
		addEquation(ExpressionFactory.createExpression("Ip + In"));
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}