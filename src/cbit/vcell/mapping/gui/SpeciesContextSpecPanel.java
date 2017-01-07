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
import javax.swing.border.Border;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.graph.HighlightableShapeInterface;
import cbit.vcell.graph.LargeShapePanel;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.ExpressionException;

@SuppressWarnings("serial")
public class SpeciesContextSpecPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecParameterTableModel ivjSpeciesContextSpecParameterTableModel = null;
	
	private JScrollPane scrollPane;		// shapePanel lives inside this
	private SpeciesPatternLargeShape spls;
	private LargeShapePanel shapePanel = null;
	private JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);


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
		};
		shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point overWhat = e.getPoint();
				PointLocationInShapeContext locationContext = new PointLocationInShapeContext(overWhat);
				spls.contains(locationContext);
				shapePanel.setToolTipText("View-Only panel");
			} 
		});
		shapePanel.setBackground(Color.white);		
		shapePanel.setZoomFactor(-2);
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new java.awt.BorderLayout());
		upperPanel.add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
        getScrollPaneTable().setModel(getSpeciesContextSpecParameterTableModel());
		
        scrollPane = new JScrollPane(shapePanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		splitPaneHorizontal.setTopComponent(upperPanel);
		splitPaneHorizontal.setBottomComponent(scrollPane);
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

private void updateInterface(SpeciesContextSpec speciesContextSpec) {
	updateShape(speciesContextSpec);
}

public static final int xOffsetInitial = 20;
public static final int yOffsetInitial = 10;
private void updateShape(SpeciesContextSpec speciesContextSpec) {
	shapePanel.setZoomFactor(-3);
	if(speciesContextSpec != null) {
		SpeciesContext sc = speciesContextSpec.getSpeciesContext();
		SpeciesPattern sp = sc.getSpeciesPattern();
		spls = new SpeciesPatternLargeShape(xOffsetInitial, yOffsetInitial, -1, sp, shapePanel, sc);
		shapePanel.repaint();
	}
}

public void setSpeciesContextSpec(SpeciesContextSpec newValue) {
	getSpeciesContextSpecParameterTableModel().setSpeciesContextSpec(newValue);
	updateInterface(newValue);
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
