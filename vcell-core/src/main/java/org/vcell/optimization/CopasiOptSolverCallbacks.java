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

import org.vcell.optimization.jtd.OptProgressReport;
import org.vcell.util.PropertyChangeListenerProxyVCell;




public class CopasiOptSolverCallbacks {
	public static final String OPT_PROGRESS_REPORT = "optProgressReport";
	private PropertyChangeSupport propertyChange = null;
	private OptProgressReport optProgressReport = null;
	private boolean fieldStopRequested = false;

	public void setProgressReport(OptProgressReport optProgressReport) {
		OptProgressReport oldValue = this.optProgressReport;
		this.optProgressReport = optProgressReport;
		firePropertyChange(OPT_PROGRESS_REPORT, oldValue, optProgressReport);
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		PropertyChangeListenerProxyVCell.addProxyListener(getPropertyChange(), listener);
	}
	
	private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		PropertyChangeListenerProxyVCell.removeProxyListener(getPropertyChange(), listener);
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	private PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	public boolean getStopRequested()
	{
//		System.out.println("stop Requested = " + fieldStopRequested);
		return fieldStopRequested;
	}

	public OptProgressReport getProgressReport()
	{
		return optProgressReport;
	}

	public void setStopRequested(boolean stopRequested)
	{
		boolean oldValue = fieldStopRequested;
		fieldStopRequested = stopRequested;
		firePropertyChange("stopRequested", new Boolean(oldValue), new Boolean(stopRequested));
	}

//	public int getRunNumber()
//	{
//		if(copasiEvaluationHolder != null)
//		{
//			return copasiEvaluationHolder.getRunNumber();
//		}
//		return 1;
//	}
	
	public void reset() {
		fieldStopRequested = false;
		optProgressReport = null;
	}

}
