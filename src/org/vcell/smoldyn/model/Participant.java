package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.Species.StateType;

/**
 * @author mfenwick
 *
 */
public abstract class Participant {
	
	private final Species species;
	private final StateType statetype;
	
	
	public Participant(Species species) {
		this(species, StateType.solution);
	}
	
	public Participant(Species species, StateType statetype) {
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
	public static class Product extends Participant {
		
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
	public static class Reactant extends Participant {
		
		public Reactant(Species species) {
			super(species);
		}
		
		public Reactant(Species species, StateType statetype) {
			super(species, statetype);
		}
		
	}
}
