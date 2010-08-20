package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.Species.StateType;

/**
 * @author mfenwick
 *
 */
public abstract class SpeciesAndState {
	
	private final Species species;
	private final StateType statetype;
	
	
	public SpeciesAndState(Species species) {
		this(species, StateType.solution);
	}
	
	public SpeciesAndState(Species species, StateType statetype) {
		this.species = species;
		this.statetype = statetype;
	}
	
	public Species getSpecies() {
		return species;
	}
	
	public StateType getStatetype() {
		return statetype;
	}
	
	
	
	/**
	 * @author mfenwick
	 *
	 */
	public static class Product extends SpeciesAndState {
		
		public Product(Species species) {
			super(species);
		}
		
		public Product(Species species, StateType statetype) {
			super(species, statetype);
		}
	}
	
	
	
	/**
	 * @author mfenwick
	 *
	 */
	public static class Reactant extends SpeciesAndState {
		
		public Reactant(Species species) {
			super(species);
		}
		
		public Reactant(Species species, StateType statetype) {
			super(species, statetype);
		}
		
	}
}
