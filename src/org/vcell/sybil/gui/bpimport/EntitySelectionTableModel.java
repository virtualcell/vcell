package org.vcell.sybil.gui.bpimport;

/*   EntitySelectionTableModel  --- by Oliver Ruebenacker, UCHC --- November (?) 2008 to December 2009
 *   Model for table to select entities from an SBBox
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.models.sbbox.factories.ThingFactory;
import org.vcell.sybil.models.sbbox.factories.ThingFactory.ThingWithType;
import com.hp.hpl.jena.rdf.model.Resource;

public class EntitySelectionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8218151513808885665L;

	public static final int colCount = 3;
	public static final int iColSelected = 0;
	public static final int iColEntity = 1;
	public static final int iColType = 2;
	
	public static class Row {
		protected NamedThing thing;
		protected RDFType type;
		protected Boolean selected = new Boolean(false);
		
		public Row(NamedThing thing, RDFType typeNew) {
			this.thing = thing;
			this.type = typeNew;
		}
		
		public Boolean selected() { return selected; }
		public void setSelected(Boolean selectedNew) { selected = selectedNew; }
		public NamedThing thing() { return thing; }
		public RDFType type() { return type; }
		
	}

	protected Vector<Row> rows = new Vector<Row>();

	public EntitySelectionTableModel(SBBox box) {
		addRows(box.factories().process());
		addRows(box.factories().substance());
	}
	
	protected <T extends NamedThing> void addRows(ThingFactory<T> factory) {
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
		for(T thing: thingMap.keySet()) {
			ThingWithType<? extends T> thingWithType = thingMap.get(thing);
			if(thingWithType != null) {
				RDFType type = thingWithType.type();
				rows.add(new Row(thing, type));				
			}
		}
	}
	
	// TODO switch to things
	public Set<Resource> selectedEntities() {
		HashSet<Resource> selectedEntities = new HashSet<Resource>();
		for(Row row : rows) { if(row.selected()) { selectedEntities.add(row.thing().resource()); } }
		return selectedEntities;
	}
	
	public int getColumnCount() { return colCount; }
	public int getRowCount() { return rows.size(); }

	public Class<?> getColumnClass(int iCol) {
		if(iCol == iColSelected) { return Boolean.class; }
		else { return String.class; }
	}
	
	public String getColumnName(int iCol) {
		switch(iCol) {
		case iColSelected: return "Get?";
		case iColEntity: return "Entity Name";
		case iColType: return "Type";
		default: return null;
		}
	}
	
	public Object getValueAt(int iRow, int iCol) {
		switch(iCol) {
		case iColSelected: return rows.get(iRow).selected();
		case iColEntity: return rows.get(iRow).thing().label();
		case iColType: return rows.get(iRow).type().label();
		default: return null;
		}
	}
	
	public boolean isCellEditable(int iRow, int iCol) {
		return iCol == iColSelected;
	}
	
	public void setValueAt(Object valueNew, int iRow, int iCol) {
		if(valueNew instanceof Boolean && iCol == iColSelected) {
			rows.get(iRow).setSelected((Boolean) valueNew);
		}
	}
	
}
