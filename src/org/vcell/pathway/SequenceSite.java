package org.vcell.pathway;

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
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "positionStatus",positionStatus,level);
		printInteger(sb, "sequencePosition",sequencePosition,level);
	}

}
