package org.vcell.smoldyn.model;


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
	public SurfaceReaction() {
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getRate() {
		// TODO Auto-generated method stub
		return 0;
	}

}
