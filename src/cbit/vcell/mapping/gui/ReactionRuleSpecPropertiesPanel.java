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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.ReactionRulePropertiesTableModel;
import cbit.vcell.graph.HighlightableShapeInterface;
import cbit.vcell.graph.LargeShapePanel;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.ReactionRulePatternLargeShape;
import cbit.vcell.graph.RulesShapePanel;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class ReactionRuleSpecPropertiesPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private ReactionRule reactionRule = null;
	
	private ReactionRulePropertiesTableModel tableModel = null;
	private ScrollTable table = null;

	private JScrollPane scrollPane;		// shapePanel lives inside this
	private RulesShapePanel shapePanel = null;
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	private ReactionRulePatternLargeShape reactantShape;
	private ReactionRulePatternLargeShape productShape;

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
		shapePanel.setBackground(Color.white);		
		shapePanel.setZoomFactor(-3);
		shapePanel.setViewSingleRow(true);
		shapePanel.setShowMoleculeColor(true);
		shapePanel.setShowNonTrivialOnly(true);
		// ----------------------------------------------------------------------------------
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new java.awt.BorderLayout());
		upperPanel.add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
        getScrollPaneTable().setModel(getReactionRulePropertiesTableModel());
		
        scrollPane = new JScrollPane(shapePanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		splitPaneHorizontal.setTopComponent(upperPanel);
		splitPaneHorizontal.setBottomComponent(scrollPane);
		splitPaneHorizontal.setOneTouchExpandable(true);
		splitPaneHorizontal.setDividerLocation(160);
		splitPaneHorizontal.setResizeWeight(1);

		setLayout(new BorderLayout());
		add(splitPaneHorizontal, BorderLayout.CENTER);
		setBackground(Color.white);
		setName("SpeciesContextSpecPanel");
		
        initConnections();
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


private void updateInterface() {
	if(reactionRule == null) {
		return;
	}
	
	
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
	int maxXOffset;
	
	reactantShape = new ReactionRulePatternLargeShape(xOffsetInitial, yOffsetReactantInitial, -1, shapePanel, reactionRule, true);
	int xOffset = reactantShape.getRightEnd() + 70;
		
	productShape = new ReactionRulePatternLargeShape(xOffset, yOffsetReactantInitial, -1, shapePanel, reactionRule, false);
	xOffset += productShape.getRightEnd();

//	// TODO: instead of offset+100 compute the exact width of the image
//	Dimension preferredSize = new Dimension(xOffset+90, yOffsetReactantInitial+80+20);
//	shapePanel.setPreferredSize(preferredSize);
		
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
			tableModel = new ReactionRulePropertiesTableModel(getScrollPaneTable(), true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tableModel;
}


}
