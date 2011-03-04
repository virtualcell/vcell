package org.vcell.pathway;

import java.util.ArrayList;

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
		
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "relationshipType",relationshipType,level);
	}

}
