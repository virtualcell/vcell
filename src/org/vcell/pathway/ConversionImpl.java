package org.vcell.pathway;

import java.util.ArrayList;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class ConversionImpl extends InteractionImpl implements Conversion {

	private String conversionDirection;

	private ArrayList<Stoichiometry> participantStoichiometry = new ArrayList<Stoichiometry>();

	private Boolean spontaneous;

	public String getConversionDirection() {
		return conversionDirection;
	}

	public ArrayList<Stoichiometry> getParticipantStoichiometry() {
		return participantStoichiometry;
	}

	public Boolean getSpontaneous() {
		return spontaneous;
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
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"conversionDirection",conversionDirection,level);
//		printObjects(sb,"leftSide",leftSide,level);
		printObjects(sb,"participantStoichiometry",participantStoichiometry,level);
//		printObjects(sb,"rightSide",rightSide,level);
		printBoolean(sb,"spontaneous",spontaneous,level);
	}

}
