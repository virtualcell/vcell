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

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.Complex;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.Entity;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Stoichiometry;
import org.vcell.relationship.RelationshipEvent;
import org.vcell.relationship.RelationshipListener;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.EditorScrollTable.DefaultScrollTableComboBoxEditor;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
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
	public static final int iColID = 5;
	public static final int iColLocation = 6;
	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;

	private BioModel bioModel;
	private List<BioPaxObject> bioPaxObjects;
	private ArrayList<BioPaxObject> convertedBPObjects;
	private ArrayList<ConversionTableRow> allPathwayObjectList;
	//	private boolean bShowLinkOnly = false;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;

	private DefaultScrollTableComboBoxEditor defaultScrollTableComboBoxEditor = null;
	public BioModelEditorConversionTableModel(EditorScrollTable table) {
		super(table, new String[] {
				"Interaction", "Type", "Entity Name", "Entity Type", 
				"Stoich.\nCoef.", "  ID  ", "Location/Compartment"});
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
				return conversionTableRow.interactionName();
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
		return ((iCol == iColStoich && editable) || iCol == iColLocation);
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
						conversonTableRow.setId(((String)valueNew).trim());
					else
						conversonTableRow.setId((changeID(((String)valueNew).trim())));
				}
				break;
			}
			case iColLocation: {
				if(valueNew instanceof Structure) {
					ConversionTableRow conversonTableRow = getValueAt(iRow);
					conversonTableRow.setLocation(((Structure)valueNew).getName().trim());
					// update id value
					setValueAt(getLabel(conversonTableRow.getBioPaxObject())+"_"+((Structure)valueNew).getName().trim(),iRow,iColID);
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
		    		int c  = o1.interactionName().compareToIgnoreCase(o2.interactionName());
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
				return "unnamed";
			}
		}else if (bpObject instanceof PhysicalEntity){
			PhysicalEntity physicalEntity =(PhysicalEntity)bpObject;
			if (physicalEntity.getName().size()>0){
				return physicalEntity.getName().get(0);
			}else{
				return "unnamed";
			}
		}else{
			return bpObject.getID();
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
	
	private void refreshData() {
		if (bioModel == null || bioModel.getPathwayModel() == null || bioPaxObjects == null) {
			setData(null);
			return;
		}
		
		// function I :: get selected objects only
		// create ConversionTableRow objects
		allPathwayObjectList = new ArrayList<ConversionTableRow>();
//		HashSet<BioPaxObject> newSelectedObjects = new HashSet<BioPaxObject>();
		
		for(BioPaxObject bpo : bioPaxObjects){
		  if(bpo instanceof Conversion){
			  if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).size() == 0){
				Conversion interaction = (Conversion)bpo;
				ArrayList<String> nameList = interaction.getName();
				String interactionName = nameList.isEmpty() ? "[no name]" : nameList.get(0);
				ConversionTableRow newConversionTableRow = createTableRow(interaction, interactionName,
						"Conversion", 1.0 , null);
				allPathwayObjectList.add(newConversionTableRow);
//				newSelectedObjects.add(interaction);
				ArrayList<Stoichiometry> stoichiometryList =  interaction.getParticipantStoichiometry();
				// stoichiometryMap problem: 
				//			how to deal with the case that the same object occurs on both left and right sides
				HashMap <PhysicalEntity, Double> stoichiometryMap = createStoichiometryMap(stoichiometryList);
				// reactant
				for(BioPaxObject bpObject1: interaction.getLeft()){
					Double stoich = 1.0;
					if(stoichiometryMap.get((PhysicalEntity)bpObject1) != null){
						stoich = stoichiometryMap.get((PhysicalEntity)bpObject1);
					}
					ConversionTableRow conversionTableRow;
					if(bioModel.getRelationshipModel().getRelationshipObjects(bpObject1).isEmpty()){
						 conversionTableRow = createTableRow(bpObject1, interactionName,
							"Reactant", stoich , null);
					}else{
						 conversionTableRow = createTableRow(bpObject1, interactionName,
								"Reactant", stoich , bioModel.getRelationshipModel().getRelationshipObjects(bpObject1).iterator().next());
					}
					allPathwayObjectList.add(conversionTableRow);
//					newSelectedObjects.add(bpObject1);
				}
				// product
				for(BioPaxObject bpObject1: interaction.getRight()){
					Double stoich = 1.0;
					if(stoichiometryMap.get((PhysicalEntity)bpObject1) != null){
						stoich = stoichiometryMap.get((PhysicalEntity)bpObject1);
					}
					ConversionTableRow conversionTableRow;
					if(bioModel.getRelationshipModel().getRelationshipObjects(bpObject1).isEmpty()){
						conversionTableRow = createTableRow(bpObject1, interactionName,
								"Product", stoich , null);
					}else{
						conversionTableRow = createTableRow(bpObject1, interactionName,
								"Product", stoich , bioModel.getRelationshipModel().getRelationshipObjects(bpObject1).iterator().next());
					}
					allPathwayObjectList.add(conversionTableRow);
//					newSelectedObjects.add(bpObject1);
				}
				// catalyst
				for(BioPaxObject bpObject: bioModel.getPathwayModel().getBiopaxObjects()){
					if(bpObject instanceof Catalysis){
						if(((Catalysis) bpObject).getControlledInteraction() == interaction){
							for(PhysicalEntity pe : ((Catalysis) bpObject).getPhysicalControllers()){
								ConversionTableRow conversionTableRow;
								if(bioModel.getRelationshipModel().getRelationshipObjects(pe).isEmpty()){
									conversionTableRow = createTableRow(pe, interactionName,
											"Catalyst", 1.0 , null);
								}else{
									conversionTableRow = createTableRow(pe, interactionName,
											"Catalyst", 1.0 , bioModel.getRelationshipModel().getRelationshipObjects(pe).iterator().next());
								} 
								allPathwayObjectList.add(conversionTableRow);
//								newSelectedObjects.add(bpObject);
							}
						}
					}
				}
				
			  }else{

			  }
		  }else if(bpo instanceof PhysicalEntity){
			  if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).size() == 0){
				  PhysicalEntity physicalEntityObject = (PhysicalEntity)bpo;
				  ConversionTableRow conversionTableRow = createTableRow(physicalEntityObject, "", "", 1.0 , null);
					allPathwayObjectList.add(conversionTableRow);
//					newSelectedObjects.add(physicalEntityObject);
			  }else{
			  }
		  }else if(bpo instanceof Catalysis){
			  for(PhysicalEntity pe : ((Catalysis) bpo).getPhysicalControllers()){
					ConversionTableRow conversionTableRow;
					if(bioModel.getRelationshipModel().getRelationshipObjects(bpo).isEmpty()){
						conversionTableRow = createTableRow(pe, "",
								"Catalyst", 1.0 , null);
					}else{
						conversionTableRow = createTableRow(pe, "",
								"Catalyst", 1.0 , bioModel.getRelationshipModel().getRelationshipObjects(bpo).iterator().next());
					} 
					allPathwayObjectList.add(conversionTableRow);
//					newSelectedObjects.add(bpo);
				}
			  for(Pathway pathway : ((Catalysis) bpo).getPathwayControllers()){
				  // TODO
			  }
		  }
		}
		
		
		/*
		// function II :: get related interactions for all selected pathway objects
		// create ConversionTableRow objects

		ArrayList<ConversionTableRow> allPathwayObjectList = new ArrayList<ConversionTableRow>();
		HashSet<BioPaxObject> newSelectedObjects = new HashSet<BioPaxObject>();
		for(Conversion interaction : getRelatedInteraction()){
			newSelectedObjects.add(interaction);
			ArrayList<Stoichiometry> stoichiometryList =  interaction.getParticipantStoichiometry();
			HashMap <PhysicalEntity, Double> stoichiometryMap = createStoichiometryMap(stoichiometryList);
			// reactant
			for(BioPaxObject bpObject1: interaction.getLeft()){
				Double stoich = 1.0;
				if(stoichiometryMap.get((PhysicalEntity)bpObject1) != null){
					stoich = stoichiometryMap.get((PhysicalEntity)bpObject1);
				}
				ConversionTableRow conversionTableRow = createTableRow(bpObject1, interaction.getName().get(0),
						"Reactant", stoich );
				allPathwayObjectList.add(conversionTableRow);
				newSelectedObjects.add(bpObject1);
				if(bpObject1 instanceof Complex){
					for(PhysicalEntity pe : ((Complex)bpObject1).getComponents()){
						ConversionTableRow cTableRow = createTableRow(bpObject1, interaction.getName().get(0),
						"Reactant", stoich );
						allPathwayObjectList.add(cTableRow);
//						newSelectedObjects.add(pe);
					}
				}
			}
			// product
			for(BioPaxObject bpObject1: interaction.getRight()){
				Double stoich = 1.0;
				if(stoichiometryMap.get((PhysicalEntity)bpObject1) != null){
					stoich = stoichiometryMap.get((PhysicalEntity)bpObject1);
				}
				ConversionTableRow conversionTableRow = createTableRow(bpObject1, interaction.getName().get(0),
						"Product",  stoich );
				allPathwayObjectList.add(conversionTableRow);
				newSelectedObjects.add(bpObject1);
				if(bpObject1 instanceof Complex){
					for(PhysicalEntity pe : ((Complex)bpObject1).getComponents()){
						ConversionTableRow cTableRow = createTableRow(bpObject1, interaction.getName().get(0),
						"Product", stoich );
						allPathwayObjectList.add(cTableRow);
//						newSelectedObjects.add(pe);
					}
				}
			}
			// catalyst
			for(BioPaxObject bpObject: bioModel.getPathwayModel().getBiopaxObjects()){
				if(bpObject instanceof Catalysis){
					if(((Catalysis) bpObject).getControlledInteraction() == interaction){
						ConversionTableRow conversionTableRow = createTableRow(bpObject, interaction.getName().get(0),
								"Catalyst", 1.0 );
						allPathwayObjectList.add(conversionTableRow);
						newSelectedObjects.add(bpObject);
					}
				}
			}
		}
		*/
		

//		convertedBPObjects = new ArrayList<BioPaxObject>();
//		for(BioPaxObject bp : newSelectedObjects){
//			convertedBPObjects.add(bp);
//		}
		
		// apply text search function for particular columns
		ArrayList<ConversionTableRow> pathwayObjectList = new ArrayList<ConversionTableRow>();
		if (searchText == null || searchText.length() == 0) {
			pathwayObjectList.addAll(allPathwayObjectList);
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();
			for (ConversionTableRow rs : allPathwayObjectList){
				BioPaxObject bpObject = rs.getBioPaxObject();
				if (rs.interactionName().toLowerCase().contains(lowerCaseSearchText)
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
	
	private ConversionTableRow createTableRow(BioPaxObject bpObject, String interactionName, String participantType, 
			double  stoich, RelationshipObject relationshipObject) {
		String location = "";
		ConversionTableRow conversionTableRow = new ConversionTableRow(bpObject);
		
		conversionTableRow.setInteractionName(interactionName);
		conversionTableRow.setParticipantType(participantType);
		
		// stoichiometry and location
		if(participantType.equals("Reactant") || participantType.equals("Product") ){
			//stoichiometry
			if(stoich == 0)
				conversionTableRow.setStoich(stoich);
			else
				conversionTableRow.setStoich(1.0);
			// location
			if(((PhysicalEntity)bpObject).getCellularLocation() != null)
				location = ((PhysicalEntity)bpObject).getCellularLocation().getTerm().get(0);
			else
				location = bioModel.getModel().getStructures()[0].getName();
			conversionTableRow.setLocation(location);
		}else{
			conversionTableRow.setStoich(1.0);
			// location
			if(!participantType.equals("Catalyst") && 
					(bpObject instanceof PhysicalEntity && ((PhysicalEntity)bpObject).getCellularLocation() != null))
				location = ((PhysicalEntity)bpObject).getCellularLocation().getTerm().get(0);
			else
				location = bioModel.getModel().getStructures()[0].getName();
			conversionTableRow.setLocation(location);
		}
		// id
		if(relationshipObject == null){
			if(bpObject instanceof Entity) {
				ArrayList<String> nameList = ((Entity)bpObject).getName();
				if(!nameList.isEmpty()) {
					String id = (getSafetyName(nameList.get(0))+"_"+location).trim();
					if(isValid(id))
						conversionTableRow.setId(id);
					else
						conversionTableRow.setId(changeID(id));		
				}else{
					conversionTableRow.setId("O_"+location);
				}
			}
		}else{
			if(relationshipObject.getBioModelEntityObject().getStructure().getName().equalsIgnoreCase(location)){
				// the linked bmObject with the same location will be used
				conversionTableRow.setId(relationshipObject.getBioModelEntityObject().getName());
			}else{
				// a new bmObject will be created if no linked bmObject in the same location
				if(bpObject instanceof Entity) {
					ArrayList<String> nameList = ((Entity)bpObject).getName();
					if(!nameList.isEmpty()) {
						String id = (getSafetyName(nameList.get(0))+"_"+location).trim();
						if(isValid(id))
							conversionTableRow.setId(id);
						else
							conversionTableRow.setId(changeID(id));		
					}else{
						conversionTableRow.setId("O_"+location);
					}
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
	
	public ArrayList<BioPaxObject> getBioPaxObjects(){
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
	
	private HashSet<Conversion> getRelatedInteraction(){
		HashSet<Conversion> interactionSet = new HashSet<Conversion>();
		HashMap<BioPaxObject, HashSet<BioPaxObject>> parentMap = createParentMap();
		HashMap<BioPaxObject, HashSet<BioPaxObject>> complexMap = createComplexMap();
		for(BioPaxObject bpObject : bioPaxObjects){
			if(bpObject instanceof Conversion){
				interactionSet.add((Conversion)bpObject);
			}else if(bpObject instanceof PhysicalEntity ){
				if(parentMap.get(bpObject) != null)
					for(BioPaxObject bp : parentMap.get(bpObject)){
						interactionSet.add((Conversion)bp);
					}
				if(complexMap.get(bpObject) != null)
					for(BioPaxObject bp : complexMap.get(bpObject)){
						for(PhysicalEntity pe : ((Complex)bp).getComponents()){
							if(parentMap.get(pe) != null)
								for(BioPaxObject bpo : parentMap.get(pe)){
									interactionSet.add((Conversion)bpo);
								}
						}
					}
			}
		}
		return interactionSet;
	} 
	
	private HashMap<BioPaxObject, HashSet<BioPaxObject>> createComplexMap(){
		HashMap<BioPaxObject, HashSet<BioPaxObject>> complexMap = new HashMap<BioPaxObject, HashSet<BioPaxObject>>();
		if(bioModel != null && bioModel.getPathwayModel() != null && bioModel.getPathwayModel().getBiopaxObjects() != null){
			for(BioPaxObject bpObject: bioModel.getPathwayModel().getBiopaxObjects()){
				if(bpObject instanceof Complex){
					for(BioPaxObject bp : ((Complex)bpObject).getComponents()){
						if(complexMap.get(bp) == null){
							HashSet <BioPaxObject> complexSet = new HashSet <BioPaxObject>();
							complexSet.add(bpObject);
							complexMap.put(bp, complexSet);
						}else{
							complexMap.get(bp).add(bpObject);
						}
					}
				}
			}
		}
	return complexMap;
	}
	
	private HashMap<BioPaxObject, HashSet<BioPaxObject>> createParentMap(){
		HashMap<BioPaxObject, HashSet<BioPaxObject>> parentMap = new HashMap<BioPaxObject, HashSet<BioPaxObject>>();
		HashMap<BioPaxObject, HashSet<BioPaxObject>> complexMap = new HashMap<BioPaxObject, HashSet<BioPaxObject>>();
		if(bioModel != null && bioModel.getPathwayModel() != null && bioModel.getPathwayModel().getBiopaxObjects() != null){
			for(BioPaxObject bpObject: bioModel.getPathwayModel().getBiopaxObjects()){
				if (bpObject instanceof Conversion){
					for(InteractionParticipant ipObject: ((Conversion) bpObject).getParticipants()){
						if(parentMap.get(ipObject.getPhysicalEntity()) == null){
							HashSet <BioPaxObject> parentSet = new HashSet <BioPaxObject>();
							parentSet.add(bpObject);
							parentMap.put(ipObject.getPhysicalEntity(), parentSet);
						}else{
							parentMap.get(ipObject.getPhysicalEntity()).add(bpObject);
						}
					}
				}else if(bpObject instanceof Catalysis){
					for(InteractionParticipant ipObject : ((Catalysis)bpObject).getParticipants()){
						if(parentMap.get(ipObject.getPhysicalEntity()) == null){
							HashSet <BioPaxObject> parentSet = new HashSet <BioPaxObject>();
							parentSet.add(((Catalysis)bpObject).getControlledInteraction());
							parentMap.put(ipObject.getPhysicalEntity(), parentSet);
						}else{
							parentMap.get(ipObject.getPhysicalEntity()).add(((Catalysis)bpObject).getControlledInteraction());
						}
					}
				}
			}
		}
		return parentMap;
	}
	
//	public void setShowLinkOnly(boolean newValue) {
//		if (this.bShowLinkOnly == newValue) {
//			return;
//		}
//		bShowLinkOnly = newValue;
//		refreshData();
//	}

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
	
	//convert the name of biopax object to safety vcell object name
	private static String getSafetyName(String oldValue){
		return TokenMangler.fixTokenStrict(oldValue, 60);
	} 
	
}
