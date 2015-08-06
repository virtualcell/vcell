/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.model.rbm;

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
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.model.bngl.ParseException;
import org.vcell.util.Matchable;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class ViewGeneratedSpeciesPanel extends DocumentEditorSubPanel  {
	private BNGSpecies[] speciess = null;
	private EventHandler eventHandler = new EventHandler();
	private GeneratedSpeciesTableModel tableModel = null; 
	private EditorScrollTable table = null;
	private JTextField textFieldSearch = null;
	
	private final NetworkConstraintsPanel owner;
	JPanel shapePanel = null;
	private SpeciesPatternLargeShape spls;

	private class EventHandler implements ActionListener, DocumentListener, ListSelectionListener, TableModelListener {
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
		@Override
		public void valueChanged(ListSelectionEvent e) {
			int[] selectedRows = table.getSelectedRows();
			if(selectedRows.length == 1 && !e.getValueIsAdjusting()) {
				GeneratedSpeciesTableRow speciesTableRow = tableModel.getValueAt(selectedRows[0]);
				String inputString = speciesTableRow.getExpression();
//				System.out.println(selectedRows[0] + ": " + inputString);
				
				Model model = new Model("MyTempModel");
				if(owner != null && owner.getSimulationContext() != null) {
					List <MolecularType> mtList = owner.getSimulationContext().getModel().getRbmModelContainer().getMolecularTypeList();
					try {
						model.getRbmModelContainer().setMolecularTypeList(mtList);
					} catch (PropertyVetoException e1) {
						e1.printStackTrace();
						throw new RuntimeException("Unexpected exception setting " + MolecularType.typeName + " list: "+e1.getMessage(),e1);
					}
				} else {
					System.out.println("something is wrong, we just do nothing rather than crash");
					return;
				}
				try {
				SpeciesPattern sp = (SpeciesPattern)RbmUtils.parseSpeciesPattern(inputString, model);
				sp.resolveBonds();
//				System.out.println(sp.toString());
				SpeciesContext sc = new SpeciesContext(new Species("a",""), new Structure(null) {
					public boolean compareEqual(Matchable obj) { return false; }
					public void setName(String name, boolean bFromGUI) throws PropertyVetoException { }
					public String getTypeName() { return null; }
					public int getDimension() { return 0; }
				}, sp);
				Graphics panelContext = shapePanel.getGraphics();
				spls = new SpeciesPatternLargeShape(20, 20, -1, sp, panelContext, sc);
				shapePanel.repaint();
				} catch (ParseException e1) {
					e1.printStackTrace();
					Graphics panelContext = shapePanel.getGraphics();
					spls = new SpeciesPatternLargeShape(20, 20, -1, panelContext, true);	// error (red circle)
					shapePanel.repaint();
				}
			}
		}
		@Override
		public void tableChanged(TableModelEvent e) {
			if(table.getModel().getRowCount() == 0) {
				System.out.println("table is empty");
				spls = null;
				shapePanel.repaint();
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {				
						table.setRowSelectionInterval(0,0);
					}
				});
			}
		}
	}
	
public ViewGeneratedSpeciesPanel(NetworkConstraintsPanel owner) {
	super();
	this.owner = owner;
	initialize();
}

public ArrayList<GeneratedSpeciesTableRow> getTableRows() {
	return tableModel.getTableRows();
}

private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		setName("ViewGeneratedSpeciesPanel");
		setLayout(new GridBagLayout());
			
		table = new EditorScrollTable();
		tableModel = new GeneratedSpeciesTableModel(table);
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(eventHandler);
		table.getModel().addTableModelListener(eventHandler);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColIndex).setCellRenderer(rightRenderer);	// right align
		table.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColIndex).setMaxWidth(60);		// left column wide enough for 6-7 digits
		
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
		
		// ------------------------------------------------- shapePanel ----------------
		shapePanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(spls != null) {
					spls.paintSelf(g);
				}
			}
		};
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
		Dimension preferredSize = new Dimension(500, 50);
		shapePanel.setPreferredSize(preferredSize);
		shapePanel.setLayout(new GridBagLayout());
		shapePanel.setBorder(loweredBevelBorder);
		shapePanel.setBackground(Color.white);
		shapePanel.setVisible(true);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 0.3;
		gbc.gridwidth = 8;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		add(shapePanel, gbc);
		
				
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void searchTable() {
	String searchText = textFieldSearch.getText();
	tableModel.setSearchText(searchText);
}

public void setSpecies(BNGSpecies[] newValue) {
	if (speciess == newValue) {
		return;
	}
	speciess = newValue;
	tableModel.setData(owner.getSimulationContext().getModel(), newValue);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {

}

public GeneratedSpeciesTableModel getTableModel(){
	return tableModel;
}

}
