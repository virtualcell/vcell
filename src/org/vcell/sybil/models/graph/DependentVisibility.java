package org.vcell.sybil.models.graph;

/*   DependentVisibility  --- by Oliver Ruebenacker, UCHC --- March 2008
 *   DependentVisibility is for a shape which requires other shapes to be visible to be visible itself
 *   By definition:
 *   visibility.isExposed() == parent.exposesDescendants() && parent.isExposed()
 *   It is the responsibility of a visibility object to call all children if their exposure changes.
 */

public interface DependentVisibility<S extends UIShape<S>> extends Visibility<S> {

	public void notifyMeAsDependent(Visibility<S> depVis, boolean depIsHidden);

}
