package cbit.vcell.opt;
/**
 * Insert the type's description here.
 * Creation date: (8/26/2002 4:22:13 PM)
 * @author: Michael Duff
 */
public class OptimizationResultSet implements java.io.Serializable {
	private String[] fieldParameterNames = null;
	private double[] fieldParameterValues = null;
	private Double fieldObjectiveFunctionValue = null;
	private String[] fieldSolutionNames = null;
	private double[][] fieldSolutionValues = null;
	private OptimizationStatus optStatus = null;
	private long numObjFunctionEvaluations = 0;

/**
 * OptimizationResultSet constructor comment.
 */
public OptimizationResultSet(String[] parameterNames, double parameterValues[], Double objectiveFunctionValue, long argNumObjFuncEvals, String[] solutionNames, double solutionValues[][], OptimizationStatus argStatus) {
	if ((parameterNames==null && parameterValues!=null) || 
		(parameterNames!=null && parameterValues==null) || 
		(parameterNames!=null && parameterValues!=null && parameterNames.length!=parameterValues.length)){
		throw new IllegalArgumentException("number of parameter names not same as number of values in OptimizationResultSet");
	}
	if ((solutionNames==null && solutionValues!=null) ||
		(solutionNames!=null && solutionValues==null) ||
		(solutionNames!=null && solutionValues!=null && solutionNames.length!=solutionValues[0].length)){
		throw new IllegalArgumentException("number of model variables not same as number of values in OptimizationResultSet");
	}
	this.fieldParameterNames = parameterNames;
	this.fieldParameterValues = parameterValues;
	this.fieldObjectiveFunctionValue = objectiveFunctionValue;
	this.fieldSolutionNames = solutionNames;
	this.fieldSolutionValues = solutionValues;
	this.optStatus = argStatus;
	this.numObjFunctionEvaluations = argNumObjFuncEvals;
}


/**
 * OptimizationResultSet constructor comment.
 */
public OptimizationResultSet(String[] parameterNames, double parameterValues[], Double objectiveFunctionValue, long argNumObjFuncEvals, cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet, OptimizationStatus argStatus) {
	this(parameterNames,parameterValues,objectiveFunctionValue,argNumObjFuncEvals,null,null, argStatus);
	if (odeSolverResultSet!=null){
		try {
			int numColumns = odeSolverResultSet.getDataColumnCount();
			int numRows = odeSolverResultSet.getRowCount();
			this.fieldSolutionNames = new String[numColumns];
			this.fieldSolutionValues = new double[numRows][];
			for (int i = 0; i < numRows; i++){
				fieldSolutionValues[i] = new double[numColumns];
			}
			for (int i = 0; i < numColumns; i++){
				fieldSolutionNames[i] = odeSolverResultSet.getDataColumnDescriptions()[i].getName();
				double[] columnValues = odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn(fieldSolutionNames[i]));

				for (int j = 0; j < numRows; j++){
					fieldSolutionValues[j][i] = columnValues[j];
				}
			}
		}catch (cbit.vcell.parser.ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
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


/**
 * Insert the method's description here.
 * Creation date: (8/29/2005 3:17:13 PM)
 * @return java.lang.String[]
 */
public String[] getSolutionNames() {
	return fieldSolutionNames;
}


/**
 * Insert the method's description here.
 * Creation date: (8/26/2002 4:22:41 PM)
 * @return double[]
 */
public double[] getSolutionRow(int index) {
	if (fieldSolutionValues==null){
		throw new RuntimeException("not solution data");
	}
	return (double[])fieldSolutionValues[index].clone();
}


/**
 * Insert the method's description here.
 * Creation date: (8/26/2002 4:22:41 PM)
 * @return double[]
 */
public double[] getSolutionValues(int index) {
	if (fieldSolutionValues==null){
		throw new RuntimeException("no solution data");
	}
	double[] values = new double[fieldSolutionValues.length];
	for (int i = 0; i < values.length; i++){
		values[i] = fieldSolutionValues[i][index];
	}
	return values;
}
}