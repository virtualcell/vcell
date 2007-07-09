package cbit.vcell.modelopt;

import org.vcell.modelapp.analysis.IAnalysisTask;

/**
 * Insert the type's description here.
 * Creation date: (5/2/2006 4:35:02 PM)
 * @author: Jim Schaff
 */
public abstract class AbstractAnalysisTask implements org.vcell.util.Matchable, java.io.Serializable, IAnalysisTask {

	protected transient java.beans.PropertyChangeSupport propertyChange;
	private java.lang.String fieldName = new String();
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private java.lang.String fieldAnnotation = new String();

	
/**
 * AnalysisTask constructor comment.
 */
public AbstractAnalysisTask() {
	super();
}


/* (non-Javadoc)
 * @see org.vcell.model.analysis.IAnalysisTask#addPropertyChangeListener(java.beans.PropertyChangeListener)
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


/* (non-Javadoc)
 * @see org.vcell.model.analysis.IAnalysisTask#addVetoableChangeListener(java.beans.VetoableChangeListener)
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


/* (non-Javadoc)
 * @see org.vcell.model.analysis.IAnalysisTask#getAnnotation()
 */
public java.lang.String getAnnotation() {
	return fieldAnnotation;
}


/* (non-Javadoc)
 * @see org.vcell.model.analysis.IAnalysisTask#getName()
 */
public java.lang.String getName() {
	return fieldName;
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
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}


/* (non-Javadoc)
 * @see org.vcell.model.analysis.IAnalysisTask#hasListeners(java.lang.String)
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/* (non-Javadoc)
 * @see org.vcell.model.analysis.IAnalysisTask#removePropertyChangeListener(java.beans.PropertyChangeListener)
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


/* (non-Javadoc)
 * @see org.vcell.model.analysis.IAnalysisTask#setAnnotation(java.lang.String)
 */
public void setAnnotation(java.lang.String annotation) throws java.beans.PropertyVetoException {
	String oldValue = fieldAnnotation;
	fireVetoableChange("annotation", oldValue, annotation);
	fieldAnnotation = annotation;
	firePropertyChange("annotation", oldValue, annotation);
}


/* (non-Javadoc)
 * @see org.vcell.model.analysis.IAnalysisTask#setName(java.lang.String)
 */
public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}
}