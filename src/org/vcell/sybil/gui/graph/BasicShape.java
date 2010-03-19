package org.vcell.sybil.gui.graph;

/*   BasicShape  --- by Oliver Ruebenacker, UCHC --- January 2008 to February 2009
 *   Shapes used by Sybil
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.SimpleModelShape;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.util.text.StringUtil;


public abstract class BasicShape<S extends BasicShape<S, G>, G extends UIGraph<S, G>> 
extends SimpleModelShape<S, G> implements Location.Owner<S> {
	
	protected Location<S> location;
	protected Dimension size = new Dimension();
	protected Dimension preferedSize = new Dimension();
	protected Dimension labelSize = new Dimension();
	protected Point labelPos = new Point();
	protected boolean bNoFill = false;
	protected Color colorBG = java.awt.Color.white;
	protected Color colorFG = java.awt.Color.black;
	protected Color colorBGSelected = java.awt.Color.red;
	protected Color colorFGSelected = java.awt.Color.white;
	
	public BasicShape (G graphNew, RDFGraphComponent newSybComp) { 
		super(graphNew, newSybComp); 
		location = new Location<S>(this);
	}

	public G graph() { return graph; }
	
	public abstract Dimension getPreferedSize(Graphics2D g);
	public Dimension size() { return size; }
	public String label() { return StringUtil.trim(graphComp().label(), Shape.LABEL_MAX_LENGTH); }
	public final String name() { return graphComp().name(); }
	public int xMin() { return location.x() - size.width/2; }
	public int yMin() { return location.y() - size.height/2; }
	public int xMax() { return xMin() + size.width; }
	public int yMax() { return yMin() + size.height; }	
	protected abstract boolean isInside(Point p);
	public final boolean isOnBorder(Point p) { return false; }
	public boolean isSelected() { return graph.model().selectedComps().contains(graphComp()); }
	protected void setColorBG(Color newColor) { colorBG = newColor; }
	protected void setColorBGSelected(Color newColor) { colorBGSelected = newColor; }

	protected Color colorBG() { 
		if(isSelected()) { return colorBGSelected; }
		else { return colorBG; }
	}

	protected void setColorFG(Color newColor) { colorFG = newColor; }
	protected void setColorFGSelected(Color newColor) { colorFGSelected = newColor; }

	protected Color colorFG() { 
		if(isSelected()) { return colorFGSelected; }
		else { return colorFG; }
	}

	public abstract void paint(Graphics2D g);
	public void select() { graph().select(getThisShape()); }
	
	public void unselect() { 
		graph.model().selectedComps().remove(this.graphComp()); 
		graph.model().listenersUpdate(); 
	}

	public String toString() {
		return StringUtil.trimJavaIdentifier(getClass().getName()) + "@" + Integer.toHexString(hashCode()) 
		+ "(" + label() + ")   " + StringUtil.trimJavaIdentifier(this.graphComp().getClass().getName()) 
		+ "@" + Integer.toHexString(graphComp().hashCode()) + "(" + graphComp.label()+")"; 
	}

	public void setParent(S shape) {
		parent = shape;
		shape.visibility().updateAfterNewParent();
	}

	public Location<S> location() { return location; }
	public Point p() { return location.p(); }
	
	public void centerOn(S shape) { location.setP(shape.p()); }
	
	public void setLocationIndependent(boolean independentNew) { location.setIndependent(independentNew); }
	public boolean locationIndependent() { return location.independent(); }
	
}
