/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.plot;
import org.vcell.util.Range;
/**
 * Insert the type's description here.
 * Creation date: (5/25/2001 3:53:18 PM)
 * @author: Ion Moraru
 */
public class Plot2DSettings {
	private Plot2DSettings savedSettings = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private boolean fieldXAuto = true;
	private boolean fieldXStretch = false;
	private boolean fieldYAuto = true;
	private boolean fieldYStretch = false;
	private boolean fieldShowNodes = true;
	private boolean fieldShowCrosshair = true;
	private boolean fieldSnapToNodes = true;
	private Range fieldXAutoRange = null;
	private Range fieldYAutoRange = null;
	private Range fieldXManualRange = null;
	private Range fieldYManualRange = null;

/**
 * Plot2DSettings constructor comment.
 */
public Plot2DSettings() {
	super();
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
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the showCrosshair property (boolean) value.
 * @return The showCrosshair property value.
 * @see #setShowCrosshair
 */
public boolean getShowCrosshair() {
	return fieldShowCrosshair;
}


/**
 * Gets the showNodes property (boolean) value.
 * @return The showNodes property value.
 * @see #setShowNodes
 */
public boolean getShowNodes() {
	return fieldShowNodes;
}


/**
 * Gets the snapToNodes property (boolean) value.
 * @return The snapToNodes property value.
 * @see #setSnapToNodes
 */
public boolean getSnapToNodes() {
	return fieldSnapToNodes;
}


/**
 * Gets the xAuto property (boolean) value.
 * @return The xAuto property value.
 * @see #setXAuto
 */
public boolean getXAuto() {
	return fieldXAuto;
}


/**
 * Gets the xAutoRange property (cbit.image.Range) value.
 * @return The xAutoRange property value.
 * @see #setXAutoRange
 */
public Range getXAutoRange() {
	return fieldXAutoRange;
}


/**
 * Gets the xManualRange property (cbit.image.Range) value.
 * @return The xManualRange property value.
 * @see #setXManualRange
 */
public Range getXManualRange() {
	return fieldXManualRange;
}


/**
 * Gets the xStretch property (boolean) value.
 * @return The xStretch property value.
 * @see #setXStretch
 */
public boolean getXStretch() {
	return fieldXStretch;
}


/**
 * Gets the yAuto property (boolean) value.
 * @return The yAuto property value.
 * @see #setYAuto
 */
public boolean getYAuto() {
	return fieldYAuto;
}


/**
 * Gets the yAutoRange property (cbit.image.Range) value.
 * @return The yAutoRange property value.
 * @see #setYAutoRange
 */
public Range getYAutoRange() {
	return fieldYAutoRange;
}


/**
 * Gets the yManualRange property (cbit.image.Range) value.
 * @return The yManualRange property value.
 * @see #setYManualRange
 */
public Range getYManualRange() {
	return fieldYManualRange;
}


/**
 * Gets the yStretch property (boolean) value.
 * @return The yStretch property value.
 * @see #setYStretch
 */
public boolean getYStretch() {
	return fieldYStretch;
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
 * Insert the method's description here.
 * Creation date: (5/25/2001 7:09:41 PM)
 */
public void restoreSavedSettings() {
	if (savedSettings != null) {
		setShowCrosshair(savedSettings.getShowCrosshair());
		setShowNodes(savedSettings.getShowNodes());
		setSnapToNodes(savedSettings.getSnapToNodes());
		setXAuto(savedSettings.getXAuto());
		setXStretch(savedSettings.getXStretch());
		setYAuto(savedSettings.getYAuto());
		setYStretch(savedSettings.getYStretch());
		setXAutoRange(savedSettings.getXAutoRange());
		setXManualRange(savedSettings.getXManualRange());
		setYAutoRange(savedSettings.getYAutoRange());
		setYManualRange(savedSettings.getYManualRange());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/2001 7:08:24 PM)
 */
public void saveSettings() {
	if (savedSettings == null) {
		savedSettings = new Plot2DSettings();
	}
	savedSettings.setShowCrosshair(getShowCrosshair());
	savedSettings.setShowNodes(getShowNodes());
	savedSettings.setSnapToNodes(getSnapToNodes());
	savedSettings.setXAuto(getXAuto());
	savedSettings.setXStretch(getXStretch());
	savedSettings.setYAuto(getYAuto());
	savedSettings.setYStretch(getYStretch());
	savedSettings.setXAutoRange(getXAutoRange());
	savedSettings.setXManualRange(getXManualRange());
	savedSettings.setYAutoRange(getYAutoRange());
	savedSettings.setYManualRange(getYManualRange());
}


/**
 * Sets the showCrosshair property (boolean) value.
 * @param showCrosshair The new value for the property.
 * @see #getShowCrosshair
 */
public void setShowCrosshair(boolean showCrosshair) {
	boolean oldValue = fieldShowCrosshair;
	fieldShowCrosshair = showCrosshair;
	firePropertyChange("showCrosshair", new Boolean(oldValue), new Boolean(showCrosshair));
}


/**
 * Sets the showNodes property (boolean) value.
 * @param showNodes The new value for the property.
 * @see #getShowNodes
 */
public void setShowNodes(boolean showNodes) {
	boolean oldValue = fieldShowNodes;
	fieldShowNodes = showNodes;
	firePropertyChange("showNodes", new Boolean(oldValue), new Boolean(showNodes));
}


/**
 * Sets the snapToNodes property (boolean) value.
 * @param snapToNodes The new value for the property.
 * @see #getSnapToNodes
 */
public void setSnapToNodes(boolean snapToNodes) {
	boolean oldValue = fieldSnapToNodes;
	fieldSnapToNodes = snapToNodes;
	firePropertyChange("snapToNodes", new Boolean(oldValue), new Boolean(snapToNodes));
}


/**
 * Sets the xAuto property (boolean) value.
 * @param xAuto The new value for the property.
 * @see #getXAuto
 */
public void setXAuto(boolean xAuto) {
	boolean oldValue = fieldXAuto;
	fieldXAuto = xAuto;
	firePropertyChange("xAuto", new Boolean(oldValue), new Boolean(xAuto));
}


/**
 * Sets the xAutoRange property (cbit.image.Range) value.
 * @param xAutoRange The new value for the property.
 * @see #getXAutoRange
 */
public void setXAutoRange(Range xAutoRange) {
	Range oldValue = fieldXAutoRange;
	fieldXAutoRange = xAutoRange;
	firePropertyChange("xAutoRange", oldValue, xAutoRange);
}


/**
 * Sets the xManualRange property (cbit.image.Range) value.
 * @param xManualRange The new value for the property.
 * @see #getXManualRange
 */
public void setXManualRange(Range xManualRange) {
	Range oldValue = fieldXManualRange;
	fieldXManualRange = xManualRange;
	firePropertyChange("xManualRange", oldValue, xManualRange);
}


/**
 * Sets the xStretch property (boolean) value.
 * @param xStretch The new value for the property.
 * @see #getXStretch
 */
public void setXStretch(boolean xStretch) {
	boolean oldValue = fieldXStretch;
	fieldXStretch = xStretch;
	firePropertyChange("xStretch", new Boolean(oldValue), new Boolean(xStretch));
}


/**
 * Sets the yAuto property (boolean) value.
 * @param yAuto The new value for the property.
 * @see #getYAuto
 */
public void setYAuto(boolean yAuto) {
	boolean oldValue = fieldYAuto;
	fieldYAuto = yAuto;
	firePropertyChange("yAuto", new Boolean(oldValue), new Boolean(yAuto));
}


/**
 * Sets the yAutoRange property (cbit.image.Range) value.
 * @param yAutoRange The new value for the property.
 * @see #getYAutoRange
 */
public void setYAutoRange(Range yAutoRange) {
	Range oldValue = fieldYAutoRange;
	fieldYAutoRange = yAutoRange;
	firePropertyChange("yAutoRange", oldValue, yAutoRange);
}


/**
 * Sets the yManualRange property (cbit.image.Range) value.
 * @param yManualRange The new value for the property.
 * @see #getYManualRange
 */
public void setYManualRange(Range yManualRange) {
	Range oldValue = fieldYManualRange;
	fieldYManualRange = yManualRange;
	firePropertyChange("yManualRange", oldValue, yManualRange);
}


/**
 * Sets the yStretch property (boolean) value.
 * @param yStretch The new value for the property.
 * @see #getYStretch
 */
public void setYStretch(boolean yStretch) {
	boolean oldValue = fieldYStretch;
	fieldYStretch = yStretch;
	firePropertyChange("yStretch", new Boolean(oldValue), new Boolean(yStretch));
}
}
