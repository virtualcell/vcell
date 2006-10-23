package edu.uchc.vcell.expression.test;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import net.sourceforge.interval.ia_math.RealInterval;

import org.vcell.expression.DivideByZeroException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.ExpressionUtilities;
import org.vcell.expression.FunctionDomainException;
import org.vcell.expression.IExpression;
import org.vcell.expression.ParserException;
import org.vcell.expression.SimpleSymbolTable;

public class ExpressionTest {
/**
 * main entrypoint - starts the application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		int num = 5000;
		
		if (args.length > 0) {
			num = Integer.parseInt(args[0]);
		}
			
		java.util.Random r = new java.util.Random();
		String ids[] = {"id_0", "id_1", "id_2", "id_3", 
				"id_4", "id_5", "id_6", "id_7", "id_8", "id_9"};
		org.vcell.expression.SimpleSymbolTable symbolTable = new org.vcell.expression.SimpleSymbolTable(ids); 

		double v1[] = {0,1,2,3,4,5,6,7,8,9 };

		for (int i = 0; i < num; i ++){
			IExpression exp = ExpressionFactory.createRandomExpression(r, 4, false);
			exp.bindExpression(symbolTable);

			try {
				double d = exp.evaluateVector(v1);
				System.out.println(d + " " + exp.infix());
			} catch (Exception ex) {
				System.out.println("-0.0 " + exp.infix());						
			}
		}
		
		//testDifferentiate(500,3,100);
		//testBig();
		//testEvaluateVector();
	//	testEvaluateInterval(1000,3,100,false);
	//	testEvaluateInterval(1000,3,100,true);
		//testEvaluateNarrowing(100,2,100);
		//testFlatten(300,5,100);
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}

}


/**
 * Insert the method's description here.
 * Creation date: (5/29/2003 4:11:59 PM)
 * @return double
 * @param interval net.sourceforge.interval.ia_math.RealInterval
 */
public static double sample(RealInterval interval, java.util.Random random) {
	if (interval.nonEmpty()==false){
		throw new IllegalArgumentException("cannot sample empty bounds");
	}
	
	if (interval.lo()>Double.NEGATIVE_INFINITY && interval.hi()<Double.POSITIVE_INFINITY){
		// finitely bounded from both sides
		return random.nextDouble()*(interval.hi()-interval.lo())+interval.lo();
	}else if (interval.lo()==Double.NEGATIVE_INFINITY && interval.hi()==Double.POSITIVE_INFINITY){
		// unbounded
		return 100*random.nextGaussian();
	}else{
		// bounded on one side only, keep trying guassians centered around the bound until one fits
		double bound = (interval.lo()==Double.NEGATIVE_INFINITY)?interval.hi():interval.lo();
		double sample = 100*random.nextGaussian() + bound;
		while (sample < interval.lo() || sample > interval.hi()){
			sample = 100*random.nextGaussian() + bound;
		}
		return sample;
	}
}


/**
 * This method was created by a SmartGuide.
 */
private static void testBig() {
	try {
		

String expString = "(((ALPHA * ((ip3r_coeff * ((4.0 * pow((RIC / ch_dens), 3.0))"+
						" - (3.0 * pow((RIC / ch_dens), 4.0))) * (Ca_ER - C)) + (LEAK * (Ca_ER - C)) "+
						" - (bindingFluxFactor * ((ss1 * C * P) - (s1 * PC) + (ss2 * C * PC))))) - (bb1 * B * C)"+
						" - (b1 * B) + (b1 * slow_buffer_density)) / ((mobile_buff_dens * mobile_buff_K_value"+
						" / pow((mobile_buff_K_value + C), 2.0)) + fixed_buffer_bindingRatio + 1.0));";
//String expString = "(( - VmaxANT / (1.0 + (KeADP / ADP_OUTSIDE) + (ATP_OUTSIDE / KeATP * KeADP / ADP_OUTSIDE) + (KiATP / ATP_INSIDE))) + VmaxSYNTH);";

		IExpression origExp = ExpressionFactory.createExpression("RIC;");
		IExpression newExp = ExpressionFactory.createExpression("0.0;");
		long before = 0;
		long after = 0;
		
		before = System.currentTimeMillis();
		IExpression exp = ExpressionFactory.createExpression(expString);
		after = System.currentTimeMillis(); 
		long parseTime = after-before;
System.out.println("time for parsing = "+parseTime+" ms");
System.out.println(exp);
		
		before = System.currentTimeMillis();
		IExpression flat = exp.flatten();
		after = System.currentTimeMillis(); 
		long flattenTime = after-before;
System.out.println("time for simplifying = "+flattenTime+" ms");
System.out.println(flat);

		before = System.currentTimeMillis();
		String symbols[] = exp.getSymbols();
		after = System.currentTimeMillis(); 
		long symbolsTime = after-before;
System.out.println("time for getting symbols = "+symbolsTime+" ms");
System.out.println(flat);
		
//		System.out.println(flat);

//System.out.println("Substitute "+origExp+" for "+newExp);
		before = System.currentTimeMillis();
		flat.substituteInPlace(origExp,newExp);
		after = System.currentTimeMillis(); 
		long substituteInPlaceTime = after-before;
System.out.println("time for substitution in place = "+substituteInPlaceTime+" ms");
		
		before = System.currentTimeMillis();
		ExpressionFactory.createSubstitutedExpression(flat, origExp, newExp);
		after = System.currentTimeMillis(); 
		long substituteTime = after-before;
System.out.println("time for new Substituted Expression = "+substituteTime+" ms");
		
//System.out.println("Simplifying:");
		before = System.currentTimeMillis();
		flat = flat.flatten();
		after = System.currentTimeMillis(); 
		long flatten2Time = after-before;
System.out.println("time for simplification = "+flatten2Time+" ms");
//System.out.println(flat);
		
//System.out.println("Differentiating:");
		before = System.currentTimeMillis();
		IExpression exactD_C = flat.differentiate("C");
		after = System.currentTimeMillis(); 
		long differentiateTime = after-before;
System.out.println("time for differentiation = "+differentiateTime+" ms");
System.out.println(exactD_C);

System.out.println("Simplifying:");
		before = System.currentTimeMillis();
		flat = exactD_C.flatten();
		after = System.currentTimeMillis(); 
		long flatten3Time = after-before;
System.out.println("time for simplification = "+flatten3Time+" ms");
System.out.println(flat);
		
	} catch (ParserException e) {
		System.out.println("error parsing expression");
		e.printStackTrace();
		return;
	} catch (Exception e) {
		System.out.println("exception parsing expression");
		e.printStackTrace();
		return;
	}
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (12/27/2002 2:14:48 PM)
 */
public static void testDifferentiate(int numTrials, int depth, long seed) {
	final double relativeTolerance = 1e-7;
	final double absoluteTolerance = 1e-10;
	int numCorrect = 0;
	int numWrong = 0;
	int numDerivatives = 0;
	java.util.Random random = new java.util.Random(seed);
	for (int i = 0; i < numTrials; i++) {
		try {
			IExpression exp = ExpressionFactory.createRandomExpression(random, depth, false);
			IExpression flattened = null;
			try {
				//
				// test against identifier that is not present in expression
				//
				numDerivatives++;
				String dummySymbol = "abc123";
				IExpression diff = exp.differentiate(dummySymbol);
				try {
					diff = diff.flatten();
				}catch (DivideByZeroException e){
					System.out.println("ExpressionTest.testDifferentiate(), DivByZero while flattening '"+e.getMessage()+"', \nexp='"+diff+"'\n\n");
					continue;
				}
				if (!diff.isZero()){
					numWrong++;
					System.out.println("f() = "+exp);
					System.out.println("D f()/D("+dummySymbol+") = "+diff);
					System.out.println("D f()/D("+dummySymbol+") != 0.0\n\n\n");
					continue;
				}else{
					numCorrect++;
				}
				//
				// test against derivative wrt each symbol (test for numerical equivalence vs. central difference)
				//
				String symbols[] = exp.getSymbols();
				if (symbols!=null && symbols.length>0){
					for (int j = 0;symbols!=null && j < symbols.length; j++){
						numDerivatives++;
						try {
							diff = exp.differentiate(symbols[j]);
							try {
								diff = diff.flatten();
							}catch (DivideByZeroException e){
								System.out.println("ExpressionTest.testDifferentiate(), DivByZero while flattening '"+e.getMessage()+"', \nexp='"+diff+"'\n\n");
								continue;
							}
							boolean isSame = org.vcell.expression.ExpressionUtilities.derivativeFunctionallyEquivalent(exp,symbols[j],diff,relativeTolerance,absoluteTolerance);
							if (isSame) {
								numCorrect++;
							} else {
								numWrong++;
								System.out.println("f() = "+exp);
								System.out.println("D f()/D("+symbols[j]+") = "+diff);
								System.out.println("[f("+symbols[j]+"+delta)-f("+symbols[j]+"-delta)]/(2*delta)  !=  D f(k)/D("+symbols[j]+")\n\n\n");
							}
						} catch (org.vcell.expression.DivideByZeroException e) {
							e.printStackTrace(System.out);
						}
					}
				}
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }
    System.out.println("test for .differentiate(), "+numCorrect+" correct, "+numWrong+" wrong, "+(numDerivatives-numCorrect-numWrong)+" failed, out of "+numTrials+" trials");
}


/**
 * This method was created in VisualAge.
 */
public static void testEvaluateInterval(int numTrials, int depth, long seed, boolean bIsConstraint) {
    int numCorrect = 0;
    int numWrong = 0;
    int numFailures = 0;
    java.util.Random random = new java.util.Random(seed);
    for (int i = 0; i < numTrials; i++) {
		try {
			IExpression exp = ExpressionFactory.createRandomExpression(random, depth, bIsConstraint);
			//System.out.println("exp = '"+exp+"'");
			//
			// generate random intervals for arguments
			//
			String symbols[] = exp.getSymbols();
			RealInterval intervals[] = null;
			if (symbols!=null && symbols.length>0){
				SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);
				exp.bindExpression(symbolTable);
				intervals = new RealInterval[symbolTable.getSize()];
				for (int j = 0; j < intervals.length; j++){
					double value1 = random.nextGaussian();
					double value2 = random.nextGaussian();
					intervals[j] = new RealInterval(Math.min(value1,value2),Math.max(value1,value2));
				}
			}else{
				intervals = new RealInterval[0];
			}

			//
			// evaluate using interval arithmetic
			//
			RealInterval intervalResult = exp.evaluateInterval(intervals);

			//
			// generate several "feasible points" (samples within input intervals)
			// and evaluate expression (must be within bounds of intervalResult).
			//
			double doubleValues[] = new double[intervals.length];
			int maxExperiments = 1 + 40*intervals.length;
			for (int experimentCount = 0; experimentCount < maxExperiments; experimentCount++){
				//
				// sample input intervals as doubles
				//
				for (int j = 0; j < doubleValues.length; j++){
					double low = intervals[j].lo();
					double high = intervals[j].hi();
					if (!Double.isInfinite(low) && !Double.isInfinite(high)){
						//
						// finite interval, uniformly sample between high and low
						//
						doubleValues[j] = low+(random.nextDouble()*(high-low));
					}else{
						//
						// infinite interval, keep trying Gaussians until within the bounds
						//
						boolean bWithinRange = false;
						while (!bWithinRange){
							double sample = random.nextGaussian();
							if (sample>=low && sample<=high){
								doubleValues[j] = sample;
								bWithinRange = true;
							}
						}
					}
				}

				//
				// verify that expression evaluates within intervalResult
				//
				double doubleResult = exp.evaluateVector(doubleValues);
				if (doubleResult<intervalResult.lo() || doubleResult>intervalResult.hi()){
					numWrong++;
					System.out.println(exp);
					for (int k = 0; symbols!=null && k < symbols.length; k++){
						System.out.println(symbols[k]+" = "+intervals[k]+" ... sampled = "+doubleValues[k]);
					}
					System.out.println("interval result = " + intervalResult+", sample result = "+doubleResult+"\n\n\n");
				}else{
					numCorrect++;
				}	
			}
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			numFailures++;
		}
    }
    System.out.println("test for .testEvaluateInterval(), "+numCorrect+" correct, "+numWrong+" wrong, "+numFailures+" failures, "+numTrials+" trials");
}


/**
 * This method was created in VisualAge.
 */
public static void testEvaluateNarrowing(int numTrials, int depth, long seed) {
	try {
		java.util.Random random = new java.util.Random(seed);

		for (int i = 0; i < numTrials; i++){
			IExpression constraint = ExpressionFactory.createRandomExpression(random, depth, true);
			String symbols[] = constraint.getSymbols();
			if (symbols==null || symbols.length<1){
				continue;
			}
			RealInterval initialItervals[] = new RealInterval[symbols.length];
			RealInterval values[] = new RealInterval[symbols.length];
			RealInterval prevValues[] = new RealInterval[symbols.length];
			for (int j = 0; j < initialItervals.length; j++){
				double middle = random.nextGaussian();
				double size = random.nextGaussian()*10;
				initialItervals[j] = new RealInterval(middle-Math.abs(size),middle+Math.abs(size));
				values[j] = new RealInterval(initialItervals[j].lo(),initialItervals[j].hi());
				prevValues[j] = new RealInterval(initialItervals[j].lo(),initialItervals[j].hi());
			}
			SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);
			constraint.bindExpression(symbolTable);

			System.out.println(i+") narrowing '"+constraint+"'");
			
			boolean bValuesChanged = true;
			boolean bValuesEverChanged = true;
			boolean bConstraintSatisfied = true;
			final int maxIterations = 100;
			int iteration = 0;
			while (bValuesChanged && bConstraintSatisfied && iteration < maxIterations){
				iteration++;
				try {
					bConstraintSatisfied = constraint.narrow(values);
				}catch (Exception e){
					e.printStackTrace(System.out);
					bConstraintSatisfied = false;
				}
				if (bConstraintSatisfied==false){
					System.out.println("     constraint failed");
					continue;
				}
						
				bValuesChanged = false;
				System.out.print("     iteration "+iteration);
				for (int j = 0; j < values.length; j++){
					if (!prevValues[j].equals(values[j])){
						bValuesChanged = true;
						bValuesEverChanged = true;
						prevValues[j] = (RealInterval)values[j].clone();
					}
					System.out.print(", "+symbols[j]+" = "+values[j].toString());
				}
				System.out.println();
			}
			boolean bNarrowedIsSubset = true;
			boolean bNarrowedIsDifferent = false;
			if (bConstraintSatisfied){
				//
				// verify that narrowed intervals are a subset of the initial intervals
				//
				for (int j = 0; j < values.length; j++){
					if (values[j].lo() >= initialItervals[j].lo() &&
						values[j].lo() <= initialItervals[j].hi() &&
						values[j].hi() >= initialItervals[j].lo() &&
						values[j].hi() <= initialItervals[j].hi()){

						if (!values[j].equals(initialItervals[j])){
							bNarrowedIsDifferent = true;
						}
						System.out.println("     ------narrowed interval "+symbols[j]+" = "+values[j]+" ("+initialItervals[j]+") OK");
					}else{
						System.out.println("     ------narrowed interval "+symbols[j]+" = "+values[j]+" ("+initialItervals[j]+") <<<<< B A D>>>>>>");
						bNarrowedIsSubset = false;
					}
				}

				if (bNarrowedIsSubset && bNarrowedIsDifferent){
					//
					// verify that samples taken from the removed portions of the initial interval all fail.
					//
					int failedCount = 0;
					int successCount = 0;
					int exceptionCount = 0;
					final int maxExceptionCount = 3000;
					double samples[] = new double[values.length];
					while ((failedCount+successCount) < 300 && exceptionCount < maxExceptionCount){
						boolean bSamplesOutsideNarrowedIntervals = false;
						for (int k = 0; k < initialItervals.length; k++){
							samples[k] = sample(initialItervals[k],random);
							if (samples[k] < values[k].lo() || samples[k] > values[k].hi()){
								bSamplesOutsideNarrowedIntervals = true;
							}
						}
						try {
							if (bSamplesOutsideNarrowedIntervals){
								if (constraint.evaluateVector(samples)==1.0){
									failedCount++;
									System.out.print("     ---NARROWING FAILED, removed point was in the solution");
									for (int m = 0; m < values.length; m++){
										System.out.print(", "+symbols[m]+" = "+samples[m]);
									}
									System.out.println("");
								}else{
									successCount++;
								}
							}
						}catch (FunctionDomainException e){
							exceptionCount++;
							if (exceptionCount>=maxExceptionCount){
								System.out.println(e.toString());
							}
						}
					}
					if (failedCount>0 || successCount==0){
						System.out.println("     --<<BAD>>--  "+failedCount+" evaluations out of "+(failedCount+successCount)+" failed, "+exceptionCount+" exceptions");
					}else{
						System.out.println("     --<<GOOD>>-- "+failedCount+" evaluations out of "+(failedCount+successCount)+" failed, "+exceptionCount+" exceptions");
					}
				}
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * This method was created in VisualAge.
 */
public static void testEvaluateNarrowingOld() {
	try {
		IExpression exps[] = {
			(ExpressionFactory.createExpression("a*b==c;")).flatten(),
			(ExpressionFactory.createExpression("c<2*a;")).flatten(),
//			(new Expression("a>1;")).flatten(),
//			(new Expression("a<3;")).flatten(),
//			(new Expression("b>2;")).flatten(),
//			(new Expression("b<4;")).flatten(),
//			(new Expression("c>4;")).flatten(),
//			(new Expression("c<100;")).flatten(),
		};
//		RealInterval v[] = { RealInterval.fullInterval(), RealInterval.fullInterval(), RealInterval.fullInterval() };
		RealInterval v[] = { new RealInterval(1,3), new RealInterval(2,4), new RealInterval(4,100) };

		SimpleSymbolTable simpleSymbolTable = new SimpleSymbolTable(new String[] { "a", "b", "c"});

		for (int i = 0; i < exps.length; i++){
			exps[i].bindExpression(simpleSymbolTable);
		}
		
		for (int i=0;i<5;i++){
			for (int j=0;j<exps.length;j++){
				System.out.println(i+") narrowing '"+exps[j]+"'");
				if (!exps[j].narrow(v)){
					System.out.println("narrowing failed");
					break;
				}
				for (int k=0;k<v.length;k++){
					System.out.println("     "+v[k]);
				}
			}
		}
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * This method was created in VisualAge.
 */
public static void testEvaluateVector() {
	try {
		IExpression exp = ExpressionFactory.createExpression("a+b/c;");

		SimpleSymbolTable simpleSymbolTable = new SimpleSymbolTable(new String[] { "a", "b", "c" });

		double v1[] = { 0,1,2 };
		double v2[] = { 3,4,5 };

		exp.bindExpression(simpleSymbolTable);

		System.out.println("evaluate '"+exp+"' with v1=["+v1[0]+","+v1[1]+","+v1[2]+"]: result="+exp.evaluateVector(v1));
		System.out.println("evaluate '"+exp+"' with v2=["+v2[0]+","+v2[1]+","+v2[2]+"]: result="+exp.evaluateVector(v2));
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/27/2002 2:14:48 PM)
 */
public static void testFlatten(int numTrials, int depth, long seed) {
	int numCorrect = 0;
	int numFailed = 0;
	int numWrong = 0;
	int numNotIdempotent = 0;
	java.util.Random random = new java.util.Random(seed);
	for (int i = 0; i < numTrials; i++) {
		try {
			IExpression exp = ExpressionFactory.createRandomExpression(random, depth, false);
			try {
				IExpression flattened = exp.flatten();
				boolean isSame = ExpressionUtilities.functionallyEquivalent(exp, flattened, false);
				IExpression flattenedTwice = exp.flatten().flatten();
				boolean isIdempotent = flattenedTwice.compareEqual(exp);
				if (isSame && isIdempotent) {
					numCorrect++;
				} else {
					if (!isSame){
						numWrong++;
						System.out.println("exp        = " + exp.infix());
						System.out.println("flatten(): = " + flattened.infix());
						System.out.println("FLATTEN WRONG\n");
					}
					if (!isIdempotent){
						numNotIdempotent++;
						System.out.println("exp:                 = " + exp.infix());
						System.out.println("flatten():           = " + flattened.infix());
						System.out.println("flatten().flatten(): = " + flattenedTwice);
						System.out.println("NOT IDEMPOTENT\n");
					}
				}	
			} catch (org.vcell.expression.DivideByZeroException e) {
				System.out.println("FAILED: "+e.getMessage()+"\n");
				//e.printStackTrace(System.out);
				numFailed++;
			} catch (Throwable e) {
				System.out.println("FAILED: "+e.getMessage()+"\n");
				//e.printStackTrace(System.out);
				numFailed++;
			}
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}
	}
	System.out.println("test for .flatten(), "+numCorrect+" correct, "+numWrong+" wrong, "+numNotIdempotent+" not idempotent, "+numFailed+" failed, out of "+numTrials+" trials");
}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:12:55 PM)
 */
public void testParser() {
}
}