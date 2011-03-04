package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

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

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(cellularLocation == objectProxy) {
			cellularLocation = (CellularLocationVocabulary) concreteObject;
		}
		for (int i=0; i<feature.size(); i++) {
			EntityFeature thing = feature.get(i);
			if(thing == objectProxy) {
				feature.set(i, (EntityFeature)concreteObject);
			}
		}
		for (int i=0; i<memberPhysicalEntity.size(); i++) {
			PhysicalEntity thing = memberPhysicalEntity.get(i);
			if(thing == objectProxy) {
				memberPhysicalEntity.set(i, (PhysicalEntity)concreteObject);
			}
		}
		for (int i=0; i<notFeature.size(); i++) {
			EntityFeature thing = notFeature.get(i);
			if(thing == objectProxy) {
				notFeature.set(i, (EntityFeature)concreteObject);
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "cellularLocation",cellularLocation,level);
		printObjects(sb, "feature",feature,level);
		printObjects(sb, "memberPhysicalEntity",memberPhysicalEntity,level);
		printObjects(sb, "notFeature",notFeature,level);
	}

}
