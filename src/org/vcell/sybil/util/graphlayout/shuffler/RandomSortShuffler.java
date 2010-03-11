package org.vcell.sybil.util.graphlayout.shuffler;

/*   RandomSortShuffler  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Sorts graph elements on a tiling
 */

import java.util.Random;

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.shuffler.penalty.SpotPenalizer;
import org.vcell.sybil.util.graphlayout.tiling.TileSpot;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public class RandomSortShuffler<S extends Location.Owner<S>> implements Shuffler<S> {

	public static int rounds = 10;
	protected Random random = new Random(System.currentTimeMillis());
	protected RepetitionTimer repTimer = new RepetitionTimer(rounds, 10000);
	
	public void shuffle(Tiling<S> tiling, SpotPenalizer<S> penalizer) {
		int iXMin = tiling.iXMin();
		int iYMin = tiling.iYMin();
		int iXMax = tiling.iXMax();
		int iYMax = tiling.iYMax();
		long currentRounds = repTimer.adjustCurrent();
		for(int round = 0; round < currentRounds; round++) {
			for(int iX1 = iXMin; iX1 <= iXMax; iX1++) {
				for(int iY1 = iYMin; iY1 <= iYMax; iY1++) {
					int iX2 = random.nextInt(1 + iXMax - iXMin);
					int iY2 = random.nextInt(1 + iYMax - iYMin);
					TileSpot<S> spot1 = tiling.spot(iX1, iY1);
					TileSpot<S> spot2 = tiling.spot(iX2, iY2);
					if(spot1.canHaveShape() && spot2.canHaveShape()) {
						S shape1 = spot1.shape();
						S shape2 = spot2.shape();
						int penalty11 = penalizer.penalize(shape1, iX1, iY1, tiling);
						int penalty12 = penalizer.penalize(shape1, iX2, iY2, tiling);
						int penalty21 = penalizer.penalize(shape2, iX1, iY1, tiling);
						int penalty22 = penalizer.penalize(shape2, iX2, iY2, tiling);
						if(penalty12 + penalty21 < penalty11 + penalty22) {
							spot1.setShape(shape2);
							spot2.setShape(shape1);						
						}
					}				
				}
			}
		}
		repTimer.setTimer();
	}

	
}
