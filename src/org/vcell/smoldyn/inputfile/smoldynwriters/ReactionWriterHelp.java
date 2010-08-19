package org.vcell.smoldyn.inputfile.smoldynwriters;


import org.vcell.smoldyn.model.Participant;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.Species.StateType;
import org.vcell.smoldyn.model.util.ReactionParticipants;



/**
 * @author mfenwick
 *
 */
class ReactionWriterHelp {

	static String getSpeciesStateAsString(Species species, StateType statetype) {
		String out = species.getName() + "(" + statetype + ")";		
		return out;
	}
	
	static String getSurfaceReactionParticipantString(ReactionParticipants reactionparticipants) {
		String out;
//		if(reactionparticipants.getReactants().length == 0) {
//			out = "0";
//		} else if (reactionparticipants.get().length == 1) {
//			SpeciesState one = surfacereactionparticipants.getParticipants()[0];
//			out = ReactionWriterHelp.getSpeciesStateAsString(one);
//		} else {
//			SpeciesState [] srp = surfacereactionparticipants.getParticipants();
//			if(srp.length != 2) {
//				Utilities.throwUnexpectedException("number of participants in surface reaction should be <= 2 (is actually: <" + 
//						srp.length + ">)");
//			}
//			SpeciesState one = surfacereactionparticipants.getParticipants()[0];
//			SpeciesState two = surfacereactionparticipants.getParticipants()[1];
//			out = " " + ReactionWriterHelp.getSpeciesStateAsString(one) + " + " + ReactionWriterHelp.getSpeciesStateAsString(two);
//		}
		return null;
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
