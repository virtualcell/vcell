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
import org.jmathml.ASTNode;
import org.jmathml.MathMLReader;
import org.junit.jupiter.api.Tag;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.test.Fast;

import java.io.IOException;
import java.util.*;

@Category(Fast.class)
@RunWith(Parameterized.class)
@Tag("Fast")
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
				exp.evaluateConstantSafe();
			}catch (FunctionDomainException | DivideByZeroException e2) {
				continue;
			}catch (ExpressionException e){
			}
			expressions.add(exp);
		}
		expressions.add(new Expression("-((-x)^2)"));
		expressions.add(new Expression("-(-x^2)"));
		expressions.add(new Expression("((Kf_dimerization * pow(EGFR_active,2.0)) - (Kr_dimerization * EGFR_dimer))"));
		expressions.add(new Expression("((pow(( - 17.0 + x),2.0) + pow(( - 2.0 + y),2.0)) < (225.0 * (y > 2.0)))"));
		return expressions;
	}

	@Test
	public void testMathML_SBMLSubset() throws IOException, ExpressionException {
		if (expression.infix().contains("atan")){
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
	public void test_vcell_mathml_jMathML_mathml_vcell() throws IOException, ExpressionException {
		List<String> tokensToAvoid = Arrays.asList(
				"atan",
				"sech", "csch", "coth",
				"acosh", "acot", "acoth",
				"acsc", "acsch", "asec",
				"asech", "asinh", "atanh",
				"min", "max"
		);
		String infix = expression.infix();
		boolean skipUnsupported = tokensToAvoid.stream().anyMatch(infix::contains);
		if (skipUnsupported) {
			return;
		}
		ExpressionMathMLParser expressionMathMLParser = new ExpressionMathMLParser(null);
		MathMLReader mathMLReader = new MathMLReader();

		Expression origExp = expression;
		String vcell_MathMLStr = ExpressionMathMLPrinter.getMathML(origExp, true,
				ExpressionMathMLPrinter.MathType.REAL, ExpressionMathMLPrinter.Dialect.SBML_SUBSET);
		Expression expMathML_roundTrip = new ExpressionMathMLParser(null).fromMathML(vcell_MathMLStr, "t");
		ASTNode astNode = mathMLReader.parseMathMLFromString(vcell_MathMLStr);
		Expression expMathML = expressionMathMLParser.fromMathML(astNode,"t");

		boolean equiv = ExpressionUtils.functionallyEquivalent(origExp, expMathML, true);
		String msg = "not equivalent: origExp='"+origExp.infix()+"', expMathML='"+expMathML.infix()+"'";
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
		List<String> tokensToAvoid = Arrays.asList(
				"<",">","=","&","|","!"
		);
		boolean skipUnsupported = tokensToAvoid.stream().anyMatch(expression.infix()::contains);
		if (skipUnsupported) {
			return;
		}

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
			expSimplified = expression.simplifyJSCL(20000, true);
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

