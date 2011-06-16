/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.graphlayout.placer;

/*   PlacerDefault  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Find a spot by starting from the nearest and sweeping increasing squares in index space
 *   for an empty spot.
 */

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.tiling.TileSpot;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public class PlacerDefault<S extends Location.Owner<S>> implements Placer<S> {

	public TileSpot<S> place(S shape, Tiling<S> tiling) throws CouldNotFindVacantSpotException {
		TileSpot<S> spot = findSpot(shape, tiling);
		spot.setShape(shape);
		return spot;
	}
	
	public TileSpot<S> findSpot(S shape, Tiling<S> tiling) 
	throws Placer.CouldNotFindVacantSpotException {
		int xShape = shape.location().x();
		int yShape = shape.location().y();
		TileSpot<S> nearbySpot = tiling.nearbySpot(xShape, yShape);
		if(nearbySpot.isVacant()) { return nearbySpot; }
		int iXNear = nearbySpot.iX();
		int iYNear = nearbySpot.iY();
		int deltaMax = Math.max(Math.max(iXNear, tiling.iXMax() - iXNear),
				Math.max(iYNear, tiling.iYMax() - iYNear));
		for(int delta = 1; delta <= deltaMax; delta++) {
			int iX, iY, iXMin, iYMin, iXMax, iYMax;
			iX = iXNear - delta; 
			if(iX >= tiling.iXMin()) {
				iYMin = Math.max(iYNear - delta, tiling.iYMin());
				iYMax = Math.min(iYNear + delta, tiling.iYMax());
				for(iY = iYMin; iY <= iYMax; iY++) {
					TileSpot<S> spot = tiling.spot(iX, iY);
					if(spot.isVacant()) { return spot; }
				}
			}
			iX = iXNear + delta; 
			if(iX <= tiling.iXMax()) {
				iYMin = Math.max(iYNear - delta, tiling.iYMin());
				iYMax = Math.min(iYNear + delta, tiling.iYMax());
				for(iY = iYMin; iY <= iYMax; iY++) {
					TileSpot<S> spot = tiling.spot(iX, iY);
					if(spot.isVacant()) { return spot; }
				}
			}
			iY = iYNear - delta; 
			if(iY >= tiling.iYMin()) {
				iXMin = Math.max(iXNear - delta + 1, tiling.iXMin());
				iXMax = Math.min(iXNear + delta - 1, tiling.iXMax());
				for(iX = iXMin; iX <= iXMax; iX++) {
					TileSpot<S> spot = tiling.spot(iX, iY);
					if(spot.isVacant()) { return spot; }
				}
			}
			iY = iYNear + delta; 
			if(iY <= tiling.iYMax()) {
				iXMin = Math.max(iXNear - delta + 1, tiling.iXMin());
				iXMax = Math.min(iXNear + delta - 1, tiling.iXMax());
				for(iX = iXMin; iX <= iXMax; iX++) {
					TileSpot<S> spot = tiling.spot(iX, iY);
					if(spot.isVacant()) { return spot; }
				}
			}
		}
		throw new Placer.CouldNotFindVacantSpotException(shape, tiling);
	}
	
}
