package org.vcell.physics.component;

import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;


/**
 * Insert the type's description here.
 * Creation date: (1/22/2006 9:46:58 PM)
 * @author: Jim Schaff
 */
public class Species extends ModelComponent {
/**
 * Reaction constructor comment.
 * @param argName java.lang.String
 */
public Species(String argName) throws org.vcell.expression.ExpressionException {
	super(argName);
	Variable rate = new Variable("rate");
	addSymbol(rate);

	Variable concentration = new Variable("conc");
	addSymbol(concentration);
	addEquation(ExpressionFactory.createExpression("conc.prime - rate"));
	setConnectors(new Connector[] { new Connector(this,"conn",concentration,rate) });
}
/**
 * Reaction constructor comment.
 * @param argName java.lang.String
 */
public Species(String argName, String[] species, int[] stoich, IExpression[] equations) throws org.vcell.expression.ExpressionException {
	super(argName);
	Variable rate = new Variable("rate");
	addSymbol(rate);

	Connector[] connectors = new Connector[species.length];
	for (int i = 0; i < species.length; i++){
		Variable speciesEffort = new Variable(species[i]);
		addSymbol(speciesEffort);
		Variable speciesRate = new Variable(species[i]+"_rate");
		addSymbol(speciesRate);
		addEquation(ExpressionFactory.createExpression(species+"_rate - "+stoich[i]+"*rate"));
		connectors[i] = new Connector(this,"conn_"+species,speciesEffort,speciesRate);
	}
	setConnectors(connectors);
}
}
