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

/*   ModelEdgeShape  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   Shapes for edges independently of the GUI.
 */

import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;

public interface ModelEdgeShape<S extends UIShape<S>> extends UIShape<S> {

	UIShape<S> startShape();
	UIShape<S> endShape();
	public RDFGraphCompEdge graphComp();


}
