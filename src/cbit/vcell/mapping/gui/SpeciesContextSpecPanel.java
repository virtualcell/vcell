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
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.graph.HighlightableShapeInterface;
import cbit.vcell.graph.LargeShapePanel;
import cbit.vcell.graph.PointLocationInShapeContext;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class SpeciesContextSpecPanel extends DocumentEditorSubPanel {
	private JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecParameterTableModel ivjSpeciesContextSpecParameterTableModel1 = null;
	
	private JScrollPane scrollPane;		// shapePanel lives inside this
	private SpeciesPatternLargeShape spls;
	private LargeShapePanel shapePanel = null;



/**
 * Constructor
 */
public SpeciesContextSpecPanel() {
	super();
	initialize();
}

/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
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

/**
 * Return the SpeciesContextSpecParameterTableModel1 property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecParameterTableModel
 */
private SpeciesContextSpecParameterTableModel getSpeciesContextSpecParameterTableModel1() {
	if (ivjSpeciesContextSpecParameterTableModel1 == null) {
		try {
			ivjSpeciesContextSpecParameterTableModel1 = new SpeciesContextSpecParameterTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecParameterTableModel1;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	if (exception instanceof ExpressionException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in SpeciesContextSpecPanel");
	exception.printStackTrace(System.out);
}

/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {	
}

/**
 * Initialize the class.
 */
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
		shapePanel.setBackground(Color.white);		
		shapePanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point overWhat = e.getPoint();
				PointLocationInShapeContext locationContext = new PointLocationInShapeContext(overWhat);
				spls.contains(locationContext);
				HighlightableShapeInterface hsi = locationContext.getDeepestShape();
				if(hsi == null) {
					shapePanel.setToolTipText(null);
				} else {
					shapePanel.setToolTipText("Viewer Panel");
				}
			} 
		});
		shapePanel.setZoomFactor(-2);
		// ----------------------------------------------------------------------------------
		
		GridBagLayout mgr = new GridBagLayout();
		setLayout(mgr);
		Dimension size = new Dimension(572, 196);
		setSize(size);
		setBackground(Color.white);
		setName("SpeciesContextSpecPanel");
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
		add(getScrollPaneTable().getEnclosingScrollPane(), gbc);
        getScrollPaneTable().setModel(getSpeciesContextSpecParameterTableModel1());

		gridy++;
		size = new Dimension(572, 100);
        scrollPane = new JScrollPane(shapePanel);
        scrollPane.setSize(size);
        scrollPane.setPreferredSize(size);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc); 
		
        initConnections();
		
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
	shapePanel.setZoomFactor(-2);
	if(speciesContextSpec != null) {
		SpeciesContext sc = speciesContextSpec.getSpeciesContext();
		SpeciesPattern sp = sc.getSpeciesPattern();
		spls = new SpeciesPatternLargeShape(xOffsetInitial, yOffsetInitial, -1, sp, shapePanel, sc);
		
		int maxXOffset = xOffsetInitial + spls.getWidth();
		int maxYOffset = yOffsetInitial + 80;
		Dimension preferredSize = new Dimension(maxXOffset+120, maxYOffset+20);
		shapePanel.setPreferredSize(preferredSize);
		shapePanel.repaint();
	}
}




/**
 * Set the SpeciesContextSpec to a new value.
 * @param newValue cbit.vcell.mapping.SpeciesContextSpec
 */
void setSpeciesContextSpec(SpeciesContextSpec newValue) {
	getSpeciesContextSpecParameterTableModel1().setSpeciesContextSpec(newValue);
	updateInterface(newValue);
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
