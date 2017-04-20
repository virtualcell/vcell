/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class SequenceSite extends SequenceLocation {
	private String positionStatus;
	private Integer sequencePosition;
	
	public String getPositionStatus() {
		return positionStatus;
	}
	public Integer getSequencePosition() {
		return sequencePosition;
	}
	public void setPositionStatus(String positionStatus) {
		this.positionStatus = positionStatus;
	}
	public void setSequencePosition(Integer sequencePosition) {
		this.sequencePosition = sequencePosition;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "positionStatus",positionStatus,level);
		printInteger(sb, "sequencePosition",sequencePosition,level);
	}

}
