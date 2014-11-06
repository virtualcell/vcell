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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.SpeciesPatternLocal;
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
			if (e.getSource() == getAddSpeciesPatternMenuItem()) {
				addSpeciesPattern();
			} else if (source == getDeleteMenuItem()) {
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
	private JMenuItem addSpeciesPatternMenuItem;
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
	
	public void addSpeciesPattern() {
		SpeciesPattern sp = new SpeciesPattern();
		observable.addSpeciesPattern(sp);
		final TreePath path = observableTreeModel.findObjectPath(null, observable);
		observableTree.setSelectionPath(path);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {				
				observableTree.scrollPathToVisible(path);
			}
		});
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
		
		if(selectedUserObject instanceof SpeciesPatternLocal) {
			System.out.println("deleting species pattern local");
			Object parentUserObject = parentNode.getUserObject();
			SpeciesPatternLocal spl = (SpeciesPatternLocal)selectedUserObject;
			RbmObservable o = (RbmObservable)parentUserObject;
			List<SpeciesPattern> speciesPatternList = o.getSpeciesPatternList();
			speciesPatternList.remove(spl.speciesPattern);
			final TreePath path = observableTreeModel.findObjectPath(null, observable);
			observableTree.setSelectionPath(path);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					observableTreeModel.populateTree();			// repaint tree
					observableTree.scrollPathToVisible(path);	// scroll back up to show the observable
				}
			});
		} else if (selectedUserObject instanceof MolecularTypePattern){
			System.out.println("deleting molecular type pattern");
			MolecularTypePattern mtp = (MolecularTypePattern) selectedUserObject;
			Object parentUserObject = parentNode.getUserObject();
			SpeciesPatternLocal spl = (SpeciesPatternLocal)parentUserObject;
			SpeciesPattern sp = spl.speciesPattern;
			sp.removeMolecularTypePattern(mtp);
			sp.resolveBonds();
			final TreePath path = observableTreeModel.findObjectPath(null, spl);
			observableTree.setSelectionPath(path);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					observableTreeModel.populateTree();
					observableTree.scrollPathToVisible(path);	// this doesn't seem to work ?
				}
			});
		} else if(selectedUserObject instanceof MolecularComponentPattern) {
			MolecularComponentPattern mcp = (MolecularComponentPattern) selectedUserObject;
			Object parentUserObject = parentNode.getUserObject();
			MolecularTypePattern mtp = (MolecularTypePattern)parentUserObject;
			mtp.removeMolecularComponentPattern(mcp);
			System.out.println("deleting MolecularComponentPattern " + mcp.getMolecularComponent().getName());
			parent = parentNode.getParent();
			parentNode = (BioModelNode) parent;
			parentUserObject = parentNode.getUserObject();
			SpeciesPatternLocal spl = (SpeciesPatternLocal)parentUserObject;
			SpeciesPattern sp = spl.speciesPattern;
			sp.resolveBonds();
			final TreePath path = observableTreeModel.findObjectPath(null, spl);
			observableTree.setSelectionPath(path);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					observableTreeModel.populateTree();
					observableTree.scrollPathToVisible(path);	// this doesn't seem to work ?
				}
			});
		} else {
			System.out.println("deleting " + selectedUserObject.toString());
		}
	}

	private void initialize() {
		showDetailsCheckBox = new JCheckBox("Show All Components");
		showDetailsCheckBox.addActionListener(eventHandler);
		
		observableTree = new BioModelNodeEditableTree();
		observableTreeModel = new ObservableTreeModel(observableTree);
		observableTree.setModel(observableTreeModel);
		observableTree.setEditable(true);
		RbmObservableTreeCellRenderer cro = new RbmObservableTreeCellRenderer();
		observableTree.setCellRenderer(cro);
		DisabledTreeCellEditor dtce =  new DisabledTreeCellEditor(observableTree, (cro));
		observableTree.setCellEditor(dtce);
		observableTree.setEditable(false);
//		observableTree.setCellRenderer(new RbmTreeCellRenderer());
//		observableTree.setCellEditor(new RbmTreeCellEditor(observableTree));
		
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
	
	private JMenuItem getAddSpeciesPatternMenuItem() {
		if (addSpeciesPatternMenuItem == null) {
			addSpeciesPatternMenuItem = new JMenuItem("Add Species Pattern");
			addSpeciesPatternMenuItem.addActionListener(eventHandler);
		}
		return addSpeciesPatternMenuItem;
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
		
		boolean bDelete = false;
		boolean bAdd = false;
		boolean bEdit = false;
		boolean bRename = false;
		popupMenu.removeAll();
		Point mousePoint = e.getPoint();
		GuiUtils.selectClickTreePath(observableTree, e);		
		TreePath clickPath = observableTree.getPathForLocation(mousePoint.x, mousePoint.y);
	    if (clickPath == null) {
	    	popupMenu.add(getAddSpeciesPatternMenuItem());
	    	return;
	    }
		TreePath[] selectedPaths = observableTree.getSelectionPaths();
		if (selectedPaths == null) {
			return;
		}
		for (TreePath tp : selectedPaths) {
			Object obj = tp.getLastPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				continue;
			}
			
			BioModelNode selectedNode = (BioModelNode) obj;
			final Object selectedObject = selectedNode.getUserObject();
			
			if (selectedObject instanceof RbmObservable) {
				popupMenu.add(getAddSpeciesPatternMenuItem());
			} else if(selectedObject instanceof SpeciesPatternLocal) {
				getAddMenu().setText("Specify Molecular Type");
				getAddMenu().removeAll();
				for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
					JMenuItem menuItem = new JMenuItem(mt.getName());
					getAddMenu().add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
							((SpeciesPatternLocal)selectedObject).speciesPattern.addMolecularTypePattern(molecularTypePattern);
							final TreePath path = observableTreeModel.findObjectPath(null, molecularTypePattern);
							observableTree.setSelectionPath(path);
							SwingUtilities.invokeLater(new Runnable() {
								
								public void run() {				
									observableTree.scrollPathToVisible(path);
								}
							});
						}
					});
				}
				bAdd = true;
				bDelete = true;
			} else if (selectedObject instanceof MolecularTypePattern) {
				bDelete = true;
			} else if (selectedObject instanceof MolecularComponentPattern) {
				manageComponentPattern(observableTreeModel, observableTree, selectedNode, selectedObject);
				bDelete = true;
			}
		}
//		popupMenu.removeAll();
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
		popupMenu.show(observableTree, mousePoint.x, mousePoint.y);
	}
	
	public void setBioModel(BioModel newValue) {
		bioModel = newValue;
		observableTreeModel.setBioModel(bioModel);
	}
	
	public void manageComponentPattern(final ObservableTreeModel treeModel, final JTree tree,
			BioModelNode selectedNode, final Object selectedObject) {
		popupMenu.removeAll();
		final MolecularComponentPattern mcp = (MolecularComponentPattern)selectedObject;
		final MolecularComponent mc = mcp.getMolecularComponent();
		//
		// --- State
		//
		if(mc.getComponentStateDefinitions().size() != 0) {
			JMenu editStateMenu = new JMenu();
			editStateMenu.setText("Edit State");
			editStateMenu.removeAll();
			List<String> itemList = new ArrayList<String>();
			itemList.add("Any");
			for (final ComponentStateDefinition csd : mc.getComponentStateDefinitions()) {
				String name = csd.getName();
				itemList.add(name);
			}
			for(String name : itemList) {
				JMenuItem menuItem = new JMenuItem(name);
				editStateMenu.add(menuItem);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String name = e.getActionCommand();
						if(name.equals("Any")) {
							ComponentStatePattern csp = new ComponentStatePattern();
							mcp.setComponentStatePattern(csp);
						} else {
							ComponentStateDefinition csd = new ComponentStateDefinition(e.getActionCommand());
							ComponentStatePattern csp = new ComponentStatePattern(csd);
							mcp.setComponentStatePattern(csp);
						}
					}
				});
			}
			popupMenu.add(editStateMenu);
		}
		//
		// --- Bonds
		//						
		final MolecularTypePattern mtp;
		final SpeciesPattern sp;
		BioModelNode parentNode = (BioModelNode) selectedNode.getParent();
		Object parentObject = parentNode == null ? null : parentNode.getUserObject();
		if(parentObject != null && parentObject instanceof MolecularTypePattern) {
			mtp = (MolecularTypePattern)parentObject;
			parentNode = (BioModelNode) parentNode.getParent();
			parentObject = parentNode == null ? null : parentNode.getUserObject();
			if(parentObject != null && parentObject instanceof SpeciesPatternLocal) {
				sp = ((SpeciesPatternLocal)parentObject).speciesPattern;
			} else {
				sp = null;
			}
		} else {
			mtp = null;
			sp = null;
		}
		
		JMenu editBondMenu = new JMenu();
		editBondMenu.setText("Edit Bond");
		editBondMenu.removeAll();
		final Map<String, Bond> itemMap = new LinkedHashMap<String, Bond>();
		
		final String noneString = "<html><b>" + BondType.None.symbol + "</b> " + BondType.None.name() + "</html>";
		final String existsString = "<html><b>" + BondType.Exists.symbol + "</b> " + BondType.Exists.name() + "</html>";
		final String possibleString = "<html><b>" + BondType.Possible.symbol + "</b> " + BondType.Possible.name() + "</html>";
		itemMap.put(noneString, null);
		itemMap.put(existsString, null);
		itemMap.put(possibleString, null);
		if(mtp != null && sp != null) {
			List<Bond> bondPartnerChoices = sp.getAllBondPartnerChoices(mtp, mc);
			for(Bond b : bondPartnerChoices) {
				if(b.equals(mcp.getBond())) {
					continue;	// if the mcp has a bond already we don't offer it
				}
				int index = 0;
				if(mcp.getBondType() == BondType.Specified) {
					index = mcp.getBondId();
				} else {
					index = sp.nextBondId();
				}
				itemMap.put(b.toHtmlStringLong(sp, mtp, mc, index), b);
			}
		}
		for(String name : itemMap.keySet()) {
			JMenuItem menuItem = new JMenuItem(name);
			editBondMenu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = e.getActionCommand();
					BondType btBefore = mcp.getBondType();
					if(name.equals(noneString)) {
						if(btBefore == BondType.Specified) {	// specified -> not specified
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.None);
						mcp.setBond(null);
						treeModel.populateTree();
					} else if(name.equals(existsString)) {
						if(btBefore == BondType.Specified) {	// specified -> not specified
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Exists);
						mcp.setBond(null);
						treeModel.populateTree();
					} else if(name.equals(possibleString)) {
						if(btBefore == BondType.Specified) {	// specified -> not specified
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Possible);
						mcp.setBond(null);
						treeModel.populateTree();
					} else {
						if (btBefore != BondType.Specified) {
							// if we go from a non-specified to a specified we need to find the next available
							// bond id, so that we can choose the color for displaying the bond
							// a bad bond id, like -1, will crash badly when trying to choose the color
							int bondId = sp.nextBondId();
							mcp.setBondId(bondId);
						} else {
							// specified -> specified
							// change the old partner to possible, continue using the bond id
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Specified);
						Bond b = itemMap.get(name);
						mcp.setBond(b);
						mcp.getBond().molecularComponentPattern.setBondId(mcp.getBondId());
						sp.resolveBonds();

						final TreePath path = treeModel.findObjectPath(null, mcp);
						treeModel.populateTree();
						tree.setSelectionPath(path);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {				
								tree.scrollPathToVisible(path);
							}
						});
					}

				}
			});
		}
		popupMenu.add(editBondMenu);
	}

}
