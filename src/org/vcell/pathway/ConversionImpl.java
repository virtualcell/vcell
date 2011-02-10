package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class ConversionImpl extends InteractionImpl implements Conversion {

	private String conversionDirection;
	private ArrayList<Stoichiometry> participantStoichiometry = new ArrayList<Stoichiometry>();
	private Boolean spontaneous;
	private ArrayList<PhysicalEntity> right = new ArrayList<PhysicalEntity>();
	private ArrayList<PhysicalEntity> left = new ArrayList<PhysicalEntity>();

	public String getConversionDirection() {
		return conversionDirection;
	}
	public ArrayList<Stoichiometry> getParticipantStoichiometry() {
		return participantStoichiometry;
	}
	public Boolean getSpontaneous() {
		return spontaneous;
	}
	public ArrayList<PhysicalEntity> getLeft() {
		return left;
	}
	public ArrayList<PhysicalEntity> getRight() {
		return right;
	}
	
	public void setConversionDirection(String conversionDirection) {
		this.conversionDirection = conversionDirection;
	}
	public void setParticipantStoichiometry(
			ArrayList<Stoichiometry> participantStoichiometry) {
		this.participantStoichiometry = participantStoichiometry;
	}
	public void setSpontaneous(Boolean spontaneous) {
		this.spontaneous = spontaneous;
	}
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy,concreteObject);
	}
	public void setLeft(ArrayList<PhysicalEntity> left) {
		this.left = left;
	}
	public void setRight(ArrayList<PhysicalEntity> right) {
		this.right = right;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"conversionDirection",conversionDirection,level);
		printObjects(sb,"left",left,level);
		printObjects(sb,"participantStoichiometry",participantStoichiometry,level);
		printObjects(sb,"right",right,level);
		printBoolean(sb,"spontaneous",spontaneous,level);
	}

}
