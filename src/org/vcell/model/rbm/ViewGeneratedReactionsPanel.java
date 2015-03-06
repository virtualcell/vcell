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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.model.bngl.ASTSpeciesPattern;
import org.vcell.model.bngl.BNGLParser;
import org.vcell.model.rbm.RbmUtils.BnglObjectConstructionVisitor;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.model.Model;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;

@SuppressWarnings("serial")
public class ViewGeneratedReactionsPanel extends DocumentEditorSubPanel  {
	private BNGReaction[] reactions = null;
	private EventHandler eventHandler = new EventHandler();
	private GeneratedReactionTableModel tableModel = null; 
	private EditorScrollTable table = null;
	private JTextField textFieldSearch = null;
	
	private class EventHandler implements ActionListener, DocumentListener, ListSelectionListener {
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
//			int[] selectedRows = table.getSelectedRows();
//			if(selectedRows.length == 1 && !e.getValueIsAdjusting()) {
//				GeneratedReactionTableRow reactionTableRow = tableModel.getValueAt(selectedRows[0]);
//				String inputString = reactionTableRow.getExpression();
//				System.out.println(selectedRows[0] + ": " + inputString);
////				ReactionRule newReactionRule = (ReactionRule)RbmUtils.parseReactionRule(inputString, bioModel);
//				
//				Model model = new Model("MyTempModel");
//				
//				int arrowIndex = inputString.indexOf("<->");
//				boolean bReversible = true;
//				if (arrowIndex < 0) {
//					arrowIndex = inputString.indexOf("->");
//					bReversible = false;
//				}
//				
//				String left = inputString.substring(0, arrowIndex).trim();
//				String right = inputString.substring(arrowIndex + (bReversible ? 3 : 2)).trim();
//				if (left.length() == 0 && right.length() == 0) {
//					return;
//				}
//				ReactionRule reactionRule = model.getRbmModelContainer().createReactionRule("aaa", bReversible);
//				
//				String regex = "[^!]\\+";
//				String[] patterns = left.split(regex);
//				for (String sp : patterns) {
//					try {
//					BNGLParser parser = new BNGLParser(new StringReader(sp));
//					ASTSpeciesPattern astSpeciesPattern = parser.SpeciesPattern();
//					BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(model, null, false);
//					SpeciesPattern speciesPattern = (SpeciesPattern) astSpeciesPattern.jjtAccept(constructionVisitor, null);
//					System.out.println(speciesPattern.toString());
//					reactionRule.addReactant(new ReactantPattern(speciesPattern));
//					} catch(Throwable ex) {
//						ex.printStackTrace();
//						return;
//					}
//				}
//				
//				patterns = right.split(regex);
//				for (String sp : patterns) {
//					try {
//					BNGLParser parser = new BNGLParser(new StringReader(sp));
//					ASTSpeciesPattern astSpeciesPattern = parser.SpeciesPattern();
//					BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(model, null, false);
//					SpeciesPattern speciesPattern = (SpeciesPattern) astSpeciesPattern.jjtAccept(constructionVisitor, null);
//					System.out.println(speciesPattern.toString());
//					reactionRule.addProduct(new ProductPattern(speciesPattern));
//					} catch(Throwable ex) {
//						ex.printStackTrace();
//						return;
//					}
//				}			
//				System.out.println(" --- done.");
//			}
		}
	}
	
public ViewGeneratedReactionsPanel() {
	super();
	initialize();
}

public ArrayList<GeneratedReactionTableRow> getTableRows() {
	return tableModel.getTableRows();
}

private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		setName("ViewGeneratedReactionsPanel");
		setLayout(new GridBagLayout());
			
		table = new EditorScrollTable();
		tableModel = new GeneratedReactionTableModel(table);
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(eventHandler);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(GeneratedReactionTableModel.iColIndex).setCellRenderer(rightRenderer);	// right align
		table.getColumnModel().getColumn(GeneratedReactionTableModel.iColIndex).setMaxWidth(60);				// left column wide enough for 6-7 digits
		
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
		
		setBackground(Color.white);		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void searchTable() {
	String searchText = textFieldSearch.getText();
	tableModel.setSearchText(searchText);
}

public void setReactions(BNGReaction[] newValue) {
	if (reactions == newValue) {
		return;
	}
	reactions = newValue;
	tableModel.setReactions(newValue);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	
}

public GeneratedReactionTableModel getTableModel(){
	return tableModel;
}

}
