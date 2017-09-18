/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.model.rbm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.graph.AbstractComponentShape;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.graph.gui.RulesShapePanel;
import cbit.vcell.graph.gui.ZoomShapeIcon;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class ViewGeneratedReactionsPanel extends DocumentEditorSubPanel  {
	private BNGReaction[] reactions = null;
	private EventHandler eventHandler = new EventHandler();
	private GeneratedReactionTableModel tableModel = null; 
	private EditorScrollTable table = null;
	private JTextField textFieldSearch = null;
	private JLabel totalReactionsLabel = new JLabel("");

	private JButton zoomLargerButton = null;
	private JButton zoomSmallerButton = null;

	private final NetworkConstraintsPanel owner;
	RulesShapePanel shapePanel = null;
	List<AbstractComponentShape> reactantPatternShapeList = new ArrayList<AbstractComponentShape>();
	List<AbstractComponentShape> productPatternShapeList = new ArrayList<AbstractComponentShape>();

	
	private class EventHandler implements ActionListener, DocumentListener, ListSelectionListener, TableModelListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getZoomLargerButton()) {
				boolean ret = shapePanel.zoomLarger();
				getZoomLargerButton().setEnabled(ret);
				getZoomSmallerButton().setEnabled(true);
				int[] selectedRows = table.getSelectedRows();
				if(selectedRows.length == 1) {
					updateShape(selectedRows[0]);
				}
			} else if (e.getSource() == getZoomSmallerButton()) {
				boolean ret = shapePanel.zoomSmaller();
				getZoomLargerButton().setEnabled(true);
				getZoomSmallerButton().setEnabled(ret);
				int[] selectedRows = table.getSelectedRows();
				if(selectedRows.length == 1) {
					updateShape(selectedRows[0]);
				}
			}
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
		
		/*
		 *  TODO: (Warning) this code heavily borrows from RbmUtils.parseReactionRule and ReactionRuleEditorPropertiesPanel
		 *  Make sure to keep them in sync
		 */
		@Override
		public void valueChanged(ListSelectionEvent e) {
			int[] selectedRows = table.getSelectedRows();
			if(selectedRows.length == 1 && !e.getValueIsAdjusting()) {
				updateShape(selectedRows[0]);
			}
		}
		@Override
		public void tableChanged(TableModelEvent e) {
			if(table.getModel().getRowCount() == 0) {
				System.out.println("table is empty");
				reactantPatternShapeList.clear();
				productPatternShapeList.clear();
				shapePanel.repaint();
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {				
						table.setRowSelectionInterval(0,0);
					}
				});
			}
			totalReactionsLabel.setText("Species: " + table.getModel().getRowCount());
		}
	}
	
public ViewGeneratedReactionsPanel(NetworkConstraintsPanel owner) {
	super();
	this.owner = owner;
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

public void updateShape(int selectedRow) {
	GeneratedReactionTableRow reactionTableRow = tableModel.getValueAt(selectedRow);
	String inputString = reactionTableRow.getExpression();
	System.out.println(selectedRow + ": " + inputString);
//	ReactionRule newReactionRule = (ReactionRule)RbmUtils.parseReactionRule(inputString, bioModel);
	
	Model tempModel = null;
	try {
		tempModel = new Model("MyTempModel");
		tempModel.addFeature("c0");
	} catch (ModelException | PropertyVetoException e1) {
		e1.printStackTrace();
	}
	if(owner != null && owner.getSimulationContext() != null) {
		List <MolecularType> mtList = owner.getSimulationContext().getModel().getRbmModelContainer().getMolecularTypeList();
		try {
			tempModel.getRbmModelContainer().setMolecularTypeList(mtList);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Unexpected exception setting " + MolecularType.typeName + " list: "+e1.getMessage(),e1);
		}
	} else {
		throw new RuntimeException("Owner or SimulationContext are null.");		// This should not be possible
	}
	
	int arrowIndex = inputString.indexOf("<->");
	boolean bReversible = true;
	if (arrowIndex < 0) {
		arrowIndex = inputString.indexOf("->");
		bReversible = false;
	}
	
	String left = inputString.substring(0, arrowIndex).trim();
	String right = inputString.substring(arrowIndex + (bReversible ? 3 : 2)).trim();
	if (left.length() == 0 && right.length() == 0) {
		return;
	}

	// we recover the original rule that generated the flattened reaction we now try to transform back into a fake rule
	BNGReaction reactionObject = reactionTableRow.getReactionObject();
	String name = reactionObject.getRuleName();
	if(name.contains(GeneratedReactionTableModel.reverse)) {
		name = name.substring(GeneratedReactionTableModel.reverse.length());
	}
	if(name.endsWith(ReactionRule.DirectHalf)) {
		name = name.substring(0, name.indexOf(ReactionRule.DirectHalf));
	}
	if(name.endsWith(ReactionRule.InverseHalf)) {
		name = name.substring(0, name.indexOf(ReactionRule.InverseHalf));
	}
	// get the name of the original structure from the original rule and make here another structure with the same name
	String strStructure = null;
	Structure ruleStructure;
	SimulationContext sc = owner.getSimulationContext();
	ReactionRule rr = sc.getModel().getRbmModelContainer().getReactionRule(name);
	if(rr != null && rr.getStructure() != null) {
		strStructure = rr.getStructure().getName();
	}
	if(strStructure != null) {
		if(tempModel.getStructure(strStructure) == null) {
			try {
				if(rr.getStructure().getTypeName().equals(Structure.TYPE_NAME_MEMBRANE)) {
					tempModel.addMembrane(strStructure);
				} else {
					tempModel.addFeature(strStructure);
				}
			} catch (ModelException | PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		ruleStructure = tempModel.getStructure(strStructure);
	} else {
		throw new RuntimeException("Failed to recover a Structure name from the Reaction Rule: " + name);
	}
	// making the fake rules just for display purpose, actually they are the flattened reactions resulted from bngl
	// the name is probably not unique, it's likely that many flattened reactions are derived from the same rule
	ReactionRule reactionRule = tempModel.getRbmModelContainer().createReactionRule(name, ruleStructure, bReversible);

	String regex = "[^!]\\+";
	String[] patterns = left.split(regex);
	for (String spString : patterns) {
		try {
			spString = spString.trim();
			// if compartments are present, we're cheating big time making some fake compartments just for compartment name display purposes
			SpeciesPattern speciesPattern = (SpeciesPattern)RbmUtils.parseSpeciesPattern(spString, tempModel);
			strStructure = RbmUtils.parseCompartment(spString, tempModel);
			speciesPattern.resolveBonds();
			Structure structure;
			if(strStructure != null) {
				if(tempModel.getStructure(strStructure) == null) {
					if(sc.getModel().getStructure(strStructure).getTypeName().equals(Structure.TYPE_NAME_MEMBRANE)) {
						tempModel.addMembrane(strStructure);
					} else {
						tempModel.addFeature(strStructure);
					}
				}
				structure = tempModel.getStructure(strStructure);
			} else {
				structure = ruleStructure;		// if nothing explicit for a participant, we use by default the structure of the rule
			}
			reactionRule.addReactant(new ReactantPattern(speciesPattern, structure));
		} catch(Throwable ex) {
			ex.printStackTrace();
			SpeciesPatternLargeShape spls = new SpeciesPatternLargeShape(20, 20, -1, shapePanel, true, issueManager);	// error (red circle)
			reactantPatternShapeList.clear();
			productPatternShapeList.clear();
			reactantPatternShapeList.add(spls);
			shapePanel.repaint();
			return;
		}
	}
	
	patterns = right.split(regex);
	for (String spString : patterns) {
		try {
			spString = spString.trim();						
			SpeciesPattern speciesPattern = (SpeciesPattern)RbmUtils.parseSpeciesPattern(spString, tempModel);
			strStructure = RbmUtils.parseCompartment(spString, tempModel);
			speciesPattern.resolveBonds();
			Structure structure;
			if(strStructure != null) {
				if(tempModel.getStructure(strStructure) == null) {
					if(sc.getModel().getStructure(strStructure).getTypeName().equals(Structure.TYPE_NAME_MEMBRANE)) {
						tempModel.addMembrane(strStructure);
					} else {
						tempModel.addFeature(strStructure);
					}
				}
				structure = tempModel.getStructure(strStructure);
			} else {
				structure = ruleStructure;
			}
//		BNGLParser parser = new BNGLParser(new StringReader(sp));
//		ASTSpeciesPattern astSpeciesPattern = parser.SpeciesPattern();
//		BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(tempModel, null, false);
//		SpeciesPattern speciesPattern = (SpeciesPattern) astSpeciesPattern.jjtAccept(constructionVisitor, null);
//		for(MolecularTypePattern mtp : speciesPattern.getMolecularTypePatterns()) {
//			mtp.setParticipantMatchLabel("*");
//		}
//		System.out.println(speciesPattern.toString());
			reactionRule.addProduct(new ProductPattern(speciesPattern, structure));
		} catch(Throwable ex) {
			ex.printStackTrace();
			SpeciesPatternLargeShape spls = new SpeciesPatternLargeShape(20, 20, -1, shapePanel, true, issueManager);	// error (red circle)
			reactantPatternShapeList.clear();
			productPatternShapeList.clear();
			reactantPatternShapeList.add(spls);
			shapePanel.repaint();
			return;
		}
	}			
//----------------------------------------------------------------------------------------------------
	
	List<ReactantPattern> rpList = reactionRule.getReactantPatterns();
	reactantPatternShapeList.clear();
	int xOffset = 20;
	int xOffsetRound = 20;
	if(rpList != null && rpList.size() > 0) {
		for(int i = 0; i<rpList.size(); i++) {
			SpeciesPattern sp = rpList.get(i).getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				mtp.setParticipantMatchLabel("*");
			}
			SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffset, 20, -1, sp, shapePanel, reactionRule, issueManager);
//			SpeciesPatternRoundShape sps = new SpeciesPatternRoundShape(xOffsetRound, 20, -1, sp, shapePanel, reactionRule);
			if(i < rpList.size()-1) {
				sps.addEndText("+");
			} else {
				if(reactionRule.isReversible()) {
					sps.addEndText("<->");
				} else {
					sps.addEndText("->");
				}
			}
			xOffset = sps.getRightEnd() + 45;
			xOffsetRound = sps.getRightEnd() + 45;
			reactantPatternShapeList.add(sps);
		}
	}
	xOffset += 15;	// space for the <-> sign
	List<ProductPattern> ppList = reactionRule.getProductPatterns();
	productPatternShapeList.clear();
	if(ppList != null && ppList.size() > 0) {
		for(int i = 0; i<ppList.size(); i++) {
			SpeciesPattern sp = ppList.get(i).getSpeciesPattern();
			for(MolecularTypePattern mtp : sp.getMolecularTypePatterns()) {
				mtp.setParticipantMatchLabel("*");
			}
			SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffset, 20, -1, sp, shapePanel, reactionRule, issueManager);
//			SpeciesPatternRoundShape sps = new SpeciesPatternRoundShape(xOffset, 20, -1, sp, shapePanel, reactionRule);
			if(i < ppList.size()-1) {
				sps.addEndText("+");
			}
			xOffset = sps.getRightEnd() + 45;
			productPatternShapeList.add(sps);
		}
	}
	Dimension preferredSize = new Dimension(xOffset+90, 50);
	shapePanel.setPreferredSize(preferredSize);

	shapePanel.repaint();
}

private void initialize() {
	try {
		setName("ViewGeneratedReactionsPanel");
		setLayout(new GridBagLayout());

		shapePanel = new RulesShapePanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(AbstractComponentShape stls : reactantPatternShapeList) {
					stls.paintSelf(g);
				}
				for(AbstractComponentShape stls : productPatternShapeList) {
					stls.paintSelf(g);
				}
			}
		};
		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
		shapePanel.setLayout(new GridBagLayout());
		shapePanel.setBackground(Color.white);
		shapePanel.setEditable(true);		// don't show the brown contour even though it's not editable
		shapePanel.setShowMoleculeColor(true);
		shapePanel.setShowNonTrivialOnly(true);
		
		JScrollPane scrollPane = new JScrollPane(shapePanel);
		scrollPane.setBorder(loweredBevelBorder);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridBagLayout());
		
		getZoomSmallerButton().setEnabled(true);
		getZoomLargerButton().setEnabled(false);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,0,0,10);
		gbc.anchor = GridBagConstraints.WEST;
		optionsPanel.add(getZoomLargerButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(2,0,4,10);
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

		Dimension dim = new Dimension(500, 140);
		containerOfScrollPanel.setPreferredSize(dim);	// dimension of shape panel
		containerOfScrollPanel.setMinimumSize(dim);
		containerOfScrollPanel.setMaximumSize(dim);

		// -----------------------------------------------------------------------------

		table = new EditorScrollTable();
		tableModel = new GeneratedReactionTableModel(table, owner);
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(eventHandler);
		table.getModel().addTableModelListener(eventHandler);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
//		table.getColumnModel().getColumn(GeneratedReactionTableModel.iColIndex).setCellRenderer(rightRenderer);	// right align first table column
//		table.getColumnModel().getColumn(GeneratedReactionTableModel.iColIndex).setMaxWidth(60);				// left column wide enough for 6-7 digits
		
		int gridy = 0;
		gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
//		table.setPreferredScrollableViewportSize(new Dimension(400,200));
		add(table.getEnclosingScrollPane(), gbc);
		
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
		
		gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 0, 4, 4);
		add(textFieldSearch, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		add(totalReactionsLabel, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		add(containerOfScrollPanel, gbc);
		
		// rendering the small shapes of a fake rule in the Depiction column of this viewer table)
		// TODO: this renderer is almost identical with the one in BioModelEditorModelPanel (which paints the small shapes 
		// of a rule in the Depiction column of the reaction table)
		DefaultScrollTableCellRenderer rbmReactionShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			List<SpeciesPatternSmallShape> spssList = new ArrayList<SpeciesPatternSmallShape>();
			SpeciesPatternSmallShape spss = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == tableModel) {
						selectedObject = tableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof GeneratedReactionTableRow) {
							
							ReactionRule rr = ((GeneratedReactionTableRow)selectedObject).getReactionRule();
							Graphics panelContext = table.getGraphics();
							BioModel bioModel = owner.getSimulationContext().getBioModel();

							spssList.clear();
							List<ReactantPattern> rpList = rr.getReactantPatterns();
							int xPos = 4;
							for(int i = 0; i<rpList.size(); i++) {
								SpeciesPattern sp = rr.getReactantPattern(i).getSpeciesPattern();
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, null, panelContext, rr, isSelected, issueManager);
								if(i < rpList.size()-1) {
									spss.addEndText("+");
								} else {
									if(rr.isReversible()) {
										spss.addEndText("<->");
										xPos += 7;
									} else {
										spss.addEndText("->");
									}
								}
								xPos += spss.getWidth() + 14;
								spssList.add(spss);
							}
							
							List<ProductPattern> ppList = rr.getProductPatterns();
							xPos+= 7;
							for(int i = 0; i<ppList.size(); i++) {
								SpeciesPattern sp = rr.getProductPattern(i).getSpeciesPattern();
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, null, panelContext, rr, isSelected, issueManager);
								if(i < ppList.size()-1) {
									spss.addEndText("+");
								}
								xPos += spss.getWidth() + 14;
								spssList.add(spss);
							}
						}
					} else {
						spssList.clear();
					}
				}
				setText("");
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(SpeciesPatternSmallShape spss : spssList) {
					if(spss == null) {
						continue;
					}
					spss.paintSelf(g);
				}
			}
		};
		table.getColumnModel().getColumn(GeneratedReactionTableModel.iColDepiction).setCellRenderer(rbmReactionShapeDepictionCellRenderer);
		table.getColumnModel().getColumn(GeneratedReactionTableModel.iColDepiction).setPreferredWidth(400);
		table.getColumnModel().getColumn(GeneratedReactionTableModel.iColDepiction).setMinWidth(400);
		
		table.getColumnModel().getColumn(GeneratedReactionTableModel.iColDefinition).setPreferredWidth(30);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
