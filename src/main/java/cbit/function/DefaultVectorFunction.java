/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.function;

import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 4:41:22 PM)
 * @author: Michael Duff
 */
public abstract class DefaultVectorFunction implements VectorFunction {
/**
 * DefaultVectorFunction constructor comment.
 */
public DefaultVectorFunction() {
	super();
}
public double[][] evaluateJacobian(double[] x){
	return evaluateJacobianCentralDifference(x);
}
double[][] evaluateJacobianCentralDifference(double[] x)
	{
		final double EPS = 1.0e-8;          // Square root of double precision
		double temp;
		double h;
		int n = getSystemDimension();
		if (n != x.length){
			throw new RuntimeException("Mismatch: system dim = "+getSystemDimension()+", vector dim = "+x.length);
		}
		double[] fvect_forward = new double[n];
        double[] fvect_backward = new double[n];
		double[][] jac = new double[n][n];
		
		for(int j=0;j<n;j++){               // Iteratively tweak x[j] elements
			temp = x[j];
			h = EPS*Math.abs(temp);
			if(h==0.0) h=EPS;
			x[j]=temp+h;
			h=x[j]-temp;                    // Recompute step-size (if x[j] was close to zero)
			fvect_forward = f(x); 			  // Vector function evaluated at x[j]+h
			x[j]=temp-h;
			fvect_backward = f(x);	// Vector function evaluated at x[j]-h
			for(int i=0;i<n;i++){
				jac[i][j]=(fvect_forward[i]-fvect_backward[i])/(2.0*h);
			}
			x[j]=temp;
		}
		return jac;
	}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:42:59 PM)
 * @return double[]
 * @param x double[]
 */
public abstract double[] f(double[] x);
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:41:22 PM)
 * @return int
 */
public abstract int getSystemDimension();
	double Norm2(double[] x) throws ExpressionException
	{
		double sum=0.0;
		int n = getSystemDimension();
		double[] ff = f(x);

		for(int i=0;i<n;i++){
			sum += ff[i]*ff[i];
		}
		
		return 0.5*sum;
	}
}
