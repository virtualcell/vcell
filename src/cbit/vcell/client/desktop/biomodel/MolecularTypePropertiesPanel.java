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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.DialogUtils;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.SpeciesTypeLargeShape;
import cbit.vcell.model.common.VCellErrorMessages;

@SuppressWarnings("serial")
public class MolecularTypePropertiesPanel extends DocumentEditorSubPanel {
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener, TreeSelectionListener,
		TreeWillExpandListener
	{

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == molecularType) {
				if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
					titleLabel.setText("Properties for Species Type: " + molecularType.getName());
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == getDeleteMenuItem()) {
				delete();
			} else if (source == getRenameMenuItem()) {
				molecularTypeTree.startEditingAtPath(molecularTypeTree.getSelectionPath());
			} else if (source == getAddMenuItem()) {
				addNew();
			}			
		}

		public void mouseClicked(MouseEvent e) {			
		}

		public void mousePressed(MouseEvent e) {
			if (!e.isConsumed() && e.getSource() == molecularTypeTree) {
				showPopupMenu(e);
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (!e.isConsumed() && e.getSource() == molecularTypeTree) {
				showPopupMenu(e);
			}			
		}
		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void valueChanged(TreeSelectionEvent e) {
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
			boolean veto = false;
			if (veto) {
				throw new ExpandVetoException(e);
			}
		}
		@Override
		public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
			boolean veto = true;	// we don't want to collapse any of this
			if (veto) {
				throw new ExpandVetoException(e);
			}
		}
	}
	
	private JTree molecularTypeTree = null;
	private MolecularTypeTreeModel molecularTypeTreeModel = null;
	private MolecularType molecularType;
	private JLabel titleLabel = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	List<SpeciesTypeLargeShape> speciesTypeShapeList = new ArrayList<SpeciesTypeLargeShape>();

	private JPopupMenu popupMenu;
	private JMenuItem addMenuItem;
	private JMenuItem deleteMenuItem;	
	private JMenuItem renameMenuItem;
	
	private BioModel bioModel;
	public MolecularTypePropertiesPanel() {
		super();
		initialize();
	}
	
	public void addNew() {
		Object obj = molecularTypeTree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode) obj;
		Object selectedUserObject = selectedNode.getUserObject();
		if (selectedUserObject == molecularType){
			MolecularComponent molecularComponent = molecularType.createMolecularComponent();
			molecularType.addMolecularComponent(molecularComponent);
			bioModel.getModel().getRbmModelContainer().adjustSpeciesContextPatterns(molecularType, molecularComponent);
			molecularTypeTree.startEditingAtPath(molecularTypeTreeModel.findObjectPath(null, molecularComponent));
		} else if (selectedUserObject instanceof MolecularComponent){
			MolecularComponent molecularComponent = (MolecularComponent) selectedUserObject;
			// TODO: anything to do about ComponentStatePattern ???
			ComponentStateDefinition componentStateDefinition = molecularComponent.createComponentStateDefinition();
			molecularComponent.addComponentStateDefinition(componentStateDefinition);
			molecularTypeTree.startEditingAtPath(molecularTypeTreeModel.findObjectPath(null, componentStateDefinition));
		}	
	}

	public void delete() {
		Object obj = molecularTypeTree.getLastSelectedPathComponent();
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
		if (selectedUserObject instanceof MolecularComponent){
			MolecularComponent mc = (MolecularComponent) selectedUserObject;
			Object userObject = parentNode.getUserObject();
			if (userObject instanceof MolecularType) {
				MolecularType mt = (MolecularType) userObject;
				
				// if there are states we ask the user to delete them individually first
				// detailed verifications will be done there, to see if they are being used in reactions, species, observables
				if(!mc.getComponentStateDefinitions().isEmpty()) {
					String[] options = {"OK"};
					String errMsg = "Component '<b>" + mc.getDisplayName() + "</b>' cannot be deleted because it contains explicit States.";
					errMsg += "<br>Please delete each individual State first.";
					errMsg += "<br><br>Detailed usage information will be provided at that time to help you decide.";
					errMsg = "<html>" + errMsg + "</html>";
					JOptionPane.showOptionDialog(this.getParent().getParent(), errMsg, "Delete Molecular Component", JOptionPane.NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options , options[0]);
					return;
				}
				// we find and display component usage information to help the user decide
				Map<String, Pair<Displayable, SpeciesPattern>> usedHere = new LinkedHashMap<String, Pair<Displayable, SpeciesPattern>>();
				bioModel.getModel().getRbmModelContainer().findComponentUsage(mt, mc, usedHere);
				if(!usedHere.isEmpty()) {
					String errMsg = mc.dependenciesToHtml(usedHere);
					errMsg += "<br><br>Delete anyway?";
					errMsg = "<html>" + errMsg + "</html>";

			        int dialogButton = JOptionPane.YES_NO_OPTION;
			        int returnCode = JOptionPane.showConfirmDialog(this.getParent().getParent(), errMsg, "Delete Molecular Component", dialogButton);
					if (returnCode == JOptionPane.YES_OPTION) {
						// keep this code in sync with MolecularTypeTableModel.setValueAt
						if(bioModel.getModel().getRbmModelContainer().delete(mt, mc) == true) {
							mt.removeMolecularComponent(mc);
						}
					} else {
						return;
					}
				} else {
					if(bioModel.getModel().getRbmModelContainer().delete(mt, mc) == true) {
						mt.removeMolecularComponent(mc);
					}
				}
			}
		} else if (selectedUserObject instanceof ComponentStateDefinition) {
			ComponentStateDefinition csd = (ComponentStateDefinition) selectedUserObject;
			Object userObject = parentNode.getUserObject();
			if (!(userObject instanceof MolecularComponent)) {
				System.out.println("Unexpected parent in tree hierarchy for component state definition " + csd.getDisplayName() + "!");
				return;
			}
			MolecularComponent mc = (MolecularComponent) userObject;
			TreeNode grandParent = parentNode.getParent();
			BioModelNode grandParentNode = (BioModelNode) grandParent;
			userObject = grandParentNode.getUserObject();
			if (!(userObject instanceof MolecularType)) {
				System.out.println("Unexpected parent in tree hierarchy for molecular component " + mc.getDisplayName() + "!");
				return;
			}
			MolecularType mt = (MolecularType) userObject;
			Map<String, Pair<Displayable, SpeciesPattern>> usedHere = new LinkedHashMap<String, Pair<Displayable, SpeciesPattern>>();
			bioModel.getModel().getRbmModelContainer().findStateUsage(mt, mc, csd, usedHere);
			if(!usedHere.isEmpty()) {
				String errMsg = csd.dependenciesToHtml(usedHere);
				errMsg += "<br><br>Delete anyway?";
				errMsg = "<html>" + errMsg + "</html>";

		        int dialogButton = JOptionPane.YES_NO_OPTION;
		        int returnCode = JOptionPane.showConfirmDialog(this.getParent().getParent(), errMsg, "Delete Component State", dialogButton);
				if (returnCode == JOptionPane.YES_OPTION) {
					// keep this code in sync with MolecularTypeTableModel.setValueAt
					if(bioModel.getModel().getRbmModelContainer().delete(mt, mc, csd) == true) {
						mc.deleteComponentStateDefinition(csd);		// this stays
					}
				} else {
					return;
				}
			} else {
				if(bioModel.getModel().getRbmModelContainer().delete(mt, mc, csd) == true) {
					mc.deleteComponentStateDefinition(csd);		// this stays
				}
			}
			
			
//			mc.deleteComponentStateDefinition(csd);
		}
	}

	private void initialize() {
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		leftPanel.setBackground(Color.white);		
		JPanel rightPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
//				g.drawString("This is my custom Panel!", 10, 20);
				for(SpeciesTypeLargeShape stls : speciesTypeShapeList) {
					stls.paintSelf(g);
				}
			}
		};
		rightPanel.setLayout(new GridBagLayout());
		rightPanel.setBackground(Color.white);		
		
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(350);
		splitPane.setResizeWeight(0.9);
		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(rightPanel);
		
		molecularTypeTree = new BioModelNodeEditableTree();
		molecularTypeTreeModel = new MolecularTypeTreeModel(molecularTypeTree);
		molecularTypeTree.setModel(molecularTypeTreeModel);
		molecularTypeTree.setEditable(true);
		molecularTypeTree.setCellRenderer(new RbmMolecularTypeTreeCellRenderer());
		molecularTypeTree.setCellEditor(new RbmMolecularTypeTreeCellEditor(molecularTypeTree));
		
		int rowHeight = molecularTypeTree.getRowHeight();
		if(rowHeight < 10) { 
			rowHeight = 20; 
		}
		molecularTypeTree.setRowHeight(rowHeight + 2);
		molecularTypeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(molecularTypeTree);
		molecularTypeTree.addTreeSelectionListener(eventHandler);
		molecularTypeTree.addTreeWillExpandListener(eventHandler);
		molecularTypeTree.addMouseListener(eventHandler);
		molecularTypeTree.setLargeModel(true);
		molecularTypeTree.setRootVisible(true);
		
		setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		titleLabel = new JLabel("Construct Solid Geometry");
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		leftPanel.add(titleLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.fill = GridBagConstraints.BOTH;
		leftPanel.add(new JScrollPane(molecularTypeTree), gbc);
		
		Dimension minimumSize = new Dimension(100, 150);	//provide minimum sizes for the two components in the split pane
		splitPane.setMinimumSize(minimumSize);
		leftPanel.setMinimumSize(minimumSize);
		rightPanel.setMinimumSize(minimumSize);
		setName("MolecularTypePropertiesPanel");
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		setBackground(Color.white);

	}
	
	private JMenuItem getAddMenuItem() {
		if (addMenuItem == null) {
			addMenuItem = new JMenuItem("Add");
			addMenuItem.addActionListener(eventHandler);
		}
		return addMenuItem;
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
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		MolecularType molecularType = null;
		if (selectedObjects.length == 1 && selectedObjects[0] instanceof MolecularType) {
			molecularType = (MolecularType) selectedObjects[0];
		}
		setMolecularType(molecularType);	
	}
	
	private void setMolecularType(MolecularType newValue) {
		if (molecularType == newValue) {
			return;
		}
		MolecularType oldValue = molecularType;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
			oldValue.removePropertyChangeListener(molecularTypeTreeModel);
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
			newValue.addPropertyChangeListener(molecularTypeTreeModel);
		}
		molecularType = newValue;
		molecularTypeTreeModel.setMolecularType(molecularType);
		if (molecularType != null) {
			titleLabel.setText("Properties for Species Type: " + molecularType.getDisplayName());
			speciesTypeShapeList.clear();
			Graphics panelContext = splitPane.getRightComponent().getGraphics();
			SpeciesTypeLargeShape stls = new SpeciesTypeLargeShape(20, 20, molecularType, panelContext);
			speciesTypeShapeList.add(stls);
//			for(int i = 0; i<bioModel.getModel().getRbmModelContainer().getMolecularTypeList().size(); i++) {
//				MolecularType mt = bioModel.getModel().getRbmModelContainer().getMolecularTypeList().get(i);
//				SpeciesTypeLargeShape stls = new SpeciesTypeLargeShape(20, 20+55*i, mt);
//				speciesTypeShapeList.add(stls);
//			}
			splitPane.getRightComponent().repaint();
		}
	}
	
	private void selectClickPath(MouseEvent e) {
		Point mousePoint = e.getPoint();
		TreePath clickPath = molecularTypeTree.getPathForLocation(mousePoint.x, mousePoint.y);
	    if (clickPath == null) {
	    	return; 
	    }
		Object rightClickNode = clickPath.getLastPathComponent();
		if (rightClickNode == null || !(rightClickNode instanceof BioModelNode)) {
			return;
		}
		TreePath[] selectedPaths = molecularTypeTree.getSelectionPaths();
		if (selectedPaths == null || selectedPaths.length == 0) {
			return;
		} 
		boolean bFound = false;
		for (TreePath tp : selectedPaths) {
			if (tp.equals(clickPath)) {
				bFound = true;
				break;
			}
		}
		if (!bFound) {
			molecularTypeTree.setSelectionPath(clickPath);
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
		selectClickPath(e);		
		
		TreePath[] selectedPaths = molecularTypeTree.getSelectionPaths();
		boolean bDelete = true;
		boolean bAdd = true;
		if (selectedPaths == null) {
			return;
		}
		for (TreePath tp : selectedPaths) {
			Object obj = tp.getLastPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				continue;
			}
			
			BioModelNode selectedNode = (BioModelNode) obj;
			Object userObject = selectedNode.getUserObject();
			if (userObject instanceof MolecularType) {
				getAddMenuItem().setText("Add Molecular Component");
				bAdd = true;
				bDelete = false;
			} else if (userObject instanceof MolecularComponent) {
				getAddMenuItem().setText("Add Component State");
				bAdd = true;
				bDelete = true;
			} else if (userObject instanceof ComponentStateDefinition) {
				bAdd = false;
				bDelete = true;
			}
			
		}
		popupMenu.removeAll();
		// everything can be renamed
		popupMenu.add(getRenameMenuItem());
		if (bDelete) {
			popupMenu.add(getDeleteMenuItem());
		}
		popupMenu.add(new JSeparator());
		if (bAdd) {
			popupMenu.add(getAddMenuItem());
		}

		Point mousePoint = e.getPoint();
		popupMenu.show(molecularTypeTree, mousePoint.x, mousePoint.y);
	}
	
	public void setBioModel(BioModel newValue) {
		bioModel = newValue;
	}	
}
