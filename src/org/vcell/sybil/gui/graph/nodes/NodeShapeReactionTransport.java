package org.vcell.sybil.gui.graph.nodes;

/*   ReactionTransportNodeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape for nodes representing a reaction with a transport
 */

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class NodeShapeReactionTransport extends NodeShape {
	
	int radius = 12;

	public NodeShapeReactionTransport(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		setColorFGSelected(java.awt.Color.black);
		setColorBG(java.awt.Color.pink);
	}

}
