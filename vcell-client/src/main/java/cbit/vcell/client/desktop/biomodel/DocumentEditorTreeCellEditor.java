/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.vcell.util.gui.VCellIcons;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;

public class DocumentEditorTreeCellEditor extends DefaultTreeCellEditor {

	public DocumentEditorTreeCellEditor(JTree tree) {
		super(tree, new DefaultTreeCellRenderer(), new DefaultCellEditor(new JTextField()));
		DefaultCellEditor editor = (DefaultCellEditor) realEditor;
		final JTextField textField = (JTextField) editor.getComponent();
		textField.setToolTipText("Press Enter to commit");
		textField.addFocusListener(new FocusListener() {			
			public void focusLost(FocusEvent e) {
//				stopCellEditing();
			}
			
			public void focusGained(FocusEvent e) {
				ToolTipManager.sharedInstance().mouseMoved(
				        new MouseEvent(textField, 0, 0, 0,
				                0, 0, 0, false));
			}
		});
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		Component component = null;
		if (value instanceof BioModelNode) {
			Object userObject = ((BioModelNode) value).getUserObject();
			if (userObject instanceof SimulationContext) {
				SimulationContext sc = (SimulationContext)userObject;
				if(sc.isRuleBased()) {
					if(sc.getGeometry().getDimension() == 0) {
						renderer.setOpenIcon(VCellIcons.appRbmNonspIcon);
						renderer.setClosedIcon(VCellIcons.appRbmNonspIcon);
						renderer.setLeafIcon(VCellIcons.appRbmNonspIcon);
					}
				} else if(sc.isStoch()) {
					if(sc.getGeometry().getDimension() == 0) {
						renderer.setOpenIcon(VCellIcons.appStoNonspIcon);
						renderer.setClosedIcon(VCellIcons.appStoNonspIcon);
						renderer.setLeafIcon(VCellIcons.appStoNonspIcon);
					} else {
						renderer.setOpenIcon(VCellIcons.appStoSpatialIcon);
						renderer.setClosedIcon(VCellIcons.appStoSpatialIcon);
						renderer.setLeafIcon(VCellIcons.appStoSpatialIcon);
					}
				} else {		// deterministic
					if(sc.getGeometry().getDimension() == 0) {
						renderer.setOpenIcon(VCellIcons.appDetNonspIcon);
						renderer.setClosedIcon(VCellIcons.appDetNonspIcon);
						renderer.setLeafIcon(VCellIcons.appDetNonspIcon);
					} else {
						renderer.setOpenIcon(VCellIcons.appDetSpatialIcon);
						renderer.setClosedIcon(VCellIcons.appDetSpatialIcon);
						renderer.setLeafIcon(VCellIcons.appDetSpatialIcon);
					}
				}
				component = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
				if (editingComponent instanceof JTextField) {
					String text = null;
					JTextField textField = (JTextField)editingComponent;
					text = ((SimulationContext) userObject).getName();
					textField.setText(text);
				}
			}
		}
		return component;
	}
}
