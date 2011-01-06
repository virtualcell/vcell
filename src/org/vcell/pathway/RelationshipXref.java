package org.vcell.pathway;

import java.util.ArrayList;

public class RelationshipXref extends Xref {
	private ArrayList<RelationshipTypeVocabulary> relationshipType;

	public ArrayList<RelationshipTypeVocabulary> getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(
			ArrayList<RelationshipTypeVocabulary> relationshipType) {
		this.relationshipType = relationshipType;
	}
}
