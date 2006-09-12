package cbit.vcell.solver.test;
/**
 * Insert the type's description here.
 * Creation date: (3/18/2004 4:23:40 PM)
 * @author: Jim Schaff
 */
public class DataErrorSummary {
	private cbit.vcell.parser.Expression exactExp = null;
	private double maxRef = Double.NEGATIVE_INFINITY;
	private double minRef = Double.POSITIVE_INFINITY;
	private double maxAbsError = 0.0;
	private double relSumSquaredError = 0.0;
	private double maxRelError = 0.0;
	private long nonZeroRefDataCounter = 0;
	private double fieldTimeAtMaxAbsoluteError = -1;
	private int fieldIndexAtMaxAbsoluteError = -1;

/**
 * DataErrorSummary constructor comment.
 */
public DataErrorSummary() {
	this(null);
}


		public DataErrorSummary(cbit.vcell.parser.Expression exp) {
			exactExp = exp;
		}


public void addDataValues(double ref, double test, double time, int index) {
    maxRef = Math.max(maxRef, ref);
    minRef = Math.min(minRef, ref);
    double absError = Math.abs(ref - test);
    //maxAbsError = Math.max(maxAbsError, absError);
    //
    // more involved operation upon new maxAbsError, store coordinate (time,x,y,z) of max Absolute error ... by copy
    //
    if (absError >= maxAbsError){
	    maxAbsError = absError;
    	fieldTimeAtMaxAbsoluteError = time;
    	fieldIndexAtMaxAbsoluteError = index;
    }
    if (ref != 0.0) {
        double relError = Math.abs(absError / ref);
        maxRelError = Math.max(maxRelError, relError);
        relSumSquaredError += relError * relError;
        nonZeroRefDataCounter++;
    }
}


/**
 * Insert the method's description here.
 * Creation date: (3/18/2004 4:31:11 PM)
 * @return cbit.vcell.parser.Expression
 */
public cbit.vcell.parser.Expression getExactExp() {
	return exactExp;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 7:06:55 PM)
 * @return double[]
 */
public int getIndexAtMaxAbsoluteError() {
	return fieldIndexAtMaxAbsoluteError;
}


public double getL2Norm() {
    if (nonZeroRefDataCounter == 0) {
        return 0.0;
    }
    return Math.sqrt(relSumSquaredError / nonZeroRefDataCounter);
}


public double getMaxAbsoluteError() {
    return maxAbsError;
}


public double getMaxRef() {
    return maxRef;
}


public double getMaxRelativeError() {
    return maxRelError;
}


public double getMinRef() {
    return minRef;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 7:06:55 PM)
 * @return double[]
 */
public double getTimetMaxAbsoluteError() {
	return fieldTimeAtMaxAbsoluteError;
}
}