package org.vcell.pathway;

import java.util.ArrayList;

public class InteractionImpl extends EntityImpl implements Interaction {

	private InteractionVocabulary interactionType;

	private ArrayList<EntityImpl> participants = new ArrayList<EntityImpl>();

	public InteractionImpl(String name) {
		super(name);
	}

	public InteractionVocabulary getInteractionType() {
		return interactionType;
	}

	public ArrayList<EntityImpl> getParticipants() {
		return participants;
	}
	public void setInteractionType(InteractionVocabulary interactionType) {
		this.interactionType = interactionType;
	}
	
	public void setParticipants(ArrayList<EntityImpl> participants) {
		this.participants = participants;
	}

}
