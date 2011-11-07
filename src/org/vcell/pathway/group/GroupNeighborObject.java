/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.group;

import org.vcell.pathway.BioPaxObject;

public class GroupNeighborObject {
	private BioPaxObject bioPaxObject;
	private String type;
	
	public BioPaxObject getBioPaxObject(){
		return bioPaxObject;
	}
	public void setBioPaxObject(BioPaxObject bpObject){
		bioPaxObject = bpObject;
	}
	
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type = type;
	}
}
