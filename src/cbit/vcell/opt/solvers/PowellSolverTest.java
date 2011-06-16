/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt.solvers;

/**
 * Insert the type's description here.
 * Creation date: (5/3/2002 9:15:33 AM)
 * @author: Michael Duff
 */
public class PowellSolverTest {
/**
 * ConjGradTest constructor comment.
 */
public PowellSolverTest() {
	super();
}
/**
 * Insert the method's description here.
 *
 *  Test of PowellWrap
 *
 * Creation date: (5/3/2002 9:15:57 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	try {
		final int NDIM = 3;
		final double FTOL = 1.0e-6;
		final double PIO2 = 1.5707963;

		
		int iter,k;
		double angl,fret;

		double[] p= new double[NDIM];
	    double[][] xi= new double[NDIM][NDIM];

		
		cbit.function.ScalarFunction func = new cbit.function.DynamicScalarFunction(
			new cbit.vcell.parser.Expression("x1*x1+5"),
			new String[] { "x1" });
	//	function.ScalarFunction func = new MyObjectiveFunction();

		
		System.out.println("Program finds the minimum of a function");
		System.out.println("with different trial starting vectors.");
		System.out.println("True minimum is (0.5,0.5,0.5)");
		for (k=0;k<=4;k++) {
			angl=PIO2*k/4.0;
			p[0]=2.0*Math.cos(angl);
			p[1]=2.0*Math.sin(angl);
			p[2]=0.0;
			System.out.println("Starting vector: "+
				p[0]+" "+p[1]+" "+p[2]);

			
		    for (int i=0;i<NDIM;i++)
			   for (int j=0;j<NDIM;j++)
				  xi[i][j]=(i == j ? 1.0 : 0.0);

			PowellSolver powellSolver = new PowellSolver();
			fret = powellSolver.powell(NDIM,p,xi,FTOL,func);
		
			System.out.println("Solution vector: "+p[0]+" "+p[1]+" "+p[2]);
			
			System.out.println("Func. value at solution "+fret);
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	
}
}
