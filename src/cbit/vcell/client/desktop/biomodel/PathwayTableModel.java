package cbit.vcell.client.desktop.biomodel;

/*   EntitySelectionTableModel  --- by Oliver Ruebenacker, UCHC --- November (?) 2008 to December 2009
 *   Model for table to select entities from an SBBox
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;

@SuppressWarnings("serial")
public class PathwayTableModel extends DefaultSortTableModel<EntitySelectionTableRow> {


	public static final int colCount = 3;
	public static final int iColSelected = 0;
	public static final int iColEntity = 1;
	public static final int iColType = 2;
	
	// filtering variables 
	protected String searchText = null;
	//done

	private PathwayModel pathwayModel;

	public PathwayTableModel() {
		super(new String[] {"Select", "Entity Name", "Type"});
	}
	
	public void setPathwayModel(PathwayModel pathwayModel){
		if (this.pathwayModel == pathwayModel){
			return;
		}
		this.pathwayModel = pathwayModel;
		refreshData();
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
			entitySelectionTableRow.setSelected((Boolean) valueNew);
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
		if (pathwayModel == null) {
			setData(null);
			return;
		}
		
		List<EntitySelectionTableRow> allPathwayObjectList = new ArrayList<EntitySelectionTableRow>();
		for (BioPaxObject bpObject1 : pathwayModel.getBiopaxObjects()){
			if (bpObject1 instanceof PhysicalEntity || bpObject1 instanceof Conversion){
				allPathwayObjectList.add(new EntitySelectionTableRow(bpObject1));
			}
		}
		ArrayList<EntitySelectionTableRow> pathwayObjectList = new ArrayList<EntitySelectionTableRow>();
		for (EntitySelectionTableRow rs : allPathwayObjectList){
			BioPaxObject bpObject = rs.getBioPaxObject();
			if (searchText == null || searchText.length() == 0 
					|| getLabel(bpObject).toLowerCase().contains(searchText.toLowerCase())
					|| getType(bpObject).toLowerCase().contains(searchText.toLowerCase()) ) {
				pathwayObjectList.add(rs);
			}
		}
		setData(pathwayObjectList);
	}
}
