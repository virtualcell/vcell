package cbit.vcell.opt;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (8/2/2005 1:30:16 PM)
 * @author: Jim Schaff
 */
public class ExplicitObjectiveFunction extends ObjectiveFunction {
	private Expression exp = null;

/**
 * ExplicitObjectiveFunction constructor comment.
 * @param exp cbit.vcell.parser.Expression
 */
public ExplicitObjectiveFunction(cbit.vcell.parser.Expression argExpression) {
	if (argExpression == null){
		throw new IllegalArgumentException("expression cannot be null");
	}
	if (argExpression.isLogical() || argExpression.isRelational()){
		throw new RuntimeException("Objective function expression should evaluate to a real, not a boolean");
	}
	
	this.exp = argExpression;
}


/**
 * Insert the method's description here.
 * Creation date: (8/2/2005 2:07:28 PM)
 * @param tokens java.io.StreamTokenizer
 */
public static ExplicitObjectiveFunction fromVCML(org.vcell.util.CommentStringTokenizer tokens) throws ExpressionException {
	Expression exp = new Expression(tokens);
	return new ExplicitObjectiveFunction(exp);
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 2:30:24 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(java.util.Vector issueList) {}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 2:29:23 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getExpression() {
	return exp;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 2:29:23 PM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getScaledExpression(String[] symbols, String[] scaledSymbols, double[] scaleFactors) {
	try {
		Expression scaledExp = new Expression(exp);
		for (int i = 0;i < symbols.length; i++){
			scaledExp.substituteInPlace(new Expression(symbols[i]),new Expression(scaleFactors[i]+" * "+scaledSymbols[i]));
		}
		return scaledExp;
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/2/2005 2:10:38 PM)
 * @return java.lang.String
 */
public String getVCML() {
	return "ExplicitObjectiveFunction   "+getExpression().infix()+";\n";
}
}