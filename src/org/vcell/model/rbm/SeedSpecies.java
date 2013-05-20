package org.vcell.model.rbm;

import cbit.vcell.parser.Expression;

public class SeedSpecies extends RbmElement {

	private SpeciesPattern speciesPattern;
	private Expression initialCondition;
	public static final String PROPERTY_NAME_SPECIES_PATTERN = "speciesPattern";
	public static final String PROPERTY_NAME_INITIAL_CONDITION = "initialCondition";
	
	public SeedSpecies() {
	}
	public SeedSpecies(SpeciesPattern speciesPattern, Expression initCond) {
		this.speciesPattern = speciesPattern;
		this.initialCondition = initCond;
	}
	public final SpeciesPattern getSpeciesPattern() {
		return speciesPattern;
	}
	public final void setSpeciesPattern(SpeciesPattern newValue) {
		SpeciesPattern oldValue = speciesPattern;
		this.speciesPattern = newValue;
		firePropertyChange(PROPERTY_NAME_SPECIES_PATTERN, oldValue, newValue);
	}
	public final Expression getInitialCondition() {
		return initialCondition;
	}
	public final void setInitialCondition(Expression newValue) {
		Expression oldValue = initialCondition;
		this.initialCondition = newValue;
		firePropertyChange(PROPERTY_NAME_INITIAL_CONDITION, oldValue, newValue);
	}
	
}
