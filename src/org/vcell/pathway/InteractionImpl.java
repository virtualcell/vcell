package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class InteractionImpl extends EntityImpl implements Interaction {

	private InteractionVocabulary interactionType;

	private ArrayList<Entity> participants = new ArrayList<Entity>();

	public InteractionVocabulary getInteractionType() {
		return interactionType;
	}

	public ArrayList<Entity> getParticipants() {
		return participants;
	}
	public void setInteractionType(InteractionVocabulary interactionType) {
		this.interactionType = interactionType;
	}
	
	public void setParticipants(ArrayList<Entity> participants) {
		this.participants = participants;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "interactionType",interactionType,level);
	}
	
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		for (int i=0;i<participants.size();i++){
			Entity participant = participants.get(i);
			if (participant == objectProxy){
				participants.set(i, (Entity)concreteObject);
			}
		}
	}

}
