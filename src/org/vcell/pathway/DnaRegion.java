package org.vcell.pathway;

public class DnaRegion extends PhysicalEntity {
	private EntityReference entityReference;	
	
	public DnaRegion(String name) {
		super(name);
	}

	public EntityReference getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(EntityReference entityReference) {
		this.entityReference = entityReference;
	}
	

}
