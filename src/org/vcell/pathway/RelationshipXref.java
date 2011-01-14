package org.vcell.pathway;

import java.util.ArrayList;

public class RelationshipXref extends Xref {
	private ArrayList<RelationshipTypeVocabulary> relationshipType = new ArrayList<RelationshipTypeVocabulary>();

	public ArrayList<RelationshipTypeVocabulary> getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(
			ArrayList<RelationshipTypeVocabulary> relationshipType) {
		this.relationshipType = relationshipType;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "relationshipType",relationshipType,level);
	}

}
