package org.vcell.sybil.models.sbbox.util;

/*   LocationUtil  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   Useful static methods for Locations
 */

import java.util.HashMap;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;

public class LocationUtil {

	protected static class LocationScore {
		public int votesDirect, votesAsSurrounding, votesAsSurrounded;
		public int total() { return votesDirect + votesAsSurrounding + votesAsSurrounded; }
	}
	
	@SuppressWarnings("serial")
	protected static class LocationScoreMap extends HashMap<SBBox.Location, LocationScore> {
		public LocationScore get(Object object) {
			LocationScore locationScore = super.get(object);
			if(locationScore == null && object instanceof SBBox.Location) {
				locationScore = new LocationScore();
				put((SBBox.Location) object, locationScore);
			}
			return locationScore;
		}
	}
	
	public static SBBox.Location 
	bestLocationForParticipants(Set<? extends SBBox.Participant> participants) {
		LocationScoreMap scores = new LocationScoreMap();
		for(SBBox.Participant participant : participants) {
			SBBox.Species species = participant.species();
			if(species != null) {
				SBBox.Location location = species.location();
				if(location != null) {
					scores.get(location).votesDirect++;
					SBBox.Location surrounding = location.locationSurrounding();
					if(surrounding != null) { scores.get(surrounding).votesAsSurrounding++; }
					for(SBBox.Location surrounded : location.locationsSurrounded()) {
						scores.get(surrounded).votesAsSurrounded++;
					}
				}
			}
		}
		SBBox.Location bestLocation = null;
		LocationScore bestScore = new LocationScore();
		for(SBBox.Location location : scores.keySet()) {
			if(bestLocation == null) {
				bestLocation = location;
				bestScore = scores.get(location);
			} else {
				LocationScore score = scores.get(location);
				if(score.total() > bestScore.total() || 
						(score.total() == bestScore.total() && 
								score.votesDirect > bestScore.votesDirect)) {
					bestLocation = location;
					bestScore = score;
				}
			}
		}
		return bestLocation;
	}
	
}
