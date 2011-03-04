package org.vcell.pathway;

import java.util.List;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Catalysis extends Control {
	private String catalysisDirection;

	public String getCatalysisDirection() {
		return catalysisDirection;
	}
	public List<PhysicalEntity> getCofactors() {
		return getParticipantPhysicalEntities(InteractionParticipant.Type.COFACTOR);
	}

	public void setCatalysisDirection(String catalysisDirection) {
		this.catalysisDirection = catalysisDirection;
	}
	
	public void addCofactor(PhysicalEntity cofactor) {
		addPhysicalEntityAsParticipant(cofactor, InteractionParticipant.Type.COFACTOR);
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
		
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printString(sb,"catalysisDirection",catalysisDirection,level);
		printObjects(sb,"physicalControllers",getCofactors(),level);
	}

}
