package org.vcell.pathway;

public class RnaRegion extends PhysicalEntity {
	private EntityReference entityReference;	
	
	public RnaRegion(String name) {
		super(name);
	}

	public EntityReference getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(EntityReference entityReference) {
		this.entityReference = entityReference;
	}
	

}
