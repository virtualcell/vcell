package org.vcell.sybil.gui.graph.nodes;

/*   ReactionNodeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to March 2009
 *   Shape for reaction nodes
 */

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.gui.graph.edges.EdgeShape;
import org.vcell.sybil.gui.graph.edges.EdgeShapeLeft;
import org.vcell.sybil.gui.graph.edges.EdgeShapeRight;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class NodeShapeReaction extends NodeShape {

	protected static int leftRightOffset = 14;
	
	protected Area icon;
	
	public NodeShapeReaction(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		setColorFGSelected(java.awt.Color.black);
		setColorBG(java.awt.Color.yellow);
	}

	public Point getEdgeHook(EdgeShape edge) {
		Point edgeHook = super.getEdgeHook(edge);
		if(edgeHook != null) {
			if(edge instanceof EdgeShapeLeft) { 
				edgeHook = new Point(edgeHook.x - leftRightOffset, edgeHook.y); 
			}
			if(edge instanceof EdgeShapeRight) { 
				edgeHook = new Point(edgeHook.x + leftRightOffset, edgeHook.y); 
			}
		}
		return edgeHook;
	}
	
	public void paint (Graphics2D g) {

		int absPosX = location.x();
		int absPosY = location.y();
		//
		// draw elipse and two circles
		//
		int diameter = size.height;
		int hOval = diameter/2;
		Graphics2D g2D = (Graphics2D)g;
		g2D.setColor(colorFG());
		if (icon == null){
			icon = new Area();
			icon.add(new Area(new Ellipse2D.Double(-diameter,-diameter/2,diameter,diameter)));
			icon.add(new Area(new Ellipse2D.Double(-diameter/2,-hOval/2,diameter,hOval)));
			icon.add(new Area(new Ellipse2D.Double(0,-diameter/2,diameter,diameter)));
		}
		Area movedIcon = 
			icon.createTransformedArea(
					AffineTransform.getTranslateInstance(absPosX, absPosY));
		
		g2D.draw(movedIcon);
		g2D.setColor(colorBG());
		g2D.fill(movedIcon);
		g2D.setColor(colorFG());
		//
		// draw label
		//
			//java.awt.FontMetrics fm = g.getFontMetrics();
			int textX = absPosX  + size.width/2 - labelSize.width/2;
			int textY = absPosY - size.height + labelSize.height - diameter;
			if (label()!=null && label().length()>0){
				g.drawString(label(),textX,textY);
			}
		return;
	}
}
