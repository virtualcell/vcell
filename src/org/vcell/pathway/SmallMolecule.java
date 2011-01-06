package org.vcell.pathway;

public class SmallMolecule extends PhysicalEntity {
	private EntityReference entityReference;
	
	public SmallMolecule(String name) {
		super(name);
	}

	public EntityReference getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(EntityReference entityReference) {
		this.entityReference = entityReference;
	}
	

}
