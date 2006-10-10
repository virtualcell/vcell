package cbit.vcell.opt.solvers;
import org.vcell.expression.ExpressionFactory;

import cbit.function.*;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2002 9:15:33 AM)
 * @author: Michael Duff
 */
public class ConjGradTest {
/**
 * ConjGradTest constructor comment.
 */
public ConjGradTest() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 9:15:57 AM)
 * @param args java.lang.String[]
 */






public static void main(String[] args) 
{
try {
	final int NDIM = 3;

	final double PIO2 = 1.5707963;

		
		int iter,k;
		double angl,fret;

		double[] p= new double[NDIM];

		//ScalarFunction func = new MyObjectiveFunction();
		ScalarFunction func = new DynamicScalarFunction(ExpressionFactory.createExpression("x+y"),new String[]{"x","y"});

		ConjGradSolver cg = new ConjGradSolver();

		
		System.out.println("Program finds the minimum of a function");
		System.out.println("with different trial starting vectors.");
		System.out.println("True minimum is (0.5,0.5,0.5)");
		for (k=0;k<=4;k++) {
			angl=PIO2*k/4.0;
			p[0]=2.0*Math.cos(angl);
			p[1]=2.0*Math.sin(angl);
			p[2]=0.0;
			System.out.println("\n");
			System.out.println("Starting vector: "+
				p[0]+" "+p[1]+" "+p[2]);
			fret = cg.conjGrad(p, 1.0e-6, func);
		
			System.out.println("Solution vector: "+p[0]+" "+p[1]+" "+p[2]);
			
			System.out.println("Func. value at solution "+fret);
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}	
}
		
	
}