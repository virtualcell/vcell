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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.View;

import org.sbpax.schemas.util.SBPAX3Util;
import org.vcell.pathway.BioPAXUtil;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.CellularLocationVocabulary;
import org.vcell.pathway.Complex;
import org.vcell.pathway.Control;
import org.vcell.pathway.Dna;
import org.vcell.pathway.DnaRegion;
import org.vcell.pathway.Entity;
import org.vcell.pathway.EntityReference;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionVocabulary;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Protein;
import org.vcell.pathway.PublicationXref;
import org.vcell.pathway.RelationshipXref;
import org.vcell.pathway.Rna;
import org.vcell.pathway.RnaRegion;
import org.vcell.pathway.SmallMolecule;
import org.vcell.pathway.UnificationXref;
import org.vcell.pathway.Xref;
import org.vcell.pathway.sbo.SBOListEx;
import org.vcell.pathway.sbo.SBOTerm;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBPAXLabelUtil;
import org.vcell.pathway.sbpax.UnitOfMeasurement;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
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
				// launch the browser when double click on hyperlinks
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
						}
			    	} else if (bioPaxObject instanceof Xref) { // if xRef, get url
			    		String url = ((Xref) bioPaxObject).getURL();
			    		DialogUtils.browserLauncher(BioPaxObjectPropertiesPanel.this, url, "Wrong URL.", false);
			    	} else if (bioPaxObject instanceof SBEntity) {		// TODO: kineticLaw
			    		SBEntity sbE = (SBEntity)bioPaxObject;
						if(sbE.getID().contains("kineticLaw")) {
							String url = "http://sabio.h-its.org/sabioRestWebServices/kineticLaws/" + sbE.getID().substring(sbE.getID().indexOf("kineticLaw") + 10);
							DialogUtils.browserLauncher(BioPaxObjectPropertiesPanel.this, url, "Wrong URL.", false);
						}
			    	}
			    }
			}
		});		// --- end of addMouseListener()
		
		table.getColumnModel().getColumn(BioPaxObjectPropertiesTableModel.Column_Value).setCellRenderer(new DefaultScrollTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,	row, column);
				if (column == BioPaxObjectPropertiesTableModel.Column_Value) {
					BioPaxObjectProperty property = tableModel.getValueAt(row);
					BioPaxObject bpObject = property.bioPaxObject;
					String text = property.value;
					// colorize BLUE and add surround text with <html></html> tags
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
						} else if(bpObject instanceof SBEntity) {
							String url = ((SBEntity) bpObject).getID();
							if(url.contains("kineticLaw")) {
								setToolTipText(url);
								if (!isSelected) {
									setForeground(Color.blue);
								}
								if(url.contains("http")) {
									setText("<html><u>" + text + "</u></html>");
								}
							}
						}
					}
				}
				return this;
			}
		});		// --- end of setCellRenderer()

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
	if (bioPaxObject == null){	// sanity check
		return;
	}
	ArrayList<BioPaxObjectProperty> propertyList = new ArrayList<BioPaxObjectProperty>();

	if(!(bioPaxObject instanceof SBEntity)) {
		tableModel.setData(propertyList);
		return;
	}
	SBEntity sbEntity = (SBEntity) bioPaxObject;
	if (!(sbEntity instanceof Entity)){
		tableModel.setData(propertyList);
		return;
	}

	Entity entity = (Entity) sbEntity;
	propertyList.add(new BioPaxObjectProperty("Type", bioPaxObject.getTypeLabel()));	// entity::type
	ArrayList<String> name = entity.getName();
	if (name != null){
		if (name.size() > 0) {
			propertyList.add(new BioPaxObjectProperty("Name", name.get(0)));			// entity::name
		}
	}

	// entity::availability (***ignored***)
	// entity::dataSource (***ignored***)
	// entity::evidence (***ignored***)
	// entity::Link
	for(RelationshipObject rObject : bioModel.getRelationshipModel().getRelationshipObjects(bioPaxObject)){
		BioModelEntityObject beObject = rObject.getBioModelEntityObject();
		propertyList.add(new BioPaxObjectProperty("Linked physiology object", beObject.getName(), beObject));
	}

	if(entity instanceof PhysicalEntity){	// ------------------------ PHYSICAL ENTITY -----------------------
		PhysicalEntity physicalEntity = (PhysicalEntity)entity;
		// physicalEntity::feature (***ignored***)
		// physicalEntity::memberPhysicalEntity (***ignored***)
		// physicalEntity::notFeature (***ignored***)

// TODO:  extract the kinetic law, then the SBEntities, then the measurables, units, aso
		Set<SBEntity> kineticLaws = BioPAXUtil.getKineticLawsOfController(physicalEntity, bioModel);
		if(!kineticLaws.isEmpty()) {
			propertyList.add(new BioPaxObjectProperty("Role", "Controller"));	// if we found kinetic laws then it's a controller
		}
		for(SBEntity kL : kineticLaws) {
			propertyList.add(new BioPaxObjectProperty("SBML for Kinetic Law", kL.getID(), kL));
			ArrayList<SBEntity> klProperties = kL.getSBSubEntity();
			for(SBEntity klProperty : klProperties) {
				if(klProperty instanceof SBMeasurable) {
					SBMeasurable m  = (SBMeasurable)klProperty;
					String str1 = "";
					String str2 = "";
					String str3 = "";
					if(!m.getSBTerm().isEmpty()) {
						str1 += m.getSBTerm().get(0).toString();
						str1 = str1.substring(str1.lastIndexOf('#'));
						str1 = str1.replace('#', ' ');
						str1 = str1.replace('\'', ' ');
						str1 = str1.replace("(proxy)", "");
						str1 = str1.replace(" ", "");
						str2 += m.getNumber() + "";
						str2 = str2.replace('[', ' ');
						str2 = str2.replace(']', ' ');
					}
					if(!m.getUnit().isEmpty()) {
						UnitOfMeasurement u = m.getUnit().get(0);
						if(u.getSymbols().isEmpty()) {
							str3 += u.getIDShort();
						} else {
							str3 += u.getSymbols().get(0);
						}
					}
					// str1 is an SBO id, for example "SBO:0000064"
					SBOTerm sboT = SBOListEx.sboMap.get(str1);
					
					propertyList.add(new BioPaxObjectProperty("   " + sboT.getSymbol() + "  (" + sboT.getName() + "): " + sboT.getDescription(), str2 + str3));
				} else {
					propertyList.add(new BioPaxObjectProperty("   " + klProperty.getIDShort(), klProperty.getTypeLabel()));
				}
			}
		}
				
		if(!(physicalEntity instanceof SmallMolecule)){
			// physicalEntity::cellular location
			CellularLocationVocabulary cellularLocation = physicalEntity.getCellularLocation();
			if (cellularLocation!=null && cellularLocation.getTerm() != null && cellularLocation.getTerm().size() > 0){
				propertyList.add(new BioPaxObjectProperty("Cellular Location", cellularLocation.getTerm().get(0),cellularLocation));
			}else if (name != null && name.size()>1){
				String location  = name.get(1);
				if (location.contains("[") && location.contains("]")){
					location = location.substring(location.indexOf("[")+1, location.indexOf("]"));
					propertyList.add(new BioPaxObjectProperty("Cellular Location", location));
				}
			}
		}

		if (physicalEntity instanceof Complex){
			Complex complex = (Complex)physicalEntity;
			// complex::componentStoichiometry (***ignored***)
			// complex::components
			for(PhysicalEntity pe : complex.getComponents()){
				propertyList.add(new BioPaxObjectProperty("Component", getEntityName(pe), pe));
			}
		} else if (physicalEntity instanceof Protein){
			//				Protein protein = (Protein)entity;
			// protein::entity reference (***ignored***)
		} else if (physicalEntity instanceof SmallMolecule){
			SmallMolecule sm = (SmallMolecule)physicalEntity;
			EntityReference er = sm.getEntityReference();
			if(er != null && !er.getName().isEmpty() && er.getName().get(0) != null && !er.getName().get(0).isEmpty()) {
				propertyList.add(new BioPaxObjectProperty("Entity Reference", er.getName().get(0)));
				ArrayList<Xref> xrefList = er.getxRef();
				for (Xref xref : xrefList) {
					propertyList.add(new BioPaxObjectProperty("   Xref", xref.getDb() + ":" + xref.getId(), xref));
				}
			}
		} else if (physicalEntity instanceof Dna){
			// dna::entityReference (***ignored***)
		} else if (physicalEntity instanceof DnaRegion){
			// dnaRegion::entityReference (***ignored***)
		} else if (physicalEntity instanceof Rna){
			// rna::entityReference (***ignored***)
		} else if (physicalEntity instanceof RnaRegion){
			// rnaRegion::entityReference (***ignored***)
		}
	} else if(entity instanceof Interaction){		// --------------------------- INTERACTION -------------------
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
			String physicalEntityName = physicalEntity.getName().size()>0 ? physicalEntity.getName().get(0) : physicalEntity.getIDShort();
			String cellularLocation = "";
			if(physicalEntity.getCellularLocation() != null){
				cellularLocation = physicalEntity.getCellularLocation().getTerm().size()>0 ? " ["+physicalEntity.getCellularLocation().getTerm().get(0)+"]" : ""; 
			}
			propertyList.add(new BioPaxObjectProperty(interactionParticipant.getLevel3PropertyName(), physicalEntityName+cellularLocation, physicalEntity));
		}

		// get the controllers for interactions
		// we always need this because there's no guarantee we'll have kinetic laws
		// for instance pathway commons doesn't have quantitative information
		Set<String> controllersNames = getControllersNames(interaction);
		if(controllersNames.size() > 0 ){
			for(String str : controllersNames){
				propertyList.add(new BioPaxObjectProperty("Controlled by", str, interaction));
			}
		}
		// get the kinetic laws (if any)
		Set<Control> controls = BioPAXUtil.getControlsOfInteraction(interaction, bioModel);
		for(Control control : controls) {
			ArrayList<SBEntity> sbEntities = control.getSBSubEntity();
			for(SBEntity sbE : sbEntities) {
				if(sbE.getID().contains("kineticLaw")) {
					// TODO: when double click navigate to the catalyst (no more web hyper link)
					// TODO: or when single click highlight the catalyst
					String str = new String();
					if(control.getPhysicalControllers() != null){
						str += " for Controller(s): ";
						for(PhysicalEntity ep : control.getPhysicalControllers()){
							if(ep.getName().size() > 0) {
								str += ep.getName().get(0);
							} else {
								str += ep.getIDShort();
							}
							str += " ";
						}
					}
					propertyList.add(new BioPaxObjectProperty("Kinetic Law" + str, sbE.getID(), sbE));									
				}
			}
		}

		if (interaction instanceof Control){		// TODO: is this ever being called?
			Control c = (Control)interaction;
			// catalysis::controlled
			Interaction controlledInteraction = c.getControlledInteraction();
			if (controlledInteraction!=null){
				String controlledName = controlledInteraction.getIDShort();
				if (controlledInteraction.getName().size()>0){
					controlledName = controlledInteraction.getName().get(0);
				}
				propertyList.add(new BioPaxObjectProperty("Controlled Interaction", controlledName, controlledInteraction));
			}
		}
		
	} else if(entity instanceof GroupObject){		// ---------------------- GROUP OBJECT ------------------
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
//	for(SBVocabulary sbVocab : sbEntity.getSBTerm()) {
//		propertyList.add(new BioPaxObjectProperty("SBO Term", SBPAXLabelUtil.makeLabel(sbVocab)));
//	}
			
//	if(sbEntity instanceof Interaction) {
//		// TODO: this goes away
//		Interaction interaction = (Interaction) sbEntity;
//		Set<SBEntity> subEntities = new HashSet<SBEntity>();
//		subEntities.add(interaction);
//		Set<Control> controls = BioPAXUtil.findAllControls(interaction, bioModel.getPathwayModel());
//		subEntities.addAll(controls);
//		subEntities = SBPAX3Util.extractAllEntities(subEntities);
//		for(SBEntity subEntity : subEntities) {
//			if(subEntity instanceof SBMeasurable) {
//				propertyList.add(new BioPaxObjectProperty("Measured quantity", SBPAXLabelUtil.makeLabel(subEntity)));									
//			}
//		}
//	}
	tableModel.setData(propertyList);
}

private Set<String> getControllersNames(Interaction interaction){
	Set<String> controllersNames = new HashSet<String>();
	if(bioModel == null){
		return controllersNames;
	}
	Set<Control> controls = BioPAXUtil.getControlsOfInteraction(interaction, bioModel);
	for(Control control : controls) {
		if(control.getPhysicalControllers() != null){
			for(PhysicalEntity ep : control.getPhysicalControllers()){ 
				String type = control.getControlType();
				if(type == null) {
					type = "";
				}else{
					type = " (" + type + ")";
				}
				if(ep.getName().size() > 0)
					controllersNames.add(ep.getName().get(0)+type);
				else{
					controllersNames.add(ep.getIDShort()+type);
				}
			}
		}
	}
	return controllersNames;
}

private String getEntityName(Entity bpObject){
	if(bpObject.getName().size() == 0){
		return bpObject.getIDShort();
	}else{
		return bpObject.getName().get(0);
	}
}


public static void main(String[] argv) {
	
	JEditorPane jep = new JEditorPane("text/html", "The rain in <a href='http://foo.com/'>"  
			+"Spain</a> falls mainly on the <a href='http://bar.com/'>plain</a>.");   
	jep.setEditable(false);   
	jep.setOpaque(false);   
	jep.addHyperlinkListener(new HyperlinkListener() {
		public void hyperlinkUpdate(HyperlinkEvent hle) {   
			if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {   
				System.out.println(hle.getURL());   
			}
		}
	});
	JEditorPane jep1 = new JEditorPane("text/html", "ala bala por to calaala bala por to calaala bala por to calaala bala por to calaala bala por to cala");   
	jep1.setEditable(false);   
	jep1.setOpaque(false);
	    
	JPanel p = new JPanel();   
	p.add( new JLabel("Foo.") );   
	p.add( jep );   
	p.add( new JLabel("Bar.") );
	p.add( new JEditorPane("text","ala bala por to calaala bala por to calaala bala por to calaala bala por to calaala bala por to cala"));
	p.add( jep1 );   
	  
	JFrame f = new JFrame("HyperlinkListener");   
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
	f.getContentPane().add(p, BorderLayout.CENTER);   
	f.setSize(400, 150);   
	f.setVisible(true);   
	}  

}
