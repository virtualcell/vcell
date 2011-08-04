package org.vcell.optimization;

import cbit.vcell.solver.ode.ODESolverResultSet;

/**
 * Insert the type's description here.
 * Creation date: (12/1/2005 12:40:31 PM)
 * @author: Jim Schaff
 */
public interface OptSolverCallbacks {
	
	public interface Evaluation {
		public double getObjectiveFunctionValue();
		public double[] getParameterValues();
	}

	public static class EvaluationHolder implements Evaluation{
		public double[] parameterVector = null;
		public double objFunctionValue = 0;
	
		public EvaluationHolder() {
		}
		
		public EvaluationHolder(double[] paramValues, double objectiveFuncValue) {
			parameterVector = paramValues;
			objFunctionValue = objectiveFuncValue;
		}

		public double getObjectiveFunctionValue() {
			return objFunctionValue;
		}

		public double[] getParameterValues() {
			return parameterVector;
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:11:12 PM)
 * @param newAtBestParameters double
 */
public void addEvaluation(double[] paramValues, double objectiveFuncValue);
public void addEvaluation(OptSolverCallbacks.EvaluationHolder evaluation, ODESolverResultSet resultSet);

public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);
	
public Evaluation getBestEvaluation();
public ODESolverResultSet getBestResultSet();
public void setPenaltyMu(java.lang.Double newPenaltyMu);

/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 12:46:36 PM)
 */
public long getEvaluationCount();

/**
 * Gets the stopRequested property (boolean) value.
 * @return The stopRequested property value.
 * @see #setStopRequested
 */
public boolean getStopRequested();

/**
 * Sets the stopRequested property (boolean) value.
 * @param stopRequested The new value for the property.
 * @see #getStopRequested
 */
public void setStopRequested(boolean stopRequested);
public Double getPercentDone();

}