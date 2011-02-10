package org.vcell.pathway;

import java.util.List;

public interface Interaction extends Entity {
	
	public List<InteractionVocabulary> getInteractionTypes();
	public List<InteractionParticipant> getParticipants();
	public List<InteractionParticipant> getParticipants(InteractionParticipant.Type type);
	
	public void setInteractionTypes(List<InteractionVocabulary> interactionTypes);
	public void setParticipants(List<InteractionParticipant> participants);
	public void addParticipant(InteractionParticipant participant);
	
	public void addEntityAsParticipant(PhysicalEntity entity, InteractionParticipant.Type type);
}
