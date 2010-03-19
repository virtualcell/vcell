package org.vcell.sybil.gui.graph;

/*   GraphShape  --- by Oliver Ruebenacker, UCHC --- August 2007 to January 2009
 *   Shapes to build a graph, most notably with the ability to be invisible
 */

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public abstract class GraphShape extends Shape {

	public GraphShape(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
	}
	
}
