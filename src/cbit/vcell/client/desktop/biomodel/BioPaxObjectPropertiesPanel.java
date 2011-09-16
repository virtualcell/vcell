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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.CellularLocationVocabulary;
import org.vcell.pathway.Complex;
import org.vcell.pathway.Control;
import org.vcell.pathway.Dna;
import org.vcell.pathway.DnaRegion;
import org.vcell.pathway.Entity;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionVocabulary;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Protein;
import org.vcell.pathway.PublicationXref;
import org.vcell.pathway.RelationshipXref;
import org.vcell.pathway.Rna;
import org.vcell.pathway.RnaRegion;
import org.vcell.pathway.SmallMolecule;
import org.vcell.pathway.UnificationXref;
import org.vcell.pathway.Xref;
import org.vcell.pathway.group.PathwayGrouping;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.BioModelEntityObject;


@SuppressWarnings("serial")
public class BioPaxObjectPropertiesPanel extends DocumentEditorSubPanel {
	
	private BioPaxObject bioPaxObject = null;	
	private BioModel bioModel = null;
	private ScrollTable table = null;
	private BioPaxObjectPropertiesTableModel tableModel = null;
	
	private static class BioPaxObjectProperty {
		String name;
		String value;
		BioPaxObject bioPaxObject;
		private BioPaxObjectProperty(String name, String value, BioPaxObject bioPaxObject) {
			super();
			this.name = name;
			this.value = value;
			this.bioPaxObject = bioPaxObject;
		}
	}
	private static class BioPaxObjectPropertiesTableModel extends VCellSortTableModel<BioPaxObjectProperty> {

		private static final int Column_Property = 0;
		private static final int Column_Value = 1;
		public BioPaxObjectPropertiesTableModel(ScrollTable table) {
			super(table);
			setColumns(new String[] {"Property", "Value"});
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			BioPaxObjectProperty property = getValueAt(rowIndex);
			if (columnIndex == Column_Property) {
				return property.name;
			} 
			if (columnIndex == Column_Value) {
				return property.value;
			}
			return null;
		}
		
		@Override
		protected Comparator<BioPaxObjectProperty> getComparator(int col,
				boolean ascending) {
			return null;
		}

		@Override
		public boolean isSortable(int col) {
			return false;
		}
		
	}
			
public BioPaxObjectPropertiesPanel() {
	super();
	initialize();
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
		table = new ScrollTable();
		tableModel = new BioPaxObjectPropertiesTableModel(table);
		table.setModel(tableModel);
		
		setLayout(new BorderLayout());
		add(table.getEnclosingScrollPane(), BorderLayout.CENTER);
		setBackground(Color.white);		
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2) {
					return;
				}
				
			    Point pt = e.getPoint();
			    int crow = table.rowAtPoint(pt);
			    int ccol = table.columnAtPoint(pt);
			    if(table.convertColumnIndexToModel(ccol) == BioPaxObjectPropertiesTableModel.Column_Value) {
			    	BioPaxObjectProperty property = tableModel.getValueAt(crow);
			    	BioPaxObject bioPaxObject = property.bioPaxObject;
			    	if (bioPaxObject instanceof Xref) { // if xRef, get url 
			    		String url = ((Xref) bioPaxObject).getURL();
			    		DialogUtils.browserLauncher(BioPaxObjectPropertiesPanel.this, url, "Wrong URL.", false);
			    	}
			    }
				
			}
			
		});
		
		table.getColumnModel().getColumn(BioPaxObjectPropertiesTableModel.Column_Value).setCellRenderer(new DefaultScrollTableCellRenderer(){
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,	row, column);
				if (column == BioPaxObjectPropertiesTableModel.Column_Value) {
					BioPaxObject bpObject = tableModel.getValueAt(row).bioPaxObject;
					String text = tableModel.getValueAt(row).value;
					if(bpObject instanceof Xref){
						String url = ((Xref) bpObject).getURL();
						if(url != null){
							setToolTipText(url);
							if (!isSelected) {
								setForeground(Color.blue);
							}
							setText("<html><u>" + text + "</u></html>");
						}
					}
				}
				return this;
			}
		});

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public void setBioModel(BioModel newValue) {
	if (bioModel == newValue) {
		return;
	}
	bioModel = newValue;
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		setBioPaxObject(null);
	} else if (selectedObjects[0] instanceof BioPaxObject) {
		setBioPaxObject((BioPaxObject) selectedObjects[0]);
	} else {
		setBioPaxObject(null);
	}
}

private void setBioPaxObject(BioPaxObject newValue) {
	bioPaxObject = newValue;
	refreshInterface();
}

protected void refreshInterface() {
	if (bioPaxObject == null){
		return;
	}
	ArrayList<BioPaxObjectProperty> propertyList = new ArrayList<BioPaxObjectProperty>();
	
	if (bioPaxObject instanceof Entity){
		Entity entity = (Entity)bioPaxObject;
		// entity::type
		propertyList.add(new BioPaxObjectProperty("Type", bioPaxObject.getTypeLabel(), null));
		// entity::name
		ArrayList<String> name = entity.getName();
		if (name != null){
			if (name.size() > 0) {
				propertyList.add(new BioPaxObjectProperty("Name", name.get(0), null));
			}
		}
		// entity::availability (***ignored***)
		// entity::dataSource (***ignored***)
		// entity::evidence (***ignored***)
		
		// entity::Link
		for(RelationshipObject rObject : bioModel.getRelationshipModel().getRelationshipObjects(bioPaxObject)){
			BioModelEntityObject beObject = rObject.getBioModelEntityObject();
			propertyList.add(new BioPaxObjectProperty("Linked physiology object", beObject.getName(), bioPaxObject));
		}
		
		if(entity instanceof PhysicalEntity){
			PhysicalEntity physicalEntity = (PhysicalEntity)entity;
			// physicalEntity::feature (***ignored***)
			// physicalEntity::memberPhysicalEntity (***ignored***)
			// physicalEntity::notFeature (***ignored***)
			
			// physicalEntity::cellular location
			CellularLocationVocabulary cellularLocation = physicalEntity.getCellularLocation();
			if (cellularLocation!=null){
				propertyList.add(new BioPaxObjectProperty("Cellular Location", cellularLocation.getTerm().get(0),cellularLocation));
			}else if (name != null && name.size()>1){
				String location  = name.get(1);
				if (location.contains("[") && location.contains("]")){
					location = location.substring(location.indexOf("[")+1, location.indexOf("]"));
					propertyList.add(new BioPaxObjectProperty("Cellular Location", location, null));
				}
			}
			
			if (entity instanceof Complex){
				Complex complex = (Complex)entity;
				// complex::componentStoichiometry (***ignored***)

				// complex::components
				for(PhysicalEntity pe : complex.getComponents()){
					propertyList.add(new BioPaxObjectProperty("Component", getEntityName(pe), pe));
				}
				
			} else if (entity instanceof Protein){
//				Protein protein = (Protein)entity;
				// protein::entity reference (***ignored***)

			} else if (entity instanceof SmallMolecule){
//				SmallMolecule smallMolecule = (SmallMolecule) entity;
				// smallMolecule::entityReference (***ignored***)

			} else if (entity instanceof Dna){
				// dna::entityReference (***ignored***)

			} else if (entity instanceof DnaRegion){
				// dnaRegion::entityReference (***ignored***)
				
			} else if (entity instanceof Rna){
				// rna::entityReference (***ignored***)
				
			} else if (entity instanceof RnaRegion){
				// rnaRegion::entityReference (***ignored***)
				
			}
		}else if(entity instanceof Interaction){
			Interaction interaction = (Interaction)entity;
			// interaction::interactionType
			for (InteractionVocabulary interactionVocabulary : interaction.getInteractionTypes()){
				if (interactionVocabulary.getTerm().size()>0){
					propertyList.add(new BioPaxObjectProperty("Interaction Type", interactionVocabulary.getTerm().get(0), interactionVocabulary));
				}
			}
			// interaction::participants
			for (InteractionParticipant interactionParticipant : interaction.getParticipants()){
				PhysicalEntity physicalEntity = interactionParticipant.getPhysicalEntity();
				String physicalEntityName = physicalEntity.getName().size()>0 ? physicalEntity.getName().get(0) : physicalEntity.getID();
				propertyList.add(new BioPaxObjectProperty(interactionParticipant.getLevel3PropertyName(), physicalEntityName, physicalEntity));
			}
			
			// get the catalysts for interactions
			Set<String> catalysisList = getCatalysisSet(interaction);
			if(catalysisList.size() > 0 ){
				for(String str : catalysisList){
					propertyList.add(new BioPaxObjectProperty("Catalyzed by", str, interaction));
				}
			}
			
			// get the controls for interactions
			Set<String> controlList = getControlSet(interaction);
			if(controlList.size() > 0 ){
				for(String str : controlList){
					propertyList.add(new BioPaxObjectProperty("Controled by", str, interaction));
				}
			}
			
			if (interaction instanceof Catalysis){
				Catalysis catalysis = (Catalysis)interaction;
				// catalysis::controlled
				Interaction controlledInteraction = catalysis.getControlledInteraction();
				if (controlledInteraction!=null){
					String controlledName = controlledInteraction.getID();
					if (controlledInteraction.getName().size()>0){
						controlledName = controlledInteraction.getName().get(0);
					}
					propertyList.add(new BioPaxObjectProperty("Controlled Interaction", controlledName, controlledInteraction));
				}
			}
			
		}else if(entity instanceof GroupObject){
			GroupObject groupObject = (GroupObject)entity;
			for(BioPaxObject bpo : groupObject.getGroupedObjects()){
				propertyList.add(new BioPaxObjectProperty("Element::" + bpo.getTypeLabel(), 
						getEntityName((Entity)bpo), bpo));
			}
		}
//		
//		//Neighbor: this function will be removed later
//		PathwayGrouping pathwayGrouping = new PathwayGrouping();
//		Set<BioPaxObject> neighbors = pathwayGrouping.computeNeighbors(bioModel.getPathwayModel(), (Entity) bioPaxObject);
//		if(neighbors != null ){
//			for(BioPaxObject bpo : neighbors){
//				if(((Entity)bpo).getName() != null && ((Entity)bpo).getName().size()>0)
//					propertyList.add(new BioPaxObjectProperty("Neighbors", (((Entity)bpo).getName().get(0)), bpo));
//			}
//		}
		
		// entity::xRef
		ArrayList<Xref> xrefList = ((Entity) bioPaxObject).getxRef();
		for (Xref xref : xrefList) {
			if (xref instanceof UnificationXref){
				propertyList.add(new BioPaxObjectProperty("Xref", xref.getDb() + ":" + xref.getId(), xref));
			}
		}
		for (Xref xref : xrefList) {
			if (xref instanceof RelationshipXref){
				propertyList.add(new BioPaxObjectProperty("Xref (related)", xref.getDb() + ":" + xref.getId(), xref));
			}
		}
		for (Xref xref : xrefList) {
			if (xref instanceof PublicationXref){
				propertyList.add(new BioPaxObjectProperty("Publication", xref.getDb() + ":" + xref.getId(), xref));
			}
		}
		// entity::comments
		for (String comment : entity.getComments()){
			propertyList.add(new BioPaxObjectProperty("Comment", comment, null));
		}
	}
	tableModel.setData(propertyList);
	
}

private Set<String> getCatalysisSet(Interaction interaction){
	Set<String> catalystList = new HashSet<String>();
	if(bioModel == null){
		return catalystList;
	}
	for(BioPaxObject bpObject : bioModel.getPathwayModel().getBiopaxObjects()){
		if(bpObject instanceof Catalysis){
			Catalysis catalysis = (Catalysis) bpObject;
			if(catalysis.getControlledInteraction() == interaction){
				if(catalysis.getPhysicalControllers() != null){
					for(PhysicalEntity ep : catalysis.getPhysicalControllers()){ 
						if(ep.getName().size() > 0)
							catalystList.add(ep.getName().get(0));
						else
							catalystList.add(ep.getID());
					}
				}
			}
		}
	}
	return catalystList;
}

private Set<String> getControlSet(Interaction interaction){
	Set<String> controlList = new HashSet<String>();
	if(bioModel == null){
		return controlList;
	}
	for(BioPaxObject bpObject : bioModel.getPathwayModel().getBiopaxObjects()){
		if(bpObject instanceof Control && !(bpObject instanceof Catalysis)){
			Control control = (Control) bpObject;
			if(control.getControlledInteraction() == interaction){
				if(control.getPhysicalControllers() != null){
					for(PhysicalEntity ep : control.getPhysicalControllers()){ 
						if(ep.getName().size() > 0)
							controlList.add(ep.getName().get(0));
						else{
							controlList.add(ep.getID());
						}
					}
				}
			}
		}
	}
	return controlList;
}

private String getEntityName(Entity bpObject){
	if(bpObject.getName().size() == 0){
		return bpObject.getID();
	}else{
		return bpObject.getName().get(0);
	}
}

}

