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

import java.util.Set;

/*   SyCoContainer  --- by Oliver Ruebenacker, UCHC --- December 2007
 *   A mutable component of a graph, corresponding to a set of RDFNodes and Statements
 *   Unlike other SyCos, this one gives a real life and mutable set of its children
 */

public interface RDFGraphCompContainer extends RDFGraphComponent {

	public Set<RDFGraphComponent> children();
	
}
