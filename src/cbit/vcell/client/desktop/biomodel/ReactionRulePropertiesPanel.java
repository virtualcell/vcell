package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
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
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.ReactionRulePropertiesTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;


@SuppressWarnings("serial")

public class ReactionRulePropertiesPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private ReactionRule reactionRule = null;
	private JLabel titleLabel = null;

	private JTree reactantTree = null;
	private JTree productTree = null;
	private ReactionRulePropertiesTreeModel reactantTreeModel = null;
	private ReactionRulePropertiesTreeModel productTreeModel = null;
	private ReactionRulePropertiesTableModel tableModel = null;

	private ScrollTable table = null;

	private JPanel productPanel;
	private JPanel reactantPanel;
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private JSplitPane splitPaneTrees = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	private JTree rightClickSourceTree;
	private JEditorPane warningPane;
	private JCheckBox showDetailsCheckBox;

	private JPopupMenu popupMenu;
	private JMenu addMenu;
	private JMenuItem addReactantMenuItem;
	private JMenuItem addProductMenuItem;
	private JMenuItem deleteMenuItem;	
	private JMenuItem editMenuItem;

	private InternalEventHandler eventHandler = new InternalEventHandler();

	
	private class BioModelNodeEditableTree extends JTree {
		@Override
		public boolean isPathEditable(TreePath path) {
			Object object = path.getLastPathComponent();
			return object instanceof BioModelNode;
		}
	}
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener, TreeSelectionListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == reactionRule) {
				if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REACTANT_WARNING)) {
					Object warning = evt.getNewValue();
				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_PRODUCT_WARNING)) {
					Object warning = evt.getNewValue();
				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_NAME)) {
					updateTitleLabel();
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getAddReactantMenuItem()) {
				addReactant();
			} else if (e.getSource() == getAddProductMenuItem()) {
				addProduct();
			} else if (e.getSource() == getDeleteMenuItem()) {
				delete();
			} else if (e.getSource() == getEditMenuItem()) {
				editEntity();
			} else if (e.getSource() == showDetailsCheckBox) {
				reactantTreeModel.setShowDetails(showDetailsCheckBox.isSelected());
				productTreeModel.setShowDetails(showDetailsCheckBox.isSelected());
			}
		}

		public void mouseClicked(MouseEvent e) {			
		}

		public void mousePressed(MouseEvent e) {
			if (!e.isConsumed() && (e.getSource() == reactantTree || e.getSource() == productTree)) {
				showPopupMenu(e);
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (!e.isConsumed() && (e.getSource() == reactantTree || e.getSource() == productTree)) {
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
	
	public ReactionRulePropertiesPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		try {
			showDetailsCheckBox = new JCheckBox("Show All Components");
			showDetailsCheckBox.addActionListener(eventHandler);

			table = new ScrollTable();
			tableModel = new ReactionRulePropertiesTableModel(table,true);
			table.setModel(tableModel);
			
			splitPaneHorizontal.setOneTouchExpandable(true);
			splitPaneHorizontal.setDividerLocation(80);
			splitPaneHorizontal.setResizeWeight(0.35);
			
			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new GridBagLayout());

			splitPaneHorizontal.setTopComponent(table.getEnclosingScrollPane());
			splitPaneHorizontal.setBottomComponent(lowerPanel);
			
			splitPaneTrees.setResizeWeight(0.5);
			splitPaneTrees.setDividerLocation(340);		// absolute value
//			splitPaneTrees.setDividerLocation(0.5);		// relative value?
			splitPaneTrees.setDividerSize(6);
			splitPaneTrees.setOneTouchExpandable(true);

			reactantTree = new BioModelNodeEditableTree();
			reactantTreeModel = new ReactionRulePropertiesTreeModel(reactantTree, ReactionRuleParticipantType.Reactant);
			reactantTree.setModel(reactantTreeModel);
			reactantTree.setEditable(true);
			RbmReactionParticipantTreeCellRenderer crr = new RbmReactionParticipantTreeCellRenderer();
			reactantTree.setCellRenderer(crr);
			reactantTree.setCellEditor( new DisabledTreeCellEditor(reactantTree, (RbmReactionParticipantTreeCellRenderer)reactantTree.getCellRenderer()) );
			reactantTree.setEditable(false);
//			reactantTree.setCellEditor(new RbmTreeCellEditor(reactantTree));
			
			int rowHeight = reactantTree.getRowHeight();
			if (rowHeight < 10) { 
				rowHeight = 20; 
			}
			reactantTree.setRowHeight(rowHeight + 5);
			reactantTree.setLargeModel(true);
			reactantTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			ToolTipManager.sharedInstance().registerComponent(reactantTree);
			reactantTree.addTreeSelectionListener(eventHandler);
			reactantTree.addMouseListener(eventHandler);
			reactantTree.setRootVisible(false);

			productTree = new BioModelNodeEditableTree();
			productTreeModel = new ReactionRulePropertiesTreeModel(productTree, ReactionRuleParticipantType.Product);
			productTree.setModel(productTreeModel);
			productTree.setLargeModel(true);
			productTree.setEditable(true);
			productTree.setCellRenderer(new RbmReactionParticipantTreeCellRenderer());
			RbmReactionParticipantTreeCellRenderer crp = new RbmReactionParticipantTreeCellRenderer();
			productTree.setCellRenderer(crp);
			productTree.setCellEditor( new DisabledTreeCellEditor(productTree, (RbmReactionParticipantTreeCellRenderer)productTree.getCellRenderer()) );
			productTree.setEditable(false);
			//productTree.setCellEditor(new RbmTreeCellEditor(productTree));
			
			rowHeight = productTree.getRowHeight();
			if (rowHeight < 10) { 
				rowHeight = 20; 
			}
			productTree.setRowHeight(rowHeight + 5);
			productTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			ToolTipManager.sharedInstance().registerComponent(productTree);
			productTree.addTreeSelectionListener(eventHandler);
			productTree.addMouseListener(eventHandler);
			productTree.setRootVisible(false);
			
			Dimension minimumSize = new Dimension(100, 50);		//provide minimum sizes for the two components in the split pane
			table.getEnclosingScrollPane().setMinimumSize(minimumSize);
			splitPaneHorizontal.setMinimumSize(minimumSize);
			lowerPanel.setMinimumSize(minimumSize);
			minimumSize = new Dimension(100, 30);		
			splitPaneTrees.setMinimumSize(minimumSize);

			Color lableColor = new Color(0xF0F8FF);
			reactantPanel = new JPanel();		
			reactantPanel.setLayout(new GridBagLayout());
			reactantPanel.setBackground(Color.white);
			
			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			JLabel label = new JLabel("Reactants");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
			p.add(label);
			p.setBackground(lableColor);
			p.setBorder(GuiConstants.TAB_PANEL_BORDER);
			reactantPanel.add(p, gbc);
			
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			reactantPanel.add(new JScrollPane(reactantTree), gbc);
					
			productPanel = new JPanel();		
			productPanel.setLayout(new GridBagLayout());
			productPanel.setBackground(Color.white);
			
			gridy = 0;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			label = new JLabel("Products");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			p = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
			p.add(label);
			p.setBackground(lableColor);
			p.setBorder(GuiConstants.TAB_PANEL_BORDER);
			productPanel.add(p, gbc);
			
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			productPanel.add(new JScrollPane(productTree), gbc);

			splitPaneTrees.setPreferredSize(new Dimension(60, 1200));	// TODO: find better solution for the extra scrollbars!
			splitPaneTrees.setLeftComponent(reactantPanel);
			splitPaneTrees.setRightComponent(productPanel);

			gridy = 0;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.insets = new Insets(2,2,0,2);
			lowerPanel.add(showDetailsCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2,2,0,2);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			titleLabel = new JLabel("Reaction Rule");
			titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
			lowerPanel.add(titleLabel, gbc);
			
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(2,2,2,2);
			gbc.fill = GridBagConstraints.BOTH;
			lowerPanel.add(new JScrollPane(splitPaneTrees), gbc);
			
			setLayout(new BorderLayout());
			add(splitPaneHorizontal, BorderLayout.CENTER);
			setBackground(Color.white);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	public void addReactant() {
		ReactantPattern reactant = new ReactantPattern(new SpeciesPattern());
		reactionRule.addReactant(reactant);
		final TreePath path = reactantTreeModel.findObjectPath(null, reactant);
		reactantTree.setSelectionPath(path);
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {				
				reactantTree.scrollPathToVisible(path);
			}
		});
	}
	
	public void addProduct() {
		ProductPattern product = new ProductPattern(new SpeciesPattern());
		reactionRule.addProduct(product);
		final TreePath path = productTreeModel.findObjectPath(null, product);
		productTree.setSelectionPath(path);
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {				
				productTree.scrollPathToVisible(path);
			}
		});
	}
	
	public void editEntity() {
		rightClickSourceTree.startEditingAtPath(rightClickSourceTree.getSelectionPath());
	}
	
	public void delete() {
		Object obj = rightClickSourceTree.getLastSelectedPathComponent();
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
		if (selectedUserObject instanceof ReactionRuleParticipantLocal){
			ReactionRuleParticipantLocal reactionRuleParticipant = (ReactionRuleParticipantLocal) selectedUserObject;
			switch (reactionRuleParticipant.type) {
			case Reactant:
				reactionRule.removeReactant(new ReactantPattern(reactionRuleParticipant.speciesPattern.getSpeciesPattern()));
				break;
			case Product:
				reactionRule.removeProduct(new ProductPattern(reactionRuleParticipant.speciesPattern.getSpeciesPattern()));
				break;
			}
		} else if (selectedUserObject instanceof MolecularTypePattern){
			MolecularTypePattern mtp = (MolecularTypePattern) selectedUserObject;
			Object parentObject = parentNode.getUserObject();
			if (parentObject instanceof ReactionRuleParticipantLocal) {
				ReactionRuleParticipantLocal rrp = (ReactionRuleParticipantLocal) parentObject;
				rrp.speciesPattern.getSpeciesPattern().removeMolecularTypePattern(mtp);
			}
		}
	}
	
	private JMenu getAddMenu() {
		if (addMenu == null) {
			addMenu = new JMenu("Add");
			addMenu.addActionListener(eventHandler);
		}
		return addMenu;
	}
	
	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem("Delete");
			deleteMenuItem.addActionListener(eventHandler);
		}
		return deleteMenuItem;
	}
	
	private JMenuItem getAddReactantMenuItem() {
		if (addReactantMenuItem == null) {
			addReactantMenuItem = new JMenuItem("Add Reactant");
			addReactantMenuItem.addActionListener(eventHandler);
		}
		return addReactantMenuItem;
	}
	
	private JMenuItem getAddProductMenuItem() {
		if (addProductMenuItem == null) {
			addProductMenuItem = new JMenuItem("Add Product");
			addProductMenuItem.addActionListener(eventHandler);
		}
		return addProductMenuItem;
	}
	
	private JMenuItem getEditMenuItem() {
		if (editMenuItem == null) {
			editMenuItem = new JMenuItem("Edit");
			editMenuItem.addActionListener(eventHandler);
		}
		return editMenuItem;
	}
	
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}

	public void setBioModel(BioModel newValue) {
		if (bioModel == newValue) {
			return;
		}
		bioModel = newValue;
		reactantTreeModel.setBioModel(bioModel);
		productTreeModel.setBioModel(bioModel);
	}	
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) {
			setReactionRule(null);
		} else if (selectedObjects[0] instanceof ReactionRule) {
			setReactionRule((ReactionRule) selectedObjects[0]);
		} else {
			setReactionRule(null);
		}
	}

	private void setReactionRule(ReactionRule newValue) {
		if (reactionRule == newValue) {
			return;
		}
		ReactionRule oldValue = reactionRule;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}
		// commit the changes before switch to another reaction step
//		changeName();
		
		reactionRule = newValue;
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
		}
		productTreeModel.setReactionRule(reactionRule);
		reactantTreeModel.setReactionRule(reactionRule);
		tableModel.setReactionRule(reactionRule);
		refreshInterface();

		
		
	}

	protected void refreshInterface() {
		if (reactionRule == null){	// sanity check
			return;
		}
//		ArrayList<ReactionRuleObjectProperty> propertyList = new ArrayList<ReactionRuleObjectProperty>();
//
//		if(!(reactionRule instanceof SBEntity)) {
//			tableModel.setData(propertyList);
//			return;
//		}
//		SBEntity sbEntity = (SBEntity) reactionRule;
//		if (!(sbEntity instanceof Entity)){
//			tableModel.setData(propertyList);
//			return;
//		}
//		Entity entity = (Entity) sbEntity;
//		tableModel.setData(propertyList);
		updateTitleLabel();
	}
	
	private void updateTitleLabel() {
		if (reactionRule != null) {
			titleLabel.setText("Properties for Reaction Rule: " + reactionRule.getName());
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
		popupMenu.removeAll();
		Point mousePoint = e.getPoint();
		if (e.getSource() == reactantTree) {
			rightClickSourceTree = reactantTree;
			GuiUtils.selectClickTreePath(reactantTree, e);
			TreePath clickPath = reactantTree.getPathForLocation(mousePoint.x, mousePoint.y);
		    if (clickPath == null) {
		    	popupMenu.add(getAddReactantMenuItem());
		    } else {
				TreePath[] selectedPaths = reactantTree.getSelectionPaths();
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
					
					if (selectedObject instanceof ReactionRuleParticipantLocal) {
						getAddMenu().setText("Specify Molecular Type");
						getAddMenu().removeAll();
						for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
							JMenuItem menuItem = new JMenuItem(mt.getName());
							getAddMenu().add(menuItem);
							menuItem.addActionListener(new ActionListener() {
								
								public void actionPerformed(ActionEvent e) {
									MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
									((ReactionRuleParticipantLocal)selectedObject).speciesPattern.getSpeciesPattern().addMolecularTypePattern(molecularTypePattern);
									final TreePath path = reactantTreeModel.findObjectPath(null, molecularTypePattern);
									reactantTree.setSelectionPath(path);
									SwingUtilities.invokeLater(new Runnable() {
										
										public void run() {				
											reactantTree.scrollPathToVisible(path);
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
						manageComponentPattern(reactantTreeModel, reactantTree, selectedNode, selectedObject);
						bDelete = true;
					}
				}
		    }
		} else if (e.getSource() == productTree) {
			rightClickSourceTree = productTree;
			GuiUtils.selectClickTreePath(productTree, e);
			TreePath clickPath = productTree.getPathForLocation(mousePoint.x, mousePoint.y);
		    if (clickPath == null) {
		    	popupMenu.add(getAddProductMenuItem());
		    } else {
		    	TreePath[] selectedPaths = productTree.getSelectionPaths();
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
					if (selectedObject instanceof ReactionRuleParticipantLocal) {
						getAddMenu().setText("Specify Molecular Type");
						getAddMenu().removeAll();
//						List<MolecularTypePattern> missingMoleculesInProducts = reactionRule.getMissingMoleculesInProducts();
//						if (missingMoleculesInProducts.size() > 0) {
//							bAdd = true;
//						}
						for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
							JMenuItem menuItem = new JMenuItem(mt.getName());
							getAddMenu().add(menuItem);
							menuItem.addActionListener(new ActionListener() {
								
								public void actionPerformed(ActionEvent e) {
									MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
									((ReactionRuleParticipantLocal)selectedObject).speciesPattern.getSpeciesPattern().addMolecularTypePattern(molecularTypePattern);
									final TreePath path = productTreeModel.findObjectPath(null, molecularTypePattern);
									productTree.setSelectionPath(path);
									SwingUtilities.invokeLater(new Runnable() {
										
										public void run() {				
											productTree.scrollPathToVisible(path);
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
						manageComponentPattern(productTreeModel, productTree, selectedNode, selectedObject);
						bDelete = true;
						bDelete = true;
					}
				}
		    }
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
		popupMenu.show((Component) e.getSource(), mousePoint.x, mousePoint.y);
	}

	public void manageComponentPattern(final ReactionRulePropertiesTreeModel treeModel, final JTree tree,
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
			if(parentObject != null && parentObject instanceof ReactionRuleParticipantLocal) {
				sp = ((ReactionRuleParticipantLocal)parentObject).speciesPattern.getSpeciesPattern();
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
