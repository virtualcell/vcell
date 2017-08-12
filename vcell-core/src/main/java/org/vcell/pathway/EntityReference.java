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

public class EntityReference extends BioPaxObjectImpl implements UtilityClass {
	
	private ArrayList<EntityFeature> entityFeature = new ArrayList<EntityFeature>();
	private EntityReferenceTypeVocabulary entityReferenceType;
	private ArrayList<EntityReference> memberEntityReference = new ArrayList<EntityReference>();
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<Xref> xRef = new ArrayList<Xref>();
	private ArrayList<Evidence> evidence = new ArrayList<Evidence>();
	
	public ArrayList<Xref> getxRef() {
		return xRef;
	}
	public void setxRef(ArrayList<Xref> xRef) {
		this.xRef = xRef;
	}
	public ArrayList<Evidence> getEvidence() {
		return evidence;
	}
	public void setEvidence(ArrayList<Evidence> evidence) {
		this.evidence = evidence;
	}
	public ArrayList<EntityFeature> getEntityFeature() {
		return entityFeature;
	}
	public EntityReferenceTypeVocabulary getEntityReferenceType() {
		return entityReferenceType;
	}
	public ArrayList<EntityReference> getMemberEntityReference() {
		return memberEntityReference;
	}
	public ArrayList<String> getName() {
		return name;
	}
	public void setEntityFeature(ArrayList<EntityFeature> entityFeature) {
		this.entityFeature = entityFeature;
	}
	public void setEntityReferenceType(
			EntityReferenceTypeVocabulary entityReferenceType) {
		this.entityReferenceType = entityReferenceType;
	}
	public void setMemberEntityReference(
			ArrayList<EntityReference> memberEntityReference) {
		this.memberEntityReference = memberEntityReference;
	}
	public void setName(ArrayList<String> name) {
		this.name = name;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		for (int i=0; i<entityFeature.size(); i++) {
			EntityFeature thing = entityFeature.get(i);
			if(thing == objectProxy) {
				entityFeature.set(i, (EntityFeature)concreteObject);
			}
		}
		if(entityReferenceType == objectProxy) {
			entityReferenceType = (EntityReferenceTypeVocabulary) concreteObject;
		}
		for (int i=0; i<memberEntityReference.size(); i++) {
			EntityReference thing = memberEntityReference.get(i);
			if(thing == objectProxy) {
				memberEntityReference.set(i, (EntityReference)concreteObject);
			}
		}
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing == objectProxy) {
				xRef.set(i, (Xref)concreteObject);
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
		
		for (int i=0; i<entityFeature.size(); i++) {
			EntityFeature thing = entityFeature.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						entityFeature.set(i, (EntityFeature)concreteObject);
					}
				}
			}
		}
		if(entityReferenceType instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)entityReferenceType;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					entityReferenceType = (EntityReferenceTypeVocabulary) concreteObject;
				}
			}
		}
		for (int i=0; i<memberEntityReference.size(); i++) {
			EntityReference thing = memberEntityReference.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						memberEntityReference.set(i, (EntityReference)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						xRef.set(i, (Xref)concreteObject);
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
		printObjects(sb, "entityFeature",entityFeature,level);
		printObject(sb, "entityReferenceType",entityReferenceType,level);
		printObjects(sb, "memberEntityReference",memberEntityReference,level);
		printStrings(sb, "name",name,level);
		printObjects(sb, "xRef",xRef,level);
		printObjects(sb, "evidence",evidence,level);
	}
}
