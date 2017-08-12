/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
	private double fieldTimeAtMaxRelativeError = -1;
	private int fieldIndexAtMaxRelativeError = -1;
	
	public static double DEFAULT_ABS_ERROR = 1e-12;
	public static double DEFAULT_REL_ERROR = 1e-12;


/**
 * DataErrorSummary constructor comment.
 */
public DataErrorSummary() {
	this(null);
}


		public DataErrorSummary(cbit.vcell.parser.Expression exp) {
			exactExp = exp;
		}


public void addDataValues(double ref, double test, double time, int index,double absErrorThreshold, double relErrorThreshold) {
    if(Double.isNaN(ref) || Double.isInfinite(ref)){
    	throw new IllegalArgumentException("DataErrorSummary: Reference value is NAN or INF time="+time+" index="+index);
    }
    if(Double.isNaN(test) || Double.isInfinite(test)){
    	throw new IllegalArgumentException("DataError Summary: Test value is NAN or INF time="+time+" index="+index);    	
    }
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
    double denominator = (Math.abs(ref)*relErrorThreshold + absErrorThreshold);
    if(denominator != 0){
        double relError = Math.abs(absError / denominator);
        relSumSquaredError += relError * relError;
        nonZeroRefDataCounter++;
        if (relError >= maxRelError){
        	maxRelError = relError;
        	fieldTimeAtMaxRelativeError = time;
        	fieldIndexAtMaxRelativeError = index;
        }
    }else{
    	throw new IllegalArgumentException("Rel and Abs error threshold expression evaluated to 0");
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
public double getTimeAtMaxAbsoluteError() {
	return fieldTimeAtMaxAbsoluteError;
}


public int getIndexAtMaxRelativeError() {
	return fieldIndexAtMaxRelativeError;
}


public double getTimeAtMaxRelativeError() {
	return fieldTimeAtMaxRelativeError;
}
}
