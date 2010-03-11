package org.vcell.sybil.util.graphlayout.shuffler.penalty;

/*   SpotPenalizer  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Interface for a penalty to rearrangements of graph elements on a tiling
 */

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public interface SpotPenalizer<S extends Location.Owner<S>> {
	public int penalize(S shape, int iX, int iY, Tiling<S> tiling);
}