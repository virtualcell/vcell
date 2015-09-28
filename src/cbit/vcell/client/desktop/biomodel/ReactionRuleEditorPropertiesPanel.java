package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
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

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ParticipantMatchLabelLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.MolecularComponentLargeShape;
import cbit.vcell.graph.MolecularTypeLargeShape;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.MolecularComponentLargeShape.ComponentStateLargeShape;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.Structure;
import cbit.vcell.model.common.VCellErrorMessages;


@SuppressWarnings("serial")

public class ReactionRuleEditorPropertiesPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private ReactionRule reactionRule = null;
//	private JLabel titleLabel = null;

	private JTree reactantTree = null;
	private JTree productTree = null;
	private ReactionRulePropertiesTreeModel reactantTreeModel = null;
	private ReactionRulePropertiesTreeModel productTreeModel = null;
	
	List<SpeciesPatternLargeShape> reactantPatternShapeList = new ArrayList<SpeciesPatternLargeShape>();
	List<SpeciesPatternLargeShape> productPatternShapeList = new ArrayList<SpeciesPatternLargeShape>();

	private JPanel shapePanel;
	private JScrollPane scrollPane;
	private JPanel productPanel;
	private JPanel reactantPanel;
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private JSplitPane splitPaneTrees = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	private JTree rightClickSourceTree;
	private JEditorPane warningPane;
	
//	private JCheckBox showDetailsCheckBox;

	private JPopupMenu popupMenu;
	private JMenu addMenu;
	private JMenuItem addReactantMenuItem;
	private JMenuItem addProductMenuItem;
	private JMenuItem deleteMenuItem;	
	private JMenuItem editMenuItem;

	private JPopupMenu popupFromShapeMenu;

	private InternalEventHandler eventHandler = new InternalEventHandler();

	private class BioModelNodeEditableTree extends JTree {
		@Override
		public boolean isPathEditable(TreePath path) {
			Object object = path.getLastPathComponent();
			return object instanceof BioModelNode;
		}
	}
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener, TreeSelectionListener,
		TreeWillExpandListener
	{
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == reactionRule) {
				if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REACTANT_WARNING)) {
					Object warning = evt.getNewValue();
				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_PRODUCT_WARNING)) {
					Object warning = evt.getNewValue();
				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REVERSIBLE)) {
					updateInterface();
				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_NAME)) {
					updateInterface();
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

		@Override
		public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
			boolean veto = false;
			if (veto) {
				throw new ExpandVetoException(e);
			}
		}
		@Override
		public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
			JTree tree = (JTree) e.getSource();
			TreePath path = e.getPath();
			boolean veto = false;
			if(path.getParentPath() == null) {
				veto = true;
			}
			if (veto) {
				throw new ExpandVetoException(e);	// veto root colapse
			}
		}
	}
	
	public ReactionRuleEditorPropertiesPanel() {
		super();
		initialize();
	}
	
	private static int yDividerLocation = 100;
	private void initialize() {
		try {

			shapePanel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					for(SpeciesPatternLargeShape stls : reactantPatternShapeList) {
						stls.paintSelf(g);
					}
					for(SpeciesPatternLargeShape stls : productPatternShapeList) {
						stls.paintSelf(g);
					}
				}
			};
			shapePanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if(e.getButton() == 1) {		// left click selects the object (we highlight it)
						Point whereClicked = e.getPoint();
						PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
						Graphics g = shapePanel.getGraphics();
						for (SpeciesPatternLargeShape sps : reactantPatternShapeList) {
							sps.turnHighlightOffRecursive(g);
						}
						for (SpeciesPatternLargeShape sps : productPatternShapeList) {
							sps.turnHighlightOffRecursive(g);
						}
						boolean found = false;
						for (SpeciesPatternLargeShape sps : reactantPatternShapeList) {
							if (sps.contains(locationContext)) {		//check if mouse is inside shape
								found = true;
								break;
							}
						}
						if(!found) {
							for (SpeciesPatternLargeShape sps : productPatternShapeList) {
								if (sps.contains(locationContext)) {
									found = true;
									break;
								}
							}
						}
						locationContext.highlightDeepestShape();
						if(locationContext.getDeepestShape() == null) {
							// nothing selected means all the reactant bar or all the product bar is selected 
							int xExtent = SpeciesPatternLargeShape.xExtent;
							Rectangle2D reactantRectangle = new Rectangle2D.Double(xOffsetInitial-xExtent, yOffsetReactantInitial-3, 3000, 80-2+ReservedSpaceForNameOnYAxis);
							Rectangle2D productRectangle = new Rectangle2D.Double(xOffsetInitial-xExtent, yOffsetProductInitial-3, 3000, 80-2+ReservedSpaceForNameOnYAxis);
							
							if(locationContext.isInside(reactantRectangle)) {
//								locationContext.paintContour(g, reactantRectangle);
								for(SpeciesPatternLargeShape spls : reactantPatternShapeList) {
									spls.paintSelf(g, false);
								}
							} else if(locationContext.isInside(productRectangle)) {
//								locationContext.paintContour(g, productRectangle);
								for(SpeciesPatternLargeShape spls : productPatternShapeList) {
									spls.paintSelf(g, false);
								}
							} else {
								
							}
						} else {
							locationContext.paintDeepestShape(g);
						}
					} else if(e.getButton() == 3) {						// right click invokes popup menu (only if the object is highlighted)
						Point whereClicked = e.getPoint();
						PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
						boolean found = false;
						for (SpeciesPatternLargeShape sps : reactantPatternShapeList) {
							if (sps.contains(locationContext)) {		//check if mouse is inside shape
								found = true;
								break;		// if mouse is inside a shape it can't be simultaneously in another one
							}
						}
						if(!found) {
							for (SpeciesPatternLargeShape sps : productPatternShapeList) {
								if (sps.contains(locationContext)) {
									found = true;
									break;
								}
							}
						}
						if(locationContext.getDeepestShape() != null && !locationContext.getDeepestShape().isHighlighted()) {
							// TODO: (maybe) add code here to highlight the shape if it's not highlighted already but don't show the menu
							
							// return;
						}					
						showPopupMenu(e, locationContext);
					}
				}
			});
			shapePanel.setLayout(new GridBagLayout());
			shapePanel.setBackground(Color.white);	
			
			scrollPane = new JScrollPane(shapePanel);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			splitPaneHorizontal.setOneTouchExpandable(true);
//			splitPaneHorizontal.setDividerLocation(yDividerLocation);
//			splitPaneHorizontal.setResizeWeight(0.1);
			
			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new GridBagLayout());

			splitPaneHorizontal.setTopComponent(scrollPane);
			splitPaneHorizontal.setBottomComponent(lowerPanel);
			
			splitPaneTrees.setResizeWeight(0.5);
			splitPaneTrees.setDividerLocation(340);		// absolute value
			splitPaneTrees.setDividerSize(6);
			splitPaneTrees.setOneTouchExpandable(true);

			reactantTree = new BioModelNodeEditableTree();
			reactantTreeModel = new ReactionRulePropertiesTreeModel(reactantTree, ReactionRuleParticipantType.Reactant);
			reactantTree.setModel(reactantTreeModel);
			reactantTree.setEditable(true);
			RbmReactionParticipantTreeCellRenderer crr = new RbmReactionParticipantTreeCellRenderer(reactantTree);
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
			reactantTree.addTreeWillExpandListener(eventHandler);
			reactantTree.addMouseListener(eventHandler);
			reactantTree.setLargeModel(true);
			reactantTree.setRootVisible(true);

			productTree = new BioModelNodeEditableTree();
			productTreeModel = new ReactionRulePropertiesTreeModel(productTree, ReactionRuleParticipantType.Product);
			productTree.setModel(productTreeModel);
			productTree.setLargeModel(true);
			productTree.setEditable(true);
			RbmReactionParticipantTreeCellRenderer crp = new RbmReactionParticipantTreeCellRenderer(productTree);
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
			productTree.addTreeWillExpandListener(eventHandler);
			productTree.addMouseListener(eventHandler);
			productTree.setLargeModel(true);
			productTree.setRootVisible(true);
			
			Dimension minimumSize = new Dimension(100, 50);		//provide minimum sizes for the two components in the split pane
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
//			JLabel label = new JLabel("Reactants");
//			label.setFont(label.getFont().deriveFont(Font.BOLD));
//			JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
//			p.add(label);
//			p.setBackground(lableColor);
//			p.setBorder(GuiConstants.TAB_PANEL_BORDER);
//			reactantPanel.add(p, gbc);
			
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
//			label = new JLabel("Products");
//			label.setFont(label.getFont().deriveFont(Font.BOLD));
//			p = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
//			p.add(label);
//			p.setBackground(lableColor);
//			p.setBorder(GuiConstants.TAB_PANEL_BORDER);
//			productPanel.add(p, gbc);
			
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
//			gbc.anchor = GridBagConstraints.LINE_START;
//			gbc.insets = new Insets(2,2,0,2);
//			lowerPanel.add(showDetailsCheckBox, gbc);
//			
//			gbc = new GridBagConstraints();
//			gbc.gridx = 1;
//			gbc.gridy = gridy;
//			gbc.weightx = 1.0;
//			gbc.insets = new Insets(2,2,0,2);
//			gbc.fill = GridBagConstraints.HORIZONTAL;
//			titleLabel = new JLabel("Reaction Rule");
//			titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//			titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
//			lowerPanel.add(titleLabel, gbc);
//			
//			gridy ++;
//			gbc = new GridBagConstraints();
//			gbc.gridx = 0;
//			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(2,2,2,2);
			gbc.fill = GridBagConstraints.BOTH;
			lowerPanel.add(new JScrollPane(splitPaneTrees), gbc);
			
			splitPaneHorizontal.setResizeWeight(1.0d);
			splitPaneHorizontal.getBottomComponent().setMinimumSize(new Dimension());
			splitPaneHorizontal.getBottomComponent().setPreferredSize(new Dimension());
			splitPaneHorizontal.setDividerLocation(1.0d);

			
			setLayout(new BorderLayout());
			add(splitPaneHorizontal, BorderLayout.CENTER);
			setBackground(Color.white);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	public void addReactant() {
		ReactantPattern reactant = new ReactantPattern(new SpeciesPattern(), reactionRule.getStructure());
		reactionRule.addReactant(reactant);
		final TreePath path = reactantTreeModel.findObjectPath(null, reactant);
		reactantTree.setSelectionPath(path);
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {				
				reactantTree.scrollPathToVisible(path);
//				productTreeModel.populateTree();
			}
		});
	}
	
	public void addProduct() {
		ProductPattern product = new ProductPattern(new SpeciesPattern(), reactionRule.getStructure());
		reactionRule.addProduct(product);
		final TreePath path = productTreeModel.findObjectPath(null, product);
		productTree.setSelectionPath(path);
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {				
				productTree.scrollPathToVisible(path);
//				reactantTreeModel.populateTree();
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
				reactionRule.removeReactant((ReactantPattern)(reactionRuleParticipant.speciesPattern));
				break;
			case Product:
				reactionRule.removeProduct((ProductPattern)(reactionRuleParticipant.speciesPattern));
				break;
			}
		} else if (selectedUserObject instanceof MolecularTypePattern){
			MolecularTypePattern mtp = (MolecularTypePattern) selectedUserObject;
			Object parentObject = parentNode.getUserObject();
			if (parentObject instanceof ReactionRuleParticipantLocal) {
				ReactionRuleParticipantLocal rrp = (ReactionRuleParticipantLocal) parentObject;
				rrp.speciesPattern.getSpeciesPattern().removeMolecularTypePattern(mtp);

				if(rrp.type == ReactionRuleParticipantType.Reactant) {	// we reset the "opposite" tree because it too might have changed
					productTreeModel.populateTree();
				} else if(rrp.type == ReactionRuleParticipantType.Product) {
					reactantTreeModel.populateTree();
				}
			}
// delete site doesn't make sense for a reaction rule
//		} else if (selectedUserObject instanceof MolecularComponentPattern) {
//			MolecularComponentPattern mcp = (MolecularComponentPattern)selectedUserObject;
//			Object parentObject = parentNode.getUserObject();
//			if (parentObject instanceof MolecularTypePattern) {
//				MolecularTypePattern mtp = (MolecularTypePattern)parentObject;
//				mtp.removeMolecularComponentPattern(mcp);
//			}
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
		reactantTreeModel.setBioModel(newValue);
		productTreeModel.setBioModel(newValue);
		if (bioModel == newValue) {
			return;
		}
		bioModel = newValue;
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

	public void setReactionRule(ReactionRule newValue) {
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
		updateInterface();
	}

	protected void updateInterface() {
		if (reactionRule == null){	// sanity check
			return;
		}
		updateShape();
	}

	public static final int xOffsetInitial = 25;
	public static final int yOffsetReactantInitial = 8;
	public static final int yOffsetProductInitial = 100;
	public static final int ReservedSpaceForNameOnYAxis = 10;
	private void updateShape() {
		List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
		reactantPatternShapeList.clear();
		int maxXOffset;
		int xOffset = xOffsetInitial;
		if(rpList != null && rpList.size() > 0) {
			Graphics gc = splitPaneHorizontal.getTopComponent().getGraphics();
			for(int i = 0; i<rpList.size(); i++) {
				SpeciesPattern sp = rpList.get(i).getSpeciesPattern();
				// TODO: count the number of bonds for this sp and allow enough vertical space for them
				SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffset, yOffsetReactantInitial, -1, sp, gc, reactionRule);
//				if(i==0) { sps.setHighlight(true); }
				if(i < rpList.size()-1) {
					sps.addEndText("+");
				} else {
					if(reactionRule.isReversible()) {
						sps.addEndText("<->");
					} else {
						sps.addEndText("->");
					}
				}
				xOffset = sps.getRightEnd() + 42;	// distance between species patterns
				reactantPatternShapeList.add(sps);
			}
		}
		maxXOffset = Math.max(xOffsetInitial, xOffset);
		
		xOffset = xOffsetInitial;
		List<ProductPattern> ppList = reactionRule.getProductPatterns();
		productPatternShapeList.clear();
		if(ppList != null && ppList.size() > 0) {
			Graphics gc = splitPaneHorizontal.getTopComponent().getGraphics();
			for(int i = 0; i<ppList.size(); i++) {
				SpeciesPattern sp = ppList.get(i).getSpeciesPattern();
				SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffset, yOffsetProductInitial, -1, sp, gc, reactionRule);
//				if(i==0) { sps.setHighlight(true); }
				if(i < ppList.size()-1) {
					sps.addEndText("+");
				}
				xOffset = sps.getRightEnd() + 42;
				productPatternShapeList.add(sps);
			}
		}
		maxXOffset = Math.max(maxXOffset, xOffset);

		// TODO: instead of offset +200 compute the exact width of the image
		Dimension preferredSize = new Dimension(maxXOffset+200, yOffsetProductInitial+80+80);
		shapePanel.setPreferredSize(preferredSize);

		splitPaneHorizontal.getTopComponent().repaint();
	}
	
	private void showPopupMenu(MouseEvent e, PointLocationInShapeContext locationContext) {
		if (popupFromShapeMenu == null) {
			popupFromShapeMenu = new JPopupMenu();			
		}		
		if (popupFromShapeMenu.isShowing()) {
			return;
		}
		boolean bDelete = false;
		boolean bAdd = false;
		boolean bEdit = false;
		boolean bRename = false;
		popupFromShapeMenu.removeAll();
		Point mousePoint = e.getPoint();

		final Object deepestShape = locationContext.getDeepestShape();
		final Object selectedObject;
		
		if(deepestShape == null) {
			selectedObject = null;
			System.out.println("outside");		// when cursor is outside any species pattern we offer to add a new one
//			popupFromShapeMenu.add(getAddSpeciesPatternFromShapeMenuItem());
		} else if(deepestShape instanceof ComponentStateLargeShape) {
			System.out.println("inside state");
			if(((ComponentStateLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((ComponentStateLargeShape)deepestShape).getComponentStateDefinition();
			} else {
				return;
			}
		} else if(deepestShape instanceof MolecularComponentLargeShape) {
			System.out.println("inside component");
			if(((MolecularComponentLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((MolecularComponentLargeShape)deepestShape).getMolecularComponentPattern();
			} else {
				return;
			}
		} else if(deepestShape instanceof MolecularTypeLargeShape) {
			System.out.println("inside molecule");
			if(((MolecularTypeLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((MolecularTypeLargeShape)deepestShape).getMolecularTypePattern();
			} else {
				return;
			}
		} else if(deepestShape instanceof SpeciesPatternLargeShape) {
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
		
		boolean bReactantsZone = false;
		int xExtent = SpeciesPatternLargeShape.xExtent;
		Rectangle2D reactantRectangle = new Rectangle2D.Double(xOffsetInitial-xExtent, yOffsetReactantInitial-3, 3000, 80-2+ReservedSpaceForNameOnYAxis);
		Rectangle2D productRectangle = new Rectangle2D.Double(xOffsetInitial-xExtent, yOffsetProductInitial-3, 3000, 80-2+ReservedSpaceForNameOnYAxis);
		if(locationContext.isInside(reactantRectangle)) {
			bReactantsZone = true;		// clicked inside the reactant rectangle (above yOffsetProductInitial)
		} else if(locationContext.isInside(productRectangle)) {
			bReactantsZone = false;		// clicked inside the product rectangle (below yOffsetProductInitial)
		} else {
			return;
		}
		
		// -------------------------------- reactant zone --------------------------------------------------------
		if(bReactantsZone) {
			if(selectedObject == null) {									// add reactant / product pattern
				JMenuItem addMenuItem = new JMenuItem("Add Reactant");
				addMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						reactionRule.addReactant(new ReactantPattern(new SpeciesPattern(), reactionRule.getStructure())); 
					}
				});
				popupFromShapeMenu.add(addMenuItem);
			} else if(selectedObject instanceof SpeciesPattern) {			// delete (pattern) / specify molecule
				final SpeciesPattern sp = (SpeciesPattern)selectedObject;
				JMenuItem deleteMenuItem = new JMenuItem("Delete");
				deleteMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for(ReactantPattern rp : reactionRule.getReactantPatterns()) {
							if(rp.getSpeciesPattern() == sp) {
								reactionRule.removeReactant(rp);
								Structure st = rp.getStructure();
								if(reactionRule.getReactantPatterns().isEmpty()) {
									reactionRule.addReactant(new ReactantPattern(new SpeciesPattern(), st)); 
								}
							}
						}
					}
				});
				popupFromShapeMenu.add(deleteMenuItem);
				JMenu addMenuItem = new JMenu(VCellErrorMessages.SpecifyMolecularTypes);
				popupFromShapeMenu.add(addMenuItem);
				addMenuItem.removeAll();
				for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
					JMenuItem menuItem = new JMenuItem(mt.getName());
					Graphics gc = shapePanel.getGraphics();
					Icon icon = new MolecularTypeSmallShape(1, 4, mt, gc, mt);
					menuItem.setIcon(icon);
					addMenuItem.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
							for(MolecularComponentPattern mcp : molecularTypePattern.getComponentPatternList()) {
								mcp.setBondType(BondType.None);
							}
							sp.addMolecularTypePattern(molecularTypePattern);
						}
					});
				}
			} else if(selectedObject instanceof MolecularTypePattern) {		// delete molecule / reassign match
				MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
				String deleteMenuText = "Delete <b>" + mtp.getMolecularType().getName() + "</b>";
				deleteMenuText = "<html>" + deleteMenuText + "</html>";
				JMenuItem deleteMenuItem = new JMenuItem(deleteMenuText);
				deleteMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
						SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
						sp.removeMolecularTypePattern(mtp);
					}
				});
				popupFromShapeMenu.add(deleteMenuItem);
				if(mtp.hasExplicitParticipantMatch()) {
					String newKey = mtp.getParticipantMatchLabel();
					List<String> keyCandidates = new ArrayList<String>();
					List<MolecularTypePattern> mtpReactantList = reactionRule.populateMaps(mtp.getMolecularType(), ReactionRuleParticipantType.Reactant);
					List<MolecularTypePattern> mtpProductList = reactionRule.populateMaps(mtp.getMolecularType(), ReactionRuleParticipantType.Product);
					for(MolecularTypePattern mtpCandidate : mtpReactantList) {	// we can look for indexes in any list, we should find the same
						if(mtpCandidate.hasExplicitParticipantMatch() && !mtpCandidate.getParticipantMatchLabel().equals(newKey)) {
							keyCandidates.add(mtpCandidate.getParticipantMatchLabel());
						}
					}
					if(!keyCandidates.isEmpty()) {
						JMenu reassignMatchMenuItem = new JMenu();
						reassignMatchMenuItem.setText("Reassign match");
						for(int i=0; i<keyCandidates.size(); i++) {
							JMenuItem menuItem = new JMenuItem(keyCandidates.get(i));
							reassignMatchMenuItem.add(menuItem);
							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									String oldKey = e.getActionCommand();
									MolecularTypePattern productToReassign = reactionRule.findMatch(oldKey, mtpProductList);
									MolecularTypePattern orphanProduct = reactionRule.findMatch(newKey, mtpProductList);
									productToReassign.setParticipantMatchLabel(newKey);
									orphanProduct.setParticipantMatchLabel(oldKey);
									// TODO: replace the populate tree with reactantPatternShapeList.update() and productPatternShapeList.update()
									// when the tree will be gone
									reactantTreeModel.populateTree();
									productTreeModel.populateTree();
									shapePanel.repaint();
								}
							});
						}
						popupFromShapeMenu.add(reassignMatchMenuItem);
					}
				}
			} else if(selectedObject instanceof MolecularComponentPattern) {	// edit bond / edit state
				manageComponentPatternFromShape(selectedObject, locationContext, reactantTreeModel);			
			}
		// ---------------------------------------- product zone ---------------------------------------------
		} else if(!bReactantsZone) {
			if(selectedObject == null) {									// add product pattern
				JMenuItem addMenuItem = new JMenuItem("Add Product");
				addMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						reactionRule.addProduct(new ProductPattern(new SpeciesPattern(), reactionRule.getStructure())); 
					}
				});
				popupFromShapeMenu.add(addMenuItem);
			} else if(selectedObject instanceof SpeciesPattern) {			// delete (pattern) / specify molecule
				final SpeciesPattern sp = (SpeciesPattern)selectedObject;
				JMenuItem deleteMenuItem = new JMenuItem("Delete");
				deleteMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for(ProductPattern pp : reactionRule.getProductPatterns()) {
							if(pp.getSpeciesPattern() == sp) {
								reactionRule.removeProduct(pp);
								Structure st = pp.getStructure();
								if(reactionRule.getProductPatterns().isEmpty()) {
									reactionRule.addProduct(new ProductPattern(new SpeciesPattern(), st)); 
								}
							}
						}
					}
				});
				popupFromShapeMenu.add(deleteMenuItem);
				JMenu addMenuItem = new JMenu(VCellErrorMessages.SpecifyMolecularTypes);
				popupFromShapeMenu.add(addMenuItem);
				addMenuItem.removeAll();
				for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
					JMenuItem menuItem = new JMenuItem(mt.getName());
					Graphics gc = shapePanel.getGraphics();
					Icon icon = new MolecularTypeSmallShape(1, 4, mt, gc, mt);
					menuItem.setIcon(icon);
					addMenuItem.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
							for(MolecularComponentPattern mcp : molecularTypePattern.getComponentPatternList()) {
								mcp.setBondType(BondType.None);
							}
							sp.addMolecularTypePattern(molecularTypePattern);
						}
					});
				}
			} else if(selectedObject instanceof MolecularTypePattern) {		// delete molecule / reassign match
				MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
				String deleteMenuText = "Delete <b>" + mtp.getMolecularType().getName() + "</b>";
				deleteMenuText = "<html>" + deleteMenuText + "</html>";
				JMenuItem deleteMenuItem = new JMenuItem(deleteMenuText);
				deleteMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
						SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
						sp.removeMolecularTypePattern(mtp);
					}
				});
				popupFromShapeMenu.add(deleteMenuItem);
				if(mtp.hasExplicitParticipantMatch()) {
					String newKey = mtp.getParticipantMatchLabel();
					List<String> keyCandidates = new ArrayList<String>();
					List<MolecularTypePattern> mtpReactantList = reactionRule.populateMaps(mtp.getMolecularType(), ReactionRuleParticipantType.Reactant);
					List<MolecularTypePattern> mtpProductList = reactionRule.populateMaps(mtp.getMolecularType(), ReactionRuleParticipantType.Product);
					for(MolecularTypePattern mtpCandidate : mtpReactantList) {	// we can look for indexes in any list, we should find the same
						if(mtpCandidate.hasExplicitParticipantMatch() && !mtpCandidate.getParticipantMatchLabel().equals(newKey)) {
							keyCandidates.add(mtpCandidate.getParticipantMatchLabel());
						}
					}
					if(!keyCandidates.isEmpty()) {
						JMenu reassignMatchMenuItem = new JMenu();
						reassignMatchMenuItem.setText("Reassign match");
						for(int i=0; i<keyCandidates.size(); i++) {
							JMenuItem menuItem = new JMenuItem(keyCandidates.get(i));
							reassignMatchMenuItem.add(menuItem);
							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									String oldKey = e.getActionCommand();
									MolecularTypePattern reactantToReassign = reactionRule.findMatch(oldKey, mtpReactantList);
									MolecularTypePattern orphanReactant = reactionRule.findMatch(newKey, mtpReactantList);
									reactantToReassign.setParticipantMatchLabel(newKey);
									orphanReactant.setParticipantMatchLabel(oldKey);
									// TODO: replace the populate tree with reactantPatternShapeList.update() and productPatternShapeList.update()
									// when the tree will be gone
									reactantTreeModel.populateTree();
									productTreeModel.populateTree();
									shapePanel.repaint();
								}
							});
						}
						popupFromShapeMenu.add(reassignMatchMenuItem);
					}
				}
			} else if(selectedObject instanceof MolecularComponentPattern) {	// edit bond / edit state
				manageComponentPatternFromShape(selectedObject, locationContext, productTreeModel);			
			}
		}
		popupFromShapeMenu.show(e.getComponent(), mousePoint.x, mousePoint.y);
	}
	public void manageComponentPatternFromShape(final Object selectedObject, PointLocationInShapeContext locationContext, final ReactionRulePropertiesTreeModel treeModel) {
		popupFromShapeMenu.removeAll();
		final MolecularComponentPattern mcp = (MolecularComponentPattern)selectedObject;
		final MolecularComponent mc = mcp.getMolecularComponent();
		
		// ------------------------------------------------------------------- State
		if(mc.getComponentStateDefinitions().size() != 0) {
			JMenu editStateMenu = new JMenu();
			editStateMenu.setText("Edit State");
			editStateMenu.removeAll();
			List<String> itemList = new ArrayList<String>();
			itemList.add(ComponentStatePattern.strAny);
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
						if(name.equals(ComponentStatePattern.strAny)) {
							ComponentStatePattern csp = new ComponentStatePattern();
							mcp.setComponentStatePattern(csp);
						} else {
							String csdName = e.getActionCommand();
							ComponentStateDefinition csd = mcp.getMolecularComponent().getComponentStateDefinition(csdName);
							if(csd == null) {
								throw new RuntimeException("Missing ComponentStateDefinition " + csdName + " for Component " + mcp.getMolecularComponent().getName());
							}
							ComponentStatePattern csp = new ComponentStatePattern(csd);
							mcp.setComponentStatePattern(csp);
						}
						treeModel.populateTree();
//						shapePanel.repaint();
					}
				});
			}
			popupFromShapeMenu.add(editStateMenu);
		}
		
		// ------------------------------------------------------------------------------------------- Bonds
		final MolecularTypePattern mtp = locationContext.getMolecularTypePattern();
		final SpeciesPattern sp = locationContext.getSpeciesPattern();
		
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
//				itemMap.put(b.toHtmlStringLong(sp, mtp, mc, index), b);
				itemMap.put(b.toHtmlStringLong(sp, index), b);
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
					} else if(name.equals(existsString)) {
						if(btBefore == BondType.Specified) {	// specified -> not specified
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Exists);
						mcp.setBond(null);
					} else if(name.equals(possibleString)) {
						if(btBefore == BondType.Specified) {	// specified -> not specified
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Possible);
						mcp.setBond(null);
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
					}
					// TODO: replace the populate tree with reactantPatternShapeList.update() and productPatternShapeList.update()
					// when the tree will be gone
					treeModel.populateTree();
//					shapePanel.repaint();				
				}
			});
		}
		popupFromShapeMenu.add(editBondMenu);
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
		    	//popupMenu.add(getAddReactantMenuItem());
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
					
					if (selectedObject instanceof ReactionRule) {
						popupMenu.add(getAddReactantMenuItem());
					} else if (selectedObject instanceof ReactionRuleParticipantLocal) {
						getAddMenu().setText(VCellErrorMessages.SpecifyMolecularTypes);
						getAddMenu().removeAll();
						for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
							JMenuItem menuItem = new JMenuItem(mt.getName());
							Graphics gc = reactantPanel.getGraphics();
							Icon icon = new MolecularTypeSmallShape(1, 4, mt, gc, mt);
							menuItem.setIcon(icon);
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
											productTreeModel.populateTree();
										}
									});
								}
							});
						}
						bAdd = true;
						bDelete = true;
					} else if (selectedObject instanceof MolecularTypePattern) {
						MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
						if(mtp.hasExplicitParticipantMatch()) {
							String newKey = mtp.getParticipantMatchLabel();
							List<String> keyCandidates = new ArrayList<String>();
							List<MolecularTypePattern> mtpReactantList = reactionRule.populateMaps(mtp.getMolecularType(), ReactionRuleParticipantType.Reactant);
							List<MolecularTypePattern> mtpProductList = reactionRule.populateMaps(mtp.getMolecularType(), ReactionRuleParticipantType.Product);
							for(MolecularTypePattern mtpCandidate : mtpReactantList) {	// we can look for indexes in any list, we should find the same
								if(mtpCandidate.hasExplicitParticipantMatch() && !mtpCandidate.getParticipantMatchLabel().equals(newKey)) {
									keyCandidates.add(mtpCandidate.getParticipantMatchLabel());
								}
							}
							if(!keyCandidates.isEmpty()) {
								JMenu reassignMatchMenuItem = new JMenu();
								reassignMatchMenuItem.setText("Reassign match");
								for(int i=0; i<keyCandidates.size(); i++) {
									JMenuItem menuItem = new JMenuItem(keyCandidates.get(i));
									reassignMatchMenuItem.add(menuItem);
									menuItem.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											String oldKey = e.getActionCommand();
											MolecularTypePattern productToReassign = reactionRule.findMatch(oldKey, mtpProductList);
											MolecularTypePattern orphanProduct = reactionRule.findMatch(newKey, mtpProductList);
											productToReassign.setParticipantMatchLabel(newKey);
											orphanProduct.setParticipantMatchLabel(oldKey);
											reactantTreeModel.populateTree();
											productTreeModel.populateTree();
										}
									});
								}
								popupMenu.add(reassignMatchMenuItem);
							}
						}
						bDelete = true;
					} else if (selectedObject instanceof MolecularComponentPattern) {
						manageComponentPattern(reactantTreeModel, reactantTree, selectedNode, selectedObject);
						bDelete = false;	// we don't delete a site in a reaction rule
					}
				}
		    }
		} else if (e.getSource() == productTree) {
			rightClickSourceTree = productTree;
			GuiUtils.selectClickTreePath(productTree, e);
			TreePath clickPath = productTree.getPathForLocation(mousePoint.x, mousePoint.y);
		    if (clickPath == null) {
		    	//popupMenu.add(getAddProductMenuItem());
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
					if (selectedObject instanceof ReactionRule) {
						popupMenu.add(getAddProductMenuItem());
					} else if (selectedObject instanceof ReactionRuleParticipantLocal) {
						getAddMenu().setText(VCellErrorMessages.SpecifyMolecularTypes);
						getAddMenu().removeAll();
//						List<MolecularTypePattern> missingMoleculesInProducts = reactionRule.getMissingMoleculesInProducts();
//						if (missingMoleculesInProducts.size() > 0) {
//							bAdd = true;
//						}
						for (final MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
							JMenuItem menuItem = new JMenuItem(mt.getName());
							Graphics gc = productPanel.getGraphics();
							Icon icon = new MolecularTypeSmallShape(1, 4, mt, gc, mt);
							menuItem.setIcon(icon);
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
											reactantTreeModel.populateTree();

										}
									});
								}
							});
						}
						bAdd = true;
						bDelete = true;
					} else if (selectedObject instanceof MolecularTypePattern) {
						MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
						if(mtp.hasExplicitParticipantMatch()) {
							String newKey = mtp.getParticipantMatchLabel();
							List<String> keyCandidates = new ArrayList<String>();
							List<MolecularTypePattern> mtpReactantList = reactionRule.populateMaps(mtp.getMolecularType(), ReactionRuleParticipantType.Reactant);
							List<MolecularTypePattern> mtpProductList = reactionRule.populateMaps(mtp.getMolecularType(), ReactionRuleParticipantType.Product);
							for(MolecularTypePattern mtpCandidate : mtpReactantList) {	// we can look for indexes in any list, we should find the same
								if(mtpCandidate.hasExplicitParticipantMatch() && !mtpCandidate.getParticipantMatchLabel().equals(newKey)) {
									keyCandidates.add(mtpCandidate.getParticipantMatchLabel());
								}
							}
							if(!keyCandidates.isEmpty()) {
								JMenu reassignMatchMenuItem = new JMenu();
								reassignMatchMenuItem.setText("Reassign match");
								for(int i=0; i<keyCandidates.size(); i++) {
									JMenuItem menuItem = new JMenuItem(keyCandidates.get(i));
									reassignMatchMenuItem.add(menuItem);
									menuItem.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											String oldKey = e.getActionCommand();
											MolecularTypePattern reactantToReassign = reactionRule.findMatch(oldKey, mtpReactantList);
											MolecularTypePattern orphanReactant = reactionRule.findMatch(newKey, mtpReactantList);
											reactantToReassign.setParticipantMatchLabel(newKey);
											orphanReactant.setParticipantMatchLabel(oldKey);
											reactantTreeModel.populateTree();
											productTreeModel.populateTree();
										}
									});
								}
								popupMenu.add(reassignMatchMenuItem);
							}
						}
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
			itemList.add(ComponentStatePattern.strAny);
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
						if(name.equals(ComponentStatePattern.strAny)) {
							ComponentStatePattern csp = new ComponentStatePattern();
							mcp.setComponentStatePattern(csp);
						} else {
							String csdName = e.getActionCommand();
							ComponentStateDefinition csd = mcp.getMolecularComponent().getComponentStateDefinition(csdName);
							if(csd == null) {
								throw new RuntimeException("Missing ComponentStateDefinition " + csdName + " for Component " + mcp.getMolecularComponent().getName());
							}
							ComponentStatePattern csp = new ComponentStatePattern(csd);
							mcp.setComponentStatePattern(csp);
						}
						treeModel.populateTree();
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
				itemMap.put(b.toHtmlStringLong(sp, index), b);
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
