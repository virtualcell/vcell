package org.vcell.sybil.gui.graph.edges;

/*   SybEdgeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape for edges representing the BioPAX Level 2 property RIGHT
 */

import java.awt.Graphics2D;

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;
import org.vcell.sybil.util.gui.ArrowPainter;


public class EdgeShapeLeft extends EdgeShape {

	public EdgeShapeLeft(Graph graphNew, RDFGraphCompEdge sybCompNew) { 
		super(graphNew, sybCompNew); 
	}
	
	@Override
	public void paint(Graphics2D g2D) {
		if((!haveHooks) || start == null || end == null) { return; }
		super.paintEdge(g2D);
		ArrowPainter.paintArrow(g2D, end, start, ARROW_LENGTH, ARROW_WIDTH);
	}
	
}
