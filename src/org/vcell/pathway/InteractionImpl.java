package org.vcell.pathway;

import java.util.ArrayList;
import java.util.List;

import org.vcell.pathway.InteractionParticipant.Type;
import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

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
			if(participant.getType().hasSuperType(type)) {
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
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
//		printObject(sb, "interactionType",interactionType,level);
		for(InteractionParticipant participant : participants) {
			printObject(sb, participant.getType().name(), participant.getPhysicalEntity(), level);
		}
	}
	
	public void addParticipant(InteractionParticipant participant) {
		participants.add(participant);
	}

	public void addEntityAsParticipant(PhysicalEntity entity, Type type) {
		participants.add(new InteractionParticipant(this, entity, type));
	}

	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		for (int i=0;i<participants.size();i++){
			InteractionParticipant participant = participants.get(i);
			if (participant.getPhysicalEntity() == objectProxy){
				InteractionParticipant participantNew = 
					new InteractionParticipant(participant.getInteraction(), (PhysicalEntity) concreteObject,
							participant.getType());
				participants.set(i, participantNew);
			}
		}
	}

}
