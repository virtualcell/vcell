package org.vcell.model.rbm;

import cbit.vcell.bionetgen.BNGSpecies;

public class GeneratedSpeciesTableRow {

	private BNGSpecies speciesObject;
	private String index;
	private String expression;
	
	public GeneratedSpeciesTableRow(BNGSpecies speciesObject) {
		this.speciesObject = speciesObject;
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

