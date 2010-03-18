package org.vcell.sybil.models.graph;

/*   Visibility  --- by Oliver Ruebenacker, UCHC --- December 2007 to January 2009
 *   Visibility of a shape describes whether it is visible, whether it allows descendants to be visible
 *   and whether its descendants allow it to be visible.
 *   By definition:
 *   visibility.isExposed() == parent.exposesDescendants() && parent.isExposed()
 *   It is the responsibility of a visibility object to call all children if their exposure changes.
 */

import java.util.Set;

public interface Visibility<S extends UIShape<S>> {

	public static interface Hider {};
	
	public static final Hider hiderSelection = new Hider() {};
	public static final Hider hiderFilter = new Hider() {};
	
	public S shape();
	public boolean hasHiders();
	public boolean hidesFamily();
	public boolean absorbsFamily();
	public boolean displaysFamily();
	public boolean isHidden();
	public boolean isHiddenFamily();
	public boolean isAbsorbedFamily();
	public boolean isVisible();
	public Set<Hider> hiders();
	public void setHidesItself(boolean hidesItselfNew);
	public void setHidesFamily(boolean hidesFamilyNew);
	public void setAbsorbesFamily(boolean absorbsFamilyNew);
	public void updateAfterNewParent();
	public void update();
	public void notifyChildren();
	public void notifyMeAsChild(boolean isHiddenFamily, boolean isAbsorbedFamily);
	public void notifyDependents();
	public boolean validate();
	public void dump();
	
}
