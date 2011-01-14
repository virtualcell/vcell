package cbit.vcell.client.desktop.biomodel;
// set up the function for sortable table

import org.vcell.pathway.BioPaxObject;

public class EntitySelectionTableRow {
	protected BioPaxObject bioPaxObject;
	protected Boolean selected = new Boolean(false);
	
	public EntitySelectionTableRow(BioPaxObject bpObject) {
		this.bioPaxObject = bpObject;
	}
	
	public Boolean selected() {
		return selected;
	}
	public void setSelected(Boolean selectedNew) {
		selected = selectedNew;
	}
	public BioPaxObject getBioPaxObject(){
		return bioPaxObject;
	}
}
