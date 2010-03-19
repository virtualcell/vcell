package org.vcell.sybil.gui.graph;

/*   EllipseShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape used base class for nodes in Sybil
 */

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;


public class ElipseShape extends GraphShape {

	public ElipseShape (Graph graph, RDFGraphComponent newSybComp) {
		super(graph, newSybComp);
	}
		
	public void updatePrerequisites(Graphics2D g) {
		java.awt.FontMetrics fm = g.getFontMetrics();
		labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		labelSize.width = fm.stringWidth(label());		
	}

	public void updatePreferedSize(Graphics2D g) { preferedSize = getPreferedSize(g); }
	
	public void updatePositions(Graphics2D g) { 
		labelPos.x = location.x() - labelSize.width/2;
		labelPos.y = location.y() - labelSize.height/2;
	}
	
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		preferedSize.height = labelSize.height + 10;
		preferedSize.width = labelSize.width + 10;
		return preferedSize;
	}

	final double getRadius ( Point pick ) {
		double radiusX = pick.x - location.x();
		double radiusY = pick.y - location.y();
		double b = size.height/2;
		double a = size.width/2;
		return radiusX*radiusX/(a*a) + radiusY*radiusY/(b*b);
	}

	public final boolean isInside (Point p ) { return getRadius(p) < 1.0; }

	public void paint(Graphics2D g2D) {

		int absPosX = location.x();
		int absPosY = location.y();
		g2D.setColor(colorBG());
		g2D.fillOval(absPosX - size.width/2, absPosY - size.height/2, size.width, size.height);
		g2D.setColor(colorFG());
		g2D.drawOval(absPosX - size.width/2, absPosY - size.height/2, size.width, size.height);

		java.awt.FontMetrics fm = g2D.getFontMetrics();
		int textX = absPosX  - fm.stringWidth(label())/2;
		int textY = absPosY + size.height/2 + fm.getMaxAscent();
		if (label() != null && label().length() > 0){
			g2D.drawString(label(), textX, textY);
		}
		return;
	}
	
	public PaintLevel paintLevel() { return PaintLevel.Node; }
}