package org.vcell.model.rbm;

import cbit.vcell.bionetgen.BNGSpecies;

public class GeneratedSpeciesTableRow {

	private String originalName;
	private BNGSpecies speciesObject;
	private String index;
	private String expression;

	public GeneratedSpeciesTableRow(String originalName, BNGSpecies speciesObject) {
		this.setOriginalName(originalName);
		this.speciesObject = speciesObject;
	}
	
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public BNGSpecies getSpeciesObject() {
		return speciesObject;
	}

	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
}

