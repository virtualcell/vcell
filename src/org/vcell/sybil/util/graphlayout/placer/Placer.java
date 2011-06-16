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

/*   Placer  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Places graph elements on a tiling
 */

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.tiling.TileSpot;
import org.vcell.sybil.util.graphlayout.tiling.Tiling;

public interface Placer<S extends Location.Owner<S>> {
	
	public static class CouldNotFindVacantSpotException extends Exception {
	
		private static final long serialVersionUID = -1917070494957196052L;
	
		protected Object shape;
		protected Tiling<?> tiling;
		
		public CouldNotFindVacantSpotException(Object shapeNew, Tiling<?> tilingNew) {
			super("Could not find vacant spot to place shape");
			shape = shapeNew;
			tiling = tilingNew;
		}
		
		public Object shape() { return shape; }
		public Tiling<?> tiling() { return tiling; }
		
	}

	public TileSpot<S> place(S shape, Tiling<S> tiling)
	throws CouldNotFindVacantSpotException;
}
