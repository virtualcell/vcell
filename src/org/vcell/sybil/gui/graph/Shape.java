package org.vcell.sybil.gui.graph;

/*   Shape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shapes used by Sybil. 
 */

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ConcurrentModificationException;

import org.vcell.sybil.gui.graph.edges.EdgeShape;
import org.vcell.sybil.models.graph.DefaultVisibility;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.util.exception.CatchUtil;


public abstract class Shape extends BasicShape<Shape, Graph> {

	public static final int LABEL_MAX_LENGTH = 28;
	public static enum PaintLevel { Container, Edge, Node }
	protected boolean updateScreenSize = true;
	protected boolean updatePreferedSize = true;
	protected boolean usePreferedForScreenSize = true;
	protected Font boldFont = null;
	
	public Shape (Graph graphNew, RDFGraphComponent newSybComp) {
		super(graphNew, newSybComp);
		visibility = new DefaultVisibility<Shape>(this); 
	}

	public Shape getThisShape() { return this; }
	
	public Shape pick(Point point) {
		Shape frontShape = null;
		if(visibility.isVisible() && isInside(point)) { frontShape = this; }
		if (visibility.displaysFamily()) {
			for(Shape child : children) {
				Shape childPick = child.pick(point);
				if (childPick != null) {
					if(frontShape == null) {
						frontShape = childPick;
					} else {
						if(childPick.paintLevel().compareTo(frontShape.paintLevel()) >= 0) {
							frontShape = childPick;
						}
					}
				}
			}			
		}
		return frontShape;
	}

	public void resize(java.awt.Graphics2D g, Dimension newSize) { size = newSize; }

	public Point getEdgeHook(EdgeShape edge) {
		Shape shape = nextVisibleThisOrAncestor();
		if(shape == null || shape == graph.topShape()) { return null; }
		return shape.p();
	}

	public boolean contains(Shape shape) { return children.contains(shape); }
	
	public void updateSizes(Graphics2D g) {
		updatePrerequisites(g);
		if(updatePreferedSize) { updatePreferedSize(g); }
		if(updateScreenSize) {
			if(usePreferedForScreenSize) { size = preferedSize; }
			else { updateScreenSize(g); }
		}
		updateOtherSizes(g);
		updatePositions(g);
	}

	public void updatePrerequisites(Graphics2D g) {  }
	public void updatePreferedSize(Graphics2D g) { preferedSize = getPreferedSize(g); }
	public void updateScreenSize(Graphics2D g) { if(size == null) { size = new Dimension(); } }

	public void updateOtherSizes(Graphics2D g) { 
		if(labelSize == null) { labelSize = new Dimension(size); }
	};	
	
	public void updatePositions(Graphics2D g) { 
		labelPos = new Point(location.x() - labelSize.width/2 , 
				location.y() - labelSize.height/2);
	}
	
	public abstract void paint(Graphics2D g);

	public abstract PaintLevel paintLevel();
	
	public void paintFamily(Graphics2D g2D) {
		for(PaintLevel level : PaintLevel.values()) { paintFamily(g2D, level); }
	}
	
	public void paintFamily(Graphics2D g2D, PaintLevel level) {
		try { 
			updateSizes(g2D);
			if(level == paintLevel() && visibility().isVisible()) { 
				paint(g2D); 
			}
		}
		catch (ConcurrentModificationException e) { CatchUtil.handle(e, CatchUtil.RecordSilently); }
		if(visibility().displaysFamily()) { 
			for(Shape child : children) { 
				child.paintFamily(g2D, level); 
			}
		}
	}

	public Font getBoldFont(Graphics g) {
		if(boldFont == null){
			boldFont = g.getFont().deriveFont(Font.BOLD);
		}
		return boldFont;
	}
	
}