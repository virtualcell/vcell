package cbit.vcell.client.desktop.biomodel;

/*   EntitySelectionTableModel  --- by Oliver Ruebenacker, UCHC --- November (?) 2008 to December 2009
 *   Model for table to select entities from an SBBox
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.swing.JTable;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.relationship.RelationshipEvent;
import org.vcell.relationship.RelationshipListener;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;

@SuppressWarnings("serial")
public class BioModelEditorPathwayTableModel extends DefaultSortTableModel<EntitySelectionTableRow> implements PathwayListener, RelationshipListener {


	public static final int colCount = 3;
	public static final int iColSelected = 0;
	public static final int iColEntity = 1;
	public static final int iColType = 2;
	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;
	//done

	private BioModel bioModel;
	private BioModelEntityObject bioModelEntityObject;
	private JTable ownerTable;
	private boolean bShowLinkOnly = false;

	public BioModelEditorPathwayTableModel(JTable table) {
		super(new String[] {"Link", "Entity Name", "Type"});
		this.ownerTable = table;
	}
	
	public Class<?> getColumnClass(int iCol) {
		if(iCol == iColSelected) { return Boolean.class; }
		else { return String.class; }
	}
	
	public Object getValueAt(int iRow, int iCol) {
		EntitySelectionTableRow entitySelectionTableRow = getValueAt(iRow);
		BioPaxObject bpObject = entitySelectionTableRow.getBioPaxObject();
		switch(iCol) {		
			case iColSelected:{
				return entitySelectionTableRow.selected();
			}
			case iColEntity:{
				return getLabel(bpObject);
			}
			case iColType:{
				return getType(bpObject);
			}
			default:{
				return null;
			}
		}
	}
	
	public boolean isCellEditable(int iRow, int iCol) {
		return iCol == iColSelected;
	}
	
	public void setValueAt(Object valueNew, int iRow, int iCol) {
		if(valueNew instanceof Boolean && iCol == iColSelected) {
			EntitySelectionTableRow entitySelectionTableRow = getValueAt(iRow);
			if ((Boolean)valueNew) { // if the row is checked, then add the link to relationshipModel
				RelationshipObject reObject = new RelationshipObject(bioModelEntityObject, entitySelectionTableRow.getBioPaxObject());
				bioModel.getRelationshipModel().addRelationshipObject(reObject);
			} else {// if the row is unchecked and the link is in the relationshipModel, 
				   // then remove the link from the relationshipModel
				for(RelationshipObject re: bioModel.getRelationshipModel().getRelationshipObjects()){
					if (re.getBioModelEntityObject() == bioModelEntityObject
							&& re.getBioPaxObject() == (entitySelectionTableRow.getBioPaxObject())){
						bioModel.getRelationshipModel().removeRelationshipObject(re);
					}
				}
			}			
		}
	}
	
	// generate the sortable table. Set up the functions for each column
	public Comparator<EntitySelectionTableRow> getComparator(final int col, final boolean ascending) {
		return new Comparator<EntitySelectionTableRow>() {
		    public int compare(EntitySelectionTableRow o1, EntitySelectionTableRow o2){
		    	if (col == iColSelected) {
		    		int c  = o1.selected().compareTo(o2.selected());
		    		return ascending ? c : -c;
		    	} else 

		    	if (col == iColEntity) {// only sortable on entity column
		    		int c  = getLabel(o1.getBioPaxObject()).compareToIgnoreCase(getLabel(o2.getBioPaxObject()));
		    		return ascending ? c : -c;
		    	} else 
		    		
		    	if (col == iColType) {
		    		int c  = getType(o1.getBioPaxObject()).compareToIgnoreCase(getType(o2.getBioPaxObject()));
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
		ArrayList<EntitySelectionTableRow> pathwayObjectList = new ArrayList<EntitySelectionTableRow>();
		if (bioModel != null) {		
			HashSet<RelationshipObject> relationshipObjects = null;
			if (bioModelEntityObject != null) {
				relationshipObjects = bioModel.getRelationshipModel().getRelationshipObjects(bioModelEntityObject);
			}
			List<EntitySelectionTableRow> allPathwayObjectList = new ArrayList<EntitySelectionTableRow>();
			for (BioPaxObject bpObject1 : bioModel.getPathwayModel().getBiopaxObjects()){
				if (bpObject1 instanceof PhysicalEntity && (bioModelEntityObject == null || bioModelEntityObject instanceof SpeciesContext)
						|| bpObject1 instanceof Conversion && (bioModelEntityObject == null || bioModelEntityObject instanceof ReactionStep)) {
					EntitySelectionTableRow entityRow = new EntitySelectionTableRow(bpObject1);
					if (relationshipObjects != null) {
						for (RelationshipObject ro : relationshipObjects) {
							if (ro.getBioPaxObject() == entityRow.getBioPaxObject()) {
								entityRow.setSelected(true);
								break;
							}
						}
					}
					if (!bShowLinkOnly || entityRow.selected) {
						allPathwayObjectList.add(entityRow);
					}
				}
			}
			
			for (EntitySelectionTableRow rs : allPathwayObjectList){
				BioPaxObject bpObject = rs.getBioPaxObject();
				if (searchText == null || searchText.length() == 0 
						|| getLabel(bpObject).toLowerCase().contains(searchText.toLowerCase())
						|| getType(bpObject).toLowerCase().contains(searchText.toLowerCase()) ) {
					pathwayObjectList.add(rs);
				}
			}
		}
		setData(pathwayObjectList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}

	public void setBioModel(BioModel newValue) {
		if (bioModel == newValue) {
			return;
		}
		BioModel oldValue = bioModel;
		if (oldValue != null) {
			oldValue.getPathwayModel().removePathwayListener(this);
			oldValue.getRelationshipModel().removeRelationShipListener(this);
		}
		bioModel = newValue;
		if (newValue != null) {
			newValue.getPathwayModel().addPathwayListener(this);
			newValue.getRelationshipModel().addRelationShipListener(this);
		}
	}

	public void setBioModelEntityObject(BioModelEntityObject newValue) {
		if (bioModelEntityObject == newValue) {
			return;
		}
		bioModelEntityObject = newValue;
		refreshData();
	}

	public void pathwayChanged(PathwayEvent event) {
		refreshData();		
	}
	
	public void setShowLinkOnly(boolean newValue) {
		if (this.bShowLinkOnly == newValue) {
			return;
		}
		bShowLinkOnly = newValue;
		refreshData();
	}

	public void relationshipChanged(RelationshipEvent event) {
		refreshData();		
	}
	
}
