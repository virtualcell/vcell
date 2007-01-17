package cbit.vcell.bionetgen;
import cbit.vcell.parser.Expression;


/**
 * Insert the type's description here.
 * Creation date: (1/13/2006 5:47:24 PM)
 * @author: Jim Schaff
 */
public class BNGReaction {
	private Expression paramExpression = null; 
	private BNGSpecies[] reactants = null;
	private BNGSpecies[] products = null;

/**
 * BNGReaction constructor comment.
 */
public BNGReaction(BNGSpecies[] argReactants, BNGSpecies[] argPdts, Expression argExpr) {
	super();
	if (argReactants.length > 0) {
		reactants = argReactants;
	}
	if (argPdts.length > 0) {
		products = argPdts;
	}
	paramExpression = argExpr;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public Expression getParamExpression() {
	return paramExpression;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public BNGSpecies[] getProducts() {
	return products;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public BNGSpecies[] getReactants() {
	return reactants;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public void setParamExpression(Expression argExpr) {
	paramExpression = argExpr;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public void setProducts(BNGSpecies[] argPdts) {
	products = argPdts;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public void setReactants(BNGSpecies[] argReacts) {
	reactants = argReacts;
}


/**
 * Insert the method's description here.
 * Creation date: (1/13/2006 6:01:30 PM)
 */
public String writeReaction() {
	//
	// Writes out the reaction represented by this class in the proper reaction format :
	// Since this is a reaction rather than a reaction rule, for now, it is a forward reaction, with reactant(s), pdt(s), rate const expression.
	//
	// e.g., 		 A + B  -> C		kf
	//
	
	String reactionStr = "";

	int numReactants = getReactants().length;
	int numPdts = getProducts().length;
	
	// Write out the reactants
	for (int i = 0; i < numReactants; i++){
		if (i == 0) {
			reactionStr = getReactants()[i].getName();
			continue;
		}
		reactionStr = reactionStr + " + " + getReactants()[i].getName();
	}

	// Write the irreversible reaction symbol
	reactionStr = reactionStr + " -> ";

	// Write the products
	for (int i = 0; i < numPdts; i++){
		if (i == 0) {
			reactionStr = reactionStr + getProducts()[i].getName();
			continue;
		}
		reactionStr = reactionStr + " + " + getProducts()[i].getName();
	}

	reactionStr = reactionStr + "\t\t";
	
	// Write the parameter expression
	reactionStr = reactionStr + getParamExpression().infix();

	return reactionStr;
}
}