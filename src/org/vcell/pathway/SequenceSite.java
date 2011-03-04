package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class SequenceSite extends SequenceLocation {
	private String positionStatus;
	private Integer sequencePosition;
	
	public String getPositionStatus() {
		return positionStatus;
	}
	public Integer getSequencePosition() {
		return sequencePosition;
	}
	public void setPositionStatus(String positionStatus) {
		this.positionStatus = positionStatus;
	}
	public void setSequencePosition(Integer sequencePosition) {
		this.sequencePosition = sequencePosition;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "positionStatus",positionStatus,level);
		printInteger(sb, "sequencePosition",sequencePosition,level);
	}

}
