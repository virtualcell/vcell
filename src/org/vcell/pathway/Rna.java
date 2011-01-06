package org.vcell.pathway;

public class Rna extends PhysicalEntity {
	private EntityReference entityReference;	
	
	public Rna(String name) {
		super(name);
	}

	public EntityReference getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(EntityReference entityReference) {
		this.entityReference = entityReference;
	}
	

}
