package org.vcell.util.gui;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.util.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (7/13/00 2:23:46 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ButtonGroupCivilized extends javax.swing.ButtonGroup {
	protected transient java.beans.PropertyChangeSupport propertyChange;
/**
 * ButtonGroupCivilized constructor comment.
 */
public ButtonGroupCivilized() {
	super();
}
/**
 * Adds the button to the group.
 */ 
public void add(AbstractButton b) {
	if (b.isSelected()) b.setSelected(false);
	super.add(b);
	b.setSelected(true);
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
public synchronized void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
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
 * Insert the method's description here.
 * Creation date: (8/23/00 3:21:01 PM)
 * @return javax.swing.AbstractButton
 * @param actionCommand java.lang.String
 */
public AbstractButton getButton(String actionCommand) {
	Enumeration<AbstractButton> enumButtons = getElements();
	while (enumButtons.hasMoreElements()) {
		AbstractButton button = (AbstractButton) enumButtons.nextElement();
		if (button.getActionCommand().equals(actionCommand)) {
			return button;
		}
	}
	throw new RuntimeException("Button not found for " + actionCommand);
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
public synchronized void removePropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Sets the selected value for the button.
 */
public void setSelected(ButtonModel m, boolean b) {
	ButtonModel old = getSelection();
	super.setSelected(m,b);
	firePropertyChange("selection", old, m);
}
/**
 * This method was created in VisualAge.
 * @param actionCommand java.lang.String
 */
public void setSelection(String actionCommand) {
	//
	// if selected button does not have this action command, select the first button we find with appropriate action command
	//
	String currSelectedString = null;
	if (getSelection() != null) {
		currSelectedString = getSelection().getActionCommand();
	}
	if (currSelectedString != null) {
		if (currSelectedString.equals(actionCommand)) {
			return;
		}
	}
	ButtonModel buttonModel = null;
	Enumeration<AbstractButton> buttons = getElements();
	while (buttons.hasMoreElements()) {
		buttonModel = ((AbstractButton)buttons.nextElement()).getModel();
		if (buttonModel.getActionCommand().equals(actionCommand)) {
			setSelection(buttonModel);
			return;
		}
	}
	//
	// if we get this far and there actually were some buttons in the group...
	//
	if (buttonModel != null) {
		System.out.println("ERROR: button with actionCommand " + actionCommand + " not found");
	}
}
	/**
	 * Sets the selected value for the button.
	 */
	public void setSelection(ButtonModel m) {
		m.setSelected(true);
		setSelected(m, true);
	}
}
