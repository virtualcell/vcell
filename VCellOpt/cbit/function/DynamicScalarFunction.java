package cbit.function;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 4:10:45 PM)
 * @author: Michael Duff
 */
public class DynamicScalarFunction extends DefaultScalarFunction {
	private IExpression functionExp = null;
	private IExpression[] gradientExps = null;
	private String[] identifiers = null;
/**
 * DynamicScalarFunction constructor comment.
 */
public DynamicScalarFunction(IExpression argFunction, String argIdentifiers[]) throws org.vcell.expression.ExpressionException {
	identifiers = (String[])argIdentifiers.clone();
	functionExp = ExpressionFactory.createExpression(argFunction);

	//
	// create temporary symbolTable to assign indices to identifiers
	//
	org.vcell.expression.SimpleSymbolTable symbolTable = new org.vcell.expression.SimpleSymbolTable(identifiers);

	//
	// bind expression to temporary symbol table
	//
	
	functionExp.bindExpression(symbolTable);
	

	//
	// analytically evaluate gradient and bind to same symbol table (gives same indices for value vector)
	//
	initializeGradient(symbolTable);
	
}
public double[] evaluateGradient(double[] x){
	try{
		int n = getNumArgs();
		double[] grad = new double[n];
		
		for (int i = 0; i < n; i++){
			grad[i] = gradientExps[i].evaluateVector(x);
		}
		return grad;
	}catch(org.vcell.expression.ExpressionException e){
		throw new RuntimeException(e.getMessage());
	}
	
}
/**
 * f method comment.
 */
public double f(double[] x){
	try {
		return functionExp.evaluateVector(x);
		
	}catch(org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}		

}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 2:46:33 PM)
 * @return java.lang.String[]
 */
public String[] getIdentifiers() {
    return (String[]) identifiers.clone();
}
/**
 * Change this to return number of variables
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:10:45 PM)
 * @return int
 */
public int getNumArgs() {
	return identifiers.length;
}
/**
 * Best guess at this point
 * Insert the method's description here.
 * Creation date: (4/3/2002 5:37:12 PM)
 */
private void initializeGradient(org.vcell.expression.SymbolTable symbolTable) throws ExpressionException {
	int n = getNumArgs();
	gradientExps = new IExpression[n];
	for (int i=0;i<n;i++){
		
		gradientExps[i] = functionExp.differentiate(identifiers[i]);
		//
		// simplifies
		//
		gradientExps[i] = gradientExps[i].flatten();

		//
		// binds gradient expression to symbol table in preparation for evaluation with a vector
		//
		gradientExps[i].bindExpression(symbolTable);
		
	}
}
}
