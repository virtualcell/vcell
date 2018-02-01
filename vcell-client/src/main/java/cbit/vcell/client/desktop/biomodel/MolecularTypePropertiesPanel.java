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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.HighlightableShapeInterface;
import cbit.vcell.graph.LargeShape;
import cbit.vcell.graph.MolecularComponentLargeShape;
import cbit.vcell.graph.MolecularComponentLargeShape.ComponentStateLargeShape;
import cbit.vcell.graph.MolecularTypeLargeShape;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.ReactionCartoon.RuleAnalysisChanged;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.gui.LargeShapePanel;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.Model;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class MolecularTypePropertiesPanel extends DocumentEditorSubPanel {
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener, TreeSelectionListener,
		TreeWillExpandListener, FocusListener, ItemListener
	{
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == molecularType) {
				if (evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_NAME)) {
//					titleLabel.setText("Properties for " + molecularType.getDisplayType() + ": " + molecularType.getDisplayName());
//					titleLabel.setText("Anchor");
				} else if(evt.getPropertyName().equals(MolecularType.PROPERTY_NAME_ANNOTATION)) {
					annotationTextArea.setText((String)evt.getNewValue());
					annotationTextArea.setCaretPosition(0);
				}
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == getDeleteFromTreeMenuItem()) {
				deleteFromTree();
			} else if (source == getRenameFromTreeMenuItem()) {
				molecularTypeTree.startEditingAtPath(molecularTypeTree.getSelectionPath());
			} else if (source == getAddFromTreeMenuItem()) {
				addNewFromTree();
			} else if (source == getAnchorAllButton()) {
				molecularType.setAnchorAll(true);
				anchorPanel.enableAll(false);
			} else if (source == getAnchorOnlyButton()) {
				molecularType.setAnchorAll(false);
				anchorPanel.enableAll(true);
//			} else if (source == getRenameFromShapeMenuItem()) {
//				molecularTypeTree.startEditingAtPath(molecularTypeTree.getSelectionPath());
			}			
		}
		@Override
		public void mouseClicked(MouseEvent e) {			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			if (!e.isConsumed() && e.getSource() == molecularTypeTree) {
//				showPopupMenu(e);
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!e.isConsumed() && e.getSource() == molecularTypeTree) {
				// the popup menu when click inside shape is called elsewhere below
//				showPopupMenu(e);
			}			
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
//			if(e.getSource() == annotationTextArea){
//				changeFreeTextAnnotation();
//			}
		}
		@Override
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
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
//			if (e.getSource() == annotationTextArea) {
//				changeFreeTextAnnotation();
//			}
		}
		@Override
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getSource();
			if(!(source instanceof JCheckBox)) {
				return;
			}
			JCheckBox cb = (JCheckBox)source;
			String name = cb.getText();
			if(name == null) {
				throw new RuntimeException("Checkbox name missing.");
			}
			Structure struct = bioModel.getModel().getStructure(name);
			if(struct == null) {
				throw new RuntimeException("Unable to find Structure " + name);
			}
			if(e.getStateChange() == ItemEvent.SELECTED) {
				molecularType.getAnchors().add(struct);
			} else {
				molecularType.getAnchors().remove(struct);
			}
		}
	}
	
	private class JAnchorPanel extends JPanel {
		public List<JCheckBox> structureList = new ArrayList<>();
		public JAnchorPanel() {
			super();
		}
		
		public void enableAll(boolean b) {
			for(Component component : getComponents()) {
				if(component instanceof JCheckBox) {
					JCheckBox cb = (JCheckBox)component;
					cb.setEnabled(b);
				}
			}
		}
		public void setAnchors() {
			for(JCheckBox cb : structureList) {
				String checkBoxText = cb.getText();
				boolean found = false;
				for(Structure struct : molecularType.getAnchors()) {
					String structureName = struct.getName();
					if(checkBoxText.equals(structureName)) {
						found = true;
						break;
					}
				}
				cb.setSelected(found);
			}
		}
		private void initialize() {
			structureList.clear();
			Model model = bioModel.getModel();
			for(Structure struct : model.getStructures()) {
				JCheckBox cb = new JCheckBox(struct.getName());
				cb.addItemListener(eventHandler);
				structureList.add(cb);
			}
			setLayout(new GridBagLayout());
			int gridy = 0;
			for(JCheckBox cb : structureList) {
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = gridy;
				gbc.anchor = GridBagConstraints.WEST;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(2, 4, 2, 4);
				add(cb, gbc);
				gridy++;
			}
			gridy++;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 2;
			gbc.weightx = 1;
			gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 10);
			add(new JLabel(""), gbc);
		}
		@Override
		public void removeAll() {
			for(JCheckBox cb : structureList) {
				cb.removeItemListener(eventHandler);
			}
			super.removeAll();
			structureList.clear();
		}
	}
	
	private JTree molecularTypeTree = null;
	private MolecularTypeTreeModel molecularTypeTreeModel = null;
	private MolecularType molecularType;
	private JLabel titleLabel = null;
//	private JTextArea annotationTextArea;
	private JTextPane annotationTextArea;
	private JScrollPane linkedPOScrollPane;

	private JRadioButton anchorAllButton;
	private JRadioButton anchorOnlyButton;
	private JAnchorPanel anchorPanel;
	private JScrollPane anchorScrollPanel;
	
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);	// between shape and annotation
	private JScrollPane scrollPane;
	private LargeShapePanel shapePanel = null;
	private List<MolecularTypeLargeShape> molecularTypeShapeList = new ArrayList<MolecularTypeLargeShape>();

	private JPopupMenu popupMenu;
	private JMenuItem addFromTreeMenuItem;
	private JMenuItem deleteFromTreeMenuItem;	
	private JMenuItem renameFromTreeMenuItem;
	
	private JPopupMenu popupFromShapeMenu;
	private JMenuItem renameFromShapeMenuItem;
	
	private BioModel bioModel;
	
	public MolecularTypePropertiesPanel() {
		super();
		initialize();
	}
		
	public void addNewFromTree() {
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
			bioModel.getModel().getRbmModelContainer().adjustObservablesPatterns(molecularType, molecularComponent);
			bioModel.getModel().getRbmModelContainer().adjustRulesPatterns(molecularType, molecularComponent);
			molecularTypeTree.startEditingAtPath(molecularTypeTreeModel.findObjectPath(null, molecularComponent));
		} else if (selectedUserObject instanceof MolecularComponent){
			MolecularComponent molecularComponent = (MolecularComponent) selectedUserObject;
			ComponentStateDefinition componentStateDefinition = molecularComponent.createComponentStateDefinition();
			molecularComponent.addComponentStateDefinition(componentStateDefinition);
			bioModel.getModel().getRbmModelContainer().adjustObservablesPatterns(molecularType, molecularComponent, componentStateDefinition);
			molecularTypeTree.startEditingAtPath(molecularTypeTreeModel.findObjectPath(null, componentStateDefinition));
		}	
	}
	public void deleteFromTree() {
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
					String errMsg = mc.getDisplayType() + " '<b>" + mc.getDisplayName() + "</b>' cannot be deleted because it contains explicit States.";
					errMsg += "<br>Please delete each individual State first.";
					errMsg += "<br><br>Detailed usage information will be provided at that time to help you decide.";
					errMsg = "<html>" + errMsg + "</html>";
					JOptionPane.showOptionDialog(this.getParent().getParent(), errMsg, "Delete " + mc.getDisplayType(), JOptionPane.NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options , options[0]);
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
			        int returnCode = JOptionPane.showConfirmDialog(this.getParent().getParent(), errMsg, "Delete " + mc.getDisplayType(), dialogButton);
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
				System.out.println("Unexpected parent in tree hierarchy for " + ComponentStateDefinition.typeName + " " + csd.getDisplayName() + "!");
				return;
			}
			MolecularComponent mc = (MolecularComponent) userObject;
			TreeNode grandParent = parentNode.getParent();
			BioModelNode grandParentNode = (BioModelNode) grandParent;
			userObject = grandParentNode.getUserObject();
			if (!(userObject instanceof MolecularType)) {
				System.out.println("Unexpected parent in tree hierarchy for " + mc.getDisplayType() + " " + mc.getDisplayName() + "!");
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
		        int returnCode = JOptionPane.showConfirmDialog(this.getParent().getParent(), errMsg, "Delete " + ComponentStateDefinition.typeName, dialogButton);
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

	private JRadioButton getAnchorAllButton() {
		if (anchorAllButton == null) {
			anchorAllButton = new JRadioButton("No restrictions");
			anchorAllButton.setToolTipText("This Molecule can be present in any Structure (recommended)");
			anchorAllButton.addActionListener(eventHandler);
		}
		return anchorAllButton;
	}
	private JRadioButton getAnchorOnlyButton() {
		if (anchorOnlyButton == null) {
			anchorOnlyButton = new JRadioButton("Only these:");
			anchorOnlyButton.setToolTipText("This Molecule may be present only in the Structures checked below");
			anchorOnlyButton.addActionListener(eventHandler);
		}
		return anchorOnlyButton;
	}

	private void initialize() {
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
//		leftPanel.setBackground(Color.white);
		
		anchorPanel = new JAnchorPanel();
		anchorScrollPanel = new JScrollPane(anchorPanel);
		
		molecularTypeTree = new BioModelNodeEditableTree();
		molecularTypeTreeModel = new MolecularTypeTreeModel(molecularTypeTree);
		molecularTypeTree.setModel(molecularTypeTreeModel);
		molecularTypeTree.setEditable(true);
		molecularTypeTree.setCellRenderer(new RbmMolecularTypeTreeCellRenderer(molecularTypeTree, issueManager));
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
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(getAnchorAllButton());
		bg.add(getAnchorOnlyButton());
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(getAnchorAllButton(), gbc);

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(getAnchorOnlyButton(), gbc);

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.fill = GridBagConstraints.BOTH;
		leftPanel.add(anchorScrollPanel, gbc);
		
		// ------------------------------------------------------------------------------
		
		splitPaneHorizontal.setOneTouchExpandable(true);
		splitPaneHorizontal.setDividerLocation(120);
		splitPaneHorizontal.setResizeWeight(0.1);
		
		Border border = BorderFactory.createLineBorder(Color.gray);
		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder annotationBorder = BorderFactory.createTitledBorder(loweredEtchedBorder, " Annotation and Pathway Links ");
		annotationBorder.setTitleJustification(TitledBorder.LEFT);
		annotationBorder.setTitlePosition(TitledBorder.TOP);
		annotationBorder.setTitleFont(getFont().deriveFont(Font.BOLD));

		shapePanel = new LargeShapePanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(LargeShape stls : molecularTypeShapeList) {
					stls.paintSelf(g);
				}
			}

			@Override
			public DisplayMode getDisplayMode() {
				return DisplayMode.other;
			}

			@Override
			public RuleAnalysisChanged hasStateChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasStateChanged(MolecularComponentPattern molecularComponentPattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasBondChanged(String reactionRuleName, MolecularComponentPattern molecularComponentPattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasBondChanged(MolecularComponentPattern molecularComponentPattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasNoMatch(String reactionRuleName, MolecularTypePattern mtp) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public RuleAnalysisChanged hasNoMatch(MolecularTypePattern molecularTypePattern) {
				return RuleAnalysisChanged.UNCHANGED;
			}

			@Override
			public boolean isViewSingleRow() {
				return true;
			}

			@Override
			public RuleParticipantSignature getSignature() {
				return null;
			}

			@Override
			public GroupingCriteria getCriteria() {
				return null;
			}

		};
		shapePanel.setBorder(border);
		shapePanel.setLayout(null);
		shapePanel.setBackground(Color.white);
		shapePanel.setEditable(true);
		shapePanel.setShowMoleculeColor(true);
		shapePanel.setShowNonTrivialOnly(true);

		shapePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				stopEditing();
				if(e.getButton() == 1) {		// left click selects the object (we highlight it)
					Point whereClicked = e.getPoint();
					PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
					manageMouseActivity(locationContext);
				} else if(e.getButton() == 3) {						// right click invokes popup menu (only if the object is highlighted)
					Point whereClicked = e.getPoint();
					PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
					manageMouseActivity(locationContext);
					if(locationContext.getDeepestShape() != null && !locationContext.getDeepestShape().isHighlighted()) {
						// TODO: (maybe) add code here to highlight the shape if it's not highlighted already but don't show the menu
						// return;
					}					
					showPopupMenu(e, locationContext);
				}
			}
			private void manageMouseActivity(PointLocationInShapeContext locationContext) {
				Graphics g = shapePanel.getGraphics();
				for (MolecularTypeLargeShape mtls : molecularTypeShapeList) {
					mtls.turnHighlightOffRecursive(g);
				}
				for (MolecularTypeLargeShape mtls : molecularTypeShapeList) {
					if (mtls.contains(locationContext)) {		//check if mouse is inside shape
						break;
					}
				}
				locationContext.highlightDeepestShape();
				locationContext.paintDeepestShape(g);
			}
		});
		shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point overWhat = e.getPoint();
				PointLocationInShapeContext locationContext = new PointLocationInShapeContext(overWhat);
				for (MolecularTypeLargeShape mtls : molecularTypeShapeList) {
					if (mtls.contains(locationContext)) {
						break;
					}
				}
				HighlightableShapeInterface hsi = locationContext.getDeepestShape();
				if(hsi == null) {
					shapePanel.setToolTipText(null);
				} else {
					shapePanel.setToolTipText("Right click for " + hsi.getDisplayType() + " menus");
				}
				for (MolecularTypeLargeShape mtls : molecularTypeShapeList) {
					Rectangle r = mtls.getAnchorHotspot();
					if(r != null && r.contains(overWhat)) {
						mtls.getMolecularType();
						shapePanel.setToolTipText(mtls.getAnchorsHTML());
						break;
					}
				}
			} 
		});

		// -------------------------------------------------------------------------------------------
		
		JPanel generalPanel = new JPanel();		// right bottom panel, contains just the annotation
		generalPanel.setBorder(annotationBorder);
		generalPanel.setLayout(new GridBagLayout());

		gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		JLabel pathwayLink = new JLabel("Linked Pathway Object(s): ");
		generalPanel.add(pathwayLink, gbc);
		
		linkedPOScrollPane = new JScrollPane();
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);
		generalPanel.add(linkedPOScrollPane, gbc);
		
		gridy ++;
//		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
//		annotationTextArea.setLineWrap(true);
//		annotationTextArea.setWrapStyleWord(true);
//		annotationTextArea.setFont(new Font("monospaced", Font.PLAIN, 11));
		annotationTextArea = new JTextPane();
		annotationTextArea.setContentType("text/html");
		annotationTextArea.setEditable(false);
		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		generalPanel.add(jsp, gbc);

		scrollPane = new JScrollPane(shapePanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		splitPaneHorizontal.setTopComponent(scrollPane);
		splitPaneHorizontal.setBottomComponent(generalPanel);

		// -----------------------------------------------------------------------------
		splitPane.setOneTouchExpandable(true);
		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(splitPaneHorizontal);
		
		splitPane.setResizeWeight(0.0);
		splitPane.getLeftComponent().setMaximumSize(new Dimension(120, 200));
		splitPane.getLeftComponent().setPreferredSize(new Dimension(120, 200));
//		splitPane.setDividerLocation(0.0d);		// completely hides the left component
		splitPane.setDividerLocation(-1);		// attempt to use the preferred size

		setName("MolecularTypePropertiesPanel");
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		setBackground(Color.white);

		annotationTextArea.addFocusListener(eventHandler);
		annotationTextArea.addMouseListener(eventHandler);
	}
	
	private JMenuItem getAddFromTreeMenuItem() {
		if (addFromTreeMenuItem == null) {
			addFromTreeMenuItem = new JMenuItem("Add");
			addFromTreeMenuItem.addActionListener(eventHandler);
		}
		return addFromTreeMenuItem;
	}
	private JMenuItem getRenameFromTreeMenuItem() {
		if (renameFromTreeMenuItem == null) {
			renameFromTreeMenuItem = new JMenuItem("Rename");
			renameFromTreeMenuItem.addActionListener(eventHandler);
		}
		return renameFromTreeMenuItem;
	}
//	private JMenuItem getRenameFromShapeMenuItem() {
//		if (renameFromShapeMenuItem == null) {
//			renameFromShapeMenuItem = new JMenuItem("Rename");
//			renameFromShapeMenuItem.addActionListener(eventHandler);
//		}
//		return renameFromShapeMenuItem;
//	}
	private JMenuItem getDeleteFromTreeMenuItem() {
		if (deleteFromTreeMenuItem == null) {
			deleteFromTreeMenuItem = new JMenuItem("Delete");
			deleteFromTreeMenuItem.addActionListener(eventHandler);
		}
		return deleteFromTreeMenuItem;
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		MolecularType molecularType = null;
		if (selectedObjects.length == 1 && selectedObjects[0] instanceof MolecularType) {
			molecularType = (MolecularType) selectedObjects[0];
		}
		setMolecularType(molecularType);
		if(molecularType != null) {
			// we want to start with a "normal" state for the depiction (not highlighted).
			shapePanel.setHighlightedRecursively(molecularType, LargeShapePanel.Highlight.off);
			
			final boolean bAnchorAll = molecularType.isAnchorAll();
			if(bAnchorAll) {
				getAnchorAllButton().setSelected(true);
			} else {
				getAnchorOnlyButton().setSelected(true);
			}
			anchorPanel.removeAll();	// components
			anchorPanel.initialize();
			anchorPanel.enableAll(!bAnchorAll);
			anchorPanel.setAnchors();
			anchorPanel.validate();		// this must be called after finished removing or adding components to a panel
		}
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
		updateInterface();
	}

	public static final int xOffsetInitial = 20;
//	public static final int yOffsetInitial = 60;
	public static final int yOffsetInitial = 20;
	public void updateInterface() {
		boolean bNonNullMolecularType = molecularType != null && bioModel != null;
//		annotationTextArea.setEditable(bNonNullMolecularType);
		if (bNonNullMolecularType) {
			VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
			annotationTextArea.setText(vcMetaData.getFreeTextAnnotation(molecularType));
			annotationTextArea.setCaretPosition(0);
//			annotationTextArea.setText(molecularType.comment);
			
//			titleLabel.setText("Properties for " + molecularType.getDisplayType() + ": " + molecularType.getDisplayName());
			titleLabel.setText("Anchor Molecule");
			titleLabel.setToolTipText("Anchor a Molecule to a Structure to prevent it being moved to unwanted locations by a rule.");
			molecularTypeShapeList.clear();
			int maxYOffset = computeStatesVerticalOffset(molecularType);
			MolecularTypeLargeShape stls = new MolecularTypeLargeShape(xOffsetInitial, maxYOffset, molecularType, shapePanel, molecularType, issueManager);
			molecularTypeShapeList.add(stls);

			int maxXOffset = xOffsetInitial + stls.getWidth();
			
			Dimension preferredSize = new Dimension(maxXOffset+100, maxYOffset+60);
			shapePanel.setPreferredSize(preferredSize);
			shapePanel.repaint();
//			splitPane.getRightComponent().repaint();
		} else {
			annotationTextArea.setText(null);
		}
		listLinkedPathwayObjects();
	}
	private String listLinkedPathwayObjects(){
		if (molecularType == null) {
			return "no selected molecule";
		}
		if(bioModel == null || bioModel.getModel() == null){
			return "no biomodel";
		}
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		String linkedPOlist = "";
		for(RelationshipObject relObject : bioModel.getRelationshipModel().getRelationshipObjects(molecularType)){
			final BioPaxObject bpObject = relObject.getBioPaxObject();
			if(bpObject instanceof Entity){
				JLabel label = new JLabel("<html><u>" + ((Entity)bpObject).getName().get(0) + "</u></html>");
				label.setForeground(Color.blue);
				label.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram),new Object[]{bpObject});
						}
					}
				});
				panel.add(label);
			}
		}
		Dimension dim = new Dimension(200, 20);
		panel.setMinimumSize(dim);
		panel.setPreferredSize(dim);
		linkedPOScrollPane.setViewportView(panel);
		return linkedPOlist;
	}
	private int computeStatesVerticalOffset(MolecularType mt) {
		if(mt.getComponentList().size() == 0) {
			return yOffsetInitial;
		}
		int maxNumberOfStates = 0;	// we need to find the highest number of states per component
		for(MolecularComponent mc : mt.getComponentList()) {
			maxNumberOfStates = Math.max(maxNumberOfStates, mc.getComponentStateDefinitions().size());
		}
		if(maxNumberOfStates < 2) {		// no need to add any offset if there's just 1 state defined, it fits inside the body of the molecule shape
			return yOffsetInitial;

		} else {
			int stateHeight = MolecularComponentLargeShape.computeStateHeight(shapePanel.getGraphics(), shapePanel);
			return yOffsetInitial + stateHeight * (maxNumberOfStates-1);
		}
	}
	
//	private void changeFreeTextAnnotation() {
//		try{
//			if (molecularType == null) {
//				return;
//			}
//			// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
//			if(bioModel.getModel() != null && bioModel.getModel().getVcMetaData() != null){
//				VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
//				String textAreaStr = (annotationTextArea.getText() == null || annotationTextArea.getText().length()==0?null:annotationTextArea.getText());
//				if(!Compare.isEqualOrNull(vcMetaData.getFreeTextAnnotation(molecularType),textAreaStr)){
//					vcMetaData.setFreeTextAnnotation(molecularType, textAreaStr);	
//				}
//			}
//		} catch(Exception e){
//			e.printStackTrace(System.out);
//			PopupGenerator.showErrorDialog(this,"Edit Molecule Error\n"+e.getMessage(), e);
//		}
//	}

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
	
	private void stopEditing(){
		shapePanel.requestFocus();
		if(shapePanel.getComponentCount() > 0){
			shapePanel.remove(0);
		}
		shapePanel.validate();
		shapePanel.repaint();
	}
	private void editInPlace(final LargeShape selectedShape){
		if(shapePanel.getComponentCount() > 0){		// remove if there's one already present
			// this is the only component that may exist in this panel
			shapePanel.remove(0);
		}
		Rectangle labelOutline = selectedShape.getLabelOutline();
		Font font = selectedShape.getLabelFont();
		
	//Add press 'Enter' action, 'Escape' action, editor gets focus and mouse 'Exit' parent action
		final JTextField jTextField = new JTextField(selectedShape.getFullName());
		jTextField.setFont(font);
		jTextField.selectAll();
		jTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					String name = jTextField.getText();
					//Type specific edit actions
					if(selectedShape instanceof MolecularTypeLargeShape){
						((MolecularTypeLargeShape)selectedShape).getMolecularType().setName(name);
					}else if(selectedShape instanceof MolecularComponentLargeShape){
						((MolecularComponentLargeShape)selectedShape).getMolecularComponent().setName(name);
					}else if(selectedShape instanceof ComponentStateLargeShape){
						((ComponentStateLargeShape)selectedShape).getComponentStateDefinition().setName(name);
					}
				}catch(Exception e2){
					e2.printStackTrace();
					DialogUtils.showErrorDialog(shapePanel, e2.getMessage());
					shapePanel.remove(0);
					updateInterface();
				}
				if(shapePanel.getComponentCount() > 0){
					shapePanel.remove(0);
				}
			}
		});
		InputMap im = jTextField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = jTextField.getActionMap();
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "cancelChange");
        am.put("cancelChange", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				shapePanel.remove(0);
				updateInterface();
			}
		});
        final int MinWidth = 30;
		jTextField.setBounds(labelOutline.x,labelOutline.y,Math.max(labelOutline.width, MinWidth), labelOutline.height);
		shapePanel.add(jTextField);
//		shapePanel.validate();
		jTextField.requestFocus();
		return;
	}
	
	private void showPopupMenu(MouseEvent e, PointLocationInShapeContext locationContext) {
		if (popupFromShapeMenu == null) {
			popupFromShapeMenu = new JPopupMenu();			
		}		
		if (popupFromShapeMenu.isShowing()) {
			return;
		}
		
		final Object deepestShape = locationContext.getDeepestShape();
		final Object selectedObject;

		if(deepestShape == null) {
			selectedObject = null;
			System.out.println("outside");		// when cursor is outside there's nothing to do  ???
			return;
		} else if(deepestShape instanceof ComponentStateLargeShape) {
			System.out.println("inside state");
			if(((ComponentStateLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((ComponentStateLargeShape)deepestShape).getComponentStateDefinition();
			} else {
				return;		// right click only works on highlighted entity, if it's not highlighted we simply return
			}
		} else if(deepestShape instanceof MolecularComponentLargeShape) {
			System.out.println("inside component");
			if(((MolecularComponentLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((MolecularComponentLargeShape)deepestShape).getMolecularComponent();
			} else {
				return;
			}
		} else if(deepestShape instanceof MolecularTypeLargeShape) {
			System.out.println("inside molecule");
			if(((MolecularTypeLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((MolecularTypeLargeShape)deepestShape).getMolecularType();
			} else {
				return;
			}
		} else if(deepestShape instanceof SpeciesPatternLargeShape) {	// this cannot happen, here just for symmetry
			System.out.println("inside species pattern");
			if(((SpeciesPatternLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((SpeciesPatternLargeShape)deepestShape).getSpeciesPattern();
			} else {
				return;
			}
		} else {
			selectedObject = null;
			System.out.println("inside something else?");
			return;
		}
		System.out.println(selectedObject);		

		boolean bDelete = false;
		boolean bAdd = false;
		popupFromShapeMenu.removeAll();
		Point mousePoint = e.getPoint();
		
		if (selectedObject instanceof MolecularType) {				// rename, add
			if(selectedObject != molecularType) {
				throw new RuntimeException("The selected object from shape different from the current object");
			}
			JMenuItem renamMenuItem = new JMenuItem("Rename");
			popupFromShapeMenu.add(renamMenuItem);

			JMenuItem addMenuItem = new JMenuItem("Add " + MolecularComponent.typeName);
//			Icon icon = new MolecularTypeSmallShape(1, 4, mt, gc, mt);
//			menuItem.setIcon(icon);
			popupFromShapeMenu.add(new JSeparator());
			popupFromShapeMenu.add(addMenuItem);
			addMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MolecularComponent molecularComponent = molecularType.createMolecularComponent();
					molecularType.addMolecularComponent(molecularComponent);
					bioModel.getModel().getRbmModelContainer().adjustSpeciesContextPatterns(molecularType, molecularComponent);
					bioModel.getModel().getRbmModelContainer().adjustObservablesPatterns(molecularType, molecularComponent);
					bioModel.getModel().getRbmModelContainer().adjustRulesPatterns(molecularType, molecularComponent);
//					editInPlace((LargeShape)deepestShape);
				}
			});
			renamMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editInPlace((LargeShape)deepestShape);
				}
			});
		} else if (selectedObject instanceof MolecularComponent) {		// move left / right / separator / rename, delete, separator, add
			
			String moveRightMenuText = "Move <b>" + "right" + "</b>";
			moveRightMenuText = "<html>" + moveRightMenuText + "</html>";
			JMenuItem moveRightMenuItem = new JMenuItem(moveRightMenuText);
			Icon icon = VCellIcons.moveRightIcon;
			moveRightMenuItem.setIcon(icon);
			moveRightMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MolecularComponent from = (MolecularComponent)selectedObject;
					List<MolecularComponent> mcList = molecularType.getComponentList();
					int fromIndex = mcList.indexOf(from);
					if(mcList.size() == fromIndex+1) {		// already the last element
						return;
					}
					int toIndex = fromIndex+1;
					MolecularComponent to = mcList.remove(toIndex);
					mcList.add(fromIndex, to);
					molecularTypeTreeModel.populateTree();
					molecularType.firePropertyChange("entityChange", null, "bbb");
				}
			});
			popupFromShapeMenu.add(moveRightMenuItem);
			
			String moveLeftMenuText = "Move <b>" + "left" + "</b>";
			moveLeftMenuText = "<html>" + moveLeftMenuText + "</html>";
			JMenuItem moveLeftMenuItem = new JMenuItem(moveLeftMenuText);
			icon = VCellIcons.moveLeftIcon;
			moveLeftMenuItem.setIcon(icon);
			moveLeftMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MolecularComponent from = (MolecularComponent)selectedObject;
					List<MolecularComponent> mcList = molecularType.getComponentList();
					int fromIndex = mcList.indexOf(from);
					if(fromIndex == 0) {			// already the first element
						return;
					}
					int toIndex = fromIndex-1;
					MolecularComponent to = mcList.remove(toIndex);
					mcList.add(fromIndex, to);
					molecularTypeTreeModel.populateTree();
					molecularType.firePropertyChange("entityChange", null, "bbb");
				}
			});
			popupFromShapeMenu.add(moveLeftMenuItem);
			popupFromShapeMenu.add(new JSeparator());
			
			JMenuItem renamMenuItem = new JMenuItem("Rename");
			popupFromShapeMenu.add(renamMenuItem);

			JMenuItem addMenuItem = new JMenuItem("Add " + ComponentStateDefinition.typeName);
			JMenuItem deleteMenuItem = new JMenuItem("Delete ");
			popupFromShapeMenu.add(deleteMenuItem);
			popupFromShapeMenu.add(new JSeparator());
			popupFromShapeMenu.add(addMenuItem);
			deleteMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MolecularComponent mc = (MolecularComponent) selectedObject;
					// if there are states we ask the user to delete them individually first
					// detailed verifications will be done there, to see if they are being used in reactions, species, observables
					if(!mc.getComponentStateDefinitions().isEmpty()) {
						String[] options = {"OK"};
						String errMsg = mc.getDisplayType() + " '<b>" + mc.getDisplayName() + "</b>' cannot be deleted because it contains explicit States.";
						errMsg += "<br>Please delete each individual State first.";
						errMsg += "<br><br>Detailed usage information will be provided at that time to help you decide.";
						errMsg = "<html>" + errMsg + "</html>";
						JOptionPane.showOptionDialog(shapePanel, errMsg, "Delete " + mc.getDisplayType(), JOptionPane.NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options , options[0]);
						return;
					}
					// we find and display component usage information to help the user decide
					Map<String, Pair<Displayable, SpeciesPattern>> usedHere = new LinkedHashMap<String, Pair<Displayable, SpeciesPattern>>();
					bioModel.getModel().getRbmModelContainer().findComponentUsage(molecularType, mc, usedHere);
					if(!usedHere.isEmpty()) {
						String errMsg = mc.dependenciesToHtml(usedHere);
						errMsg += "<br><br>Delete anyway?";
						errMsg = "<html>" + errMsg + "</html>";
				        int dialogButton = JOptionPane.YES_NO_OPTION;
				        int returnCode = JOptionPane.showConfirmDialog(shapePanel, errMsg, "Delete " + mc.getDisplayType(), dialogButton);
						if (returnCode == JOptionPane.YES_OPTION) {
							// keep this code in sync with MolecularTypeTableModel.setValueAt
							if(bioModel.getModel().getRbmModelContainer().delete(molecularType, mc) == true) {
								molecularType.removeMolecularComponent(mc);
							}
						}
					} else {
						if(bioModel.getModel().getRbmModelContainer().delete(molecularType, mc) == true) {
							molecularType.removeMolecularComponent(mc);
						}
					}
				}
			});
			addMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MolecularComponent mc = (MolecularComponent) selectedObject;
					ComponentStateDefinition componentStateDefinition = mc.createComponentStateDefinition();
					mc.addComponentStateDefinition(componentStateDefinition);
					bioModel.getModel().getRbmModelContainer().adjustObservablesPatterns(molecularType, mc, componentStateDefinition);
					bioModel.getModel().getRbmModelContainer().adjustRulesPatterns(molecularType, mc, componentStateDefinition);
					bioModel.getModel().getRbmModelContainer().adjustSpeciesPatterns(molecularType, mc, componentStateDefinition);
//					editInPlace((LargeShape)deepestShape);
				}
			});
			renamMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editInPlace((LargeShape)deepestShape);
				}
			});
		} else if (selectedObject instanceof ComponentStateDefinition) {		// rename, delete
			
			JMenuItem renamMenuItem = new JMenuItem("Rename");
			popupFromShapeMenu.add(renamMenuItem);

			JMenuItem deleteMenuItem = new JMenuItem("Delete");
			popupFromShapeMenu.add(deleteMenuItem);
			deleteMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ComponentStateDefinition csd = (ComponentStateDefinition) selectedObject;
					MolecularComponent mc = locationContext.mcs.getMolecularComponent();	// must exist, we're deleting one of its states
					Map<String, Pair<Displayable, SpeciesPattern>> usedHere = new LinkedHashMap<String, Pair<Displayable, SpeciesPattern>>();
					bioModel.getModel().getRbmModelContainer().findStateUsage(molecularType, mc, csd, usedHere);
					if(!usedHere.isEmpty()) {
						String errMsg = csd.dependenciesToHtml(usedHere);
						errMsg += "<br><br>Delete anyway?";
						errMsg = "<html>" + errMsg + "</html>";
				        int dialogButton = JOptionPane.YES_NO_OPTION;
				        int returnCode = JOptionPane.showConfirmDialog(shapePanel, errMsg, "Delete " + ComponentStateDefinition.typeName, dialogButton);
						if (returnCode == JOptionPane.YES_OPTION) {
							// keep this code in sync with MolecularTypeTableModel.setValueAt
							if(bioModel.getModel().getRbmModelContainer().delete(molecularType, mc, csd) == true) {
								mc.deleteComponentStateDefinition(csd);
							}
						}
					} else {
						if(bioModel.getModel().getRbmModelContainer().delete(molecularType, mc, csd) == true) {
							mc.deleteComponentStateDefinition(csd);
						}
					}
				}
			});
			renamMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editInPlace((LargeShape)deepestShape);
				}
			});
		}
		popupFromShapeMenu.show(e.getComponent(), mousePoint.x, mousePoint.y);
	}
	
	@Deprecated
	private void swapComponentPatterns(MolecularType mt, MolecularComponent a, MolecularComponent b) {
		Model model = bioModel.getModel();
		if(model == null) {
			return;
		}
		for(RbmObservable o : model.getRbmModelContainer().getObservableList()) {
			for(SpeciesPattern sp : o.getSpeciesPatternList()) {
				for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
					if(mtp.getMolecularType() != mt) {
						continue;
					}
					mtp.swapComponentPatterns(a, b);
				}
			}
		}
		for(SpeciesContext sc : model.getSpeciesContexts()) {
			if(!sc.hasSpeciesPattern()) {
				continue;
			}
			for(MolecularTypePattern mtp : sc.getSpeciesPattern().getMolecularTypePatterns()) {
				if(mtp.getMolecularType() != mt) {
					continue;
				}
				mtp.swapComponentPatterns(a, b);
			}
		}
		for(ReactionRule r : model.getRbmModelContainer().getReactionRuleList()) {
			for(ReactantPattern rp : r.getReactantPatterns()) {
				SpeciesPattern sp = rp.getSpeciesPattern();
				for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
					if(mtp.getMolecularType() != mt) {
						continue;
					}
					mtp.swapComponentPatterns(a, b);
				}
			}
			for(ProductPattern pp : r.getProductPatterns()) {
				SpeciesPattern sp = pp.getSpeciesPattern();
				for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
					if(mtp.getMolecularType() != mt) {
						continue;
					}
					mtp.swapComponentPatterns(a, b);
				}
			}
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
				getAddFromTreeMenuItem().setText("Add " + MolecularComponent.typeName);
				bAdd = true;
				bDelete = false;
			} else if (userObject instanceof MolecularComponent) {
				getAddFromTreeMenuItem().setText("Add " + ComponentStateDefinition.typeName);
				bAdd = true;
				bDelete = true;
			} else if (userObject instanceof ComponentStateDefinition) {
				bAdd = false;
				bDelete = true;
			}
			
		}
		popupMenu.removeAll();
		// everything can be renamed
		popupMenu.add(getRenameFromTreeMenuItem());
		if (bDelete) {
			popupMenu.add(getDeleteFromTreeMenuItem());
		}
		popupMenu.add(new JSeparator());
		if (bAdd) {
			popupMenu.add(getAddFromTreeMenuItem());
		}

		Point mousePoint = e.getPoint();
		popupMenu.show(molecularTypeTree, mousePoint.x, mousePoint.y);
	}
	
	public void setBioModel(BioModel newValue) {
		bioModel = newValue;
	}	
}
