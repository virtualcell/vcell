package org.vcell.smoldyn.model.util;


import java.util.ArrayList;

import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.VolumeReaction;


/**
 * A {@link VolumeReaction} is between two {@link Species} in solution.  Thus, VolumeReactionParticipants
 * has 0 Species for a 0th-order reaction, 1 for a unimolecular reaction, and 2 for a bimolecular reaction.
 * 
 * @author mfenwick
 *
 */
public class VolumeReactionParticipants {

	private ArrayList<Species> participants = new ArrayList<Species>();
	
	
	/**
	 * Constructs a new instance.  Either, both, or none of participant1 and participant2 may be null:
	 * set both to null to specify a 0th-order reaction, set either one to null to indicate a unimolecular reaction,
	 * and set neither to null to specify a 2nd-order reaction.
	 * 
	 * @param participant1
	 * @param participant2
	 */
	public VolumeReactionParticipants(Species participant1, Species participant2) {
		if (participant1 != null) {
			this.participants.add(participant1);
		}
		if (participant2 != null) {
			this.participants.add(participant2);
		}
	}


	public Species [] getParticipants() {
		return this.participants.toArray(new Species [this.participants.size()]);
	}

}
