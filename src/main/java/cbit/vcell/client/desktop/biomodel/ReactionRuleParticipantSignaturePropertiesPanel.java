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
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
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
import org.vcell.util.Pair;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.GraphConstants;
import cbit.vcell.graph.HighlightableShapeInterface;
import cbit.vcell.graph.MolecularComponentLargeShape;
import cbit.vcell.graph.MolecularTypeLargeShape;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.ReactionCartoon;
import cbit.vcell.graph.ReactionRulePatternLargeShape;
import cbit.vcell.graph.RuleParticipantEdgeDiagramShape;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.graph.MolecularComponentLargeShape.ComponentStateLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape.DisplayRequirements;
import cbit.vcell.graph.gui.LargeShapePanel;
import cbit.vcell.graph.gui.ParticipantSignatureShapePanel;
import cbit.vcell.graph.gui.RulesShapePanel;
import cbit.vcell.graph.gui.ZoomShapeIcon;
import cbit.vcell.graph.gui.ZoomShapeIcon.Sign;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRuleParticipant;
import cbit.vcell.model.RbmObservable.ObservableType;
import cbit.vcell.model.RbmObservable.Sequence;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.Structure;
import cbit.vcell.model.common.VCellErrorMessages;


@SuppressWarnings("serial")

public class ReactionRuleParticipantSignaturePropertiesPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private RuleParticipantSignature signature = null;

	private JCheckBox showMoleculeColorButton = null;
	private JCheckBox showDifferenceButton = null;
	private JCheckBox showNonTrivialButton = null;
	
	private JButton zoomLargerButton = null;
	private JButton zoomSmallerButton = null;
	
	private Map<String,ReactionRule> reactionRuleMap = new LinkedHashMap<>();	// rules that involve the participant with this signature
	private List<Pair<ReactionRulePatternLargeShape, ReactionRulePatternLargeShape>> ruleShapeList = new ArrayList<> ();	// pair of reactants and products for a rule
	
	private ParticipantSignatureShapePanel shapePanel;
	private JScrollPane scrollPane;
	private JPanel containerOfScrollPanel;

	private InternalEventHandler eventHandler = new InternalEventHandler();
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == signature) {
				if (evt.getPropertyName().equals("entityChange")) {
					updateInterface();
				}
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getZoomLargerButton()) {
				boolean ret = shapePanel.zoomLarger();
				getZoomLargerButton().setEnabled(ret);
				getZoomSmallerButton().setEnabled(true);
				updateInterface();
			} else if (e.getSource() == getZoomSmallerButton()) {
				boolean ret = shapePanel.zoomSmaller();
				getZoomLargerButton().setEnabled(true);
				getZoomSmallerButton().setEnabled(ret);
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
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	public ReactionRuleParticipantSignaturePropertiesPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		try {
			shapePanel = new ParticipantSignatureShapePanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					for(Pair<ReactionRulePatternLargeShape, ReactionRulePatternLargeShape> p : ruleShapeList) {
						ReactionRulePatternLargeShape rp = p.one;
						rp.paintSelf(g);
						ReactionRulePatternLargeShape pp = p.two;
						pp.paintSelf(g);
					}
				}
			};
			
			shapePanel.setLayout(new GridBagLayout());
			shapePanel.setBackground(Color.white);
			shapePanel.zoomSmaller();
			shapePanel.zoomSmaller();
			shapePanel.zoomSmaller();
			getZoomSmallerButton().setEnabled(true);
			getZoomLargerButton().setEnabled(true);
			
			scrollPane = new JScrollPane(shapePanel);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			JPanel optionsPanel = new JPanel();
			optionsPanel.setPreferredSize(new Dimension(140, 200));
			optionsPanel.setLayout(new GridBagLayout());
			
			getZoomSmallerButton().setEnabled(true);
			getZoomLargerButton().setEnabled(true);
			
			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.insets = new Insets(8,4,4,0);
			gbc.anchor = GridBagConstraints.WEST;
			optionsPanel.add(getZoomLargerButton(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.insets = new Insets(8,0,4,4);
			gbc.anchor = GridBagConstraints.WEST;
			optionsPanel.add(getZoomSmallerButton(), gbc);
			// apparently we don't need a fake 3rd cell to the right

			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 2;
			gbc.weightx = 1;
			gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 10);
			optionsPanel.add(new JLabel(""), gbc);

			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(0,4,4,4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			optionsPanel.add(getShowMoleculeColorButton(), gbc);

			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(0,4,4,4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			optionsPanel.add(getShowNonTrivialButton(), gbc);

			gridy++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(0,4,4,4);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			optionsPanel.add(getShowDifferencesButton(), gbc);

			getShowMoleculeColorButton().setSelected(true);
			getShowNonTrivialButton().setSelected(true);
			getShowDifferencesButton().setSelected(false);
			shapePanel.setShowMoleculeColor(getShowMoleculeColorButton().isSelected());
			shapePanel.setShowNonTrivialOnly(getShowNonTrivialButton().isSelected());
			shapePanel.setShowDifferencesOnly(getShowDifferencesButton().isSelected());
			
			
			containerOfScrollPanel = new JPanel();
			containerOfScrollPanel.setLayout(new BorderLayout());
			containerOfScrollPanel.add(optionsPanel, BorderLayout.WEST);
			containerOfScrollPanel.add(scrollPane, BorderLayout.CENTER);
			
			setLayout(new BorderLayout());
			add(containerOfScrollPanel, BorderLayout.CENTER);
			setBackground(Color.white);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
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
	
	private JButton getZoomLargerButton() {
		if (zoomLargerButton == null) {
			zoomLargerButton = new JButton();
			ZoomShapeIcon.setZoomMod(zoomLargerButton, ZoomShapeIcon.Sign.plus);
			zoomLargerButton.addActionListener(eventHandler);
		}
		return zoomLargerButton;
	}
	private JButton getZoomSmallerButton() {
		if (zoomSmallerButton == null) {
			zoomSmallerButton = new JButton();
			ZoomShapeIcon.setZoomMod(zoomSmallerButton, ZoomShapeIcon.Sign.minus);
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
		if (bioModel == newValue) {
			return;
		}
		bioModel = newValue;
	}	
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) {
			setRuleParticipantSignature(null);
		} else if (selectedObjects[0] instanceof RuleParticipantSignature) {
			setRuleParticipantSignature((RuleParticipantSignature) selectedObjects[0]);
		} else {
			setRuleParticipantSignature(null);
		}
	}

	public void setRuleParticipantSignature(RuleParticipantSignature newValue) {
		if (signature == newValue) {
			return;
		}
		RuleParticipantSignature oldValue = signature;
		if (oldValue != null) {
//			oldValue.removePropertyChangeListener(eventHandler);
		}
		signature = newValue;
		if(shapePanel != null) {
			findRulesForSignature();
			shapePanel.setSignature(signature);
			shapePanel.setRulesForSignature(reactionRuleMap);
		}
		
		if (newValue != null) {
//			newValue.addPropertyChangeListener(eventHandler);
		}
		updateInterface();
	}

	protected void updateInterface() {
		if (signature == null){	// sanity check
			return;
		}
		updateShape();
	}
	
	private void findRulesForSignature() {
		reactionRuleMap.clear();
		if(signature == null) {
			return;
		}
//		ReactionCartoon rc = (ReactionCartoon) signature.getModelCartoon();
//		RuleParticipantSignature.Criteria crit = rc.getRuleParticipantGroupingCriteria();
//		shapePanel.setCriteria(crit);
		
		GroupingCriteria crit = signature.getGroupingCriteria();
		shapePanel.setCriteria(crit);
		for(ReactionRule rr : bioModel.getModel().getRbmModelContainer().getReactionRuleList()) {
			boolean found = false;
			for(ReactionRuleParticipant participant : rr.getReactionRuleParticipants()) {
				if (signature.getStructure() == participant.getStructure() && signature.compareByCriteria(participant.getSpeciesPattern(), crit)){
					found = true;
					break;
				}
			}
			if(!found) {
				continue;	// this rule has no participant with this signature, go to next
			}
			reactionRuleMap.put(rr.getName(), rr);
		}
	}

	public static final int xOffsetInitial = 15;
	public static final int yOffsetReactantInitial = 8;
	public static final int ReservedSpaceForNameOnYAxis = GraphConstants.ReactionRuleParticipantDisplay_ReservedSpaceForNameOnYAxis;
	
	private void updateShape() {
		int maxXOffset = 0;
		ruleShapeList.clear();
		
		// all the reactants go in one single ReactionRulePatternLargeShape, all the products the other
		int yOffset = yOffsetReactantInitial + GraphConstants.ReactionRuleParticipantDisplay_ReservedSpaceForNameOnYAxis;
		for(Map.Entry<String,ReactionRule> entry : reactionRuleMap.entrySet()) {
			ReactionRule rr = entry.getValue();
			ReactionRulePatternLargeShape reactantShape = new ReactionRulePatternLargeShape(xOffsetInitial, yOffset, -1, shapePanel, rr, true, issueManager);
			reactantShape.setWriteName(true);
			int xOffset = reactantShape.getRightEnd() + 70;
			
			ReactionRulePatternLargeShape productShape = new ReactionRulePatternLargeShape(xOffset, yOffset, -1, shapePanel, rr, false, issueManager);
			xOffset += productShape.getRightEnd();
			yOffset += SpeciesPatternLargeShape.defaultHeight + GraphConstants.ReactionRuleParticipantDisplay_ReservedSpaceForNameOnYAxis;
			maxXOffset = Math.max(maxXOffset, xOffset);
			
			Pair<ReactionRulePatternLargeShape, ReactionRulePatternLargeShape> p = new Pair<> (reactantShape, productShape);
			ruleShapeList.add(p);
		}
		int maxYOffset = Math.max(yOffsetReactantInitial + SpeciesPatternLargeShape.defaultHeight + GraphConstants.ReactionRuleParticipantDisplay_ReservedSpaceForNameOnYAxis,
				yOffsetReactantInitial + (SpeciesPatternLargeShape.defaultHeight + GraphConstants.ReactionRuleParticipantDisplay_ReservedSpaceForNameOnYAxis)*ruleShapeList.size());
		Dimension preferredSize = new Dimension(maxXOffset, maxYOffset);
		
		shapePanel.setPreferredSize(preferredSize);
		containerOfScrollPanel.repaint();
	}

}
