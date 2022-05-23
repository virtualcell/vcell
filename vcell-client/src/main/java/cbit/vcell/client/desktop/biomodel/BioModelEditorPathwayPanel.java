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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Complex;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PathwaySelectionExpander;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;

@SuppressWarnings("serial")
public class BioModelEditorPathwayPanel extends DocumentEditorSubPanel {
	
	private PathwayData pathwayData = null; 
	private JSortTable table;
	private PathwayTableModel tableModel = null;
	private JButton importButton = null;
	private JTextField textFieldSearch = null;
	private JPopupMenu addPopupMenu;
	private JMenuItem addSelectedOnlyMenuItem, addWithInteractionsMenuItem, addWithComponentsMenueItem, addWithComplexesMenuItem;
	private JLabel pathwayTitleLabel;
	
	private void searchTable() {
		String searchText = textFieldSearch.getText();
		tableModel.setSearchText(searchText);
	}
	
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	
	private class EventHandler implements ActionListener, ListSelectionListener, DocumentListener  {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == importButton) {
				getAddPopupMenu().show(importButton, 0, importButton.getHeight());
//				importPathway();
			} else if (e.getSource() == addSelectedOnlyMenuItem) {
				importPathway(false, true, false);
			} else if (e.getSource() == addWithInteractionsMenuItem) {
				importPathway(false, true, true);
			} else if (e.getSource() == addWithComponentsMenueItem) {
				importPathway(false, true, true);
			} else if (e.getSource() == addWithComplexesMenuItem) {
				importPathway(true, true, true);
			}
		}
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				importButton.setEnabled(table.getSelectedRowCount() > 0);
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
	}
	
	public BioModelEditorPathwayPanel() {
		super();
		initialize();
	}
	
	public void importPathway(boolean addComplexes, boolean addComponents, boolean addInteractions) {
		ArrayList<BioPaxObject> selectedBioPaxObjects = new ArrayList<BioPaxObject>();
		int[] rows = table.getSelectedRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		for (int row : rows) {
			BioPaxObject bioPaxObject = tableModel.getValueAt(row);
			selectedBioPaxObjects.add(bioPaxObject);
		}
		PathwaySelectionExpander selectionExpander = new PathwaySelectionExpander();
		PathwayModel rawPathwayModel = pathwayData.getPathwayModel();
		if(addComplexes) { selectionExpander.forPhysicalEntitiesAddComplexes(rawPathwayModel, selectedBioPaxObjects); }
		if(addComponents) { selectionExpander.forComplexesAddComponents(rawPathwayModel, selectedBioPaxObjects); }
		if(addInteractions) { selectionExpander.forPhysicalEntitiesAddInteractions(rawPathwayModel, selectedBioPaxObjects); }
		selectionExpander.forInteractionsAddControls(rawPathwayModel, selectedBioPaxObjects);
		selectionExpander.forInteractionsAddParticipants(rawPathwayModel, selectedBioPaxObjects);
		PathwayModel selectedPathwayModel = new PathwayModel();
		HashSet<BioPaxObject> objectsToDelete = new HashSet<BioPaxObject>();
		for (BioPaxObject candidateObject : selectedBioPaxObjects){
			// is the object in the current pathwayModel already?
			BioPaxObject keeperObject = bioModel.getPathwayModel().find(candidateObject);
			if(keeperObject == null) {
				// not found in the current pathwayModel, add it
				selectedPathwayModel.add(candidateObject);
			} else {
				// make a list with the objects we don't bring in because of duplication
				objectsToDelete.add(candidateObject);
			}
		}
		// we replace references to those objects within selectedPathwayModel with the real thing
		for (BioPaxObject bpObject : selectedPathwayModel.getBiopaxObjects()){
			if(bpObject == null) {
				System.out.println("PathwayModel: null BioPaxObject.");
				continue;
			}
			for(BioPaxObject objectToDelete : objectsToDelete) {
				BioPaxObject keeperObject = bioModel.getPathwayModel().find(objectToDelete);
				// for now we only implemented this for InteractionParticipant entities of Conversions
				bpObject.replace(keeperObject);
			}
		}
		
		// we bring proteins, small molecules, etc that are components of a Complex
		ArrayList<PhysicalEntity> addList = new ArrayList<>();
		for(BioPaxObject bpo : selectedPathwayModel.getBiopaxObjects()) {
			if(bpo instanceof Complex) {
				Complex complex = (Complex)bpo;
				addComplexComponents(complex, addList, 0);
			}
		}
		for(PhysicalEntity pe : addList) {
			if(selectedPathwayModel.find(pe) == null) {
				selectedPathwayModel.add(pe);
			}
		}
		bioModel.getPathwayModel().merge(selectedPathwayModel);
		
		// jump the view to pathway diagram panel
		if (selectionManager != null) {
			selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram),selectedPathwayModel.getBiopaxObjects().toArray());
		}
	}
	
	private static final int DEPTH_LIMIT = 12;
	private void addComplexComponents(Complex complex, ArrayList<PhysicalEntity> addList, int depth) {
		depth++;
		if(depth > DEPTH_LIMIT) {
			throw new RuntimeException("Recursion limit exceeded for Complex " + complex.getDisplayName());
		}
		for(PhysicalEntity pe : complex.getComponents()) {
			if(pe instanceof Complex) {		// call recursively if the complex contains other complex(es)
				addComplexComponents((Complex)pe, addList, depth);
			} else {		// if it's not a complex we add it to the list unless it's there already
				if(!addList.contains(pe)) {
					addList.add(pe);
				}
			}
		}
	}

	private void initialize() {
		table = new JSortTable();
		tableModel = new PathwayTableModel(table);
		table.setModel(tableModel);
		table.disableUneditableForeground();
		importButton = new JButton("Import", new DownArrowIcon());
		importButton.setHorizontalTextPosition(SwingConstants.LEFT);
		importButton.setEnabled(false);
		importButton.addActionListener(eventHandler);
		
		table.getSelectionModel().addListSelectionListener(eventHandler);
		
		int gridy = 0;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		pathwayTitleLabel = new JLabel();
		pathwayTitleLabel.setFont(pathwayTitleLabel.getFont().deriveFont(Font.BOLD));
		pathwayTitleLabel.setHorizontalAlignment(JLabel.CENTER);
		add(pathwayTitleLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);
		
		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);

		textFieldSearch = new JTextField(15);
		textFieldSearch.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(textFieldSearch, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,20,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		importButton.setPreferredSize(importButton.getPreferredSize());
		add(importButton, gbc);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		PathwayData pathwayData = null;
		if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof PathwayData) {
			pathwayData = (PathwayData) selectedObjects[0];
			setPathwayData(pathwayData);
		}
	}


	private void setPathwayData(PathwayData pathwayData) {
		if (this.pathwayData == pathwayData) {
			return;
		}
		this.pathwayData = pathwayData;
		refreshInterface();
	}


	private void refreshInterface() {
		if (pathwayData == null) {
			return;
		}
		String pathwayName = pathwayData.getTopLevelPathwayName();
		pathwayTitleLabel.setText(pathwayName);
		tableModel.setPathwayModel(pathwayData.getPathwayModel());
		
		GuiUtils.flexResizeTableColumns(table);
	}

	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
		tableModel.setBioModel(bioModel);
	}
	
	private JPopupMenu getAddPopupMenu() {
		if (addPopupMenu == null) {
			addPopupMenu = new JPopupMenu();
			addSelectedOnlyMenuItem = new JMenuItem("Selected Only");
			addSelectedOnlyMenuItem.addActionListener(eventHandler);			
			addWithInteractionsMenuItem = new JMenuItem("Plus Interactions");
			addWithInteractionsMenuItem.addActionListener(eventHandler);
//			addWithComponentsMenueItem = new JMenuItem("Plus Components");		// we do it by default
//			addWithComponentsMenueItem.addActionListener(eventHandler);
//			addWithComplexesMenuItem = new JMenuItem("Plus Complexes");			// works but dubious need for it
//			addWithComplexesMenuItem.addActionListener(eventHandler);
			addPopupMenu.add(addSelectedOnlyMenuItem);
			addPopupMenu.add(addWithInteractionsMenuItem);
//			addPopupMenu.add(addWithComponentsMenueItem);
//			addPopupMenu.add(addWithComplexesMenuItem);	
		}
		return addPopupMenu;
	}

}
