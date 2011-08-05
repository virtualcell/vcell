/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
