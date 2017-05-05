/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

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
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.ReactionRulePropertiesTableModel;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.ReactionRulePatternLargeShape;
import cbit.vcell.graph.ZoomShapeIcon;
import cbit.vcell.graph.gui.RulesShapePanel;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class ReactionRuleSpecPropertiesPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private ReactionRule reactionRule = null;
	private EventHandler eventHandler = new EventHandler();
	
	private ReactionRulePropertiesTableModel tableModel = null;
	private ScrollTable table = null;

	private JScrollPane scrollPane;		// shapePanel lives inside this
	private RulesShapePanel shapePanel = null;
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	
	private JButton zoomLargerButton = null;
	private JButton zoomSmallerButton = null;
	
	@SuppressWarnings("rawtypes")
	private JComboBox kineticsTypeComboBox = null;
	JCheckBox isReversibleCheckBox = null;

	private ReactionRulePatternLargeShape reactantShape;
	private ReactionRulePatternLargeShape productShape;
	
	private class EventHandler implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getZoomLargerButton()) {
				boolean ret = shapePanel.zoomLarger();
				getZoomLargerButton().setEnabled(ret);
				getZoomSmallerButton().setEnabled(true);
				updateShape();
			} else if (e.getSource() == getZoomSmallerButton()) {
				boolean ret = shapePanel.zoomSmaller();
				getZoomLargerButton().setEnabled(true);
				getZoomSmallerButton().setEnabled(ret);
				updateShape();
			}
		}
	}
	
	
public ReactionRuleSpecPropertiesPanel() {
	super();
	initialize();
}

private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	if (exception instanceof ExpressionException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in SpeciesContextSpecPanel");
	exception.printStackTrace(System.out);
}


private void initConnections() throws java.lang.Exception {	
}

private void initialize() {
	try {
		shapePanel = new RulesShapePanel() {		// glyph (shape) panel
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				reactantShape.paintSelf(g);
				productShape.paintSelf(g);
			}
		};
		shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point overWhat = e.getPoint();
				PointLocationInShapeContext locationContext = new PointLocationInShapeContext(overWhat);
				reactantShape.contains(locationContext);
				productShape.contains(locationContext);
				shapePanel.setToolTipText("View-Only panel.");
			} 
		});
		shapePanel.setBackground(new Color(0xe0e0e0));		
		shapePanel.setZoomFactor(-2);
		shapePanel.setEditable(false);	// colored with a shade of brown, close to DefaultScrollTableCellRenderer.uneditableForeground
		shapePanel.setViewSingleRow(true);
		shapePanel.setShowMoleculeColor(false);
		shapePanel.setShowNonTrivialOnly(false);
//		shapePanel.setShowDifferencesOnly(true);
		// ----------------------------------------------------------------------------------
		
		JPanel upperPanel = new JPanel();
		
		upperPanel.setName("ReactionRuleSpecKineticsPanel");
		upperPanel.setLayout(new GridBagLayout());
		upperPanel.setBackground(Color.white);

		int gridy = 0;

		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = new JLabel("<html><u>View only. Edit properties in Physiology</u></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		upperPanel.add(label, gbc);
		
		isReversibleCheckBox = new JCheckBox("Reversible");
		isReversibleCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		isReversibleCheckBox.setEnabled(false);
		isReversibleCheckBox.setBackground(upperPanel.getBackground());
		
		gridy++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(0, 8, 4, 4);
		upperPanel.add(isReversibleCheckBox, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(0, 8, 4, 4);
		upperPanel.add(new JLabel("Kinetic Type"), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 2; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		upperPanel.add(getKineticsTypeComboBox(), gbc);
		getKineticsTypeComboBox().setEnabled(false);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 3;
		upperPanel.add(getScrollPaneTable().getEnclosingScrollPane(), gbc);
        getScrollPaneTable().setModel(getReactionRulePropertiesTableModel());
		
        scrollPane = new JScrollPane(shapePanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridBagLayout());
		
		getZoomSmallerButton().setEnabled(true);
		getZoomLargerButton().setEnabled(true);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(4,4,0,10);
		gbc.anchor = GridBagConstraints.WEST;
		optionsPanel.add(getZoomLargerButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(4,4,4,10);
		gbc.anchor = GridBagConstraints.WEST;
		optionsPanel.add(getZoomSmallerButton(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		optionsPanel.add(new JLabel(""), gbc);

		JPanel containerOfScrollPanel = new JPanel();
		containerOfScrollPanel.setLayout(new BorderLayout());
		containerOfScrollPanel.add(optionsPanel, BorderLayout.WEST);
		containerOfScrollPanel.add(scrollPane, BorderLayout.CENTER);

		// -------------------------------------------------------------------------------
		splitPaneHorizontal.setTopComponent(upperPanel);
		splitPaneHorizontal.setBottomComponent(containerOfScrollPanel);
		splitPaneHorizontal.setOneTouchExpandable(true);
		splitPaneHorizontal.setDividerLocation(125);
		splitPaneHorizontal.setResizeWeight(1);

		setLayout(new BorderLayout());
		add(splitPaneHorizontal, BorderLayout.CENTER);
		setBackground(Color.white);
		setName("ReactionRuleSpecPropertiesPanel");
		
        initConnections();
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


private void updateInterface() {
	
	isReversibleCheckBox.setSelected(false);

	if(reactionRule == null) {
		return;
	}
	isReversibleCheckBox.setSelected(reactionRule.isReversible());
	updateShape();
}

public static final int xOffsetInitial = 25;
public static final int yOffsetReactantInitial = 2;
public static final int yOffsetProductInitial = 100;
public static final int ReservedSpaceForNameOnYAxis = 10;

private void updateShape() {
	if(reactionRule == null) {
		return;
	}
	reactantShape = new ReactionRulePatternLargeShape(xOffsetInitial, yOffsetReactantInitial, -1, shapePanel, reactionRule, true);
	int xOffset = reactantShape.getRightEnd() + 70;
		
	productShape = new ReactionRulePatternLargeShape(xOffset, yOffsetReactantInitial, -1, shapePanel, reactionRule, false);
	xOffset += productShape.getRightEnd();

	// TODO: instead of offset+100 compute the exact width of the image
	Dimension preferredSize = new Dimension(xOffset+40, yOffsetReactantInitial+80);
	shapePanel.setPreferredSize(preferredSize);
	shapePanel.repaint();
}


public void setBioModel(BioModel newValue) {
	if (bioModel == newValue) {
		return;
	}
	bioModel = newValue;
	if(bioModel == null) {
		return;
	}
	
}
public void setReactionRule(ReactionRuleSpec rrSpec) {
	if(rrSpec == null) {
		this.reactionRule = null;
	} else {
		ReactionRule rr = rrSpec.getReactionRule();
		this.reactionRule = rr;
	}
	getReactionRulePropertiesTableModel().setReactionRule(reactionRule);
	updateInterface();
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	ReactionRuleSpec reactionRuleSpec = null;
	if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof ReactionRuleSpec) {
		reactionRuleSpec = (ReactionRuleSpec) selectedObjects[0];
	}
	setReactionRule(reactionRuleSpec);	
}

private ScrollTable getScrollPaneTable() {
	if (table == null) {
		try {
			table = new ScrollTable();
			table.setModel(getReactionRulePropertiesTableModel());
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return table;
}
private ReactionRulePropertiesTableModel getReactionRulePropertiesTableModel() {
	if (tableModel == null) {
		try {
			tableModel = new ReactionRulePropertiesTableModel(getScrollPaneTable(), false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tableModel;
}

private JButton getZoomLargerButton() {
	if (zoomLargerButton == null) {
		zoomLargerButton = new JButton();		// "+"
//		ResizeCanvasShape.setCanvasNormalMod(zoomLargerButton, ResizeCanvasShape.Sign.expand);
		ZoomShapeIcon.setZoomMod(zoomLargerButton, ZoomShapeIcon.Sign.plus);
		zoomLargerButton.addActionListener(eventHandler);
	}
	return zoomLargerButton;
}
private JButton getZoomSmallerButton() {
	if (zoomSmallerButton == null) {
		zoomSmallerButton = new JButton();		// -
//		ResizeCanvasShape.setCanvasNormalMod(zoomSmallerButton, ResizeCanvasShape.Sign.shrink);
		ZoomShapeIcon.setZoomMod(zoomSmallerButton, ZoomShapeIcon.Sign.minus);
		zoomSmallerButton.addActionListener(eventHandler);
	}
	return zoomSmallerButton;
}
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
private JComboBox getKineticsTypeComboBox() {
	if (kineticsTypeComboBox == null) {
		kineticsTypeComboBox = new JComboBox();
		kineticsTypeComboBox.setName("JComboBox1");
		kineticsTypeComboBox.setRenderer(new DefaultListCellRenderer() {
			private final static String PI = "\u03A0";
			private final static String PIs = "<strong>\u03A0</strong>";
			private final static String PIsb = "<strong><big>\u03A0</big></strong>";
			private final static String pi = "\u03C0";
			private final static String MULT = "\u22C5";

			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
				String str = "<html>Mass Action ( for each reaction:  ";
				str += "Kf" + MULT + PIs + " <small>reactants</small> - Kr" + MULT + PIs + " <small>products</small> )</html>";
				setText(str);
				return component;
			}
		});
	}
	return kineticsTypeComboBox;
}



}
