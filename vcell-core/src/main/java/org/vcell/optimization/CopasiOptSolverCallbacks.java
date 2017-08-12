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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.vcell.util.PropertyChangeListenerProxyVCell;




public class CopasiOptSolverCallbacks {
	public static final String COPASI_EVALUATION_HOLDER = "copasiEvaluationHolder";
	private PropertyChangeSupport propertyChange = null;
	private CopasiEvaluationHolder copasiEvaluationHolder = null;
	private boolean fieldStopRequested = false;
	
	public static class CopasiEvaluationHolder
	{
		private double objFunctionValue = 0;
		private double currentValue;
		private int numEvaluations = 0;
		private Double endValue;
		private int runNo = 1;

		public CopasiEvaluationHolder(int numEvaluations, double objFunctionValue,
				double currentValue, Double endValue, int runNo) {
			super();
			this.numEvaluations = numEvaluations;
			this.objFunctionValue = objFunctionValue;
			this.currentValue = currentValue;
			this.endValue = endValue;
			this.runNo = runNo;
		}
		
		public double getObjFunctionValue() {
			return objFunctionValue;
		}

		public double getCurrentValue() {
			return currentValue;
		}

		public int getNumEvaluations() {
			return numEvaluations;
		}

		public Double getEndValue() {
			return endValue;
		}
		
		public int getRunNumber()
		{
			return runNo;
		}

	}

	public void setEvaluation(int numEvaluations, double objFunctionValue, double currentValue, Double endValue, int runNo)
	{
		CopasiEvaluationHolder oldValue = this.copasiEvaluationHolder;
		copasiEvaluationHolder = new CopasiEvaluationHolder(numEvaluations, objFunctionValue, currentValue, endValue, runNo);
		firePropertyChange(COPASI_EVALUATION_HOLDER, oldValue, copasiEvaluationHolder);
	}
	
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		PropertyChangeListenerProxyVCell.addProxyListener(getPropertyChange(), listener);
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		PropertyChangeListenerProxyVCell.removeProxyListener(getPropertyChange(), listener);
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	public PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	
	public int getEvaluationCount()
	{
		if(copasiEvaluationHolder != null)
		{
			return copasiEvaluationHolder.getNumEvaluations();
		}
		return 0;
	}
	
	public double getObjectiveFunctionValue()
	{
		if(copasiEvaluationHolder != null)
		{
			return copasiEvaluationHolder.getObjFunctionValue();
		}
		return 0;
	}
	
	public boolean getStopRequested()
	{
//		System.out.println("stop Requested = " + fieldStopRequested);
		return fieldStopRequested;
	}
	
	public void setStopRequested(boolean stopRequested)
	{
		boolean oldValue = fieldStopRequested;
		fieldStopRequested = stopRequested;
		firePropertyChange("stopRequested", new Boolean(oldValue), new Boolean(stopRequested));
	}
	
	public int getPercent()
	{
		if(copasiEvaluationHolder != null)
		{
			if(copasiEvaluationHolder.getEndValue() != null && copasiEvaluationHolder.getEndValue().doubleValue() != 0)
			{
				return (int) (copasiEvaluationHolder.getCurrentValue() * 100.0 /copasiEvaluationHolder.getEndValue());
			}
		}
		return 0;
	}
	
	public double getCurrentValue()
	{
		if(copasiEvaluationHolder != null)
		{
			return copasiEvaluationHolder.getCurrentValue() ;
		}
		return 0;
	}
	

	public int getRunNumber()
	{
		if(copasiEvaluationHolder != null)
		{
			return copasiEvaluationHolder.getRunNumber();
		}
		return 1;
	}
	
	public void reset() {
		fieldStopRequested = false;
		copasiEvaluationHolder = null;
	}

}
