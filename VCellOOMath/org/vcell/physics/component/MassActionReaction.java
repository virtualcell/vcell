package org.vcell.physics.component;
import cbit.vcell.parser.Expression;
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
public MassActionReaction(String argName, String[] species, int[] stoich, boolean bFast) throws cbit.vcell.parser.ExpressionException {
	super(argName);
	if (!bFast){
		Variable rate = new Variable("rate");
		addSymbol(rate);
		Variable kon = new Variable("kon");
		Variable koff = new Variable("koff");
		addSymbol(kon);
		addSymbol(koff);
		Expression forwardRate = new Expression("kon");
		Expression reverseRate = new Expression("-koff");

		Connector[] connectors = new Connector[species.length];
		for (int i = 0; i < species.length; i++){
			Variable speciesEffort = new Variable(species[i]);
			addSymbol(speciesEffort);
			Variable speciesRate = new Variable(species[i]+"_rate");
			addSymbol(speciesRate);
			addEquation(new Expression(species[i]+"_rate - "+stoich[i]+"*rate"));
			connectors[i] = new Connector(this,"conn_"+species[i],speciesEffort,speciesRate);
			if (stoich[i] > 0){
				reverseRate = Expression.mult(reverseRate, new Expression("pow("+species[i]+","+stoich[i]+")"));
			}else if (stoich[i] < 0){
				forwardRate = Expression.mult(forwardRate, new Expression("pow("+species[i]+","+(-stoich[i])+")"));
			}
		}
		addEquation(new Expression("rate - "+forwardRate.infix()+" + "+reverseRate.infix()).flatten());
		addEquation(new Expression("kon-1"));
		addEquation(new Expression("koff-1"));
		setConnectors(connectors);
	}else{ // fast
		Variable rate = new Variable("rate");
		addSymbol(rate);
		Variable kon = new Variable("kon");
		Variable koff = new Variable("koff");
		addSymbol(kon);
		addSymbol(koff);
		Expression forwardRate = new Expression("kon");
		Expression reverseRate = new Expression("-koff");

		Connector[] connectors = new Connector[species.length];
		for (int i = 0; i < species.length; i++){
			Variable speciesEffort = new Variable(species[i]);
			addSymbol(speciesEffort);
			Variable speciesRate = new Variable(species[i]+"_rate");
			addSymbol(speciesRate);
			addEquation(new Expression(species[i]+"_rate - "+stoich[i]+"*rate"));
			connectors[i] = new Connector(this,"conn_"+species[i],speciesEffort,speciesRate);
			if (stoich[i] > 0){
				reverseRate = Expression.mult(reverseRate, new Expression("pow("+species[i]+","+stoich[i]+")"));
			}else if (stoich[i] < 0){
				forwardRate = Expression.mult(forwardRate, new Expression("pow("+species[i]+","+(-stoich[i])+")"));
			}
		}
		addEquation(new Expression("rate - "+forwardRate.infix()+" + "+reverseRate.infix()).flatten());
		addEquation(new Expression("kon-1"));
		addEquation(new Expression("koff-1"));
		//addEquation(new Expression("rate-0"));
		setConnectors(connectors);
	}
}
}