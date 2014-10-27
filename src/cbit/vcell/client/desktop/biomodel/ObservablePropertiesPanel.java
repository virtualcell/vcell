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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.RbmObservable;

@SuppressWarnings("serial")
public class ObservablePropertiesPanel extends DocumentEditorSubPanel {
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener, TreeSelectionListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == observable) {
				if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
					updateTitleLabel();
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == getDeleteMenuItem()) {
				delete();
			} else if (source == getRenameMenuItem()) {
				observableTree.startEditingAtPath(observableTree.getSelectionPath());
			} else if (source == getAddMenu()) {
				addNew();
			} else if (source == getEditMenuItem()) {
				observableTree.startEditingAtPath(observableTree.getSelectionPath());
			} else if (source == showDetailsCheckBox) {
				observableTreeModel.setShowDetails(showDetailsCheckBox.isSelected());
			}
		}

		public void mouseClicked(MouseEvent e) {			
		}

		public void mousePressed(MouseEvent e) {
			if (!e.isConsumed() && e.getSource() == observableTree) {
				showPopupMenu(e);
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (!e.isConsumed() && e.getSource() == observableTree) {
				showPopupMenu(e);
			}			
		}
		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void valueChanged(TreeSelectionEvent e) {
		}
		
	}
	
	private JTree observableTree = null;
	private ObservableTreeModel observableTreeModel = null;
	private RbmObservable observable;
	private JLabel titleLabel = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private JPopupMenu popupMenu;
	private JMenu addMenu;
	private JMenuItem deleteMenuItem;	
	private JMenuItem renameMenuItem;
	private JMenuItem editMenuItem;
	private JCheckBox showDetailsCheckBox;
	
	private BioModel bioModel;
	public ObservablePropertiesPanel() {
		super();
		initialize();
	}

	public void addNew() {
		Object obj = observableTree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode) obj;
		Object selectedUserObject = selectedNode.getUserObject();
		if (selectedUserObject == observable){
			for (MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
				JMenuItem menuItem = new JMenuItem(mt.getName());
				menuItem.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
			}
//			MolecularComponent molecularComponent = observable.createMolecularComponent();
//			observable.addMolecularComponent(molecularComponent);
//			jtree.startEditingAtPath(jtreeModel.findObjectPath(null, molecularComponent));
		} else if (selectedUserObject instanceof MolecularComponent){
//			MolecularComponent molecularComponent = (MolecularComponent) selectedUserObject;
//			ComponentState componentState = molecularComponent.createComponentState();
//			molecularComponent.addComponentState(componentState);
//			jtree.startEditingAtPath(jtreeModel.findObjectPath(null, componentState));
		}	
	}

	public void delete() {
		Object obj = observableTree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode) obj;
		TreeNode parent = selectedNode.getParent();
		if (!(parent instanceof BioModelNode)) {
			return;
		}
		BioModelNode parentNode = (BioModelNode) parent;
		Object selectedUserObject = selectedNode.getUserObject();
		if (selectedUserObject instanceof MolecularTypePattern){
			MolecularTypePattern mtp = (MolecularTypePattern) selectedUserObject;
			Object userObject = parentNode.getUserObject();
			if (userObject == observable) {
				// TODO: Observables can now have multiple species patterns
				observable.getSpeciesPattern(0).removeMolecularTypePattern(mtp);
			}
		}
	}

	private void initialize() {
		showDetailsCheckBox = new JCheckBox("Show All Components");
		showDetailsCheckBox.addActionListener(eventHandler);
		
		observableTree = new BioModelNodeEditableTree();
		observableTreeModel = new ObservableTreeModel(observableTree);
		observableTree.setModel(observableTreeModel);
		observableTree.setEditable(true);
		observableTree.setCellRenderer(new RbmTreeCellRenderer());
		observableTree.setCellEditor(new RbmTreeCellEditor(observableTree));
		
		int rowHeight = observableTree.getRowHeight();
		if (rowHeight < 10) { 
			rowHeight = 20; 
		}
		observableTree.setRowHeight(rowHeight + 5);
		observableTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(observableTree);
		observableTree.addTreeSelectionListener(eventHandler);
		observableTree.addMouseListener(eventHandler);
		observableTree.setLargeModel(true);
		
		setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(2,2,0,2);
		add(showDetailsCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(2,2,0,2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		titleLabel = new JLabel("Observable");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		add(titleLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2,2,2,2);
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(observableTree), gbc);
	}
	
	private JMenu getAddMenu() {
		if (addMenu == null) {
			addMenu = new JMenu("Add");
			addMenu.addActionListener(eventHandler);
		}
		return addMenu;
	}
	
	private JMenuItem getRenameMenuItem() {
		if (renameMenuItem == null) {
			renameMenuItem = new JMenuItem("Rename");
			renameMenuItem.addActionListener(eventHandler);
		}
		return renameMenuItem;
	}
	
	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem("Delete");
			deleteMenuItem.addActionListener(eventHandler);
		}
		return deleteMenuItem;
	}
	
	private JMenuItem getEditMenuItem() {
		if (editMenuItem == null) {
			editMenuItem = new JMenuItem("Edit");
			editMenuItem.addActionListener(eventHandler);
		}
		return editMenuItem;
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		RbmObservable observable = null;
		if (selectedObjects.length == 1 && selectedObjects[0] instanceof RbmObservable) {
			observable = (RbmObservable) selectedObjects[0];
		}
		setObservable(observable);	
	}
	
	private void setObservable(RbmObservable newValue) {
		if (observable == newValue) {
			return;
		}
		RbmObservable oldValue = observable;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
		}
		observable = newValue;
		observableTreeModel.setObservable(observable);
		updateTitleLabel();
	}
	
	private void updateTitleLabel() {
		if (observable != null) {
			titleLabel.setText("Properties for Observable : " + observable.getName());
		}
	}
	
	private void showPopupMenu(MouseEvent e){ 
		if (!e.isPopupTrigger()) {
			return;
		}
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();			
		}		
		if (popupMenu.isShowing()) {
			return;
		}
		GuiUtils.selectClickTreePath(observableTree, e);		
		
		TreePath[] selectedPaths = observableTree.getSelectionPaths();
		boolean bDelete = false;
		boolean bAdd = false;
		boolean bEdit = false;
		boolean bRename = false;
		if (selectedPaths == null) {
			return;
		}
		for (TreePath tp : selectedPaths) {
			Object obj = tp.getLastPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				continue;
			}
			
			BioModelNode selectedNode = (BioModelNode) obj;
			final Object userObject = selectedNode.getUserObject();
			if (userObject instanceof RbmObservable) {
				getAddMenu().setText("Specify Molecular Type");
				getAddMenu().removeAll();
				for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
					JMenuItem menuItem = new JMenuItem(mt.getName());
					getAddMenu().add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							observable.getSpeciesPattern(0).addMolecularTypePattern(new MolecularTypePattern(mt));
						}
					});
				}
				bAdd = true;
				bDelete = false;
				bRename = true;
			} else if (userObject instanceof MolecularTypePattern) {
				bDelete = true;
			} else if (userObject instanceof MolecularComponent) {
				getEditMenuItem().setText("Edit Pattern");
				bEdit = true;
			}
		}
		popupMenu.removeAll();
		// everything can be renamed
		if (bRename) {
			popupMenu.add(getRenameMenuItem());
		}
		if (bDelete) {
			popupMenu.add(getDeleteMenuItem());
		}
		if (bEdit) {
			popupMenu.add(getEditMenuItem());
		}
		if (bAdd) {
			popupMenu.add(new JSeparator());
			popupMenu.add(getAddMenu());
		}

		Point mousePoint = e.getPoint();
		popupMenu.show(observableTree, mousePoint.x, mousePoint.y);
	}
	
	public void setBioModel(BioModel newValue) {
		bioModel = newValue;
		observableTreeModel.setBioModel(bioModel);
	}	
}
