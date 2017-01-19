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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.vcell.pathway.InteractionParticipant.Type;
import org.vcell.pathway.persistence.BiopaxProxy.*;
import org.vcell.relationship.PathwayMapping;

public class InteractionImpl extends EntityImpl implements Interaction {

	private List<InteractionVocabulary> interactionTypes = new ArrayList<InteractionVocabulary>();
	private List<InteractionParticipant> participants = new ArrayList<InteractionParticipant>();

	public List<InteractionVocabulary> getInteractionTypes() {
		return interactionTypes;
	}
	public List<InteractionParticipant> getParticipants() {
		return participants;
	}
	public List<InteractionParticipant> getParticipants(Type type) {
		List<InteractionParticipant> participantsOfType = new ArrayList<InteractionParticipant>();
		for(InteractionParticipant participant : participants) {
			if(participant.getType().equals(type)) {
				participantsOfType.add(participant);
			}
		}
		return participantsOfType;
	}

	public void setInteractionTypes(List<InteractionVocabulary> interactionTypes) {
		this.interactionTypes = interactionTypes;
	}
	public void setParticipants(List<InteractionParticipant> participants) {
		this.participants = participants;
	}
	
	public void addParticipant(InteractionParticipant participant) {
		participants.add(participant);
	}

	public void addPhysicalEntityAsParticipant(PhysicalEntity entity, Type type) {
		participants.add(new InteractionParticipant(this, entity, type));
	}
	
	public List<PhysicalEntity> getParticipantPhysicalEntities(InteractionParticipant.Type type) {
		ArrayList<PhysicalEntity> cofactors = new ArrayList<PhysicalEntity>();
		for (InteractionParticipant participant : getParticipants()){
			if (participant.getType().equals(type)){
				cofactors.add(participant.getPhysicalEntity());
			}
		}
		return Collections.unmodifiableList(cofactors);
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		for (int i=0; i<interactionTypes.size(); i++) {
			InteractionVocabulary thing = interactionTypes.get(i);
			if(thing == objectProxy) {
				interactionTypes.set(i, (InteractionVocabulary)concreteObject);
			}
		}
		for (int i=0; i<participants.size(); i++) {
			InteractionParticipant thing = participants.get(i);
			if(thing.getPhysicalEntity() == objectProxy) {
				participants.set(i, new InteractionParticipant(this, (PhysicalEntity)concreteObject, thing.getType()));
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		
		for (int i=0; i<interactionTypes.size(); i++) {
			InteractionVocabulary thing = interactionTypes.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						interactionTypes.set(i, (InteractionVocabulary)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<participants.size(); i++) {
			InteractionParticipant thing = participants.get(i);
			if(thing.getPhysicalEntity() instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing.getPhysicalEntity();
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						PhysicalEntity physicalEntity = (PhysicalEntity)concreteObject;
						participants.set(i, new InteractionParticipant(this, physicalEntity, thing.getType()));
					}
				}
			}
		}
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
//		printObject(sb, "interactionType",interactionType,level);
		for(InteractionParticipant participant : participants) {
			printObject(sb, participant.getType().name(), participant.getPhysicalEntity(), level);
		}
	}

	public static final String typeName = "Interaction";
	@Override
	public String getDisplayName() {
		if(getName().size() == 0) {
			return PathwayMapping.getSafetyName(getID());
		} else {
			return PathwayMapping.getSafetyName(getName().get(0));
		}
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}
	
//	public String getTypeLabel(){
//		return "Interaction";
//	}
}
