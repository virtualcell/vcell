package cbit.vcell.opt.solvers;
import cbit.function.*;
/**
 * Insert the type's description here.
 * Creation date: (5/1/2002 1:26:41 PM)
 * @author: Michael Duff
 */
public class ConjGradSolver {
	private double EPS;				// Small number to rectify special case of converging to exactly zero function value.
	private int ITMAX;				// max number iterations 

	private double[] g = null;		// 
	private double[] h = null;		// 
	private double[] xi = null;		// Gradient

/**
 * ConjGrad constructor comment.
 */
public ConjGradSolver() {
	super();
	ITMAX = 200;    //max number of iterations in conjgrad
	EPS   = 1.0e-10;
}


/**
 * Insert the method's description here.
 * Creation date: (8/14/2002 10:38:49 AM)
 * @param argFtol double
 */
public ConjGradSolver(int argITMAX, double argEPS) {
	ITMAX = argITMAX;	// max number of iterations in conjgrad
	EPS   = argEPS;		// Small number to rectify special case of converging to exactly zero function value
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
public double conjGrad(double[] p, double ftol, ScalarFunction f) 
{
	int j,its;
	double gg, gam, fp, dgg;
	int n = p.length;
	double fret = 0;

	if (g == null || g.length != n || h == null || h.length != n || xi == null || xi.length != n) {
		g = new double[n];
		h = new double[n];
		xi = new double[n];
	}

	fp = f.f(p);
	xi = f.evaluateGradient(p);
	for (j = 0; j < n; j++) {     
		g[j] = -xi[j];
		xi[j] = h[j] = g[j];
	}
	for (its = 1; its <= ITMAX; its++) {
		fret = OptUtils.linmin(p, xi, f);
		
		// Normal termination	
		if (2.0*Math.abs(fret-fp) <= ftol*(Math.abs(fret) + Math.abs(fp) + EPS)){
			return fret;
		}
		fp = fret;
		xi = f.evaluateGradient(p);
		dgg = gg = 0.0;
		for (j = 0; j < n; j++) {
			gg += g[j]*g[j];
			dgg += (xi[j] + g[j]) * xi[j];	// Polak - Rebiere method
		}
		if (gg == 0.0) {
			return fret;
		}
		gam = dgg/gg;
		for (j = 0; j < n; j++) {
			g[j] = -xi[j];
			xi[j] = h[j] = g[j] + gam*h[j];
		}
	}
	System.out.println("*********ConjGrad Error: Too many iterations");
	return fret;
}
}