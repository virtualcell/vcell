package org.vcell.pathway;

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
}
