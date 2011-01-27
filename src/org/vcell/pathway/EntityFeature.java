package org.vcell.pathway;

import java.util.ArrayList;

public interface EntityFeature extends UtilityClass {

	public ArrayList<SequenceLocation> getFeatureLocation();
	
	public ArrayList<SequenceRegionVocabulary> getFeatureLocationType();
	
	public ArrayList<EntityFeature> getMemberFeature();
	
	public ArrayList<Evidence> getEvidence();
	
	public void setFeatureLocation(ArrayList<SequenceLocation> featureLocation);
	
	public void setFeatureLocationType(ArrayList<SequenceRegionVocabulary> featureLocationType);

	public void setMemberFeature(ArrayList<EntityFeature> memberFeature);

	public void setEvidence(ArrayList<Evidence> evidence);

}
