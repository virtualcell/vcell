package org.vcell.sybil.util.graphlayout.shuffler;

/*   RandomPickShuffler  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Sorts graph elements on a tiling
 */

import java.util.Random;

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.shuffler.penalty.SpotPenalizer;
import org.vcell.sybil.util.graphlayout.tiling.TileSpot;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public class RandomPickShuffler<S extends Location.Owner<S>> implements Shuffler<S> {

	public static int rounds = 10;
	public static int maxAttemptsToFindShape = 100;
	protected Random random = new Random(System.currentTimeMillis());
	protected RepetitionTimer repTimer = new RepetitionTimer(rounds, 10000);
	
	public void shuffle(Tiling<S> tiling, SpotPenalizer<S> penalizer) {
		int iXMin = tiling.iXMin();
		int iYMin = tiling.iYMin();
		int iXMax = tiling.iXMax();
		int iYMax = tiling.iYMax();
		int iAttempt = 0;
		long currentRounds = repTimer.adjustCurrent();
		for(int round = 0; round < currentRounds; round++) {
			int iX1 = random.nextInt(1 + iXMax - iXMin);
			int iY1 = random.nextInt(1 + iYMax - iYMin);						
			TileSpot<S> spot1 = tiling.spot(iX1, iY1);
			if(spot1.canHaveShape()) { 
				S shape1 = spot1.shape(); 
				int penalty1 = penalizer.penalize(shape1, iX1, iY1, tiling);
				TileSpot<S> spotBest = spot1;
				int penaltyBest = penalty1;
				for(int iX2 = iXMin; iX2 <= iXMax; iX2++) {
					for(int iY2 = iYMin; iY2 <= iYMax; iY2++) {
						TileSpot<S> spot2 = tiling.spot(iX2, iY2);
						if(spot2.canHaveShape()) {
							int penalty2 = penalizer.penalize(shape1, iX2, iY2, tiling);
							if(penalty2 < penaltyBest) {
								penaltyBest = penalty2;
								spotBest = spot2;
							}
						}				
					}
				}
				if(penaltyBest < penalty1 && spotBest != spot1) {
					S shapeBest = spotBest.shape();
					spotBest.setShape(shape1);
					spot1.setShape(shapeBest);
				}
			} else {
				round = round > 0 ? round - 1 : 0;
				iAttempt++;
				if(iAttempt >= 10*repTimer.current()) { break; }
			}
		}
		repTimer.setTimer();
	}


	
}
