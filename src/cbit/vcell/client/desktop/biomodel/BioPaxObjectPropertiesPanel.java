package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Complex;
import org.vcell.pathway.Entity;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Xref;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;


@SuppressWarnings("serial")
public class BioPaxObjectPropertiesPanel extends DocumentEditorSubPanel {
	
	private BioPaxObject bioPaxObject = null;	
	private BioModel bioModel = null;
	private ScrollTable table = null;
	private BioPaxObjectPropertiesTableModel tableModel = null;
	
	private static class BioPaxObjectProperty {
		String name;
		String value;
		private BioPaxObjectProperty(String name, String value) {
			super();
			this.name = name;
			this.value = value;
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
	
	if(bioPaxObject instanceof PhysicalEntity){
		if(((PhysicalEntity)bioPaxObject).getName() != null){
			propertyList.add(new BioPaxObjectProperty("name", ((PhysicalEntity)bioPaxObject).getName().get(0)));
			if(((PhysicalEntity)bioPaxObject).getName().size()>1){
				if(((PhysicalEntity)bioPaxObject).getTypeLabel().equals("Complex")){ 
					String location  = ((PhysicalEntity)bioPaxObject).getName().get(1);
					location = location.substring(location.indexOf("[")+1, location.indexOf("]"));
					propertyList.add(new BioPaxObjectProperty("Cellular Location", ((PhysicalEntity)bioPaxObject).getName().get(0)));
				}
			}
		}
			
		propertyList.add(new BioPaxObjectProperty("Type", bioPaxObject.getTypeLabel()));
		if(((PhysicalEntity)bioPaxObject).getTypeLabel().equals("Complex")){
			Complex complex = (Complex) bioPaxObject;
			for(PhysicalEntity pe : complex.getComponents()){
				propertyList.add(new BioPaxObjectProperty("Component", pe.getName().get(0)));
			}
		}	
	}else if(bioPaxObject instanceof Interaction){
		ArrayList<String> nameList = ((Interaction)bioPaxObject).getName();
		if(nameList.size() > 0) {
			propertyList.add(new BioPaxObjectProperty("name", nameList.get(0)));
		}
		propertyList.add(new BioPaxObjectProperty("Type", bioPaxObject.getTypeLabel()));
	}
	if (bioPaxObject instanceof Entity) {
		ArrayList<Xref> xrefList = ((Entity) bioPaxObject).getxRef();
		for (Xref xref : xrefList) {
			propertyList.add(new BioPaxObjectProperty("Xref", xref.getDb() + ":" + xref.getId()));
		}
	}
	tableModel.setData(propertyList);
	
}

}

