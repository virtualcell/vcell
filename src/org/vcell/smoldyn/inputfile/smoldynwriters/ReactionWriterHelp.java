package org.vcell.smoldyn.inputfile.smoldynwriters;


import org.vcell.smoldyn.model.SpeciesAndState;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.Species.StateType;



/**
 * @author mfenwick
 *
 */
class ReactionWriterHelp {

	static String getSpeciesStateAsString(SpeciesAndState speciesandstate) {
		final Species species = speciesandstate.getSpecies();
		final StateType statetype = speciesandstate.getStatetype();
		final String out = species.getName() + "(" + statetype + ")";		
		return out;
	}
	
	static String getSurfaceReactionParticipantString(SpeciesAndState [] participants) {
		if(participants.length == 0) {
			return "0";
		} else if (participants.length == 1) {
			return getSpeciesStateAsString(participants[0]);
		} else if (participants.length == 2) {
			return getSpeciesStateAsString(participants[0]) + " + " + getSpeciesStateAsString(participants[1]);
		} else {
			Utilities.throwUnexpectedException("too many participants in surface reaction");
		}
		return null;//java is too stupid to recognize that control flow never gets here
	}

	static String getVolumeReactionParticipantString(Species [] participants) {
		if(participants.length == 0) {
			return "0";
		} else if (participants.length == 1) {
			return participants[0].getName();
		} else if (participants.length == 2) {
			return participants[0].getName() + " + " + participants[1].getName();
		} else {
			Utilities.throwUnexpectedException("too many participants in volume reaction");
		}
		return null;//java is actually too stupid to realize that control flow never gets here.  good job, java
	}

}
