package org.vcell.sybil.gui.graph.edges;

/*   EdgeShapeController  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape for edge representing the BioPAX Level 2 property CONTROLLER
 */

import java.awt.BasicStroke;
import java.awt.Stroke;
import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;


public class EdgeShapeController extends EdgeShape {

	public EdgeShapeController(Graph graphNew, RDFGraphCompEdge sybCompNew) { 
		super(graphNew, sybCompNew); 
	}
	
	protected static final java.awt.BasicStroke DOTTED_STROKE =
		new java.awt.BasicStroke(1,java.awt.BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,
				10f,new float[] { 1,3 }/*DASH_ARRAY*/,10f);

	public void paint(java.awt.Graphics2D g2D) {
		if((!haveHooks) || start == null || end == null) { return; }
		Stroke oldStroke = g2D.getStroke();
		g2D.setStroke(DOTTED_STROKE);
		super.paintEdge(g2D);
		g2D.setStroke(oldStroke);
	}
	
}
