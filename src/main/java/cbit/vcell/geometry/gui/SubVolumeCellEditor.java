/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import cbit.vcell.geometry.SubVolume;
import javax.swing.*;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (4/9/01 3:30:31 PM)
 * @author: Jim Schaff
 */
public class SubVolumeCellEditor extends javax.swing.DefaultCellEditor {
/**
 * SubVolumeCellEditor constructor comment.
 * @param checkBox javax.swing.JCheckBox
 */
public SubVolumeCellEditor(javax.swing.JCheckBox checkBox) {
	super(checkBox);
}
/**
 * SubVolumeCellEditor constructor comment.
 * @param comboBox javax.swing.JComboBox
 */
public SubVolumeCellEditor(javax.swing.JComboBox comboBox) {
	super(comboBox);
}
/**
 * SubVolumeCellEditor constructor comment.
 * @param textField javax.swing.JTextField
 */
public SubVolumeCellEditor(javax.swing.JTextField textField) {
	super(textField);
}
public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	if (value instanceof SubVolume){
		delegate.setValue(((SubVolume)value).getName());
	}else{
		delegate.setValue(value);
	}
	return editorComponent;
}
}
