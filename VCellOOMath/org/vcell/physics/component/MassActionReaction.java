package org.vcell.physics.component;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

/**
 * Insert the type's description here.
 * Creation date: (1/22/2006 9:46:58 PM)
 * @author: Jim Schaff
 */
public class MassActionReaction extends ModelComponent {
/**
 * Reaction constructor comment.
 * @param argName java.lang.String
 */
public MassActionReaction(String argName, String[] species, int[] stoich, boolean bFast) throws org.vcell.expression.ExpressionException {
	super(argName);
	if (!bFast){
		Variable rate = new Variable("rate");
		addSymbol(rate);
		Variable kon = new Variable("kon");
		Variable koff = new Variable("koff");
		addSymbol(kon);
		addSymbol(koff);
		IExpression forwardRate = ExpressionFactory.createExpression("kon");
		IExpression reverseRate = ExpressionFactory.createExpression("-koff");

		Connector[] connectors = new Connector[species.length];
		for (int i = 0; i < species.length; i++){
			Variable speciesEffort = new Variable(species[i]);
			addSymbol(speciesEffort);
			Variable speciesRate = new Variable(species[i]+"_rate");
			addSymbol(speciesRate);
			addEquation(ExpressionFactory.createExpression(species[i]+"_rate - "+stoich[i]+"*rate"));
			connectors[i] = new Connector(this,"conn_"+species[i],speciesEffort,speciesRate);
			if (stoich[i] > 0){
				reverseRate = ExpressionFactory.mult(reverseRate, ExpressionFactory.createExpression("pow("+species[i]+","+stoich[i]+")"));
			}else if (stoich[i] < 0){
				forwardRate = ExpressionFactory.mult(forwardRate, ExpressionFactory.createExpression("pow("+species[i]+","+(-stoich[i])+")"));
			}
		}
		addEquation(ExpressionFactory.createExpression("rate - "+forwardRate.infix()+" + "+reverseRate.infix()).flatten());
		addEquation(ExpressionFactory.createExpression("kon-1"));
		addEquation(ExpressionFactory.createExpression("koff-1"));
		setConnectors(connectors);
	}else{ // fast
		Variable rate = new Variable("rate");
		addSymbol(rate);
		Variable kon = new Variable("kon");
		Variable koff = new Variable("koff");
		addSymbol(kon);
		addSymbol(koff);
		IExpression forwardRate = ExpressionFactory.createExpression("kon");
		IExpression reverseRate = ExpressionFactory.createExpression("-koff");

		Connector[] connectors = new Connector[species.length];
		for (int i = 0; i < species.length; i++){
			Variable speciesEffort = new Variable(species[i]);
			addSymbol(speciesEffort);
			Variable speciesRate = new Variable(species[i]+"_rate");
			addSymbol(speciesRate);
			addEquation(ExpressionFactory.createExpression(species[i]+"_rate - "+stoich[i]+"*rate"));
			connectors[i] = new Connector(this,"conn_"+species[i],speciesEffort,speciesRate);
			if (stoich[i] > 0){
				reverseRate = ExpressionFactory.mult(reverseRate, ExpressionFactory.createExpression("pow("+species[i]+","+stoich[i]+")"));
			}else if (stoich[i] < 0){
				forwardRate = ExpressionFactory.mult(forwardRate, ExpressionFactory.createExpression("pow("+species[i]+","+(-stoich[i])+")"));
			}
		}
		addEquation(ExpressionFactory.createExpression("rate - "+forwardRate.infix()+" + "+reverseRate.infix()).flatten());
		addEquation(ExpressionFactory.createExpression("kon-1"));
		addEquation(ExpressionFactory.createExpression("koff-1"));
		//addEquation(new Expression("rate-0"));
		setConnectors(connectors);
	}
}
}