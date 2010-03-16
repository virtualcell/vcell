package org.vcell.sybil.util.graphlayout.shuffler.penalty;

/*   ZeroPenalty  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Interface for a penalty to rearrangements of graph elements on a tiling
 */

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public class ZeroPenalizer<S extends Location.Owner<S>> implements SpotPenalizer<S> {
	public int penalize(S shape, int ix, int iy, Tiling<S> tiling) { return 0; }
}