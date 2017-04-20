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
import java.util.Vector;

import org.vcell.optimization.OptSolverCallbacks;

import cbit.function.DoubleHolder;
import cbit.function.DynamicScalarFunction;
import cbit.function.DynamicVectorFunction;
import cbit.function.ScalarFunction;
import cbit.vcell.opt.Constraint;
import cbit.vcell.opt.ConstraintType;
import cbit.vcell.opt.ExplicitObjectiveFunction;
import cbit.vcell.opt.ImplicitObjectiveFunction;
import cbit.vcell.opt.OdeObjectiveFunction;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.Expression;

/**
 * Insert the type's description here.
 * Creation date: (5/2/2002 11:34:27 AM)
 * @author: Michael Duff
 */
public final class OptUtils {

	public static class F1DClass {
		public ScalarFunction scalarFunc;
		public double[] point;
		public double[] direction;

		public F1DClass(ScalarFunction argScalarFn, double[] argPoint, double[] argDirection) {
			super();
			if (argPoint.length != argDirection.length) {
				throw new RuntimeException("Point and direction should be of same length");
			}
			point  = argPoint;
			direction = argDirection;
			scalarFunc  = argScalarFn;
			
		}

		public double f1DStep(double x) {
			double[] xt = new double[point.length];
			for(int j=0;j<point.length;j++){
				xt[j] = point[j]  +  x * direction[j];
			}
			return scalarFunc.f(xt);
		}
	}	

/**
 * Insert the method's description here.
 * Creation date: (5/1/2002 10:52:05 AM)
 */
public static void bracket(double[] xbrack, double[] fbrack, F1DClass f)
{
	
final double GOLD   = 1.618034;
final double GLIMIT = 100.0;			 // max magnification allowed for parabolic fit step in bracket
final double TINY   = 1.0e-20;

double ulim,u,r,q,fu,dum;
double temp;

	fbrack[0] = f.f1DStep(xbrack[0]);
	fbrack[1] = f.f1DStep(xbrack[1]);
	if (fbrack[1] > fbrack[0]) {
		dum       = xbrack[0];  // swap xbrack[0] and xbrack[1]
		xbrack[0] = xbrack[1];
		xbrack[1] = dum;
		dum       = fbrack[0];  // swap fbrack[0] and fbrack[1]
		fbrack[0] = fbrack[1];
		fbrack[1] = dum;
		
	}
	
	xbrack[2] = (xbrack[1]) + GOLD*(xbrack[1]-xbrack[0]);
	fbrack[2]= f.f1DStep(xbrack[2]);
	
	while (fbrack[1] > fbrack[2]) {
		r=(xbrack[1]-xbrack[0])*(fbrack[1]-fbrack[2]);
		q=(xbrack[1]-xbrack[2])*(fbrack[1]-fbrack[0]);
		if (q-r > 0){
			temp = Math.abs(Math.max(Math.abs(q-r),TINY));
		}
		else{
			temp = -Math.abs(Math.max(Math.abs(q-r),TINY));
		}
		u=(xbrack[1])-((xbrack[1]-xbrack[2])*q-(xbrack[1]-xbrack[0])*r)/
			(2.0*temp);
		ulim=(xbrack[1])+GLIMIT*(xbrack[2]-xbrack[1]);
		if ((xbrack[1]-u)*(u-xbrack[2]) > 0.0) {
			fu= f.f1DStep(u);
			if (fu < fbrack[2]) {
				xbrack[0]=(xbrack[1]);
				xbrack[1]=u;
				fbrack[0]=(fbrack[1]);
				fbrack[1]=fu;
				return;
			} else if (fu > fbrack[1]) {
				xbrack[2]=u;
				fbrack[2]=fu;
				return;
			}
			u=(xbrack[2])+GOLD*(xbrack[2]-xbrack[1]);
			fu= f.f1DStep(u);
		} else if ((xbrack[2]-u)*(u-ulim) > 0.0) {
			fu= f.f1DStep(u);
			if (fu < fbrack[2]) {
				xbrack[1] = xbrack[2];
				xbrack[2] = u;
				u         = xbrack[2]+GOLD*(xbrack[2]-xbrack[1]);
				fbrack[1] = fbrack[2];
				fbrack[2] = fu;
				f.f1DStep(u);
			}
		} else if ((u-ulim)*(ulim-xbrack[2]) >= 0.0) {
			u=ulim;
			fu= f.f1DStep(u);
		} else {
			u=(xbrack[2])+GOLD*(xbrack[2]-xbrack[1]);
			fu= f.f1DStep(u);
		}
		xbrack[0] = xbrack[1];
		xbrack[1] = xbrack[2];
		xbrack[2] = u;
		fbrack[0] = fbrack[1];
		fbrack[1] = fbrack[2];
		fbrack[2] = fu;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/2/2002 5:32:06 PM)
 * @return double
 * @param ax double
 * @param bx double
 * @param cx double
 * @param f opt.F1DClass
 * @param tol double
 * @parma xmin DoubleWrap
 */
public static double brent(double ax, double bx, double cx, F1DClass f, double tol, DoubleHolder xmin) {
	

double ITMAX = 25;  //100;
double CGOLD = 0.3819660;
double ZEPS  = 1.0e-10;


	int iter;
	double a,b,etemp,fu,fv,fw,fx,p,q,r,tol1,tol2,u,v,w,x,xm;
	double e=0.0;
	double d=0.0;  //Added this initialization

	double temp;


	a=(ax < cx ? ax : cx);
	b=(ax > cx ? ax : cx);
	x=w=v=bx;
	fw=fv=fx= f.f1DStep(x);
	for (iter=1;iter<=ITMAX;iter++) {
		xm=0.5*(a+b);
		tol2=2.0*(tol1=tol*Math.abs(x)+ZEPS);
		if (Math.abs(x-xm) <= (tol2-0.5*(b-a))) {
			xmin.setval(x);
			System.out.println("---> "+iter+" iterations in brent");
			return fx;
		}
		if (Math.abs(e) > tol1) {
			r=(x-w)*(fx-fv);
			q=(x-v)*(fx-fw);
			p=(x-v)*q-(x-w)*r;
			q=2.0*(q-r);
			if (q > 0.0) p = -p;
			q=Math.abs(q);
			etemp=e;
			e=d;
			if (Math.abs(p) >= Math.abs(0.5*q*etemp) || p <= q*(a-x) || p >= q*(b-x))
				d=CGOLD*(e=(x >= xm ? a-x : b-x));
			else {
				d=p/q;
				u=x+d;
				if (u-a < tol2 || b-u < tol2){
					if(xm-x > 0){
						temp= Math.abs(b-u);
					}
					else{
						temp= -Math.abs(b-u);
					}
					
					d=temp;
				}
			}
		} else {
			d=CGOLD*(e=(x >= xm ? a-x : b-x));
		}

		if(d>0){
			temp= Math.abs(tol1);
		}
		else{
			temp= -Math.abs(tol1);
		}
		
		u=(Math.abs(d) >= tol1 ? x+d : x+temp);
		fu=f.f1DStep(u);                        // Function evaluation
		if (fu <= fx) {
			if (u >= x) a=x; else b=x;
			v=w;
			w=x;
			x=u;
			fv=fw;
			fw=fx;
			fx=fu;
		} else {
			if (u < x) a=u; else b=u;
			if (fu <= fw || w == x) {
				v=w;
				w=u;
				fv=fw;
				fw=fu;
			} else if (fu <= fv || v == x || v == w) {
				v=u;
				fv=fu;
			}
		}
	}
	System.out.println("---> "+iter+"(MAX) iterations in brent");
	xmin.setval(x);
	return fx;
		
}


/**
 * Insert the method's description here.
 * Creation date: (5/2/2002 11:24:01 AM)
 * @return double
 * @param x double
 */
public static double f1DStep(ScalarFunction f, double[] p, double[] xi, double x) {
	final int n = f.getNumArgs();
	double[] xt = new double[n];
	for(int j=0;j<n;j++){
		xt[j] = p[j]  +  x * xi[j];
	}
	return f.f(xt);
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/2005 3:26:14 PM)
 * @return function.AugmentedObjectiveFunction
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @param power double
 * @param mu double
 */
public static AugmentedObjectiveFunction getAugmentedObjectiveFunction(OptimizationSpec optSpec, double power, double mu, OptSolverCallbacks optSolverCallbacks) throws cbit.vcell.parser.ExpressionException {
	
	Parameter[] parameters = optSpec.getParameters();

	//
	// build symbol list
	//
	String origSymbols[] = optSpec.getParameterNames();
	String scaledSymbols[] = optSpec.getScaledParameterNames();
	double scaleFactors[] = optSpec.getScaleFactors();

	ScalarFunction objFunction = null;
	
	if (optSpec.getObjectiveFunction() instanceof ExplicitObjectiveFunction){
		ExplicitObjectiveFunction explicitObjectiveFunction = (ExplicitObjectiveFunction)optSpec.getObjectiveFunction();
		//
		// build objective function
		//
		objFunction = new DynamicScalarFunction(explicitObjectiveFunction.getScaledExpression(origSymbols,scaledSymbols,scaleFactors),scaledSymbols);
	} else if (optSpec.getObjectiveFunction() instanceof OdeObjectiveFunction){
		OdeObjectiveFunction odeObjectiveFunction = (OdeObjectiveFunction)optSpec.getObjectiveFunction();
		OdeLSFunction odeLSFunction = new OdeLSFunction(odeObjectiveFunction,origSymbols,scaleFactors,optSolverCallbacks);
		objFunction = odeLSFunction;
	}else if (optSpec.getObjectiveFunction() instanceof ImplicitObjectiveFunction){
		ImplicitObjectiveFunction implicitObjectiveFunction = (ImplicitObjectiveFunction)optSpec.getObjectiveFunction();
		//
		// build objective function
		//
		objFunction = implicitObjectiveFunction.getObjectiveFunction();
	} else{
		throw new RuntimeException("unsupported objective function type : "+optSpec.getObjectiveFunction().getClass().getName());
	}
	//
	// build equality and inequality constraints
	//
	DynamicVectorFunction equalityConstraints = null;
	DynamicVectorFunction inequalityConstraints = null;
	
	Constraint constraints[] = optSpec.getConstraints();
	Vector equExpList = new Vector();
	Vector inequExpList = new Vector();
	//
	// add constraints from OptimizationSpec's linear and nonlinear general constraints
	//
	for (int i = 0; i < constraints.length; i++){
		ConstraintType type = constraints[i].getConstraintType();
		if (type.equals(ConstraintType.LinearEquality) || type.equals(ConstraintType.NonlinearEquality)){
			equExpList.add(constraints[i].getExpression());
		}else{
			inequExpList.add(constraints[i].getExpression());
		}
	}
	//
	// add constraints from OptimizationSpec's variable bounds
	//
	for (int i = 0; i < parameters.length; i++){
		if (!Double.isInfinite(parameters[i].getLowerBound())){
			inequExpList.add(new Expression("("+parameters[i].getLowerBound()+" - "+parameters[i].getName()+")/"+scaleFactors[i]));
		}
		if (!Double.isInfinite(parameters[i].getUpperBound())){
			inequExpList.add(new Expression("("+parameters[i].getName()+" - "+parameters[i].getUpperBound()+")/"+scaleFactors[i]));
		}			
	}
	if (equExpList.size()>0){
		Expression exps[] = (Expression[])org.vcell.util.BeanUtils.getArray(equExpList,Expression.class);
		equalityConstraints = new DynamicVectorFunction(exps,origSymbols);
	}
	if (inequExpList.size()>0){
		Expression exps[] = (Expression[])org.vcell.util.BeanUtils.getArray(inequExpList,Expression.class);
		inequalityConstraints = new DynamicVectorFunction(exps,origSymbols);
	}
	
	AugmentedObjectiveFunction augmentedObjFunc = new AugmentedObjectiveFunction(objFunction,equalityConstraints,inequalityConstraints,power,mu,optSolverCallbacks);
		
	return augmentedObjFunc;
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2002 2:29:11 PM)
 * @return double
 */
public static double linmin(double[] p, double[] xi, ScalarFunction f)
{

	final double TOL = 2.0e-4;		// linMin parameter (stopping criterion for brent)
	int j;
	double xx, fx, fb, fa, bx, ax;
	double fret;
	double[] xbrack = new double[3];
	double[] fbrack = new double[3];
	DoubleHolder xmin = new DoubleHolder();

	F1DClass f1dim = new F1DClass(f, p, xi);

	xbrack[0]=0;
	xbrack[1]=1.0;
	bracket(xbrack, fbrack, f1dim);
	fret = brent(xbrack[0], xbrack[1], xbrack[2], f1dim, TOL, xmin);
	for (j = 0; j < p.length; j++) {
		xi[j] *= xmin.getval();
		p[j] += xi[j];
	}

    System.out.println("======================LINMIN   fret="+fret);
	
	return fret;
}
	
}
