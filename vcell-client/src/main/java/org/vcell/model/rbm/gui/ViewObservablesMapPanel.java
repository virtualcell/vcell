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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
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
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Pair;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.bionetgen.ObservableGroup;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.graph.ReactionCartoon.RuleAnalysisChanged;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.graph.gui.LargeShapePanel;
import cbit.vcell.graph.gui.ZoomShapeIcon;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class ViewObservablesMapPanel extends DocumentEditorSubPanel  {
	private BNGSpecies[] speciess = null;
	private ObservableGroup[] observabless = null;
	private EventHandler eventHandlerO = new EventHandler();
	private EventHandler eventHandlerS = new EventHandler();
	private GeneratedSpeciesTableModel speciesTableModel = null; 
	private ObservablesGroupTableModel observablesTableModel = null; 
	private EditorScrollTable speciesTable = null;
	private EditorScrollTable observablesTable = null;
	private JTextField textFieldSearch = null;
	
	private JButton zoomLargerButton = null;
	private JButton zoomSmallerButton = null;

	private final NetworkConstraintsPanel owner;
	LargeShapePanel shapePanel = null;
	private SpeciesPatternLargeShape spls;

	private class EventHandler implements ActionListener, DocumentListener, ListSelectionListener, TableModelListener {
		
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getZoomLargerButton()) {
				boolean ret = shapePanel.zoomLarger();
				getZoomLargerButton().setEnabled(ret);
				getZoomSmallerButton().setEnabled(true);
				int[] selectedRows = speciesTable.getSelectedRows();
				if(selectedRows.length == 1) {
					updateShape(selectedRows[0]);
				}
			} else if (e.getSource() == getZoomSmallerButton()) {
				boolean ret = shapePanel.zoomSmaller();
				getZoomLargerButton().setEnabled(true);
				getZoomSmallerButton().setEnabled(ret);
				int[] selectedRows = speciesTable.getSelectedRows();
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
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(this == eventHandlerO) {
				int[] selectedRows = observablesTable.getSelectedRows();
				if(selectedRows.length == 1 && !e.getValueIsAdjusting()) {
					System.out.println("Observable, row " + selectedRows[0]);
					speciesTableModel.setObservableFilter(observablesTableModel.getValueAt(selectedRows[0]));
				} else if(selectedRows.length != 1 && !e.getValueIsAdjusting()) {
					System.out.println("Observable, unselected or multiple");
					speciesTableModel.setObservableFilter(null);
				}
			} else if(this == eventHandlerS) {
				int[] selectedRows = speciesTable.getSelectedRows();
				if(selectedRows.length == 1 && !e.getValueIsAdjusting()) {
					System.out.println("Species, row " + selectedRows[0]);
					updateShape(selectedRows[0]);
				}
			} else {
				System.out.println("Neither table");
			}
		}
		@Override
		public void tableChanged(TableModelEvent e) {
			if(this == eventHandlerO) {
				if(observablesTable.getModel().getRowCount() == 0) {
					System.out.println("table is empty");
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {				
							observablesTable.setRowSelectionInterval(0,0);
						}
					});
				}
			} else if(this == eventHandlerS) {
				if(speciesTable.getModel().getRowCount() == 0) {
					System.out.println("table is empty");
					spls = null;
					shapePanel.repaint();
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {				
							speciesTable.setRowSelectionInterval(0,0);
						}
					});
				}
			}
		}
	}
	
public ViewObservablesMapPanel(NetworkConstraintsPanel owner) {
	super();
	this.owner = owner;
	initialize();
}

public ArrayList<GeneratedSpeciesTableRow> getTableRows() {
	return speciesTableModel.getTableRows();
}

private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		// --------------------------------------- the split panels
		setName("ViewGeneratedSpeciesPanel");
		setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		
		JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPaneHorizontal.setOneTouchExpandable(true);
		splitPaneHorizontal.setDividerLocation(120);
		splitPaneHorizontal.setResizeWeight(0.5);
		splitPaneHorizontal.setTopComponent(topPanel);
		splitPaneHorizontal.setBottomComponent(bottomPanel);
		add(splitPaneHorizontal, BorderLayout.CENTER);

		// ---------------------------------------- species shape panel
		shapePanel = new LargeShapePanel() {
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
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
		shapePanel.setLayout(new GridBagLayout());
		shapePanel.setBackground(Color.white);
		shapePanel.setEditable(true);		// not really editable but we don't want the brown contours here
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
		
		Dimension dim = new Dimension(500, 135);
		containerOfScrollPanel.setPreferredSize(dim);	// dimension of shape panel
		containerOfScrollPanel.setMinimumSize(dim);
		containerOfScrollPanel.setMaximumSize(dim);

		// -------------- connection between tables, table models, selection models, renderers, event handlers
		speciesTable = new EditorScrollTable();
		speciesTableModel = new GeneratedSpeciesTableModel(speciesTable, owner);
		speciesTable.setModel(speciesTableModel);
		speciesTable.getSelectionModel().addListSelectionListener(eventHandlerS);
		speciesTable.getModel().addTableModelListener(eventHandlerS);
		
		observablesTable = new EditorScrollTable();
		observablesTableModel = new ObservablesGroupTableModel(observablesTable, owner, speciesTableModel);
		observablesTable.setModel(observablesTableModel);
		observablesTable.getSelectionModel().addListSelectionListener(eventHandlerO);
		observablesTable.getModel().addTableModelListener(eventHandlerO);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColIndex).setCellRenderer(rightRenderer);	// right align
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColIndex).setMaxWidth(60);		// left column wide enough for 6-7 digits
		
		speciesTable.addMouseMotionListener(new MouseMotionAdapter() {		// add toolTipText for each table cell
		    public void mouseMoved(MouseEvent e) { 	
		            Point p = e.getPoint(); 
		            int row = speciesTable.rowAtPoint(p);
		            int column = speciesTable.columnAtPoint(p);
		            speciesTable.setToolTipText(String.valueOf(speciesTable.getValueAt(row,column)));
		    } 
		});

		// ---------------------------------------------- top panel
		int gridy = 0;
		gbc = new java.awt.GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		observablesTable.setPreferredScrollableViewportSize(new Dimension(400,200));
		topPanel.add(observablesTable.getEnclosingScrollPane(), gbc);

		// -------------------------------------------- bottom panel
		gridy = 0;
		gbc = new java.awt.GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		speciesTable.setPreferredScrollableViewportSize(new Dimension(400,200));
		bottomPanel.add(speciesTable.getEnclosingScrollPane(), gbc);
		
		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		bottomPanel.add(new JLabel("Search "), gbc);

		textFieldSearch = new JTextField(70);
		textFieldSearch.addActionListener(eventHandlerS);
		textFieldSearch.getDocument().addDocumentListener(eventHandlerS);
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 0, 4, 4);
		bottomPanel.add(textFieldSearch, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.gridwidth = 8;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		bottomPanel.add(containerOfScrollPanel, gbc);
		
		// rendering the small shapes of the flattened species in the Depiction column of this viewer table)
		// TODO: this renderer is almost identical with the one in BioModelEditorModelPanel (which paints the small shapes 
		// of a species context in the Depiction column of the species table)
		DefaultScrollTableCellRenderer rbmSpeciesShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			SpeciesPatternSmallShape spss = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == speciesTableModel) {
						selectedObject = speciesTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof GeneratedSpeciesTableRow) {
							
							SpeciesContext sc = ((GeneratedSpeciesTableRow)selectedObject).getSpecies();
							SpeciesPattern sp = sc.getSpeciesPattern();		// sp cannot be null
							Graphics panelContext = table.getGraphics();
							spss = new SpeciesPatternSmallShape(4, 2, sp, panelContext, sc, isSelected, issueManager);
						}
					} else {
						spss = null;
					}
				}
				setText("");
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(spss != null) {
					spss.paintSelf(g);
				}
			}
		};
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColDepiction).setCellRenderer(rbmSpeciesShapeDepictionCellRenderer);
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColDepiction).setPreferredWidth(400);
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColDepiction).setMinWidth(400);
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel.iColDefinition).setPreferredWidth(30);
		speciesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		DefaultScrollTableCellRenderer rbmObservableShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			List<SpeciesPatternSmallShape> spssList = new ArrayList<SpeciesPatternSmallShape>();
			SpeciesPatternSmallShape spss = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == observablesTableModel) {
						selectedObject = observablesTableModel.getValueAt(row);
					}
					if (selectedObject != null && selectedObject instanceof ObservablesGroupTableRow) {
							
						ObservablesGroupTableRow ogtr = ((ObservablesGroupTableRow)selectedObject);
						String obsName = ogtr.getObservableGroupObject().getObservableGroupName();
						RbmObservable observable = ogtr.getObservable(obsName);
						Graphics panelContext = table.getGraphics();
						int xPos = 4;
						spssList.clear();
						for(int i = 0; i<observable.getSpeciesPatternList().size(); i++) {
							SpeciesPattern sp = observable.getSpeciesPatternList().get(i);
							spss = new SpeciesPatternSmallShape(xPos, 2, sp, panelContext, observable, isSelected, issueManager);
							xPos += spss.getWidth() + 6;
							spssList.add(spss);
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
		observablesTable.getColumnModel().getColumn(ObservablesGroupTableModel.iColDepiction).setCellRenderer(rbmObservableShapeDepictionCellRenderer);
		observablesTable.getColumnModel().getColumn(ObservablesGroupTableModel.iColDepiction).setPreferredWidth(150);
		observablesTable.getColumnModel().getColumn(ObservablesGroupTableModel.iColDepiction).setMinWidth(150);
		observablesTable.getColumnModel().getColumn(ObservablesGroupTableModel.iColDefinition).setPreferredWidth(80);
		observablesTable.getColumnModel().getColumn(ObservablesGroupTableModel.iColExpression).setPreferredWidth(100);
		observablesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public void updateShape(int selectedRow) {
	GeneratedSpeciesTableRow speciesTableRow = speciesTableModel.getValueAt(selectedRow);
	String inputString = speciesTableRow.getExpression();
//	System.out.println(selectedRows[0] + ": " + inputString);
	
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
		System.out.println("something is wrong, we just do nothing rather than crash");
		return;
	}
	try {
		
	String strStructure = null;
	if(inputString.contains(RbmUtils.SiteStruct)) {
		// we are in the mode where we emulate compartments by adding the compartment name as a fake site
		Pair<List<String>, String> p = RbmUtils.extractCompartment(inputString);
		strStructure = p.one.get(0);	// we'll just assume there's only one, may want to throw exception if more
		inputString = p.two;
	} else {
		// should be the normal @comp:expression format - if it's not it will return null
		strStructure = RbmUtils.parseCompartment(inputString, tempModel);
	}
	Structure structure;
	if(strStructure != null) {
		if(tempModel.getStructure(strStructure) == null) {
			if(owner.getSimulationContext().getModel().getStructure(strStructure).getTypeName().equals(Structure.TYPE_NAME_MEMBRANE)) {
				tempModel.addMembrane(strStructure);
			} else {
				tempModel.addFeature(strStructure);
			}
		}
		structure = tempModel.getStructure(strStructure);
	} else {
		structure = tempModel.getStructure(0);
	}
	SpeciesPattern sp = (SpeciesPattern)RbmUtils.parseSpeciesPattern(inputString, tempModel);
	sp.resolveBonds();
	SpeciesContext sc = new SpeciesContext(new Species("a",""), structure, sp);
	spls = new SpeciesPatternLargeShape(20, 20, -1, sp, shapePanel, sc, issueManager);
	
	} catch (ParseException | PropertyVetoException | ModelException e1) {
		e1.printStackTrace();
		spls = new SpeciesPatternLargeShape(20, 20, -1, shapePanel, true, issueManager);	// error (red circle)
		shapePanel.repaint();
	}
	
	int xOffset = spls.getRightEnd() + 45;
	Dimension preferredSize = new Dimension(xOffset+90, 50);
	shapePanel.setPreferredSize(preferredSize);
	
	shapePanel.repaint();
}

private JButton getZoomLargerButton() {
	if (zoomLargerButton == null) {
		zoomLargerButton = new JButton();		// "+"
		ZoomShapeIcon.setZoomMod(zoomLargerButton, ZoomShapeIcon.Sign.plus);
		zoomLargerButton.addActionListener(eventHandlerS);
	}
	return zoomLargerButton;
}
private JButton getZoomSmallerButton() {
	if (zoomSmallerButton == null) {
		zoomSmallerButton = new JButton();		// -
		ZoomShapeIcon.setZoomMod(zoomSmallerButton, ZoomShapeIcon.Sign.minus);
		zoomSmallerButton.addActionListener(eventHandlerS);
	}
	return zoomSmallerButton;
}

private void searchTable() {
	String searchText = textFieldSearch.getText();
	speciesTableModel.setSearchText(searchText);
}

public void setData(BNGSpecies[] speciess, ObservableGroup[] observabless) {
	if (this.speciess == speciess && this.observabless == observabless) {
		return;
	}
	this.speciess = speciess;
	this.observabless = observabless;
	speciesTableModel.setData(owner.getSimulationContext().getModel(), speciess);
	observablesTableModel.setData(owner.getSimulationContext().getModel(), observabless);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {

}

public GeneratedSpeciesTableModel getGeneratedSpeciesTableModel(){
	return speciesTableModel;
}
public ObservablesGroupTableModel getObservablesGroupTableModel(){
	return observablesTableModel;
}

}
