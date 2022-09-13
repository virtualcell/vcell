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

import jscl.text.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.*;

@RunWith(Parameterized.class)
public class MathMLTester {

	private Expression expression;

	public MathMLTester(Expression exp) {
		this.expression = exp;
	}

	@Parameterized.Parameters
	public static Collection<Expression> testCases() throws ExpressionException {
		int depthOfExpressionTree = 2;
		int numTests = 1500;
		Random random = new Random(0);
		List<String> tokensToAvoid = Arrays.asList(
				"<",">","=","&","|","!"
//				,"min","max"
			);
		ArrayList<Expression> expressions = new ArrayList<>();
		for (int i = 0; i < numTests; i++) {
			Expression exp = ExpressionUtils.generateExpression(random, depthOfExpressionTree, false);
			String infix = exp.infix();
			boolean skipUnsupported = tokensToAvoid.stream().anyMatch(infix::contains);
			if (skipUnsupported) {
				continue;
			}
			if (exp.infix_JSCL().length()>120){
				continue;
			}
			try {
				ExpressionUtils.functionallyEquivalent(exp,exp);
			}catch (Exception e){
				continue;
			}
			try {
				exp.evaluateConstant();
			}catch (FunctionDomainException fde) {
				continue;
			}catch (ExpressionException e){
			}
			expressions.add(exp);
		}
		expressions.add(new Expression("-((-x)^2)"));
		expressions.add(new Expression("-(-x^2)"));
		expressions.add(new Expression("((Kf_dimerization * pow(EGFR_active,2.0)) - (Kr_dimerization * EGFR_dimer))"));
		return expressions;
	}

	@Test
	public void testMathML_SBMLSubset() throws IOException, ExpressionException {
		if (expression.infix().contains("atan")
				|| expression.infix().contains("min")
				|| expression.infix().contains("max")){
			return;
		}
		String expMathMLStr = ExpressionMathMLPrinter.getMathML(expression, true,
				ExpressionMathMLPrinter.MathType.REAL, ExpressionMathMLPrinter.Dialect.SBML_SUBSET);
		Expression expMathML = new ExpressionMathMLParser(null).fromMathML(expMathMLStr, "t");
		boolean equiv = ExpressionUtils.functionallyEquivalent(expression, expMathML, true);
		String msg = "not equivalent: origExp='"+expression.infix()+"', expMathML='"+expMathML.infix()+"'";
		Assert.assertTrue(msg, equiv);
	}

	@Test
	public void testMathML_GeneralSubset() throws IOException, ExpressionException {
		if (expression.infix().contains("atan")){
			return;
		}
		String expMathMLStr = ExpressionMathMLPrinter.getMathML(expression, true,
				ExpressionMathMLPrinter.MathType.REAL, ExpressionMathMLPrinter.Dialect.GENERAL);
		Expression expMathML = new ExpressionMathMLParser(null).fromMathML(expMathMLStr, "t");
		boolean equiv = ExpressionUtils.functionallyEquivalent(expression, expMathML, true);
		String msg = "not equivalent: origExp='"+expression.infix()+"', expMathML='"+expMathML.infix()+"'";
		Assert.assertTrue(msg, equiv);
	}

	@Test
	public void testFlatten() throws ExpressionException {
		Expression expFlattened = expression.flatten();
		Assert.assertTrue("not equivalent: origExp='"+expression.infix()+"', expFlattened='"+expFlattened.infix()+"'",
				ExpressionUtils.functionallyEquivalent(expression, expFlattened, false));
	}

	@Test
	public void testJSCLParseInfix() throws ExpressionException, ParseException {
		jscl.math.Expression jsclExp = jscl.math.Expression.valueOf(expression.infix_JSCL());
		String jsclExpInfix = jsclExp.toString();
		Expression roundTripExp = new Expression(jsclExpInfix);

		// JSCL mangles underscores ... have to undo that
		String[] jsclSymbols = roundTripExp.getSymbols();
		for (int i = 0;jsclSymbols!=null && i < jsclSymbols.length; i++){
			String restoredSymbol = cbit.vcell.parser.SymbolUtils.getRestoredStringJSCL(jsclSymbols[i]);
			if (!restoredSymbol.equals(jsclSymbols[i])){
				roundTripExp.substituteInPlace(new cbit.vcell.parser.Expression(jsclSymbols[i]),new cbit.vcell.parser.Expression(restoredSymbol));
			}
		}
		String msg = "not equivalent: origExp='"+expression.infix()+"', expSimplified='"+roundTripExp.infix()+"', jsclInfix='"+jsclExpInfix+"'";
		boolean equiv = ExpressionUtils.functionallyEquivalent(expression, roundTripExp, false);
		Assert.assertTrue(msg, equiv);
	}

	@Test
	public void testJSCLSimplifyInfix() {
		Expression expSimplified = null;
		try {
			expSimplified = ExpressionUtils.simplifyUsingJSCL(expression, 200);
		} catch (ExpressionException e) {
			String msg = "failed to simplify '"+expression.infix()+"': "+e.getMessage();
			Assert.fail(msg);
			return;
		}
		if (expSimplified.infix().contains("pi")){
			return;
		}
		String msg = "not equivalent: origExp='"+expression.infix()+"', expSimplified='"+expSimplified.infix()+"'";
		boolean equiv = ExpressionUtils.functionallyEquivalent(expression, expSimplified, false);
		Assert.assertTrue(msg, equiv);
	}

}

