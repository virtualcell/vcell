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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.*;

import static cbit.vcell.parser.ExpressionUtils.functionallyEquivalent;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("Fast")
public class MathMLTest {

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
	public void testMathMLParsing_XOR() throws IOException, ExpressionException {
		// XOR is supported as an SBML operator for importing only
		Expression exp = new Expression("(x && !y) || (!x && y)");
		String temp_mathMLStr_with_and = ExpressionMathMLPrinter.getMathML(new Expression("x && y"), true,
				ExpressionMathMLPrinter.MathType.REAL, ExpressionMathMLPrinter.Dialect.SBML_SUBSET);
		String mathMLStr_with_xor = temp_mathMLStr_with_and.replace("and", "xor");
		Expression xor_from_MathML = new ExpressionMathMLParser(null).fromMathML(mathMLStr_with_xor, "t");
		boolean equiv = ExpressionUtils.functionallyEquivalent(exp, xor_from_MathML, true);
		String msg = "not equivalent: origExp='"+exp.infix()+"', expMathML='"+xor_from_MathML.infix()+"'";
		assertTrue(equiv, msg);
	}

	@Test
	public void testNaN_MathmlParsing() throws ExpressionException {
		String nanMathML = "<notanumber/>";
		Expression exp = new ExpressionMathMLParser(null).fromMathML(nanMathML, "t");
		Expression expectedExp = new Expression("0/0");
		boolean equiv = ExpressionUtils.functionallyEquivalent(exp, expectedExp, true);
		String msg = "not equivalent: origExp='"+exp.infix()+"', expMathML='"+expectedExp.infix()+"'";
		assertTrue(equiv, msg);
	}

	@Test
	public void testInfinity_MathmlParsing() throws ExpressionException {
		String nanMathML = "<infinity/>";
		Expression exp = new ExpressionMathMLParser(null).fromMathML(nanMathML, "t");
		Expression expectedExp = new Expression(Double.MAX_VALUE);
		boolean equiv = ExpressionUtils.functionallyEquivalent(exp, expectedExp, true);
		String msg = "not equivalent: origExp='"+exp.infix()+"', expMathML='"+expectedExp.infix()+"'";
		assertTrue(equiv, msg);
	}


	@ParameterizedTest
	@MethodSource("testCases")
	public void testMathML_SBMLSubset(Expression expression) throws IOException, ExpressionException {
		if (expression.infix().contains("atan")){
			return;
		}
		String expMathMLStr = ExpressionMathMLPrinter.getMathML(expression, true,
				ExpressionMathMLPrinter.MathType.REAL, ExpressionMathMLPrinter.Dialect.SBML_SUBSET);
		Expression expMathML = new ExpressionMathMLParser(null).fromMathML(expMathMLStr, "t");
		boolean equiv = ExpressionUtils.functionallyEquivalent(expression, expMathML, true);
		String msg = "not equivalent: origExp='"+expression.infix()+"', expMathML='"+expMathML.infix()+"'";
        assertTrue(equiv, msg);
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void test_vcell_mathml_jMathML_mathml_vcell(Expression expression) throws IOException, ExpressionException {
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
        assertTrue(equiv, msg);
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void testMathML_GeneralSubset(Expression expression) throws IOException, ExpressionException {
		if (expression.infix().contains("atan")){
			return;
		}
		String expMathMLStr = ExpressionMathMLPrinter.getMathML(expression, true,
				ExpressionMathMLPrinter.MathType.REAL, ExpressionMathMLPrinter.Dialect.GENERAL);
		Expression expMathML = new ExpressionMathMLParser(null).fromMathML(expMathMLStr, "t");
		boolean equiv = ExpressionUtils.functionallyEquivalent(expression, expMathML, true);
		String msg = "not equivalent: origExp='"+expression.infix()+"', expMathML='"+expMathML.infix()+"'";
        assertTrue(equiv, msg);
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void testFlatten(Expression expression) throws ExpressionException {
		Expression expFlattened = expression.flatten();
        assertTrue(functionallyEquivalent(expression, expFlattened, false), "not equivalent: origExp='" + expression.infix() + "', expFlattened='" + expFlattened.infix() + "'");
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void testJSCLParseInfix(Expression expression) throws ExpressionException, ParseException {
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
        assertTrue(equiv, msg);
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void testJSCLSimplifyInfix(Expression expression) {
		Expression expSimplified = null;
		try {
			expSimplified = expression.simplifyJSCL(20000, true);
		} catch (ExpressionException e) {
			String msg = "failed to simplify '"+expression.infix()+"': "+e.getMessage();
			fail(msg);
			return;
		}
		if (expSimplified.infix().contains("pi")){
			return;
		}
		String msg = "not equivalent: origExp='"+expression.infix()+"', expSimplified='"+expSimplified.infix()+"'";
		boolean equiv = ExpressionUtils.functionallyEquivalent(expression, expSimplified, false);
        assertTrue(equiv, msg);
	}

}

