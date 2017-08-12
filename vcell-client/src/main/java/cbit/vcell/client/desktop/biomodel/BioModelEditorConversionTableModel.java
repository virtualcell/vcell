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

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.vcell.model.rbm.MolecularType;
import org.vcell.pathway.BioPAXUtil;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.Control;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.Entity;
import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Stoichiometry;
import org.vcell.pathway.Transport;
import org.vcell.relationship.ConversionTableRow;
import org.vcell.relationship.RelationshipEvent;
import org.vcell.relationship.RelationshipListener;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.EditorScrollTable.DefaultScrollTableComboBoxEditor;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorConversionTableModel extends VCellSortTableModel<ConversionTableRow> 
	implements PathwayListener, RelationshipListener, PropertyChangeListener, AutoCompleteTableModel{

	public static final int colCount = 7;
	public static final int iColInteraction = 0;
	public static final int iColParticipant = 1;
	public static final int iColEntity = 2;
	public static final int iColType = 3;
	public static final int iColStoich = 4;
	public static final int iColLocation = 5;
	public static final int iColID = 6;
	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;

	private BioModel bioModel;
	private List<BioPaxObject> bioPaxObjects;
	private HashSet<BioPaxObject> convertedBPObjects;
	private ArrayList<ConversionTableRow> allPathwayObjectList;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;

	private DefaultScrollTableComboBoxEditor defaultScrollTableComboBoxEditor = null;
	public BioModelEditorConversionTableModel(EditorScrollTable table) {
		super(table, new String[] {
				"Interaction", "Type", "Entity Name", "Entity Type", 
				"Stoich.\nCoef.", "Location/Compartment", "  ID  "});
	}
	
	public Class<?> getColumnClass(int iCol) {
		switch (iCol){		
			case iColInteraction:{
				return String.class;
			}case iColParticipant:{
				return String.class;
			}case iColEntity:{
				return String.class;
			}case iColType:{
				return String.class;
			}case iColStoich:{
				return Double.class;
			}case iColID:{
				return String.class;
			}case iColLocation:{
				return String.class;
			}
		}
	return Object.class;
	}
	
	public Object getValueAt(int iRow, int iCol) {
		ConversionTableRow conversionTableRow = getValueAt(iRow);
		BioPaxObject bpObject = conversionTableRow.getBioPaxObject();
		switch(iCol) {
			case iColInteraction:{
				return conversionTableRow.interactionLabel();
			}
			case iColParticipant:{
				return conversionTableRow.participantType();
			}
			case iColEntity:{
				return getLabel(bpObject);
			}
			case iColType:{
				return getType(bpObject);
			}
			case iColStoich:{
				return conversionTableRow.stoich();
			}case iColID:{
				return conversionTableRow.id();
			}case iColLocation:{
				return conversionTableRow.location();
			}
			default:{
				return null;
			}
		}
	}
	
	public boolean isCellEditable(int iRow, int iCol) {
		ConversionTableRow conversonTableRow = getValueAt(iRow);
		boolean editable = true;
		if(conversonTableRow.participantType().equals("Conversion") 
				|| conversonTableRow.participantType().equals("")){
			editable = false;
		}
		// only allow users to edit the stoich and location
		return ((iCol == iColStoich && editable) || iCol == iColLocation || iCol == iColID);
	}
	
	public void setValueAt(Object valueNew, int iRow, int iCol) {
		if (bioModel.getModel() == null || valueNew == null) {
			return;
		}
		
		switch (iCol) {
			case iColStoich: {
				if(valueNew instanceof Double) {
					ConversionTableRow conversonTableRow = getValueAt(iRow);
					// only set stoich values for reactants and products
					conversonTableRow.setStoich((Double)valueNew);
				}
				break;
			} 
			case iColID: {
				if(valueNew instanceof String) {
					ConversionTableRow conversonTableRow = getValueAt(iRow);
					if(isValid(((String)valueNew).trim()))
						conversonTableRow.setId(BioPAXUtil.getSafetyName(((String)valueNew).trim()));
					else
						conversonTableRow.setId(BioPAXUtil.getSafetyName((changeID(((String)valueNew).trim()))));
				}
				break;
			}
			case iColLocation: {
				if(valueNew instanceof Structure) {
					ConversionTableRow conversonTableRow = getValueAt(iRow);
					conversonTableRow.setLocation(((Structure)valueNew).getName().trim());
					// update id value
					setValueAt(BioPAXUtil.getSafetyName(getLabel(conversonTableRow.getBioPaxObject()))+"_"+
							BioPAXUtil.getSafetyName(((Structure)valueNew).getName().trim()),iRow,iColID);
				}
				break;
			}
			
		}
	}
	
	private boolean isValid(String id){
		boolean valid = true;
		if(bioModel.getModel().getSpeciesContext(id) != null )
			valid = false;
		else if( bioModel.getModel().getReactionStep(id) != null)
			valid = false;
		return valid;
	}
	
	private String changeID(String oldID){
		String newID = oldID +"X";
		while(!isValid(newID)){
			newID += "X";
		}
		return newID;
	}
	
	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final int row, final int column) {
		return null;
	}
	
	public Set<String> getAutoCompletionWords(int row, int iCol) {
		if (iCol == iColLocation) {
			Set<String> words = new HashSet<String>();
			for (Structure s : bioModel.getModel().getStructures()) {
				words.add(s.getName());
			}
			return words;
		}
		return null;
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() == bioModel.getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
			updateStructureComboBox();
			refreshData();
		} 
		
	}
	
	protected void updateStructureComboBox() {
		JComboBox structureComboBoxCellEditor = (JComboBox) getStructureComboBoxEditor().getComponent();
		if (structureComboBoxCellEditor == null) {
			structureComboBoxCellEditor = new JComboBox();
		}
		Structure[] structures = bioModel.getModel().getStructures();
		DefaultComboBoxModel aModel = new DefaultComboBoxModel();
		for (Structure s : structures) {
			aModel.addElement(s);
		}

		DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer() {
			
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setHorizontalTextPosition(SwingConstants.LEFT);
				if (value instanceof Structure) {
					setText(((Structure)value).getName());
				}
				return this;
			}
		};
		structureComboBoxCellEditor.setRenderer(defaultListCellRenderer);
		structureComboBoxCellEditor.setModel(aModel);
		structureComboBoxCellEditor.setSelectedIndex(0);
	}

	protected DefaultScrollTableComboBoxEditor getStructureComboBoxEditor() {
		if (defaultScrollTableComboBoxEditor == null) {
			defaultScrollTableComboBoxEditor = ((EditorScrollTable)ownerTable).new DefaultScrollTableComboBoxEditor(new JComboBox());
		}
		return defaultScrollTableComboBoxEditor;
	}
	
	// generate the sortable table. Set up the functions for each column
	public Comparator<ConversionTableRow> getComparator(final int col, final boolean ascending) {
		return new Comparator<ConversionTableRow>() {
		    public int compare(ConversionTableRow o1, ConversionTableRow o2){
		    	if (col == iColEntity) {// only sortable on entity column
		    		int c  = getLabel(o1.getBioPaxObject()).compareToIgnoreCase(getLabel(o2.getBioPaxObject()));
		    		return ascending ? c : -c;
		    	} else 
		    		
		    	if (col == iColType) {
		    		int c  = getType(o1.getBioPaxObject()).compareToIgnoreCase(getType(o2.getBioPaxObject()));
		    		return ascending ? c : -c;
		    	} else 
		    		
		    	if (col == iColInteraction) {
		    		int c  = o1.interactionLabel().compareToIgnoreCase(o2.interactionLabel());
		    		return ascending ? c : -c;
		    	}else 
		    		
			    	if (col == iColParticipant) {
			    		int c  = o1.participantType().compareToIgnoreCase(o2.participantType());
			    		return ascending ? c : -c;
			    	}

		    	return 0;
		    }
		};
	}
	
	private String getType(BioPaxObject bpObject){
		return bpObject.getTypeLabel();
	}
	
	private String getLabel(BioPaxObject bpObject){
		if (bpObject instanceof Conversion){
			Conversion conversion =(Conversion)bpObject;
			if (conversion.getName().size()>0){
				return conversion.getName().get(0);
			}else{
				return conversion.getIDShort();
			}
		}else if (bpObject instanceof PhysicalEntity){
			PhysicalEntity physicalEntity =(PhysicalEntity)bpObject;
			if (physicalEntity.getName().size()>0){
				return physicalEntity.getName().get(0);
			}else{
				return physicalEntity.getIDShort();
			}
		}else{
			return bpObject.getIDShort();
		}
	}
	
	// filtering functions
	public void setSearchText(String newValue) {
		if (searchText == newValue) {
			return;
		}
		searchText = newValue;
		refreshData();
	}
	
	// displays on console the bioPaxObjects in the local list (the things we want to bring into the physiology)
	public static void printObjects(List<BioPaxObject> bpoList) {
		System.out.println(bpoList.size() + " bioPaxObjects in the local list");
		for(BioPaxObject bpo : bpoList) {
			BioModel.printObject(bpo);
		}
	}

	private void refreshData() {
		if (bioModel == null || bioModel.getPathwayModel() == null || bioPaxObjects == null) {
			setData(null);
			return;
		}
		// function I :: get selected objects only
		// create ConversionTableRow objects
		allPathwayObjectList = new ArrayList<ConversionTableRow>();
		convertedBPObjects = new HashSet<BioPaxObject>();
		
		printObjects(bioPaxObjects);
		BioModel.printBpModelObjects(bioModel.getPathwayModel().getBiopaxObjects());
//		BioModel.printBpRelationshipObjects(bioModel.getRelationshipModel().getBioPaxObjects());	// derived; the bpObjects that are part of a relationship
		BioModel.printRelationships(bioModel.getRelationshipModel().getRelationshipObjects());
		System.out.println("----------------------------------------------------------------------");
		
		for(BioPaxObject bpo : bioPaxObjects) {
		  if(bpo instanceof Conversion){
			  if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).size() == 0){
	  		    Conversion conversion = (Conversion)bpo;
				ArrayList<String> nameList = conversion.getName();
				String interactionId = conversion.getID();
				String interactionLabel = nameList.isEmpty() ? conversion.getIDShort() : nameList.get(0);
				ConversionTableRow newConversionTableRow = createTableRow(conversion, interactionId, interactionLabel,
						"Conversion", 1.0 , null);
				allPathwayObjectList.add(newConversionTableRow);
				convertedBPObjects.add(bpo);
				ArrayList<Stoichiometry> stoichiometryList =  conversion.getParticipantStoichiometry();
				// stoichiometryMap problem: 
				//			how to deal with the case that the same object occurs on both left and right sides
				HashMap <PhysicalEntity, Double> stoichiometryMap = createStoichiometryMap(stoichiometryList);
				// reactant
				for(BioPaxObject bpObject1: conversion.getLeft()){
					Double stoich = 1.0;
					if(stoichiometryMap.get((PhysicalEntity)bpObject1) != null){
						stoich = stoichiometryMap.get((PhysicalEntity)bpObject1);
					}
					ConversionTableRow conversionTableRow;
					if(bioModel.getRelationshipModel().getRelationshipObjects(bpObject1).isEmpty()){
						if(conversion instanceof Transport)
							conversionTableRow = createTableRowForTransportParticipant(bpObject1, interactionId, interactionLabel, "Reactant", stoich , null);
						else
							conversionTableRow = createTableRow(bpObject1, interactionId, interactionLabel, "Reactant", stoich , null);
					}else{
						if(conversion instanceof Transport)
							conversionTableRow = createTableRowForTransportParticipant(bpObject1, interactionId, interactionLabel,
									"Reactant", stoich , bioModel.getRelationshipModel().getRelationshipObjects(bpObject1));
						else
							conversionTableRow = createTableRow(bpObject1, interactionId, interactionLabel,
								"Reactant", stoich , bioModel.getRelationshipModel().getRelationshipObjects(bpObject1));
					}
					allPathwayObjectList.add(conversionTableRow);
					convertedBPObjects.add(bpObject1);
				}
				// product
				for(BioPaxObject bpObject1: conversion.getRight()){
					Double stoich = 1.0;
					if(stoichiometryMap.get((PhysicalEntity)bpObject1) != null){
						stoich = stoichiometryMap.get((PhysicalEntity)bpObject1);
					}
						ConversionTableRow conversionTableRow;
						if(bioModel.getRelationshipModel().getRelationshipObjects(bpObject1).isEmpty()){
							if(conversion instanceof Transport)
								conversionTableRow = createTableRowForTransportParticipant(bpObject1, interactionId, interactionLabel, "Product", stoich , null);
							else
								conversionTableRow = createTableRow(bpObject1, interactionId, interactionLabel, "Product", stoich , null);	
						}else{
							if(conversion instanceof Transport)
								conversionTableRow = createTableRowForTransportParticipant(bpObject1, interactionId, interactionLabel,
										"Product", stoich , bioModel.getRelationshipModel().getRelationshipObjects(bpObject1));
							else
								conversionTableRow = createTableRow(bpObject1, interactionId, interactionLabel,
									"Product", stoich , bioModel.getRelationshipModel().getRelationshipObjects(bpObject1));
						}
						allPathwayObjectList.add(conversionTableRow);
						convertedBPObjects.add(bpObject1);
				}
				//	 control
				for(BioPaxObject bpObject: bioModel.getPathwayModel().getBiopaxObjects()){
					if(bpObject instanceof Control){
						Control control = (Control)bpObject;
						if(control instanceof Catalysis){ // catalysis
							if(BioPAXUtil.getControlledNonControlInteraction(control) == conversion){
								for(PhysicalEntity pe : ((Catalysis) control).getPhysicalControllers()){
									ConversionTableRow conversionTableRow;
									if(bioModel.getRelationshipModel().getRelationshipObjects(pe).isEmpty()){
										conversionTableRow = createTableRow(pe, interactionId, interactionLabel,
												"Catalyst", 1.0 , null);
									}else{
										conversionTableRow = createTableRow(pe, interactionId, interactionLabel,
												"Catalyst", 1.0 , bioModel.getRelationshipModel().getRelationshipObjects(pe));
									} 
									allPathwayObjectList.add(conversionTableRow);
									convertedBPObjects.add(pe);
								}
							}
						}else{// other control types
							if(BioPAXUtil.getControlledNonControlInteraction(control) == conversion){
								for(PhysicalEntity pe : control.getPhysicalControllers()){
									ConversionTableRow conversionTableRow;
									if(bioModel.getRelationshipModel().getRelationshipObjects(pe).isEmpty()){
										conversionTableRow = createTableRow(pe, interactionId, interactionLabel,
												"Control", 1.0 , null);
									}else{
										conversionTableRow = createTableRow(pe, interactionId, interactionLabel,
												"Control", 1.0 , bioModel.getRelationshipModel().getRelationshipObjects(pe));
									} 
									allPathwayObjectList.add(conversionTableRow);
									convertedBPObjects.add(pe);
								}
							}
						}
					}
				}
			 }
		  }else if(bpo instanceof Catalysis){
			  for(PhysicalEntity pe : ((Catalysis) bpo).getPhysicalControllers()){
				  if (!convertedBPObjects.contains(pe)){
					ConversionTableRow conversionTableRow;
					if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).isEmpty()){
						conversionTableRow = createTableRow(pe, "", "",
								"Catalyst", 1.0 , null);
					}else{
						conversionTableRow = createTableRow(pe, "", "",
								"Catalyst", 1.0 , bioModel.getRelationshipModel().getRelationshipObjects(bpo));
					} 
					allPathwayObjectList.add(conversionTableRow);
					convertedBPObjects.add(pe);
				  }
				}
			  for(Pathway pathway : ((Catalysis) bpo).getPathwayControllers()){
				  // TODO
			  }
		  }else if(bpo instanceof Control){
			  for(PhysicalEntity pe : ((Catalysis) bpo).getPhysicalControllers()){
				  if (!convertedBPObjects.contains(pe)){
					ConversionTableRow conversionTableRow;
					if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).isEmpty()){
						conversionTableRow = createTableRow(pe, "", "",
								"Control", 1.0 , null);
					}else{
						conversionTableRow = createTableRow(pe, "", "",
								"Control", 1.0 , bioModel.getRelationshipModel().getRelationshipObjects(bpo));
					} 
					allPathwayObjectList.add(conversionTableRow);
					convertedBPObjects.add(pe);
				  }
				}
			  for(Pathway pathway : ((Catalysis) bpo).getPathwayControllers()){
				  // TODO
			  }
		  }
		}
		// 2nd pass - entities selected as themselves
		for(BioPaxObject bpo : bioPaxObjects){
			if(bpo instanceof PhysicalEntity){
				if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).size() == 0){
					PhysicalEntity physicalEntityObject = (PhysicalEntity)bpo;
					// we add standalone selected entities, only if they were not already added as part of any reaction
					if(!convertedBPObjects.contains(physicalEntityObject)){
						ConversionTableRow conversionTableRow = createTableRow(physicalEntityObject, "", "", "", 1.0 , null);
						allPathwayObjectList.add(conversionTableRow);
						convertedBPObjects.add(physicalEntityObject);
					}
				}
			}
		}
		
		// apply text search function for particular columns
		ArrayList<ConversionTableRow> pathwayObjectList = new ArrayList<ConversionTableRow>();
		if (searchText == null || searchText.length() == 0) {
			pathwayObjectList.addAll(allPathwayObjectList);
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();
			for (ConversionTableRow rs : allPathwayObjectList){
				BioPaxObject bpObject = rs.getBioPaxObject();
				if (rs.interactionLabel().toLowerCase().contains(lowerCaseSearchText)
					|| rs.participantType().toLowerCase().contains(lowerCaseSearchText)
					|| getLabel(bpObject).toLowerCase().contains(lowerCaseSearchText)
					|| getType(bpObject).toLowerCase().contains(lowerCaseSearchText) ) {
					pathwayObjectList.add(rs);
				}
			}
		}
		setData(pathwayObjectList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	
	private ConversionTableRow createTableRow(BioPaxObject bpObject, String interactionId, String interactionLabel, String participantType, 
			double  stoich, HashSet<RelationshipObject> relationshipObjects) {
		String location = "";
		ConversionTableRow conversionTableRow = new ConversionTableRow(bpObject);
		
		conversionTableRow.setInteractionId(interactionId);
		conversionTableRow.setInteractionLabel(interactionLabel);
		conversionTableRow.setParticipantType(participantType);
		
		// stoichiometry and location
		if(participantType.equals("Reactant") || participantType.equals("Product") ){
			//stoichiometry
			if(stoich != 0)
				conversionTableRow.setStoich(stoich);
			else
				conversionTableRow.setStoich(1.0);
			// location
//			if(((PhysicalEntity)bpObject).getCellularLocation() != null && ((PhysicalEntity)bpObject).getCellularLocation().getTerm().size()>0)
//				location = ((PhysicalEntity)bpObject).getCellularLocation().getTerm().get(0);
//			else
				location = bioModel.getModel().getStructures()[0].getName();
			conversionTableRow.setLocation(location);
		}else{
			conversionTableRow.setStoich(1.0);
			// location
//			if(!participantType.equals("Catalyst") && !participantType.equals("Control")  && 
//					(bpObject instanceof PhysicalEntity && ((PhysicalEntity)bpObject).getCellularLocation() != null)
//					&& ((PhysicalEntity)bpObject).getCellularLocation().getTerm().size()>0)
//				location = ((PhysicalEntity)bpObject).getCellularLocation().getTerm().get(0);
//			else 
			if(bpObject instanceof Transport){
				if(bioModel.getModel().getMembranes().size() > 0)
					location = bioModel.getModel().getMembranes().get(0).getName();
				else
					location = bioModel.getModel().getStructures()[0].getName();
			}else
				location = bioModel.getModel().getStructures()[0].getName();
			conversionTableRow.setLocation(location);
		}
		// id
		if(relationshipObjects == null){
			if(bpObject instanceof Entity) {
				String id = (BioPAXUtil.getName((Entity) bpObject)+"_"+location).trim();
				if(isValid(id))
					conversionTableRow.setId(id);
				else
					conversionTableRow.setId(changeID(id));		
			}
		}else{
			String id = null;
			for(RelationshipObject relationshipObject : relationshipObjects){
				if(relationshipObject.getBioModelEntityObject() instanceof MolecularType){
					id = relationshipObject.getBioModelEntityObject().getName();
					location = "";
				} else if(relationshipObject.getBioModelEntityObject().getStructure().getName().equalsIgnoreCase(location)){
					id = relationshipObject.getBioModelEntityObject().getName();
				}
			}
			if(id != null){
				// the linked bmObject with the same location will be used
				conversionTableRow.setId(id);
			}else{
				// a new bmObject will be created if no linked bmObject in the same location
				if(bpObject instanceof Entity) {
					id = (BioPAXUtil.getName((Entity) bpObject)+"_"+location).trim();
					if(isValid(id))
						conversionTableRow.setId(id);
					else
						conversionTableRow.setId(changeID(id));		
				}
			}
		}
		return conversionTableRow;
	}
	
	private ConversionTableRow createTableRowForTransportParticipant(BioPaxObject bpObject, String interactionId, 
			String interactionLabel, String participantType, double  stoich, HashSet<RelationshipObject> relationshipObjects) {
		String location = "";
		ConversionTableRow conversionTableRow = new ConversionTableRow(bpObject);
		
		conversionTableRow.setInteractionId(interactionId);
		conversionTableRow.setInteractionLabel(interactionLabel);
		conversionTableRow.setParticipantType(participantType);
		
		// stoichiometry and location
		Model model = bioModel.getModel();
		StructureTopology structTopology = model.getStructureTopology();
		if(participantType.equals("Reactant")){
			//stoichiometry
			if(stoich != 0)
				conversionTableRow.setStoich(stoich);
			else
				conversionTableRow.setStoich(1.0);
			// location
//			if(((PhysicalEntity)bpObject).getCellularLocation() != null){
//				location = ((PhysicalEntity)bpObject).getCellularLocation().getTerm().get(0);
//			}
//			else{
				if(model.getMembranes().size() > 0)
					location = structTopology.getOutsideFeature(model.getMembranes().get(0)).getName();
				else
					location = model.getStructures()[0].getName(); 
//			}
			conversionTableRow.setLocation(location);
		}else if(participantType.equals("Product")){
			//stoichiometry
			if(stoich != 0)
				conversionTableRow.setStoich(stoich);
			else
				conversionTableRow.setStoich(1.0);
			// location
//			if(((PhysicalEntity)bpObject).getCellularLocation() != null){
//				location = ((PhysicalEntity)bpObject).getCellularLocation().getTerm().get(0);
//			}
//			else{
				if(model.getMembranes().size() > 0)
					location = structTopology.getInsideFeature(model.getMembranes().get(0)).getName();
				else
					location = model.getStructures()[0].getName(); 
//			}
			conversionTableRow.setLocation(location);
		}else{
			conversionTableRow.setStoich(1.0);
			// location
//			if(!participantType.equals("Catalyst") && !participantType.equals("Control")  && 
//					(bpObject instanceof PhysicalEntity && ((PhysicalEntity)bpObject).getCellularLocation() != null))
//				location = ((PhysicalEntity)bpObject).getCellularLocation().getTerm().get(0);
//			else 
			if(bpObject instanceof Transport){
				if(model.getMembranes().size() > 0)
					location = model.getMembranes().get(0).getName();
				else
					location = model.getStructures()[0].getName();
			}else
				location = model.getStructures()[0].getName();
			conversionTableRow.setLocation(location);
		}
		// id
		if(relationshipObjects == null){
			if(bpObject instanceof Entity) {
				String id = (BioPAXUtil.getName((Entity) bpObject)+"_"+location).trim();
				if(isValid(id))
					conversionTableRow.setId(id);
				else
					conversionTableRow.setId(changeID(id));		
			}
		}else{
			String id = null;
			for(RelationshipObject relationshipObject : relationshipObjects){
				if(relationshipObject.getBioModelEntityObject().getStructure().getName().equalsIgnoreCase(location)){
					id = relationshipObject.getBioModelEntityObject().getName();
				}
			}
			if(id != null){
				// the linked bmObject with the same location will be used
				conversionTableRow.setId(id);
			}else{
				// a new bmObject will be created if no linked bmObject in the same location
				if(bpObject instanceof Entity) {
					id = (BioPAXUtil.getName((Entity) bpObject)+"_"+location).trim();
					if(isValid(id))
						conversionTableRow.setId(id);
					else
						conversionTableRow.setId(changeID(id));		
				}
			}
		}
		
		return conversionTableRow;
	}
	
	public void setBioModel(BioModel newValue) {
		if (bioModel == newValue) {
			return;
		}
		BioModel oldValue = bioModel;
		if (oldValue != null) {
			oldValue.getModel().removePropertyChangeListener(this);
			oldValue.getPathwayModel().removePathwayListener(this);
			oldValue.getRelationshipModel().removeRelationShipListener(this);
		}
		bioModel = newValue;
		if (newValue != null) {
			newValue.getModel().addPropertyChangeListener(this);
			newValue.getPathwayModel().addPathwayListener(this);
			newValue.getRelationshipModel().addRelationShipListener(this);
		}
		ownerTable.getColumnModel().getColumn(iColLocation).setCellEditor(getStructureComboBoxEditor());
		updateStructureComboBox();
	}

	public void setBioPaxObjects(List<BioPaxObject> newValue) {
		if (bioPaxObjects == newValue) {
			return;
		}
		bioPaxObjects = newValue;
		refreshData(); 
	}
	
	public HashSet<BioPaxObject> getBioPaxObjects(){
		return convertedBPObjects;
	}
	
	public ArrayList<ConversionTableRow> getTableRows(){
		return allPathwayObjectList;
	}

	public void pathwayChanged(PathwayEvent event) {
		refreshData();
	}
	
	private HashMap <PhysicalEntity, Double> createStoichiometryMap(ArrayList<Stoichiometry> stoichiometryList){
		HashMap <PhysicalEntity, Double> stoichiometryMap = new HashMap <PhysicalEntity, Double>();
		for(Stoichiometry sc : stoichiometryList){
			stoichiometryMap.put(sc.getPhysicalEntity(), sc.getStoichiometricCoefficient());
		}
		return stoichiometryMap;
	}
	
	public void relationshipChanged(RelationshipEvent event) {
//		if (event.getRelationshipObject() == null || event.getRelationshipObject().getBioModelEntityObject() == bioModelEntityObject) {
//			refreshData();
//		}
	}
	
	public String checkInputValue(String inputValue, int row, int column) {
		
		if(column == iColLocation && bioModel.getModel().getStructure(inputValue) == null){
				return "Structure '" + inputValue + "' does not exist!";
		}
		return null;
	}
	
}
