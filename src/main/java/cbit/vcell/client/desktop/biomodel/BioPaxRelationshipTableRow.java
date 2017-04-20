/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
