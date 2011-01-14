package org.vcell.pathway;

import java.util.ArrayList;

public class EntityFeatureImpl extends BioPaxObjectImpl implements EntityFeature {
	
	private ArrayList<SequenceLocation> featureLocation = new ArrayList<SequenceLocation>();
	private ArrayList<SequenceRegionVocabulary> featureLocationType = new ArrayList<SequenceRegionVocabulary>();
	private ArrayList<EntityFeature> memberFeature = new ArrayList<EntityFeature>();
	private ArrayList<Evidence> evidence = new ArrayList<Evidence>();
	
	public ArrayList<Evidence> getEvidence() {
		return evidence;
	}
	public void setEvidence(ArrayList<Evidence> evidence) {
		this.evidence = evidence;
	}
	public ArrayList<SequenceLocation> getFeatureLocation() {
		return featureLocation;
	}
	public ArrayList<SequenceRegionVocabulary> getFeatureLocationType() {
		return featureLocationType;
	}
	public ArrayList<EntityFeature> getMemberFeature() {
		return memberFeature;
	}
	public void setFeatureLocation(ArrayList<SequenceLocation> featureLocation) {
		this.featureLocation = featureLocation;
	}
	public void setFeatureLocationType(
			ArrayList<SequenceRegionVocabulary> featureLocationType) {
		this.featureLocationType = featureLocationType;
	}
	public void setMemberFeature(ArrayList<EntityFeature> memberFeature) {
		this.memberFeature = memberFeature;
	}
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "featureLocation",featureLocation,level);
		printObjects(sb, "featureLocationType",featureLocationType,level);
		printObjects(sb, "memberFeature",memberFeature,level);
		printObjects(sb, "evidence",evidence,level);
	}

}
