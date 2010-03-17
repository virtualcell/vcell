package org.vcell.sybil.models.graphcomponents;

import java.util.Set;

/*   SyCoContainer  --- by Oliver Ruebenacker, UCHC --- December 2007
 *   A mutable component of a graph, corresponding to a set of RDFNodes and Statements
 *   Unlike other SyCos, this one gives a real life and mutable set of its children
 */

public interface RDFGraphCompContainer extends RDFGraphComponent {

	public Set<RDFGraphComponent> children();
	
}
