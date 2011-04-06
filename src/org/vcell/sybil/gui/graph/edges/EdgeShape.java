package org.vcell.sybil.gui.graph.edges;

/*   EdgeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to March 2009
 *   Shape for edges of graphs for Sybil
 */

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Set;

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.gui.graph.GraphShape;
import org.vcell.sybil.gui.graph.Shape;
import org.vcell.sybil.gui.graph.nodes.NodeShape;
import org.vcell.sybil.models.graph.EdgeVisibility;
import org.vcell.sybil.models.graph.ModelEdgeShape;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;
import org.vcell.sybil.util.sets.SetOfTwo;

public abstract class EdgeShape extends GraphShape implements ModelEdgeShape<Shape> {
	
	protected NodeShape startShape = null;
	protected NodeShape endShape = null;
	protected Set<Shape> dependencies;
	
	protected Point start = new Point();
	protected Point end = new Point();
	protected boolean haveHooks = false;

	protected Line2D.Double curve = null;
	protected static final java.awt.BasicStroke DASHED_STROKE =
		new java.awt.BasicStroke(1, java.awt.BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, 
				new float[] { 5,3 }/*DASH_ARRAY*/, 10f);
	public static final int ARROW_WIDTH = 8;
	public static final int ARROW_LENGTH = 12;

	public EdgeShape (Graph graphNew, RDFGraphCompEdge sybCompNew) {
		super(graphNew, sybCompNew);
		startShape = (NodeShape) graphNew.shapeMap().get(sybCompNew.startComp());
		startShape.dependents().add(this);
		endShape = (NodeShape) graphNew.shapeMap().get(sybCompNew.endComp());		
		endShape.dependents().add(this);
		dependencies = new SetOfTwo<Shape>(startShape, endShape);
		visibility = new EdgeVisibility<Shape>(this);
		setColorFG(java.awt.Color.black);
		setColorFGSelected(java.awt.Color.red);
		updatePreferedSize = true;
		updateScreenSize = true;
		usePreferedForScreenSize = false;
		setLocationIndependent(false);
	}
	
	public NodeShape startShape() { return startShape; }
	public NodeShape endShape() { return endShape; }
	@Override
	public RDFGraphCompEdge graphComp() { return (RDFGraphCompEdge) super.graphComp(); }
	@Override
	public EdgeVisibility<Shape> visibility() { return (EdgeVisibility<Shape>) visibility; }
	@Override
	public Set<Shape> dependencies() { return dependencies; }

	@Override
	public void updatePrerequisites(Graphics2D g) {
		haveHooks = (startShape != null) && (endShape != null);
		if (haveHooks) { 
			Point startHook = startShape.getEdgeHook(this);
			Point endHook = endShape.getEdgeHook(this);
			if(startHook != null && endHook != null) {
				start = startHook;  
				end = endHook; 				
			}
		}
		FontMetrics fm = g.getFontMetrics();
		labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		labelSize.width = fm.stringWidth(label());
	}
	
	@Override
	public void updatePreferedSize(Graphics2D g) {
		preferedSize = getPreferedSize(g);
	}

	@Override
	public void updateScreenSize(Graphics2D g) {
		if(haveHooks && start != null && end != null) {
			size.width = Math.abs(start.x-end.x);
			size.height = Math.abs(start.y-end.y);		
		}
	}
	
	@Override
	public void updatePositions(Graphics2D g) {
		if(haveHooks && start != null && end != null) {
			location.setP((start.x + end.x) / 2, (start.y + end.y)/2);
			labelPos.x = location.x() - labelSize.width/2;
			labelPos.y = location.y() - labelSize.height/2;			
		}
	}
	
	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		return new Dimension(labelSize.height + 10, labelSize.width + 10);
	}

	public void layout() {
		if(start == null || end == null) { return; }
		visibility().setIsTooShort(Math.abs(start.x - end.x) + Math.abs(start.y -end.y) <5);
	}

	@Override
	public final boolean isInside (Point p ) {
		return haveHooks && curve.intersects(p.getX()-2,p.getY()-2,4,4);
	}

	public void paintEdge(Graphics2D g2D) {
		g2D.setRenderingHint(
				java.awt.RenderingHints.KEY_ANTIALIASING,
				java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		paint0(g2D);
	}

	private void paint0(Graphics2D g2D) {
		if(start != null && end != null) {
			g2D.setColor(colorFG());
			curve = new Line2D.Double(start, end); 
			g2D.draw(curve);
		}
	}
	
	@Override
	public PaintLevel paintLevel() { return PaintLevel.Edge; }

}