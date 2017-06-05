/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.vcell.pathway.BioPaxObject;
import org.vcell.relationship.ConversionTableRow;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;

@SuppressWarnings("serial")
public class ConversionPanel extends DocumentEditorSubPanel  {
	private List<BioPaxObject> bioPaxObjects= null;
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	private BioModelEditorConversionTableModel tableModel = null; 
	private EditorScrollTable table = null;
	private JTextField textFieldSearch = null;
	private JCheckBox addSubunits = null;
	
	private class EventHandler implements ActionListener, DocumentListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
		}
		public void insertUpdate(DocumentEvent e) {
			searchTable();
		}
		public void removeUpdate(DocumentEvent e) {
			searchTable();
		}
		public void changedUpdate(DocumentEvent e) {
			searchTable();
		}
	}
	
public ConversionPanel() {
	super();
	initialize();
}

public ArrayList<ConversionTableRow> getTableRows() {
	return tableModel.getTableRows();
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		setName("KineticsTypeTemplatePanel");
		setLayout(new GridBagLayout());
			
		table = new EditorScrollTable();
		tableModel = new BioModelEditorConversionTableModel(table);
		table.setModel(tableModel);
		tableModel.setIssueManager(issueManager);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		table.setPreferredScrollableViewportSize(new Dimension(400,200));
		add(table.getEnclosingScrollPane(), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 9;
		gbc.gridy = gridy;
		// add toolTipText for each table cell
		table.addMouseMotionListener(new MouseMotionAdapter() { 
		    public void mouseMoved(MouseEvent e) { 	
		            Point p = e.getPoint(); 
		            int row = table.rowAtPoint(p);
		            int column = table.columnAtPoint(p);
		            table.setToolTipText(String.valueOf(table.getValueAt(row,column)));


		    } 
		});

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);

		textFieldSearch = new JTextField(70);
		textFieldSearch.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 0, 4, 4);
		add(textFieldSearch, gbc);
		
//		addSubunits = new JCheckBox("Add Subunits");
//		addSubunits.setHorizontalTextPosition(SwingConstants.LEFT);
//		addSubunits.setSelected(false);
//		addSubunits.setBackground(Color.white);
//		
//		gbc = new java.awt.GridBagConstraints();
//		gbc.gridx = 7; 
//		gbc.gridy = gridy;
//		gbc.anchor = GridBagConstraints.LINE_END;
//		gbc.insets = new Insets(4, 4, 4, 4);
//		add(addSubunits, gbc);
		
		setBackground(Color.white);		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void searchTable() {
	String searchText = textFieldSearch.getText();
	tableModel.setSearchText(searchText);
}
public boolean isAddSubunits() {
	return false;
//	return addSubunits.isSelected();
}

// done
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ConversionPanel aKineticsTypeTemplatePanel;
		aKineticsTypeTemplatePanel = new ConversionPanel();
		frame.setContentPane(aKineticsTypeTemplatePanel);
		frame.setSize(aKineticsTypeTemplatePanel.getSize());
		frame.setSize(500, 500);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Sets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @param kinetics The new value for the property.
 * @see #getKinetics
 */
public void setBioPaxObjects(List<BioPaxObject> newValue) {
	if (bioPaxObjects == newValue) {
		return;
	}
	bioPaxObjects = newValue;
	tableModel.setBioPaxObjects(newValue);
}

public void setBioModel(BioModel newValue) {
	if (bioModel == newValue) {
		return;
	}
	bioModel = newValue;
	tableModel.setBioModel(newValue);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	// TODO Auto-generated method stub
	
}

public BioModelEditorConversionTableModel getTableModel(){
	return tableModel;
}

}
