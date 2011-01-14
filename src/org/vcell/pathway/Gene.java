package org.vcell.pathway;

public class Gene extends EntityImpl {
	private BioSource organism;

	public BioSource getOrganism() {
		return organism;
	}

	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "organism",organism,level);
	}
}
