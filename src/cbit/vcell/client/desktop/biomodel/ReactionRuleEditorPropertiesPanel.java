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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
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
import org.vcell.model.rbm.RbmObject;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.HighlightableShapeInterface;
import cbit.vcell.graph.LargeShapePanel;
import cbit.vcell.graph.MolecularComponentLargeShape;
import cbit.vcell.graph.MolecularTypeLargeShape;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.ReactionRulePatternLargeShape;
import cbit.vcell.graph.RulesShapePanel;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.graph.MolecularComponentLargeShape.ComponentStateLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape.DisplayRequirements;
import cbit.vcell.graph.ZoomShape.Sign;
import cbit.vcell.graph.ZoomShape;
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
	
	private JCheckBox isReversibleCheckBox;
	private JButton addReactantButton = null;
	private JButton addProductButton = null;
	private JButton zoomLargerButton = null;
	private JButton zoomSmallerButton = null;
	
	private JCheckBox viewSingleRowButton = null;
	private JCheckBox showMoleculeColorButton = null;
	private JCheckBox showDifferenceButton = null;
	private JCheckBox showNonTrivialButton = null;

	ReactionRulePatternLargeShape reactantShape;
	ReactionRulePatternLargeShape productShape;
	
	private RulesShapePanel shapePanel;
	private JScrollPane scrollPane;
	private JPanel productPanel;
	private JPanel reactantPanel;
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private JSplitPane splitPaneTrees = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	private JTree rightClickSourceTree;
	
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
		@Override
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
				} else if (evt.getPropertyName().equals("entityChange")) {
					updateInterface();
				}
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getAddReactantMenuItem()) {
				addReactant();
			} else if (e.getSource() == getAddProductMenuItem()) {
				addProduct();
			} else if (e.getSource() == getAddReactantButton()) {
				addReactant();
			} else if (e.getSource() == getAddProductButton()) {
				addProduct();
			} else if (e.getSource() == getDeleteMenuItem()) {
				delete();
			} else if (e.getSource() == getEditMenuItem()) {
				editEntity();
			} else if (e.getSource() == getZoomLargerButton()) {
				boolean ret = shapePanel.zoomLarger();
				getZoomLargerButton().setEnabled(ret);
				getZoomSmallerButton().setEnabled(true);
				updateInterface();
			} else if (e.getSource() == getZoomSmallerButton()) {
				boolean ret = shapePanel.zoomSmaller();
				getZoomLargerButton().setEnabled(true);
				getZoomSmallerButton().setEnabled(ret);
				updateInterface();
			} else if (e.getSource() == isReversibleCheckBox) {
				setReversible(isReversibleCheckBox.isSelected());
				
			} else if (e.getSource() == getViewSingleRowButton()) {
				shapePanel.setViewSingleRow(getViewSingleRowButton().isSelected());
				updateInterface();
			} else if (e.getSource() == getShowMoleculeColorButton()) {
				shapePanel.setShowMoleculeColor(getShowMoleculeColorButton().isSelected());
				shapePanel.repaint();
			} else if (e.getSource() == getShowDifferencesButton()) {
				shapePanel.setShowDifferencesOnly(getShowDifferencesButton().isSelected());
				shapePanel.repaint();
			} else if (e.getSource() == getShowNonTrivialButton()) {
				shapePanel.setShowNonTrivialOnly(getShowNonTrivialButton().isSelected());
				shapePanel.repaint();
			}
		}
		@Override
		public void mouseClicked(MouseEvent e) {			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			if (!e.isConsumed() && (e.getSource() == reactantTree || e.getSource() == productTree)) {
//				showPopupMenu(e);
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!e.isConsumed() && (e.getSource() == reactantTree || e.getSource() == productTree)) {
//				showPopupMenu(e);
			}			
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
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
			shapePanel = new RulesShapePanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					reactantShape.paintSelf(g);
					productShape.paintSelf(g);
				}
			};
			shapePanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if(getViewSingleRowButton().isSelected()) {
						return;
					}
					if(e.getButton() == 1) {		// left click selects the object (we highlight it)
						Point whereClicked = e.getPoint();
						PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
						reactantShape.setPointLocationInShapeContext(locationContext);
						productShape.setPointLocationInShapeContext(locationContext);
						manageMouseActivity(locationContext);
					} else if(e.getButton() == 3) {						// right click invokes popup menu (only if the object is highlighted)
						Point whereClicked = e.getPoint();
						PointLocationInShapeContext locationContext = new PointLocationInShapeContext(whereClicked);
						reactantShape.setPointLocationInShapeContext(locationContext);
						productShape.setPointLocationInShapeContext(locationContext);
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
					reactantShape.turnHighlightOffRecursive(g);
					productShape.turnHighlightOffRecursive(g);

					boolean found = false;
					if(reactantShape.contains(locationContext)) {
						found = true;
					}
					if(!found) {
						if(productShape.contains(locationContext)) {
							;	// we just need to initialize the location context with the proper shapes under the cursor by calling "contains"
						}
					}

					locationContext.highlightDeepestShape();
//					if(locationContext.getDeepestShape() == null) {
//						// nothing selected means all the reactant bar or all the product bar is selected 
						int xExtent = SpeciesPatternLargeShape.calculateXExtent(shapePanel);
						Rectangle2D reactantRectangle = new Rectangle2D.Double(xOffsetInitial-xExtent, yOffsetReactantInitial-3, 3000, 80-2+ReservedSpaceForNameOnYAxis);
						Rectangle2D productRectangle = new Rectangle2D.Double(xOffsetInitial-xExtent, yOffsetProductInitial-3, 3000, 80-2+ReservedSpaceForNameOnYAxis);
						
						if(locationContext.isInside(reactantRectangle)) {
							reactantShape.paintSelf(g, true);
						} else if(locationContext.isInside(productRectangle)) {
							productShape.paintSelf(g, true);
//						} else {
//							
//						}
//					} else {
//						locationContext.paintDeepestShape(g);
					}
				}
			});
			shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseMoved(MouseEvent e) {
					if(getViewSingleRowButton().isSelected()) {
						shapePanel.setToolTipText(null);
						return;
					}
					Point overWhat = e.getPoint();
					PointLocationInShapeContext locationContext = new PointLocationInShapeContext(overWhat);
					reactantShape.contains(locationContext);
					productShape.contains(locationContext);
					HighlightableShapeInterface hsi = locationContext.getDeepestShape();
					if(hsi == null) {
						shapePanel.setToolTipText(null);
					} else {
						shapePanel.setToolTipText("Right click for " + hsi.getDisplayType() + " menus");
					}
				} 
			});
			
			shapePanel.setLayout(new GridBagLayout());
			shapePanel.setBackground(Color.white);	
			shapePanel.zoomSmaller();
			shapePanel.zoomSmaller();
			getZoomSmallerButton().setEnabled(true);
			getZoomLargerButton().setEnabled(true);

			scrollPane = new JScrollPane(shapePanel);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			isReversibleCheckBox = new JCheckBox("");
			isReversibleCheckBox.addActionListener(eventHandler);
			isReversibleCheckBox.setEnabled(true);

			JPanel optionsPanel = new JPanel();
			optionsPanel.setPreferredSize(new Dimension(150, 200));
			optionsPanel.setLayout(new GridBagLayout());
			
			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.insets = new Insets(6,6,2,1);
			gbc.anchor = GridBagConstraints.WEST;
			optionsPanel.add(new JLabel("Reversible"), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.insets = new Insets(6,1,2,5);
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			optionsPanel.add(isReversibleCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = gridy;
			gbc.insets = new Insets(6,5,2,2);
			gbc.anchor = GridBagConstraints.EAST;
			optionsPanel.add(getZoomLargerButton(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = gridy;
			gbc.insets = new Insets(6,2,2,6);
			gbc.anchor = GridBagConstraints.EAST;
			optionsPanel.add(getZoomSmallerButton(), gbc);
			// apparently we don't need a fake 3rd cell to the right

			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(4,4,2,4);			// top, left bottom, right
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			optionsPanel.add(getAddReactantButton(), gbc);
			
			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(2,4,4,4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			optionsPanel.add(getAddProductButton(), gbc);
			
			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 4;
			gbc.weightx = 1;
			gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 10);
			optionsPanel.add(new JLabel(""), gbc);

			// -----------------------------------------------------------
			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(2,4,0,4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			optionsPanel.add(getViewSingleRowButton(), gbc);

			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(0,4,4,4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			optionsPanel.add(getShowMoleculeColorButton(), gbc);

			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(0,4,4,4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			optionsPanel.add(getShowNonTrivialButton(), gbc);

			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 3;
			gbc.insets = new Insets(0,4,4,4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			optionsPanel.add(getShowDifferencesButton(), gbc);
			
			getViewSingleRowButton().setSelected(false);		// set initial buttons' state
			getShowMoleculeColorButton().setSelected(true);
			getShowNonTrivialButton().setSelected(true);
			getShowDifferencesButton().setSelected(false);
			shapePanel.setViewSingleRow(getViewSingleRowButton().isSelected());
			shapePanel.setShowMoleculeColor(getShowMoleculeColorButton().isSelected());
			shapePanel.setShowNonTrivialOnly(getShowNonTrivialButton().isSelected());
			shapePanel.setShowDifferencesOnly(getShowDifferencesButton().isSelected());
			// ----------------------------------------------------------

			JPanel containerOfScrollPanel = new JPanel();
			containerOfScrollPanel.setLayout(new BorderLayout());
			containerOfScrollPanel.add(optionsPanel, BorderLayout.WEST);
			containerOfScrollPanel.add(scrollPane, BorderLayout.CENTER);
			
			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new GridBagLayout());

			splitPaneHorizontal.setTopComponent(containerOfScrollPanel);
			splitPaneHorizontal.setBottomComponent(lowerPanel);
			
			splitPaneTrees.setResizeWeight(0.5);
			splitPaneTrees.setDividerLocation(340);		// absolute value
			splitPaneTrees.setDividerSize(6);
			splitPaneTrees.setOneTouchExpandable(true);
			splitPaneHorizontal.setOneTouchExpandable(true);
//			splitPaneHorizontal.setDividerLocation(yDividerLocation);

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
			
			gridy = 0;
			gbc = new GridBagConstraints();
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
	
	private JButton getAddReactantButton() {
		if (addReactantButton == null) {
			addReactantButton = new JButton("Add Reactant");
			addReactantButton.addActionListener(eventHandler);
		}
		return addReactantButton;
	}
	private JButton getAddProductButton() {
		if (addProductButton == null) {
			addProductButton = new JButton("Add Product");
			addProductButton.addActionListener(eventHandler);
		}
		return addProductButton;
	}
	// ----------------------------------------------------------------------------
	private JCheckBox getViewSingleRowButton() {
		if(viewSingleRowButton == null) {
			viewSingleRowButton = new JCheckBox("Single Row Viewer");
			viewSingleRowButton.addActionListener(eventHandler);
		}
		return viewSingleRowButton;
	}
	private JCheckBox getShowMoleculeColorButton() {
		if(showMoleculeColorButton == null) {
			showMoleculeColorButton = new JCheckBox("Show Molecule Color");
			showMoleculeColorButton.addActionListener(eventHandler);
		}
		return showMoleculeColorButton;
	}
	private JCheckBox getShowDifferencesButton() {
		if(showDifferenceButton == null) {
			showDifferenceButton = new JCheckBox("Show Differences");
			showDifferenceButton.addActionListener(eventHandler);
		}
		return showDifferenceButton;
	}
	private JCheckBox getShowNonTrivialButton() {
		if(showNonTrivialButton == null) {
			showNonTrivialButton = new JCheckBox("Show Non-trivial");
			showNonTrivialButton.addActionListener(eventHandler);
		}
		return showNonTrivialButton;
	}
	// ------------------------------------------------------------------------------
	private JButton getZoomLargerButton() {
		if (zoomLargerButton == null) {
			zoomLargerButton = new JButton();
			ZoomShape.setZoomMod(zoomLargerButton, ZoomShape.Sign.plus);
			zoomLargerButton.addActionListener(eventHandler);
		}
		return zoomLargerButton;
	}
	private JButton getZoomSmallerButton() {
		if (zoomSmallerButton == null) {
			zoomSmallerButton = new JButton();
			ZoomShape.setZoomMod(zoomSmallerButton, ZoomShape.Sign.minus);
			zoomSmallerButton.addActionListener(eventHandler);
		}
		return zoomSmallerButton;
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
		if(bioModel != null && shapePanel != null) {
			bioModel.setRulesShapeManager(shapePanel);
		}
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
		shapePanel.setReactionRule(reactionRule);
		productTreeModel.setReactionRule(reactionRule);
		reactantTreeModel.setReactionRule(reactionRule);
		updateInterface();
	}
	private void setReversible(boolean bReversible) {
		reactionRule.setReversible(bReversible);
	}

	protected void updateInterface() {
		if (reactionRule == null){	// sanity check
			isReversibleCheckBox.setSelected(false);
			return;
		}
		isReversibleCheckBox.setSelected(reactionRule.isReversible());
		updateShape();
	}

	public static final int xOffsetInitial = 25;
	public static final int yOffsetReactantInitial = 8;
	public static final int yOffsetProductInitial = 100;
	public static final int ReservedSpaceForNameOnYAxis = 10;
	
	private void updateShape() {
		int maxXOffset;
		
		if(getViewSingleRowButton().isSelected()) {
			reactantShape = new ReactionRulePatternLargeShape(xOffsetInitial, yOffsetReactantInitial, -1, shapePanel, reactionRule, true);
			int xOffset = reactantShape.getRightEnd() + 70;
			
			productShape = new ReactionRulePatternLargeShape(xOffset, yOffsetReactantInitial, -1, shapePanel, reactionRule, false);
			xOffset += productShape.getRightEnd();

			// TODO: instead of offset+100 compute the exact width of the image
			Dimension preferredSize = new Dimension(xOffset+90, yOffsetReactantInitial+80+20);
			shapePanel.setPreferredSize(preferredSize);
			
		} else {
			reactantShape = new ReactionRulePatternLargeShape(xOffsetInitial, yOffsetReactantInitial, -1, shapePanel, reactionRule, true);
			maxXOffset = Math.max(xOffsetInitial, reactantShape.getXOffset());
			
			productShape = new ReactionRulePatternLargeShape(xOffsetInitial, yOffsetProductInitial, -1, shapePanel, reactionRule, false);
			maxXOffset = Math.max(maxXOffset, productShape.getXOffset());
	
			Dimension preferredSize = new Dimension(maxXOffset+90, yOffsetProductInitial+80+20);
			shapePanel.setPreferredSize(preferredSize);
		}
		splitPaneHorizontal.getTopComponent().repaint();
	}
	
	private void showPopupMenu(MouseEvent e, PointLocationInShapeContext locationContext) {
		if (popupFromShapeMenu == null) {
			popupFromShapeMenu = new JPopupMenu();			
		}		
		if (popupFromShapeMenu.isShowing()) {
			return;
		}
		popupFromShapeMenu.removeAll();
		Point mousePoint = e.getPoint();

		final Object deepestShape = locationContext.getDeepestShape();
		final RbmObject selectedObject;
		
		if(deepestShape == null) {
			selectedObject = null;
			System.out.println("outside");		// when cursor is outside any species pattern we offer to add a new one
//			popupFromShapeMenu.add(getAddSpeciesPatternFromShapeMenuItem());
		} else if(deepestShape instanceof ComponentStateLargeShape) {
			System.out.println("inside state");
			if(((ComponentStateLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((ComponentStateLargeShape)deepestShape).getComponentStatePattern();
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
		} else if(deepestShape instanceof ReactionRulePatternLargeShape) {
			System.out.println("inside reactant line or products line");
			if(((ReactionRulePatternLargeShape)deepestShape).isHighlighted()) {
				selectedObject = ((ReactionRulePatternLargeShape)deepestShape).getReactionRule();
			} else {
				return;
			}
		} else {
			selectedObject = null;
			System.out.println("inside something else?");
			return;
		}
		
		boolean bReactantsZone = false;
		int xExtent = SpeciesPatternLargeShape.calculateXExtent(shapePanel);
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
			if(selectedObject == null) {
				return;
			} else if(selectedObject instanceof ReactionRule) {				// add reactant pattern
				JMenuItem addMenuItem = new JMenuItem("Add Reactant");
				addMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						reactionRule.addReactant(new ReactantPattern(new SpeciesPattern(), reactionRule.getStructure()));
						shapePanel.repaint();
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
									shapePanel.repaint();
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
					Icon icon = new MolecularTypeSmallShape(1, 4, mt, null, gc, mt, null);
					menuItem.setIcon(icon);
					addMenuItem.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
							for(MolecularComponentPattern mcp : molecularTypePattern.getComponentPatternList()) {
								mcp.setBondType(BondType.Possible);
							}
							sp.addMolecularTypePattern(molecularTypePattern);
							shapePanel.repaint();
						}
					});
				}
				JMenu compartmentMenuItem = new JMenu("Specify structure");
				popupFromShapeMenu.add(compartmentMenuItem);
				if(sp.getMolecularTypePatterns().isEmpty()) {
					compartmentMenuItem.setEnabled(false);
				}
				compartmentMenuItem.removeAll();
				for (final Structure struct : bioModel.getModel().getStructures()) {
					JMenuItem menuItem = new JMenuItem(struct.getName());
					compartmentMenuItem.add(menuItem);
					for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
						MolecularType mt = mtp.getMolecularType();
						if(mt.isAnchorAll()) {
							continue;		// no restrictions (no anchor exclusion) for this molecular type
						}
						if(!mt.getAnchors().contains(struct)) {
							menuItem.setEnabled(false);		// sp can't be in this struct if any of its molecules is excluded (not anchored)
							break;
						}
					}
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String nameStruct = e.getActionCommand();
							Structure struct = bioModel.getModel().getStructure(nameStruct);
							ReactantPattern rp = reactionRule.getReactantPattern(sp);
							rp.setStructure(struct);
							productTreeModel.populateTree();
							shapePanel.repaint();
						}
					});
				}
			} else if(selectedObject instanceof MolecularTypePattern) {		// move left / right / delete molecule / reassign match
				MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
				int numMtp = locationContext.sps.getSpeciesPattern().getMolecularTypePatterns().size();
				
				String moveRightMenuText = "Move <b>" + "right" + "</b>";
				moveRightMenuText = "<html>" + moveRightMenuText + "</html>";
				JMenuItem moveRightMenuItem = new JMenuItem(moveRightMenuText);
				Icon icon = VCellIcons.moveRightIcon;
				moveRightMenuItem.setIcon(icon);
				moveRightMenuItem.setEnabled(numMtp < 2 ? false : true);
				moveRightMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern from = (MolecularTypePattern)selectedObject;
						SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
						sp.shiftRight(from);
						reactantTreeModel.populateTree();
						productTreeModel.populateTree();
						shapePanel.repaint();
					}
				});
				popupFromShapeMenu.add(moveRightMenuItem);
				
				String moveLeftMenuText = "Move <b>" + "left" + "</b>";
				moveLeftMenuText = "<html>" + moveLeftMenuText + "</html>";
				JMenuItem moveLeftMenuItem = new JMenuItem(moveLeftMenuText);
				icon = VCellIcons.moveLeftIcon;
				moveLeftMenuItem.setIcon(icon);
				moveLeftMenuItem.setEnabled(numMtp < 2 ? false : true);
				moveLeftMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern from = (MolecularTypePattern)selectedObject;
						SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
						sp.shiftLeft(from);
						reactantTreeModel.populateTree();
						productTreeModel.populateTree();
						shapePanel.repaint();
					}
				});
				popupFromShapeMenu.add(moveLeftMenuItem);
				popupFromShapeMenu.add(new JSeparator());
				
				String deleteMenuText = "Delete <b>" + mtp.getMolecularType().getName() + "</b>";
				deleteMenuText = "<html>" + deleteMenuText + "</html>";
				JMenuItem deleteMenuItem = new JMenuItem(deleteMenuText);
				deleteMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
						SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
						sp.removeMolecularTypePattern(mtp);
						shapePanel.repaint();
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
						reassignMatchMenuItem.setText("Reassign match to");
						for(int i=0; i<keyCandidates.size(); i++) {
							JMenuItem menuItem = new JMenuItem(keyCandidates.get(i));
							reassignMatchMenuItem.add(menuItem);
							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									String oldKey = e.getActionCommand();
									MolecularTypePattern orphanReactant = reactionRule.findMatch(oldKey, mtpReactantList);
									mtp.setParticipantMatchLabel(oldKey);
									orphanReactant.setParticipantMatchLabel(newKey);
									// TODO: replace the populate tree with reactantPatternShapeList.update() and productPatternShapeList.update()
									// when the tree will be gone
									reactantTreeModel.populateTree();
									productTreeModel.populateTree();
									shapePanel.repaint();
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {				
											reactantShape.flash(oldKey);
											productShape.flash(oldKey);
										}
									});
								}
							});
						}
						popupFromShapeMenu.add(reassignMatchMenuItem);
					}
				}
			} else if(selectedObject instanceof MolecularComponentPattern) {	// edit bond / edit state
				manageComponentPatternFromShape(selectedObject, locationContext, reactantTreeModel, ShowWhat.ShowBond, bReactantsZone);
				
			} else if(selectedObject instanceof ComponentStatePattern) {		// edit state
				MolecularComponentPattern mcp = ((ComponentStateLargeShape)deepestShape).getMolecularComponentPattern();
				manageComponentPatternFromShape(mcp, locationContext, reactantTreeModel, ShowWhat.ShowState, bReactantsZone);			
			}
		// ---------------------------------------- product zone ---------------------------------------------
		} else if(!bReactantsZone) {
			if(selectedObject == null) {
				return;
			} else if(selectedObject instanceof ReactionRule) {				// add product pattern
				JMenuItem addMenuItem = new JMenuItem("Add Product");
				addMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						reactionRule.addProduct(new ProductPattern(new SpeciesPattern(), reactionRule.getStructure()));
						shapePanel.repaint();
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
									shapePanel.repaint();
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
					Icon icon = new MolecularTypeSmallShape(1, 4, mt, null, gc, mt, null);
					menuItem.setIcon(icon);
					addMenuItem.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							MolecularTypePattern molecularTypePattern = new MolecularTypePattern(mt);
							for(MolecularComponentPattern mcp : molecularTypePattern.getComponentPatternList()) {
								mcp.setBondType(BondType.Possible);
							}
							sp.addMolecularTypePattern(molecularTypePattern);
							shapePanel.repaint();
						}
					});
				}
				JMenu compartmentMenuItem = new JMenu("Specify structure");		// specify structure
				popupFromShapeMenu.add(compartmentMenuItem);
				compartmentMenuItem.removeAll();
				if(sp.getMolecularTypePatterns().isEmpty()) {
					compartmentMenuItem.setEnabled(false);
				}
				for (final Structure struct : bioModel.getModel().getStructures()) {
					JMenuItem menuItem = new JMenuItem(struct.getName());
					compartmentMenuItem.add(menuItem);
					for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
						MolecularType mt = mtp.getMolecularType();
						if(mt.isAnchorAll()) {
							continue;	// no restrictions for this molecular type
						}
						if(!mt.getAnchors().contains(struct)) {
							menuItem.setEnabled(false);		// sp can't be in this struct if any of its molecules is excluded (not anchored)
							break;
						}
					}
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String nameStruct = e.getActionCommand();
							Structure struct = bioModel.getModel().getStructure(nameStruct);
							ProductPattern pp = reactionRule.getProductPattern(sp);
							pp.setStructure(struct);
							productTreeModel.populateTree();
							shapePanel.repaint();
						}
					});
				}
			} else if(selectedObject instanceof MolecularTypePattern) {		// move left / right / delete molecule / reassign match
				MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
				int numMtp = locationContext.sps.getSpeciesPattern().getMolecularTypePatterns().size();
				
				String moveRightMenuText = "Move <b>" + "right" + "</b>";
				moveRightMenuText = "<html>" + moveRightMenuText + "</html>";
				JMenuItem moveRightMenuItem = new JMenuItem(moveRightMenuText);
				Icon icon = VCellIcons.moveRightIcon;
				moveRightMenuItem.setIcon(icon);
				moveRightMenuItem.setEnabled(numMtp < 2 ? false : true);
				moveRightMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern from = (MolecularTypePattern)selectedObject;
						SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
						sp.shiftRight(from);
						reactantTreeModel.populateTree();
						productTreeModel.populateTree();
						shapePanel.repaint();
					}
				});
				popupFromShapeMenu.add(moveRightMenuItem);
				
				String moveLeftMenuText = "Move <b>" + "left" + "</b>";
				moveLeftMenuText = "<html>" + moveLeftMenuText + "</html>";
				JMenuItem moveLeftMenuItem = new JMenuItem(moveLeftMenuText);
				icon = VCellIcons.moveLeftIcon;
				moveLeftMenuItem.setIcon(icon);
				moveLeftMenuItem.setEnabled(numMtp < 2 ? false : true);
				moveLeftMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern from = (MolecularTypePattern)selectedObject;
						SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
						sp.shiftLeft(from);
						reactantTreeModel.populateTree();
						productTreeModel.populateTree();
						shapePanel.repaint();
					}
				});
				popupFromShapeMenu.add(moveLeftMenuItem);
				popupFromShapeMenu.add(new JSeparator());
				
				String deleteMenuText = "Delete <b>" + mtp.getMolecularType().getName() + "</b>";
				deleteMenuText = "<html>" + deleteMenuText + "</html>";
				JMenuItem deleteMenuItem = new JMenuItem(deleteMenuText);
				deleteMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MolecularTypePattern mtp = (MolecularTypePattern)selectedObject;
						SpeciesPattern sp = locationContext.sps.getSpeciesPattern();
						sp.removeMolecularTypePattern(mtp);
						shapePanel.repaint();
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
						reassignMatchMenuItem.setText("Reassign match to");
						for(int i=0; i<keyCandidates.size(); i++) {
							JMenuItem menuItem = new JMenuItem(keyCandidates.get(i));
							reassignMatchMenuItem.add(menuItem);
							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									String oldKey = e.getActionCommand();
									MolecularTypePattern orphanProduct = reactionRule.findMatch(oldKey, mtpProductList);
									mtp.setParticipantMatchLabel(oldKey);
									orphanProduct.setParticipantMatchLabel(newKey);
									// TODO: replace the populate tree with reactantPatternShapeList.update() and productPatternShapeList.update()
									// when the tree will be gone
									reactantTreeModel.populateTree();
									productTreeModel.populateTree();
									shapePanel.repaint();
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {				
											reactantShape.flash(oldKey);
											productShape.flash(oldKey);
										}
									});
								}
							});
						}
						popupFromShapeMenu.add(reassignMatchMenuItem);
					}
				}
			} else if(selectedObject instanceof MolecularComponentPattern) {	// edit bond / edit state
				manageComponentPatternFromShape(selectedObject, locationContext, productTreeModel, ShowWhat.ShowBond, bReactantsZone);
				
			} else if(selectedObject instanceof ComponentStatePattern) {		// edit state
				MolecularComponentPattern mcp = ((ComponentStateLargeShape)deepestShape).getMolecularComponentPattern();
				manageComponentPatternFromShape(mcp, locationContext, productTreeModel, ShowWhat.ShowState, bReactantsZone);			
			}
		}
		popupFromShapeMenu.show(e.getComponent(), mousePoint.x, mousePoint.y);
	}
	
	private void reflectStateToProduct(MolecularComponentPattern mcpReactant, ComponentStatePattern cspReactant) {
		MolecularTypePattern mtpReactant = reactionRule.getReactantMoleculeOfComponent(mcpReactant);
		MolecularTypePattern mtpProduct = reactionRule.getMatchingProductMolecule(mtpReactant);
		if(mtpProduct == null) {
			return;
		}
		for(MolecularComponentPattern mcpProduct : mtpProduct.getComponentPatternList()) {
			if(mcpProduct.getMolecularComponent() != mcpReactant.getMolecularComponent()) {
				continue;
			}
			ComponentStatePattern csp = new ComponentStatePattern();	// use this if isAny
			if(!cspReactant.isAny()) {
				ComponentStateDefinition csd = cspReactant.getComponentStateDefinition();
				csp = new ComponentStatePattern(csd);
			}
			mcpProduct.setComponentStatePattern(csp);
		}
	}
	private void reflectBondToProduct(MolecularComponentPattern mcpReactant) {
		if(mcpReactant.getBondType() == BondType.Specified) {
			return;		// we don't transfer to product explicit bonds
		}
		MolecularTypePattern mtpReactant = reactionRule.getReactantMoleculeOfComponent(mcpReactant);
		MolecularTypePattern mtpProduct = reactionRule.getMatchingProductMolecule(mtpReactant);
		if(mtpProduct == null) {
			return;
		}
		for(MolecularComponentPattern mcpProduct : mtpProduct.getComponentPatternList()) {
			if(mcpProduct.getMolecularComponent() != mcpReactant.getMolecularComponent()) {
				continue;
			}
			// finally we have the mcpProduct we need to modify
			if(mcpProduct.getBondType() == BondType.Specified) {
				// we don't reset an explicit bond, we assume that the user knew what he was doing
//				mcpProduct.getBond().molecularComponentPattern.setBondType(BondType.Possible);
//				mcpProduct.getBond().molecularComponentPattern.setBond(null);
//				mcpProduct.setBondType(mcpReactant.getBondType());
//				mcpProduct.setBond(null);
			} else {
				mcpProduct.setBondType(mcpReactant.getBondType());
				mcpProduct.setBond(null);	// bond type can be none, exist or possible
			}
		}
	}

	private enum ShowWhat {
		ShowState,
		ShowBond
	}
	public void manageComponentPatternFromShape(final Object selectedObject, PointLocationInShapeContext locationContext, 
			final ReactionRulePropertiesTreeModel treeModel, ShowWhat showWhat, boolean bIsReactant) {
		popupFromShapeMenu.removeAll();
		final MolecularComponentPattern mcp = (MolecularComponentPattern)selectedObject;
		final MolecularComponent mc = mcp.getMolecularComponent();
		boolean anyStateProhibited = false;
		boolean explicitStateProhibited = false;
		
		boolean existBondProhibited = false;
		boolean noneBondProhibited = false;
		boolean possibleBondProhibited = false;
		boolean specifiedBondProhibited = false;
		
		if(!bIsReactant) {		// product has restrictions for states and bonds, depending on reactant
			BondType reactantComponentBondType = reactionRule.getReactantComponentBondType(mcp);
			if(reactantComponentBondType != null && reactantComponentBondType == BondType.Exists) {				// has external		+
//				existBondProhibited = true;
				noneBondProhibited = true;
				possibleBondProhibited = true;
				specifiedBondProhibited = true;
			} else if(reactantComponentBondType != null && reactantComponentBondType == BondType.None) {		// is unbound		-
				existBondProhibited = true;
//				noneBondProhibited = true;
				possibleBondProhibited = true;
//				specifiedBondProhibited = true;
			} else if(reactantComponentBondType != null && reactantComponentBondType == BondType.Possible) {	// may be bound		?
				existBondProhibited = true;
				noneBondProhibited = true;
//				possibleBondProhibited = true;
				specifiedBondProhibited = true;
			} else if(reactantComponentBondType != null && reactantComponentBondType == BondType.Specified) {
				existBondProhibited = true;
//				noneBondProhibited = true;
				possibleBondProhibited = true;
//				specifiedBondProhibited = true;
			}	// if it's null nothing is prohibited
			ComponentStatePattern reactantComponentStatePattern = reactionRule.getReactantComponentState(mcp);
			if(reactantComponentStatePattern != null && reactantComponentStatePattern.isAny()) {
				explicitStateProhibited = true;
			} else if(reactantComponentStatePattern != null && !reactantComponentStatePattern.isAny()) {
				anyStateProhibited = true;
			}		// if reactantComponentStatePattern is null nothing is prohibited, we may not have a matching reactant for this product
		}
		// ------------------------------------------------------------------- State
		if(showWhat == ShowWhat.ShowState && mc.getComponentStateDefinitions().size() != 0) {
			String prefix = "State:  ";
			String csdCurrentName = "";
			final Map<String, String> itemMap = new LinkedHashMap<String, String>();
			if(mcp.getComponentStatePattern() == null || mcp.getComponentStatePattern().isAny()) {
				csdCurrentName = "<html>" + prefix + "<b>" + ComponentStatePattern.strAny + "</b></html>";
			} else {
				csdCurrentName = "<html>" + prefix + ComponentStatePattern.strAny + "</html>";
			}
			itemMap.put(csdCurrentName, ComponentStatePattern.strAny);
			for (final ComponentStateDefinition csd : mc.getComponentStateDefinitions()) {
				csdCurrentName = "";
				if(mcp.getComponentStatePattern() != null && !mcp.getComponentStatePattern().isAny()) {
					ComponentStateDefinition csdCurrent = mcp.getComponentStatePattern().getComponentStateDefinition();
					csdCurrentName = csdCurrent.getName();
				}
				String name = csd.getName();
				if(name.equals(csdCurrentName)) {	// currently selected menu item is shown in bold
					name = "<html>" + prefix + "<b>" + name + "</b></html>";
				} else {
					name = "<html>" + prefix + name + "</html>";
				}
				itemMap.put(name, csd.getName());
			}
			for(String key : itemMap.keySet()) {
				JMenuItem menuItem = new JMenuItem(key);
				if(!bIsReactant) {
					String name = itemMap.get(key);
					if(name.equals(ComponentStatePattern.strAny) && anyStateProhibited) {
						menuItem.setEnabled(false);
					} else if(!name.equals(ComponentStatePattern.strAny) && explicitStateProhibited) {
						menuItem.setEnabled(false);
					}
				}
				popupFromShapeMenu.add(menuItem);
				menuItem.setIcon(VCellIcons.rbmComponentStateIcon);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String key = e.getActionCommand();
						String name = itemMap.get(key);
						ComponentStatePattern csp = new ComponentStatePattern();
						if(!name.equals(ComponentStatePattern.strAny)) {
							ComponentStateDefinition csd = mcp.getMolecularComponent().getComponentStateDefinition(name);
							if(csd == null) {
								throw new RuntimeException("Missing ComponentStateDefinition " + name + " for Component " + mcp.getMolecularComponent().getName());
							}
							csp = new ComponentStatePattern(csd);
						}
						mcp.setComponentStatePattern(csp);
						if(bIsReactant) {
							reflectStateToProduct(mcp, csp);
							productTreeModel.populateTree();
						}
						treeModel.populateTree();
						shapePanel.repaint();
					}
				});
			}
		}
		if(showWhat == ShowWhat.ShowState) {
			return;
		}
		
		// ------------------------------------------------------------------------------------------- Bonds
		final MolecularTypePattern mtp = locationContext.getMolecularTypePattern();
		final SpeciesPattern sp = locationContext.getSpeciesPattern();
		
		JMenu editBondMenu = new JMenu();
		final String specifiedString = mcp.getBondType() == BondType.Specified ? "<html><b>" + "Site bond specified" + "</b></html>" : "<html>" + "Site bond specified" + "</html>";
		editBondMenu.setText(specifiedString);
		editBondMenu.setToolTipText("Specified");
		editBondMenu.removeAll();
		editBondMenu.setEnabled(!specifiedBondProhibited);
		final Map<String, Bond> itemMap = new LinkedHashMap<String, Bond>();
		
		String noneString = mcp.getBondType() == BondType.None ? "<html><b>" + "Site is unbound" + "</b></html>" : "<html>" + "Site is unbound" + "</html>";
		String existsString = mcp.getBondType() == BondType.Exists ? "<html><b>" + "Site has external bond" + "</b></html>" : "<html>" + "Site has external bond" + "</html>";	// Site is bound
		String possibleString = mcp.getBondType() == BondType.Possible ? "<html><b>" + "Site may be bound" + "</b></html>" : "<html>" + "Site may be bound" + "</html>";
		itemMap.put(noneString, null);
		itemMap.put(existsString, null);
		itemMap.put(possibleString, null);
		if(mtp != null && sp != null && !specifiedBondProhibited) {
			List<Bond> bondPartnerChoices = sp.getAllBondPartnerChoices(mtp, mc);
			for(Bond b : bondPartnerChoices) {
//				if(b.equals(mcp.getBond())) {
//					continue;	// if the mcp has a bond already we don't offer it
//				}
				int index = 0;
				if(mcp.getBondType() == BondType.Specified) {
					index = mcp.getBondId();
				} else {
					index = sp.nextBondId();
				}
				itemMap.put(b.toHtmlStringLong(sp, mtp, mc, index), b);
//				itemMap.put(b.toHtmlStringLong(sp, index), b);
			}
		}
		int index = 0;
		Graphics gc = shapePanel.getGraphics();
		for(String name : itemMap.keySet()) {
			JMenuItem menuItem = new JMenuItem(name);
			if(!bIsReactant) {
				if(name.equals(noneString) && noneBondProhibited) {
					menuItem.setEnabled(false);
				} else if(name.equals(existsString) && existBondProhibited) {
					menuItem.setEnabled(false);
				} else if(name.equals(possibleString) && possibleBondProhibited) {
					menuItem.setEnabled(false);
				} else if(!name.equals(noneString) && !name.equals(existsString) && !name.equals(possibleString) && specifiedBondProhibited) {
					menuItem.setEnabled(false);
				}
			}
			if(index == 0) {
				menuItem.setIcon(VCellIcons.rbmBondNoneIcon);
				menuItem.setToolTipText("None");
				popupFromShapeMenu.add(menuItem);
			} else if(index == 1) {
				menuItem.setIcon(VCellIcons.rbmBondExistsIcon);
				menuItem.setToolTipText("Exists");
				popupFromShapeMenu.add(menuItem);
			} else if(index == 2) {
				menuItem.setIcon(VCellIcons.rbmBondPossibleIcon);
				menuItem.setToolTipText("Possible");
				popupFromShapeMenu.add(menuItem);
			} else if(index > 2) {					// we skip None, Exists, Possible
				Bond b = itemMap.get(name);
				SpeciesPattern spBond = new SpeciesPattern(sp);		// clone of the sp, with only the bond of interest
				spBond.resetBonds();
				spBond.resetStates();
				MolecularTypePattern mtpFrom = spBond.getMolecularTypePattern(mtp.getMolecularType().getName(), mtp.getIndex());
				MolecularComponentPattern mcpFrom = mtpFrom.getMolecularComponentPattern(mc);
				MolecularTypePattern mtpTo = spBond.getMolecularTypePattern(b.molecularTypePattern.getMolecularType().getName(), b.molecularTypePattern.getIndex());
				MolecularComponentPattern mcpTo = mtpTo.getMolecularComponentPattern(b.molecularComponentPattern.getMolecularComponent());
				spBond.setBond(mtpTo, mcpTo, mtpFrom, mcpFrom);
				Icon icon = new SpeciesPatternSmallShape(3,4, spBond, gc, reactionRule, false);
				((SpeciesPatternSmallShape)icon).setDisplayRequirements(DisplayRequirements.highlightBonds);
				menuItem.setIcon(icon);
				editBondMenu.add(menuItem);
			}
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
						if(btBefore == BondType.Specified) {	// specified -> exists
							// change the partner to possible
							mcp.getBond().molecularComponentPattern.setBondType(BondType.Possible);
							mcp.getBond().molecularComponentPattern.setBond(null);
						}
						mcp.setBondType(BondType.Exists);
						mcp.setBond(null);
					} else if(name.equals(possibleString)) {
						if(btBefore == BondType.Specified) {	// specified -> possible
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
					if(bIsReactant) {
						reflectBondToProduct(mcp);
						productTreeModel.populateTree();
					}
					treeModel.populateTree();
					shapePanel.repaint();				
				}
			});
			index++;
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
							Icon icon = new MolecularTypeSmallShape(1, 4, mt, null, gc, mt, null);
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
							Icon icon = new MolecularTypeSmallShape(1, 4, mt, null, gc, mt, null);
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
