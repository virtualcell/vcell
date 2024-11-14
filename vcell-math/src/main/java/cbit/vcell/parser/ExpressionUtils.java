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

import cbit.vcell.matrix.MatrixException;
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalExpMatrix;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;

import java.util.Random;
import java.util.Vector;
import java.util.function.BiPredicate;

/**
 * Insert the type's description here.
 * Creation date: (12/27/2002 1:37:29 PM)
 * @author: Jim Schaff
 */
public class ExpressionUtils {
	private static Logger lg = LogManager.getLogger(ExpressionUtils.class);
	public final static String legacy_molecules_per_uM_um3_NUMERATOR = "6.02e8";
	public final static String latest_molecules_per_uM_um3_NUMERATOR = "6.02214179E8";
	public static String value_molecules_per_uM_um3_NUMERATOR = latest_molecules_per_uM_um3_NUMERATOR;

	public static Expression getLinearFactor(Expression exp, String k) throws ExpressionException {
		// let exp = f(k,x)
		// if exp is linear in k then there exists a g(x) such that f(k,x) = k*g(x)
		// in order to preserve the number original form of exp (e.g. same number of discontinuities)
		// we will simply divide by k.  (divide by zero will be handled in functionallyEquivalent())
		//
		// (a)      exp(k==>1) = f(1,x) = 1*g(x) = g(x)
		// (b)      f(k,x)/k = k*g(x)/k = g(x)  (where k!=0)
		// (c)      f(k,x)/k == f(1,x)
		//
		// so exp is linear in k if:
		//
		// (e)      exp(k==>1) == exp / k
		//
		Expression kExp = new Expression(k);
		Expression exp_substitute_1 = exp.getSubstitutedExpression(kExp, new Expression(1.0));
		Expression exp_div_k = Expression.div(exp, kExp);
		if (functionallyEquivalent(exp_substitute_1, exp_div_k, false)){
			Expression factor = exp_substitute_1.simplifyJSCL();
			return factor;
		}else{
			return null;
		}
	}

	public static Expression getLinearFactor_no_division(Expression exp, String k) throws ExpressionException {
		// let exp = f(k,x)
		// if exp is linear in k then there exists a g(x) such that f(k,x) = k*g(x)
		// we would like a formula to identify g(x) which does not use division.
		//
		// (a)      exp(k==>1) = f(1,x) = g(x)
		// (b)      exp(k==>2) = f(2,x) = 2 * g(x)
		// (c)      exp(k==>2) - exp(k==>1) = f(2,x) - f(1,x) = 2 * g(x) - g(x) = g(x)
		//
		// then
		//
		// (d)      k * (f(2,x) - f(1,x)) = k * g(x) = f(k,x)
		//
		// so exp is linear in k if:
		//
		// (e)      exp == k * (exp(k==>2) - exp(k==>1))
		//
		Expression kExp = new Expression(k);
		Expression exp_substitute_2 = exp.getSubstitutedExpression(kExp, new Expression(2.0));
		Expression exp_substitute_1 = exp.getSubstitutedExpression(kExp, new Expression(1.0));
		Expression gExp = Expression.add(exp_substitute_2, Expression.negate(exp_substitute_1));
		Expression k_times_g = Expression.mult(kExp, gExp);
		if (functionallyEquivalent(exp, k_times_g, false)){
			Expression factor = exp_substitute_1.simplifyJSCL();
			return factor;
		}else{
			return null;
		}
	}

	public static class ExpressionEquivalencePredicate implements BiPredicate<Matchable,Matchable> {
		@Override
		public boolean test(Matchable matchable, Matchable matchable2) {
			if (matchable instanceof Expression && matchable2 instanceof Expression){
				return functionallyEquivalent((Expression)matchable, (Expression) matchable2);
			}
			return false;
		}
	}

private static SimpleNode createNode(java.util.Random random, boolean bIsConstraint) {
	final int AddNode = 0;
	final int AndNode = 1;
	final int FloatNode = 2;
	final int FuncNode = 3;
	final int IDNode = 4;
	final int InvertNode = 5;
	final int MinusNode = 6;
	final int MultNode = 7;
	final int NotNode = 8;
	final int OrNode = 9;
	final int PowerNode = 10;
	final int RelationalNode = 11;

	final double nodeProbabilityNormal[] = {
		0.5,	// ADD
		0.25,	// AND
		0.5,	// FLOAT
		10.0,	// FUNCTION
		0.2,	// ID
		1.0,	// INVERT
		1.0,	// MINUS
		2.0,	// MULT
		0.3,	// NOT
		0.3,	// OR
		1,		// POWER
		0.3,	// RELATIONAL
	};
	final double nodeProbabilityConstraint[] = {
		2.0,	// ADD
		0.0,	// AND
		2.0,	// FLOAT
		0.0,	// FUNCTION
		1.0,	// ID
		1.0,	// INVERT
		1.0,	// MINUS
		2.0,	// MULT
		0.0,	// NOT
		0.0,	// OR
		0.0,	// POWER
		0.0,	// RELATIONAL
	};
		
	final FunctionType[] functionIDs = {
		FunctionType.ABS,
		FunctionType.ACOS,
		FunctionType.ASIN,
		FunctionType.ATAN,
		FunctionType.ATAN2,
		FunctionType.COS,
		FunctionType.EXP,
		FunctionType.LOG,
		FunctionType.MAX,
		FunctionType.MIN,
		FunctionType.POW,
		FunctionType.SIN,
		FunctionType.SQRT,
		FunctionType.TAN,
		FunctionType.CEIL,
		FunctionType.FLOOR,
		FunctionType.CSC,
		FunctionType.COT,
		FunctionType.SEC,
		FunctionType.ACSC,
		FunctionType.ACOT,
		FunctionType.ASEC,
		FunctionType.SINH,
		FunctionType.COSH,
		FunctionType.TANH,
		FunctionType.CSCH,
		FunctionType.COTH,
		FunctionType.SECH,
		FunctionType.ASINH,
		FunctionType.ACOSH,
		FunctionType.ATANH,
		FunctionType.ACSCH,
		FunctionType.ACOTH,
		FunctionType.ASECH,
		FunctionType.FACTORIAL
	};
	double nodeProbability[] = null;
	if (bIsConstraint){
		nodeProbability = nodeProbabilityConstraint;
	}else{
		nodeProbability = nodeProbabilityNormal;
	}
	double totalProb = 0;
	for (int i = 0; i < nodeProbability.length; i++){
		totalProb += nodeProbability[i];
	}
	double f = random.nextDouble()*totalProb;
	int nodeChoice = -1;
	double cumulativeProb = 0;
	for (int i = 0; i < nodeProbability.length; i++){
		cumulativeProb += nodeProbability[i];
		if (f < cumulativeProb){
			nodeChoice = i;
			break;
		}
	}
	switch (nodeChoice){
		case AddNode: {
			return new ASTAddNode();
		}
		case AndNode: {
			return new ASTAndNode();
		}
		case FloatNode: {
			if (random.nextFloat() > 0.1){
				return new ASTFloatNode(random.nextDouble());
			}else{
				return new ASTFloatNode(0.0);
			}
		}
		case FuncNode: {
			ASTFuncNode fn = new ASTFuncNode();
			double ftype = random.nextDouble();
			int index = (int)Math.min(functionIDs.length-1,Math.floor(functionIDs.length*ftype));
			fn.setFunctionType(functionIDs[index]);
			return fn;
		}
		case IDNode: {
			ASTIdNode idNode = new ASTIdNode();
			idNode.name = "id_"+((int)Math.abs(random.nextInt())%10);
			return idNode;
		}
		case InvertNode: {
			return new ASTInvertTermNode();
		}
		case MinusNode: {
			return new ASTMinusTermNode();
		}
		case MultNode: {
			return new ASTMultNode();
		}
		case NotNode: {
			return new ASTNotNode();
		}
		case OrNode: {
			return new ASTOrNode();
		}
		case PowerNode: {
			return new ASTPowerNode();
		}
		case RelationalNode: {
			ASTRelationalNode relNode = new ASTRelationalNode();
			double rtype = random.nextDouble();
			final String ops[] = {
				">",
				"<",
				"<=",
				">=",
				"==",
				"!="
			};
			relNode.setOperationFromToken(ops[(int)Math.floor(rtype*6)]);
			return relNode;
		}
		default: {
			throw new RuntimeException("f = "+f+", couldn't create a node");
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 4:23:34 PM)
 * @return cbit.vcell.parser.SimpleNode
 */
private static SimpleNode createTerminalNode(java.util.Random random, boolean bIsConstraint) {
	double choice = random.nextDouble();
	double percentIdentifiers = (bIsConstraint)?(0.8):(0.5);

	if (choice < percentIdentifiers){
		ASTIdNode idNode = new ASTIdNode();
		idNode.name = "id_"+((int)Math.abs(random.nextInt())%10);
		return idNode;
	}else if (choice > 0.95){
		return new ASTFloatNode(0.0);
	}else{
		return new ASTFloatNode(random.nextDouble());
	}
}

public static boolean derivativeFunctionallyEquivalent(Expression exp, String diffSymbol, Expression diff, double relativeTolerance, double absoluteTolerance) {
	try {
		String symbolsExp[] = exp.getSymbols();
		String symbolsDiff[] = diff.getSymbols();
		boolean bHasDiffSymbol = false;
		for (int i = 0; symbolsExp!=null && i < symbolsExp.length; i++){
			if (symbolsExp[i].equals(diffSymbol)){
				bHasDiffSymbol = true;
			}
		}
		if (!bHasDiffSymbol){
			//
			// if expression doesn't reference 'diffSymbol', then derivative must be 0.
			//
			if (diff.isZero()){
				return true;
			}else{
				return false;
			}
		}
		
		String symbols[] = null;
		if (symbolsDiff==null){
			symbols = symbolsExp;
		}else{
			//
			// make combined list (without duplicates)
			//
			java.util.HashSet hashSet = new java.util.HashSet();
			for (int i = 0; i < symbolsExp.length; i++){
				hashSet.add(symbolsExp[i]);
			}
			for (int i = 0; i < symbolsDiff.length; i++){
				hashSet.add(symbolsDiff[i]);
			}
			symbols = (String[])hashSet.toArray(new String[hashSet.size()]);
		}

		cbit.vcell.parser.SymbolTable symbolTable = new SimpleSymbolTable(symbols);
		exp.bindExpression(symbolTable);
		diff.bindExpression(symbolTable);
		int diffSymbolIndex = exp.getSymbolBinding(diffSymbol).getIndex();
		double values[] = new double[symbols.length];
		//
		// go through 20 different sets of random inputs to test for equivalence
		// ignore cases of Exceptions during evaluations (out of domain of imbedded functions), keep trying until 20 successful evaluations
		//
		Random rand = new Random();
		final int MAX_TRIES = 1000;
		final int REQUIRED_NUM_EVALUATIONS = 20;
		int numEvaluations = 0;
		Exception savedException = null;
		for (int i = 0; i < MAX_TRIES && numEvaluations < REQUIRED_NUM_EVALUATIONS; i++){
			for (int j = 0; j < values.length; j++){
				values[j] = i*rand.nextGaussian();
			}
			try {
				//
				// evalutate "exact" differential
				//
				double resultDiff = diff.evaluateVector(values);

				//
				// central difference approximation from expression w.r.t "P" (diffSymbol)
				// choose "delta" about 100 times greater than error.
				//
				double nominalSymbolValue = values[diffSymbolIndex];
				double deltaP = Math.max(Math.abs(nominalSymbolValue*10*relativeTolerance),absoluteTolerance*10);
				double lowSymbolValue = nominalSymbolValue - deltaP;
				double highSymbolValue = nominalSymbolValue + deltaP;
				values[diffSymbolIndex] = lowSymbolValue;
				double result_low = exp.evaluateVector(values);
				values[diffSymbolIndex] = highSymbolValue;
				double result_high = exp.evaluateVector(values);
				if (Double.isInfinite(resultDiff) || Double.isNaN(resultDiff) || Math.abs(resultDiff)>1e10){
					throw new RuntimeException("diff = '"+diff+"' evaluates to "+resultDiff);
				}
				if (Double.isInfinite(result_low) || Double.isNaN(result_low)){
					throw new RuntimeException("low("+exp+") = "+result_low);
				}
				if (Double.isInfinite(result_high) || Double.isNaN(result_high)){
					throw new RuntimeException("high("+exp+") = "+result_high);
				}
				double resultCentralDifference = (result_high-result_low)/(2*deltaP);
				double scale = Math.abs(resultDiff)+Math.abs(resultCentralDifference);
				double absdiff = Math.abs(resultDiff-resultCentralDifference);
				if (scale > absoluteTolerance){ // if scale < absoluteTolerance, they are both close enough to zero.
					if (absdiff > relativeTolerance*10*scale){
						lg.debug("ExpressionUtils.derivativeFunctionallyEquivalent() 'exact' = "+resultDiff+", approx = "+resultCentralDifference+", absDiff = "+absdiff+", f_low = "+result_low+", f_high = "+result_high+", "+diffSymbol+" = "+nominalSymbolValue+" +/- "+deltaP);
						return false;
					}
				}
				numEvaluations++;
			}catch (Exception e){
				savedException = e;
			}
		}
		if (numEvaluations < REQUIRED_NUM_EVALUATIONS){
			savedException.printStackTrace(System.out);
			throw new RuntimeException("too many failed evaluations ("+numEvaluations+" of "+REQUIRED_NUM_EVALUATIONS+") ("+savedException.getMessage()+")");
		}
		return true;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2002 12:42:18 AM)
 * @return boolean
 * @param exp1 cbit.vcell.parser.Expression non null
 * @param exp2 cbit.vcell.parser.Expression non null
 */
public static boolean functionallyEquivalent(Expression exp1, Expression exp2) {
	boolean verifySameSymbols = true;
	return functionallyEquivalent(exp1,exp2,verifySameSymbols);
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2002 12:42:18 AM)
 * @return boolean
 * @param exp1 cbit.vcell.parser.Expression non null
 * @param exp2 cbit.vcell.parser.Expression non null
 */
public static boolean functionallyEquivalent(Expression exp1, Expression exp2, boolean bVerifySameSymbols) {
	try {
		// no tolerance for zero
		if (exp1.flattenSafe().isZero() || exp2.flattenSafe().isZero()) return (exp1.flattenSafe().isZero() && exp2.flattenSafe().isZero());
	} catch (ExpressionException e1) {
		lg.debug("FlattenSafe exception in equivalency check: ", e1.getMessage(), e1);
		return false;
	} 
	double defaultAbsoluteTolerance = 1e-12;
	double defaultRelativeTolerance = 1e-9;
	boolean bFirstAnswer = functionallyEquivalent(exp1,exp2,bVerifySameSymbols,defaultRelativeTolerance,defaultAbsoluteTolerance);
	try {
		boolean bSecondAnswer = functionallyEquivalent(exp1.flattenSafe(),exp2.flattenSafe(),false,defaultRelativeTolerance,defaultAbsoluteTolerance);
		if (bFirstAnswer == bSecondAnswer){
			return bFirstAnswer;
		}else{
			lg.debug("first  '"+exp1.infix()+"=="+exp2.infix()+"':"+bFirstAnswer+"'\nsecond '"+exp1.flattenSafe().infix()+"=="+exp2.flattenSafe().infix()+"':"+bSecondAnswer+"'");
			return bFirstAnswer;
		}
	} catch (ExpressionException e) {
		throw new RuntimeException(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2002 12:42:18 AM)
 * @return boolean
 * @param exp1 cbit.vcell.parser.Expression non null
 * @param exp2 cbit.vcell.parser.Expression non null
 */
public static boolean functionallyEquivalent(Expression exp1, Expression exp2, boolean verifySameSymbols, double relativeTolerance, double absoluteTolerance) {
	try {
		for (FunctionInvocation fi : exp1.getFunctionInvocations((String fname, FunctionType fType) -> fType==FunctionType.USERDEFINED)){
			Expression funcExp = fi.getFunctionExpression();
			String newSymbol = TokenMangler.fixTokenStrict(funcExp.infix());
			exp1.substituteInPlace(funcExp, new Expression(newSymbol));
		}
		for (FunctionInvocation fi : exp2.getFunctionInvocations((String fname, FunctionType fType) -> fType==FunctionType.USERDEFINED)){
			Expression funcExp = fi.getFunctionExpression();
			String newSymbol = TokenMangler.fixTokenStrict(funcExp.infix());
			exp2.substituteInPlace(funcExp, new Expression(newSymbol));
		}
		String symbols1[] = exp1.getSymbols();
		String symbols2[] = exp2.getSymbols();
		if (symbols1==null && symbols2==null){
			//
			// compare solutions
			//
			try {
				double result1 = exp1.evaluateConstantSafe();
				double result2 = exp2.evaluateConstantSafe();
				if (Double.isInfinite(result1) || Double.isNaN(result1)){
					throw new RuntimeException("exp1 = '"+exp1+"' evaluates to "+result1);
				}
				if (Double.isInfinite(result2) || Double.isNaN(result2)){
					throw new RuntimeException("exp1 = '"+exp2+"' evaluates to "+result2);
				}
				double scale = Math.abs(result1)+Math.abs(result2);
				double absdiff = Math.abs(result1-result2);
				if (scale > absoluteTolerance){
					if (absdiff > relativeTolerance*scale){
						lg.debug("EXPRESSIONS DIFFERENT: no symbols, delta eval: "+(result2-result1));
						return false;
					}
					else {
						return true;
					}
				} else {
					return true;
				}
			}catch (Exception e){
				throw new RuntimeException("unexpected exception ("+e.getMessage()+") while evaluating functional equivalence", e);
			}
		}
		String symbols[] = null;
		if (verifySameSymbols){
			if (symbols1==null || symbols2==null){
				lg.debug("EXPRESSIONS DIFFERENT: symbols null");
				return false;
			}
			if (symbols1.length!=symbols2.length){
				lg.debug("EXPRESSIONS DIFFERENT: symbols different number");
				return false;
			}
			//
			// make sure every symbol in symbol1 is in symbol2
			//
			for (int i = 0; i < symbols1.length; i++){
				boolean bFound = false;
				for (int j = 0; j < symbols2.length; j++){
					if (symbols1[i].equals(symbols2[j])){
						bFound = true;
						break;
					}
				}
				if (!bFound){
					lg.debug("EXPRESSIONS DIFFERENT: symbols don't match");
					return false;
				}
			}
			//
			// make sure every symbol in symbol2 is in symbol1
			//
			for (int i = 0; i < symbols2.length; i++){
				boolean bFound = false;
				for (int j = 0; j < symbols1.length; j++){
					if (symbols2[i].equals(symbols1[j])){
						bFound = true;
						break;
					}
				}
				if (!bFound){
					lg.debug("EXPRESSIONS DIFFERENT: symbols don't match");
					return false;
				}
			}
			symbols = symbols1;
		}else{ // don't verify symbols
			if (symbols1==null && symbols2!=null){
				symbols = symbols2;
			}else if (symbols1!=null && symbols2==null){
				symbols = symbols1;
			}else{
				//
				// make combined list (without duplicates)
				//
				java.util.HashSet hashSet = new java.util.HashSet();
				for (int i = 0; i < symbols1.length; i++){
					hashSet.add(symbols1[i]);
				}
				for (int i = 0; i < symbols2.length; i++){
					hashSet.add(symbols2[i]);
				}
				symbols = (String[])hashSet.toArray(new String[hashSet.size()]);
			}
		}
		cbit.vcell.parser.SymbolTable symbolTable = new SimpleSymbolTable(symbols);
		exp1.bindExpression(symbolTable);
		exp2.bindExpression(symbolTable);
		double values[] = new double[symbols.length];
		//
		// go through 20 different sets of random inputs to test for equivalence
		// ignore cases of Exceptions during evaluations (out of domain of imbedded functions), keep trying until 20 successful evaluations
		//
		Random rand = new Random(0);
		final int MAX_TRIES = 1000;
		final int REQUIRED_NUM_EVALUATIONS = 20;
		int numEvaluations = 0;
		Exception savedException = null;
		//
		// we want to scale logarithmically to explore magnitudes
		//
		//  base^NUM_TRIES = dynamicRange
		//  NUM_TRIES * log10(base) = log10(dynamicRange)
		//  base = 10^(log10(dynamicRange)/NUM_TRIES)
		//
		double dynamicRange = 1e3; // same as before, only log scaled
		double base = Math.pow(10,Math.log10(dynamicRange)/MAX_TRIES);
		double scaleFactor = 0.01;
		double result1MaxAbs = 0;
		double result2MaxAbs = 0;
		double maxDiffAbs = 0;
		for (int i = 0; i < MAX_TRIES && numEvaluations < REQUIRED_NUM_EVALUATIONS; i++){
			scaleFactor *= base;
			for (int j = 0; j < values.length; j++){
				values[j] = scaleFactor*rand.nextGaussian();
			}
			try {
				double result1 = exp1.evaluateVector(values);
				double result2 = exp2.evaluateVector(values);
				double result1Abs = Math.abs(result1);
				double result2Abs = Math.abs(result2);
				result1MaxAbs = Math.max(result1MaxAbs, result1Abs);
				result2MaxAbs = Math.max(result2MaxAbs, result2Abs);
				maxDiffAbs = Math.max(maxDiffAbs, Math.abs(result2-result1));
				if (Double.isInfinite(result1) || Double.isNaN(result1)){
					throw new RuntimeException("Expression = '"+exp1+"' evaluates to "+result1);
				}
				if (Double.isInfinite(result2) || Double.isNaN(result2)){
					throw new RuntimeException("Expression = '"+exp2+"' evaluates to "+result2);
				}
				double scale = Math.abs(result1)+Math.abs(result2);
				double absdiff = Math.abs(result1-result2);
				if (scale > absoluteTolerance){
					if (absdiff > relativeTolerance*scale){
						int logRelError = (int) Math.log10(absdiff / (relativeTolerance * scale));
						lg.debug("EXPRESSIONS DIFFERENT: numerical test "+numEvaluations+", tolerance exceeded by "+ logRelError +"digits");
						return false;
					}
				}
				numEvaluations++;
			}catch (Exception e){
				savedException = e;
			}
		}
		if (numEvaluations < REQUIRED_NUM_EVALUATIONS){
			//savedException.printStackTrace(System.out);
			throw new RuntimeException("too many failed evaluations ("+numEvaluations+" of "+REQUIRED_NUM_EVALUATIONS+") ("+savedException.toString()+") of expressions '"+exp1+"' and '"+exp2+"'");
		}
		// before we declare victory, the expressions may have been poorly scaled (e.g. max magnitude was very small)
		double maxAbsScale = result1MaxAbs + result2MaxAbs;
		if (maxAbsScale<absoluteTolerance && maxDiffAbs > relativeTolerance*maxAbsScale){
			int logRelError = (int) Math.log10(maxDiffAbs / (relativeTolerance * maxAbsScale));
			lg.debug("EXPRESSIONS DIFFERENT: numerical test not well scaled, maxAbsScale="+maxAbsScale+", maxDiffAbs exceeded relative tolerance by "+ logRelError +"digits");
			return false;
		}

		Vector<Discontinuity> discont1 = exp1.getDiscontinuities();
		Vector<Discontinuity> discont2 = exp2.getDiscontinuities();
//		if(discont1.size() != discont2.size())
//		{
//			return false;
//		}
//		else
//		{
		if ((discont1.size() != 0) || (discont2.size() !=0))
			{
				Expression productOfdiscont1 = new Expression(1);
				Expression productOfdiscont2 = new Expression(1);
				for(Discontinuity dis1 : discont1)
				{
					productOfdiscont1 = Expression.mult(productOfdiscont1, dis1.getSignedRootFindingExp());
				}
				for(Discontinuity dis2 : discont2)
				{
					productOfdiscont2 = Expression.mult(productOfdiscont2, dis2.getSignedRootFindingExp());
				}
				return functionallyEquivalent(productOfdiscont1, productOfdiscont2, verifySameSymbols, relativeTolerance, absoluteTolerance);
			}
//		}
		return true;
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		lg.debug("EXPRESSIONS DIFFERENT: "+e);
		return false;
	}
}

public interface IExpression {

	String[] getSymbols();

	double evaluateConstant() throws ExpressionException;

	void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException;

	double evaluateVector(double[] values) throws ExpressionException;

	Vector<Discontinuity> getDiscontinuities() throws ExpressionException;
};

	public static class VCellIExpression implements IExpression {
		final Expression exp;
		public VCellIExpression(Expression expression){
			this.exp = expression;
		}
		@Override
		public String[] getSymbols() {
			return exp.getSymbols();
		}
		@Override
		public double evaluateConstant() throws ExpressionException {
			return exp.evaluateConstant();
		}
		@Override
		public void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException {
			exp.bindExpression(symbolTable);
		}
		@Override
		public double evaluateVector(double[] values) throws ExpressionException {
			return exp.evaluateVector(values);
		}
		@Override
		public Vector<Discontinuity> getDiscontinuities() throws ExpressionException {
			return exp.getDiscontinuities();
		}
	}

//	public static class JSCLIExpression implements IExpression {
//		final jscl.math.Expression exp;
//		public JSCLIExpression(jscl.math.Expression expression){
//			this.exp = expression;
//		}
//		@Override
//		public String[] getSymbols() {
//			return jscl.math.Expression.exp.getSymbols();
//		}
//		@Override
//		public double evaluateConstant() throws ExpressionException {
//			return exp.evaluateConstant();
//		}
//		@Override
//		public void bindExpression(SymbolTable symbolTable) throws ExpressionBindingException {
//			exp.bindExpression(symbolTable);
//		}
//		@Override
//		public double evaluateVector(double[] values) throws ExpressionException {
//			return exp.evaluateVector(values);
//		}
//		@Override
//		public Vector<Discontinuity> getDiscontinuities() throws ExpressionException {
//			return exp.getDiscontinuities();
//		}
//	}

	public static boolean functionallyEquivalent(IExpression exp1, IExpression exp2, boolean verifySameSymbols, double relativeTolerance, double absoluteTolerance) {
		try {
			String symbols1[] = exp1.getSymbols();
			String symbols2[] = exp2.getSymbols();
			if (symbols1==null && symbols2==null){
				//
				// compare solutions
				//
				try {
					double result1 = exp1.evaluateConstant();
					double result2 = exp2.evaluateConstant();
					if (Double.isInfinite(result1) || Double.isNaN(result1)){
						throw new RuntimeException("exp1 = '"+exp1+"' evaluates to "+result1);
					}
					if (Double.isInfinite(result2) || Double.isNaN(result2)){
						throw new RuntimeException("exp1 = '"+exp2+"' evaluates to "+result2);
					}
					double scale = Math.abs(result1)+Math.abs(result2);
					double absdiff = Math.abs(result1-result2);
					if (scale > absoluteTolerance){
						if (absdiff > relativeTolerance*scale){
							lg.debug("EXPRESSIONS DIFFERENT: no symbols, delta eval: "+(result2-result1));
							return false;
						}
						else {
							return true;
						}
					} else {
						return true;
					}
				}catch (Exception e){
					throw new RuntimeException("unexpected exception ("+e.getMessage()+") while evaluating functional equivalence", e);
				}
			}
			String symbols[] = null;
			if (verifySameSymbols){
				if (symbols1==null || symbols2==null){
					lg.debug("EXPRESSIONS DIFFERENT: symbols null");
					return false;
				}
				if (symbols1.length!=symbols2.length){
					lg.debug("EXPRESSIONS DIFFERENT: symbols different number");
					return false;
				}
				//
				// make sure every symbol in symbol1 is in symbol2
				//
				for (int i = 0; i < symbols1.length; i++){
					boolean bFound = false;
					for (int j = 0; j < symbols2.length; j++){
						if (symbols1[i].equals(symbols2[j])){
							bFound = true;
							break;
						}
					}
					if (!bFound){
						lg.debug("EXPRESSIONS DIFFERENT: symbols don't match");
						return false;
					}
				}
				//
				// make sure every symbol in symbol2 is in symbol1
				//
				for (int i = 0; i < symbols2.length; i++){
					boolean bFound = false;
					for (int j = 0; j < symbols1.length; j++){
						if (symbols2[i].equals(symbols1[j])){
							bFound = true;
							break;
						}
					}
					if (!bFound){
						lg.debug("EXPRESSIONS DIFFERENT: symbols don't match");
						return false;
					}
				}
				symbols = symbols1;
			}else{ // don't verify symbols
				if (symbols1==null && symbols2!=null){
					symbols = symbols2;
				}else if (symbols1!=null && symbols2==null){
					symbols = symbols1;
				}else{
					//
					// make combined list (without duplicates)
					//
					java.util.HashSet hashSet = new java.util.HashSet();
					for (int i = 0; i < symbols1.length; i++){
						hashSet.add(symbols1[i]);
					}
					for (int i = 0; i < symbols2.length; i++){
						hashSet.add(symbols2[i]);
					}
					symbols = (String[])hashSet.toArray(new String[hashSet.size()]);
				}
			}
			cbit.vcell.parser.SymbolTable symbolTable = new SimpleSymbolTable(symbols);
			exp1.bindExpression(symbolTable);
			exp2.bindExpression(symbolTable);
			double values[] = new double[symbols.length];
			//
			// go through 20 different sets of random inputs to test for equivalence
			// ignore cases of Exceptions during evaluations (out of domain of imbedded functions), keep trying until 20 successful evaluations
			//
			Random rand = new Random();
			final int MAX_TRIES = 1000;
			final int REQUIRED_NUM_EVALUATIONS = 20;
			int numEvaluations = 0;
			Exception savedException = null;
			for (int i = 0; i < MAX_TRIES && numEvaluations < REQUIRED_NUM_EVALUATIONS; i++){
				for (int j = 0; j < values.length; j++){
					values[j] = 0.01*(i+1)*rand.nextGaussian();
				}
				try {
					double result1 = exp1.evaluateVector(values);
					double result2 = exp2.evaluateVector(values);
					if (Double.isInfinite(result1) || Double.isNaN(result1)){
						throw new RuntimeException("Expression = '"+exp1+"' evaluates to "+result1);
					}
					if (Double.isInfinite(result2) || Double.isNaN(result2)){
						throw new RuntimeException("Expression = '"+exp2+"' evaluates to "+result2);
					}
					double scale = Math.abs(result1)+Math.abs(result2);
					double absdiff = Math.abs(result1-result2);
					if (scale > absoluteTolerance){
						if (absdiff > relativeTolerance*scale){
							lg.debug("EXPRESSIONS DIFFERENT: numerical test "+numEvaluations+", tolerance exceeded by "+(int)Math.log(absdiff/(relativeTolerance*scale))+"digits");
							return false;
						}
					}
					numEvaluations++;
				}catch (Exception e){
					savedException = e;
				}
			}
			if (numEvaluations < REQUIRED_NUM_EVALUATIONS){
				//savedException.printStackTrace(System.out);
				throw new RuntimeException("too many failed evaluations ("+numEvaluations+" of "+REQUIRED_NUM_EVALUATIONS+") ("+savedException.toString()+") of expressions '"+exp1+"' and '"+exp2+"'");
			}
			Vector<Discontinuity> discont1 = exp1.getDiscontinuities();
			Vector<Discontinuity> discont2 = exp2.getDiscontinuities();
			if(discont1.size() != discont2.size())
			{
				return false;
			}
			else
			{
				if (discont1.size() != 0)
				{
					Expression productOfdiscont1 = new Expression(1);
					Expression productOfdiscont2 = new Expression(1);
					for(Discontinuity dis1 : discont1)
					{
						productOfdiscont1 = Expression.mult(productOfdiscont1, dis1.getSignedRootFindingExp());
					}
					for(Discontinuity dis2 : discont2)
					{
						productOfdiscont2 = Expression.mult(productOfdiscont2, dis2.getSignedRootFindingExp());
					}
					return functionallyEquivalent(productOfdiscont1, productOfdiscont2, verifySameSymbols, relativeTolerance, absoluteTolerance);
				}
			}
			return true;
		}catch (cbit.vcell.parser.ExpressionException e){
			e.printStackTrace(System.out);
			lg.debug("EXPRESSIONS DIFFERENT: "+e);
			return false;
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 5:02:53 PM)
 * @return cbit.vcell.parser.Expression
 */
public static Expression generateExpression(java.util.Random random, int maxDepth, boolean bIsConstraint) throws ExpressionException {
	SimpleNode node = generateSubtree(0,maxDepth,random,bIsConstraint);
	if (bIsConstraint){
		node = (SimpleNode)node.copyTreeBinary();
	}
	return new Expression(node.infixString(SimpleNode.LANGUAGE_DEFAULT));
}

private static SimpleNode generateSubtree(int depth, int maxDepth, java.util.Random random, boolean bIsConstraint) {
	SimpleNode newNode = null;
	if (depth == 0 && bIsConstraint){
		ASTRelationalNode relNode = new ASTRelationalNode();
		double rtype = random.nextDouble();
		final String ops[] = {
			">",
			"<",
			"<=",
			">=",
			"==",
			//"!="
		};
		relNode.setOperationFromToken(ops[(int)Math.floor(rtype*ops.length)]);
		newNode = relNode;
	}else if (depth >= maxDepth){
		newNode = createTerminalNode(random,bIsConstraint);
	}else{
		newNode = createNode(random,bIsConstraint);
	}
	int numChildren = getNumberOfChildren(newNode);
	for (int i = 0; i < numChildren; i++){
		if (newNode instanceof ASTMultNode && i>0){  // InvertTerm is only ok if it's a second or later child of a mult node
			newNode.jjtAddChild(generateSubtree(depth+1,maxDepth,random,bIsConstraint));
		}else{
			SimpleNode child = null;
			while ((child = generateSubtree(depth+1,maxDepth,random,bIsConstraint)) instanceof ASTInvertTermNode){
			}
			newNode.jjtAddChild(child);
		}
	}
	return newNode;
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:41:17 PM)
 * @return int
 * @param node cbit.vcell.parser.SimpleNode
 */
private static int getNumberOfChildren(SimpleNode node) {
	if (node instanceof ASTAddNode){
		return 3;
	}else if (node instanceof ASTAndNode){
		return 2;
	}else if (node instanceof ASTFloatNode){
		return 0;
	}else if (node instanceof ASTFuncNode){
		ASTFuncNode fn = (ASTFuncNode)node;
		switch (fn.getFunction()){
			case ABS: {
				return 1;
			}
			case ACOS: {
				return 1;
			}
			case ASIN: {
				return 1;
			}
			case ATAN: {
				return 1;
			}
			case ATAN2: {
				return 2;
			}
			case COS: {
				return 1;
			}
			case EXP: {
				return 1;
			}
			case LOG: {
				return 1;
			}
			case MAX: {
				return 2;
			}
			case MIN: {
				return 2;
			}
			case POW: {
				return 2;
			}
			case SIN: {
				return 1;
			}
			case SQRT: {
				return 1;
			}
			case TAN: {
				return 1;
			}
			case CEIL: {
				return 1;
			}
			case FLOOR: {
				return 1;
			} 			
			case CSC: {
				return 1;
			}
			case COT: {
				return 1;
			}
			case SEC: {
				return 1;
			}
			case ACSC: {
				return 1;
			}
			case ACOT: {
				return 1;
			}
			case ASEC: {
				return 1;
			}
			case SINH: {
				return 1;
			}
			case COSH: {
				return 1;
			}
			case TANH: {
				return 1;
			}
			case CSCH: {
				return 1;
			}
			case COTH: {
				return 1;
			}
			case SECH: {
				return 1;
			}
			case ASINH: {
				return 1;
			}
			case ACOSH: {
				return 1;
			}
			case ATANH: {
				return 1;
			}
			case ACSCH: {
				return 1;
			}
			case ACOTH: {
				return 1;
			}
			case ASECH: {
				return 1;
			}
			case FACTORIAL: {
				return 1;
			}			
			default:{
				throw new RuntimeException("unknown function type : "+fn.getFunction());
			}
		}
	}else if (node instanceof ASTIdNode){
		return 0;
	}else if (node instanceof ASTInvertTermNode){
		return 1;
	}else if (node instanceof ASTMinusTermNode){
		return 1;
	}else if (node instanceof ASTMultNode){
		return 3;
	}else if (node instanceof ASTNotNode){
		return 1;
	}else if (node instanceof ASTOrNode){
		return 2;
	}else if (node instanceof ASTPowerNode){
		return 2;
	}else if (node instanceof ASTRelationalNode){
		return 2;
	}else{
		throw new RuntimeException("unknown node type");
	}
}

public static Expression[] solveLinear(Expression[] implicitEquations, String[] vars) throws ExpressionException, MatrixException, RationalExpMatrix.NoSolutionExistsException {
	RationalExpMatrix rationalExpMatrix = new RationalExpMatrix(vars.length,vars.length+1);
	for (int equIndex=0; equIndex < implicitEquations.length; equIndex++){
		Expression implicitEquation = implicitEquations[equIndex];
		Expression implicitEquationConstantTerm = new Expression(implicitEquation);
		for (int varIndex=0; varIndex < vars.length; varIndex++) {
			Expression derivative_wrt_var = implicitEquation.differentiate(vars[varIndex]).flatten();
			if (derivative_wrt_var != null && !derivative_wrt_var.isZero()){
				rationalExpMatrix.set_elem(equIndex,varIndex,RationalExpUtils.getRationalExp(derivative_wrt_var));
			}
			implicitEquationConstantTerm.substituteInPlace(new Expression(vars[varIndex]),new Expression(0));
		}
		rationalExpMatrix.set_elem(equIndex,vars.length,RationalExpUtils.getRationalExp(implicitEquationConstantTerm.flatten()));
	}
//	rationalExpMatrix.show();

	RationalExp[] solutions = rationalExpMatrix.solveLinearSystem(vars);
	Expression[] solutionExpressions = new Expression[solutions.length];
	for (int i = 0; i < solutions.length; i++){
		if (solutions[i] != null) {
			solutionExpressions[i] = new Expression(solutions[i].infixString());
		}
	}
	return solutionExpressions;
}

}
