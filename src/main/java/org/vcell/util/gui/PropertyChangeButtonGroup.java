package org.vcell.util.gui;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 * ButtonGroup which sends property change events when included buttons selections are set.
 * Refactored from {@link ButtonGroupCivilized} without side effects of {@link ButtonGroupCivilized#add(javax.swing.AbstractButton)}
 */
@SuppressWarnings("serial")
public class PropertyChangeButtonGroup extends ButtonGroup {

	protected transient java.beans.PropertyChangeSupport propertyChange;

	public PropertyChangeButtonGroup() {
		// TODO Auto-generated constructor stub
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
	public synchronized void addPropertyChangeListener(String propertyName,
			java.beans.PropertyChangeListener listener) {
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
	public void firePropertyChange(String propertyName, int oldValue, int newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
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
	 * The hasListeners method was generated to support the propertyChange field.
	 */
	public synchronized boolean hasListeners(String propertyName) {
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
	public synchronized void removePropertyChangeListener(String propertyName,
			java.beans.PropertyChangeListener listener) {
				getPropertyChange().removePropertyChangeListener(propertyName, listener);
			}

	/**
	 * Sets the selected value for the button.
	 */
	@Override
	public void setSelected(ButtonModel m, boolean b) {
		ButtonModel old = getSelection();
		super.setSelected(m,b);
		firePropertyChange("selection", old, m);
	}

}
