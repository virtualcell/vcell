package cbit.vcell.model;

/*  Useful static methods concerning structures
 *  November 2010
 */

public class StructureUtil {

	public static boolean areAdjacent(Structure structure1, Structure structure2) {
		if(structure1 instanceof Membrane) {
			Membrane membrane1 = (Membrane) structure1;
			if(membrane1.getInsideFeature().equals(structure2) || 
					membrane1.getOutsideFeature().equals(structure2)) {
				return true;
			}
		}
		if(structure2 instanceof Membrane) {
			Membrane membrane2 = (Membrane) structure2;
			if(membrane2.getInsideFeature().equals(structure1) || 
					membrane2.getOutsideFeature().equals(structure1)) {
				return true;
			}
		}
		return false;
	}
	
}
