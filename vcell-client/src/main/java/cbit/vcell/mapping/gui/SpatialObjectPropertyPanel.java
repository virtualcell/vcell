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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SurfaceRegionObject;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class SpatialObjectPropertyPanel extends DocumentEditorSubPanel {
	private JSortTable scrollPaneTable = null;
	private SpatialQuantityTableModel spatialQuantityTableModel = null;
//	private JLabel optionsTitle = null;
	private JPanel optionsPanel = null;
	private JPanel topPanel = null;
	private ArrayList<JCheckBox> optionsCheckboxes = new ArrayList<JCheckBox>();
//	private JTextArea statusTextArea = null;
	private SpatialObject spatialObject = null;
	
	private ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (spatialObject != null && e.getSource() instanceof JCheckBox){
				JCheckBox checkbox = (JCheckBox)e.getSource();
				boolean isSelected = checkbox.isSelected();
				int index = optionsCheckboxes.indexOf(checkbox);
				if (index>=0 && index < spatialObject.getQuantityCategories().size()){
					QuantityCategory category = spatialObject.getQuantityCategories().get(index);
					spatialObject.setQuantityCategoryEnabled(category, isSelected);
					spatialQuantityTableModel.fireTableDataChanged();
				}
			}
		}
		
	};

/**
 * Constructor
 */
public SpatialObjectPropertyPanel() {
	super();
	initialize();
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
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in SpatialObjectPropertyPanel");
	exception.printStackTrace(System.out);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("SpatialObjectPropertyPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(572, 196);
		setBackground(Color.white);
		
		this.topPanel = new JPanel();
		topPanel.setName("TopPanel");
		topPanel.setLayout(new BorderLayout());
		add(topPanel,BorderLayout.NORTH);
		
//		this.optionsTitle = new JLabel("select spatial quantities",SwingConstants.CENTER);
//		optionsTitle.setName("optionsTitle");
//		topPanel.add(optionsTitle, BorderLayout.NORTH);
		
		this.optionsPanel = new JPanel();
		optionsPanel.setBackground(Color.lightGray.brighter());
		optionsPanel.setName("OptionsPanel");
		optionsPanel.setLayout(new FlowLayout());
		topPanel.add(optionsPanel,BorderLayout.CENTER);

//		this.statusTextArea = new JTextArea();
//		statusTextArea.setName("StatusTextArea");
//		statusTextArea.setText("abcdefghijklmnopqrstuvwxyz abcdefghijklmnopqrstuvwxyz abcdefghijklmnopqrstuvwxyz abcdefghijklmnopqrstuvwxyz");
//		topPanel.add(statusTextArea, BorderLayout.SOUTH);
		
//		JLabel label = new JLabel("<html><u>Select only one species to set initial conditions</u></html>");
//		label.setHorizontalAlignment(SwingConstants.CENTER);
//		label.setFont(label.getFont().deriveFont(Font.BOLD));
//		add(label, BorderLayout.NORTH);
		
		this.scrollPaneTable = getScrollTable();
		add(scrollPaneTable.getEnclosingScrollPane(), BorderLayout.CENTER);
		
		this.scrollPaneTable.setModel(getSpatialQuantityTableModel());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		SpatialObjectPropertyPanel aSpeciesContextSpecPanel;
		aSpeciesContextSpecPanel = new SpatialObjectPropertyPanel();
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

/**
 * Set the SpeciesContextSpec to a new value.
 * @param newValue cbit.vcell.mapping.SpeciesContextSpec
 */
void setSpatialObject(SpatialObject newValue) {
	SpatialObject oldSpatialObject = this.spatialObject;
	this.spatialObject = newValue;
	getSpatialQuantityTableModel().setSpatialObject(this.spatialObject);
	for (JCheckBox checkbox : this.optionsCheckboxes){
		checkbox.removeActionListener(actionListener);
	}
	optionsCheckboxes.clear();
	optionsPanel.removeAll();
	
	if (spatialObject != null){
		for (QuantityCategory category : spatialObject.getQuantityCategories()){
			JCheckBox checkbox = new JCheckBox();
			checkbox.addActionListener(actionListener);
			checkbox.setText(category.description);
			checkbox.setSelected(spatialObject.isQuantityCategoryEnabled(category));
			checkbox.setBackground(Color.lightGray.brighter());
			// TODO: disable features not supported yet
			if(spatialObject instanceof SurfaceRegionObject) {
				if(category.xmlName.contentEquals(QuantityCategory.SurfaceDistanceMap.xmlName)) {
					checkbox.setEnabled(false);
				}
				if(category.xmlName.contentEquals(QuantityCategory.DirectionToSurface.xmlName)) {
					checkbox.setEnabled(false);
				}
			}
			if(spatialObject instanceof VolumeRegionObject) {
				if(category.xmlName.contentEquals(QuantityCategory.Centroid.xmlName)) {
					checkbox.setEnabled(false);
				}
			}
			optionsPanel.add(checkbox);
			System.out.println("adding checkbox for "+checkbox.getText());
			optionsCheckboxes.add(checkbox);
		}
	}
	optionsPanel.revalidate();
}

private SpatialQuantityTableModel getSpatialQuantityTableModel() {
	if (spatialQuantityTableModel == null){
		spatialQuantityTableModel = new SpatialQuantityTableModel(getScrollTable());
	}
	return spatialQuantityTableModel;
}


private JSortTable getScrollTable() {
	if (scrollPaneTable == null){
		scrollPaneTable = new JSortTable();
		scrollPaneTable.setName("SortTable");
	}
	return scrollPaneTable;
}


@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	SpatialObject spatialObject = null;
	if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof SpatialObject) {
		spatialObject = (SpatialObject) selectedObjects[0];
	}
	setSpatialObject(spatialObject);	
}

}
