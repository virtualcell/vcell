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

/*   SquareTwoDegreePenalty  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   This penalty is sum of distances squared of two-degree dependency relatives
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.tiling.TileSpot;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public class SquareTwoDegreePenalizer<S extends Location.Owner<S>> implements SpotPenalizer<S> {

	public void addRelatives(Set<S> relatives, S shape) {
		relatives.add(shape);
		relatives.addAll(shape.dependencies());
		relatives.addAll(shape.dependents());
	}
	
	public int penalize(S shape, int iX, int iY, Tiling<S> tiling) {
		if(shape != null) {
			Set<S> relatives = new HashSet<S>();
			for(S relative : shape.dependencies()) { addRelatives(relatives, relative); }
			for(S relative : shape.dependencies()) { addRelatives(relatives, relative); }
			addRelatives(relatives, shape);
			int penalty = 0;
			TileSpot<S> spot = tiling.spot(iX, iY);
			int xSpot = spot.x();
			int ySpot = spot.y();
			for(S relative : relatives) {
				Location<S> relLoc = relative.location();
				int dx = relLoc.x() - xSpot;
				int dy = relLoc.y() - ySpot;
				penalty += dx*dx + dy*dy;
			}
			return penalty;
		}
		return 0;
	}

}
