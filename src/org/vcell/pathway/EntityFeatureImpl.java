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
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

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
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		for (int i=0; i<featureLocation.size(); i++) {
			SequenceLocation thing = featureLocation.get(i);
			if(thing == objectProxy) {
				featureLocation.set(i, (SequenceLocation)concreteObject);
			}
		}
		for (int i=0; i<featureLocationType.size(); i++) {
			SequenceRegionVocabulary thing = featureLocationType.get(i);
			if(thing == objectProxy) {
				featureLocationType.set(i, (SequenceRegionVocabulary)concreteObject);
			}
		}
		for (int i=0; i<memberFeature.size(); i++) {
			EntityFeature thing = memberFeature.get(i);
			if(thing == objectProxy) {
				memberFeature.set(i, (EntityFeature)concreteObject);
			}
		}
		for (int i=0; i<evidence.size(); i++) {
			Evidence thing = evidence.get(i);
			if(thing == objectProxy) {
				evidence.set(i, (Evidence)concreteObject);
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		
		for (int i=0; i<featureLocation.size(); i++) {
			SequenceLocation thing = featureLocation.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						featureLocation.set(i, (SequenceLocation)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<featureLocationType.size(); i++) {
			SequenceRegionVocabulary thing = featureLocationType.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						featureLocationType.set(i, (SequenceRegionVocabulary)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<memberFeature.size(); i++) {
			EntityFeature thing = memberFeature.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						memberFeature.set(i, (EntityFeature)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<evidence.size(); i++) {
			Evidence thing = evidence.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						evidence.set(i, (Evidence)concreteObject);
					}
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "featureLocation",featureLocation,level);
		printObjects(sb, "featureLocationType",featureLocationType,level);
		printObjects(sb, "memberFeature",memberFeature,level);
		printObjects(sb, "evidence",evidence,level);
	}

}
