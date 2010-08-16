package org.vcell.smoldyn.inputfile.smoldynwriters;

import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.SpeciesState;
import org.vcell.smoldyn.model.util.SurfaceReactionParticipants;
import org.vcell.smoldyn.model.util.VolumeReactionParticipants;



/**
 * @author mfenwick
 *
 */
public class ReactionWriterHelp {

	public static String getSpeciesStateAsString(SpeciesState speciesstate) {
		String out = speciesstate.getSpecies().getName() + "(" + speciesstate.getState() + ")";		
		return out;
	}
	
	public static String getReactionParticipantString(SurfaceReactionParticipants surfacereactionparticipants) {
		String out;
		if(surfacereactionparticipants.getParticipants().length == 0) {
			out = "0";
		} else if (surfacereactionparticipants.getParticipants().length == 1) {
			SpeciesState one = surfacereactionparticipants.getParticipants()[0];
			out = ReactionWriterHelp.getSpeciesStateAsString(one);
		} else {
			SpeciesState [] srp = surfacereactionparticipants.getParticipants();
			if(srp.length != 2) {
				Utilities.throwUnexpectedException("number of participants in surface reaction should be <= 2 (is actually: <" + 
						srp.length + ">)");
			}
			SpeciesState one = surfacereactionparticipants.getParticipants()[0];
			SpeciesState two = surfacereactionparticipants.getParticipants()[1];
			out = " " + ReactionWriterHelp.getSpeciesStateAsString(one) + " + " + ReactionWriterHelp.getSpeciesStateAsString(two);
		}
		return out;
	}

	
	public static String getReactionParticipantString(VolumeReactionParticipants volumeereactionparticipants) {
		String out;
		if(volumeereactionparticipants.getParticipants().length == 0) {
			out = "0";
		} else if (volumeereactionparticipants.getParticipants().length == 1) {
			Species one = volumeereactionparticipants.getParticipants()[0];
			out = one.getName();
		}else {
			Species [] vrp = volumeereactionparticipants.getParticipants();
			if (vrp.length != 2) {
				Utilities.throwUnexpectedException("number of participants in volume reaction should be <= 2 (is actually: <" +
						vrp.length + ">)");
			}
			Species one = volumeereactionparticipants.getParticipants()[0];
			Species two = volumeereactionparticipants.getParticipants()[1];
			out = one.getName() + " + " + two.getName();
		}
		return out;
	}
//	
//	public static String getDirectionalityAsString(SurfaceReaction surfacereaction) {
//		String directionality;
//		if (surfacereaction.isBidirectional()) {
//			directionality = " <-> ";
//		} else {
//			directionality = " -> ";
//		}
//		return directionality;
//	}
//	
//	public static String getDirectionalityAsString(VolumeReaction volumereaction) {
//		String directionality;
//		if (volumereaction.isBidirectional()) {
//			directionality = " <-> ";
//		} else {
//			directionality = " -> ";
//		}
//		return directionality;
//	}
}
