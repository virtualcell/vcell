/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

import org.vcell.model.bngl.ASTSpeciesPattern;
import org.vcell.model.bngl.BNGLParser;
import org.vcell.model.bngl.ParseException;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.RbmUtils.BnglObjectConstructionVisitor;
import org.vcell.model.rbm.gui.GeneratedSpeciesTableRow;
import org.vcell.solver.nfsim.NFSimMolecularConfigurations;
import org.vcell.solver.nfsim.NFSimSolver;
import org.vcell.util.Pair;
import org.vcell.util.document.LocalVCDataIdentifier;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.client.DataViewerManager;
import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.graph.ReactionCartoon.RuleAnalysisChanged;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.graph.gui.LargeShapePanel;
import cbit.vcell.graph.gui.ZoomShapeIcon;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.GroupingCriteria;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;

@SuppressWarnings("serial")
public class OutputSpeciesResultsPanel extends DocumentEditorSubPanel  {
	private BNGSpecies[] speciess = null;
	private EventHandler eventHandler = new EventHandler();
	private OutputSpeciesResultsTableModel tableModel = null; 
	private EditorScrollTable table = null;
	private JTextField textFieldSearch = null;
	private JLabel totalSpeciesLabel = new JLabel("");
	
	private JButton zoomLargerButton = null;
	private JButton zoomSmallerButton = null;

	private final ODEDataViewer owner;
	LargeShapePanel shapePanel = null;
	private SpeciesPatternLargeShape spls;

	// ===================================================================================================
	private class OutputSpeciesResultsTableModel  extends VCellSortTableModel<GeneratedSpeciesTableRow> 
	implements  PropertyChangeListener, AutoCompleteTableModel {

		public final int colCount = 4;
		public static final int iColCount = 0;
		public static final int iColStructure = 1;
		public static final int iColDepiction = 2;
		public static final int iColDefinition = 3;
		
		// filtering variables 
		protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
		protected String searchText = null;
		private Model model;
		private ArrayList<GeneratedSpeciesTableRow> allGeneratedSpeciesList;

		public OutputSpeciesResultsTableModel() {
			super(table, new String[] {"Count", "Structure", "Depiction", "BioNetGen Definition"});
			setMaxRowsPerPage(1000);
		}
		
		public Class<?> getColumnClass(int iCol) {
			switch (iCol) {		
				case iColCount:
					return Integer.class;
				case iColStructure:
					return String.class;
				case iColDepiction:
					return Object.class;
				case iColDefinition:
					return String.class;
			}
			return Object.class;
		}

		public ArrayList<GeneratedSpeciesTableRow> getTableRows() {
			return allGeneratedSpeciesList;
		}
		
		public void setSearchText(String newValue) {
			if (searchText == newValue) {
				return;
			}
			searchText = newValue;
			refreshData();
		}
		
		@Override
		public void setValueAt(Object valueNew, int iRow, int iCol) {
			return;
		}
		@Override
		public Object getValueAt(int iRow, int iCol) {
			GeneratedSpeciesTableRow speciesTableRow = getValueAt(iRow);
			switch(iCol) {
			case iColCount:
				return speciesTableRow.count;
			case iColStructure:
				return speciesTableRow.structure;
			case iColDepiction:
			case iColDefinition:
				return speciesTableRow.expression;
			default:
				return null;
			}
		}
		public boolean isCellEditable(int iRow, int iCol) {
			switch(iCol) {
			case iColDefinition:
				return true;	// being editable means that you can select a row and copy its contents to the clipboard
			default:
				return false;
			}
		}
		@Override
		public String checkInputValue(String inputValue, int row, int column) {
			return null;
		}
		@Override
		public SymbolTable getSymbolTable(int row, int column) {
			return null;
		}
		@Override
		public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
			return null;
		}
		@Override
		public Set<String> getAutoCompletionWords(int row, int column) {
			return null;
		}
		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
		}
		@Override
		protected Comparator<GeneratedSpeciesTableRow> getComparator(int col, boolean ascending) {
			final int scale = ascending ? 1 : -1;
			return new Comparator<GeneratedSpeciesTableRow>() {
			    public int compare(GeneratedSpeciesTableRow o1, GeneratedSpeciesTableRow o2) {
					switch (col) {
					case iColCount:
						return scale * o1.count.compareTo(o2.count);
					case iColStructure:
						return scale * o1.structure.compareToIgnoreCase(o2.structure);
					case iColDefinition:
						return scale * o1.expression.compareToIgnoreCase(o2.expression);
					case iColDepiction:
						if(o1.species != null && o1.species.hasSpeciesPattern() && o2.species != null && o2.species.hasSpeciesPattern()) {
							Integer i1 = o1.species.getSpeciesPattern().getMolecularTypePatterns().size();
							Integer i2 = o2.species.getSpeciesPattern().getMolecularTypePatterns().size();
							return scale * i1.compareTo(i2);
						}
						return 0;
					default:
						return 0;
					}
			    }
			};
		}
		public String getLatestMatchedFilename(String path, final String extension) {
			File dir = new File(path);    
			File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String nameFilter) {
			return nameFilter.endsWith(extension);
			}
			});
			File lastModified = null;
			for(File file : files) {
				if(lastModified == null || file.lastModified() > lastModified.lastModified()) {
					lastModified = file;
				}
			}
			return lastModified.getAbsolutePath();
		}

		public void refreshData() {
			allGeneratedSpeciesList = new ArrayList<>();
			ODESolverResultSet srs = owner.getOdeSolverResultSet();
			VCDataIdentifier vcdi = owner.getVcDataIdentifier();
			Simulation sim = owner.getSimulation();
			SolverTaskDescription std = sim.getSolverTaskDescription();
			boolean isNFSim = std.getSolverDescription().isNFSimSolver();
			if(isNFSim && srs instanceof ODESimData) {
				NFSimMolecularConfigurations nfsmc = owner.getNFSimMolecularConfigurations();
				if(nfsmc == null) {
					System.out.println("NFSimMolecularConfigurations is null");
					return;
				}
				Map<String, Integer> molecularConfigurations = nfsmc.getMolecularConfigurations();
				for(String pattern : molecularConfigurations.keySet()) {
					Integer number = molecularConfigurations.get(pattern);
					GeneratedSpeciesTableRow newRow = createTableRow(pattern, number);
					allGeneratedSpeciesList.add(newRow);
				}
			} else {
				System.out.println("Conditions not met");
				setData(allGeneratedSpeciesList);
				return;
			}
			System.out.println("Simulation: " + sim.getName());
			
			// apply text search function for particular columns
			List<GeneratedSpeciesTableRow> speciesObjectList = new ArrayList<>();
			if (searchText == null || searchText.length() == 0) {
				speciesObjectList.addAll(allGeneratedSpeciesList);
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();
				for (GeneratedSpeciesTableRow rs : allGeneratedSpeciesList) {
					boolean added = false;
					if (rs.expression.toLowerCase().contains(lowerCaseSearchText) ) {
						speciesObjectList.add(rs);
						added = true;
					}
					if(!added && rs.structure.toLowerCase().contains(lowerCaseSearchText)) {
						speciesObjectList.add(rs);
						added = true;
					}
				}
			}

			
			
			
			setData(allGeneratedSpeciesList);
			GuiUtils.flexResizeTableColumns(ownerTable);
		}
	}		// end OutputSpeciesResultsTableModel class
	
	// ======================================================================================
	private class GeneratedSpeciesTableRow {
		private Integer count = 0;		// used only in relation to an observable
		private String expression = "?";
		private String structure = "?";
		private SpeciesContext species = null;
		
		// never call this directly, the object is not fully constructed
		// always call createTableRow() instead
		protected GeneratedSpeciesTableRow(Integer count) {
			if(count != null) {
				this.count = count;
			}
		}
		
		private void deriveSpecies(String originalExpression) {
			String strMolecularPattern;
			String newExpression = "";
			StringTokenizer moleculeTokenizer = new StringTokenizer(originalExpression,".)");
			while(moleculeTokenizer.hasMoreTokens()) {
				strMolecularPattern = moleculeTokenizer.nextToken();
				String moleculeName = strMolecularPattern.substring(0, strMolecularPattern.indexOf("("));
				newExpression += moleculeName + "(";
				String betweenParanthesis = strMolecularPattern.substring(strMolecularPattern.indexOf("(")+1, strMolecularPattern.length());
				StringTokenizer componentTokenizer = new StringTokenizer(betweenParanthesis);
				// extract structure from the first token  (ex: AAA~cyt)
				String struct = componentTokenizer.nextToken(",");
				structure = struct.substring(struct.indexOf("~")+1);
				// ignore the second token (ex: AAB~0) 
				componentTokenizer.nextToken(",");
				// build what's left to newExpression unchanged
				String remaining = componentTokenizer.nextToken(")");
				if(remaining.startsWith(",")) {
					remaining = remaining.substring(1, remaining.length());
				}
				newExpression += remaining + ").";
			}
			if(newExpression.endsWith(".")) {
				expression = newExpression.substring(0, newExpression.length()-1);
			}
			
			Model tempModel = null;
			try {
			tempModel = new Model("MyTempModel");
			tempModel.addFeature(structure);

			Structure structure = tempModel.getStructure(0);
			BNGLParser parser = new BNGLParser(new StringReader(expression));
			ASTSpeciesPattern astSpeciesPattern = parser.SpeciesPattern();
			BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(tempModel, null, false);
			SpeciesPattern sp = (SpeciesPattern) astSpeciesPattern.jjtAccept(constructionVisitor, null);
			sp.resolveBonds();
			species = new SpeciesContext(new Species("a",""), structure, sp);
			} catch (ParseException | ModelException | PropertyVetoException e1) {
				e1.printStackTrace();
				species = null;
			}
		}

	}		// end GeneratedSpeciesTableRow class
	// ======================================================================================
	
	private class EventHandler implements ActionListener, DocumentListener, ListSelectionListener, TableModelListener {
		@Override
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
		@Override
		public void valueChanged(ListSelectionEvent e) {
			System.out.println("OutputSpeciesResultsPanel value changed");
			int[] selectedRows = table.getSelectedRows();
			if(selectedRows.length == 1 && !e.getValueIsAdjusting()) {
				updateShape(selectedRows[0]);
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
			totalSpeciesLabel.setText("Species: " + table.getModel().getRowCount());
		}
	}
	
public OutputSpeciesResultsPanel(ODEDataViewer owner) {
	super();
	this.owner = owner;
	initialize();
}

private GeneratedSpeciesTableRow createTableRow(String expression, Integer count) {
	GeneratedSpeciesTableRow row = new GeneratedSpeciesTableRow(count);
	row.deriveSpecies(expression);
	return row;
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

		// ------------------------------------------------------------------------
		
		table = new EditorScrollTable();
		tableModel = new OutputSpeciesResultsTableModel();
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(eventHandler);
		table.getModel().addTableModelListener(eventHandler);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		
		int gridy = 0;
		gbc = new java.awt.GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		table.setPreferredScrollableViewportSize(new Dimension(400,200));
		add(table.getEnclosingScrollPane(), gbc);
		
//		gbc = new java.awt.GridBagConstraints();
//		gbc.gridx = 9;
//		gbc.gridy = gridy;
		
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

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		add(totalSpeciesLabel, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
		gbc.gridwidth = 8;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		add(containerOfScrollPanel, gbc);
		
		// rendering the small shapes of the flattened species in the Depiction column of this viewer table)
		DefaultScrollTableCellRenderer rbmSpeciesShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
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
						if(selectedObject instanceof GeneratedSpeciesTableRow) {
							SpeciesContext sc = ((GeneratedSpeciesTableRow)selectedObject).species;
							if(sc == null || sc.getSpeciesPattern() == null) {
								spss = null;
							} else {
								SpeciesPattern sp = sc.getSpeciesPattern();
								Graphics panelContext = table.getGraphics();
								spss = new SpeciesPatternSmallShape(4, 2, sp, panelContext, sc, isSelected, issueManager);
							}
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

		table.getColumnModel().getColumn(OutputSpeciesResultsTableModel.iColDepiction).setCellRenderer(rbmSpeciesShapeDepictionCellRenderer);
		table.getColumnModel().getColumn(OutputSpeciesResultsTableModel.iColDepiction).setPreferredWidth(400);
		table.getColumnModel().getColumn(OutputSpeciesResultsTableModel.iColDepiction).setMinWidth(400);
		
		table.getColumnModel().getColumn(OutputSpeciesResultsTableModel.iColDefinition).setPreferredWidth(30);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public void updateShape(int selectedRow) {
	
	GeneratedSpeciesTableRow speciesTableRow = tableModel.getValueAt(selectedRow);
	SpeciesContext sc = speciesTableRow.species;
	if(sc == null || sc.getSpeciesPattern() == null) {
		spls = new SpeciesPatternLargeShape(20, 20, -1, shapePanel, true, issueManager);	// error (red circle)
	} else {
		spls = new SpeciesPatternLargeShape(20, 20, -1, sc.getSpeciesPattern(), shapePanel, sc, issueManager);
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
		zoomLargerButton.addActionListener(eventHandler);
	}
	return zoomLargerButton;
}
private JButton getZoomSmallerButton() {
	if (zoomSmallerButton == null) {
		zoomSmallerButton = new JButton();		// -
		ZoomShapeIcon.setZoomMod(zoomSmallerButton, ZoomShapeIcon.Sign.minus);
		zoomSmallerButton.addActionListener(eventHandler);
	}
	return zoomSmallerButton;
}

private void searchTable() {
	String searchText = textFieldSearch.getText();
	tableModel.setSearchText(searchText);
}

//public void setSpecies(BNGSpecies[] newValue) {
//	if (speciess == newValue) {
//		return;
//	}
//	speciess = newValue;
//	tableModel.setData(owner.getSimulationContext().getModel(), newValue);
//}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {

}

public OutputSpeciesResultsTableModel getTableModel(){
	return tableModel;
}

public void refreshData() {
	tableModel.refreshData();
}

}
