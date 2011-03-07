package org.vcell.util.graphlayout.energybased;

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.util.lists.ListOfTwo;
import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraph.Edge;
import org.vcell.util.graphlayout.ContainedGraph.Node;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyFunction;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyTerm;

public class AllEdgesEnergyTermFactory implements EnergyTerm.Factory {
	
	protected final EnergyFunction function;
	
	public AllEdgesEnergyTermFactory(EnergyFunction functionFactory) {
		this.function = functionFactory;
	}
	
	public Set<EnergyTerm> generateTerms(ContainedGraph graph) {
		HashSet<EnergyTerm> terms = new HashSet<EnergyTerm>();
		for(Edge edge : graph.getEdges()) {
			terms.add(new EnergyTerm.Default(
					new ListOfTwo<Node>(edge.getNode1(), edge.getNode2()), function));
		}
		return terms;
	}

}
