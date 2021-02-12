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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.vcell.util.BeanUtils;

import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.TopLevelWindow;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEvent;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEventListener;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.geometry.gui.GeometryViewer;

@SuppressWarnings("serial")
public abstract class DocumentEditorSubPanel extends JPanel implements PropertyChangeListener, IssueEventListener {
	protected SelectionManager selectionManager = null;
	protected IssueManager issueManager = null;
	
	public DocumentEditorSubPanel() {
		super();
	}
	
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
		if (selectionManager != null) {
			selectionManager.removePropertyChangeListener(this);
			selectionManager.addPropertyChangeListener(this);
		}
	}

	public void setIssueManager(IssueManager newValue) {
		if (issueManager == newValue) {
			return;
		}
		IssueManager oldValue = issueManager;
		if (oldValue != null) {
			oldValue.removeIssueEventListener(this);
		}
		this.issueManager = newValue;
		if (newValue != null) {
			newValue.removeIssueEventListener(this);
			newValue.addIssueEventListener(this);
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == selectionManager) {
			if (evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_SELECTED_OBJECTS)) {
				Object[] objects = selectionManager.getSelectedObjects();
				onSelectedObjectsChange(objects);
			} else if (evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_ACTIVE_VIEW)) {
				onActiveViewChange(selectionManager.getActiveView());
			}
		}
	}

	protected void onActiveViewChange(ActiveView activeView){};

	protected abstract void onSelectedObjectsChange(Object[] selectedObjects);
	protected <T> void setSelectedObjectsFromTable(JTable table, VCellSortTableModel<T> tableModel) {
		int[] rows = table.getSelectedRows();
		ArrayList<Object> selectedObjects = new ArrayList<Object>();
		for (int i = 0; i < rows.length; i++) {
			if (rows[i] < tableModel.getRowCount()) {
				T valueAt = tableModel.getValueAt(rows[i]);
				if (valueAt != null) {
					selectedObjects.add(valueAt);
				}
			}
		}
		setSelectedObjects(selectedObjects.toArray());
	}
	
	public static <T> void setTableSelections(Object[] selectedObjects, JTable table, VCellSortTableModel<T> tableModel) {
		tableModel.setTableSelections(selectedObjects, table);
	}
	protected void setSelectedObjects(Object[] selectedObjects) {
		if (selectionManager != null) {
			selectionManager.setSelectedObjects(selectedObjects);
		}
	}
	
	protected void setActiveView(ActiveView activeView) {
		if (selectionManager != null) {
			selectionManager.setActiveView(activeView);
		}
	}
	
	protected void followHyperlink(ActiveView newActiveView, Object[] newSelection) {
		if (selectionManager != null) {
			selectionManager.followHyperlink(newActiveView, newSelection);
		}
	}

	public void issueChange(IssueEvent issueEvent) {
	}
	
	public static void addFieldDataMenuItem(Component parent/*JTable*/,JPopupMenu addToThisPopup,int position) {
		do {
			System.out.println(parent.getClass().getName());
			if(parent instanceof TopLevelWindow) {
				ArrayList<Component> comps = new ArrayList<Component>();
				BeanUtils.findComponent((Container)parent, GeometryViewer.class, comps);
				TopLevelWindowManager topLevelWindowManager = ((TopLevelWindow)parent).getTopLevelWindowManager();
				if(topLevelWindowManager instanceof BioModelWindowManager) {
					final BioModelWindowManager bmwm = ((BioModelWindowManager)topLevelWindowManager);
					JMenuItem fdJMenuItem = new JMenuItem("Add FieldData...");
					fdJMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							bmwm.actionPerformed(new ActionEvent(comps.get(0), 0, GuiConstants.ACTIONCMD_EDITCURRENTSPATIAL_GEOMETRY) {
								@Override
								public String toString() {
									return BioModelWindowManager.FIELD_DATA_FLAG;
								}});							
						}
					});
					addToThisPopup.insert(fdJMenuItem, position);
					break;
				}
			}
			parent = parent.getParent();
		}while(parent != null);

	}
}
