package org.vcell.pathway;

import java.util.ArrayList;

public interface Interaction extends Entity {
	
	public InteractionVocabulary getInteractionType();

	public ArrayList<Entity> getParticipants();
	
	public void setInteractionType(InteractionVocabulary interactionType);
	
	public void setParticipants(ArrayList<Entity> participants);
}
