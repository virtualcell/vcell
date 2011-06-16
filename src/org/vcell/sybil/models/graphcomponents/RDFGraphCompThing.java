/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graphcomponents;

/*   SyCoResourceLike  --- by Oliver Ruebenacker, UCHC --- December 2007 to November 2009
 *   A component of a graph, corresponding to a single RDFNode
 */

import org.vcell.sybil.models.sbbox.SBBox.NamedThing;

import com.hp.hpl.jena.rdf.model.Resource;

public interface RDFGraphCompThing extends RDFGraphComponent {

	public NamedThing thing();
	public Resource type();
}
