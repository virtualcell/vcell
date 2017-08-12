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

import javax.swing.DefaultListSelectionModel;
/**
 * Insert the type's description here.
 * Creation date: (6/25/2001 5:56:09 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class DefaultListSelectionModelFixed extends DefaultListSelectionModel {
/**
 * DefaultListSelectionModelFixed constructor comment.
 */
public DefaultListSelectionModelFixed() {
	super();
}
public void insertIndexInterval(int index, int length, boolean before) {
	super.insertIndexInterval(index, length, before);
	fireValueChanged(getMinSelectionIndex(), getMaxSelectionIndex());
}
public void removeIndexInterval(int index0, int index1) {
	super.removeIndexInterval(index0, index1);
	fireValueChanged(getMinSelectionIndex(), getMaxSelectionIndex());
}
}
