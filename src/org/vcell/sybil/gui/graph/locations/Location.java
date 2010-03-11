package org.vcell.sybil.gui.graph.locations;

/*   Location  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Locations of shapes used by Sybil
 */

import java.awt.Dimension;
import java.awt.Point;
import java.util.Set;

public class Location<S extends Location.Owner<S>> implements BasicLocation {

	public static interface Owner<S extends Owner<S>> {
		public S parent();
		public Set<S> children();
		public Set<S> dependencies();
		public Set<S> dependents();
		public Location<S> location();
	}
	
	protected Owner<S> owner;
	protected Point p = new Point();
	protected Dimension d = new Dimension();
	protected boolean independent;
	
	public Location(Owner<S> ownerNew) { this(ownerNew, true); }
	
	public Location(Owner<S> ownerNew, boolean independentNew) { 
		owner = ownerNew; 
		independent = independentNew;
	}
	
	public int x() { return p.x; }
	public int y() { return p.y; }
	public Point p() { return p; };
	
	protected BasicLocation parentLoc() {
		S parent = owner.parent();
		BasicLocation parentLoc = parent != null ? parent.location() : BasicLocation.Origin; 
		return parentLoc != null ? parentLoc : BasicLocation.Origin;
	}

	protected void eventParentHasChanged() {
		BasicLocation parent = parentLoc();
		if(independent) {
			d.width = p.x - parent.x();
			d.height = p.y - parent.y();			
		} else {
			p.x = parent.x() + d.width;
			p.y = parent.y() + d.height;
			notifyChildren();
		}
	}
	
	protected void notifyChildren() {
		for(Owner<S> child : owner.children()) { child.location().eventParentHasChanged(); }
	}
	
	public boolean independent() { return independent; }
	public void setIndependent(boolean independentNew) { independent = independentNew; }
	
	public int setX(int xNew) {
		p.x = xNew;
		BasicLocation parent = parentLoc();
		if(parent != null) { d.width = p.x - parent.x(); }
		else { d.width = p.x; }
		notifyChildren();
		return p.x;
	}
	
	public int setY(int yNew) {
		p.y = yNew;
		BasicLocation parent = parentLoc();
		d.height = p.y - parent.y();
		notifyChildren();
		return p.y;
	}
	
	public Point setP(int xNew, int yNew) {
		p.x = xNew;
		p.y = yNew;
		BasicLocation parent = parentLoc();
		d.width = p.x - parent.x(); 
		d.height = p.y - parent.y(); 
		notifyChildren();
		return p;
	}
	
	public Point setP(Point pNew) {
		p.x = pNew.x;
		p.y = pNew.y;
		BasicLocation parent = parentLoc();		
		d.width = p.x - parent.x(); 
		d.height = p.y - parent.y(); 
		notifyChildren();
		return p;
	}
	
}
