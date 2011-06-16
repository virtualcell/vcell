/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graph.manipulator;

/*   GraphManipulator  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   Generic interface for object executing graph manipulations.
 */

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public interface GraphManipulator<S extends UIShape<S>, G extends UIGraph<S, G>> {
	
	public void applyToGraph(G graph) throws GraphManipulationException;

}
