package org.vcell.sybil.models.graph;

/*   ModelShape  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   Shapes independently of the GUI.
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.util.iterators.FilterIter;
import org.vcell.sybil.util.iterators.NestedIter;
import org.vcell.sybil.util.iterators.SmartIter;
import org.vcell.sybil.util.sets.SetOfNone;


public abstract class SimpleModelShape<S extends UIShape<S>, G extends UIGraph<S, G>> 
implements UIShape<S> {

	public static class VisibilityTester<S extends UIShape<S>> implements FilterIter.Tester<S> {
		public boolean accepts(S shape) { return shape.visibility().isVisible(); }
	}

	protected RDFGraphComponent graphComp;
	protected Visibility<S> visibility;
	protected S parent;
	protected Set<S> children = new HashSet<S>();
	protected Set<S> dependents = new HashSet<S>();
	protected G graph;
	
	public Visibility<S> visibility() { return visibility; }

	public SimpleModelShape(G graph, RDFGraphComponent syCo) {
		this.graph = graph;
		this.graphComp = syCo;
	}

	public abstract void setParent(S shape);
	public abstract S getThisShape();
	
	public void addChildShape(S shape) {
		S oldParent = shape.parent();
		if(oldParent != null) { oldParent.children().remove(shape); }
		children.add(shape);
		shape.setParent(getThisShape());
	}

	public S parent() { return parent; }
	public Set<S> children() { return children; }
	public Set<S> dependents() { return dependents; }
	public Set<S> dependencies() { return new SetOfNone<S>(); }
	
	public SmartIter<S> visibleChildren() {
		return new FilterIter<S>(children.iterator(), new VisibilityTester<S>());
	}

	public SmartIter<S> descendants() {
		NestedIter<S> iter = new NestedIter<S>();
		iter.add(visibleChildren());
		for(S child : children) { 
			if(child.visibility().displaysFamily()) { iter.add(child.descendants()); }
		}
		return iter;
	}

	public int countDescendants() {
		int num = 0;
		for(S child : children) { 
			if(child.visibility().isVisible()) { ++num; }
			if(child.visibility().displaysFamily()) { num += child.countDescendants(); }
		}
		return num;		
	}

	public boolean isDescendant(S shape) {
		if(this.visibility().displaysFamily()) {
			for(S child : children) {
				if (child == shape) { return child.visibility().isVisible(); }
				boolean childHasShape = child.isDescendant(shape);	
				if (childHasShape) return true;
			}			
		}
		return false;
	}

	public S nextVisibleThisOrAncestor() {
		if(visibility.isVisible()) return getThisShape();
		S ancestor = getThisShape();
		while(ancestor.visibility().isAbsorbedFamily() && (ancestor.parent() != null)) {
			ancestor = ancestor.parent();
			if(ancestor.visibility().isVisible()) { return ancestor; }
		}
		return null;
	}

	public RDFGraphComponent graphComp() { return graphComp; }

}
