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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.relationship.PathwayMapping;

public class ConversionImpl extends InteractionImpl implements Conversion {

	private String conversionDirection;
	private ArrayList<Stoichiometry> participantStoichiometry = new ArrayList<Stoichiometry>();
	private Boolean spontaneous;

	public String getConversionDirection() {
		return conversionDirection;
	}
	public ArrayList<Stoichiometry> getParticipantStoichiometry() {
		return participantStoichiometry;
	}
	public Boolean getSpontaneous() {
		return spontaneous;
	}
	
	public List<PhysicalEntity> getLeft() {
		return getParticipantPhysicalEntities(InteractionParticipant.Type.LEFT);
	}
	
	public List<PhysicalEntity> getRight() {
		return getParticipantPhysicalEntities(InteractionParticipant.Type.RIGHT);
	}
	
	public void setConversionDirection(String conversionDirection) {
		this.conversionDirection = conversionDirection;
	}
	public void setParticipantStoichiometry(
			ArrayList<Stoichiometry> participantStoichiometry) {
		this.participantStoichiometry = participantStoichiometry;
	}
	public void setSpontaneous(Boolean spontaneous) {
		this.spontaneous = spontaneous;
	}
	public void addLeft(PhysicalEntity left) {
		addPhysicalEntityAsParticipant(left, InteractionParticipant.Type.LEFT);
	}
	public void addRight(PhysicalEntity right) {
		addPhysicalEntityAsParticipant(right, InteractionParticipant.Type.RIGHT);
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		for (int i=0; i<participantStoichiometry.size(); i++) {
			Stoichiometry thing = participantStoichiometry.get(i);
			if(thing == objectProxy) {
				participantStoichiometry.set(i, (Stoichiometry)concreteObject);
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		
		for (int i=0; i<participantStoichiometry.size(); i++) {
			Stoichiometry thing = participantStoichiometry.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						participantStoichiometry.set(i, (Stoichiometry)concreteObject);
					}
				}
			}
		}
	}
	
	public void replace(BioPaxObject keeperObject) {
		for (int i=0; i<getParticipants().size(); i++) {
			InteractionParticipant thing = (InteractionParticipant)getParticipants().get(i);
			if(thing.getPhysicalEntity().getID().equals(keeperObject.getID())) {
				getParticipants().set(i, new InteractionParticipant(thing.getInteraction(), (PhysicalEntity)keeperObject, thing.getType()));
			}
		}
	}	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"conversionDirection",conversionDirection,level);
		printObjects(sb,"left",getLeft(),level);
		printObjects(sb,"participantStoichiometry",participantStoichiometry,level);
		printObjects(sb,"right",getRight(),level);
		printBoolean(sb,"spontaneous",spontaneous,level);
	}

	public static final String typeName = "Conversion";
	@Override
	public final String getDisplayName() {
		if(getName().size() == 0) {
			return PathwayMapping.getSafetyName(getID());
		} else {
			return PathwayMapping.getSafetyName(getName().get(0));
		}
	}
	@Override
	public final String getDisplayType() {
		return typeName;
	}

}
