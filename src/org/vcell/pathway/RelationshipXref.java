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

public class RelationshipXref extends Xref {
	private ArrayList<RelationshipTypeVocabulary> relationshipType = new ArrayList<RelationshipTypeVocabulary>();

	public ArrayList<RelationshipTypeVocabulary> getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(
			ArrayList<RelationshipTypeVocabulary> relationshipType) {
		this.relationshipType = relationshipType;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);

		for (int i=0; i<relationshipType.size(); i++) {
			RelationshipTypeVocabulary thing = relationshipType.get(i);
			if(thing == objectProxy) {
				relationshipType.set(i, (RelationshipTypeVocabulary)concreteObject);
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);

		for (int i=0; i<relationshipType.size(); i++) {
			RelationshipTypeVocabulary thing = relationshipType.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						relationshipType.set(i, (RelationshipTypeVocabulary)concreteObject);
					}
				}
			}
		}
	}
		
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "relationshipType",relationshipType,level);
	}

}
