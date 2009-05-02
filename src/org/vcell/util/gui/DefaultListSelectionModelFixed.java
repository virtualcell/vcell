package org.vcell.util.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (6/25/2001 5:56:09 PM)
 * @author: Ion Moraru
 */
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
