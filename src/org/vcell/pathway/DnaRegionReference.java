package org.vcell.pathway;

import java.util.ArrayList;

public class DnaRegionReference extends EntityReference {
	private SequenceLocation absoluteRegion;
	private ArrayList<DnaRegionReference> dnaSubRegion;
	private BioSource organism;
	private SequenceRegionVocabulary regionType;
	private ArrayList<RnaRegionReference> rnaSubRegion;
	private String sequence;
	public SequenceLocation getAbsoluteRegion() {
		return absoluteRegion;
	}
	public ArrayList<DnaRegionReference> getDnaSubRegion() {
		return dnaSubRegion;
	}
	public BioSource getOrganism() {
		return organism;
	}
	public SequenceRegionVocabulary getRegionType() {
		return regionType;
	}
	public ArrayList<RnaRegionReference> getRnaSubRegion() {
		return rnaSubRegion;
	}
	public String getSequence() {
		return sequence;
	}
	public void setAbsoluteRegion(SequenceLocation absoluteRegion) {
		this.absoluteRegion = absoluteRegion;
	}
	public void setDnaSubRegion(ArrayList<DnaRegionReference> dnaSubRegion) {
		this.dnaSubRegion = dnaSubRegion;
	}
	public void setOrganism(BioSource organism) {
		this.organism = organism;
	}
	public void setRegionType(SequenceRegionVocabulary regionType) {
		this.regionType = regionType;
	}
	public void setRnaSubRegion(ArrayList<RnaRegionReference> rnaSubRegion) {
		this.rnaSubRegion = rnaSubRegion;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
}
