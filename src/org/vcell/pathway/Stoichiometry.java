package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Stoichiometry extends BioPaxObjectImpl implements UtilityClass {
	private PhysicalEntity physicalEntity;
	private Double stoichiometricCoefficient;
	
	public PhysicalEntity getPhysicalEntity() {
		return physicalEntity;
	}
	public Double getStoichiometricCoefficient() {
		return stoichiometricCoefficient;
	}
	public void setPhysicalEntity(PhysicalEntity physicalEntity) {
		this.physicalEntity = physicalEntity;
	}
	public void setStoichiometricCoefficient(Double stoichiometricCoefficient) {
		this.stoichiometricCoefficient = stoichiometricCoefficient;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		if(physicalEntity == objectProxy) {
			physicalEntity = (PhysicalEntity) concreteObject;
		}
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "physicalEntity",physicalEntity,level);
		printDouble(sb, "stoichiometricCoefficient",stoichiometricCoefficient,level);
	}

}
