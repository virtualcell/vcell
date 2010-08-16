package org.vcell.smoldyn.model.util;


import java.util.ArrayList;

import org.vcell.smoldyn.model.SpeciesState;


/**
 * A surface reaction has up to two participants.  TODO what must be surface-bound
 * and what mustn't?  Thus, SurfaceReactionParticipants has 0 Species for a 0th-order reaction, 
 * 1 for a unimolecular reaction, and 2 for a bimolecular reaction.
 * 
 * @author mfenwick
 */
public class SurfaceReactionParticipants {

	private ArrayList<SpeciesState> participants = new ArrayList<SpeciesState>();
	// TODO although Smoldyn limits reactions to having only two reactants, they can probably have as many products as they want....
	//		perhaps this should be an array or ArrayList instead
	
	
	/**
	 * Constructs a new instance.  Either, both, or none of participant1 and participant2 may be null:
	 * set both to null to specify a 0th-order reaction, set either one to null to indicate a unimolecular reaction,
	 * and set neither to null to specify a 2nd-order reaction.
	 * 
	 * @param participant1
	 * @param participant2
	 */
	public SurfaceReactionParticipants(SpeciesState participant1, SpeciesState participant2) {
		if (participant1 != null) {
			this.participants.add(participant1);
		}
		if (participant2 != null) {
			this.participants.add(participant2);
		}
	}


	public SpeciesState [] getParticipants() {
		return this.participants.toArray(new SpeciesState [this.participants.size()]);
	}
		
}
