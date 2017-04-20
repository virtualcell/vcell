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


public class InteractionParticipant {

	public static enum Type { PARTICIPANT, COFACTOR, CONTROLLED, CONTROLLER, LEFT, PRODUCT, RIGHT, TEMPLATE;
	}
	
	protected final Interaction interaction;
	protected final PhysicalEntity physicalEntity;
	protected final Type type;

	public InteractionParticipant(Interaction interaction, PhysicalEntity physicalEntity, Type type) {
		this.interaction = interaction;
		this.physicalEntity = physicalEntity;
		this.type = type;
	}

	public Interaction getInteraction() { return interaction; }
	public PhysicalEntity getPhysicalEntity() { return physicalEntity; }
	public Type getType() { return type; }

	public boolean equals(Object object) {
		if(object instanceof InteractionParticipant) {
			InteractionParticipant participant = (InteractionParticipant) object;
			return interaction.equals(participant.interaction) 
			&& physicalEntity.equals(participant.physicalEntity) && type.equals(participant.type);
		}
		return false;
	}
	
	public String getLevel3PropertyName(){
		switch (type) {
		case PARTICIPANT: {
			return "Participant";
		}
		case COFACTOR: {
			return "Cofactor";
		}
		case CONTROLLED: {
			return "Controlled";
		}
		case CONTROLLER: {
			return "Controller";
		}
		case LEFT: {
			return "Left";
		}
		case RIGHT: {
			return "Right";
		}
		case TEMPLATE: {
			return "Template";
		}
		default:
			return "";
		}
	}

	public int hashCode() {
		return interaction.hashCode() + physicalEntity.hashCode() + type.hashCode();
	}

}
