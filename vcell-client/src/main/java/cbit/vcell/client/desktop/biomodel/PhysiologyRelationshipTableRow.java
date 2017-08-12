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
// set up the function for sortable table

import org.vcell.pathway.BioPaxObject;

public class PhysiologyRelationshipTableRow {
	protected BioPaxObject bioPaxObject;
	protected Boolean selected = new Boolean(false);
	
	public PhysiologyRelationshipTableRow(BioPaxObject bpObject) {
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
