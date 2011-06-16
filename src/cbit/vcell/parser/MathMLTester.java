/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;

import java.util.Random;

public class MathMLTester {
	
	
public static void main(java.lang.String[] args) {
	try {
		int depthOfExpressionTree = 2;
		Random random = new Random(0);
		Expression[] expressions = getRandomExpressions(random,depthOfExpressionTree);
//		Expression[] expressions = new Expression[] {
//				new Expression("(acsch(0.08597493855078953) || acos(0.0879514303003861))")
//		};
		testMathML(expressions);
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
}
public static Expression[] getRandomExpressions(Random random, int depthOfExpressionTree) throws ExpressionException{
	int numTests = 2000;
	Expression[] expressions = new Expression[numTests];
	for (int i = 0; i < numTests; i++) {
		expressions[i] = cbit.vcell.parser.ExpressionUtils.generateExpression(random, depthOfExpressionTree, false);
	}
	return expressions;
}

public static void testMathML(Expression[] expressions) {
	int numPassed = 0;
	int numFailed = 0;
	for (int i = 0; i < expressions.length; i++) {
		try {
			System.out.println(i);
			cbit.vcell.parser.Expression exp = expressions[i];
//				exp = new cbit.vcell.parser.Expression("(a == 2*a)*log(-1)");
//				exp = new cbit.vcell.parser.Expression("(acos(sech((0.7072305309341316 * 0.6202963370938354 * id_5))) *  - asinh(log(id_0)) * sqrt(((id_0 ^ 0.4595156436219041) * floor(id_2) * (0.12558289998640915 < 0.0))))");
			if ( (exp.infix().indexOf("atan") >= 0) ) {
				continue;
			}
			String expMathMLStr = cbit.vcell.parser.ExpressionMathMLPrinter.getMathML(exp, true);
//				System.out.println(i+". " + expMathMLStr);
			cbit.vcell.parser.Expression newExp = null;
			cbit.vcell.parser.Expression tempExp = cbit.vcell.parser.Expression.mult(new cbit.vcell.parser.Expression(0.5), exp);
			cbit.vcell.parser.Expression exp1 = cbit.vcell.parser.Expression.add(tempExp, tempExp);
//				System.out.println("Orig Expression : " + newExp.infix());
			int equiv1 = -1;
			int equiv2 = -1;
			int equiv3 = -1;
			String errMsg1 = "";
			String errMsg2 = "";
			String errMsg3 = "";
			try {
				boolean equivalent1 = cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(exp, exp1, false);
				equiv1 = (equivalent1) ? 1 : 0;
			} catch (Exception e) {
				errMsg1 = "equivalency  FAILED : " + e.getMessage();
			}
			try {
				boolean equivalent2 = cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(exp, exp1.flatten(), false);
				equiv2 = (equivalent2) ? 1 : 0;
			} catch (Exception e) {
				errMsg2 = "equivalency  FAILED : " + e.getMessage();
			}
			try {
				cbit.vcell.parser.ExpressionMathMLParser expMathParser = new cbit.vcell.parser.ExpressionMathMLParser(null);
				newExp = expMathParser.fromMathML(expMathMLStr);
				boolean equivalent3 = cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(exp, newExp, false);
				equiv3 = (equivalent3) ? 1 : 0;
			} catch (Exception e) {
				errMsg3 = "equivalency  FAILED : " + e.getMessage();
			}
			String errMsgTrim1 = errMsg1.substring(0, Math.min(80, errMsg1.length()));
			String errMsgTrim2 = errMsg2.substring(0, Math.min(80, errMsg2.length()));
			String errMsgTrim3 = errMsg3.substring(0, Math.min(80, errMsg3.length()));
			if ( (equiv1 != equiv2) || (equiv2 != equiv3) || !errMsgTrim1.equals(errMsgTrim2) || !errMsgTrim2.equals(errMsgTrim3)) {
				System.out.println("EXP = " + ((exp!=null)?(exp.infix()):("null")));
				exp.printTree();
				System.out.println("EXP1 = " + ((exp1!=null)?(exp1.infix()):("null")));
				exp1.printTree();
				System.out.println("EXP1.FLATTEN = " + ((exp1!=null)?(exp1.flatten().infix()):("null")));
				exp1.flatten().printTree();
				System.out.println("NEWEXP = " + ((newExp!=null)?(newExp.infix()):("null")));
				if (newExp != null) {
					newExp.printTree();
				}
				System.out.println("equiv1 " + equiv1 + ";\tequiv2 " + equiv2 + ";\tequiv3 " + equiv3);
				System.out.println("EM_1 " + errMsg1);
				System.out.println("EM_2 " + errMsg2);
				System.out.println("EM_3 " + errMsg3);
				System.out.println("Orig Expression : " + exp.infix());
				System.out.println(expMathMLStr + "\n\n");
				numFailed++;
			} else {
				numPassed++;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	System.out.println("PASSED = " + numPassed + ";\tFAILED = " + numFailed);
}
}

