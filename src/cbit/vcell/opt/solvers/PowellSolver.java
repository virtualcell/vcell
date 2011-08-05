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

import cbit.function.*;
/**
 * Insert the type's description here.
 *
 *  Old version of Powell's method; newer version is PowellSolver
 *
 * Creation date: (5/1/2002 1:26:41 PM)
 * @author: Michael Duff
 */
public class PowellSolver {
	/**
		See Powell's method as defined in Numerical Recipies in C/Fortran for an explanation of the variable names, etc. : 
		Chapter 10 : Section 10.5 Powell's Method in Multidimensions.
	**/
	
	private double[] pt  = null; 	// Point in parameter space
	private double[] ptt = null;	// Extrapolated point in parameter space
	private double[] xit = null;	// Search direction vector
/**
 * ConjGrad constructor comment.
 */
public PowellSolver() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2002 11:41:43 AM)
 * @return double
 * @param n int
 * @param p double[]
 * @param ftol double
 * @param f function.ScalarFunction
 * @param df function.VectorFunction
 */
public double powell(int n, double[] p, double[][] xi, double ftol, ScalarFunction f) 
{
	//
	// Arguments : 
	// p[]    : parameter vector
	// xi[][] : search directions
	// 
	final int ITMAX = 200;
	int i, ibig, j, iter;
	double del, fp, fptt, t, fret;

	if (pt == null || pt.length != n || ptt == null || ptt.length != n || xit == null || xit.length != n) {
		pt = new double[n];
		ptt = new double[n];
		xit = new double[n];
	}
	
	fret = f.f(p); // Function value at initial point

	for (j = 0 ; j < n; j++) {
		pt[j] = p[j];
	}
	
	for (iter = 1; ; ++iter) {
		fp = fret;   		// f.f(p); // Can we use fret here ??
		ibig = 0;
		del = 0.0;
		for (i = 0; i < n;i++) {  					// Loop over all directions in the direction set
			for (j = 0; j < n;j++) {   				// Copy direction
				xit[j] = xi[j][i];
			}
			fptt = fret;	// f.f(p); // Can we use fret here ??
			fret = OptUtils.linmin(p, xit, f);  	//    Minimize along the direction
			if ( fptt-fret > del) {
				del = fptt - fret;					//    Largest function decrease so far
				ibig = i;
			}
		}
		
		if (2.0*(fp-fret) <= ftol*(Math.abs(fp) + Math.abs(fret))) { // Termination criterion
			return fret;
		}
		if (iter == ITMAX) {
			System.out.println("powell exceeding maximum iterations.");
			return fret;
		}
		
		for (j = 0; j < n; j++) {  				// Construct extrapolated point and the average direction moved.
			ptt[j] = 2.0*p[j] - pt[j];			// 
			xit[j] = p[j] - pt[j];				// Direction we just moved
			pt[j] = p[j];						// Save the old starting point.
		}
		
		fptt = f.f(ptt);						//  Compute function value at extrapolated point.
		
		if (fptt < fp) {
			t = 2.0*(fp - 2.0*fret + fptt)*(fp-fret-del)*(fp-fret-del) - del*(fp-fptt)*(fp-fptt);
			if (t < 0.0) {
				fret = OptUtils.linmin(p, xit, f);  //  Move to minimum along the new direction.
				
				for (j = 0; j < n; j++) {			//  Save the new direction.
					xi[j][ibig] = xi[j][n-1];
					xi[j][n-1] = xit[j];
				}
			}
		}
	} //Next iteration
}
}
