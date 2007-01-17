package cbit.function;

import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 4:41:22 PM)
 * @author: Michael Duff
 */
public abstract class DefaultScalarFunction implements ScalarFunction {
/**
 * DefaultVectorFunction constructor comment.
 */
public DefaultScalarFunction() {
	super();
}
public double[] evaluateGradient(double[] x) {
	return evaluateGradientCentralDifference(x);
}
double[] evaluateGradientCentralDifference(double[] x)
	{
		final double EPS = 1.0e-8;          // Square root of double precision
		double temp;
		double h;
		int n = getNumArgs();
		double f_forward;
        double f_backward;
		double[] grad = new double[n];
		
		for(int j=0;j<n;j++){               // Iteratively tweak x[j] elements
			temp = x[j];
			h = EPS*Math.abs(temp);
			if(h==0.0) h=EPS;
			x[j]=temp+h;
			h=x[j]-temp;                    // Recompute step-size (if x[j] was close to zero)
			f_forward = f(x); 			  // Vector function evaluated at x[j]+h
			x[j]=temp-h;
			f_backward = f(x);	          // Vector function evaluated at x[j]-h
			grad[j]=(f_forward-f_backward)/(2.0*h);
			x[j]=temp;
		}
		return grad;
	}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:42:59 PM)
 * @return double
 * @param x double[]
 */
public abstract double f(double[] x);
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:41:22 PM)
 * @return int
 */
public abstract int getNumArgs();
}
