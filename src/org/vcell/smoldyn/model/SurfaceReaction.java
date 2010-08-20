package org.vcell.smoldyn.model;

import java.util.ArrayList;

import org.vcell.smoldyn.model.SpeciesAndState.Product;
import org.vcell.smoldyn.model.SpeciesAndState.Reactant;
import org.vcell.smoldyn.model.util.ReactionParticipants;
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

	private final ArrayList<SpeciesAndState> reactants = new ArrayList<SpeciesAndState>(2);
	private final ArrayList<SpeciesAndState> products = new ArrayList<SpeciesAndState>(2);
	private final String name;
	private final Surface surface;
	private final double rate;
	
	
	/**
	 * Constructs a new reaction which is localized to a surface.
	 * If reactants has 0 participants, this indicates a 0th order reaction.  If products has 0 participants, this indicates
	 * a reaction which destroys molecules.  If both have at least 1 participant, this is a 'normal' surface reaction.
	 *
	 * @param reactionname -- not null
	 * @param surface -- not null
	 * @param participants -- not null
	 * @param rate -- not negative
	 */
	public SurfaceReaction(String reactionname, Surface surface, ReactionParticipants participants, double rate) {
		SimulationUtilities.checkForNull("argument to surface reaction constructor", reactionname, surface, participants);
		SimulationUtilities.checkForNonNegative("surface reaction rate", rate);
		for(Reactant r : participants.getReactants()) {
			this.reactants.add(r);
		}
		for(Product p : participants.getProducts()) {
			this.products.add(p);
		}
		this.rate = rate;
		this.name = reactionname;
		this.surface = surface;
	}

	public String getName() {
		return this.name;
	}

	public double getRate() {
		return this.rate;
	}

	public Surface getSurface() {
		return this.surface;
	}
	
	public SpeciesAndState [] getReactants() {
		return this.reactants.toArray(new SpeciesAndState [this.reactants.size()]);
	}
	
	public SpeciesAndState [] getProducts() {
		return this.products.toArray(new SpeciesAndState [this.products.size()]);
	}
}
