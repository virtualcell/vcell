package org.vcell.sybil.gui.bpimport;
// set up the function for sortable table

import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;

class EntitySelectionTableRow {
	protected NamedThing thing;
	protected RDFType type;
	protected Boolean selected = new Boolean(false);
	
	public EntitySelectionTableRow(NamedThing thing, RDFType typeNew) {
		this.thing = thing;
		this.type = typeNew;
	}
	
	public Boolean selected() { return selected; }
	public void setSelected(Boolean selectedNew) { selected = selectedNew; }
	public NamedThing thing() { return thing; }
	public RDFType type() { return type; }
	
}
