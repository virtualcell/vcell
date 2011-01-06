package org.vcell.pathway;

import java.util.ArrayList;

public class PhysicalEntity extends EntityImpl {
	private CellularLocationVocabulary cellularLocation;
	private ArrayList<EntityFeature> feature;

	private ArrayList<PhysicalEntity> memberPhysicalEntity;

	private ArrayList<EntityFeature> notFeature;

	public PhysicalEntity(String name) {
		super(name);
	}

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
}
