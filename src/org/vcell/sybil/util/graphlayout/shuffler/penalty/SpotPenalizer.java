/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.graphlayout.shuffler.penalty;

/*   SpotPenalizer  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Interface for a penalty to rearrangements of graph elements on a tiling
 */

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public interface SpotPenalizer<S extends Location.Owner<S>> {
	public int penalize(S shape, int iX, int iY, Tiling<S> tiling);
}
