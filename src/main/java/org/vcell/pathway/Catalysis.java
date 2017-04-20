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
import java.util.List;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Catalysis extends Control {
	private String catalysisDirection;

	public String getCatalysisDirection() {
		return catalysisDirection;
	}
	public List<PhysicalEntity> getCofactors() {
		return getParticipantPhysicalEntities(InteractionParticipant.Type.COFACTOR);
	}

	public void setCatalysisDirection(String catalysisDirection) {
		this.catalysisDirection = catalysisDirection;
	}
	
	public void addCofactor(PhysicalEntity cofactor) {
		addPhysicalEntityAsParticipant(cofactor, InteractionParticipant.Type.COFACTOR);
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
	}
		
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"catalysisDirection",catalysisDirection,level);
		printObjects(sb,"physicalControllers",getCofactors(),level);
	}

}
