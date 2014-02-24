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
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JTable;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.CellularLocationVocabulary;
import org.vcell.pathway.Complex;
import org.vcell.pathway.Control;
import org.vcell.pathway.Dna;
import org.vcell.pathway.DnaRegion;
import org.vcell.pathway.Entity;
import org.vcell.pathway.EntityImpl;
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
import org.vcell.relationship.AnnotationMapping;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
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
		BioModelEntityObject bioModelEntityObject;
		private BioPaxObjectProperty(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		private BioPaxObjectProperty(String name, String value, BioPaxObject bioPaxObject) {
			super();
			this.name = name;
			this.value = value;
			this.bioPaxObject = bioPaxObject;
		}
		private BioPaxObjectProperty(String name, String value, BioModelEntityObject bioModelEntityObject) {
			super();
			this.name = name;
			this.value = value;
			this.bioModelEntityObject = bioModelEntityObject;
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


private TreeSet<Entity> lookupEntities =
new TreeSet<Entity>(new Comparator<Entity>() {
	@Override
	public int compare(Entity o1, Entity o2) {
		return ((EntityImpl)o1).getID().compareTo(((EntityImpl)o2).getID());
	}
});

private synchronized boolean lookupContains(Entity entity){
return lookupEntities.contains(entity);
}
private synchronized void lookupAdd(Entity entity){
lookupEntities.add(entity);
}
private synchronized void lookupRemove(Entity entity){
lookupEntities.remove(entity);
}

private void lookupFormalName(final int tableRow){
	final String FORMAL_NAMES_KEY = "FORMAL_NAMES_KEY";
	final Entity entity = (Entity)BioPaxObjectPropertiesPanel.this.bioPaxObject;
	AsynchClientTask initLookupTask = new AsynchClientTask("init lookup...",AsynchClientTask.TASKTYPE_SWING_BLOCKING,false) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(!lookupContains(entity)){
				lookupAdd(entity);
			}
			BioPaxObject current = BioPaxObjectPropertiesPanel.this.bioPaxObject;
			if (current == entity) {
				refreshInterface();
			}
		}
	};
	AsynchClientTask lookupTask = new AsynchClientTask("looking...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,false) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			ArrayList<Xref> xrefArrList = entity.getxRef();
			ArrayList<String> formalNames = AnnotationMapping.getNameRef(xrefArrList, null);
			if(formalNames != null && formalNames.size() > 0){
				hashTable.put(FORMAL_NAMES_KEY, formalNames);
			}
		}
	};
	AsynchClientTask finishLookupTask = new AsynchClientTask("formal name lookup complete",AsynchClientTask.TASKTYPE_SWING_NONBLOCKING,false) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			try{
				@SuppressWarnings("unchecked")
				ArrayList<String> formalNames = (ArrayList<String>)hashTable.get(FORMAL_NAMES_KEY);
				if(formalNames != null){
					entity.setFormalNames(formalNames);
				}else if(entity.getxRef() != null && entity.getxRef().size() > 0){
					String str = "";
					for (int i = 0; i < entity.getxRef().size(); i++) {
						str+= (i>0?"\n":"")+entity.getxRef().get(i).getDb()+":"+entity.getxRef().get(i).getId();
					}
					throw new Exception("Formal name lookup not implemented using:\n"+str);
				}else{
					throw new Exception("No cross-references available to lookup formal name");
				}
			}finally{
				lookupRemove(entity);
				BioPaxObject current = BioPaxObjectPropertiesPanel.this.bioPaxObject;
				if (current == entity) {
					refreshInterface();
					table.setRowSelectionInterval(tableRow, tableRow);
				}
			}
		}
	};
	ClientTaskDispatcher.dispatch(null, new Hashtable<String, Object>(), new AsynchClientTask[] {initLookupTask,lookupTask,finishLookupTask}, null, false, false, false, null, false);

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
			    	if (bioPaxObject == null) {
			    		BioModelEntityObject bioModelEntityObject = property.bioModelEntityObject;
						if (bioModelEntityObject != null) {
							selectionManager.setActiveView(new ActiveView(null,DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram));
							selectionManager.setSelectedObjects(new Object[]{bioModelEntityObject});
						}else if(((Entity)BioPaxObjectPropertiesPanel.this.bioPaxObject).getFormalNames() == null || ((Entity)BioPaxObjectPropertiesPanel.this.bioPaxObject).getFormalNames().size() == 0){
							lookupFormalName(crow);
						}
			    	} else if (bioPaxObject instanceof Xref) { // if xRef, get url
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
					BioPaxObjectProperty property = tableModel.getValueAt(row);
					BioPaxObject bpObject = property.bioPaxObject;
					String text = property.value;
					if (bpObject == null) {
						BioModelEntityObject bioModelEntityObject = property.bioModelEntityObject;
						if (bioModelEntityObject != null) {
							if (!isSelected) {
								setForeground(Color.blue);
							}
							setText("<html><u>" + text + "</u></html>");
						}
					} else {	
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
		propertyList.add(new BioPaxObjectProperty("Type", bioPaxObject.getTypeLabel()));
		// entity::name
		if(lookupContains(entity)){
			propertyList.add(new BioPaxObjectProperty("Name",entity.getName().get(0)+" (looking...)"));
		}else if(entity.getFormalNames() != null && entity.getFormalNames().size() != 0){
			propertyList.add(new BioPaxObjectProperty("Name", entity.getName().get(0)+" ("+entity.getFormalNames().get(0)+")"));
		}else if (entity.getName() != null && entity.getName().size() > 0){
			String displayName = entity.getName().get(0);
			if(entity.getxRef() != null && entity.getxRef().size() > 0){
				displayName = displayName+" (double-click lookup)";
			}
			propertyList.add(new BioPaxObjectProperty("Name", displayName));			// entity::name
		}
		// entity::availability (***ignored***)
		// entity::dataSource (***ignored***)
		// entity::evidence (***ignored***)
		
		// entity::Link
		for(RelationshipObject rObject : bioModel.getRelationshipModel().getRelationshipObjects(bioPaxObject)){
			BioModelEntityObject beObject = rObject.getBioModelEntityObject();
			propertyList.add(new BioPaxObjectProperty("Linked physiology object", beObject.getName(), beObject));
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
			}else if (entity.getName() != null && entity.getName().size()>1){
				String location  = entity.getName().get(1);
				if (location.contains("[") && location.contains("]")){
					location = location.substring(location.indexOf("[")+1, location.indexOf("]"));
					propertyList.add(new BioPaxObjectProperty("Cellular Location", location));
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
			propertyList.add(new BioPaxObjectProperty("Comment", comment));
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

