package org.vcell.pathway;

import java.util.ArrayList;

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
