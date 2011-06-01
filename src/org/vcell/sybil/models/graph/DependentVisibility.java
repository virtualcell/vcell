/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
