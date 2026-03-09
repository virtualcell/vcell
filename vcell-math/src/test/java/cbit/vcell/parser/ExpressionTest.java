package cbit.vcell.parser;

import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.FunctionArgType;
import cbit.vcell.units.VCUnitSystem;
import net.sourceforge.interval.ia_math.RealInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@Tag("Fast")
public class ExpressionTest {
	private static final boolean FAIL_ON_FIRST = true;
	private static final boolean ENSURE_SAME_SEED = true; // change me
	private static final long SAME_SEED = 1772035787937L;
	private static File tempDir;
	private static int numberOfTrials;
	private static int standardDepth;
	private static long randomSeed;
//	public static void main(java.lang.String[] args) {
//		int num = 5000;
//
//		if (args.length > 0) {
//			num = Integer.parseInt(args[0]);
//		}
//		testEval(num);
////		testCopyTree(num);
//	}

	private static Set<String> skipInfixList = new HashSet<>(List.of(
		"floor((pow(asinh(0.10118387985713251),cos(id_0)) * !(cot(id_0)) * min(csch(0.9837650351250335), coth(id_6))))",
		" - (min(cot(id_0), csch(0.36737356253208997)) * atan2((0.7233117778683602 >= 0.9759958353333693), (0.1299551891086368 * id_7 * 0.7165699878023953)) / abs(id_1))",
		"sech( - (coth(0.028177547796530367) ^ cot(id_1)))",
		"coth(((sinh(0.6449666197606081) ^ (id_4 * id_8 * id_9)) + cot((id_3 < 0.2261011754280382)) + sech(abs(0.8120086721536708))))",
		"(((cosh(id_3) * log(0.5598410965557573) * asin(0.015550241603964676)) * sin(floor(id_0)) * cosh(max(id_1, 0.585221823072032))) >= cot(floor((id_9 * id_2 * 0.30343841532874716))))",
		"coth(floor(cot(abs(id_0))))",
		" - factorial(( - id_2 / id_1 * ceil(id_5)))",
		"csch(exp(csc((id_2 * id_2 * id_9))))",
		"atan((tan(ceil(0.0)) || (cot(id_0) + asinh(0.5597772404451554) + sec(id_4))))",
		"cot(factorial( - (0.20783348997951046 && id_5)))",
		" - cos((cos(id_1) ^ cot(id_0)))",
		"atan(abs(cot(floor(id_5))))",
		"coth(floor(cot((id_0 * 0.10280284481700341 * 0.3190467590050531))))",
		" - (( - id_1 * acos(0.9408318720601397) * cot(id_0)) && asinh(min(id_6, 0.130136440506475)))",
		"coth(factorial(floor(log(id_4))))",
		"(cot(!(sech(id_3))) ^ min( - atan2(0.7870190039110462, id_2), ((id_7 * id_0 * 0.10143937822636417) * min(id_4, id_2) * sech(0.5569515469432404))))",
		"cot(exp(coth((0.01722292952879423 ^ 0.7576553654648215))))",
		"cosh(min(cot((id_7 * id_0 * 0.9871116728260001)),  - !(id_6)))",
		" - acos(((0.01949787088472099 * id_1 * id_9) ^ cot(id_0)))",
		"factorial(min(tanh((id_9 ^ id_1)), floor( - 0.5153523376029631)))"
	));


	@BeforeAll
	public static void setUp() throws IOException {
		ExpressionTest.numberOfTrials = 5000; // Must be positive integer
		ExpressionTest.standardDepth = 4; // Must be positive integer
		ExpressionTest.randomSeed = ENSURE_SAME_SEED ? SAME_SEED : System.currentTimeMillis();
		ExpressionTest.tempDir = Files.createTempDirectory("VCellExpressionTest").toFile();
	}

	@BeforeEach
	public void setUpEach(){
		String configuration = String.format(
				"Test Configuration:\n\t%d Trials\n\tExpected Depth: %d\n\tRandom Seed: %d",
				ExpressionTest.numberOfTrials,
				ExpressionTest.standardDepth,
				ExpressionTest.randomSeed
		);
		System.err.println(configuration);
	}

	@Test
	public void testPythonInfix() throws InterruptedException {
		java.util.Random r = new java.util.Random(ExpressionTest.randomSeed);
		String[] ids = {"id_0", "id_1", "id_2", "id_3",
				"id_4", "id_5", "id_6", "id_7", "id_8", "id_9"};
		SimpleSymbolTable symbolTable = new SimpleSymbolTable(ids);

		double[] v1 = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9 };
		Map<Expression, Double> expressionToResults = new LinkedHashMap<>();
		List<String> failingInfixes = new ArrayList<>();
		List<String> recordedFailures = new ArrayList<>();
		System.out.println("Beginning Expression Generation...");
		ProgressTracker generationTracker = ProgressTracker.generateProgressTracker(ExpressionTest.numberOfTrials);
		ScheduledFuture<?> generationTrackerStatus = generationTracker.startTracker();
		for (int i = 0; i < ExpressionTest.numberOfTrials; i++) {
			try {
				Expression exp = ExpressionUtils.generateExpression(r, ExpressionTest.standardDepth, false);
				// Test expression for validity
				if (ExpressionTest.skipInfixList.contains(exp.infix())) throw new UnsupportedOperationException("skipping...");
				if (exp.infix().contains("0.0 ^")) throw new UnsupportedOperationException("Not solvable");

				try {
					ExpressionUtils.functionallyEquivalent(exp,exp);
				}catch (Exception e){
					throw new UnsupportedOperationException("Not solvable");
				}
				try {
					exp.evaluateConstantSafe();
				}catch (FunctionDomainException | DivideByZeroException | ArithmeticException e2) {
					throw new UnsupportedOperationException("Not solvable");
				}catch (ExpressionException ignored){}

				exp.bindExpression(symbolTable);
				// check for duplicates
				if (expressionToResults.containsKey(exp)) throw new ExpressionException("dupe");
				// check it's solvable
				double result = exp.evaluateVector(v1);
				if (Double.isInfinite(result)) throw new ExpressionArithmeticException("expression out of range");
				if (Double.isNaN(result)) throw new ExpressionArithmeticException("Unexpected NaN detected");

				// confirm expression is python safe
				exp.getRootNode().infixString(SimpleNode.LANGUAGE_PYTHON);

				// All good to go!
				expressionToResults.put(exp, result);
				generationTracker.incrementProgress();
			} catch (ExpressionException | UnsupportedOperationException e) {
				// We just need valid, solved expressions, if we fail, we try again
				i--;
			}
		}
		generationTracker.waitUntilTrackerIsDone();
		System.out.println("Finished Expression Generation.");

		int maxJobs = expressionToResults.size();
		System.out.println("Starting Python Equivalence Testing...");
		ProgressTracker resultsTracker = ProgressTracker.generateProgressTracker(maxJobs);
		ScheduledFuture<?> resultsTrackerStatus = resultsTracker.startTracker();
		boolean trackerEnabled = true;
		double expectedResult = Double.NaN, actualResult = Double.NaN, resultDiff = Double.NaN, threshold = Double.NaN;
		String vcellInfixExpression = "<none>", pythonInfixExpression = "<none>";
		try (PythonInterpreter interpreter = new PythonInterpreter()) {
			interpreter.exec("import math");
			for (int i = 0; i < ids.length; i++) interpreter.set(ids[i], v1[i]);
			for (Expression exp : expressionToResults.keySet()) {
				if (trackerEnabled && resultsTrackerStatus.isDone()) { // uh-oh, shouldn't be done yet!
					trackerEnabled = false;
					try{
						resultsTrackerStatus.get();
					} catch (CancellationException e){
						System.err.println("Results tracker was cancelled:\n" + e.getMessage());
					} catch (InterruptedException e){
						System.err.println("Results tracker was interrupted:\n" + e.getMessage());
					} catch (ExecutionException e) {
						System.err.println("Results tracker suffered an exception:\n" + e.getMessage());
					}
				}
				try {
					expectedResult = expressionToResults.get(exp);
					try{
						vcellInfixExpression = exp.getRootNode().infixString(SimpleNode.LANGUAGE_DEFAULT);
						pythonInfixExpression = exp.getRootNode().infixString(SimpleNode.LANGUAGE_PYTHON);
						actualResult = (Double) interpreter.eval(pythonInfixExpression).__tojava__(Double.class);
					} catch (PyException | ArithmeticException | UnsupportedOperationException e){
						// try flattened
						Expression flatExp;
						try {
							flatExp = exp.flatten();
						} catch (ExpressionException ee){
							throw e;
						}
						vcellInfixExpression = flatExp.getRootNode().infixString(SimpleNode.LANGUAGE_DEFAULT);
						pythonInfixExpression = flatExp.getRootNode().infixString(SimpleNode.LANGUAGE_PYTHON);
						actualResult = (Double) interpreter.eval(pythonInfixExpression).__tojava__(Double.class);
					}

					resultDiff = Math.abs(expectedResult - actualResult);
					threshold = this.getComparisonResult(expectedResult, actualResult);
					if (resultDiff <= threshold) resultsTracker.incrementProgress();
					else throw new ExpressionArithmeticException("Evaluations do not match!");
				} catch (PyException | ExpressionArithmeticException | UnsupportedOperationException e) {
					failingInfixes.add(exp.infix());
					boolean isArithmeticException = e instanceof ExpressionArithmeticException;
					boolean isPyException = e instanceof PyException;
					String errorType = isArithmeticException ? "Mismatch between expressions and results:\n" : "Unable to parse valid VCell Expression in Python:\n";
					String error = errorType + String.format("\tProgress: %s\n", resultsTracker.currentProgress) +
							String.format("\tVCell Original Infix: %s\n", exp.infix()) +
							String.format("\tVCell Flat Infix: %s\n", vcellInfixExpression) +
							String.format("\tExpected: %s\n", expectedResult) +
							String.format("\tPython Infix: %s\n", pythonInfixExpression) +
							(isPyException ? String.format("\tPython Exception: %s\n", e.getMessage()) : "") +
							(isArithmeticException ? String.format("\tActual: %s\n", actualResult): "") +
							(isArithmeticException ? String.format("\tDiff: %s\n", resultDiff): "") +
							(isArithmeticException ? String.format("\tThreshold: %s\n", threshold) : "");

					resultsTracker.incrementProgress();
					RuntimeException re = new RuntimeException(error, e);
					if (ExpressionTest.FAIL_ON_FIRST) throw re;

					if (recordedFailures.size() != Integer.MAX_VALUE){
						recordedFailures.add(re.toString());
						continue;
					}
					resultsTracker.declareFailure();
					System.err.println("There is no more room to declare failures!");
				}
			}
		}
		resultsTracker.waitUntilTrackerIsDone();
		if (recordedFailures.isEmpty()) System.out.println("All models pass!");
		else {
			String resultsString = String.format("%d / %d models pass!", maxJobs - recordedFailures.size(), maxJobs);
			System.out.println(resultsString);
			for (String failedInfix: failingInfixes) System.out.println("\"" + failedInfix + "\",");
			String uniqueTestId = String.format("seed_%d__%d_outOf_%d", randomSeed, maxJobs - recordedFailures.size(), maxJobs);
			String targetFile = String.format("pythonInfixTest_%s.txt", uniqueTestId);
			File resultsFile = null;
			try {
				resultsFile = Path.of("src/test/reports", targetFile).toFile().getCanonicalFile();
				if (resultsFile.exists()) if(!resultsFile.delete()) throw new IOException("Unable to delete existing file!");
				if (!resultsFile.createNewFile()) throw new IOException("Unable to create file!");
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultsFile))){ // record what went wrong to file:
					bw.write(resultsString);
					for (String errMsg : recordedFailures) {
						bw.write("\n" + errMsg);
					}
				}
			} catch (IOException e){
				System.out.println("Unable to export failures to file` " + resultsFile + "`: " + e.getMessage());
			}
		}
	}

	//@Test
	public void testCopyTree() {
		java.util.Random r = new java.util.Random(ExpressionTest.randomSeed);
		try {
			for (int j = 0; j < 1; j ++) {
				//java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileOutputStream("D:\\VCell\\Testing\\ExpressionParser\\ExpParserTest" + j + ".txt"));
				File targetFile = new File(ExpressionTest.tempDir, String.format("ExpParserTest_copyTree%d.cpp", j));
				java.io.PrintWriter pw2 = new java.io.PrintWriter(new java.io.FileOutputStream(targetFile));
				pw2.println("#include <math.h>");
				pw2.println("#include <string>");
				pw2.println("#include <iostream>");
				pw2.println("using namespace std;");

				pw2.println("#include \"Expression.h\"");
				pw2.println("using VCell::Expression;");
				pw2.println();
				
				String[] undefinedFunctions = {
						"csc",		
						"cot",		
						"sec",		
						"acsc",		
						"acot",		
						"asec",		
//						"sinh",		
//						"cosh",		
//						"tanh",		
						"csch",		
						"coth",		
						"sech",		
						"asinh",	
						"acosh",	
						"atanh",	
						"acsch",	
						"acoth",	
						"asech",	
						"factorial"	
				};
				for (int k = 0; k < undefinedFunctions.length; k ++) {
					pw2.println("#define " + undefinedFunctions[k] + "(a) (MathUtil::" + undefinedFunctions[k] + "(a))");
				}
				pw2.println("#define abs(a) fabs(a)");
				pw2.println();
				
				pw2.println("void main() {");
				pw2.println("\tExpression* expression, *copyExpression;");
				pw2.println("\tstring infix, copyInfix;");
				pw2.println("\tstring str;");

				for (int d = 1; d < ExpressionTest.standardDepth; d++){
					for (int i = 0; i < ExpressionTest.numberOfTrials; i ++){
						Expression exp = ExpressionUtils.generateExpression(r, d, false);

						try {
							pw2.println("\t// " + i);
							pw2.println("\tstr = string(\"" + exp.infix() + ";\");");
							pw2.println("\texpression = new Expression(str);");
							pw2.println("\tcopyExpression = new Expression(expression);");
							pw2.println("\tinfix = expression->infix();");
							pw2.println("\tcopyInfix = copyExpression->infix();");
							pw2.println("\tif (infix != copyInfix) {");
							pw2.println("\t\tcout << " + i + " << \" different\" << endl;");
							pw2.println("\t\tcout << \"infix : \" << infix << endl;");
							pw2.println("\t\tcout << \"copyInfix : \" << copyInfix << endl;");
							pw2.println("\t} else {");
							pw2.println("\t\tcout << " + i + " << \" same\" << endl;");
							pw2.println("\t}");
							pw2.println();
						} catch (Exception ex) {
							System.out.println("!!!!!" + ex.getMessage());
						}
					}
				}			
				//pw.close();
				pw2.println("}");
				pw2.close();
			}
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}

	}

@Test
public void testEval() {
	try {
			
		java.util.Random r = new java.util.Random(ExpressionTest.randomSeed);
		String ids[] = {"id_0", "id_1", "id_2", "id_3", 
				"id_4", "id_5", "id_6", "id_7", "id_8", "id_9"};
		SimpleSymbolTable symbolTable = new SimpleSymbolTable(ids);		

		double v1[] = {0,1,2,3,4,5,6,7,8,9 };

		for (int j = 0; j < 1; j ++) {
			//java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileOutputStream("D:\\VCell\\Testing\\ExpressionParser\\ExpParserTest" + j + ".txt"));
			File targetFile = new File(ExpressionTest.tempDir, String.format("ExpParserTest_eval%d.cpp", j));
			java.io.PrintWriter pw2 = new java.io.PrintWriter(new java.io.FileOutputStream(targetFile));
			pw2.println("#include \"Windows.h\"");
			pw2.println("#include \"ExpressionTest.h\"");
			pw2.println("#include \"MathUtil.h\"");
			pw2.println("#include \"SimpleSymbolTable.h\"");
			pw2.println("#include <math.h>");
			pw2.println("#include <string>");
			pw2.println("#include <iostream>");
			pw2.println("using namespace std;");
			pw2.println();
			
			String[] undefinedFunctions = {
					"csc",		
					"cot",		
					"sec",		
					"acsc",		
					"acot",		
					"asec",		
//					"sinh",		
//					"cosh",		
//					"tanh",		
					"csch",		
					"coth",		
					"sech",		
					"asinh",	
					"acosh",	
					"atanh",	
					"acsch",	
					"acoth",	
					"asech",	
					"factorial"	
			};
			for (int k = 0; k < undefinedFunctions.length; k ++) {
				pw2.println("#define " + undefinedFunctions[k] + "(a) (MathUtil::" + undefinedFunctions[k] + "(a))");
			}
			pw2.println("#define abs(a) fabs(a)");
			pw2.println();
			
			pw2.println("void main() {");
			for (int k = 0; k < 10; k ++) {
				pw2.println("\tdouble id_" + k + " = " + k + ";");
			}

			pw2.println("\tdouble r = 0;");
			pw2.println("\tstring ids[] = {\"id_0\", \"id_1\", \"id_2\", \"id_3\", \"id_4\", \"id_5\", \"id_6\", \"id_7\", \"id_8\", \"id_9\"};");
			pw2.println("\tSimpleSymbolTable* symbolTable = new SimpleSymbolTable(ids, 10);");
			pw2.println("\tdouble values[10] = {0,1,2,3,4,5,6,7,8,9};");
			pw2.println();

			for (int d = 1; d < ExpressionTest.standardDepth; d++) {
				for (int i = 0; i < ExpressionTest.numberOfTrials; i++) {
					Expression exp = ExpressionUtils.generateExpression(r, 4, false);
					exp.bindExpression(symbolTable);

					try {
						double result = exp.evaluateVector(v1);
						pw2.println("\t// " + i);
						pw2.println("\tr = " + exp.infix_C() + ";");
						pw2.println("\tExpressionTest::testParser(" + i + ", \"" + result + "\", r, \"" + exp.infix() + "\", symbolTable, values);");
						pw2.println();
					} catch (Exception ex) {
						System.out.println("!!!!!" + ex.getMessage());
					}
				}
			}
			//pw.close();
			pw2.println("}");
			pw2.close();
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}

@Test
public void testNumExprGeneration() throws ExpressionException {
	Expression expr1 = new Expression("(2.0 && id_2) * 3.3 / acsc(1 + id_1) * pow(2, 3.2 ^ 2)");
	String numExpr1 = expr1.infix_NumExpr();
	Assertions.assertEquals("(where(((0.0!=2.0) & (0.0!=id_2)), 3.3 / (arcsin(1.0/((1.0 + id_1)))) * ((2.0)**(((3.2)**(2.0)))), 0.0))", numExpr1);

	Expression expr2 = new Expression("(1.2 * 2.2) * logbase(3.3) * 2.0");
	String numExpr2 = expr2.infix_NumExpr();
	Assertions.assertEquals("((1.2 * 2.2) * (1.0/log(3.3)) * 2.0)", numExpr2);

	Expression expr3 = new Expression("(1.2 || 2.2) * (3.3 && !(2.0 && 0.0))");
	String numExpr3 = expr3.infix_NumExpr();
	Assertions.assertEquals("(((0.0!=1.2) | (0.0!=2.2)) & ((0.0!=3.3) & (0.0!=~(((0.0!=2.0) & (0.0!=0.0))))))", numExpr3);
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
//@Test
public void testBig() throws ExpressionException {
		

String expString = "(((ALPHA * ((ip3r_coeff * ((4.0 * pow((RIC / ch_dens), 3.0))"+
						" - (3.0 * pow((RIC / ch_dens), 4.0))) * (Ca_ER - C)) + (LEAK * (Ca_ER - C)) "+
						" - (bindingFluxFactor * ((ss1 * C * P) - (s1 * PC) + (ss2 * C * PC))))) - (bb1 * B * C)"+
						" - (b1 * B) + (b1 * slow_buffer_density)) / ((mobile_buff_dens * mobile_buff_K_value"+
						" / pow((mobile_buff_K_value + C), 2.0)) + fixed_buffer_bindingRatio + 1.0));";
//String expString = "(( - VmaxANT / (1.0 + (KeADP / ADP_OUTSIDE) + (ATP_OUTSIDE / KeATP * KeADP / ADP_OUTSIDE) + (KiATP / ATP_INSIDE))) + VmaxSYNTH);";

		Expression origExp = new Expression("RIC;");
		Expression newExp = new Expression("0.0;");
		long before = 0;
		long after = 0;
		
		before = System.currentTimeMillis();
		Expression exp = new Expression(expString);
		after = System.currentTimeMillis(); 
		long parseTime = after-before;
System.out.println("time for parsing = "+parseTime+" ms");
System.out.println(exp);
		
		before = System.currentTimeMillis();
		Expression flat = exp.flatten();
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
		flat.getSubstitutedExpression(origExp,newExp);
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
		Expression exactD_C = flat.differentiate("C");
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
}

//@Test
public void testDifferentiation() throws ExpressionException {
	ExpressionTest.testDifferentiate(ExpressionTest.numberOfTrials, ExpressionTest.standardDepth, ExpressionTest.randomSeed);
}

/**
 * Insert the method's description here.
 * Creation date: (12/27/2002 2:14:48 PM)
 */
public static void testDifferentiate(int numTrials, int depth, long seed) throws ExpressionException {
	final double relativeTolerance = 1e-7;
	final double absoluteTolerance = 1e-10;
	int numCorrect = 0;
	int numWrong = 0;
	int numDerivatives = 0;
	java.util.Random random = new java.util.Random(seed);
	for (int i = 0; i < numTrials; i++) {
			cbit.vcell.parser.Expression exp = cbit.vcell.parser.ExpressionUtils.generateExpression(random, depth, false);
			cbit.vcell.parser.Expression flattened = null;
				//
				// test against identifier that is not present in expression
				//
				numDerivatives++;
				String dummySymbol = "abc123";
				Expression diff = exp.differentiate(dummySymbol);
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

					for (int j = 0;symbols!=null && j < symbols.length; j++){
						numDerivatives++;
							diff = exp.differentiate(symbols[j]);
							try {
								diff = diff.flatten();
							}catch (DivideByZeroException e){
								System.out.println("ExpressionTest.testDifferentiate(), DivByZero while flattening '"+e.getMessage()+"', \nexp='"+diff+"'\n\n");
								continue;
							}
							boolean isSame = cbit.vcell.parser.ExpressionUtils.derivativeFunctionallyEquivalent(exp,symbols[j],diff,relativeTolerance,absoluteTolerance);
							if (isSame) {
								numCorrect++;
							} else {
								numWrong++;
								System.out.println("f() = "+exp);
								System.out.println("D f()/D("+symbols[j]+") = "+diff);
								System.out.println("[f("+symbols[j]+"+delta)-f("+symbols[j]+"-delta)]/(2*delta)  !=  D f(k)/D("+symbols[j]+")\n\n\n");
							}
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
			cbit.vcell.parser.Expression exp = cbit.vcell.parser.ExpressionUtils.generateExpression(random, depth, bIsConstraint);
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

//@Test
public void testNarrowingEvaluation(){
	ExpressionTest.testEvaluateNarrowing(ExpressionTest.numberOfTrials, ExpressionTest.standardDepth, ExpressionTest.randomSeed);
}

/**
 * This method was created in VisualAge.
 */
public static void testEvaluateNarrowing(int numTrials, int depth, long seed) {
	try {
		java.util.Random random = new java.util.Random(seed);

		for (int i = 0; i < numTrials; i++){
			Expression constraint = ExpressionUtils.generateExpression(random,depth,true);
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
		Expression exps[] = {
			(new Expression("a*b==c;")).flatten(),
			(new Expression("c<2*a;")).flatten(),
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
//@Test
public void testEvaluateVector() throws ExpressionException{
	Expression exp = new Expression("a+b/c;"); // Note lack of parenthesis

	SimpleSymbolTable simpleSymbolTable = new SimpleSymbolTable(new String[] { "a", "b", "c" });

	double v1[] = { 0,1,2 };
	double v2[] = { 3,4,5 };
	double expectedResult1 = v1[0] + v1[1] / v1[2]; // Note lack of parenthesis
	double expectedResult2 = v2[0] + v2[1] / v2[2]; // Note lack of parenthesis
	exp.bindExpression(simpleSymbolTable);
	double actualResult1 = exp.evaluateVector(v1);
	double actualResult2 = exp.evaluateVector(v2);
	System.out.println("evaluate '"+exp+"' with v1=["+v1[0]+","+v1[1]+","+v1[2]+"]: result="+actualResult1);
	Assertions.assertEquals(expectedResult1,actualResult1);
	System.out.println("evaluate '"+exp+"' with v2=["+v2[0]+","+v2[1]+","+v2[2]+"]: result="+actualResult2);
	Assertions.assertEquals(expectedResult2,actualResult2);
}

//@Test
public void testEvaluateUserDefinedFunctions() throws ExpressionException {
	ExpressionTest.testUserDefinedFunctions(new VCUnitSystem() {});
}

public static void testUserDefinedFunctions(VCUnitSystem vcUnitSystem) throws ExpressionException {
		SimpleSymbolTableFunctionEntry steFunc = new SimpleSymbolTableFunctionEntry("myfunc", new String[] { "arg1", "arg2" }, new FunctionArgType[] { FunctionArgType.NUMERIC, FunctionArgType.NUMERIC }, new Expression("arg1+arg2"), vcUnitSystem.getInstance_DIMENSIONLESS(), null);
		SimpleSymbolTable symbolTable = new SimpleSymbolTable(new String[] { "a", "b", "c" }, new SymbolTableFunctionEntry[] { steFunc }, null);
		
		bindFlattenAndPrint("4*myfunc(4,5)",  symbolTable);
		bindFlattenAndPrint("4*myfunc(2+3+1/3,5)",  symbolTable);
		bindFlattenAndPrint("4*myfunc(a+b,5)",  symbolTable);
		bindFlattenAndPrint("4*myfunc('a',b)",  symbolTable);
		bindFlattenAndPrint("4*myfunc('a',b)",  symbolTable);
		bindFlattenAndPrint("4*myfunc(myfunc(1,2),b)",  symbolTable);
}

private static void bindFlattenAndPrint(String expStr, SymbolTable symbolTable) throws ExpressionException{
	try {
		Expression exp = new Expression(expStr);
		exp.bindExpression(symbolTable);
		System.out.println(exp.infix()+" ==> "+exp.flatten().infix());
	}catch (Exception e){
		System.out.println("failed expression: \""+expStr+"\",  reason: "+e.getMessage());
	}
	System.out.println();
}

//@Test
public void testFlatten() throws ExpressionException {
	ExpressionTest.testFlatten(ExpressionTest.numberOfTrials, ExpressionTest.standardDepth, ExpressionTest.randomSeed);
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
			Expression exp = ExpressionUtils.generateExpression(random, depth, false);
			try {
				Expression flattened = exp.flatten();
				boolean isSame = ExpressionUtils.functionallyEquivalent(exp, flattened, false);
				Expression flattenedTwice = exp.flatten().flatten();
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
			} catch (cbit.vcell.parser.DivideByZeroException e) {
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

	@Test
	public void testLinearity() throws ExpressionException {
		Expression[][] tests = new Expression[][] {
				{ new Expression("k*abc"), new Expression("k"), new Expression("abc") },
				{ new Expression("((1.0E-12 * KMOLE * s1) * c0 * (1.0 / 1000.0))"), new Expression("c0"), new Expression("((1.0E-12 * KMOLE * s1) / 1000.0)") },
				{ new Expression("((1.0E-12 * KMOLE * s1) * c0 * (1.0 / 1000.0))"), new Expression("c2"), null },
		};

		for (Expression[] test : tests) {
			Expression exp = test[0];
			String multiplierSymbol = test[1].infix();
			Expression expectedFactor = test[2];
			Expression computedFactor = ExpressionUtils.getLinearFactor(new Expression(test[0]), multiplierSymbol);
			if (expectedFactor == null){
				assertTrue(computedFactor == null, "expectedFactor is null, computedFactor was not null");
			}else {
				assertTrue(computedFactor != null, "expectedFactor was '" + expectedFactor.infix() + "', computedFactor was null");
				boolean equiv = ExpressionUtils.functionallyEquivalent(expectedFactor, computedFactor);
				assertTrue(equiv, expectedFactor.infix() + " != " + computedFactor.infix());
			}
		}

        Assertions.assertTrue(ExpressionUtils.functionallyEquivalent(
                new Expression("((1.0E-12 * KMOLE * s1) * (1.0 / 1000.0))"),
                ExpressionUtils.getLinearFactor(new Expression("((1.0E-12 * KMOLE * s1) * c0 * (1.0 / 1000.0))"), "c0")));
        Assertions.assertTrue(ExpressionUtils.functionallyEquivalent(
                new Expression("((0.001 * Vmax2_bleaching2 * rfB * Laser) * ((t > 1.0) && (t < 1.5)))"),
                ExpressionUtils.getLinearFactor(new Expression("((0.001 * Vmax2_bleaching2 * rfB * Laser) * ((t > 1.0) && (t < 1.5)))*Nuc"), "Nuc")));
	}

	public static class FunctionalEquivTestData {
		Expression exp1;
		Expression exp2;
		boolean shouldPass;

		public FunctionalEquivTestData(Expression exp1, Expression exp2, boolean shouldPass) {
			this.exp1 = exp1;
			this.exp2 = exp2;
			this.shouldPass = shouldPass;
		}
	}

	@Test
	public void test_functional_equivalence_with_functionSTEs() throws ExpressionException {
		final String infix1 = "((CLEC2_Raft * ((0.42857142857142855 * Syk_A) + (1.806642537E8 * CLEC2_Syk_A / " +
				"(4.1538E18 * vcRegionVolume('subdomain1') / (1.0E10 * vcRegionArea('subdomain0_subdomain1_membrane'))))" +
				" + (0.35 * SFK_NF) + (1.2044283579999998E8 * CLEC2_SFK_A / (1.806E18 * vcRegionVolume('subdomain1')" +
				" / (1.0E10 * vcRegionArea('subdomain0_subdomain1_membrane')))))) - (0.07941176470588236 * CLEC2_P * DUSP3_A)" +
				" + (0.2 * CLEC2_Syk) - (CLEC2_P * Syk) + CLEC2_SFK_A - (CLEC2_P * SFK_NF))";
		final String infix2 = "((((0.42857142857142855 * Syk_A) + (1.806642537E8 * CLEC2_Syk_A / " +
				"(4.1538E18 * vcRegionVolume('subdomain1') / (1.0E10 * vcRegionArea('subdomain0_subdomain1_membrane')))) " +
				"+ (0.35 * SFK_NF) + (1.2044283579999998E8 * CLEC2_SFK_A / (1.806E18 * vcRegionVolume('subdomain1') " +
				"/ (1.0E10 * vcRegionArea('subdomain0_subdomain1_membrane'))))) * CLEC2_Raft) - (0.07941176470588236 * DUSP3_A * CLEC2_P) " +
				"- ((CLEC2_P * Syk) - (0.2 * CLEC2_Syk)) - ((CLEC2_P * SFK_NF) - CLEC2_SFK_A))";
		FunctionalEquivTestData[] tests = new FunctionalEquivTestData[]{
				new FunctionalEquivTestData(new Expression(infix1), new Expression(infix1), true),
				new FunctionalEquivTestData(new Expression(infix1), new Expression(infix2), true),
				new FunctionalEquivTestData(
						new Expression("vcRegionVolume('subdomain1')"),
						new Expression("vcRegionVolume('subdomain1')"),
						true),
				new FunctionalEquivTestData(
						new Expression("vcRegionVolume('subdomain1')"),
						new Expression("vcRegionVolume('subdomain2')"),
						false),
		};

		for (FunctionalEquivTestData test : tests) {
			boolean passed = ExpressionUtils.functionallyEquivalent(test.exp1, test.exp2);
			if (test.shouldPass) {
				assertTrue(passed, "exps should have been equivalent: exp1='" + test.exp1.infix() + "', exp2='" + test.exp2.infix() + "'");
			}else{
				assertFalse(passed, "exps should not have been equivalent: exp1='" + test.exp1.infix() + "', exp2='" + test.exp2.infix() + "'");
			}
		}
	}


/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:12:55 PM)
 */
public void testParser() {
}

private double getComparisonResult(double expectedResult, double actualResult){
//	final double OFFSET = 2.225e-308;
//	if (expectedResult == actualResult) return 0.0;
//	double denominator = expectedResult + actualResult;
//	double numerator = 2*(actualResult - expectedResult);
//	if (0.0 != denominator) return Math.abs(numerator/denominator);
//	else return Math.abs((numerator + OFFSET)/(denominator + 2*OFFSET));
	return this.getComparisonResult(expectedResult, actualResult, 1e-6, 1e-6);
}

private double getComparisonResult(double expectedResult, double actualResult, double absTolerance, double relTolerance){
	return Math.max(absTolerance, relTolerance * Math.max(Math.abs(expectedResult), Math.abs(actualResult)));

}

static class ProgressTracker {
	final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	final private AtomicBoolean writerFailed = new AtomicBoolean(false);
	final private AtomicReference<Integer> currentProgress = new AtomicReference<>(0);
	final private AtomicReference<Integer> previousProgress = new AtomicReference<>(-1);
	final private AtomicReference<Integer> goal;
	final private Runnable trackerCheck;

	public static ProgressTracker generateProgressTracker(int goal){

		return new ProgressTracker(goal);
	}

	public java.util.concurrent.ScheduledFuture<?> startTracker(){
		return this.executorService.scheduleAtFixedRate(this.trackerCheck, 0, 500, TimeUnit.MILLISECONDS);
	}

	public void updateProgress(int newProgress){
		try{
			if (newProgress < 0) throw new IllegalArgumentException("newProgress must be non-negative");
			this.currentProgress.set(newProgress);
		} catch (Exception e){
			this.writerFailed.set(true);
			throw e;
		}
	}

	public void waitUntilTrackerIsDone() throws InterruptedException {
		if(this.executorService.awaitTermination(5, TimeUnit.SECONDS)) return;
		System.err.println("Timed-out waiting for tracker to finish");
	}

	public void incrementProgress(){
		this.updateProgress(this.currentProgress.get() + 1);
	}

	public void declareFailure(){
		this.writerFailed.set(true);
	}

	private ProgressTracker(int goal) {
		if (goal <= 0) throw new IllegalArgumentException("goal must be positive");
		this.goal = new AtomicReference<>(goal);
		this.trackerCheck = ()->{
			if (this.writerFailed.get()) {
				this.executorService.shutdown();
				return;
			}

			int newProgress = this.currentProgress.get();
			if (this.previousProgress.get() == newProgress) return;
			int target = this.goal.get();
			this.previousProgress.set(newProgress);
			double progressPercent = 100 * newProgress / (double) this.goal.get();
			System.out.printf("Progress: %s%% (%d/%d)%n", progressPercent, newProgress, target);

			if ((1.0 * newProgress) / target >= 1.0) this.executorService.shutdown();
		};
	}
}

}