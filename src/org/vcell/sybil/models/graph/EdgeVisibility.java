package org.vcell.sybil.models.graph;

/*   EdgeVisibility  --- by Oliver Ruebenacker, UCHC --- March 2008
 *   Visibility of an EdgeShape, which has the V's of its end points as dependencies.
 */

public class EdgeVisibility<S extends UIShape<S>> 
extends DefaultVisibility<S> implements DependentVisibility<S> {

	protected boolean depStartIsHidden;
	protected boolean depEndIsHidden;
	protected boolean isTooShort;
	
	public EdgeVisibility(S shape) {
		super(shape);
		depStartIsHidden = edgeShape().startShape().visibility().isHidden();
		depEndIsHidden = edgeShape().endShape().visibility().isHidden();
		update();
	}

	public ModelEdgeShape<S> edgeShape() { return (ModelEdgeShape<S>) shape(); }
	
	public void notifyMeAsDependent(Visibility<S> depVis, boolean depIsHidden) {
		if(depVis.shape() == edgeShape().startShape()) { depStartIsHidden = depIsHidden; } 
		else if(depVis.shape() == edgeShape().endShape()) { depEndIsHidden = depIsHidden; }
		update();
	}
	
	public void setIsTooShort(boolean isTooShortNew) {
		isTooShort = isTooShortNew;
		update();
	}

	public boolean validateDependencies() {
		return (depStartIsHidden == edgeShape().startShape().visibility().isHidden()) && 
		(depEndIsHidden == edgeShape().endShape().visibility().isHidden());
	}
	
	@Override
	public boolean calculateIsHiddenNew() {
		/*System.out.println("Edge is hidden: " + 
				(hidesItself || isHiddenFamily || depStartIsHidden || depEndIsHidden || isTooShort)
				+ "   hides itself : " + hidesItself + "   is hidden family: " + isHiddenFamily 
				+ "   start hidden: " + depStartIsHidden + "   end hidden: " + depEndIsHidden
				+ "   too short: " + isTooShort); */
		return hasHiders || isHiddenFamily || depStartIsHidden || depEndIsHidden || isTooShort;
	}
	
	@Override
	public void dump() {
		System.out.println("depStartIsHidden = " + depStartIsHidden);
		System.out.println("depEndIsHidden = " + depEndIsHidden);
		System.out.println("isTooShort = " + isTooShort);
		super.dump();
	}
		
}
