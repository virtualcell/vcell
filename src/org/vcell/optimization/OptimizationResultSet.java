package org.vcell.optimization;
/**
 * Insert the type's description here.
 * Creation date: (8/26/2002 4:22:13 PM)
 * @author: Michael Duff
 */
public class OptimizationResultSet implements java.io.Serializable {
	private String[] fieldParameterNames = null;
	private double[] fieldParameterValues = null;
	private Double fieldObjectiveFunctionValue = null;
	private OptimizationStatus optStatus = null;
	private long numObjFunctionEvaluations = 0;

/**
 * OptimizationResultSet constructor comment.
 */
public OptimizationResultSet(String[] parameterNames, double parameterValues[], Double objectiveFunctionValue, long argNumObjFuncEvals, OptimizationStatus argStatus) {
	if ((parameterNames==null && parameterValues!=null) || 
		(parameterNames!=null && parameterValues==null) || 
		(parameterNames!=null && parameterValues!=null && parameterNames.length!=parameterValues.length)){
		throw new IllegalArgumentException("number of parameter names not same as number of values in OptimizationResultSet");
	}
	this.fieldParameterNames = parameterNames;
	this.fieldParameterValues = parameterValues;
	this.fieldObjectiveFunctionValue = objectiveFunctionValue;
	this.optStatus = argStatus;
	this.numObjFunctionEvaluations = argNumObjFuncEvals;
}

/**
 * Insert the method's description here.
 * Creation date: (9/5/2005 11:48:10 AM)
 * @return java.lang.Double
 */
public java.lang.Double getObjectiveFunctionValue() {
	return fieldObjectiveFunctionValue;
}


/**
 * Insert the method's description here.
 * Creation date: (12/15/2005 11:17:32 AM)
 * @return long
 */
public long getObjFunctionEvaluations() {
	return numObjFunctionEvaluations;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 10:53:48 AM)
 * @return java.lang.String
 */
public OptimizationStatus getOptimizationStatus() {
	return optStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 12:05:02 AM)
 * @return java.lang.String[]
 */
public String[] getParameterNames() {
	return fieldParameterNames;
}


/**
 * Insert the method's description here.
 * Creation date: (8/29/2005 3:17:44 PM)
 * @return double[]
 */
public double[] getParameterValues() {
	return fieldParameterValues;
}

public void show(){
	System.out.print("OptResults: numEvals="+numObjFunctionEvaluations+", bestObjFuncValue="+fieldObjectiveFunctionValue+", status="+optStatus.toString()+", params = [");
	for (int i = 0; i < fieldParameterNames.length; i++) {
		System.out.print(fieldParameterNames[i]+"="+fieldParameterValues[i]);
		if (i<fieldParameterNames.length-1){
			System.out.print(", ");
		}
	}
	System.out.println("]");
}

}