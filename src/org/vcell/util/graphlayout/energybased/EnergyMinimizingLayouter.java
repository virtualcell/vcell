/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout.energybased;

import java.util.Set;

import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraphLayouter;
import org.vcell.util.graphlayout.StretchToBoundaryLayouter;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyTerm;
import org.vcell.util.graphlayout.energybased.EnergySum.Minimizer;

public class EnergyMinimizingLayouter extends ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layouter Energy Minimizing";

	protected boolean useStretchLayouter = true;
	protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();

	protected final Set<EnergyTerm.Factory> termFactories;
	protected final Minimizer minimizer;

	public EnergyMinimizingLayouter(Set<EnergyTerm.Factory> termFactories, Minimizer minimizer) {
		this.termFactories = termFactories;
		this.minimizer = minimizer;
	}

	public void layout(ContainedGraph graph) {
		EnergySum energySum = new EnergySum.Default(graph);
		for(EnergyTerm.Factory termFactory : termFactories) {
			energySum.generateTerms(termFactory);
		}
		minimizer.minimize(energySum);
		if(useStretchLayouter) { stretchLayouter.layout(graph);	}
	}

	public String getLayoutName() {
		return LAYOUT_NAME;
	}

}
