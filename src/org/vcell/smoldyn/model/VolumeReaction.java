package org.vcell.smoldyn.model;

import java.util.ArrayList;

import org.vcell.smoldyn.model.SpeciesAndState.Product;
import org.vcell.smoldyn.model.SpeciesAndState.Reactant;
import org.vcell.smoldyn.model.util.ReactionParticipants;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * A reaction that occurs within a specified volume, where the volume is either a
 * compartment or the entire simulation.  The reaction has reactants, products,
 * a rate, a name, and a location.  The reactants must be in the solution state.
 * 
 * Invariants:
 * 		name, location, reactants, and products are not null
 * 		rate is non-negative
 * 
 * @author mfenwick
 *
 */
public class VolumeReaction implements SmoldynReaction {
	
	private final ArrayList<Species> reactants =  new ArrayList<Species>();
	private final ArrayList<Species> products = new ArrayList<Species>();
	private final double rate;
	private final String name;
	private final Compartment location;
	
	
	/**
	 * @param reactants -- not null, no more than two, no null values
	 * @param products -- not null, no more than two, no null values, and at least one of (reactants and products) must have more than
	 * 		0 elements
	 * @param rate -- nonnegative
	 * @param reactionname -- not null
	 * @param compartment -- not null
	 */
	public VolumeReaction(String reactionname, Compartment compartment, ReactionParticipants participants, double rate) {
		SimulationUtilities.checkForNull("argument to volume reaction constructor", reactionname, compartment, participants);
		SimulationUtilities.checkForNonNegative("volume reaction rate", rate);
		for(Reactant r : participants.getReactants()) {
			this.reactants.add(r.getSpecies());
		}
		for(Product p : participants.getProducts()) {
			this.products.add(p.getSpecies());
		}
		this.rate = rate;
		this.name = reactionname;
		this.location = compartment;
	}
	
	
	public Species [] getReactants() {
		return reactants.toArray(new Species [reactants.size()]);
	}
	
	public Species [] getProducts() {
		return products.toArray(new Species [products.size()]);
	}
	
	public double getRate() {
		return rate;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the space in which this reaction occurs.
	 * @return where this reaction is localized.  If null, then the VolumeReaction should be understood
	 * to apply to the entire volume of the simulation.
	 */
	public Compartment getLocation() {
		return this.location;
	}
}
