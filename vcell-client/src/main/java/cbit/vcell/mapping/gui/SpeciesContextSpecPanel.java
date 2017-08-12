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
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.ReactionCartoon.RuleAnalysisChanged;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.gui.LargeShapePanel;
import cbit.vcell.graph.gui.ZoomShapeIcon;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.Model;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.ExpressionException;

@SuppressWarnings("serial")
public class SpeciesContextSpecPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private SpeciesContext speciesContext = null;
	private EventHandler eventHandler = new EventHandler();

	
	private JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecParameterTableModel ivjSpeciesContextSpecParameterTableModel = null;
	
	private JScrollPane scrollPane;		// shapePanel lives inside this
	private SpeciesPatternLargeShape spls;
	private LargeShapePanel shapePanel = null;
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	private JButton zoomLargerButton = null;
	private JButton zoomSmallerButton = null;

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

public SpeciesContextSpecPanel() {
	super();
	initialize();
}

private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

private SpeciesContextSpecParameterTableModel getSpeciesContextSpecParameterTableModel() {
	if (ivjSpeciesContextSpecParameterTableModel == null) {
		try {
			ivjSpeciesContextSpecParameterTableModel = new SpeciesContextSpecParameterTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecParameterTableModel;
}

private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	if (exception instanceof ExpressionException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in SpeciesContextSpecPanel");
	exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		shapePanel = new LargeShapePanel() {		// glyph (shape) panel
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(spls != null) {
					spls.paintSelf(g);
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
			public RuleParticipantSignature getSignature() {
				return null;
			}

			@Override
			public GroupingCriteria getCriteria() {
				return null;
			}

			@Override
			public boolean isViewSingleRow() {
				return true;
			}
		};
		shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point overWhat = e.getPoint();
				PointLocationInShapeContext locationContext = new PointLocationInShapeContext(overWhat);
				spls.contains(locationContext);
				shapePanel.setToolTipText("View-Only panel");
			} 
		});
		shapePanel.setBackground(new Color(0xe0e0e0));		
		shapePanel.setZoomFactor(-2);
		shapePanel.setEditable(false);
		shapePanel.setShowMoleculeColor(false);
		shapePanel.setShowNonTrivialOnly(false);
		// --------------------------------------------------------------
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new java.awt.BorderLayout());
		upperPanel.add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
        getScrollPaneTable().setModel(getSpeciesContextSpecParameterTableModel());
		
        scrollPane = new JScrollPane(shapePanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridBagLayout());
		
		getZoomSmallerButton().setEnabled(true);
		getZoomLargerButton().setEnabled(true);
		
		GridBagConstraints gbc = new GridBagConstraints();
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
		
		// ------------------------------------------------
		splitPaneHorizontal.setTopComponent(upperPanel);
		splitPaneHorizontal.setBottomComponent(containerOfScrollPanel);
		splitPaneHorizontal.setOneTouchExpandable(true);
		splitPaneHorizontal.setDividerLocation(165);	// upper panel is 165 pixel height
		splitPaneHorizontal.setResizeWeight(1);

		setLayout(new BorderLayout());
		add(splitPaneHorizontal, BorderLayout.CENTER);
		setBackground(Color.white);
		setName("SpeciesContextSpecPanel");
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void updateInterface() {
	if(speciesContext == null) {
		return;
	}
	updateShape();
}

public static final int xOffsetInitial = 20;
public static final int yOffsetInitial = 10;
private void updateShape() {
	if(speciesContext == null) {
		return;
	}
	SpeciesPattern sp = speciesContext.getSpeciesPattern();
	spls = new SpeciesPatternLargeShape(xOffsetInitial, yOffsetInitial, -1, sp, shapePanel, speciesContext, issueManager);
	
	Dimension preferredSize = new Dimension(spls.getRightEnd()+40, yOffsetInitial+80);
	shapePanel.setPreferredSize(preferredSize);
	shapePanel.repaint();
}

public void setSpeciesContextSpec(SpeciesContextSpec scSpec) {
	if(scSpec == null) {
		this.speciesContext = null;
	} else {
		this.speciesContext = scSpec.getSpeciesContext();
	}
	getSpeciesContextSpecParameterTableModel().setSpeciesContextSpec(scSpec);
	updateInterface();
}
public void setBioModel(BioModel newValue) {
	if (bioModel == newValue) {
		return;
	}
	bioModel = newValue;
	if(bioModel == null) {
		return;
	}
	Model model = bioModel.getModel();
	if(model != null & model.getRbmModelContainer().getMolecularTypeList().size() > 0) {
		splitPaneHorizontal.setDividerLocation(165);
	} else {
		// since we have no molecular types we initialize a much smaller shape panel 
		// because we can only show a trivial shape (circle)
		splitPaneHorizontal.setDividerLocation(195);
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	SpeciesContextSpec speciesContextSpec = null;
	if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof SpeciesContextSpec) {
		speciesContextSpec = (SpeciesContextSpec) selectedObjects[0];
	}
	setSpeciesContextSpec(speciesContextSpec);	
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

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		SpeciesContextSpecPanel aSpeciesContextSpecPanel;
		aSpeciesContextSpecPanel = new SpeciesContextSpecPanel();
		frame.add("Center", aSpeciesContextSpecPanel);
		frame.setSize(aSpeciesContextSpecPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}

}
