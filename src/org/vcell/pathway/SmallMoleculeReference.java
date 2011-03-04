package org.vcell.pathway;

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
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "chemicalFormula",chemicalFormula,level);
		printDouble(sb, "molecularWeight",molecularWeight,level);
		printObject(sb, "structure",structure,level);
	}
}
