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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.model.rbm.MolecularType;
import org.vcell.pathway.BioPAXUtil;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.CellularLocationVocabulary;
import org.vcell.pathway.Complex;
import org.vcell.pathway.Control;
import org.vcell.pathway.Dna;
import org.vcell.pathway.DnaRegion;
import org.vcell.pathway.Entity;
import org.vcell.pathway.EntityImpl;
import org.vcell.pathway.EntityReference;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionVocabulary;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
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
import org.vcell.pathway.sbpax.SBVocabulary;
import org.vcell.relationship.AnnotationMapping;
import org.vcell.relationship.PathwayMapping;
import org.vcell.relationship.RelationshipEvent;
import org.vcell.relationship.RelationshipListener;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.BeanUtils;
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
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.resource.PropertyLoader;


@SuppressWarnings("serial")
public class BioPaxObjectPropertiesPanel extends DocumentEditorSubPanel implements RelationshipListener {
	
	private BioPaxObject bioPaxObject = null;	
	private BioModel bioModel = null;
	private ScrollTable table = null;
	private BioPaxObjectPropertiesTableModel tableModel = null;
	private JTextPane details = null;
	private JSplitPane splitPane = null;
	
	private static class BioPaxObjectProperty {
		String name;
		String value;
		String tooltip;				// sometimes we want to enforce a tooltip instead of extracting it from the object
		String details;
		BioPaxObject bioPaxObject;
		BioModelEntityObject bioModelEntityObject;

		private BioPaxObjectProperty(String name, String value, String tooltip) {
			super();
			this.name = name;
			this.value = value;
			if(tooltip != null) {
				this.tooltip = tooltip;
			} else {
				this.tooltip = "";
			}
			this.details = "";
		}
		private BioPaxObjectProperty(String name, String value) {
			this(name, value, "");
		}
		private BioPaxObjectProperty(String name, String value, BioPaxObject bioPaxObject) {
			this(name, value, "");
			this.bioPaxObject = bioPaxObject;
		}
		private BioPaxObjectProperty(String name, String value, BioPaxObject bioPaxObject, String tooltip) {
			this(name, value, tooltip);
			this.bioPaxObject = bioPaxObject;
		}
		private BioPaxObjectProperty(String name, String value, BioModelEntityObject bioModelEntityObject) {
			this(name, value, "");
			this.bioModelEntityObject = bioModelEntityObject;
		}
		private BioPaxObjectProperty(String name, String value, BioModelEntityObject bioModelEntityObject, String tooltip) {
			this(name, value, tooltip);
			this.bioModelEntityObject = bioModelEntityObject;
		}
		private void setDetails(String details) {
			if(details != null) {
				this.details = details;
			}
		}
		private String getDetails() {
			return details;
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
			refreshInterface();
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
	AsynchClientTask finishLookupTask = new AsynchClientTask("init lookup...",AsynchClientTask.TASKTYPE_SWING_NONBLOCKING,false) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			try{
				ArrayList<String> formalNames = (ArrayList<String>)hashTable.get(FORMAL_NAMES_KEY);
				if(formalNames != null){
					entity.setFormalNames(formalNames);
				}else if(entity.getxRef() != null && entity.getxRef().size() > 0){
					String str = "";
					for (int i = 0; i < ((Entity)BioPaxObjectPropertiesPanel.this.bioPaxObject).getxRef().size(); i++) {
						str+= (i>0?"\n":"")+entity.getxRef().get(i).getDb()+":"+entity.getxRef().get(i).getId();
					}
					throw new Exception("Formal name lookup not yet implemented using:\n"+str);
				}else{
					throw new Exception("No cross-references available to lookup formal name for "+entity.getName().get(0));
				}
			}finally{
				lookupRemove(entity);
				refreshInterface();
				table.setRowSelectionInterval(tableRow, tableRow);
			}
		}
	};
	ClientTaskDispatcher.dispatch(null, new Hashtable<String, Object>(), new AsynchClientTask[] {initLookupTask,lookupTask,finishLookupTask}, null, false, false, false, null, false);
	
}

private static String FormatDetails(BioPaxObjectProperty property) {
	if(property.name.equalsIgnoreCase("comment")) {		// format comments
		String text = property.value;
		return PathwayMapping.FormatDetails(text);
	}
	return property.value;
}

private void initialize() {
	try {
		table = new ScrollTable();
		tableModel = new BioPaxObjectPropertiesTableModel(table);
		table.setModel(tableModel);
		
		details = new JTextPane();
		details.setContentType("text/html");
		details.setEditable(false);
		JScrollPane scrl = new JScrollPane(details); 
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					BioPaxObjectProperty property = tableModel.getValueAt(table.getSelectedRow());
					if(property != null) {
						final String htmlStart = "<html><font face = \"Arial\"><font size =\"-2\">";
						final String htmlEnd = "</font></font></html>";
						if(!property.getDetails().isEmpty()) {
							details.setText(htmlStart + property.getDetails() + htmlEnd);
						} else if((property.value != null) && !property.value.isEmpty()) {
							String text = FormatDetails(property);
							details.setText(htmlStart + text + htmlEnd);
						} else {
							details.setText(htmlStart + "row: " + table.getSelectedRow() + ", col: " + table.getSelectedColumn() + htmlEnd);
						}
					}
				}
			}
		});
		
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, table.getEnclosingScrollPane(), scrl);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);
		
		Dimension minimumSize = new Dimension(100, 50);		//provide minimum sizes for the two components in the split pane
		table.getEnclosingScrollPane().setMinimumSize(minimumSize);
		scrl.setMinimumSize(minimumSize);

		
		setLayout(new BorderLayout());
//		add(table.getEnclosingScrollPane(), BorderLayout.CENTER);
//		add(details, BorderLayout.CENTER);
		add(splitPane, BorderLayout.CENTER);
		setBackground(Color.white);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
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
								if(bioModelEntityObject instanceof SpeciesContext) {
									selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram),new Object[]{bioModelEntityObject});
								} else if(bioModelEntityObject instanceof MolecularType) {
									selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.MOLECULAR_TYPES_NODE, ActiveViewID.species_definitions),new Object[]{bioModelEntityObject});
								} else if(bioModelEntityObject instanceof SimpleReaction) {
									selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram),new Object[]{bioModelEntityObject});
								}
							}else if(((Entity)BioPaxObjectPropertiesPanel.this.bioPaxObject).getFormalNames() == null || ((Entity)BioPaxObjectPropertiesPanel.this.bioPaxObject).getFormalNames().size() == 0){
								lookupFormalName(crow);
							}
						} else if (bioPaxObject instanceof Xref) { // if xRef, get url
							String url = ((Xref) bioPaxObject).getURL();
							DialogUtils.browserLauncher(BioPaxObjectPropertiesPanel.this, url, "Wrong URL.");
						} else if (bioPaxObject instanceof SBEntity) {		// TODO: kineticLaw
							SBEntity sbE = (SBEntity)bioPaxObject;
							if(sbE.getID().contains("kineticLaw")) {
//								String url = "http://sabio.h-its.org/sabioRestWebServices/kineticLaws/" + sbE.getID().substring(sbE.getID().indexOf("kineticLaw") + 10);
								String url = BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.SABIO_DIRECT_IFRAME_URL) + sbE.getID().substring(sbE.getID().indexOf("kineticLaw") + 10);
								DialogUtils.browserLauncher(BioPaxObjectPropertiesPanel.this, url, "Wrong URL.");
							}
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
				BioPaxObjectProperty property = tableModel.getValueAt(row);
				if(!property.tooltip.isEmpty()) {
					setToolTipText(property.tooltip);
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
	BioModel oldValue = bioModel;
	if (oldValue != null) {
//		oldValue.getModel().removePropertyChangeListener(this);		// doesn't appear that we need here to listen to the model
		oldValue.getRelationshipModel().removeRelationShipListener(this);
	}
	bioModel = newValue;
	if (newValue != null) {
//		newValue.getModel().addPropertyChangeListener(this);
		newValue.getRelationshipModel().addRelationShipListener(this);
	}
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
	details.setText("");
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
		String sType = beObject.getDisplayType();
		propertyList.add(new BioPaxObjectProperty("Linked physiology object (" + sType + ")", beObject.getName(), beObject));
	}

	if(entity instanceof PhysicalEntity){	// ------------------------ PHYSICAL ENTITY -----------------------
		PhysicalEntity physicalEntity = (PhysicalEntity)entity;
		// physicalEntity::feature (***ignored***)
		// physicalEntity::memberPhysicalEntity (***ignored***)
		// physicalEntity::notFeature (***ignored***)

// TODO:  extract the kinetic law, then the SBEntities, then the measurables, units, aso
		boolean isReactionParticipant = BioPAXUtil.isReactionParticipant(physicalEntity, bioModel.getPathwayModel());
		String role = "";
		if(BioPAXUtil.isController(physicalEntity, bioModel.getPathwayModel())) {
			role += "Controller";
			if(isReactionParticipant) {
				role += ", Participant";
			}
		} else if (isReactionParticipant) {
			role += "Participant";
		}
		if(!role.isEmpty()) {
			propertyList.add(new BioPaxObjectProperty("Role(s)", role));
		}
				
		if(!(physicalEntity instanceof SmallMolecule)){
			// physicalEntity::cellular location
			CellularLocationVocabulary cellularLocation = physicalEntity.getCellularLocation();
			if (cellularLocation!=null && cellularLocation.getTerm() != null && cellularLocation.getTerm().size() > 0){
				propertyList.add(new BioPaxObjectProperty("Cellular Location", cellularLocation.getTerm().get(0),cellularLocation));
			}else if (entity.getName() != null && entity.getName().size()>1){
				String location  = entity.getName().get(1);
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
				//String tooltip = "<html>how many of these 12 M <br>average size  1.12345*E12 nm <br>temperature 37 degrees Celsius</html>";
				String tooltip = "";
				propertyList.add(new BioPaxObjectProperty("Controlled by", str, interaction, tooltip));
			}
		}
		// get the kinetic laws (if any)
		Set<Control> controls = BioPAXUtil.getControlsOfInteraction(interaction, bioModel.getPathwayModel());
		for(Control control : controls) {
			ArrayList<SBEntity> sbEntities = control.getSBSubEntity();
			for(SBEntity sbE : sbEntities) {
				// the following if clause may not be needed, we KNOW that 
				// the only SBSubEntities allowed in a control are kinetic laws
				if(sbE.getID().contains("kineticLaw")) {
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
					String sDetails = "";
					ArrayList<SBVocabulary> sbTerms = sbE.getSBTerm();
					for(SBVocabulary sbv : sbTerms) {
						String str1 = sbv.getID();		// type of kinetic law
						str1 = str1.substring(str1.lastIndexOf('#')+1);
						System.out.println(str1);
						SBOTerm sboT = SBOListEx.sboMap.get(str1);
						sDetails += "<font color=\"#660000\"><b>" + sboT.getName() + "</b></font>" + "  " + 
						"<font color=\"#006600\">" + sboT.getDescription() + "</font>";
						sDetails += "<br>";
					}
					
					ArrayList<SBEntity> klProperties = sbE.getSBSubEntity();
					for(SBEntity klProperty : klProperties) {
						if(klProperty instanceof SBMeasurable) {
							SBMeasurable m  = (SBMeasurable)klProperty;
							String str1 = "";
							String str2 = "";
							String str3 = "";
							if(m.hasTerm()) {
								str1 += m.extractSBOTermAsString();
							}
							if(m.hasNumber()) {
								str2 += m.getNumber().get(0);
							}
							if(m.hasUnit()) {
								str3 += m.extractSBOUnitAsString();
							}
							// str1 is an SBO id, for example "SBO:0000064"
							SBOTerm sboT = SBOListEx.sboMap.get(str1);
							sDetails += sboT.getSymbol() + "   (" + sboT.getName() + ")" + 
							"<font color=\"#660000\"><b>" + str2 + str3 + "</b></font>" + "  " + 
							"<font color=\"#006600\">" + sboT.getDescription() + "</font>";
						} else {
							sDetails = klProperty.getIDShort() + "  " + klProperty.getTypeLabel();
						}
						sDetails += "<br>";
					}
					// String tooltip = "<html>how many of these 12 M <br>average size  1.12345*E12 nm <br>temperature 37 degrees Celsius</html>";
					String tooltip = "";
					BioPaxObjectProperty bpop = new BioPaxObjectProperty("Kinetic Law" + str, sbE.getID(), sbE, tooltip);
					bpop.setDetails(sDetails);
					propertyList.add(bpop);									
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

	// entity::comments
	for (String comment : entity.getComments()){
		propertyList.add(new BioPaxObjectProperty("Comment", comment));
	}
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
	Set<Control> controls = BioPAXUtil.getControlsOfInteraction(interaction, bioModel.getPathwayModel());
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

@Override
public void relationshipChanged(RelationshipEvent event) {
	if (bioPaxObject != null && event.getRelationshipObject() != null && event.getRelationshipObject().getBioPaxObject() == bioPaxObject) {
		refreshInterface();
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
