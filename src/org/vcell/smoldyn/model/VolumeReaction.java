package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.util.VolumeReactionParticipants;
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
	private final VolumeReactionParticipants reactants;
	private final VolumeReactionParticipants products;
	private final double rate;
	private final String name;
	private final Compartment location;
	
	
	/**
	 * @param reactants -- not null
	 * @param products -- not null
	 * @param rate -- nonnegative
	 * @param reactionname -- not null
	 * @param compartment -- not null
	 */
	public VolumeReaction(String reactionname, Compartment compartment, VolumeReactionParticipants reactants, 
			VolumeReactionParticipants products, double rate) {
		SimulationUtilities.checkForNull("argument to volume reaction constructor", reactionname, compartment, reactants, products);
		SimulationUtilities.checkForNonNegative("volume reaction rate", rate);
		this.reactants = reactants;
		this.products = products;
		this.rate = rate;
		this.name = reactionname;
		this.location = compartment;
	}
	
	
	public VolumeReactionParticipants getReactants() {
		return reactants;
	}
	
	public VolumeReactionParticipants getProducts() {
		return products;
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
