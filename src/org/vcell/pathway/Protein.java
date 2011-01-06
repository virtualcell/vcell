package org.vcell.pathway;

public class Protein extends PhysicalEntity {
	private EntityReference entityReference;	
	
	public Protein(String name) {
		super(name);
	}

	public EntityReference getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(EntityReference entityReference) {
		this.entityReference = entityReference;
	}
	

}
