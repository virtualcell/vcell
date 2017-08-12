/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import cbit.vcell.solver.ode.ODESolverResultSet;

/**
 * Insert the type's description here.
 * Creation date: (12/1/2005 12:40:31 PM)
 * @author: Jim Schaff
 */
public class DefaultOptSolverCallbacks implements OptSolverCallbacks {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private boolean fieldStopRequested = false;
	private Double percentDone = null;
	private Double penaltyMu = null;

	private java.util.Vector<OptSolverCallbacks.Evaluation> evaluations = new java.util.Vector<OptSolverCallbacks.Evaluation>();
	private OptSolverCallbacks.Evaluation bestEvaluation = null;
	private ODESolverResultSet bestResultSet = null;

	/**
 * OptSolverCallbacks constructor comment.
 */
public DefaultOptSolverCallbacks() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:11:12 PM)
 * @param newAtBestParameters double
 */
public void addEvaluation(double[] paramValues, double objectiveFuncValue) {
	addEvaluation(new OptSolverCallbacks.EvaluationHolder(paramValues, objectiveFuncValue), null);
}

public void showStatistics(){
	if(evaluations.size() > 0)
	{
		//index 0 stores max of parameter value, and index 1 stores min of parameter value
		int indexMax = 0;
		int indexMin = 1;
		int numParameters = evaluations.get(0).getParameterValues().length;
		double[][] stat= new double[numParameters][2]; 
		for(int i=0; i<numParameters; i++)
		{
			stat[i][indexMax]=0;
			stat[i][indexMin]=1e8;
		}
		for (OptSolverCallbacks.Evaluation evalHolder : evaluations)
		{
			System.out.println("objective funciton error :"+evalHolder.getObjectiveFunctionValue());
			double[] parameterValues = evalHolder.getParameterValues();
			for(int i=0; i<parameterValues.length; i++)
			{
				if(stat[i][indexMax]<parameterValues[i])
				{
					stat[i][indexMax] = parameterValues[i];
				}
				if(stat[i][indexMin]>parameterValues[i])
				{
					stat[i][indexMin] = parameterValues[i];
				}
			}
		}
		//print out
		for(int i=0; i<stat.length; i++)
		{
			System.out.println("------------ Parameter"+i+" max and min------------");
			double[] oneParam = stat[i];
			for(double oneStat: oneParam)
			{
				System.out.print("    " + oneStat);
			}
			System.out.println();
		}
		System.out.println();
	}
}
	

/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:11:12 PM)
 * @param newAtBestParameters double
 */
public void addEvaluation(OptSolverCallbacks.Evaluation evaluation, ODESolverResultSet resultSet) {
	evaluations.add(evaluation);
	if (bestEvaluation==null || bestEvaluation.getObjectiveFunctionValue() > evaluation.getObjectiveFunctionValue()){
		bestEvaluation = evaluation;
		bestResultSet = resultSet;
	}
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:11:12 PM)
 * @return double[]
 */
public Evaluation getBestEvaluation() {
	return bestEvaluation;
}


/**
 * Insert the method's description here.
 * Creation date: (12/1/2005 12:46:36 PM)
 */
public long getEvaluationCount() {
	return evaluations.size();
}


/**
 * Insert the method's description here.
 * Creation date: (12/28/2005 3:59:11 PM)
 * @return java.lang.Double
 */
public java.lang.Double getPenaltyMu() {
	return penaltyMu;
}


/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:31:28 PM)
 * @return java.lang.Double
 */
public java.lang.Double getPercentDone() {
	return percentDone;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the stopRequested property (boolean) value.
 * @return The stopRequested property value.
 * @see #setStopRequested
 */
public boolean getStopRequested() {
	return fieldStopRequested;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (12/28/2005 3:59:11 PM)
 * @param newPenaltyMu java.lang.Double
 */
public void setPenaltyMu(java.lang.Double newPenaltyMu) {
	penaltyMu = newPenaltyMu;
}


/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:31:28 PM)
 * @param newPercentDone java.lang.Double
 */
public void setPercentDone(java.lang.Double newPercentDone) {
	percentDone = newPercentDone;
}


/**
 * Sets the stopRequested property (boolean) value.
 * @param stopRequested The new value for the property.
 * @see #getStopRequested
 */
public void setStopRequested(boolean stopRequested) {
	boolean oldValue = fieldStopRequested;
	fieldStopRequested = stopRequested;
	firePropertyChange("stopRequested", new Boolean(oldValue), new Boolean(stopRequested));
}

public final ODESolverResultSet getBestResultSet() {
	return bestResultSet;
}
}
