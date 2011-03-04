package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class ProteinReference extends EntityReference {
	private BioSource organism;
	private String sequence;
	
	public BioSource getOrganism() {
		return organism;
	}
	public String getSequence() {
		return sequence;
	}
	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
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
		printString(sb, "sequence",sequence,level);
	}
}
