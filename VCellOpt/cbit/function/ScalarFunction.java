package cbit.function;

import org.vcell.expression.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (4/2/2002 1:26:52 PM)
 * @author: Michael Duff
 */
public interface ScalarFunction {
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:45:49 PM)
 * @return double[][]
 * @param x double[]
 * @exception org.vcell.expression.ExpressionException The exception description.
 */
double[] evaluateGradient(double[] x);
	double f(double[] x);
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:06:04 PM)
 * @return int
 */
int getNumArgs();
}
