package cbit.vcell.solver.test;

import java.util.Random;

import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.parser.*;
/**
 * Insert the type's description here.
 * Creation date: (9/11/2003 11:42:15 AM)
 * @author: Anuradha Lakshminarayana
 */
public class MathTestingUtilitiesTest {
/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 11:53:01 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		System.out.println("\n\ntesting with identical datasets\n\n");
		testOdeCompareResultSets(0.0);
		System.out.println("\n\ntesting with non-identical datasets\n\n");
		testOdeCompareResultSets(1.0);
		System.out.println("\n\ntesting Interpolated with identical datasets\n\n");
		testOdeCompareResultSetsInterpolated(0.0);
		System.out.println("\n\ntesting Interpolated with non-identical datasets\n\n");
		testOdeCompareResultSetsInterpolated(1.0);	
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 11:49:05 AM)
 * @param simCompareSummary cbit.vcell.solver.test.SimulationComparisonSummary
 */
public static void show(SimulationComparisonSummary simCompareSummary) {
	VariableComparisonSummary varSummaries[] = simCompareSummary.getVariableComparisonSummaries();
	for (int i = 0; i < varSummaries.length; i++){
		System.out.println(varSummaries[i].toString());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 11:43:26 AM)
 */
public static void testOdeCompareResultSets(double epsilon) {
	//
	// test with no interpolation required
	//
	try {
		final int numTimes = 100;
		double times[] = new double[numTimes];
		for (int i = 0; i < numTimes; i++){
			times[i] = ((double)i)/numTimes;
		}
		String names[] = { "v1", "v2", "v3" };
		Expression exps1[] = { new Expression("cos(5*t)"), new Expression("sin(10*t)"), new Expression("cos(20*t)") };
		ODESolverResultSet r1 = cbit.vcell.solver.ode.ODESolverResultSetTest.getExample(times,names,exps1);

		Expression exps2[] = { new Expression("cos(5*t)+"+epsilon), new Expression("sin(10*t)+"+epsilon), new Expression("cos(20*t)+"+epsilon) };
		ODESolverResultSet r2 = cbit.vcell.solver.ode.ODESolverResultSetTest.getExample(times,names,exps2);
		
		System.out.println("comparing identical result sets using no interpolation");
		SimulationComparisonSummary simCompareSummaryNoInterpolation = MathTestingUtilities.compareResultSets(r1,r2,names,TestCaseNew.EXACT,DataErrorSummary.DEFAULT_ABS_ERROR,DataErrorSummary.DEFAULT_REL_ERROR);
		show(simCompareSummaryNoInterpolation);
		
		System.out.println("comparing identical result sets using interpolation");
		for (int i = 0; i < 5; i ++) {
			SimulationComparisonSummary simCompareSummary = MathTestingUtilities.compareUnEqualResultSets(r1,r2,names,DataErrorSummary.DEFAULT_ABS_ERROR,DataErrorSummary.DEFAULT_REL_ERROR, i);
			show(simCompareSummary);
		}
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2003 11:43:26 AM)
 */
public static void testOdeCompareResultSetsInterpolated(double epsilon) {
	//
	// test with no interpolation required
	//
	try {
		final int numTimes1 = 100;
		double times1[] = new double[numTimes1];
		for (int i = 0; i < numTimes1; i++){
			times1[i] = ((double)i)/numTimes1;
		}
		final int numTimes2 = 3333;
		double times2[] = new double[numTimes2];
		for (int i = 0; i < numTimes2; i++){
			times2[i] = ((double)i)/numTimes2;
		}		
		String names[] = { "v1", "v2", "v3" };
		Expression exps1[] = { new Expression("cos(5*t)"), new Expression("sin(10*t)"), new Expression("cos(20*t)") };
		ODESolverResultSet r1 = cbit.vcell.solver.ode.ODESolverResultSetTest.getExample(times1,names,exps1);

		Expression exps2[] = { new Expression("cos(5*t)+"+epsilon), new Expression("sin(10*t)+"+epsilon), new Expression("cos(20*t)+"+epsilon) };
		ODESolverResultSet r2 = cbit.vcell.solver.ode.ODESolverResultSetTest.getExample(times2,names,exps2);
		
		System.out.println("comparing different result sets using interpolation");
		for (int i = 0; i < 5; i ++) {
			SimulationComparisonSummary simCompareSummary1 = MathTestingUtilities.compareUnEqualResultSets(r1,r2,names,DataErrorSummary.DEFAULT_ABS_ERROR,DataErrorSummary.DEFAULT_REL_ERROR, i);
			show(simCompareSummary1);
		}

		System.out.println("comparing different result sets using interpolation (Switching r1 & r2)");
		for (int i = 0; i < 5; i ++) {
			SimulationComparisonSummary simCompareSummary2 = MathTestingUtilities.compareUnEqualResultSets(r2,r1,names,DataErrorSummary.DEFAULT_ABS_ERROR,DataErrorSummary.DEFAULT_REL_ERROR, i);
			show(simCompareSummary2);
		}
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}

public static void testPolynomialInterpolation() {
	int num=100;
	double[] times = new double[num];
	double[] values = new double[num];
	int num2 = 50;
	double[] sampleTimes = new double[num2];

	Random random = new Random();
	for (int i = 0; i < num; i ++) {
		times[i] = i + random.nextDouble();
		values[i] = 2 * Math.pow(times[i], 5) + 1;
	}

	for (int i = 0; i < num2; i ++) {
		sampleTimes[i] = random.nextDouble() + 0.5; 
	}
	double sampledData[] = MathTestingUtilities.interpolateArray(sampleTimes, times, values, 5);
	for (int i = 0; i < num2; i ++) {
		double error = sampledData[i] - (2 * Math.pow(sampleTimes[i],5) + 1);
		System.out.println(error);
	}	
}
}
