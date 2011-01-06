package org.vcell.pathway;

import java.util.ArrayList;

public class EntityReference implements UtilityClass {
	
	private ArrayList<EntityFeature> entityFeature;
	private EntityReferenceTypeVocabulary entityReferenceType;
	private ArrayList<EntityReference> memberEntityReference;
	private ArrayList<String> name;
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

}
