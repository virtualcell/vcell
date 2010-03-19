package org.vcell.sybil.gui.graph.nodes;

/*   ComplexNodeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape for simple node representing a complex
 */

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class NodeShapeComplex extends NodeShape {
	int radius = 8;

	public NodeShapeComplex(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		setColorFGSelected(java.awt.Color.black);
		setColorBG(java.awt.Color.magenta);
	}

}
