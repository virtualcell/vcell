package org.vcell.sybil.models.graph.manipulator;

/*   GraphManipulator  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   Generic interface for object executing graph manipulations.
 */

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public interface GraphManipulator<S extends UIShape<S>, G extends UIGraph<S, G>> {
	
	public void applyToGraph(G graph) throws GraphManipulationException;

}
