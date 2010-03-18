package org.vcell.sybil.models.graph;

/*   ModelShape  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   Shapes independently of the GUI.
 */

import java.util.Set;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.util.iterators.FilterIter;
import org.vcell.sybil.util.iterators.SmartIter;


public interface UIShape<S extends UIShape<S>> {

	public static class VisibilityTester<S extends UIShape<S>> implements FilterIter.Tester<S> {
		public boolean accepts(S shape) { return shape.visibility().isVisible(); }
	}

	public Visibility<S> visibility();

	public void setParent(S shape);
	
	public void addChildShape(S shape);
	
	public S parent();
	public Set<S> children();
	public void centerOn(S shape);
	public void setLocationIndependent(boolean independentNew);
	public boolean locationIndependent();
	public Set<S> dependents();
	public Set<S> dependencies();
	
	public SmartIter<S> directDescendants();

	public SmartIter<S> descendants();

	public int countDescendants();

	abstract boolean isDescendant(S shape);

	public S nextVisibleThisOrAncestor();

	public RDFGraphComponent graphComp();

}
