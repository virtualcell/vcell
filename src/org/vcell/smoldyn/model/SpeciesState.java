package org.vcell.smoldyn.model;


import org.vcell.smoldyn.model.util.DiffusionDriftMatrix;
import org.vcell.smoldyn.simulation.SimulationUtilities;


/**
 * A {@link Species} may move between several states defined by Smoldyn.  The states are:
 * solution, and four membrane-bound states, corresponding to transmembrane states
 * and sides of the membrane.  The diffusion properties of a Species depend on the
 * state it is in.  These properties can be specified with a simple, isotropic
 * diffusion coefficient, or an anisotropic {@link DiffusionDriftMatrix}, and optionally also
 * a DiffusionDriftMatrix to specify anisotropic drift.
 * 
 * Invariants:
 * 		species and state are non-null
 * 		neither species nor state may not be changed
 * 		diffusionconstant is greater than or equal to 0
 * 
 * 
 * @author mfenwick
 *
 */
public class SpeciesState {
	private final Species species;
	private final StateType state;
	private double diffusionconstant;
	
	
	/**
	 * Uses the default diffusion constant of 0.
	 * 
	 * @param species
	 * @param statetype
	 */
	public SpeciesState(Species species, StateType statetype) {
		this(species, statetype, 0);
	}
	
	
	/**
	 * Instantiates a SpeciesState with the given {@link Species} and {@link StateType} and ISOTROPIC diffusion constant.
	 * Anisotropic diffusion is not currently supported, but parameters are included as placeholders for future development.  However,
	 * attempting to use these features will cause an exception to be thrown.
	 * 
	 * @param species Species must not be null
	 * @param statetype StateType must not be null
	 * @param isotropicdiffusionconstant must be non-negative
	 */
	public SpeciesState(Species species, StateType statetype, double isotropicdiffusionconstant) {
		SimulationUtilities.checkForNull("species or statetype", species, statetype);
		SimulationUtilities.checkForNonNegative("diffusion constant", isotropicdiffusionconstant);
		
		this.species = species;
		this.state = statetype;
		this.diffusionconstant = isotropicdiffusionconstant;
	}
	

	public double getDifc() {
		return diffusionconstant;
	}
	
	/**
	 * @param difc new diffusions constant -- must be non-negative
	 */
	public void setDifc(double difc) {
		SimulationUtilities.checkForNonNegative("diffusion constant", difc);
		this.diffusionconstant = difc;
	}

	public StateType getState() {
		return this.state;
	}
	
	public Species getSpecies() {
		return this.species;
	}
	
	public String asString() {
		return new String(species.getName() + "(" + state + ")");
	}
	
	public String hashName() {
		return this.species.getName() + this.state;
	}
	
	public String hashName(String name, StateType statetype) {
		return name + statetype;
	}
	
	
	/**
	 * fsoln and bsoln are both solution states, they're just relative to a surface
	 * 	.... so, a molecule can not be in state 'fsoln', but a surface can say that
	 * 	a molecule is in 'fsoln'
	 * 
	 * @author mattf
	 *
	 */
	public enum StateType {
		front,
		back,
		up,
		down,
		solution,
//		fsoln,
//		bsoln,
	}
}