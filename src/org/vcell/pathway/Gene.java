package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Gene extends EntityImpl {
	private BioSource organism;

	public BioSource getOrganism() {
		return organism;
	}

	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(organism == objectProxy) {
			organism = (BioSource) concreteObject;
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "organism",organism,level);
	}
}
