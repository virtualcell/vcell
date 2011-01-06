package org.vcell.pathway;

public class Dna extends PhysicalEntity {
	private EntityReference entityReference;	
	
	public Dna(String name) {
		super(name);
	}

	public EntityReference getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(EntityReference entityReference) {
		this.entityReference = entityReference;
	}
	

}
