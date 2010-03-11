package org.vcell.sybil.util.graphlayout.shuffler;

/*   RandomShuffler  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Randomly shuffles graph elements on a tiling
 */

import java.util.Random;

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.shuffler.penalty.SpotPenalizer;
import org.vcell.sybil.util.graphlayout.tiling.TileSpot;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public class RandomShuffler<S extends Location.Owner<S>> implements Shuffler<S> {

	protected Random random = new Random(System.currentTimeMillis());
	
	public void shuffle(Tiling<S> tiling, SpotPenalizer<S> penalty) {
		int iXMin = tiling.iXMin();
		int iYMin = tiling.iYMin();
		int iXMax = tiling.iXMax();
		int iYMax = tiling.iYMax();
		for(int iX1 = iXMin; iX1 <= iXMax; iX1++) {
			for(int iY1 = iYMin; iY1 <= iYMax; iY1++) {
				int iX2 = random.nextInt(1 + iXMax - iXMin);
				int iY2 = random.nextInt(1 + iYMax - iYMin);
				TileSpot<S> spot1 = tiling.spot(iX1, iY1);
				TileSpot<S> spot2 = tiling.spot(iX2, iY2);
				if(spot1.canHaveShape() && spot2.canHaveShape()) {
					S shape1 = spot1.shape();
					S shape2 = spot2.shape();
					spot1.setShape(shape2);
					spot2.setShape(shape1);
				}				
			}
		}
	}

}
