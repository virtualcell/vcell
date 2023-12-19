package cbit.vcell.parser;

import net.sourceforge.interval.ia_math.IAException;
import net.sourceforge.interval.ia_math.RealInterval;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
@Tag("Fast")
public class IntervalNarrowingTest {
	private Expression constraint;
	
	public IntervalNarrowingTest(Expression constraintExp){
		this.constraint = constraintExp;
	}


	public static Collection constraintExpressions() throws ExpressionException {
		final int NUM_TRIALS = 10;
		final int MAX_DEPTH = 3;
		final Random random = new Random(0);
		
		
		ArrayList<Object[]> parameters = new ArrayList<Object[]>();
		for (int i=0;i<NUM_TRIALS;i++){
			parameters.add(new Object[] { ExpressionUtils.generateExpression(random, MAX_DEPTH, true) });
		}
		return parameters;
	}
	
	@ParameterizedTest
	@MethodSource("constraintExpressions")
	public void testIntervalNarrowing() throws DivideByZeroException, ExpressionException {
		final Random random = new Random(0);
		String symbols[] = constraint.getSymbols();
		if (symbols==null || symbols.length<1){
            fail("<<" + constraint.infix() + ">> doesn't have any symbols in it, can't be a proper constraint, bad test case");
		}
		SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);
		constraint.bindExpression(symbolTable);

		RealInterval initialItervals[] = new RealInterval[symbols.length];
		RealInterval values[] = new RealInterval[symbols.length];
		RealInterval prevValues[] = new RealInterval[symbols.length];
		
		//
		// try to find an interval that satisfies the constraints
		//
		final int MAX_INITIAL_VALUE_ATTEMPTS = 1000;
		int initialValueCount = 0;
		boolean bInitialValueSatisfiesConstraint = false;
		while (initialValueCount < MAX_INITIAL_VALUE_ATTEMPTS){
			//
			// first look for random intervals that satisfy constraints
			//
			for (int j = 0; j < initialItervals.length; j++){
				double middle = random.nextGaussian();
				double size = random.nextGaussian()*10;
				initialItervals[j] = new RealInterval(middle-Math.abs(size),middle+Math.abs(size));
				values[j] = new RealInterval(initialItervals[j].lo(),initialItervals[j].hi());
				prevValues[j] = new RealInterval(initialItervals[j].lo(),initialItervals[j].hi());
			}
			initialValueCount++;
			try {
				RealInterval result = constraint.evaluateInterval(values);
				result.intersect(new RealInterval(1.0)); // test if it includes "true"
				if (!result.nonEmpty()){
					bInitialValueSatisfiesConstraint = true;
					break;
				}
			}catch (FunctionDomainException e){
			}catch (IAException e){
				assertEquals(e.getMessage().contains("intersection is empty"), true, "exception indicates that intersection is empty");
			}
		}
		if (!bInitialValueSatisfiesConstraint){
			// couldn't find a random initial constraint which satisfies constraint, try full intervals
			for (int j=0;j<values.length;j++){
				initialItervals[j] = RealInterval.fullInterval();
				values[j] = RealInterval.fullInterval();
				prevValues[j] = RealInterval.fullInterval();
			}
		}
		try {
			boolean bSatisfied = constraint.narrow(values);
		}catch (FunctionDomainException e){
            fail("<<" + constraint.infix() + ">> " + getPoint(symbols, values) + " initial values result in FunctionDomainException, " + e.getMessage());
		}catch (IAException e){
            fail("<<" + constraint.infix() + ">> " + getPoint(symbols, values) + " initial value result in IAException, " + e.getMessage());
		}
		boolean bValuesChanged = true;
		boolean bValuesEverChanged = false;
		final int maxIterations = 100;
		int iteration = 0;
		//
		// try to narrow the accepted initial interval
		//
		while (bValuesChanged && iteration < maxIterations){
			iteration++;
			boolean bConstraintSatisfied = constraint.narrow(values);
			assertEquals(bConstraintSatisfied, true, "<<" + constraint.infix() + ">> " + getPoint(symbols, values) + " constraint not satisfied");
					
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
		boolean bNarrowedIsDifferent = false;

		//
		// verify that narrowed intervals are a subset of the initial intervals
		//
		for (int j = 0; j < values.length; j++){
			assertEquals(values[j].lo() >= initialItervals[j].lo(), true, "<<" + constraint.infix() + ">> narrowing bounds new.low >= orig.low");
			assertEquals(values[j].lo() <= initialItervals[j].hi(), true, "<<" + constraint.infix() + ">> narrowing bounds new.low <= orig.high");
			assertEquals(values[j].hi() >= initialItervals[j].lo(), true, "<<" + constraint.infix() + ">> narrowing bounds new.high >= orig.low");
			assertEquals(values[j].hi() <= initialItervals[j].hi(), true, "<<" + constraint.infix() + ">> narrowing bounds new.high <= orig.high");

			if (!values[j].equals(initialItervals[j])){
				bNarrowedIsDifferent = true;
			}
		}

		if (bNarrowedIsDifferent){
			//
			// verify that samples taken from the removed portions of the initial interval all fail.
			//
			final int NUM_SAMPLES = 300;
			final int MAX_EXCEPTION_COUNT = 3000;
			int successCount = 0;
			int exceptionCount = 0;
			double samples[] = new double[values.length];
			FunctionDomainException functionDomainException = null;
			while (successCount < NUM_SAMPLES && exceptionCount < MAX_EXCEPTION_COUNT){
				boolean bSamplesOutsideNarrowedIntervals = true;
				for (int k = 0; k < initialItervals.length; k++){
					samples[k] = sample(initialItervals[k],random);
					if (samples[k] >= values[k].lo() && samples[k] <= values[k].hi()){
						bSamplesOutsideNarrowedIntervals = false;
						exceptionCount++;
						break;
					}
				}
				try {
					if (bSamplesOutsideNarrowedIntervals){
						double result = constraint.evaluateVector(samples);
						assertEquals(result == 0.0, true, "<<" + constraint.infix() + ">> " + getPoint(symbols, samples) + " removed point was in solution");
						successCount++;
					}
				}catch (FunctionDomainException e){
					exceptionCount++;
					functionDomainException = e;
				}
			}
			assertEquals(successCount < NUM_SAMPLES, false, "<<" + constraint.infix() + ">> only threw FunctionDomainExceptions, exception=" + functionDomainException);
		}
	}

	private String getPoint(String[] symbols, RealInterval[] values) {
		StringBuffer buffer = new StringBuffer("[");
		for (int m = 0; m < values.length; m++){
			buffer.append(symbols[m]+" = "+values[m]);
			if (m<values.length-1){
				buffer.append(",");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}

	public String getPoint(String[] symbols, double[] samples){
		StringBuffer buffer = new StringBuffer("[");
		for (int m = 0; m < samples.length; m++){
			buffer.append(symbols[m]+" = "+samples[m]);
			if (m<samples.length-1){
				buffer.append(",");
			}
		}
		buffer.append("]");
		return buffer.toString();
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

}
