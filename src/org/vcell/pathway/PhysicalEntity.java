package org.vcell.pathway;

import java.util.ArrayList;

public class PhysicalEntity extends EntityImpl {
	private CellularLocationVocabulary cellularLocation;
	private ArrayList<EntityFeature> feature = new ArrayList<EntityFeature>();

	private ArrayList<PhysicalEntity> memberPhysicalEntity = new ArrayList<PhysicalEntity>();

	private ArrayList<EntityFeature> notFeature = new ArrayList<EntityFeature>();

	public CellularLocationVocabulary getCellularLocation() {
		return cellularLocation;
	}

	public ArrayList<EntityFeature> getFeature() {
		return feature;
	}

	public ArrayList<PhysicalEntity> getMemberPhysicalEntity() {
		return memberPhysicalEntity;
	}

	public ArrayList<EntityFeature> getNotFeature() {
		return notFeature;
	}
	
	public void setCellularLocation(CellularLocationVocabulary cellularLocation) {
		this.cellularLocation = cellularLocation;
	}
	public void setFeature(ArrayList<EntityFeature> feature) {
		this.feature = feature;
	}
	public void setMemberPhysicalEntity(
			ArrayList<PhysicalEntity> memberPhysicalEntity) {
		this.memberPhysicalEntity = memberPhysicalEntity;
	}

	public void setNotFeature(ArrayList<EntityFeature> notFeature) {
		this.notFeature = notFeature;
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "cellularLocation",cellularLocation,level);
		printObjects(sb, "feature",feature,level);
		printObjects(sb, "memberPhysicalEntity",memberPhysicalEntity,level);
		printObjects(sb, "notFeature",notFeature,level);
	}

}
