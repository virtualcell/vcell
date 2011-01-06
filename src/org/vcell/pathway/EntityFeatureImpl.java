package org.vcell.pathway;

import java.util.ArrayList;

public class EntityFeatureImpl implements EntityFeature {
	
	private ArrayList<SequenceLocation> featureLocation;
	private ArrayList<SequenceRegionVocabulary> featureLocationType;
	private ArrayList<EntityFeature> memberFeature;
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

}
