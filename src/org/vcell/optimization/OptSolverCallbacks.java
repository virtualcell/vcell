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

/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:11:12 PM)
 * @param newAtBestParameters double
 */
public void addEvaluation(double[] paramValues, double objectiveFuncValue);


public Evaluation getBestEvaluation();

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

}
