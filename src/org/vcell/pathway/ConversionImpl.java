package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.InteractionParticipant.Type;
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
		// Since arrayList left had never been set, it will return an empty ArrayList if we just "return left;" 
		// Xintao implemented the function as the way to return arrayList leftSide after filling in the participants that have the LEFT type.
		// Feel free to modify it if you can return the same thing here because some functions rely on it.
		// 02/2011
		ArrayList<PhysicalEntity> leftSide = new ArrayList<PhysicalEntity>();
		for (InteractionParticipant obj :getParticipants()){
			if(obj.getType() == Type.LEFT){
				leftSide.add(obj.getPhysicalEntity());
			}
		}
		return leftSide;
	}
	public ArrayList<PhysicalEntity> getRight() {
		// Since arrayList right had never been set, it will return an empty ArrayList if we just "return right;" 
		// Xintao implemented the function as the way to return arrayList rightSide after filling in the participants that have the RIGHT type.
		// Feel free to modify it if you can return the same thing here because some functions rely on it
		// 02/2011
		ArrayList<PhysicalEntity> rightSide = new ArrayList<PhysicalEntity>();
		for (InteractionParticipant obj :getParticipants()){
			if(obj.getType() == Type.RIGHT){
				rightSide.add(obj.getPhysicalEntity());
			}
		}
		return rightSide;
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
