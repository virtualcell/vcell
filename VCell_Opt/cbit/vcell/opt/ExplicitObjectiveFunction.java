package cbit.vcell.opt;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

/**
 * Insert the type's description here.
 * Creation date: (8/2/2005 1:30:16 PM)
 * @author: Jim Schaff
 */
public class ExplicitObjectiveFunction extends ObjectiveFunction {
	private IExpression exp = null;

/**
 * ExplicitObjectiveFunction constructor comment.
 * @param exp cbit.vcell.parser.Expression
 */
public ExplicitObjectiveFunction(IExpression argExpression) {
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
	IExpression exp = ExpressionFactory.createExpression(tokens);
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
public IExpression getExpression() {
	return exp;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 2:29:23 PM)
 * @return cbit.vcell.parser.Expression
 */
public IExpression getScaledExpression(String[] symbols, String[] scaledSymbols, double[] scaleFactors) {
	try {
		IExpression scaledExp = ExpressionFactory.createExpression(exp);
		for (int i = 0;i < symbols.length; i++){
			scaledExp.substituteInPlace(ExpressionFactory.createExpression(symbols[i]),ExpressionFactory.createExpression(scaleFactors[i]+" * "+scaledSymbols[i]));
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