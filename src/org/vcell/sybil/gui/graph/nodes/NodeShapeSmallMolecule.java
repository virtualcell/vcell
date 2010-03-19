package org.vcell.sybil.gui.graph.nodes;

/*   SmallMoleculeNodeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape for small Molecule nodes
 */

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class NodeShapeSmallMolecule extends NodeShape {
	
	int radius = 8;

	public NodeShapeSmallMolecule(Graph graphNew, RDFGraphComponent newSybComp) {
		super(graphNew, newSybComp);
		setColorFGSelected(java.awt.Color.black);
		setColorBG(java.awt.Color.blue);
	}

}
