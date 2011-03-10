package org.vcell.util.graphlayout.energybased;

import java.util.Set;

import org.vcell.sybil.util.sets.SetOfTwo;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyFunction;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyTerm;

public class ShootAndCutLayouter extends EnergyMinimizingLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layouter Shoot and Cut";
	public static final double COULOMB_STRENGTH = 50000;
	public static final double EDGE_SPRING_STRENGTH = 1;
	public static final double COULOMB_CUT_OFF = 1e7;
	public static final boolean REPULSION_IS_ACROSS_CONTAINERS = false;

	public ShootAndCutLayouter() {
		super(createTermFactories(), new ShootAndCutMinimizer());
	}
	
	public static Set<EnergyTerm.Factory> createTermFactories() {
		EnergyFunction nodesRepulsion = new CoulombEnergyFunction(COULOMB_STRENGTH);
		EnergyFunction edgesPull = new ParabularEnergyFunction(EDGE_SPRING_STRENGTH);
		EnergyTerm.Factory nodesRepulsionFactory = 
			new AllNodePairsEnergyTermFactory(nodesRepulsion, REPULSION_IS_ACROSS_CONTAINERS);
		EnergyTerm.Factory edgesPullFactory = new AllEdgesEnergyTermFactory(edgesPull);
		return new SetOfTwo<EnergyTerm.Factory>(nodesRepulsionFactory, edgesPullFactory);
	}
	
	public String getLayoutName() {
		return LAYOUT_NAME;
	}


	
}
