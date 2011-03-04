package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class RnaRegionReference extends EntityReference {
	private SequenceLocation absoluteRegion;
	private ArrayList<DnaRegionReference> dnaSubRegion = new ArrayList<DnaRegionReference>();
	private BioSource organism;
	private SequenceRegionVocabulary regionType;
	private ArrayList<RnaRegionReference> rnaSubRegion = new ArrayList<RnaRegionReference>();
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
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(absoluteRegion == objectProxy) {
			absoluteRegion = (SequenceLocation) concreteObject;
		}
		if(organism == objectProxy) {
			organism = (BioSource) concreteObject;
		}
		if(regionType == objectProxy) {
			regionType = (SequenceRegionVocabulary) concreteObject;
		}
		for (int i=0;i<dnaSubRegion.size();i++){
			DnaRegionReference thing = dnaSubRegion.get(i);
			if (thing == objectProxy && concreteObject instanceof DnaRegionReference){
				dnaSubRegion.set(i, (DnaRegionReference)concreteObject);
			} else if(thing == objectProxy && !(concreteObject instanceof DnaRegionReference)) {
				dnaSubRegion.remove(i);
			}
		}
		for (int i=0;i<rnaSubRegion.size();i++){
			RnaRegionReference thing = rnaSubRegion.get(i);
			if (thing == objectProxy && concreteObject instanceof RnaRegionReference){
				rnaSubRegion.set(i, (RnaRegionReference)concreteObject);
			} else if (thing == objectProxy && !(concreteObject instanceof RnaRegionReference)){
				rnaSubRegion.remove(i);
			}
		}
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "abstoluteRegion",absoluteRegion,level);
		printObjects(sb, "dnaSubRegion",dnaSubRegion,level);
		printObject(sb, "organism",organism,level);
		printObject(sb, "regionType",regionType,level);
		printObjects(sb, "rnaSubRegion",rnaSubRegion,level);
		printString(sb, "sequence",sequence,level);
		printObjects(sb, "dnaSubRegion",dnaSubRegion,level);
	}

}
