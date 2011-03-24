package cbit.vcell.client.desktop.biomodel;

import cbit.vcell.model.BioModelEntityObject;

public class BioPaxRelationshipTableRow {
	protected BioModelEntityObject bioModelEntityObject;
	protected Boolean selected = new Boolean(false);
	
	public BioPaxRelationshipTableRow(BioModelEntityObject bpObject) {
		this.bioModelEntityObject = bpObject;
	}
	
	public Boolean selected() {
		return selected;
	}
	public void setSelected(Boolean selectedNew) {
		selected = selectedNew;
	}
	public BioModelEntityObject getBioModelEntityObject(){
		return bioModelEntityObject;
	}
}
