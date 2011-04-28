package cbit.vcell.model;

/*  Useful static methods concerning structures
 *  November 2010
 */

public class StructureUtil {

	public static boolean isAdjacentMembrane(Membrane membrane, Feature feature) {
		return membrane.getInsideFeature() == feature || membrane.getOutsideFeature() == feature;
	}
	
	public static boolean areAdjacent(Structure structure1, Structure structure2) {
		if(structure1 instanceof Membrane && structure2 instanceof Feature) {
			return isAdjacentMembrane((Membrane) structure1, (Feature) structure2);
		} else if(structure2 instanceof Membrane && structure1 instanceof Feature) {
			return isAdjacentMembrane((Membrane) structure2, (Feature) structure1);
		}
		return false;
	}
	
	public static boolean reactionHereCanHaveParticipantThere(Structure structureReaction,
			Structure structureSpeciesContext) {
		if(structureReaction == null || structureSpeciesContext == null) { return false; }
		if(structureReaction == structureSpeciesContext) {
			return true;
		} 
		if(structureReaction instanceof Membrane && structureSpeciesContext instanceof Feature) {
			Membrane membraneReaction = (Membrane) structureReaction;
			Feature featureSpeciesContext = (Feature) structureSpeciesContext;
			return isAdjacentMembrane(membraneReaction, featureSpeciesContext);
		}
		return false;
	}
	
}
