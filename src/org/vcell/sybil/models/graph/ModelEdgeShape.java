package org.vcell.sybil.models.graph;

/*   ModelEdgeShape  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   Shapes for edges independently of the GUI.
 */

import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;

public interface ModelEdgeShape<S extends UIShape<S>> extends UIShape<S> {

	UIShape<S> startShape();
	UIShape<S> endShape();
	public RDFGraphCompEdge graphComp();


}
