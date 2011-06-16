/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.bpimport;
// set up the function for sortable table

import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;

public class EntitySelectionTableRow {
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
