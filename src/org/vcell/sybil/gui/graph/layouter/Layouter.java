/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.graph.layouter;

/*   Layouter  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Interface for graph layout methods
 */

import java.util.HashMap;
import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.util.graphlayout.LayoutType;


public interface Layouter {	
	
	public void applyToGraph(Graph graph);
	
	public static Registry registry = new Registry();

	public static class Registry extends HashMap<LayoutType, Layouter> {
		private static final long serialVersionUID = -6057204794488941771L;

		public Registry() {
			put(LayoutType.Randomizer, new Triangulizer());
			put(LayoutType.Circularizer, new Circlelizer());
			put(LayoutType.Annealer, new SortTriangulizer());
			put(LayoutType.Leveller, new DoubleCirclelizer());
			put(LayoutType.Relaxer, new PickTriangulizer());
		}
	}
	
}
