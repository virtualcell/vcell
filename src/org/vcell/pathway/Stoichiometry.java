/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.HashMap;
import java.util.HashSet;

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
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		if(physicalEntity instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)physicalEntity;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					physicalEntity = (PhysicalEntity) concreteObject;
				}
			}
		}
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "physicalEntity",physicalEntity,level);
		printDouble(sb, "stoichiometricCoefficient",stoichiometricCoefficient,level);
	}

}
