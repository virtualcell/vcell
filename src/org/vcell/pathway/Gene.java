package org.vcell.pathway;

public class Gene extends EntityImpl {
	private BioSource organism;

	public Gene(String name) {
		super(name);
	}

	public BioSource getOrganism() {
		return organism;
	}

	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}
}
