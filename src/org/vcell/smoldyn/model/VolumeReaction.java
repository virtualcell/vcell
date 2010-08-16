package org.vcell.smoldyn.model;

import org.vcell.smoldyn.model.util.VolumeReactionParticipants;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * A reaction that occurs within a specified volume, where the volume is either a
 * compartment or the entire simulation.  The reaction has reactants, products,
 * a rate, a name, and a location.  The reactants must be in the solution state.
 * 
 * Invariants:
 * 		name, reactants, and products are not null
 * 		name, location, reactants, and products may not be changed
 * 
 * @author mfenwick
 *
 */
public class VolumeReaction implements SmoldynReaction {
	private final VolumeReactionParticipants reactants;
	private final VolumeReactionParticipants products;
	private double rate;
	private final String name;
	private final Compartment location;
	
	
	/**
	 * @param reactants
	 * @param products
	 * @param rate
	 * @param reactionname
	 * @param compartment set to null to specify the entire simulation as the reaction volume
	 */
	public VolumeReaction(String reactionname, Compartment compartment, VolumeReactionParticipants reactants, 
			VolumeReactionParticipants products, double rate) {
		this.reactants = reactants;
		this.products = products;
		SimulationUtilities.checkForNonNegative("volume reaction rate", rate);
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
	
	public void setRate(double newrate) {
		SimulationUtilities.checkForNonNegative("volume reaction rate", newrate);
		this.rate = newrate;
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
