package org.vcell.pathway;

import java.util.HashSet;
import java.util.Set;


public class InteractionParticipant {

	public static enum Type { PARTICIPANT, LEFT(PARTICIPANT), RIGHT(PARTICIPANT), 
		PHYSICAL_CONTROLLER(PARTICIPANT), COFACTOR(PHYSICAL_CONTROLLER);
	
		protected final Set<Type> superTypes = new HashSet<Type>();
	
		private Type() { superTypes.add(this); };
		
		private Type(Type superType) {
			superTypes.add(this);
			superTypes.addAll(superType.superTypes);
		}
		
		public boolean hasSuperType(Type type) {
			return superTypes.contains(type);
		}
		
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

	public int hashCode() {
		return interaction.hashCode() + physicalEntity.hashCode() + type.hashCode();
	}

}
