package org.vcell.pathway;

import java.util.ArrayList;

public class DnaReference extends EntityReference {
	private ArrayList<DnaRegionReference> dnaSubRegion = new ArrayList<DnaRegionReference>();
	private BioSource organism;
	private ArrayList<RnaRegionReference> rnaSubRegion = new ArrayList<RnaRegionReference>();
	private String sequence;
	
	public ArrayList<DnaRegionReference> getDnaSubRegion() {
		return dnaSubRegion;
	}
	public BioSource getOrganism() {
		return organism;
	}
	public ArrayList<RnaRegionReference> getRnaSubRegion() {
		return rnaSubRegion;
	}
	public String getSequence() {
		return sequence;
	}
	public void setDnaSubRegion(ArrayList<DnaRegionReference> dnaSubRegion) {
		this.dnaSubRegion = dnaSubRegion;
	}
	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}
	public void setRnaSubRegion(ArrayList<RnaRegionReference> rnaSubRegion) {
		this.rnaSubRegion = rnaSubRegion;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	} 

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObjects(sb,"dnaSubRegion",dnaSubRegion,level);
		printObject(sb,"organism",organism,level);
		printObjects(sb,"rnaSubRegion",rnaSubRegion,level);
		printString(sb,"sequence",sequence,level);
	}
}
