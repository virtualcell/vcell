package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Rna extends PhysicalEntity {
	private EntityReference entityReference;	
	
	public EntityReference getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(EntityReference entityReference) {
		this.entityReference = entityReference;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(entityReference == objectProxy) {
			entityReference = (EntityReference) concreteObject;
		}
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "entityReference",entityReference,level);
	}
}
