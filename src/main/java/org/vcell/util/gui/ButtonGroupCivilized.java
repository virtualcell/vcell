/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
/**
 * Insert the type's description here.
 * Creation date: (7/13/00 2:23:46 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ButtonGroupCivilized extends PropertyChangeButtonGroup {
	/**
 * ButtonGroupCivilized constructor comment.
 */
public ButtonGroupCivilized() {
	super();
}
/**
 * Adds the button to the group.
 * Sets button setSelected to true
 */ 
@Override
public void add(AbstractButton b) {
	if (b.isSelected()) b.setSelected(false);
	super.add(b);
	b.setSelected(true);
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
