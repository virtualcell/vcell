package org.vcell.pathway;

import java.util.ArrayList;

public interface Interaction extends Entity {
	
	public InteractionVocabulary getInteractionType();

	public ArrayList<EntityImpl> getParticipants();
	
	public void setInteractionType(InteractionVocabulary interactionType);
	
	public void setParticipants(ArrayList<EntityImpl> participants);
}
