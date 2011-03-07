package org.vcell.util.graphlayout.energybased;

import java.util.Set;

import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraphLayouter;
import org.vcell.util.graphlayout.StretchToBoundaryLayouter;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyTerm;
import org.vcell.util.graphlayout.energybased.EnergySum.Minimizer;

public class EnergyMinimizingLayouter implements ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layouter Energy Minimizing";

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
		stretchLayouter.layout(graph);
	}

	public String getLayoutName() {
		return LAYOUT_NAME;
	}


	
}
