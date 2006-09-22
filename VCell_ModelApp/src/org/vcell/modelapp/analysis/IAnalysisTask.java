package org.vcell.modelapp.analysis;

import cbit.util.Matchable;

public interface IAnalysisTask extends Matchable{

	public abstract IAnalysisTaskFactory getAnalysisTaskFactory();
	
	public abstract IAnalysisTask createClone(String newName);

	public abstract org.jdom.Element getXML();

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public abstract void addPropertyChangeListener(
			java.beans.PropertyChangeListener listener);

	/**
	 * Insert the method's description here.
	 * Creation date: (5/2/2006 11:04:33 PM)
	 * @param issueList java.util.Vector
	 */
	public abstract void gatherIssues(java.util.Vector issueList);

	/**
	 * Gets the annotation property (java.lang.String) value.
	 * @return The annotation property value.
	 * @see #setAnnotation
	 */
	public abstract java.lang.String getAnnotation();

	/**
	 * Gets the name property (java.lang.String) value.
	 * @return The name property value.
	 * @see #setName
	 */
	public abstract java.lang.String getName();

	/**
	 * The hasListeners method was generated to support the propertyChange field.
	 */
	public abstract boolean hasListeners(java.lang.String propertyName);

	/**
	 * Insert the method's description here.
	 * Creation date: (5/2/2006 4:47:47 PM)
	 */
	public abstract void refreshDependencies();

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public abstract void removePropertyChangeListener(
			java.beans.PropertyChangeListener listener);

	/**
	 * Sets the annotation property (java.lang.String) value.
	 * @param annotation The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getAnnotation
	 */
	public abstract void setAnnotation(java.lang.String annotation)
			throws java.beans.PropertyVetoException;

	/**
	 * Sets the name property (java.lang.String) value.
	 * @param name The new value for the property.
	 * @exception java.beans.PropertyVetoException The exception description.
	 * @see #getName
	 */
	public abstract void setName(java.lang.String name)
			throws java.beans.PropertyVetoException;

}