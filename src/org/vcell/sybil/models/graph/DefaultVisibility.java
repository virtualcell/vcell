package org.vcell.sybil.models.graph;

/*   DefaultVisibility  --- by Oliver Ruebenacker, UCHC --- December 2007 to November 2009
 *   Visibility of a shape describes whether it is visible, whether it allows descendants to be visible
 *   and whether its descendants allow it to be visible.
 *   By definition:
 *   visibility.isExposed() == parent.exposesDescendants() || parent.isExposed()
 *   It is the responsibility of a visibility object to call all children if their exposure changes.
 */

import org.vcell.sybil.util.sets.BoundedHashSet;
import org.vcell.sybil.util.sets.BoundedSet;
import org.vcell.sybil.util.sets.BoundedSet.Event;
import org.vcell.sybil.util.sets.SetEmptyUpdater;

public class DefaultVisibility<S extends UIShape<S>> 
implements Visibility<S>, BoundedSet.Listener<Visibility.Hider>, 
SetEmptyUpdater.Listener<Visibility.Hider> {

	protected S shape;
	protected BoundedSet<Hider> hiders = new BoundedHashSet<Hider>();
	protected boolean hasHiders;
	protected boolean hidesFamily;
	protected boolean absorbsFamily;
	protected boolean displaysFamily;
	protected boolean isHidden;
	protected boolean isHiddenFamily;
	protected boolean isAbsorbedFamily;
	protected boolean isVisible;
	protected boolean childrenAreHidden;
	protected boolean childrenAreAbsorbed;
	protected boolean childrenNeedNotify;
	protected boolean dependentsNeedNotify;
	protected boolean isValid;
	
	public DefaultVisibility(S shape) { 
		this.shape = shape;
		hasHiders = false;
		new SetEmptyUpdater<Hider>(hiders).listeners().add(this);
		hidesFamily = false;
		absorbsFamily = false;
		childrenNeedNotify = true;
		dependentsNeedNotify = true;
		updateAfterNewParent();
	}
	
	public S shape() { return shape; }
	public BoundedSet<Hider> hiders() { return hiders; }
	public boolean hasHiders() { return hasHiders; }
	public boolean hidesFamily() { return hidesFamily; }
	public boolean absorbsFamily() { return absorbsFamily; }
	public boolean displaysFamily() { return displaysFamily; }
	public boolean isHidden() { return isHidden; }
	public boolean isHiddenFamily() { return isHiddenFamily; }
	public boolean isAbsorbedFamily() { return isAbsorbedFamily; }
	public boolean isVisible() { return isVisible; }

	public void setHidesItself(boolean hidesItselfNew) {
		if(hasHiders != hidesItselfNew) {
			hasHiders = hidesItselfNew;
			update();
		}
	}

	public void setAbsorbesFamily(boolean absorbsFamilyNew) {
		if(absorbsFamily != absorbsFamilyNew) {
			absorbsFamily = absorbsFamilyNew;
			childrenNeedNotify = true;
			update();
		}
	}

	public void setHidesFamily(boolean hidesFamilyNew) {
		if(hidesFamily != hidesFamilyNew) {
			hidesFamily = hidesFamilyNew;
			childrenNeedNotify = true;
			update();
		}
	}


	
	public void updateAfterNewParent() {
		S parentShape = shape().parent();
		Visibility<S> parentVis = (parentShape != null) ? parentShape.visibility() : null;
		boolean isHiddenFamilyNew = false;
		boolean isAbsorbedFamilyNew = false;
		if(parentVis != null) {
			isHiddenFamilyNew = parentVis.isHiddenFamily() || parentVis.hidesFamily();
			isAbsorbedFamilyNew = parentVis.isAbsorbedFamily() || parentVis.absorbsFamily();
		} 
		if((isHiddenFamily != isHiddenFamilyNew) || (isAbsorbedFamily != isAbsorbedFamilyNew)) {
			isHiddenFamily = isHiddenFamilyNew;
			isAbsorbedFamily = isAbsorbedFamilyNew;
		}
		update();
	}
	
	public boolean calculateIsHiddenNew() {
		return hasHiders || isHiddenFamily;
	}
	
	public void update() {
		boolean isHiddenNew = calculateIsHiddenNew();
		if(isHidden != isHiddenNew) {
			isHidden = isHiddenNew;
			dependentsNeedNotify = true;
		}
		isVisible = (!isAbsorbedFamily) && (!isHidden);
		boolean childrenAreHiddenNew = hidesFamily || isHiddenFamily;
		boolean childrenAreAbsorbedNew = absorbsFamily || isAbsorbedFamily;
		if((childrenAreHidden != childrenAreHiddenNew) || (childrenAreAbsorbed != childrenAreAbsorbedNew)) {
			childrenAreHidden = childrenAreHiddenNew;
			childrenAreAbsorbed = childrenAreAbsorbedNew;
			childrenNeedNotify = true;
		}
		displaysFamily = (!childrenAreHidden) && (!childrenAreAbsorbed);
		if(childrenNeedNotify) { notifyChildren(); }
		if(dependentsNeedNotify) { notifyDependents(); }
	}
	
	public void notifyChildren() {
		for(S childShape : shape.children()) {
			childShape.visibility().notifyMeAsChild(childrenAreHidden, childrenAreAbsorbed);
		}
		childrenNeedNotify = false;
	}

	public void notifyDependents() {
		for(S dependentShape : shape.dependents()) {
			Visibility<S> vis = dependentShape.visibility();
			if(vis instanceof DependentVisibility<?>) {
				((DependentVisibility<S>) vis).notifyMeAsDependent(this, isHidden);
			}
		}
		for(S child : shape.children()) { child.visibility().notifyDependents(); }
		dependentsNeedNotify = false;
	}

	public void notifyMeAsChild(boolean isHiddenFamilyNew, boolean isAbsorbedFamilyNew) {
		if((isHiddenFamily != isHiddenFamilyNew) || (isAbsorbedFamily != isAbsorbedFamilyNew)) {
			isHiddenFamily = isHiddenFamilyNew;
			isAbsorbedFamily = isAbsorbedFamilyNew;
		}
		update();
	}

	private void checkThisIsTrue(boolean test, String message) {
		isValid = isValid && test;
		if(!test) { 
			System.out.println("Problem with " + shape);
			System.out.println("Message: " + message); 
		}
	}
	
	public boolean validate() {
		isValid = true;
		checkThisIsTrue((displaysFamily == ((!childrenAreHidden) && 
				(!childrenAreAbsorbed))), "displaysFamily wrong");
		checkThisIsTrue((isHidden == calculateIsHiddenNew()), "isHidden wrong");
		checkThisIsTrue((isVisible == ((!isHidden) && (!isAbsorbedFamily))), "isVisible wrong");
		checkThisIsTrue((childrenAreHidden == (isHiddenFamily || hidesFamily)), "childrenAreHidden wrong");
		checkThisIsTrue((childrenAreAbsorbed == (isAbsorbedFamily || absorbsFamily)), 
				"childrenAreAbsorbed wrong");
		for(S childShape : shape.children()) {
			Visibility<S> childVis = childShape.visibility();
			checkThisIsTrue((childVis.isHiddenFamily() == childrenAreHidden), 
					"isHiddenFamily wrong with child " + childShape);
			checkThisIsTrue((childVis.isAbsorbedFamily() == childrenAreAbsorbed), 
					"is AbsorbedFamily wrong with child " + childShape);
		}
		for(S depShape : shape.dependents()) {
			Visibility<S> depVis = depShape.visibility();
			if(depVis instanceof EdgeVisibility<?>) {
				EdgeVisibility<S> depEdgeVis = (EdgeVisibility<S>) depVis;
				checkThisIsTrue(depEdgeVis.validateDependencies(), 
						"Dependency hidden wrong for dependent " + depShape);
			}
		}
		return isValid;
	}
	
	public void dump() {
		System.out.println("hidesItself = " + hasHiders);
		System.out.println("hidesFamily = " + hidesFamily);
		System.out.println("absorbsFamily = " + absorbsFamily);
		System.out.println("displaysFamily = " + displaysFamily);
		System.out.println("isHidden = " + isHidden);
		System.out.println("isHiddenFamily = " + isHiddenFamily);
		System.out.println("isAbsorbedFamily = " + isAbsorbedFamily);
		System.out.println("isVisible = " + isVisible);
		System.out.println("childrenAreHidden = " + childrenAreHidden);
		System.out.println("childrenAreAbsorbed = " + childrenAreAbsorbed);
		System.out.println("childrenNeedNotify = " + childrenNeedNotify);
		System.out.println("dependentsNeedNotify = " + dependentsNeedNotify);
	}

	public void fireEvent(Event<Hider> event) { update(); }
	public void eventBecameEmpty(BoundedSet<Hider> set) { setHidesItself(false); }
	public void eventBecameNotEmpty(BoundedSet<Hider> set) { setHidesItself(true); }

}
