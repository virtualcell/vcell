package org.vcell.sybil.gui.graph.nodes;

/*   NodeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   Shapes of Nodes 
 */

import java.awt.*;

import org.vcell.sybil.gui.graph.ElipseShape;
import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class NodeShape extends ElipseShape {

	public NodeShape(Graph graph, RDFGraphComponent newSybComp) {
		super(graph, newSybComp);
		setColorFGSelected(java.awt.Color.black);
		setColorBG(java.awt.Color.green);
		setLocationIndependent(true);
	}

	@Override
	public void updatePositions(Graphics2D g) {
		labelPos.x = (size.width/2) - labelSize.width/2;
		labelPos.y = 0;		
	}

	@Override
	public void resize(Graphics2D g, Dimension newSize) { return; }

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		preferedSize.width = 20;
		preferedSize.height = 16;
		return preferedSize;
	}

	@Override
	public void updateOtherSizes(Graphics2D g) {
		labelSize.width = g.getFontMetrics().stringWidth(label());
		labelSize.height = g.getFontMetrics().getMaxAscent()+g.getFontMetrics().getMaxDescent();
	}

}
