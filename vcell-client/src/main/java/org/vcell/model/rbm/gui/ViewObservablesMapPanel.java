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
import cbit.vcell.graph.GraphConstants;
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
	private GeneratedSpeciesTableModel2 speciesTableModel = null; 
	private ObservablesGroupTableModel observablesTableModel = null; 
	private EditorScrollTable speciesTable = null;
	private EditorScrollTable observablesTable = null;
	private JTextField textFieldSearchSpecies = null;
	private JTextField textFieldSearchObservables = null;
	private JLabel totalSpeciesLabel = new JLabel("");
	private JLabel totalObservablesLabel = new JLabel("");
	
	private JButton zoomLargerButtonSpecies = null;
	private JButton zoomSmallerButtonSpecies = null;
	private JButton zoomLargerButtonObservable = null;
	private JButton zoomSmallerButtonObservable = null;

	private final NetworkConstraintsPanel owner;
	LargeShapePanel shapePanelSpecies = null;
	LargeShapePanel shapePanelObservable = null;
	private SpeciesPatternLargeShape spls;				// for the generated species shapes
	List<SpeciesPatternLargeShape> spsList = new ArrayList<SpeciesPatternLargeShape>();		// for the observable shapes

	private class EventHandler implements ActionListener, DocumentListener, ListSelectionListener, TableModelListener {
		
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getZoomLargerButtonSpecies()) {
				boolean ret = shapePanelSpecies.zoomLarger();
				getZoomLargerButtonSpecies().setEnabled(ret);
				getZoomSmallerButtonSpecies().setEnabled(true);
				int[] selectedRows = speciesTable.getSelectedRows();
				if(selectedRows.length == 1) {
					updateShapeSpecies(selectedRows[0]);
				}
			} else if (e.getSource() == getZoomSmallerButtonSpecies()) {
				boolean ret = shapePanelSpecies.zoomSmaller();
				getZoomLargerButtonSpecies().setEnabled(true);
				getZoomSmallerButtonSpecies().setEnabled(ret);
				int[] selectedRows = speciesTable.getSelectedRows();
				if(selectedRows.length == 1) {
					updateShapeSpecies(selectedRows[0]);
				}
			} else if (e.getSource() == getZoomLargerButtonObservable()) {
				boolean ret = shapePanelObservable.zoomLarger();
				getZoomLargerButtonObservable().setEnabled(ret);
				getZoomSmallerButtonObservable().setEnabled(true);
				int[] selectedRows = observablesTable.getSelectedRows();
				if(selectedRows.length == 1) {
					updateShapeObservable(selectedRows[0]);
				}
			} else if (e.getSource() == getZoomSmallerButtonObservable()) {
				boolean ret = shapePanelObservable.zoomSmaller();
				getZoomLargerButtonObservable().setEnabled(true);
				getZoomSmallerButtonObservable().setEnabled(ret);
				int[] selectedRows = observablesTable.getSelectedRows();
				if(selectedRows.length == 1) {
					updateShapeObservable(selectedRows[0]);
				}
			}

		}
		public void insertUpdate(DocumentEvent e) {
			if(this == eventHandlerO) {
				searchTableObservables();
			} else {
				searchTableSpecies();
			}
		}
		public void removeUpdate(DocumentEvent e) {
			if(this == eventHandlerO) {
				searchTableObservables();
			} else {
				searchTableSpecies();
			}
		}
		public void changedUpdate(DocumentEvent e) {
			if(this == eventHandlerO) {
				searchTableObservables();
			} else {
				searchTableSpecies();
			}
		}
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(this == eventHandlerO) {
				int[] selectedRows = observablesTable.getSelectedRows();
				if(selectedRows.length == 1 && !e.getValueIsAdjusting()) {
					speciesTableModel.setObservableFilter(observablesTableModel.getValueAt(selectedRows[0]));
					updateShapeObservable(selectedRows[0]);
				} else if(selectedRows.length != 1 && !e.getValueIsAdjusting()) {
					System.out.println("Observable, unselected or multiple");
					speciesTableModel.setObservableFilter(null);
					updateShapeObservable(-1);		// no observable
				}
			} else if(this == eventHandlerS) {
				int[] selectedRows = speciesTable.getSelectedRows();
				if(selectedRows.length == 1 && !e.getValueIsAdjusting()) {
//					System.out.println("Species, row " + selectedRows[0]);
					updateShapeSpecies(selectedRows[0]);
				}
			} else {
				System.out.println("Neither table");
			}
		}
		@Override
		public void tableChanged(TableModelEvent e) {
			if(this == eventHandlerO) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if(observablesTable.getModel().getRowCount() == 0) {
							System.out.println("table is empty");
						} else {
							observablesTable.setRowSelectionInterval(0,0);
						}
						totalObservablesLabel.setText("Observables: " + observablesTable.getModel().getRowCount());
					}
				});
			} else if(this == eventHandlerS) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if(speciesTable.getModel().getRowCount() == 0) {
							System.out.println("table is empty");
							spls = null;
							shapePanelSpecies.repaint();
						} else {
							speciesTable.setRowSelectionInterval(0,0);
						}
//						System.out.println("table changed, " + speciesTable.getModel().getRowCount() + " species");
						totalSpeciesLabel.setText("Species: " + speciesTable.getModel().getRowCount());
					}
				});
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
		splitPaneHorizontal.setDividerSize(10);
		splitPaneHorizontal.setOneTouchExpandable(true);
		splitPaneHorizontal.setDividerLocation(260);
		splitPaneHorizontal.setResizeWeight(0.5);
		splitPaneHorizontal.setTopComponent(topPanel);
		splitPaneHorizontal.setBottomComponent(bottomPanel);
		add(splitPaneHorizontal, BorderLayout.CENTER);

		// ---------------------------------------- species shape panel
		shapePanelSpecies = new LargeShapePanel() {
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
		shapePanelSpecies.setLayout(new GridBagLayout());
		shapePanelSpecies.setBackground(Color.white);
		shapePanelSpecies.setEditable(true);		// not really editable but we don't want the brown contours here
		shapePanelSpecies.setShowMoleculeColor(true);
		shapePanelSpecies.setShowNonTrivialOnly(true);

		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
		JScrollPane scrollPaneSpecies = new JScrollPane(shapePanelSpecies);
		scrollPaneSpecies.setBorder(loweredBevelBorder);
		scrollPaneSpecies.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneSpecies.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		JPanel optionsPanelSpecies = new JPanel();
		optionsPanelSpecies.setLayout(new GridBagLayout());
		
		getZoomSmallerButtonSpecies().setEnabled(true);
		getZoomLargerButtonSpecies().setEnabled(true);
		shapePanelSpecies.zoomSmaller();
		shapePanelSpecies.zoomSmaller();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,0,0,10);
		gbc.anchor = GridBagConstraints.WEST;
		optionsPanelSpecies.add(getZoomLargerButtonSpecies(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(2,0,4,10);
		gbc.anchor = GridBagConstraints.WEST;
		optionsPanelSpecies.add(getZoomSmallerButtonSpecies(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		optionsPanelSpecies.add(new JLabel(""), gbc);

		JPanel containerOfScrollPanelSpecies = new JPanel();
		containerOfScrollPanelSpecies.setLayout(new BorderLayout());
		containerOfScrollPanelSpecies.add(optionsPanelSpecies, BorderLayout.WEST);
		containerOfScrollPanelSpecies.add(scrollPaneSpecies, BorderLayout.CENTER);
		
		Dimension dimS = new Dimension(500, 125);
		containerOfScrollPanelSpecies.setPreferredSize(dimS);	// dimension of shape panel
		containerOfScrollPanelSpecies.setMinimumSize(dimS);
		containerOfScrollPanelSpecies.setMaximumSize(dimS);

		shapePanelObservable = new LargeShapePanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(SpeciesPatternLargeShape sps : spsList) {
					if(sps == null) {
						continue;
					}
					sps.paintSelf(g);
				}
			}
			@Override
			public DisplayMode getDisplayMode() {
				return DisplayMode.other;
			}
			@Override
			public RuleAnalysisChanged hasStateChanged(String reactionRuleName,	MolecularComponentPattern molecularComponentPattern) {
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

		//shapePanelObservable.setLayout(null);
		shapePanelObservable.setLayout(new GridBagLayout());
		shapePanelObservable.setBackground(Color.white);
		shapePanelObservable.setEditable(true);
		shapePanelObservable.setShowMoleculeColor(true);
		shapePanelObservable.setShowNonTrivialOnly(true);
		
		JScrollPane scrollPaneObservable = new JScrollPane(shapePanelObservable);
		scrollPaneObservable.setBorder(loweredBevelBorder);
		scrollPaneObservable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneObservable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		JPanel optionsPanelObservable = new JPanel();
		optionsPanelObservable.setLayout(new GridBagLayout());
		
		getZoomSmallerButtonObservable().setEnabled(true);
		getZoomLargerButtonObservable().setEnabled(true);
		shapePanelObservable.zoomSmaller();
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,0,0,10);
		gbc.anchor = GridBagConstraints.WEST;
		optionsPanelObservable.add(getZoomLargerButtonObservable(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(2,0,4,10);
		gbc.anchor = GridBagConstraints.WEST;
		optionsPanelObservable.add(getZoomSmallerButtonObservable(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		optionsPanelObservable.add(new JLabel(""), gbc);

		JPanel containerOfScrollPanelObservable = new JPanel();
		containerOfScrollPanelObservable.setLayout(new BorderLayout());
		containerOfScrollPanelObservable.add(optionsPanelObservable, BorderLayout.WEST);
		containerOfScrollPanelObservable.add(scrollPaneObservable, BorderLayout.CENTER);
		
		Dimension dimO = new Dimension(500, 100);
		containerOfScrollPanelObservable.setPreferredSize(dimO);	// dimension of shape panel
		containerOfScrollPanelObservable.setMinimumSize(dimO);
		containerOfScrollPanelObservable.setMaximumSize(dimO);

		// -------------- connection between tables, table models, selection models, renderers, event handlers
		speciesTable = new EditorScrollTable();
		speciesTableModel = new GeneratedSpeciesTableModel2(speciesTable, owner);
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
//		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel2.iColMultiplier).setCellRenderer(rightRenderer);	// right align
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel2.iColMultiplier).setMaxWidth(70);	// left column wide enough for title
		
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

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		topPanel.add(new JLabel("Search "), gbc);

		textFieldSearchObservables = new JTextField(70);
		textFieldSearchObservables.addActionListener(eventHandlerO);
		textFieldSearchObservables.getDocument().addDocumentListener(eventHandlerO);
		textFieldSearchObservables.putClientProperty("JTextField.variant", "search");
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 0, 4, 4);
		topPanel.add(textFieldSearchObservables, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		topPanel.add(totalObservablesLabel, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.gridwidth = 8;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		topPanel.add(containerOfScrollPanelObservable, gbc);

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

		textFieldSearchSpecies = new JTextField(70);
		textFieldSearchSpecies.addActionListener(eventHandlerS);
		textFieldSearchSpecies.getDocument().addDocumentListener(eventHandlerS);
		textFieldSearchSpecies.putClientProperty("JTextField.variant", "search");
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 0, 4, 4);
		bottomPanel.add(textFieldSearchSpecies, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		bottomPanel.add(totalSpeciesLabel, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.gridwidth = 8;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		bottomPanel.add(containerOfScrollPanelSpecies, gbc);
		
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
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel2.iColDepiction).setCellRenderer(rbmSpeciesShapeDepictionCellRenderer);
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel2.iColDepiction).setPreferredWidth(400);
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel2.iColDepiction).setMinWidth(400);
		speciesTable.getColumnModel().getColumn(GeneratedSpeciesTableModel2.iColDefinition).setPreferredWidth(30);
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

public void updateShapeSpecies(int selectedRow) {
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
	spls = new SpeciesPatternLargeShape(20, 20, -1, sp, shapePanelSpecies, sc, issueManager);
	
	} catch (ParseException | PropertyVetoException | ModelException e1) {
		e1.printStackTrace();
		spls = new SpeciesPatternLargeShape(20, 20, -1, shapePanelSpecies, true, issueManager);	// error (red circle)
		shapePanelSpecies.repaint();
	}
	
	int xOffset = spls.getRightEnd() + 45;
	Dimension preferredSize = new Dimension(xOffset+90, 50);
	shapePanelSpecies.setPreferredSize(preferredSize);
	shapePanelSpecies.repaint();
}

public static final int xOffsetInitial = 25;
public static final int ReservedSpaceForNameOnYAxis = GraphConstants.ObservableDisplay_ReservedSpaceForNameOnYAxis;	// just a little empty spacing above the shape
private void updateShapeObservable(int selectedRow) {
	spsList.clear();
	if(selectedRow == -1) {
		shapePanelObservable.repaint();
		return;
	}
	ObservablesGroupTableRow observablesTableRow = observablesTableModel.getValueAt(selectedRow);
	String obsName = observablesTableRow.getObservableGroupObject().getObservableGroupName();
	RbmObservable observable = observablesTableRow.getObservable(obsName);
	
	int maxXOffset = xOffsetInitial;
	int maxYOffset = 8;
	if(observable != null && observable.getSpeciesPatternList() != null && observable.getSpeciesPatternList().size() > 0) {
		// if more than one sp per observable, since non-editable we show them all on a single row
		for(int i = 0; i<observable.getSpeciesPatternList().size(); i++) {
			SpeciesPattern sp = observable.getSpeciesPatternList().get(i);
			SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(maxXOffset, maxYOffset, 80, sp, shapePanelObservable, observable, issueManager);
			spsList.add(sps);
			int xOffset = sps.getRightEnd();
			maxXOffset = xOffset + 40;
		}
	}
	Dimension preferredSize = new Dimension(maxXOffset+200, maxYOffset);
	shapePanelObservable.setPreferredSize(preferredSize);
	shapePanelObservable.repaint();
}

private JButton getZoomLargerButtonSpecies() {
	if (zoomLargerButtonSpecies == null) {
		zoomLargerButtonSpecies = new JButton();		// "+"
		ZoomShapeIcon.setZoomMod(zoomLargerButtonSpecies, ZoomShapeIcon.Sign.plus);
		zoomLargerButtonSpecies.addActionListener(eventHandlerS);
	}
	return zoomLargerButtonSpecies;
}
private JButton getZoomSmallerButtonSpecies() {
	if (zoomSmallerButtonSpecies == null) {
		zoomSmallerButtonSpecies = new JButton();		// -
		ZoomShapeIcon.setZoomMod(zoomSmallerButtonSpecies, ZoomShapeIcon.Sign.minus);
		zoomSmallerButtonSpecies.addActionListener(eventHandlerS);
	}
	return zoomSmallerButtonSpecies;
}
private JButton getZoomLargerButtonObservable() {
	if (zoomLargerButtonObservable == null) {
		zoomLargerButtonObservable = new JButton();		// "+"
		ZoomShapeIcon.setZoomMod(zoomLargerButtonObservable, ZoomShapeIcon.Sign.plus);
		zoomLargerButtonObservable.addActionListener(eventHandlerO);
	}
	return zoomLargerButtonObservable;
}
private JButton getZoomSmallerButtonObservable() {
	if (zoomSmallerButtonObservable == null) {
		zoomSmallerButtonObservable = new JButton();		// -
		ZoomShapeIcon.setZoomMod(zoomSmallerButtonObservable, ZoomShapeIcon.Sign.minus);
		zoomSmallerButtonObservable.addActionListener(eventHandlerO);
	}
	return zoomSmallerButtonObservable;
}

private void searchTableSpecies() {
	String searchText = textFieldSearchSpecies.getText();
	speciesTableModel.setSearchText(searchText);
}
private void searchTableObservables() {
	String searchText = textFieldSearchObservables.getText();
	observablesTableModel.setSearchText(searchText);
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

public GeneratedSpeciesTableModel2 getGeneratedSpeciesTableModel(){
	return speciesTableModel;
}
public ObservablesGroupTableModel getObservablesGroupTableModel(){
	return observablesTableModel;
}

}
