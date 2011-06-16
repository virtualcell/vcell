/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.graphlayout.shuffler;

/*   Shuffler  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Interface for shuffling graph elements on a tiling
 */

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.shuffler.penalty.SpotPenalizer;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public interface Shuffler<S extends Location.Owner<S>> {
	
	public void shuffle(Tiling<S> tiling, SpotPenalizer<S> penalty);

}
