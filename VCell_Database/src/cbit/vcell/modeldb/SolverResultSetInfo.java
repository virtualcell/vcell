package cbit.vcell.modeldb;

import cbit.vcell.solvers.VCSimulationDataIdentifier;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Metadata for SolverResultSets.
 * Creation date: (8/17/2000 9:10:20 PM)
 * @author: John Wagner
 */
public class SolverResultSetInfo implements java.io.Serializable, cbit.util.Matchable {
	/**
	 * The date and time the solver began.
	 */
	private java.util.Date fieldStartingDate = new java.util.Date();
	/**
	 * The date and time the solver completed.
	 */
	private java.util.Date fieldEndingDate = new java.util.Date();
	/**
	 * Specifies the problem that generated the result set..
	 */
	private VCSimulationDataIdentifier fieldVCSimDataID = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private java.lang.String fieldDataFilePath = new String();

/**
 * ODESolverResultSetInfo constructor comment.
 */
public SolverResultSetInfo(VCSimulationDataIdentifier arg_vcSimID) {
	super();
	fieldVCSimDataID = arg_vcSimID;
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	if (obj instanceof SolverResultSetInfo){
		SolverResultSetInfo otherRSI = (SolverResultSetInfo)obj;

		if (!cbit.util.Compare.isEqual(getDataFilePath(),otherRSI.getDataFilePath())){
			return false;
		}
		if (!cbit.util.Compare.isEqual(getEndingDate(),otherRSI.getEndingDate())){
			return false;
		}
		if (!cbit.util.Compare.isEqual(getStartingDate(),otherRSI.getStartingDate())){
			return false;
		}
		if (!cbit.util.Compare.isEqual(getVCSimulationDataIdentifier().getID(),otherRSI.getVCSimulationDataIdentifier().getID())){
			return false;
		}		
		return true;
	}else{
		return false;
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Gets the dataFilePath property (java.lang.String) value.
 * @return The dataFilePath property value.
 * @see #setDataFilePath
 */
public java.lang.String getDataFilePath() {
	return fieldDataFilePath;
}


/**
 * Gets the endDate property (java.util.Date) value.
 * @return The endDate property value.
 * @see #setEndDate
 */
public java.util.Date getEndingDate() {
	return fieldEndingDate;
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
 * Gets the startDate property (java.util.Date) value.
 * @return The startDate property value.
 * @see #setStartDate
 */
public java.util.Date getStartingDate() {
	return fieldStartingDate;
}


/**
 * Gets the oDESolverSpecification property (cbit.vcell.solver.ode.ODESolverSpecification) value.
 * @return The oDESolverSpecification property value.
 * @see #setODESolverSpecification
 */
public VCSimulationDataIdentifier getVCSimulationDataIdentifier() {
	return fieldVCSimDataID;
}


/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
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
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * Sets the dataFilePath property (java.lang.String) value.
 * @param dataFilePath The new value for the property.
 * @see #getDataFilePath
 */
public void setDataFilePath(java.lang.String dataFilePath) {
	String oldValue = fieldDataFilePath;
	fieldDataFilePath = dataFilePath;
	firePropertyChange("dataFilePath", oldValue, dataFilePath);
}


/**
 * Sets the endDate property (java.util.Date) value.
 * @param endDate The new value for the property.
 * @see #getEndDate
 */
public void setEndingDate(java.util.Date endingDate) throws java.beans.PropertyVetoException {
	java.util.Date oldValue = fieldEndingDate;
	fireVetoableChange("endingDate", oldValue, endingDate);
	fieldEndingDate = endingDate;
	firePropertyChange("endingDate", oldValue, endingDate);
}


/**
 * Sets the startDate property (java.util.Date) value.
 * @param startDate The new value for the property.
 * @see #getStartDate
 */
public void setStartingDate(java.util.Date startingDate) throws java.beans.PropertyVetoException {
	java.util.Date oldValue = fieldStartingDate;
	fireVetoableChange("startingDate", oldValue, startingDate);
	fieldStartingDate = startingDate;
	firePropertyChange("startingDate", oldValue, startingDate);

}


/**
 * Insert the method's description here.
 * Creation date: (12/30/2000 3:32:02 PM)
 * @return java.lang.String
 */
public String toString() {
	return "SolverResultSetInfo("+getStartingDate()+","+getEndingDate()+","+getDataFilePath()+") for " + getVCSimulationDataIdentifier();
}
}