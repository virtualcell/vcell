package org.vcell.optimization;
/**
 * Insert the type's description here.
 * Creation date: (12/1/2005 12:40:31 PM)
 * @author: Jim Schaff
 */
public class SimpleOptSolverCallbacks implements OptSolverCallbacks {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private boolean fieldStopRequested = false;
	private Double percentDone = null;
	private Double penaltyMu = null;

	private java.util.Vector<EvaluationHolder> evaluations = new java.util.Vector<EvaluationHolder>();
	private EvaluationHolder bestEvaluation = null;
	
	public static class EvaluationHolder implements OptSolverCallbacks.Evaluation {
		private double[] parameterVector = null;
		private double objFunctionValue = 0;

		public EvaluationHolder(double[] paramValues, double objectiveFuncValue) {
			parameterVector = paramValues;
			objFunctionValue = objectiveFuncValue;
		}
		
		public double getObjectiveFunctionValue(){
			return objFunctionValue;
		}
		
		public double[] getParameterValues(){
			return parameterVector;
		}
	};

/**
 * OptSolverCallbacks constructor comment.
 */
public SimpleOptSolverCallbacks() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:11:12 PM)
 * @param newAtBestParameters double
 */
public void addEvaluation(double[] paramValues, double objectiveFuncValue) {
	addEvaluation(new EvaluationHolder(paramValues, objectiveFuncValue));
}


/**
 * Insert the method's description here.
 * Creation date: (12/20/2005 4:11:12 PM)
 * @param newAtBestParameters double
 */
private void addEvaluation(SimpleOptSolverCallbacks.EvaluationHolder evaluation) {
	evaluations.add(evaluation);
	if (bestEvaluation==null || bestEvaluation.objFunctionValue > evaluation.objFunctionValue){
		bestEvaluation = evaluation;
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
public SimpleOptSolverCallbacks.EvaluationHolder getBestEvaluation() {
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
}