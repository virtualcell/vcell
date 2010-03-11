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
