package org.vcell.smoldyn.model;


import org.vcell.smoldyn.model.util.SurfaceReactionParticipants;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/** 
 * A reaction that only occurs on a surface.  It is specified by its {@link SurfaceReactionParticipants} (reactants and products), its
 * reaction name, the {@link Surface} on which it occurs, and a rate.
 * 
 * Invariants:
 * 		reactionname is not null and may not be changed
 * 		surface is not null and may not be changed
 * 		reactants and products are not null and may not be changed
 * 		rate is greater than or equal to 0
 * 
 * @author mfenwick
 *
 */
public class SurfaceReaction implements SmoldynReaction {

	private final SurfaceReactionParticipants reactants;
	private final SurfaceReactionParticipants products;
	private final String reactionname;
	private double rate;
	private final Surface surface;
	
	
	/**
	 * Constructs a new SurfaceReaction.  Either reactants or products may have 0 participants, but not both, as this would be a do-nothing
	 * reaction.  If reactants has 0 participants, this indicates a 0th order reaction.  If products has 0 participants, this indicates
	 * a reaction which destroys molecules.  If both have at least 1 participant, this is a 'normal' surface reaction.
	 * 
	 * @param reactionname String -- must be non-null
	 * @param surface Surface -- must be non-null
	 * @param reactants {@link SurfaceReactionParticipants} -- must be non-null.  also, both reactants and products can not have zero 
	 * 		participants
	 * @param products {@link SurfaceReactionParticipants} -- must be non-null
	 * @param rate double -- must be non-negative
	 * 
	 */
	public SurfaceReaction(String reactionname, Surface surface, SurfaceReactionParticipants reactants,
			SurfaceReactionParticipants products, double rate) {
		SimulationUtilities.checkForNull("surface reaction constructor argument", reactionname, surface, reactants, products);
		if(reactants.getParticipants().length == 0 && products.getParticipants().length == 0) {
			SimulationUtilities.throwRuntimeException("a surface reaction must have at least 1 product or reactant");
		}
		SimulationUtilities.checkForNonNegative("reaction rate", rate);
		this.reactionname = reactionname;
		this.surface = surface;
		this.reactants = reactants;
		this.products = products;
		this.rate = rate;
	}


	public SurfaceReactionParticipants getReactants() {
		return reactants;
	}


	public SurfaceReactionParticipants getProducts() {
		return products;
	}
	
	
	public String getName() {
		return this.reactionname;
	}
	
	
	public double getRate() {
		return this.rate;
	}
	
	public void setRate(double newrate) {
		SimulationUtilities.checkForNonNegative("reaction rate", newrate);
		this.rate = newrate;
	}
	
	public Surface getSurface() {
		return this.surface;
	}
}
