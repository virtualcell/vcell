package org.vcell.sybil.gui.bpimport;

/*   EntitySelectionTableModel  --- by Oliver Ruebenacker, UCHC --- November (?) 2008 to December 2009
 *   Model for table to select entities from an SBBox
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.models.sbbox.factories.ThingFactory;
import org.vcell.sybil.models.sbbox.factories.ThingFactory.ThingWithType;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;

public class EntitySelectionTableModel extends DefaultSortTableModel<EntitySelectionTableRow> {

	private static final long serialVersionUID = 8218151513808885665L;

	public static final int colCount = 3;
	public static final int iColSelected = 0;
	public static final int iColEntity = 1;
	public static final int iColType = 2;


	public EntitySelectionTableModel(SBBox box) {
		setColumns(new String[] {"Get?", "Entity Name", "Type"});
		List<EntitySelectionTableRow> rowList = addRows(box.factories().processFactory());
		rowList.addAll(addRows(box.factories().substanceFactory()));
		setData(rowList);
	}
	
	protected  <T extends NamedThing> List<EntitySelectionTableRow> addRows(ThingFactory<T> factory) {
		Map<T, ThingWithType<? extends T>> thingMap = new HashMap<T, ThingWithType<? extends T>>();
		Map<RDFType, Integer> typeToRank = new HashMap<RDFType, Integer>();
		for(ThingWithType<? extends T> thingWithType : factory.openThingsWithTypes()) {
			T thing = thingWithType.thing();
			RDFType type = thingWithType.type();
			int typeRank = thingWithType.typeRank();
			typeToRank.put(type, new Integer(typeRank));
			ThingWithType<? extends T> thingWithType2 = thingMap.get(thing);
			if(thingWithType2 != null) {
				int typeRank2 = thingWithType2.typeRank();
				if(typeRank > typeRank2) { thingMap.put(thing, thingWithType); }
			} else {
				thingMap.put(thing, thingWithType);				
			}
		}
		List<EntitySelectionTableRow> rowList = new ArrayList<EntitySelectionTableRow>();
		for(T thing: thingMap.keySet()) {
			ThingWithType<? extends T> thingWithType = thingMap.get(thing);
			if(thingWithType != null) {
				RDFType type = thingWithType.type();
				rowList.add(new EntitySelectionTableRow(thing, type));				
			}
		}
		return rowList;
	}
	
	public Class<?> getColumnClass(int iCol) {
		if(iCol == iColSelected) { return Boolean.class; }
		else { return String.class; }
	}
	
//	public String getColumnName(int iCol) {
//		switch(iCol) {
//		case iColSelected: return "Get?";
//		case iColEntity: return "Entity Name";
//		case iColType: return "Type";
//		default: return null;
//		}
//	}
	
	public Object getValueAt(int iRow, int iCol) {
		EntitySelectionTableRow entitySelectionTableRow = getValueAt(iRow);
		switch(iCol) {		
		case iColSelected: return entitySelectionTableRow.selected();
		case iColEntity: return entitySelectionTableRow.thing().label();
		case iColType: return entitySelectionTableRow.type().label();
		default: return null;
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
		    		int c  = o1.thing().label().compareToIgnoreCase(o2.thing().label());
		    		return ascending ? c : -c;
		    	} else 
		    		
		    	if (col == iColType) {
		    		int c  = o1.type().label().compareTo(o2.type().label());
		    		return ascending ? c : -c;
		    	}

		    	return 0;
		    }
		};
	}
	
}
