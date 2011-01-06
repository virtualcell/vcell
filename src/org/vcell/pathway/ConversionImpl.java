package org.vcell.pathway;

import java.util.ArrayList;

public class ConversionImpl extends InteractionImpl implements Conversion {

	private String conversionDirection;

	private ArrayList<PhysicalEntity> leftSide;

	private ArrayList<Stoichiometry> participantStoichiometry;

	private ArrayList<PhysicalEntity> rightSide;

	private Boolean spontaneous;

	public ConversionImpl(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public String getConversionDirection() {
		return conversionDirection;
	}

	public ArrayList<PhysicalEntity> getLeftSide() {
		return leftSide;
	}

	public ArrayList<Stoichiometry> getParticipantStoichiometry() {
		return participantStoichiometry;
	}

	public ArrayList<PhysicalEntity> getRightSide() {
		return rightSide;
	}

	public Boolean getSpontaneous() {
		return spontaneous;
	}
	public void setConversionDirection(String conversionDirection) {
		this.conversionDirection = conversionDirection;
	}
	public void setLeftSide(ArrayList<PhysicalEntity> leftSide) {
		this.leftSide = leftSide;
	}
	public void setParticipantStoichiometry(
			ArrayList<Stoichiometry> participantStoichiometry) {
		this.participantStoichiometry = participantStoichiometry;
	}
	public void setRightSide(ArrayList<PhysicalEntity> rightSide) {
		this.rightSide = rightSide;
	}
	
	public void setSpontaneous(Boolean spontaneous) {
		this.spontaneous = spontaneous;
	}

}
