package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class ConversionImpl extends InteractionImpl implements Conversion {

	private String conversionDirection;

	private ArrayList<PhysicalEntity> leftSide = new ArrayList<PhysicalEntity>();

	private ArrayList<Stoichiometry> participantStoichiometry = new ArrayList<Stoichiometry>();

	private ArrayList<PhysicalEntity> rightSide = new ArrayList<PhysicalEntity>();

	private Boolean spontaneous;

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
	
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy,concreteObject);
		for (int i=0;i<leftSide.size();i++){
			PhysicalEntity participant = leftSide.get(i);
			if (participant == objectProxy){
				leftSide.set(i, (PhysicalEntity)concreteObject);
			}
		}
		for (int i=0;i<rightSide.size();i++){
			PhysicalEntity participant = rightSide.get(i);
			if (participant == objectProxy){
				rightSide.set(i, (PhysicalEntity)concreteObject);
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"conversionDirection",conversionDirection,level);
		printObjects(sb,"leftSide",leftSide,level);
		printObjects(sb,"participantStoichiometry",participantStoichiometry,level);
		printObjects(sb,"rightSide",rightSide,level);
		printBoolean(sb,"spontaneous",spontaneous,level);
	}

}
