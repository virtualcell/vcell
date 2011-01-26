package org.vcell.pathway;

import java.util.List;

public interface Interaction extends Entity {
	
	public InteractionVocabulary getInteractionType();

	public List<InteractionParticipant> getParticipants();
	
	public List<InteractionParticipant> getParticipants(InteractionParticipant.Type type);
	
	public void setInteractionType(InteractionVocabulary interactionType);
	
	public void setParticipants(List<InteractionParticipant> participants);
		
	public void addParticipant(InteractionParticipant participant);
	
	public void addEntityAsParticipant(PhysicalEntity entity, InteractionParticipant.Type type);
}
