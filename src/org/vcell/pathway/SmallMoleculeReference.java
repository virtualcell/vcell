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

public class SmallMoleculeReference extends EntityReference {
	private String chemicalFormula;
	private Double molecularWeight;
	private ChemicalStructure structure;
	
	public String getChemicalFormula() {
		return chemicalFormula;
	}
	public Double getMolecularWeight() {
		return molecularWeight;
	}
	public ChemicalStructure getStructure() {
		return structure;
	}
	public void setChemicalFormula(String chemicalFormula) {
		this.chemicalFormula = chemicalFormula;
	}
	public void setMolecularWeight(Double molecularWeight) {
		this.molecularWeight = molecularWeight;
	}
	public void setStructure(ChemicalStructure structure) {
		this.structure = structure;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(structure == objectProxy) {
			structure = (ChemicalStructure) concreteObject;
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		
		if(structure instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)structure;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					structure = (ChemicalStructure) concreteObject;
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "chemicalFormula",chemicalFormula,level);
		printDouble(sb, "molecularWeight",molecularWeight,level);
		printObject(sb, "structure",structure,level);
	}
}
