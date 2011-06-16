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

import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (4/2/2002 11:43:41 AM)
 * @author: Michael Duff
 */
public class DefaultVectorFunctionTest {
/**
 * TestJacobiani constructor comment.
 */
public DefaultVectorFunctionTest() {
	super();
}
	public static void main(String[] argv)
	{
		try {
			//DefaultVectorFunction vecFunction = new MyVectorFunction();
			
			Expression exps[] = new Expression[2];
			exps[0] = new Expression("2*x0+3*x0*x1+4");
			exps[1] = new Expression("5*x0+6*x0*x1+7*x1+8");
			String identifiers[] = new String[2];
			identifiers[0] = "x0";
			identifiers[1] = "x1";
			DefaultVectorFunction vecFunction = new DynamicVectorFunction(exps,identifiers);

			double[] xx = new double[2];
			double[][] jac = new double[2][2];

			for(xx[0]=1;xx[0]<=3;xx[0]++){
				for(xx[1]=1;xx[1]<=3;xx[1]++){

					System.out.println("Norm2()="+vecFunction.Norm2(xx));
					
					System.out.println("evaluateJacobian()");
					jac=vecFunction.evaluateJacobian(xx);
					System.out.println("Jac="+jac[0][0]+" "+jac[0][1]);
					System.out.println("Jac="+jac[1][0]+" "+jac[1][1]);
					System.out.println("centralDifference()");
					jac=vecFunction.evaluateJacobianCentralDifference(xx);
					System.out.println("Jac="+jac[0][0]+" "+jac[0][1]);
					System.out.println("Jac="+jac[1][0]+" "+jac[1][1]);
					System.out.println();
				}
			}
		}catch (Throwable e){
			System.out.println("Uncaught exception in JacobianTest.main()");
			e.printStackTrace(System.out);
		}
	}
}
