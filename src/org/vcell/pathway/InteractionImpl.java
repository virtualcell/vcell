package org.vcell.pathway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vcell.pathway.InteractionParticipant.Type;
import org.vcell.pathway.persistence.BiopaxProxy.*;

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

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
//		printObject(sb, "interactionType",interactionType,level);
		for(InteractionParticipant participant : participants) {
			printObject(sb, participant.getType().name(), participant.getPhysicalEntity(), level);
		}
	}
	

}
