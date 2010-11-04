package cbit.vcell.opt;

import org.vcell.optimization.OptSolverResultSet;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;

/**
 * Insert the type's description here.
 * Creation date: (8/26/2002 4:22:13 PM)
 * @author: Michael Duff
 */
@SuppressWarnings("serial")
public class OptimizationResultSet implements java.io.Serializable {
	
	private OptSolverResultSet optSolverResultSet = null;
	private String[] fieldSolutionNames = null;
	private double[][] fieldSolutionValues = null;

/**
 * OptimizationResultSet constructor comment.
 */
public OptimizationResultSet(OptSolverResultSet ors, ODESolverResultSet odeSolverResultSet) {
	optSolverResultSet = ors;
	if (odeSolverResultSet!=null){
		try {
			int numColumns = odeSolverResultSet.getDataColumnCount();
			int numRows = odeSolverResultSet.getRowCount();
			this.fieldSolutionNames = new String[numColumns];
			fieldSolutionValues = new double[numRows][];
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
		}catch (ExpressionException e){
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
public OptSolverResultSet getOptSolverResultSet() {
	return optSolverResultSet;
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